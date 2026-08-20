import pymysql
import requests
import math

API_KEY = "636365455665686435356647754373"
BASE_URL = f"http://openapi.seoul.go.kr:8088/{API_KEY}/json/LOCALDATA_114602"

# 본인의 MySQL 비밀번호로 수정하세요
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': 'root', 
    'database': 'airstrike',
    'charset': 'utf8mb4'
}

# 1. 전체 데이터 개수 확인
response = requests.get(f"{BASE_URL}/1/1/").json()
total_count = response['LOCALDATA_114602']['list_total_count']
print(f"공습대피소 총 데이터 개수: {total_count}개")

# 2. 1000개씩 나누어 반복 호출하기 위한 페이지 수 계산
page_size = 1000
total_pages = math.ceil(total_count / page_size)

# 3. 데이터베이스 연결 및 데이터 적재 시작
connection = pymysql.connect(**db_config)
try:
    with connection.cursor() as cursor:
        for i in range(total_pages):
            start = (i * page_size) + 1
            end = min((i + 1) * page_size, total_count)
            url = f"{BASE_URL}/{start}/{end}/"
            
            rows = requests.get(url).json()['LOCALDATA_114602']['row']
            
            # 기존 테이블 구조(lot, lat)에 맞춘 SQL
            sql = """INSERT INTO air_shelter_info 
                     (ctpv_nm, sgg_nm, fclt_nm, daddr, lot, lat, mng_dept_nm) 
                     VALUES (%s, %s, %s, %s, %s, %s, %s)"""
            
            for row in rows:
                # 관리부서 데이터 예외 처리
                mng_dept = row.get('MNG_DEPT_NM')
                if not mng_dept:
                    mng_dept = '-'

                # X좌표 -> lot(경도) 칼럼 매칭 (비어있으면 0.0)
                lot_val = row.get('XCRD')
                if not lot_val or lot_val == '':
                    lot_val = 0.0

                # Y좌표 -> lat(위도) 칼럼 매칭 (비어있으면 0.0)
                lat_val = row.get('YCRD')
                if not lat_val or lat_val == '':
                    lat_val = 0.0

                val = (
                    row.get('CTPV_NM'), 
                    row.get('SGG_NM'), 
                    row.get('BPLC_NM'),      # 사업장명 -> 시설명
                    row.get('LOTNO_ADDR'),   # 지번주소 -> 상세주소
                    float(lot_val),          # X좌표를 숫자로 변환해 lot에 저장
                    float(lat_val),          # Y좌표를 숫자로 변환해 lat에 저장
                    mng_dept
                )
                cursor.execute(sql, val)
                
            print(f"공습대피소 {start} ~ {end} 데이터 저장 완료!")
            
    connection.commit()
    print("모든 공습대피소 데이터가 airstrike DB에 성공적으로 저장되었습니다!")
finally:
    connection.close()