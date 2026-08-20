import pymysql
import requests
import math

API_KEY = "444e4569696568643536476365704c"
BASE_URL = f"http://openapi.seoul.go.kr:8088/{API_KEY}/json/TbEqkShelter"

db_config = {
    'host': 'localhost', 'user': 'root', 'password': 'root',
    'database': 'shelter_db', 'charset': 'utf8mb4'
}

# 1. 전체 데이터 개수 먼저 확인
response = requests.get(f"{BASE_URL}/1/1/").json()
total_count = response['TbEqkShelter']['list_total_count']
print(f"총 데이터 개수: {total_count}개")

# 2. 1000개씩 나누어 반복 호출
page_size = 1000
total_pages = math.ceil(total_count / page_size)

connection = pymysql.connect(**db_config)
try:
    with connection.cursor() as cursor:
        for i in range(total_pages):
            start = (i * page_size) + 1
            end = (i + 1) * page_size
            url = f"{BASE_URL}/{start}/{end}/"
            
            rows = requests.get(url).json()['TbEqkShelter']['row']
            
            sql = """INSERT INTO shelter_info 
                     (ctpv_nm, sgg_nm, fclt_nm, daddr, lot, lat, mng_dept_nm) 
                     VALUES (%s, %s, %s, %s, %s, %s, %s)"""
            
            for row in rows:
                val = (row['CTPV_NM'], row['SGG_NM'], row['FCLT_NM'], 
                       row['DADDR'], row['LOT'], row['LAT'], row['MNG_DEPT_NM'])
                cursor.execute(sql, val)
            print(f"{start} ~ {end} 데이터 저장 완료!")
    connection.commit()
finally:
    connection.close()