package dao;

import util.JDBCUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagementDAO {
	// 싱글톤 패턴
	private static ManagementDAO instance = null;

	private ManagementDAO() {
	}

	public static ManagementDAO getInstance() {
		if (instance == null)
			instance = new ManagementDAO();
		return instance;
	}

	JDBCUtil jdbc = JDBCUtil.getInstance();

	
	public List<Map<String, Object>> adminlist(){
		return jdbc.selectList("select ad_id, ad_pw from admin");
	}

	public List<Map<String, Object>> route() {
		return jdbc.selectList("select rt_id, rt_name, rt_price, rt_pnum, rt_time from route order by rt_id");
	}

	public List<Map<String, Object>> buslist() {
		return jdbc.selectList("SELECT BUS_ID, BUS_NUM, BUS_GRADE FROM BUS ORDER BY BUS_ID");
	}
	public List<Map<String, Object>> servicelist() {
		return jdbc.selectList("SELECT SV_ID, SV_DATE, RT_ID, BUS_ID FROM SERVICE");
	}
	public List<Map<String, Object>> stationlist() {
		return jdbc.selectList("SELECT ST_ID, ST_NAME, ST_LOC FROM STATION");
	}
	
	//운행버스 등록
	public int insert(List<Object> param) {
		String sql = "INSERT INTO SERVICE(RT_ID, SV_DATE, BUS_ID, SV_ID) ";
		sql = sql + " VALUES(?, ?, ?, ?) ";
		return jdbc.update(sql, param);
		// public int update(String sql, List<Object> param) { 실행
	}
	
	//운행버스 삭제
	public int delete(List<Object> param) {
		String sql="DELETE FROM SERVICE WHERE SV_ID= ? ";
		return jdbc.update(sql, param);
		
	}

	public Map<String, Object> getsvId(List<Object> param) {
		
		return jdbc.selectOne(" SELECT FN_CREATE_SERVICE_NUMBER( ? ) ID from dual", param);
	} 
	
	//정류장 등록
	public int stInsert(List<Object> param) {
		String sql = "INSERT INTO STATION(SV_ID, SV_NAME, ST_LOC) ";
		sql = sql + " VALUES(?, ?, ?) ";
		return jdbc.update(sql, param);
	}
	
	//정류장 삭제
	public int stDelete(List<Object> param) {
		String sql="DELETE FROM STATION WHERE ST_ID= ? ";
		return jdbc.update(sql, param);
	
	
}
}


