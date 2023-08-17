package controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.LoginService;
import service.ManagementService;
import service.MemberService;
import service.RefundService;
import service.TicketingService;
import util.ScanUtil;
import util.View;

public class Controller {
	
	static public Map<String, Object> sessionStorage = new HashMap<>();
	static public Map<String, Object> ticketStorage = new HashMap<>();

	ManagementService managementService = ManagementService.getInstance();
	TicketingService ticketingService = TicketingService.getInstance();
	RefundService refundService = RefundService.getInstance();

	public static void main(String[] args) {
		
		new Controller().start();
	}

	private void start() {
		sessionStorage.put("login", false);
		sessionStorage.put("loginInfo", null);

		int view = View.HOME;
		while (true) {
			switch (view) {
			case View.HOME:
				view = home();
				break;

			case View.TICKET:
				view = ticketingService.list();
				break;
			case View.TICKET_SEARCHING_STATION:
				view = ticketingService.searchingStation();
				break;
			case View.TICKET_SEARCHING_ROUTE:
				view = ticketingService.searchingRoute();
				break;
			case View.TICKET_SEARCHING_BUS_SEAT:
				view = ticketingService.searchingSeat();
				break;
			case View.TICKET_BUY:
				view = ticketingService.buy();
				break;
			case View.REFUND:
				view = refundService.refund();
				break;
			case View.REFUND_TICKET:
				view = refundService.refundTicket();
				break;
			case View.MANAGE_LOGIN:
				view = managementService.adlogin();
				break;
			case View.MANAGE_MENU:
				view = managementService.selectMenu();
				break;
			case View.MANAGE_SERVICE:
				view = managementService.serviceMenu();
				break;
			case View.MANAGE_SERVICE_INSERT:
				view = managementService.submitsv();
				break;
			case View.MANAGE_SERVICE_DELETE:
				view = managementService.deletesv();
				break;
			case View.MANAGE_STATION:
				view = managementService.stationMenu();
				break;
			case View.MANAGE_STATION_INSERT:
				view = managementService.submitst();
				break;
			case View.MANAGE_STATION_DELETE:
				view = managementService.deletest();
				break;
			case View.CASH_RESET:
				view = resetTicketStorage();
			default:
				view = View.HOME;
			}
		}
	}

	private int test() {
		return 0;
	}

	private int home() {
//		System.out.println(sessionStorage.get("login"));
//		System.out.println(sessionStorage.get("loginInfo"));

		System.out.println("==== 버스터미널  개발원 ====");
		System.out.println("   1.로그인 2.회원가입 3.예매/환불 5.관리자");
		System.out.println("=========================");
		System.out.print("번호 입력 >> ");
		switch (ScanUtil.nextInt()) {
		case 1:
			return View.MEMBER_LOGIN;
		case 2:
			return View.MEMBER_SIGNUP;
		case 3:
			return View.TICKET;
		case 5:
			return View.MANAGE_MENU;
		case 0:
			return View.TEST_PAGE;
		default:
			return View.HOME;
		}
	}

	public int resetTicketStorage() {
		ticketStorage.put("station", null);
		ticketStorage.put("service", null);
		ticketStorage.put("seat", null);
		ticketStorage.put("grade", null);
		ticketStorage.put("price", null);
		ticketStorage.put("ticket", null);
		
		return View.HOME;
	}

}
