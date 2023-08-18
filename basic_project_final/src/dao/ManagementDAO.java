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

	//관리자 로그인
	public Map<String, Object> adminlist(){
		return jdbc.selectOne(" select ad_id, ad_pw from admin");
	}
	
	public List<Map<String, Object>> route() {
		return jdbc.selectList(" select * from route");
	}
	
	public List<Map<String, Object>> buslist() {
		return jdbc.selectList(" SELECT BUS_ID, BUS_NUM, BUS_GRADE FROM BUS ORDER BY BUS_ID ");
	}
	
	public List<Map<String, Object>> servicelist() {
		return jdbc.selectList(" SELECT SV_ID, SV_DATE, RT_ID, BUS_ID FROM SERVICE ");
	}
	
	//운행버스 등록
	public int insert(List<Object> param) {
		String sql = " INSERT INTO SERVICE(RT_ID, SV_DATE, BUS_ID, SV_ID) ";
		sql = sql + " VALUES(?, ?, ?, ?) ";
		return jdbc.update(sql, param);
		// public int update(String sql, List<Object> param) { 실행
	}
	
	public int ServiceDelete(List<Object> param) {
		String sql=" DELETE FROM SERVICE WHERE SV_ID= ? ";
		return jdbc.update(sql, param);
		
	}
	
	//운행 ID 생성
	public Map<String, Object> getsvId(List<Object> param) {
		return jdbc.selectOne(" SELECT FN_CREATE_SERVICE_NUMBER( ? ) ID from dual", param);
	}
	
	//버스ID 생성
	public Map<String, Object> getBusId() {
		return jdbc.selectOne(" SELECT FN_CREATE_BUS_NUMBER ID from dual ");
	}
	
	//버스 추가
	public int busAdd(List<Object> param) {
		return jdbc.update( " INSERT INTO BUS ( BUS_ID, BUS_NUM, BUS_GRADE ) VALUES ( ? , ? , ? ) ", param);
	}
	
	//버스 삭제
	public int busDelete(List<Object> param) {
		return jdbc.update(" DELETE bus WHERE bus_id = ? ",param);
	}
	
	//터미널 ID 생성
	public Map<String, Object> getStationId() {
		// TODO Auto-generated method stub
		return jdbc.selectOne(" SELECT FN_CREATE_STATION_NUMBER  ID from dual ");
	}
	
	public List<Map<String, Object>> Stationlist() {
		return jdbc.selectList(" SELECT * FROM station ");
	}
	
	//터미널 추가
	public int StationAdd(List<Object> param) {
		return jdbc.update( " INSERT INTO STATION ( ST_ID, ST_NAME, ST_LOC ) VALUES ( ? , ? , ? ) ", param);
	}
	
	//터미널 삭제
	public int stationDelete(List<Object> param) {
		return jdbc.update(" DELETE STATION WHERE st_id = ? ", param);
	}
	
	//노선 추가
	public int routeAdd(List<Object> param) {
		return jdbc.update(" INSERT INTO route (rt_id,st_id_start, st_id_end, rt_name, rt_price, rt_pnum, rt_time) \r\n" + 
				" VALUES ( ?, ?, ?, ?, ?, ?, ? ) ", param);
	}
	
	//노선 삭제
	public int routeDelete(List<Object> param) {
		return jdbc.update(" DELETE route WHERE rt_id = ? ", param);
	}

	public int stationUpdate(List<Object> param) {
		return jdbc.update(" UPDATE station SET st_name = ? , st_loc = ? where st_id = ? ", param);
	}

	public int busUpdate(List<Object> param) {
		return jdbc.update(" UPDATE bus SET BUS_NUM = ? , BUS_GRADE = ? where BUS_ID = ? ", param);
	}

}


