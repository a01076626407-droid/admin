import os
import pymysql
from dotenv import load_dotenv

# ============================================================
# 1. .env 경로 설정 및 로드
# ============================================================
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
dotenv_path = os.path.join(BASE_DIR, "config", ".env")
load_dotenv(dotenv_path=dotenv_path, override=True)

DB_PASSWORD = os.getenv("DB_PASSWORD")

if not DB_PASSWORD:
    print("❌ DB_PASSWORD를 읽지 못했습니다.")
    exit()

db_config = {
    "host": "localhost",
    "user": "root",
    "password": DB_PASSWORD,
    "database": "disaster_db",
    "charset": "utf8mb4"
}

def check_database():
    connection = None
    try:
        connection = pymysql.connect(**db_config)
        with connection.cursor() as cursor:
            print("\n========================================")
            print("🔍 데이터베이스 대피소 적재 현황 검증")
            print("========================================")

            # 1. 수해 대피소 개수 확인
            cursor.execute("SELECT COUNT(*) FROM flood_shelter")
            flood_count = cursor.fetchone()[0]
            print(f"🌊 [수해대피소] flood_shelter: 총 {flood_count}건")

            # 2. 지진 대피소 개수 확인
            cursor.execute("SELECT COUNT(*) FROM earthquake_shelter")
            earthquake_count = cursor.fetchone()[0]
            print(f" زمین [지진대피소] earthquake_shelter: 총 {earthquake_count}건")

            # 3. 공습 대피소 개수 확인
            cursor.execute("SELECT COUNT(*) FROM air_shelter_info")
            air_count = cursor.fetchone()[0]
            print(f"🚨 [공습대피소] air_shelter_info: 총 {air_count}건")

            print("========================================")
            print("🎉 모든 대피소 데이터 검증 완료!")
            print("========================================")

    except Exception as e:
        print("\n❌ 검증 중 오류 발생!")
        print(e)
    finally:
        if connection:
            connection.close()

if __name__ == "__main__":
    check_database()