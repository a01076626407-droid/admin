import pandas as pd
# 2. 파이썬과 MySQL을 연결해주는 SQLAlchemy 엔진 생성 함수 로드
from sqlalchemy import create_engine

# 3. MySQL 접속 주소 (본인의 MySQL 비밀번호와 DB 이름에 맞게 설정)
DB_URL = "mysql+pymysql://root:root@localhost:3306/shelter_project"

def verify_database():
    try:
        # 4. DB 연결 통로(엔진) 생성
        engine = create_engine(DB_URL)
        
        # 5. DB의 shelter_info 테이블에 저장된 전체 행 개수를 세는 SQL 실행
        count_query = "SELECT COUNT(*) AS total_count FROM flood_shelter"
        count_df = pd.read_sql(count_query, con=engine)
        total_count = count_df['total_count'].iloc[0]
        
        print("=" * 60)
        print(f"🎉 [DB 검증 완료] shelter_info 테이블에 총 {total_count:,}건의 데이터가 저장되어 있습니다!")
        print("-" * 60)
        
        # 6. 상위 3건의 샘플 데이터를 가져와서 실제 내용이 깨지지 않았는지 확인 (실제 칼럼명 반영)
        sample_query = "SELECT shlt_id, ctpv_nm, sgg_nm, fclt_nm, daddr, lot, lat, mng_dept_nm FROM flood_shelter LIMIT 3"
        sample_df = pd.read_sql(sample_query, con=engine)
        print("📋 [저장된 데이터 샘플 3건]:")
        print(sample_df)
        print("=" * 60)

    except Exception as e:
        # 7. DB 접속 실패 또는 테이블 없을 시 에러 출력
        print(f"❌ DB 접속 및 조회 실패: {e}")

# 스크립트 직접 실행 시 verify_database 함수 작동
if __name__ == "__main__":
    verify_database()