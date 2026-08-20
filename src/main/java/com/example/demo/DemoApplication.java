package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
/**
 * [스프링 부트 메인 실행 클래스]
 * - exclude 옵션 이유: 초기 화면 및 API 통신 검증 단계에서 DB(DataSource) 미설정으로 인한 실행 에러 방지
 * - TODO: 추후 MySQL/H2 데이터베이스 연결 정보(application.properties) 설정 완료 후 exclude 옵션 제거 예정
 */
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
