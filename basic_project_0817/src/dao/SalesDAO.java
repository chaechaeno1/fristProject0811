package dao;

import java.util.List;
import java.util.Map;

import service.SalesService;
import util.JDBCUtil;

public class SalesDAO {
	private static SalesDAO instance = null;

	private SalesDAO() {
	}

	public static SalesDAO getInstance() {
		if (instance == null)
			instance = new SalesDAO();
		return instance;
	}

	JDBCUtil jdbc = JDBCUtil.getInstance();

	public List<Map<String, Object>> SalesList(List<Object> param) {
		return jdbc.selectList(" SELECT route.rt_name rt, sum(reservation.res_price) sal, count(*) cnt, service.rt_id, reservation.res_date\r\n" + 
				"    FROM ticket, service, route, reservation\r\n" + 
				"    WHERE ticket.res_id = reservation.res_id and ticket.sv_id = service.sv_id and service.rt_id = route.rt_id \r\n" + 
				"        and TO_CHAR(reservation.res_date, ? ) = ? \r\n" + 
				"    GROUP BY route.rt_name, reservation.res_date, service.rt_id ", param);
	}
	
	

}
