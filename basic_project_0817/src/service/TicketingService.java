package service;


import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;
import dao.TicketingDAO;
import oracle.net.ns.SessionAtts;
import util.ScanUtil;
import util.SeatView;
import util.TicketPriceCalc;
import util.View;
import util.TicketPriceCalc;

public class TicketingService {

	private static TicketingService instance = null;
	private Map<String, Object> ticketStorage = new HashMap<>();
	List<Object> param = null;
	boolean[] seat;
	SeatView sv = new SeatView();

	private TicketingService() {
	}

	public static TicketingService getInstance() {
		if (instance == null)
			instance = new TicketingService();
		return instance;
	}

	TicketingDAO dao = TicketingDAO.getInstance();

	public int list() {
		if ( !((boolean) Controller.sessionStorage.get("login")) ) {
			System.out.println("로그인 해주세요 ");
			return View.MEMBER_LOGIN;
		}
		
		ticketStorage = new HashMap<>();
		
		System.out.println("==============================================================================");
		System.out.println("메뉴를 선택해주세요.");
		System.out.println("1. 티켓구매 | 2. 티켓환불 | 0.홈");
		System.out.println("==============================================================================");
		System.out.print("메뉴 번호 입력 >> ");
		switch (ScanUtil.nextInt()) {
		case 1:
			return View.TICKET_SEARCHING_STATION;
		case 2:
			return View.REFUND;
		case 0:
			return View.HOME;
		default:
			System.out.print("잘못선택하셨습니다. ");
			return View.TICKET;
		}
	}

	public int searchingStation() {

		List<Map<String, Object>> list = dao.stationList();

		if (list == null) {
			System.out.println("버스가 없습니다.");
		} else {
			System.out.println("-----------------------------");
			int sec = 1;
			for (Map<String, Object> item : list) {

				System.out.printf("%2d : %15s\n", sec, item.get("ST_NAME"));
				sec++;
			}
			System.out.println("-----------------------------");
		}

		System.out.println("되돌아가기 : 0");
		System.out.println("목적지 번호를 입력하세요 >>");

		int st = ScanUtil.nextInt();
		if (st == 0)
			return View.TICKET_LIST;
		
		try {
			ticketStorage.put("station", list.get(st - 1).get("ST_ID"));
			return View.TICKET_SEARCHING_ROUTE;
		} catch (Exception e) {
			System.out.println("잘못 입력하셨습니다.");
			resetTicketStorage();
			return View.TICKET_LIST;
		}

	}

	public int searchingRoute() {

		param = new ArrayList<>();
		param.add(ticketStorage.get("station"));
		List<Map<String, Object>> list = dao.routeList(param);

		if (list == null) {
			System.out.println("노선이 없습니다.");
			return View.TICKET_SEARCHING_STATION;
		} else {
			System.out.println("-----------------------------");
			System.out.println(" \t    노선 \t\t  시간   \t\t  버스번호 \t\t등급");
			int sec = 1;

			for (Map<String, Object> item : list) {

				System.out.printf("%2d : %12s " ,sec, item.get("RT_NAME"));
				String str = String.valueOf(item.get("SV_DATE"));
				System.out.print("\t" + str.substring(0, 13));
				System.out.printf("\t%9s\t" , item.get("BUS_NUM"));
				if (item.get("BUS_GRADE").equals("p"))
					System.out.println("프리미엄");
				else if (item.get("BUS_NUM").equals("u"))
					System.out.println("우등");
				else
					System.out.println("일반");

				sec++;
			}
			System.out.println("-----------------------------");
		}

		System.out.println("되돌아가기 : 0");
		System.out.print("노선 번호를 입력하세요 >>");

		int st = ScanUtil.nextInt();

		if (st == 0) {
			resetTicketStorage();
			return View.TICKET;
		}
		
		try {
			ticketStorage.put("service", list.get(st - 1).get("SV_ID"));
			ticketStorage.put("bus", list.get(st - 1).get("BUS_ID"));
			ticketStorage.put("grade", list.get(st - 1).get("BUS_GRADE"));
			ticketStorage.put("price", Integer.parseInt(String.valueOf(list.get(st - 1).get("RT_PRICE"))));
			return View.TICKET_SEARCHING_BUS_SEAT;
		} catch (Exception e) {
			System.out.println("잘못 입력하셨습니다.");
			resetTicketStorage();
			return View.TICKET_SEARCHING_ROUTE;
		}

	}

