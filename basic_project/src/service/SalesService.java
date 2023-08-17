package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dao.SalesDAO;
import util.ScanUtil;
import util.View;

public class SalesService {
	private static SalesService instance = null;

	private SalesService() {
	}

	public static SalesService getInstance() {
		if (instance == null)
			instance = new SalesService();
		return instance;
	}

	SalesDAO dao = SalesDAO.getInstance();
	List<Object> param = null;

	public int salesList() {
		System.out.println("==============================================================================");
		System.out.println("관리자메뉴를 선택해주세요.");
		System.out.println("1.연도별 매출 | 2. 월별 매출 | 3. 일별 매출 | 0.홈");
		System.out.println("==============================================================================");

		System.out.print(">>메뉴 번호 입력 : ");
		int SelectMenu = ScanUtil.nextInt();

		switch (SelectMenu) {
		case 1:
			return salesYear();
		case 2:
			return salesMonth();
		case 3:
			return salesDay();
		default:
			break;
		}
		return View.HOME;
	}

	public int salesYear() {

		System.out.print(">>조회할 연도 입력 (yy) : ");
		String date = ScanUtil.nextLine();
		List<Object> param = new ArrayList<>();
		List<Map<String, Object>> list = null;

		param.add("yy");
		param.add(date);
		list = dao.SalesList(param);

		int sec = 1;
		if (list == null) {
			System.out.println("자료가 없습니다.");
			return View.MANAGE_SALES;
		}

		System.out.println("\t노선 \t탑승인원 \t매출");
		for (Map<String, Object> item : list) {

			System.out.print(sec + ". " + item.get("RT"));
			System.out.print(" \t " + item.get("CNT"));
			System.out.println(" \t " + item.get("SAL"));
			sec++;
		}
		System.out.println();
		return View.MANAGE_SALES;
	}

	public int salesMonth() {
		System.out.print(">>조회할 연도 입력 (yyMM) : ");
		String date = ScanUtil.nextLine();
		List<Object> param = new ArrayList<>();
		List<Map<String, Object>> list = null;

		param.add("yyMM");
		param.add(date);
		list = dao.SalesList(param);
		
		int sec = 1;
		if (list == null) {
			System.out.println("자료가 없습니다.");
			return View.MANAGE_SALES;
		}

		System.out.println("\t노선 \t탑승인원 \t매출");
		for (Map<String, Object> item : list) {

			System.out.print(sec + ". " + item.get("RT"));
			System.out.print(" \t " + item.get("CNT"));
			System.out.println(" \t " + item.get("SAL"));
			sec++;
		}
		System.out.println();
		return View.MANAGE_SALES;
	}

	public int salesDay() {
		System.out.print(">>조회할 연도 입력 (yyMMdd) : ");
		String date = ScanUtil.nextLine();
		List<Object> param = new ArrayList<>();
		List<Map<String, Object>> list = null;

		param.add("yyMMdd");
		param.add(date);
		list = dao.SalesList(param);

		int sec = 1;
		if (list == null) {
			System.out.println("자료가 없습니다.");
			return View.MANAGE_SALES;
		}

		System.out.println("\t노선 \t탑승인원 \t매출");
		for (Map<String, Object> item : list) {

			System.out.print(sec + ". " + item.get("RT"));
			System.out.print(" \t " + item.get("CNT"));
			System.out.println(" \t " + item.get("SAL"));
			sec++;
		}
		System.out.println();
		return View.MANAGE_SALES;
	}
}
