package edu.human.com.util;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.human.com.member.service.EmployerInfoVO;
import edu.human.com.member.service.MemberService;

@Controller
public class CommonUtil {
	@Inject
	private MemberService memberService;
	
	@RequestMapping(value="/idcheck.do", method=RequestMethod.GET)
	@ResponseBody
	public String idcheck(@RequestParam("emplyr_id") String emplyr_id) throws Exception{
		String result = "0";//기본값이 중복값 존재x
		EmployerInfoVO memberVO = memberService.viewMember(emplyr_id);
		if(memberVO != null) {
			result = "1";//중복 아이디가 존재할 때
		}
		return result;//return "1"일 때, @responsebody를 붙이면 1.jsp페이지로 이동x, text값으로 반환만 함.
	}
}