	public int searchingSeat() {

		List<Object> param = new ArrayList<>();
		List<Object> seatStack = new ArrayList<>();

		param.add(ticketStorage.get("service"));

		List<Map<String, Object>> list = dao.seatList(param);
		
		seat = new boolean[50];
		for (int i = 0; i < seat.length; i++)
			seat[i] = true;
		int selectMaxSeat;

		if (list != null) {
			for (Map<String, Object> item : list) {
				int set = Integer.parseInt(String.valueOf(item.get("TK_SNUM")));
				seat[set] = false;
			}
		}

		while (true) {

			if (ticketStorage.get("grade").equals("p")) {
				selectMaxSeat = 21;
				sv.pr(seat);
			} else if (ticketStorage.get("grade").equals("u")) {
				selectMaxSeat = 28;
				sv.ud(seat);
			} else {
				selectMaxSeat = 45;
				sv.nm(seat);
			}

			System.out.println("처음으로 : 0");
			System.out.print("좌석 번호를 입력하세요 >>");

			int selectSeat = ScanUtil.nextInt();

			if (selectSeat == 0)
				return View.TICKET;

			if (seat[selectSeat]) {
				if (selectMaxSeat < selectSeat || selectSeat <= 0) {
					System.out.println("잘못 입력하셨습니다.");
					continue;
				}

				seatStack.add(selectSeat);
				seat[selectSeat] = false;

				System.out.println(selectSeat + "번 좌석이 성공적으로 추가되었습니다.");
				System.out.println("선택한 좌석 : ");
				for (Object item : seatStack)
					System.out.println(item + "번 ");

				System.out.print("추가로 선택하시겠습니까? (y/n) : ");

				String yn = null;
				yn = ScanUtil.nextLine();

				if (yn.equals("y")) {
					continue;
				} else if (yn.equals("n")) {
					ticketStorage.put("seat", seatStack);
					return View.TICKET_BUY;
				} else {
					System.out.println("잘못 입력하셨습니다. ");
				}
			} else
				System.out.println("이미 선택된 좌석입니다.");
		}
	}

