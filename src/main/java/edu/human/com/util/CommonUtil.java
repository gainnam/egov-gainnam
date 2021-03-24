package edu.human.com.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.impl.SimpleLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import edu.human.com.member.service.EmployerInfoVO;
import edu.human.com.member.service.MemberService;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.let.uat.uia.service.EgovLoginService;
import egovframework.rte.fdl.security.userdetails.util.EgovUserDetailsHelper;
import egovframework.rte.fdl.string.EgovObjectUtil;

@Controller
public class CommonUtil {
	@Inject
	private MemberService memberService;
	@Autowired
	private EgovLoginService loginService;
	@Autowired
	private EgovMessageSource egovMessageSource;
	
	private Logger logger = Logger.getLogger(SimpleLog.class);
	
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
	public String actionLogin(@ModelAttribute("loginVO") LoginVO loginVO,HttpServletResponse response, HttpServletRequest request, ModelMap model) throws Exception {
		
		// 1. 일반 로그인 처리
		LoginVO resultVO = loginService.actionLogin(loginVO);

		boolean loginPolicyYn = true;

		if (resultVO != null && resultVO.getId() != null && !resultVO.getId().equals("") && loginPolicyYn) {
			//로그인 성공 시
			request.getSession().setAttribute("LoginVO", resultVO);
			//로그인 성공 후 관리자그룹일 때 관리자 세션 ROLE_ADMIN명 추가
			//스프링 시큐리티 사용x 개발자 임의로 생성
			/*
			LoginVO sessionLoginVO = (LoginVO) RequestContextHolder.getRequestAttributes().getAttribute("LoginVO", RequestAttributes.SCOPE_SESSION);
			EmployerInfoVO memberVO = memberService.viewMember(sessionLoginVO.getId());
			if("GROUP_00000000000000".equals(memberVO.getGROUP_ID())) {
				request.getSession().setAttribute("ROLE_ADMIN", memberVO.getGROUP_ID());
			}
			*/
			
			//스프링 시큐리티 권한체크 추가
			UsernamePasswordAuthenticationFilter springSecurity = null; 
			ApplicationContext act = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
			Map<String, UsernamePasswordAuthenticationFilter> beans = act.getBeansOfType(UsernamePasswordAuthenticationFilter.class);
			if (beans.size() > 0) {
				springSecurity = (UsernamePasswordAuthenticationFilter) beans.values().toArray()[0];
				springSecurity.setUsernameParameter("egov_security_username");
				springSecurity.setPasswordParameter("egov_security_password");
				springSecurity.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(
						request.getServletContext().getContextPath() + "/egov_security_login", "POST"));
			} else {
				//throw new IllegalStateException("No AuthenticationProcessingFilter");
				return "forward:/tiles/home.do";//context-security.xml에서 bean설정이 없을 경우
			}
			springSecurity.setContinueChainBeforeSuccessfulAuthentication(false); 
			//false이면 chain 처리 되지 않음.. (filter가 아닌 경우 false로...)
			springSecurity.doFilter(new RequestWrapperForSecurity(request, resultVO.getId(), resultVO.getPassword()), response, null);

			//System.out.println("context-security.xml파일의 jdbcAuthoritiesByUsernameQuery 확인");
	    	//List<String> authorities = EgovUserDetailsHelper.getAuthorities();
			
			List<String> authorities = getAuthorities("EgovUserDetailsHelper");
	    	// 1. authorites 에  권한이 있는지 체크 TRUE/FALSE
	    	logger.debug("디버그" + authorities.contains("ROLE_ADMIN"));
	    	logger.debug("디버그" + authorities.contains("ROLE_USER"));
	    	logger.debug("디버그" + authorities.contains("ROLE_ANONYMOUS"));
	    	//위 값을 이용해서 세션을 발생시킵니다.
	    	if(authorities.contains("ROLE_ADMIN")) {
	    		request.getSession().setAttribute("ROLE_ADMIN", true);
	    	}
			return "forward:/tiles/home.do";//new홈
		} else {
			//로그인 실패 시
			model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
			return "login.tiles";//new login form
		}

	}
	
	private List<String> getAuthorities(String string) {
		List<String> listAuth = new ArrayList<String>();
		if (EgovObjectUtil.isNull((LoginVO) RequestContextHolder.getRequestAttributes().getAttribute("LoginVO", RequestAttributes.SCOPE_SESSION))) {
			return null;
		}
		//스프링 시큐리티 연동 추가(아래)
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String listAuthTemp1 = authentication.getAuthorities().toString();
		logger.debug("디버그: 현재 사용자의 로그인 권한리스트 출력 " + listAuthTemp1);
		//String을 리스트형으로 변환해서 반환값에 넣습니다.
		listAuthTemp1 = listAuthTemp1.replace("[", "");
		listAuthTemp1 = listAuthTemp1.replace("]", "");
		listAuthTemp1 = listAuthTemp1.replace(" ", "");
		String[] listAuthTemp2 = listAuthTemp1.split(",");
		listAuth = Arrays.asList(listAuthTemp2);
		//listAuth = listAuthTemp1;
		return listAuth;
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
class RequestWrapperForSecurity extends HttpServletRequestWrapper {
	private String username = null;
	private String password = null;
	
	public RequestWrapperForSecurity(HttpServletRequest request, String username, String password) {
		super(request);
		this.username = username;
		this.password = password;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getRequestURI() {
		return ((HttpServletRequest) super.getRequest()).getContextPath() + "/egov_security_login";

	}

	@Override
	public String getServletPath() {
		return ((HttpServletRequest) super.getRequest()).getContextPath() + "/egov_security_login";

	}

	@Override
	public String getParameter(String name) {
		if (name.equals("egov_security_username")) {
			return username;
		}
		if (name.equals("egov_security_password")) {
			return password;
		}

		return super.getParameter(name);
	}
	
	
	
	
}
