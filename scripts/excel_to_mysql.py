import os
import pandas as pd
from sqlalchemy import create_engine

# 1. 엑셀 파일 폴더 경로
FOLDER_PATH = "scripts"

# 2. MySQL 접속 URL (disaster_db로 수정 완료)
DB_URL = "mysql+pymysql://root:root@localhost:3306/disaster_db"

def run_etl():
    print("=" * 60)
    print("🚀 [Step 1] 엑셀 데이터 파일 로드 시작")
    print("=" * 60)

    target_file = None
    for f in os.listdir(FOLDER_PATH):
        if f.startswith("flood_shelter") and (f.endswith(".xlsx") or f.endswith(".xls")):
            target_file = os.path.join(FOLDER_PATH, f)
            break

    if not target_file:
        print("❌ flood_shelter 엑셀 파일을 찾을 수 없습니다.")
        return

    raw_df = pd.read_excel(target_file, engine='openpyxl')
    print(f"📄 원본 엑셀 데이터 {len(raw_df):,}건 로드 완료")

    print("\n🧹 [Step 2] MySQL 컬럼 규격에 맞춰 데이터 정제 중...")
    clean_df = pd.DataFrame()

    clean_df['ctpv_nm'] = raw_df['ctpv_nm'].astype(str)
    clean_df['sgg_nm'] = raw_df['sgg_nm'].astype(str)
    clean_df['fclt_nm'] = raw_df['fclt_nm'].astype(str)
    clean_df['daddr'] = raw_df['daddr'].astype(str)

    clean_df['lot'] = pd.to_numeric(raw_df['lot'], errors='coerce')
    clean_df['lat'] = pd.to_numeric(raw_df['lat'], errors='coerce')
    clean_df['mng_dept_nm'] = raw_df['mng_dept_nm'].fillna('미지정')

    clean_df = clean_df.dropna(subset=['lat', 'lot', 'fclt_nm'])

    print(f"✨ 정제 완료: 유효 데이터 {len(clean_df):,}건 준비됨")

    print("\n💾 [Step 3] MySQL flood_shelter 테이블에 데이터 밀어 넣기...")
    engine = create_engine(DB_URL)

    clean_df.to_sql(name='flood_shelter', con=engine, if_exists='append', index=False)

    print("=" * 60)
    print(f"🎉 대성공! 총 {len(clean_df):,}건이 'flood_shelter' 테이블에 완벽하게 저장되었습니다!")
    print("=" * 60)

if __name__ == "__main__":
    run_etl()