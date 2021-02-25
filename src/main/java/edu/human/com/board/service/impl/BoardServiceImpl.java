package edu.human.com.board.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import edu.human.com.board.service.BoardService;

@Service
public class BoardServiceImpl implements BoardService{

	@Inject
	private BoardDAO boardDAO;//같은 패키지 안에 있으면, import안해도됨.
	@Override
	public Integer delete_board(Integer nttId) throws Exception {
		// DAO호출
		return boardDAO.delete_board(nttId);
		
	}
	@Override
	public Integer delete_attach(String atchFileId) throws Exception {
		// DAO호출 2개 /bbs(할아버지) ->attach(아들) -> attachdetail(손자)
		int result = 0;
		if(boardDAO.delete_attach_detail(atchFileId) > 0) {
			result = boardDAO.delete_attach(atchFileId);
		}
		return result;
	}

	
}
