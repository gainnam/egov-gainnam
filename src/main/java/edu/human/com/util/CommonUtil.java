package edu.human.com.util;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.human.com.member.service.EmployerInfoVO;
import edu.human.com.member.service.MemberService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.let.uat.uia.service.EgovLoginService;

@Controller
public class CommonUtil {
	@Inject
	private MemberService memberService;
	@Autowired
	private EgovLoginService loginService;
	@Autowired
	private EgovMessageSource egovMessageSource;
	/** 기존 로그인 처리는 egov것 그대로 사용하고,
	 * 로그인 후 이동 페이지는 OLD에서 NEW로 변경합니다
	 * 일반 로그인을 처리한다
	 * @param vo - 아이디, 비밀번호가 담긴 LoginVO
	 * @param request - 세션처리를 위한 HttpServletRequest
	 * @return result - 로그인결과(세션정보)
	 * @exception Exception
	 */
	@RequestMapping(value = "/login_action.do")
	public String actionLogin(@ModelAttribute("loginVO") LoginVO loginVO, HttpServletRequest request, ModelMap model) throws Exception {

		// 1. 일반 로그인 처리
		LoginVO resultVO = loginService.actionLogin(loginVO);

		boolean loginPolicyYn = true;

		if (resultVO != null && resultVO.getId() != null && !resultVO.getId().equals("") && loginPolicyYn) {
			//로그인 성공 시
			request.getSession().setAttribute("LoginVO", resultVO);
			return "forward:/tiles/home.do";//new홈
		} else {
			//로그인 실패 시
			model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
			return "login.tiles";//new login form
		}

	}
	
	/**
     * XSS 방지 처리. 자바스크립트코드를 실행하지 못하는 특수문자로 replace 하는 내용
     * 접근권한 protected -> public (protected는 현재패키지에만 사용 가능)
     * @param data
     * @return
     */
    public String unscript(String data) {
        if (data == null || data.trim().equals("")) {
            return "";
        }

        String ret = data;

        ret = ret.replaceAll("<(S|s)(C|c)(R|r)(I|i)(P|p)(T|t)", "&lt;script");
        ret = ret.replaceAll("</(S|s)(C|c)(R|r)(I|i)(P|p)(T|t)", "&lt;/script");

        ret = ret.replaceAll("<(O|o)(B|b)(J|j)(E|e)(C|c)(T|t)", "&lt;object");
        ret = ret.replaceAll("</(O|o)(B|b)(J|j)(E|e)(C|c)(T|t)", "&lt;/object");

        ret = ret.replaceAll("<(A|a)(P|p)(P|p)(L|l)(E|e)(T|t)", "&lt;applet");
        ret = ret.replaceAll("</(A|a)(P|p)(P|p)(L|l)(E|e)(T|t)", "&lt;/applet");

        ret = ret.replaceAll("<(E|e)(M|m)(B|b)(E|e)(D|d)", "&lt;embed");
        ret = ret.replaceAll("</(E|e)(M|m)(B|b)(E|e)(D|d)", "&lt;embed");

        ret = ret.replaceAll("<(F|f)(O|o)(R|r)(M|m)", "&lt;form");
        ret = ret.replaceAll("</(F|f)(O|o)(R|r)(M|m)", "&lt;form");

        return ret;
    }
	
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
