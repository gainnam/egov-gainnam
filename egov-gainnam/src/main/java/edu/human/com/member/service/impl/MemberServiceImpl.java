package edu.human.com.member.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import edu.human.com.member.service.EmployerInfoVO;
import edu.human.com.member.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService {

	@Inject
	private MemberDAO memberDAO;
	
	@Override
	public List<EmployerInfoVO> selectMember() throws Exception {
		// DAO클래스에서 메서드 호출
		return memberDAO.selectMember();
	}

	@Override
	public EmployerInfoVO viewMember(String emplyr_id) throws Exception {
		// DAO클래스에서 메서드 호출
		return memberDAO.viewMember(emplyr_id);
	}

	@Override
	public int deleteMember(String emplyr_id) throws Exception {
		// DAO클래스에서 메서드 호출
		return memberDAO.deleteMember(emplyr_id);
	}

	@Override
	public void insertMember(EmployerInfoVO employerInfoVO) throws Exception {
		// DAO클래스에서 메서드 호출
		memberDAO.insertMember(employerInfoVO);
	}

	@Override
	public void updateMember(EmployerInfoVO employerInfoVO) throws Exception {
		// DAO클래스에서 메서드 호출
		memberDAO.updateMember(employerInfoVO);
	}

}