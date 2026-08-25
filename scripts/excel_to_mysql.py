import os
import urllib.parse
import pandas as pd
from sqlalchemy import create_engine
import sqlalchemy  # SQLAlchemy 타입 지정을 위해 필수 import
import pymysql

# ============================================================
# [DB 접속 정보 설정]
# ============================================================
DB_USER = "root"
DB_PASSWORD = "root"
DB_HOST = "localhost"
DB_PORT = "3306"
DB_NAME = "shelter_db"

encoded_pw = urllib.parse.quote_plus(DB_PASSWORD)
DB_URL = f"mysql+pymysql://{DB_USER}:{encoded_pw}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

FOLDER_PATH = os.path.dirname(os.path.abspath(__file__))

def create_super_admin():
    """서버 구동 시 super 관리자 계정이 없으면 자동 생성하는 함수"""
    print("=" * 65)
    print("[Step 0] 최고 관리자(super) 계정 확인 및 생성 중...")
    print("=" * 65)
    try:
        connection = pymysql.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,  # db 파라미터 경고 수정 반영
            charset='utf8mb4'
        )
        with connection.cursor() as cursor:
            cursor.execute("SELECT COUNT(*) FROM user WHERE username = 'super'")
            result = cursor.fetchone()

            if result[0] == 0:
                sql = "INSERT INTO user (username, password, email, realname) VALUES (%s, %s, %s, %s)"
                cursor.execute(sql, ('super', 'super', 'super@admin.com', '최고관리자'))
                connection.commit()
                print(">>> [자동 생성] 최고 관리자(super) 계정이 데이터베이스에 등록되었습니다. (PW: super)")
            else:
                print(">>> [확인] 최고 관리자(super) 계정이 이미 존재합니다.")
        connection.close()
    except Exception as e:
        print(f"[ERROR] 관리자 계정 생성 에러: {e}")

def run_etl():
    create_super_admin()

    print("\n" + "=" * 65)
    print("[Step 1] 홍수 엑셀 데이터 파일 탐색 및 로드 시작")
    print("=" * 65)

    target_file = None
    for f in os.listdir(FOLDER_PATH):
        if f.startswith("flood_shelter") and (f.endswith(".xlsx") or f.endswith(".xls")):
            target_file = os.path.join(FOLDER_PATH, f)
            break

    if not target_file:
        print("[ERROR] flood_shelter 엑셀 파일을 찾을 수 없습니다.")
        return

    print(f"[TARGET] 로드 대상 파일: {os.path.basename(target_file)}")
    raw_df = pd.read_excel(target_file, engine='openpyxl')
    print(f"[INFO] 원본 엑셀 데이터 {len(raw_df):,}건 로드 완료")

    print("\n[Step 2] MySQL 규격에 맞춰 데이터 정제 중...")
    clean_df = pd.DataFrame()

    if 'shlt_id' in raw_df.columns:
        clean_df['shlt_id'] = raw_df['shlt_id'].astype(str)
    elif 'id' in raw_df.columns:
        clean_df['shlt_id'] = raw_df['id'].astype(str)
    else:
        clean_df['shlt_id'] = [f"FL_{i}" for i in range(1, len(raw_df) + 1)]

    clean_df['ctpv_nm'] = raw_df['ctpv_nm'].astype(str)
    clean_df['sgg_nm'] = raw_df['sgg_nm'].astype(str)
    clean_df['fclt_nm'] = raw_df['fclt_nm'].astype(str)
    clean_df['daddr'] = raw_df['daddr'].astype(str)
    clean_df['lot'] = pd.to_numeric(raw_df['lot'], errors='coerce')
    clean_df['lat'] = pd.to_numeric(raw_df['lat'], errors='coerce')

    clean_df['mng_dept_nm'] = '-'
    clean_df['se'] = 3

    clean_df = clean_df.dropna(subset=['lat', 'lot', 'fclt_nm'])

    print(f"[OK] 정제 완료: 유효 데이터 {len(clean_df):,}건")

    print("\n[Step 3] MySQL flood 테이블에 데이터 적재 중...")
    try:
        engine = create_engine(DB_URL)

        column_order = ['shlt_id', 'ctpv_nm', 'sgg_nm', 'fclt_nm', 'daddr', 'lot', 'lat', 'mng_dept_nm', 'se']
        clean_df = clean_df[column_order]

        dtype_mapping = {
            'shlt_id': sqlalchemy.types.VARCHAR(255),
            'ctpv_nm': sqlalchemy.types.VARCHAR(255),
            'sgg_nm': sqlalchemy.types.VARCHAR(255),
            'fclt_nm': sqlalchemy.types.VARCHAR(255),
            'daddr': sqlalchemy.types.VARCHAR(500),
            'mng_dept_nm': sqlalchemy.types.VARCHAR(255),
            'lot': sqlalchemy.types.DOUBLE,
            'lat': sqlalchemy.types.DOUBLE,
            'se': sqlalchemy.types.Integer
        }

        clean_df.to_sql(name='flood', con=engine, if_exists='replace', index=False, dtype=dtype_mapping)
        print(f"[SUCCESS] 'shlt_id'가 포함된 총 {len(clean_df):,}건의 데이터가 'flood' 테이블에 저장되었습니다!")
    except Exception as e:
        print(f"[ERROR] DB 저장 에러: {e}")

if __name__ == "__main__":
    run_etl()