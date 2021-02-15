package edu.human.com.member;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.human.com.member.service.EmployerInfoVO;
import edu.human.com.member.service.MemberService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={
		"file:src/main/webapp/WEB-INF/config/egovframework/springmvc/egov-com-servlet.xml",
		"file:src/main/resources/egovframework/spring/com/*.xml"}
)
@WebAppConfiguration

public class MemberTest {
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private MemberService memberService;
	
	
	@Test
	public void insertMember() throws Exception {
		EmployerInfoVO memberVO = new EmployerInfoVO();//고전방식 객체생성
		//memberVO에 set으로 값을 입력한 이후 DB에 인서트함.
		memberVO.setEMPLYR_ID("user01");
		memberVO.setORGNZT_ID("ORGNZT_0000000000000");//외래키이기 때문에
		memberVO.setUSER_NM("사용자01");
		//암호화 작업(아래)
		memberVO.setPASSWORD("");
		memberVO.setSEXDSTN("F");
		memberVO.setHOUSE_ADRES("집주소");
		memberVO.setGROUP_ID("GROUP_00000000000000");//외래키이기 때문에
		memberVO.setEMPLYR_STTUS_CODE("P");
		memberVO.setESNTL_ID("USRCNFRM_00000000000");//고유ID이기 때문에
		
		memberService.insertMember(memberVO);
	}
	
	@Test
	public void viewMember() throws Exception {
		EmployerInfoVO memberVO = memberService.viewMember("admin");
		System.out.println("admin회원의 상세 정보는 " + memberVO.toString());
	}
	
	@Test
	public void selectMember() throws Exception {
		List<EmployerInfoVO> memberList = memberService.selectMember();
		for(EmployerInfoVO member:memberList) {
			System.out.println("현재 등록된 회원은 :" + member.toString());
		}
	}
	
	@Test
	public void dbConnect_test() throws SQLException {
		Connection connect = dataSource.getConnection();
		System.out.println("데이터베이스 커넥션 결과: " + connect);
	}
	
	@Test
	public void junit_text() {
		System.out.println("JUnit실행확인");
	}
}
