package service;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import controller.Controller;
import dao.TicketingDAO;
import util.ScanUtil;
import util.SeatView;
import util.View;

public class TicketingService {

	private static TicketingService instance = null;
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
		
		System.out.println("1.구매 2.환불");
		System.out.print("번호 선택 >> ");
		switch (ScanUtil.nextInt()) {
		case 1:
			return View.TICKET_SEARCHING_STATION;
		case 2:
			return View.REFUND;
		default:
			System.out.print("잘못선택하셨습니다. ");
			return View.HOME;
		}
	}

	public int searchingStation() {

		List<Map<String, Object>> list = dao.stationList();

		System.out.println(list);

		if (list == null) {
			System.out.println("버스가 없습니다.");
		} else {
			System.out.println("-----------------------------");
			int sec = 1;
			for (Map<String, Object> item : list) {

				System.out.println(sec + " : \t" + item.get("ST_NAME"));
				sec++;
			}
			System.out.println("-----------------------------");
		}

		System.out.println("되돌아가기 : 0");
		System.out.println("목적지 번호를 입력하세요 >>");

		int st = ScanUtil.nextInt();
		if (st == 0)
			return View.HOME;

		try {
			Controller.ticketStorage.put("station", list.get(st - 1).get("ST_ID"));
			return View.TICKET_SEARCHING_ROUTE;
		} catch (Exception e) {
			System.out.println("잘못 입력하셨습니다.");
			return View.TICKET_SEARCHING_STATION;
		}

	}

	public int searchingRoute() {

		param = new ArrayList<>();
		param.add(Controller.ticketStorage.get("station"));
		List<Map<String, Object>> list = dao.routeList(param);

		System.out.println(list);

		if (list == null) {
			System.out.println("노선이 없습니다.");
		} else {
			System.out.println("-----------------------------");
			System.out.println(" \t  노선   \t\t  시간   \t  버스번호 \t\t등급");
			int sec = 1;
			
			for (Map<String, Object> item : list) {

				System.out.print(sec + " : \t" + item.get("RT_NAME"));
				System.out.print(" \t" + item.get("SV_DATE"));
				System.out.print(" \t" + item.get("BUS_NUM") + " \t");
				if (item.get("BUS_GRADE").equals("p"))
					System.out.println("프리미엄");
				else if (item.get("BUS_NUM").equals("u"))
					System.out.println("  우등");
				else
					System.out.println("  일반");

				sec++;
			}
			if (sec == 1)
				System.out.println("노선이 없습니다.");
			else
				System.out.println("-----------------------------");
		}

		System.out.println("되돌아가기 : 0");
		System.out.println("노선 번호를 입력하세요 >>");

		int st = ScanUtil.nextInt();

		if (st == 0)
			return View.HOME;
		try {
			Controller.ticketStorage.put("service", list.get(st - 1).get("SV_ID"));
			Controller.ticketStorage.put("bus", list.get(st - 1).get("BUS_ID"));
			Controller.ticketStorage.put("grade", list.get(st - 1).get("BUS_GRADE"));
			Controller.ticketStorage.put("price", Integer.parseInt(String.valueOf(list.get(st - 1).get("RT_PRICE"))));
			return View.TICKET_SEARCHING_BUS_SEAT;
		} catch (Exception e) {
			System.out.println("잘못 입력하셨습니다.");
			return View.TICKET_SEARCHING_ROUTE;
		}

	}

	public int searchingSeat() {

		List<Object> param = new ArrayList<>();
		List<Object> seatStack = new ArrayList<>();

		param.add(Controller.ticketStorage.get("service"));

		List<Map<String, Object>> list = dao.seatList(param);
		seat = new boolean[50];
		for (int i = 0; i < seat.length; i++)
			seat[i] = true;
		int selectMaxSeat;

		for (Map<String, Object> item : list) {
			int set = Integer.parseInt(String.valueOf(item.get("TK_SNUM")));
			seat[set] = false;
			System.out.println(set);
		}

		while (true) {

			if (Controller.ticketStorage.get("grade").equals("p")) {
				selectMaxSeat = 21;
				sv.pr(seat);
			} else if (Controller.ticketStorage.get("grade").equals("u")) {
				selectMaxSeat = 28;
				sv.ud(seat);
			} else {
				selectMaxSeat = 45;
				sv.nm(seat);
			}

			System.out.println("처음으로 되돌아가기 : 0");
			System.out.print("좌석 번호를 입력하세요 >>");

			int selectSeat = ScanUtil.nextInt();

			if (selectSeat == 0)
				return View.TICKET;

			if (seat[selectSeat]) {
				if (selectMaxSeat < selectSeat || selectSeat < 0) {
					System.out.println("잘못 입력하셨습니다.1" );
					continue;
				}

				seatStack.add(selectSeat);
				seat[selectSeat] = false;

				System.out.println("성공적으로 추가되었습니다.");

				for (Object item : seatStack)
					System.out.println(item);

				System.out.print("추가로 선택하시겠습니까? (y/n) : ");

				String yn = null; 
				yn = ScanUtil.nextLine();

				if (yn.equals("y")) {
					continue;
				} else if (yn.equals("n")) {
					Controller.ticketStorage.put("seat", seatStack);
					return View.TICKET_BUY;
				} else {
					System.out.println("잘못 입력하셨습니다.2" + yn);
				}
			}
		}
	}

	public int buy() {

		List<Object> seatStack = (List<Object>) Controller.ticketStorage.get("seat");
		param = new ArrayList<>();
		
		List<Object> logParam = new ArrayList<>();
		logParam.add(Controller.sessionStorage.get("loginInfo"));
		int seatCount = seatStack.size();
		
		int littlePerson = 0;

		System.out.println("총 " + seatCount + " 자리를 선택하셨습니다.");
		
		while (true) {
			if (seatCount != 1) {
				System.out.println("만 13세 이하의 어린이만 할인을 받을수 있습니다.");
				System.out.println("어린이의 숫자를 입력 : ");
				littlePerson = ScanUtil.nextInt();
				if(littlePerson > seatCount)
					System.out.println("잘못 입력하셨습니다.");
				break;
			}
			else {
				int cstAge = dao.getAge(logParam);
				if(cstAge <= 13)
					littlePerson++;
				break;
			}
		}
		
		System.out.println("입력한 정보 : ");
		System.out.println(Controller.ticketStorage);
		
		List<Object> seatParam = new ArrayList<>();
		seatParam = (List<Object>) Controller.ticketStorage.get("seat");
		
		int price = (int) Controller.ticketStorage.get("price");
		double gradeMulti = 1;
		int totalPrice = 0;
		
		if(Controller.ticketStorage.get("grade").equals("p")) 
			gradeMulti = 2;
		else if(Controller.ticketStorage.get("grade").equals("u"))
			gradeMulti = 1.5;
		
		for(Object item : seatParam) {
			int tmp=0;
			int ltmp = littlePerson;
			if(ltmp > 0) {
				tmp = (int) (price * gradeMulti * 0.7);
				ltmp--;
			}else {
				tmp = (int) (price * gradeMulti);
			}
			tmp /= 10;
			totalPrice += tmp*10;
		}
		
		System.out.println("총 결재금액 : " + totalPrice);
		
		List<Object> billParam = new ArrayList<>();
		List<Object> ticketParam = null;
		
		Timestamp tst = Timestamp.valueOf(LocalDateTime.now());
		SimpleDateFormat sf1 = new SimpleDateFormat("yyMMdd");
		SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMdd");

		
		List<Object> resdateparam = new ArrayList<>();
		List<Object> tkdateparam = new ArrayList<>();
		
		resdateparam.add(sf1.format(tst));
		tkdateparam.add(sf2.format(tst));
		
		String resId = (String) dao.getResId(resdateparam).get("ID");
		
		billParam.add(resId);
		billParam.add(Controller.sessionStorage.get("loginInfo"));
		billParam.add(totalPrice);
		billParam.add(tst);
		
		dao.bill(billParam);
		
		for(int i=0; i<seatParam.size(); i++) {
			ticketParam = new ArrayList<>();
			Map<String, Object> tkId =  dao.getTkId(tkdateparam);
			ticketParam.add(tkId.get("ID"));
			ticketParam.add(Controller.ticketStorage.get("bus"));
			ticketParam.add(resId);
			ticketParam.add(Controller.ticketStorage.get("service"));
			ticketParam.add(seatParam.get(i));
			if(littlePerson != 0) {
				ticketParam.add("어린이");
				littlePerson--;
			}
			System.out.println(ticketParam);
			dao.buyTicket(ticketParam);
		}
		System.out.println(billParam);
		

		
		return View.HOME;
	}

}
