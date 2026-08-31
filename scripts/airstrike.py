import os
import math
import re
import pymysql
import requests
from dotenv import load_dotenv

# ============================================================
# 1. .env 경로 설정 및 로드 (프로젝트 루트 config/.env 탐색)
# ============================================================
BASE_DIR = os.path.dirname(
    os.path.dirname(
        os.path.abspath(__file__)
    )
)

dotenv_path = os.path.join(BASE_DIR, "config", ".env")
load_dotenv(dotenv_path=dotenv_path, override=True)

# ============================================================
# 2. 환경변수 읽기 (공백 제거 포함)
# ============================================================
API_KEY = (os.getenv("API_KEY") or "").strip()
DB_PASSWORD = os.getenv("DB_PASSWORD")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "3306")
DB_NAME = os.getenv("DB_NAME", "shelter_db")
DB_USER = os.getenv("DB_USER", "root")

if not API_KEY:
    print("[ERROR] API_KEY를 읽지 못했습니다.")
    exit(0)

if not DB_PASSWORD:
    print("[ERROR] DB_PASSWORD를 읽지 못했습니다.")
    exit(0)

print(f"[OK] 공습대피소 API 인증키 확인 완료 (앞 4자리: {API_KEY[:4]}***)")
print(f"[OK] DB 대상 호스트: {DB_HOST}:{DB_PORT}")

# ============================================================
# 3. 서울 열린데이터 광장 API URL
# ============================================================
SERVICE_NAME = "LOCALDATA_114602"
BASE_URL = f"http://openapi.seoul.go.kr:8088/{API_KEY}/json/{SERVICE_NAME}"

# ============================================================
# 4. MySQL 접속 정보
# ============================================================
db_config = {
    "host": DB_HOST,
    "port": int(DB_PORT),
    "user": DB_USER,
    "password": DB_PASSWORD,
    "database": DB_NAME,
    "charset": "utf8mb4",
}

# ============================================================
# 5. 데이터 수집 및 적재 함수
# ============================================================
def fetch_and_save_data():
    connection = None
    try:
        print("\n[1] 공습대피소 전체 데이터 개수 확인 중...")
        test_url = f"{BASE_URL}/1/1/"
        response = requests.get(test_url, timeout=30)

        print(f"서버 응답 상태 코드: {response.status_code}")

        # JSON 파싱 사전 검증
        try:
            data = response.json()
        except Exception:
            print("[ERROR] 서울시 API 서버 응답이 JSON 형식이 아닙니다.")
            print(f">>> 실제 응답 내용: {response.text.strip()}")
            return

        # 서울시 API 자체 에러 응답 처리 (인증키 오류 등)
        if "RESULT" in data and data["RESULT"].get("CODE") != "INFO-000":
            err_code = data["RESULT"].get("CODE")
            err_msg = data["RESULT"].get("MESSAGE")
            print(f"[ERROR] 서울시 API 호출 실패: [{err_code}] {err_msg}")
            return

        if SERVICE_NAME not in data:
            print(f"[ERROR] 응답 데이터에 '{SERVICE_NAME}' 키가 없습니다: {data}")
            return

        api_data = data[SERVICE_NAME]
        total_count = api_data.get("list_total_count", 0)
        print(f"공습대피소 총 데이터 개수: {total_count}개")

        if total_count == 0:
            print("[INFO] 수집할 데이터가 없습니다.")
            return

        # 페이징 계산 (1000개 단위)
        page_size = 1000
        total_pages = math.ceil(total_count / page_size)
        print(f"총 {total_pages}번 요청을 시작합니다.")

        # MySQL 연결
        print("\n[2] MySQL 연결 중...")
        connection = pymysql.connect(**db_config)
        print("[OK] MySQL 연결 성공")

        with connection.cursor() as cursor:
            # 1. se 컬럼 존재 여부 확인 및 자동 생성 (스키마 자동 보정)
            cursor.execute("SHOW COLUMNS FROM airstrike LIKE 'se'")
            if not cursor.fetchone():
                print("[INFO] airstrike 테이블에 'se' 컬럼이 없어 자동으로 추가합니다...")
                cursor.execute("ALTER TABLE airstrike ADD COLUMN se VARCHAR(50) DEFAULT '2'")
                print("[OK] 'se' 컬럼 자동 추가 완료")

            # 2. 기존 데이터 삭제
            print("\n[3] 기존 공습대피소 데이터 초기화 (TRUNCATE)...")
            cursor.execute("TRUNCATE TABLE airstrike")
            print("[OK] 기존 데이터 초기화 완료")

            sql = """
                  INSERT INTO airstrike
                      (shlt_id, ctpv_nm, sgg_nm, fclt_nm, daddr, lot, lat, mng_dept_nm, se)
                  VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s) \
                  """

            for i in range(total_pages):
                start = (i * page_size) + 1
                end = min((i + 1) * page_size, total_count)
                print(f"[{i + 1}/{total_pages}] {start} ~ {end} 수집 중...")

                req_url = f"{BASE_URL}/{start}/{end}/"
                res = requests.get(req_url, timeout=30)
                page_data = res.json()
                rows = page_data.get(SERVICE_NAME, {}).get("row", [])

                for idx, row in enumerate(rows):
                    try:
                        longitude = float(row.get("XCRD") or 0)
                    except (ValueError, TypeError):
                        longitude = 0.0

                    try:
                        latitude = float(row.get("YCRD") or 0)
                    except (ValueError, TypeError):
                        latitude = 0.0

                    ctpv_nm = "서울특별시"
                    address = row.get("LOTNO_ADDR") or ""
                    sgg_nm = "-"
                    match = re.search(r'([가-힣]+구)', address)
                    if match:
                        sgg_nm = match.group(1)

                    se_value = "2"
                    shlt_id = row.get("RSTR_SN")
                    if not shlt_id:
                        shlt_id = f"AIR_{i}_{idx}"

                    values = (
                        shlt_id,
                        ctpv_nm,
                        sgg_nm,
                        row.get("BPLC_NM"),
                        address,
                        longitude,
                        latitude,
                        row.get("MNG_DEPT_NM") or "-",
                        se_value
                    )
                    cursor.execute(sql, values)

                print(f"[OK] {start} ~ {end} ({len(rows)}건) 저장 완료")

        connection.commit()
        print("\n========================================")
        print(f"[SUCCESS] 공습대피소 총 {total_count}건 데이터 저장 완료!")
        print("========================================")

    except Exception as e:
        print(f"\n[ERROR] 실행 중 오류 발생: {e}")
        if connection:
            connection.rollback()
            print("[ROLLBACK] 롤백 처리 완료")
    finally:
        if connection:
            connection.close()
            print("MySQL 연결 종료")

if __name__ == "__main__":
    fetch_and_save_data()