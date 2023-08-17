package dao;

import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class MemberDAO {
	private static MemberDAO instance = null;
	private MemberDAO() {}
	public static MemberDAO getInstance() {
		if(instance == null) instance = new MemberDAO();
		return instance;
	}
	
	JDBCUtil jdbc = JDBCUtil.getInstance();
	
	
	
	
	
	public Map<String, Object> login(String id, String pass) {
		return jdbc.selectOne("SELECT * FROM MEMBER "
				+ " WHERE MEM_ID='"+id+"' AND MEM_PASS='"+pass+"' ");
	}
	
	public int signUp(List<Object> param) {
		return jdbc.update("INSERT INTO TMEMBER (MEM_ID, MEM_PASS, MEM_NAME) VALUES (?, ?, ?)", param);
	}
}







