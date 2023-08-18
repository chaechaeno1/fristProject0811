package dao;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class RefundDAO {
	// 1. 싱글톤 만들기
	private static RefundDAO instance = null;
	private RefundDAO() {}
	public static RefundDAO getInstance() {
		if(instance == null) 
			instance = new RefundDAO();
		return instance;
	}
	
	JDBCUtil jdbc = JDBCUtil.getInstance();
	
	public List<Map<String, Object>> ticketList(List<Object> param) {
		return jdbc.selectList(" SELECT reservation.res_id, ticket.tk_id, reservation.res_date, ticket.tk_snum, route.rt_name, \r\n" + 
				"    service.sv_date, bus.bus_num, bus.bus_grade, ticket.age_div, route.rt_price\r\n" + 
				"    FROM ticket, reservation, route, bus, service\r\n" + 
				"    WHERE ticket.res_id = reservation.res_id and ticket.bus_id = bus.bus_id and service.rt_id = route.rt_id\r\n" + 
				"    and ticket.sv_id = service.sv_id\r\n" + 
				"    and reservation.cst_id = ? ", param);
	}
	
	public int refund(List<Object> param) {
		return jdbc.update(" DELETE ticket\r\n" + 
				"    WHERE tk_id = ? ", param);
	}
	
	public int resDel(List<Object> param) {
		return jdbc.update("", param);
	}
	public int updateReservation(List<Object> param) {
		
		return jdbc.update(" UPDATE reservation set res_price = res_price - ? \r\n" + 
				"WHERE res_id = ? ", param);
	}
}
