import os
import math
import pymysql
import requests
from dotenv import load_dotenv

# ============================================================
# 1. .env 경로 설정 및 로드
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
EARTHQUAKE_API_KEY = (os.getenv("EARTHQUAKE_API_KEY") or "").strip()
DB_PASSWORD = os.getenv("DB_PASSWORD")
DB_HOST = os.getenv("DB_HOST", "localhost")

if not EARTHQUAKE_API_KEY:
    print("[ERROR] EARTHQUAKE_API_KEY를 읽지 못했습니다.")
    exit(0)

if not DB_PASSWORD:
    print("[ERROR] DB_PASSWORD를 읽지 못했습니다.")
    exit(0)

print(f"[OK] 지진대피소 API 인증키 확인 완료 (앞 4자리: {EARTHQUAKE_API_KEY[:4]}***)")
print(f"[OK] DB 대상 호스트: {DB_HOST}")

# ============================================================
# 3. 서울 지진대피소 API URL
# ============================================================
SERVICE_NAME = "TbEqkShelter"
BASE_URL = f"http://openapi.seoul.go.kr:8088/{EARTHQUAKE_API_KEY}/json/{SERVICE_NAME}"

# ============================================================
# 4. MySQL 접속 정보
# ============================================================
db_config = {
    "host": DB_HOST,
    "user": "root",
    "password": DB_PASSWORD,
    "database": "shelter_db",
    "charset": "utf8mb4",
}

# ============================================================
# 5. 데이터 수집 및 적재 함수
# ============================================================
def fetch_and_save_data():
    connection = None
    try:
        print("\n[1] 지진대피소 전체 데이터 개수 확인 중...")
        test_url = f"{BASE_URL}/1/1/"
        response = requests.get(test_url, timeout=30)

        print(f"서버 응답 상태 코드: {response.status_code}")

        # JSON 파싱 사전 검증
        try:
            data = response.json()
        except Exception:
            print(f"[ERROR] 서울시 지진 API 서버 응답이 JSON 형식이 아닙니다.")
            print(f">>> 실제 응답 내용: {response.text.strip()}")
            return

        # 서울시 API 자체 에러 응답 처리
        if "RESULT" in data and data["RESULT"].get("CODE") != "INFO-000":
            err_code = data["RESULT"].get("CODE")
            err_msg = data["RESULT"].get("MESSAGE")
            print(f"[ERROR] 서울시 지진 API 호출 실패: [{err_code}] {err_msg}")
            return

        if SERVICE_NAME not in data:
            print(f"[ERROR] 응답 데이터에 '{SERVICE_NAME}' 키가 없습니다: {data}")
            return

        api_data = data[SERVICE_NAME]
        total_count = api_data.get("list_total_count", 0)
        print(f"지진대피소 총 데이터 개수: {total_count}개")

        if total_count == 0:
            print("[INFO] 수집할 지진 대피소 데이터가 없습니다.")
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
            print("\n[3] 기존 지진대피소 데이터 초기화 (TRUNCATE)...")
            cursor.execute("TRUNCATE TABLE earthquake")
            print("[OK] 기존 데이터 초기화 완료")

            sql = """
                  INSERT INTO earthquake
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
                        lot = float(row.get("LOT") or 0)
                    except (ValueError, TypeError):
                        lot = 0.0

                    try:
                        lat = float(row.get("LAT") or 0)
                    except (ValueError, TypeError):
                        lat = 0.0

                    shlt_id = row.get("SHLT_id")
                    if not shlt_id:
                        shlt_id = f"EQK_{i}_{idx}"

                    values = (
                        shlt_id,
                        row.get("CTPV_NM") or "서울특별시",
                        row.get("SGG_NM") or "-",
                        row.get("FCLT_NM"),
                        row.get("DADDR") or "",
                        lot,
                        lat,
                        row.get("MNG_DEPT_NM") or "-",
                        row.get("SE") or "3"
                    )
                    cursor.execute(sql, values)

                print(f"[OK] {start} ~ {end} ({len(rows)}건) 저장 완료")

        connection.commit()
        print("\n========================================")
        print(f"[SUCCESS] 지진대피소 총 {total_count}건 데이터 저장 완료!")
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