package edu.human.com.util;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import edu.human.com.member.service.EmployerInfoVO;
import edu.human.com.member.service.MemberService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.let.uat.uia.service.EgovLoginService;
import egovframework.rte.fdl.string.EgovObjectUtil;

@Controller
public class CommonUtil {
	@Inject
	private MemberService memberService;
	@Autowired
	private EgovLoginService loginService;
	@Autowired
	private EgovMessageSource egovMessageSource;
	
	//권한, 인증 체크를 1개의 메서드로 처리(아래)
	public Boolean getAuthorities() throws Exception {
		Boolean authority = Boolean.FALSE;
		//인증체크(로그인상태인지 아닌지 판단)
		if (EgovObjectUtil.isNull((LoginVO) RequestContextHolder.getRequestAttributes().getAttribute("LoginVO", RequestAttributes.SCOPE_SESSION))) {
			return authority;
		}
		//권한체크(관리자인지 일반사용자인지 판단)
		LoginVO sessionLoginVO = (LoginVO) RequestContextHolder.getRequestAttributes().getAttribute("LoginVO", RequestAttributes.SCOPE_SESSION);
		EmployerInfoVO memberVO = memberService.viewMember(sessionLoginVO.getId());
		if("GROUP_00000000000000".equals(memberVO.getGROUP_ID())) {
			authority = Boolean.TRUE;
		}
		//여기까지 true값을 가져오면, 관리자라는 의미
		return authority;
	}
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
			//로그인 성공 후 관리자그룹일 때 관리자 세션 ROLE_ADMIN명 추가
			LoginVO sessionLoginVO = (LoginVO) RequestContextHolder.getRequestAttributes().getAttribute("LoginVO", RequestAttributes.SCOPE_SESSION);
			EmployerInfoVO memberVO = memberService.viewMember(sessionLoginVO.getId());
			if("GROUP_00000000000000".equals(memberVO.getGROUP_ID())) {
				request.getSession().setAttribute("ROLE_ADMIN", memberVO.getGROUP_ID());
			}
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
