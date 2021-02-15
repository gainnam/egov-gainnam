package edu.human.com.member.service;

import java.util.List;

public interface MemberService {
	public List<EmployerInfoVO> selectMember() throws Exception;
	public EmployerInfoVO viewMember(String emplyr_id) throws Exception;
	public void deleteMember(String emplyr_id) throws Exception;
	public void insertMember(EmployerInfoVO employerInfoVO) throws Exception;
	public void updateMember(EmployerInfoVO employerInfoVO) throws Exception;
}