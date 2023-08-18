package controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.ManagementService;
import service.MemberService;
import service.RefundService;
import service.SalesService;
import service.TicketingService;
import util.ScanUtil;
import util.View;

public class Controller {
	
	static public Map<String, Object> sessionStorage = new HashMap<>();

	ManagementService managementService = ManagementService.getInstance();
	TicketingService ticketingService = TicketingService.getInstance();
	RefundService refundService = RefundService.getInstance();
	MemberService memberService = MemberService.getInstance();
	SalesService salesService = SalesService.getInstance();

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
			case View.MEMBER_LOGIN:
				view = memberService.login();
				break;
			case View.MEMBER_SIGNUP:
				view = memberService.signUp();
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
			case View.TICKET_LIST:
				view = ticketingService.ticketList();
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
			case View.MANAGE_BUS:
				view = managementService.manageBus();
				break;
			case View.MANAGE_SERVICE_INSERT:
				view = managementService.submitsv();
				break;
			case View.MANAGE_SERVICE_DELETE:
				view = managementService.deletesv();
				break;
			case View.MANAGE_SERVICE_LIST:
				view = managementService.serviceList();
				break;
			case View.MANAGE_STATION:
				view = managementService.manageStation();
				break;
			case View.MANAGE_ROUTE:
				view = managementService.manageRoute();
				break;
			case View.MANAGE_SALES:
				view = salesService.salesList();
				break;
			case View.LOGOUT:
				view = logout();
				break;
			default:
				view = View.HOME;
				break;
			}
		}
	}

	private int home() {
//		System.out.println(sessionStorage.get("login"));
//		System.out.println(sessionStorage.get("loginInfo");
		
		System.out.println("==== 버스터미널 시스템 ============================================================");
		System.out.println("   1.로그인 2.회원가입 3.예매/환불 서비스  4.예매목록");
		System.out.println("   9.로그아웃				 0.관리자 ");
		System.out.println("==============================================================================");
		System.out.print("번호 입력 >> ");
		switch (ScanUtil.nextInt()) {
		case 1:
			return View.MEMBER_LOGIN;
		case 2:
			return View.MEMBER_SIGNUP;
		case 3:
			return View.TICKET;
		case 4:
			return View.TICKET_LIST;
		case 9:
			return View.LOGOUT;
		case 0:
			return View.MANAGE_MENU;
		default:
			return View.HOME;
		}
	}
	
	public int logout() {
		System.out.println("로그아웃 합니다...");
		sessionStorage.put("login", false);
		sessionStorage.put("loginInfo", null);
		return View.HOME;
	}

}
