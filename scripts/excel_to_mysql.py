# scripts/excel_to_mysql.py

import os
import pandas as pd
from sqlalchemy import create_engine

FOLDER_PATH = "scripts"

# [수정 포인트] 맨 뒤의 disaster_db를 실제 존재하는 shelter_project로 변경합니다.
# 본인의 실제 MySQL root 비밀번호를 입력해주세요. (예: 1234)
DB_URL = "mysql+pymysql://root:root@localhost:3306/shelter_project"

def run_etl():
    print("=" * 60)
    print("🚀 [Step 1] 엑셀 데이터 파일 탐색 및 로드")
    print("=" * 60)

    # scripts 폴더 안에서 엑셀 파일 자동 검색
    target_file = None
    for f in os.listdir(FOLDER_PATH):
        if f.startswith("flood_shelter") and (f.endswith(".xlsx") or f.endswith(".xls")):
            target_file = os.path.join(FOLDER_PATH, f)
            break

    if not target_file:
        print("❌ flood_shelter 엑셀 파일을 찾을 수 없습니다.")
        return

    # 엑셀 파일 읽기
    raw_df = pd.read_excel(target_file, engine='openpyxl')
    print(f"📄 원본 데이터 {len(raw_df):,}건 로드 완료")

    print("\n🧹 [Step 2] 데이터 정제 및 컬럼 매핑 중...")
    clean_df = pd.DataFrame()

    # 엑셀 원본 컬럼 -> DB 컬럼 매핑
    clean_df['name'] = raw_df['fclt_nm'].astype(str)
    clean_df['address'] = (
        raw_df['ctpv_nm'].fillna('') + ' ' +
        raw_df['sgg_nm'].fillna('') + ' ' +
        raw_df['daddr'].fillna('')
    ).str.strip()
    clean_df['latitude'] = pd.to_numeric(raw_df['lat'], errors='coerce')
    clean_df['longitude'] = pd.to_numeric(raw_df['lot'], errors='coerce')
    clean_df['capacity'] = 0
    clean_df['facility_type'] = raw_df['mng_dept_nm'].fillna('수해임시주거시설')

    # 위도, 경도, 시설명 결측치 제거
    clean_df = clean_df.dropna(subset=['latitude', 'longitude', 'name'])

    print(f"✨ 최종 정제 완료: 유효 데이터 {len(clean_df):,}건 확보")

    print("\n💾 [Step 3] MySQL shelter_project에 테이블 적재 중...")
    engine = create_engine(DB_URL)

    # shelter_project DB 안에 flood_shelter 테이블 생성 및 적재
    clean_df.to_sql(name='flood_shelter', con=engine, if_exists='replace', index=False)

    print("=" * 60)
    print(f"🎉 성공! 총 {len(clean_df):,}건이 'shelter_project.flood_shelter' 테이블에 저장되었습니다!")
    print("=" * 60)

if __name__ == "__main__":
    run_etl()