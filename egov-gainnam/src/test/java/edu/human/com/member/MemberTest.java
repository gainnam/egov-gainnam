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
