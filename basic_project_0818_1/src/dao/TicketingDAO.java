package dao;

import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class TicketingDAO {
	private static TicketingDAO instance = null;
	List<Object> param = null;

	JDBCUtil jdbc = JDBCUtil.getInstance();
	
	private TicketingDAO() {}
	
	public static TicketingDAO getInstance() {
		if (instance == null)
			instance = new TicketingDAO();
		return instance;
	}
	
	public List<Map<String, Object>> stationList() {
		return jdbc.selectList(" SELECT * FROM station ");
	}
	
	public List<Map<String, Object>> routeList(List<Object> param) {
		return jdbc.selectList(" SELECT service.sv_id , route.rt_id , route.rt_name, service.sv_date, \r\n" + 
				"        service.bus_id , bus.bus_num, bus.bus_grade, route.st_id_end, route.rt_price\r\n" + 
				"    FROM service, route, bus\r\n" + 
				"    WHERE service.rt_id = route.rt_id AND  service.bus_id = bus.bus_id\r\n" + 
				"    and route.st_id_start = ? and route.st_id_end = ? ", param);
	}

	public List<Map<String, Object>> seatList(List<Object> param) {
		return jdbc.selectList(" SELECT tk_snum	FROM ticket WHERE sv_id = ? ", param);
	}
	
	public int getAge(List<Object> param) {
		Map<String, Object> age = jdbc.selectOne(" SELECT cst_age FROM customer WHERE cst_id = ? ", param);
		return (int) Integer.parseInt(String.valueOf(age.get("CST_AGE")));
	}
	
	public int bill(List<Object> param) {
		return jdbc.update(" insert into reservation (RES_ID, cst_id, res_price, res_date) VALUES ( ?, ?, ?, ? ) ", param);
	}
	
	public int buyTicket(List<Object> param) {
		return jdbc.update(" insert into ticket (TK_ID, bus_id, res_id, sv_id, tk_snum, AGE_DIV) VALUES ( ?, ?, ?, ?, ?, ? ) ", param);
	}
	
	public Map<String, Object> getResId(List<Object> param) {
		return jdbc.selectOne(" SELECT FN_CREATE_RES_NUMBER( ? ) ID FROM dual ", param);
	}
	
	public Map<String, Object> getTkId(List<Object> param){
		return jdbc.selectOne(" SELECT FN_CREATE_TICKET_NUMBER( ? ) ID from dual ", param);
	}

	public List<Map<String, Object>> ticketList(List<Object> param) {
		return jdbc.selectList(" SELECT reservation.res_id, ticket.tk_id, reservation.res_date, ticket.tk_snum, route.rt_name, \r\n" + 
				"    service.sv_date, bus.bus_num, bus.bus_grade, ticket.age_div\r\n" + 
				"    FROM ticket, reservation, route, bus, service\r\n" + 
				"    WHERE ticket.res_id = reservation.res_id and ticket.bus_id = bus.bus_id and service.rt_id = route.rt_id\r\n" + 
				"    and ticket.sv_id = service.sv_id\r\n" + 
				"    and reservation.cst_id = ? ", param);
	}
}