	public int buy() {
		TicketPriceCalc calcPrice = new TicketPriceCalc();
		List<Object> seatStack = (List<Object>) ticketStorage.get("seat");
		param = new ArrayList<>();

		List<Object> logParam = new ArrayList<>();
		logParam.add(Controller.sessionStorage.get("loginInfo"));
		int seatCount = seatStack.size();

		int littlePerson = 0;

		System.out.println("총 " + seatCount + " 자리를 선택하셨습니다.");

		while (true) {
			if (seatCount != 1) {
				System.out.println("만 13세 이하의 어린이만 할인을 받을수 있습니다.");
				System.out.println("어린이의 숫자를 입력 >> ");
				littlePerson = ScanUtil.nextInt();
				if (littlePerson > seatCount) {
					System.out.println("잘못 입력하셨습니다.");
					continue;
				}
				else
					break;
			} else {
				int cstAge = dao.getAge(logParam);
				if (cstAge <= 13)
					littlePerson++;
				break;
			}
		}

		List<Object> seatParam = new ArrayList<>();
		seatParam = (List<Object>) ticketStorage.get("seat");

		int totalPrice = 0;
		int ltmp = littlePerson;
		
		for (Object item : seatParam) {
			if (ltmp > 0) {
				totalPrice += calcPrice.price((int)ticketStorage.get("price"), (String)ticketStorage.get("grade"), "어린이");
				ltmp--;
			} else {
				totalPrice += calcPrice.price((int)ticketStorage.get("price"), (String)ticketStorage.get("grade"), "일반");
			}
		}

		System.out.println("총 결재금액 : " + totalPrice);
		int totalPriceTemp = totalPrice;
		while(true) {
			System.out.print("결제할 금액 입력 >> ");
			int cstPayment = ScanUtil.nextInt();
			if(cstPayment >= totalPriceTemp) {
				totalPriceTemp -= cstPayment;
				System.out.println("거스름돈 : " + totalPriceTemp);
				break;
			} else {
				totalPriceTemp -= cstPayment;
				System.out.println("추가로 결제할 금액 : " + totalPriceTemp);
			}
		}
		List<Object> billParam = new ArrayList<>();
		List<Object> ticketParam = null;

		Timestamp tst = Timestamp.valueOf(LocalDateTime.now());
		SimpleDateFormat sf1 = new SimpleDateFormat("yyMMdd");
		SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMdd");

		List<Object> resdateparam = new ArrayList<>();
		List<Object> tkdateparam = new ArrayList<>();

		resdateparam.add(sf1.format(tst));
		tkdateparam.add(sf2.format(tst));
		
		System.out.println("resdate : " + resdateparam);
		String resId = ("RS" + ((String) dao.getResId(resdateparam).get("ID"))).trim();

		billParam.add(resId);
		billParam.add(Controller.sessionStorage.get("loginInfo"));
		billParam.add(totalPrice);
		billParam.add(tst);

		System.out.println("billParam : " + billParam);
		dao.bill(billParam);

		for (int i = 0; i < seatParam.size(); i++) {
			ticketParam = new ArrayList<>();
			System.out.println("tkdate : " + tkdateparam);
			Map<String, Object> tkId = dao.getTkId(tkdateparam);
			ticketParam.add(("T"+tkId.get("ID")).trim());
			ticketParam.add(ticketStorage.get("bus"));
			ticketParam.add(resId);
			ticketParam.add(ticketStorage.get("service"));
			ticketParam.add(seatParam.get(i));
			if (littlePerson != 0) {
				ticketParam.add("어린이");
				littlePerson--;
			}else
				ticketParam.add("일반");
			//System.out.println("ticketParam : " + ticketParam);
			dao.buyTicket(ticketParam);
		}
		resetTicketStorage();
		return View.HOME;
	}

	public int ticketList() {
		if ( !((boolean) Controller.sessionStorage.get("login")) ) {
			System.out.println("로그인 해주세요 ");
			return View.MEMBER_LOGIN;
		}
		
		List<Object> session = null;
		param = new ArrayList<>();
		param.add(Controller.sessionStorage.get("loginInfo"));
		List<Map<String, Object>> list = dao.ticketList(param);
		if (list == null) {
			System.out.println("티켓이 없습니다.");
		} else {

			System.out.println("번호 \t 노선 \t 출발시간 \t \t버스번호 \t 좌석번호 \t나이구분 \t구매일자 \t 버스등급 ");
			System.out.println("----------------------------------------------------------");
			int sec = 1;
			for (Map<String, Object> item : list) {

				System.out.printf(" %2d : %15s\t" ,sec , item.get("RT_NAME"));
				String str = String.valueOf(item.get("SV_DATE"));
				System.out.print(" \t" + str.substring(0, 13));
				System.out.printf(" \t%10s" , item.get("BUS_NUM"));
				System.out.printf(" \t" + item.get("TK_SNUM"));
				System.out.printf(" \t%5s" , item.get("AGE_DIV"));
				System.out.printf(" \t " + item.get("RES_DATE"));
				if (item.get("BUS_GRADE").equals("p"))
					System.out.println(" \t " + "프리미엄");
				else if (item.get("BUS_NUM").equals("u"))
					System.out.println(" \t " + "  우등");
				else
					System.out.println(" \t " + "  일반");
				sec++;
			}
			System.out.println("----------------------------------------------------------");
		}
		return View.HOME;
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
