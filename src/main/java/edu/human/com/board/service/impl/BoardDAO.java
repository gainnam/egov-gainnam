package edu.human.com.board.service.impl;

import org.springframework.stereotype.Repository;

import edu.human.com.common.EgovComAbstractMapper;

@Repository
public class BoardDAO extends EgovComAbstractMapper{
	public Integer delete_board(Integer nttId) throws Exception{
		//egov 매퍼 추상클래스 사용.sqlSession templete에 직접접근하지 않고 사용
		return delete("boardMapper.delete_board", nttId);
	}

	public Integer delete_attach(String atchFileId) {
		// 마이바티스 매퍼쿼리 호출 (첨부파일 테이블)(아래)
		return delete("boardMapper.delete_attach", atchFileId);
	}

	public int delete_attach_detail(String atchFileId) {
		// 마이바티스 매퍼쿼리 호출(첨부파일 상세 테이블)
		return delete("boardMapper.delete_attach_detail", atchFileId);
	}

}
