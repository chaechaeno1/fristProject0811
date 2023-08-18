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
	
	
	
	
	
	public Map<String, Object> login(List<Object> param) {
		return jdbc.selectOne(" SELECT * FROM CUSTOMER "
				+ " WHERE CST_ID= ? AND CST_PW= ? ", param);
	}
	
	public int signUp(List<Object> param) {
		return jdbc.update(" INSERT INTO CUSTOMER (CST_ID, CST_PW, CST_NAME, CST_TEL, CST_AGE ) VALUES ( ?, ?, ?, ?, ? )", param);
	}
	public List<Map<String, Object>> checkedID(List<Object> param) {
		return jdbc.selectList(" select * FROM CUSTOMER WHERE CST_ID= ? ", param);
	}
}





