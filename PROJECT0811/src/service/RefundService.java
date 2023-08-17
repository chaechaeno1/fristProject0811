package service;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import controller.Controller;
import dao.RefundDAO;
import util.ScanUtil;
import util.View;

public class RefundService {
	// 1. 싱글톤 만들기
	private static RefundService instance = null;

	private RefundService() {
	}

	public static RefundService getInstance() {
		if (instance == null)
			instance = new RefundService();
		return instance;
	}

	// 2. DAO 호출하기 위한 길 만들기
	RefundDAO dao = RefundDAO.getInstance();
	List<Object> param = null;

	public int refund() {

		param = new ArrayList<>();
		param.add(Controller.sessionStorage.get("loginInfo"));
		List<Map<String, Object>> list = dao.ticketList(param);
		System.out.println(list);

		if (list == null) {
			System.out.println("티켓이 없습니다.");
		} else {

			System.out.println("번호 \t 노선 \t 출발시간 \t\t\t \t버스번호 \t\t 좌석번호 \t\t나이구분 \t\t구매일자 \t\t 버스등급 ");
			System.out.println("----------------------------------------------------------");
			int sec = 1;
			for (Map<String, Object> item : list) {

				System.out.print(sec + " : \t" + item.get("RT_NAME"));
				System.out.print(" : \t" + item.get("SV_DATE"));
				System.out.print(" : \t" + item.get("BUS_NUM"));
				System.out.print(" \t" + item.get("TK_SNUM"));
				System.out.print(" \t" + item.get("AGE_DIV"));
				System.out.print(" \t " + item.get("RES_DATE"));
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

		System.out.println("되돌아가기 : 0");
		System.out.println("환불할 티켓의 번호를 입력하세요 >>");

		int st = ScanUtil.nextInt();

		if (st == 0)
			return View.HOME;
		try {
			Controller.ticketStorage.put("ticket", list.get(st - 1).get("TK_ID"));
			return View.REFUND_TICKET;
		} catch (Exception e) {
			System.out.println("잘못 입력하셨습니다.");
			return View.REFUND;
		}
	}

	public int refundTicket() {

		System.out.print("정말 환불하시겠습니까? (y/n) : ");
		String yn = ScanUtil.nextLine();
		if (yn.equals("y")) {
			param = new ArrayList<>();
			param.add(Controller.ticketStorage.get("ticket"));
			
			int flag = dao.refund(param);
			
			if(flag != 0)
				System.out.println("성공");
			else
				System.out.println("실패");
			return View.CASH_RESET;
		} else if (yn.equals("n")) {
			System.out.println("홈으로");
			return View.CASH_RESET;
		}
		else {
			System.out.println("잘못입력");
			return View.REFUND_TICKET;
		}
	}

}
