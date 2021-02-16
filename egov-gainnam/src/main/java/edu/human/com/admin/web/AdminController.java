package edu.human.com.admin.web;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.human.com.member.service.EmployerInfoVO;
import edu.human.com.member.service.MemberService;

@Controller
public class AdminController {

	@Inject
	private MemberService memberService;
	
	@RequestMapping(value="/admin/member/list_member.do")
	public String list_member(Model model) throws Exception {
		//회원관리 페이지 이동
		List<EmployerInfoVO> listMember = memberService.selectMember();
		model.addAttribute("listMember", listMember);
		return "admin/member/list_member";
	}
	
	
	
	@RequestMapping(value="/admin/home.do", method=RequestMethod.GET)
	public String home() throws Exception {
		//관리자메인 페이지로 이동
		return "admin/home";
	}
}