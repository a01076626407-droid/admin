package com.example.demo.domain; // 반드시 파일 위치와 같은 패키지여야 합니다!

import jakarta.persistence.*; // @Entity, @Id 등의 기능을 불러옵니다.
import lombok.Getter;
import lombok.Setter;

@Entity // "이 클래스는 DB 테이블과 연결된 클래스야!"라고 알려줍니다.
@Table(name = "earthquake_shelter") // "실제 DB의 테이블 이름은 'earthquake_shelter'야!"라고 명시합니다.
@Getter // 필드 값을 꺼내는 Getter를 자동으로 만들어 줍니다.
@Setter // 필드에 값을 넣는 Setter를 자동으로 만들어 줍니다. (이게 없어서 에러가 났어요!)
public class EarthquakeShelter {

    @Id // "이 필드가 테이블의 기본 키(PK)야!"
    @GeneratedValue(strategy = GenerationType.IDENTITY) // "번호는 DB가 알아서 1씩 늘려줘!"
    private Long id; // 자바에서의 식별용 이름

    // @Column(name = "...")을 써서 DB의 영어 칼럼명과 자바 변수명을 연결합니다.
    @Column(name = "shlt_id")
    private String shltId; // 피난처 ID

    @Column(name = "ctpv_nm")
    private String ctpvNm; // 시도명

    @Column(name = "sgg_nm")
    private String sggNm; // 시군구명

    @Column(name = "fclt_nm")
    private String fcltNm; // 시설명

    @Column(name = "daddr")
    private String daddr; // 상세주소

    @Column(name = "fcar")
    private Double fcar; // 시설면적

    @Column(name = "lot")
    private Double lot; // 경도

    @Column(name = "lat")
    private Double lat; // 위도

    @Column(name = "se")
    private String se; // 구분

    @Column(name = "se_nm")
    private String seNm; // 구분명

    @Column(name = "mng_dept_nm")
    private String mngDeptNm; // 관리부서
}