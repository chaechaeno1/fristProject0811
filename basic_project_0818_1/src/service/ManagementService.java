package service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import controller.Controller;
import dao.ManagementDAO;
import util.ScanUtil;
import util.View;

public class ManagementService {
	// 싱글톤 패턴
	private static ManagementService instance = null;

	private ManagementService() {
	}

	public static ManagementService getInstance() {
		if (instance == null)
			instance = new ManagementService();
		return instance;
	}

	// AdminDAO 호출 생성
	ManagementDAO dao = ManagementDAO.getInstance();

	public int adlogin() {
		System.out.println("관리자 로그인 ");
		while (true) {
			System.out.print("아이디 입력>>> ");
			String adIdInput = ScanUtil.nextLine().trim();
			System.out.print("비밀번호 입력>>> ");
			String adPwInput = ScanUtil.nextLine().trim();

			Map<String, Object> admin = dao.adminlist();

			if (admin.get("AD_ID").equals(adIdInput) && admin.get("AD_PW").equals(adPwInput)) {
				Controller.sessionStorage.put("login", true);
				Controller.sessionStorage.put("loginInfo", admin.get("AD_ID"));
				return View.MANAGE_MENU; // 로그인 성공 시 메소드 종료.
			} else {
				System.out.println("잘못된 아이디/비밀번호입니다. 다시 입력해주세요.");
				System.out.println("==============================================================================");
				return View.HOME;
			}
		}
	}

	public int selectMenu() {
		while (true) {
			if (!((boolean) Controller.sessionStorage.get("login"))) {
				System.out.println("로그인 해주세요 ");
				return View.MANAGE_LOGIN;
			} else if (!Controller.sessionStorage.get("loginInfo").equals("admin")) {
				System.out.println("관리자가 아닙니다.");
				return View.HOME;
			}

			System.out.println("==============================================================================");
			System.out.println("관리자메뉴를 선택해주세요.");
			System.out.println("1. 운행설정 | 2. 버스설정 | 3. 터미널설정 | 4. 노선설정 | 9. 매출 관리 | 0.홈");
			System.out.println("==============================================================================");
			System.out.print(">>메뉴 번호 입력 : ");
			int SelectMenu = ScanUtil.nextInt();

			switch (SelectMenu) {
			case 1:
				return View.MANAGE_SERVICE;
			case 2:
				return View.MANAGE_BUS;
			case 3:
				return View.MANAGE_STATION;
			case 4:
				return View.MANAGE_ROUTE;
			case 9:
				return View.MANAGE_SALES;
			default:
				return View.HOME;
			}

		}
	}

	public int serviceMenu() {
		System.out.println("==============================================================================");
		System.out.println("메뉴를 선택해주세요.");
		System.out.println("1. 운행 추가 | 2. 운행 삭제 | 9. 운행 목록 | 0.홈 ");
		System.out.println("==============================================================================");
		System.out.print(">>메뉴 번호 입력 : ");
		int SelectMenu = ScanUtil.nextInt();

		switch (SelectMenu) {
		case 1:
			return View.MANAGE_SERVICE_INSERT;
		case 2:
			return View.MANAGE_SERVICE_DELETE;
		case 9:
			return View.MANAGE_SERVICE_LIST;
		default:
			return View.HOME;
		}
	}

	public int submitsv() {

		List<Map<String, Object>> routelist = dao.route();

		if (routelist == null) {
			System.out.println("등록된 노선 목록이 없습니다.");
			System.out.println("===========================================================");

			System.out.print("노선을 추가하시겠습니까? (y/n) >> ");
			String yn = ScanUtil.nextLine();
			if (yn.equals("y"))
				return View.MANAGE_ROUTE;
			return View.MANAGE_MENU;

		}

		System.out.println("==============================================================================");
		System.out.printf("번호         노선ID      노선명                   비용                   승차홈     소요시간\n");
		System.out.println("==============================================================================");

		int sec = 1;
		for (Map<String, Object> item : routelist) {
			System.out.printf("%2d  : %8s\t", sec, item.get("RT_ID"));
			System.out.printf("%10s\t", item.get("RT_NAME"));
			System.out.printf("%10s\t", String.valueOf(item.get("RT_PRICE")));
			System.out.printf("%4s", String.valueOf(item.get("RT_PNUM")));
			System.out.printf("%6s", String.valueOf(item.get("RT_TIME")));
			System.out.println();
			sec++;
		}

		List<Object> param = new ArrayList<>();

		System.out.println("==============================================================================");

		int rtIdInput;

		while (true) {
			System.out.println(" 0 : 홈으로 ");
			System.out.print("노선 선택 >> ");
			rtIdInput = ScanUtil.nextInt();

			if (rtIdInput == 0)
				return View.MANAGE_MENU;
			if (routelist.size() < rtIdInput || rtIdInput <= 0) {
				System.out.println("노선 입력오류!!\n리스트에 있는 노선ID를 확인해주시기 바랍니다.");
				System.out.println("==============================================================================");
			} else {
				System.out.println("선택된 노선 : " + routelist.get(rtIdInput - 1).get("RT_NAME"));
				param.add(routelist.get(rtIdInput - 1).get("RT_ID"));
				break;
			}
		}

		// 운행시간: 04:00 ~ 23:00로 제한

		List<Object> idparam = null;

		try {

			String dateInput = "";
			String timeInput = "";
			LocalDateTime dateTime;

			while (true) {
				System.out.println("==============================================================================");
				System.out.println("운행일자를 입력해주세요. (yyyyMMdd) >> ");
				System.out.print("운행날짜 입력 >> ");
				dateInput = ScanUtil.nextLine().trim();

				if (dateInput.length() != 8) {
					System.out.println(" 형식에 맞춰 다시 입력하세요. (yyyyMMdd)");
					continue;
				}

				System.out.println("==============================================================================");
				System.out.println("  범위 : (04:00 ~ 23:00)");
				System.out.print("운행시간을 입력해주세요  (hhmm) >> ");
				timeInput = ScanUtil.nextLine();

				if (timeInput.length() != 4) {
					System.out.println(" 형식에 맞춰 다시 입력하세요. (hhmm)");
					continue;
				}

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");

				idparam = new ArrayList<>();
				idparam.add(dateInput.substring(2));

				dateTime = LocalDateTime.parse(dateInput + " " + timeInput, formatter);

				if (LocalDateTime.now().isAfter(dateTime)) {
					System.out.println("현재시간 이전으로 설정할 수 없습니다.");
					continue;
				}
				break;
			}

			Timestamp dt = Timestamp.valueOf(dateTime);

			param.add(dt);
		} catch (Exception e) {
			System.out.println("잘못 입력하셨습니다.");
			return View.MANAGE_MENU;
		}

		System.out.println("==============================================================================");

		System.out.println();

		System.out.println("==============================================================================");
		System.out.println("번호\t버스ID\t차량번호\t버스등급");
		System.out.println("==============================================================================");
		// DB에 등록된 BUS 테이블 조회
		List<Map<String, Object>> buslist = dao.buslist();
		sec = 1;
		for (Map<String, Object> item : buslist) {
			System.out.printf("%2d : %6s ", sec, item.get("BUS_ID"));
			System.out.printf("%10s \t", String.valueOf(item.get("BUS_NUM")));
			if (item.get("BUS_GRADE").equals("p"))
				System.out.print("프리미엄");
			else if (item.get("BUS_GRADE").equals("u"))
				System.out.print("우등");
			else
				System.out.print("일반");

			System.out.println();
			sec++;
		}

		System.out.println("==============================================================================");
		while (true) {
			System.out.print("버스 선택 >> ");
			int busInput = ScanUtil.nextInt();

			if (buslist.size() < busInput || busInput <= 0) {
				System.out.println("버스 입력오류!!\n리스트에 있는 버스를 선택해주시기 바랍니다.");
				System.out.println("==============================================================================");

			} else {
				System.out.println("선택된 노선 : " + buslist.get(busInput - 1).get("BUS_NUM"));
				param.add(buslist.get(busInput - 1).get("BUS_ID"));
				break;
			}
		}

		// SV_ID 생성
		Map<String, Object> ID = dao.getsvId(idparam);
		String str = (String) (param.get(0));
		String svID = str.trim() + ID.get("ID");
		param.add(svID);

		while (true) {
			System.out.println("******** 등록된 데이터 ********");
			for (Object p : param) {
				System.out.print(p + "\t");
			}
			System.out.println("****************************");
			System.out.print("입력한 정보가 맞습니까? (y/n) >> ");
			String yn = ScanUtil.nextLine();
			if (yn.equals("y"))
				break;
			else if (yn.equals("n"))
				return View.MANAGE_MENU;
			else
				System.out.println("잘못 입력하셨습니다.");
		}

		dao.insert(param);
		System.out.println("운행 노선 등록이 완료되었습니다.");

		return View.MANAGE_MENU;
	}

	public int deletesv() {

		List<Map<String, Object>> servicelist = dao.servicelist();
		int sec = 1;
		if (servicelist == null) {
			System.out.println("등록된 노선 목록이 없습니다.");
			return View.MANAGE_MENU;

		} else {
			System.out.println("==============================================================================");
			System.out.println("\t운행ID\t\t\t운행일시\t  노선ID\t\t버스ID");
			System.out.println("==============================================================================");

			SimpleDateFormat sdf = new SimpleDateFormat("yy년 MM월 dd일 kk:mm");

			for (Map<String, Object> item : servicelist) {
				System.out.printf("%2d : %16s", sec, item.get("SV_ID"));
				String str = String.valueOf(item.get("SV_DATE"));
				System.out.printf("\t%15s", str.substring(2, 16));
				System.out.printf("\t%8s", item.get("RT_ID"));
				System.out.printf("\t%8s", item.get("BUS_ID"));
				System.out.println();
				sec++;
			}

			List<Object> param = new ArrayList<>();

			System.out.println("==============================================================================");

			int svIdInput;

			while (true) {
				System.out.println("되돌아가기 : 0");
				System.out.print("운행번호 입력 >> ");
				svIdInput = ScanUtil.nextInt();

				if (svIdInput == 0) {
					System.out.print("되돌아 갑니다.");
					return View.MANAGE_ROUTE;
				}

				if (servicelist.size() < svIdInput || svIdInput <= 0) {
					System.out.println("운행번호 입력오류!!\n리스트에 있는 운행번호를 확인해주시기 바랍니다.");
					System.out
							.println("==============================================================================");
				} else {
					System.out.println("삭제할 운행번호 >> " + servicelist.get(svIdInput - 1).get("SV_ID"));
					param.add(servicelist.get(svIdInput - 1).get("SV_ID"));
					break;
				}
			}

			System.out.print("정말 삭제 하시겠습니까? (y/n) >>");
			String yn = ScanUtil.nextLine();

			if (yn.equals("n")) {
				System.out.print("되돌아 갑니다.");
				return View.MANAGE_ROUTE;
			} else if (yn.equals("y")) {
				int flag = dao.ServiceDelete(param);
				if (flag != 0)
					System.out.println("운행 노선 삭제가 완료되었습니다.");
				else
					System.out.println("운행 노선 삭제에 실패하였습니다...");
			} else
				return View.MANAGE_ROUTE;
			return View.MANAGE_MENU;
		}

	}

	public int manageBus() {

		System.out.println("==============================================================================");
		System.out.println("관리자메뉴를 선택해주세요.");
		System.out.println("1. 버스 추가 | 2. 버스 삭제 | 9.버스 목록 | 0.홈");
		System.out.println("==============================================================================");
		System.out.print(">>메뉴 번호 입력 : ");
		int SelectMenu = ScanUtil.nextInt();

		if (SelectMenu == 1) {
			// 버스추가

			System.out.print("차량번호 입력 >> ");
			String busnum = ScanUtil.nextLine();

			System.out.print("버스등급은 프리미엄(p), 우등(u), 일반(n) 입니다 ");
			System.out.print("버스등급 입력 >> ");
			String busgrade = ScanUtil.nextLine();
			if (busgrade.equals("p") || busgrade.equals("P") || busgrade.equals("프리미엄")) {
				busgrade = "p";
			} else if (busgrade.equals("u") || busgrade.equals("U") || busgrade.equals("우등")) {
				busgrade = "u";
			} else {
				busgrade = "n";
			}
			List<Object> param = new ArrayList<>();

			Map<String, Object> busid = dao.getBusId();

			param.add(busid.get("ID"));
			param.add(busnum);
			param.add(busgrade);

			int result = dao.busAdd(param);
			if (result > 0) {
				System.out.println("버스가 추가되었습니다.");
			} else {
				System.out.println("버스 추가에 실패하였습니다...");
			}
		} else if (SelectMenu == 2) {
			// 버스삭제

			List<Map<String, Object>> busList = dao.buslist();

			if (busList == null) {
				System.out.println("삭제할 버스가 없습니다.");
				return View.MANAGE_MENU;
			}
			
			System.out.println("==============================================================================");
			System.out.println("번호   버스ID \t 차량번호 \t등급");
			System.out.println("==============================================================================");
			int sec = 1;
			for (Map<String, Object> item : busList) {
				System.out.printf("%2d : %6s ", sec, item.get("BUS_ID"));
				System.out.printf("%10s \t", String.valueOf(item.get("BUS_NUM")));
				if (item.get("BUS_GRADE").equals("p"))
					System.out.print("프리미엄");
				else if (item.get("BUS_GRADE").equals("u"))
					System.out.print("우등");
				else
					System.out.print("일반");
				sec++;
				System.out.println();
			}

			System.out.print("삭제할 버스를 입력 >> ");
			int selectbus = ScanUtil.nextInt();
			System.out.println("size : " + selectbus + " " + busList.size());

			if (busList.size() < selectbus || selectbus <= 0) {
				System.out.println("잘못된 입력");
				return View.MANAGE_BUS;
			}

			System.out.println("정말 삭제하시겠습니까? (y/n) >>");
			String yn = ScanUtil.nextLine();

			if (yn.equals("n"))
				System.out.println("되돌아 갑니다.");
			else if (yn.equals("y")) {
				List<Object> param = new ArrayList<>();
				param.add(busList.get(selectbus - 1).get("BUS_ID"));

				int flag = dao.busDelete(param);
				if (flag == 0)
					System.out.println("버스 삭제에 실패하였습니다...");
				else
					System.out.println("버스 삭제에 성공하였습니다.");
			} else
				System.out.println("잘못 입력 하셨습니다.");

		} else if (SelectMenu == 9) {
			// 버스목록

			List<Map<String, Object>> busList = dao.buslist();

			if (busList == null) {
				System.out.println("버스 목록이 없습니다.");
				return View.MANAGE_MENU;
			}
			
			System.out.println("==============================================================================");
			System.out.println("번호   버스ID \t 차량번호 \t등급");
			System.out.println("==============================================================================");

			int sec = 1;
			for (Map<String, Object> item : busList) {
				System.out.printf("%2d : %6s ", sec, item.get("BUS_ID"));
				System.out.printf("%10s \t", String.valueOf(item.get("BUS_NUM")));
				if (item.get("BUS_GRADE").equals("p"))
					System.out.print("프리미엄");
				else if (item.get("BUS_GRADE").equals("u"))
					System.out.print("우등");
				else
					System.out.print("일반");
				sec++;
				System.out.println();
			}
		} else if (SelectMenu == 0)
			return View.HOME;
		return View.MANAGE_BUS;

	}

	public int manageStation() {

		System.out.println("==============================================================================");
		System.out.println("관리자메뉴를 선택해주세요.");
		System.out.println("1. 터미널 추가 | 2. 터미널 삭제 | 9.터미널 목록 | 0.홈");
		System.out.println("==============================================================================");
		System.out.print(">>메뉴 번호 입력 : ");
		int SelectMenu = ScanUtil.nextInt();

		if (SelectMenu == 1) {
			// 터미널 추가

			System.out.print("터미널이름 >> ");
			String stsName = ScanUtil.nextLine();

			System.out.print("터미널 주소 >> ");
			String stsAdd = ScanUtil.nextLine();

			List<Object> param = new ArrayList<>();

			Map<String, Object> stsid = dao.getStationId();

			param.add(stsid.get("ID"));
			param.add(stsName);
			param.add(stsAdd);

			int result = dao.StationAdd(param);
			if (result > 0) {
				System.out.println("터미널이 추가되었습니다.");
			} else {
				System.out.println("터미널 추가실패!");
			}
		} else if (SelectMenu == 2) {
			// 터미널 삭제

			List<Map<String, Object>> stsList = dao.Stationlist();

			if (stsList == null) {
				System.out.println("삭제할 터미널이 없습니다.");
				return View.MANAGE_MENU;
			}
			
			System.out.println("==============================================================================");
			System.out.println("번호   \t터미널 이름 \t 주소");
			System.out.println("==============================================================================");
		
			int sec = 1;
			for (Map<String, Object> item : stsList) {

				System.out.printf("%2d : %8s", sec, item.get("ST_NAME"));
				System.out.print("\t\t" + item.get("ST_LOC") + "\t");

				sec++;
				System.out.println();
			}

			System.out.println("되돌아가기 : 0");
			System.out.print("삭제할 터미널을 입력 : ");
			int selectSation = ScanUtil.nextInt();

			if (selectSation == 0) {
				System.out.println("되돌아 갑니다.");
				return View.MANAGE_STATION;
			}

			System.out.println("size : " + selectSation + " " + stsList.size());

			if (stsList.size() < selectSation || selectSation <= 0) {
				System.out.println("잘못된 입력");
				return View.MANAGE_STATION;
			}

			List<Object> param = new ArrayList<>();
			param.add(stsList.get(selectSation - 1).get("ST_ID"));

			System.out.println("정말 삭제하시겠습니까? (y/n) >>");
			String yn = ScanUtil.nextLine();

			if (yn.equals("n")) {
				System.out.println("되돌아 갑니다.");
				return View.MANAGE_STATION;
			} else if (yn.equals("y")) {

				int flag = dao.stationDelete(param);

				if (flag == 0)
					System.out.println("터미널 삭제 실패");
				else
					System.out.println("터미널 삭제 성공");
			} else {
				System.out.println("잘못 입력 하셨습니다.");
				return View.MANAGE_STATION;
			}

		} else if (SelectMenu == 9) {
			// 터미널 목록

			List<Map<String, Object>> stsList = dao.Stationlist();

			if (stsList == null) {
				System.out.println("터미널 목록이 없습니다.");
				return View.MANAGE_MENU;
			}
			
			System.out.println("==============================================================================");
			System.out.println("번호   \t터미널 이름 \t 주소");
			System.out.println("==============================================================================");

			int sec = 1;
			for (Map<String, Object> item : stsList) {

				System.out.printf("%2d : %8s", sec, item.get("ST_NAME"));
				System.out.print("\t" + item.get("ST_LOC") + "\t");

				sec++;
				System.out.println();
			}
		} else if (SelectMenu == 0)
			return View.HOME;
		return View.MANAGE_STATION;

	}

	public int manageRoute() {

		System.out.println("==============================================================================");
		System.out.println("관리자메뉴를 선택해주세요.");
		System.out.println("1. 노선 추가 | 2. 노선 삭제 | 9.노선 목록 | 0.홈");
		System.out.println("==============================================================================");
		System.out.print(">>메뉴 번호 입력 : ");
		int SelectMenu = ScanUtil.nextInt();

		if (SelectMenu == 1) {

			List<Map<String, Object>> stsList = dao.Stationlist();
			List<Object> param = new ArrayList<>();

			if (stsList == null) {
				System.out.println("터미널 목록이 없습니다.");
				System.out.println("터미널을 추가하시겠습니까?");
				String yn = ScanUtil.nextLine();
				if (yn.equals("y"))
					return View.MANAGE_STATION;
				return View.MANAGE_MENU;
			}
			int sec = 1;
			for (Map<String, Object> item : stsList) {
				System.out.printf("%2d : %15s", sec, item.get("ST_NAME"));
				System.out.print("\t" + item.get("ST_LOC") + "\t");

				sec++;
				System.out.println();
			}

			System.out.print("출발 노선 선택 >> ");
			int startRoute = ScanUtil.nextInt();

			System.out.print("도착 노선 선택 >> ");
			int endRoute = ScanUtil.nextInt();

			System.out.println("선택한 노선 : " + startRoute + "-" + endRoute);

			if (startRoute == endRoute) {
				System.out.println("출발지와 도착지가 동일합니다. ");
				return View.MANAGE_ROUTE;
			} else if (stsList.size() < startRoute || stsList.size() < endRoute || startRoute <= 0 || endRoute <= 0) {
				System.out.println("잘못된 입력입니다.");
				return View.MANAGE_ROUTE;
			}

			System.out.print("노선 이름 입력 >> ");
			String routeName = ScanUtil.nextLine();

			System.out.print("노선 가격 입력 >> ");
			int price = ScanUtil.nextInt();

			System.out.print("플랫폼 번호 입력 >> ");
			int pnum = ScanUtil.nextInt();

			System.out.print("소요시간 입력 (분) >> ");
			int serviceTime = ScanUtil.nextInt();

			String sts = (String) stsList.get(startRoute - 1).get("ST_ID");
			String eds = (String) stsList.get(endRoute - 1).get("ST_ID");
			String ID = "RT" + sts.substring(3) + eds.substring(3);

			param.add(ID);
			param.add(stsList.get(startRoute - 1).get("ST_ID"));
			param.add(stsList.get(endRoute - 1).get("ST_ID"));
			param.add(routeName);
			param.add(price);
			param.add(pnum);
			param.add(serviceTime);

			while (true) {
				System.out.println(param);
				System.out.print("이대로 추가하시겠습니까? (y/n) >>");
				String yn = ScanUtil.nextLine();
				if (yn.equals("y"))
					break;
				else if (yn.equals("n"))
					return View.MANAGE_ROUTE;
				else
					System.out.println("잘못 입력");
			}

			int flag = dao.routeAdd(param);

			if (flag > 0) {
				System.out.println("노선이 추가되었습니다.");
			} else {
				System.out.println("노선 추가 실패!");
			}

		} else if (SelectMenu == 2) {
			List<Map<String, Object>> rtList = dao.route();

			if (rtList == null) {
				System.out.println("삭제할 노선이 없습니다.");
				return View.MANAGE_ROUTE;
			}

			System.out.println("==============================================================================");
			System.out.println("번호         노선ID\t 노선 이름 \t\t 기준가격 \t\t 플랫폼 \t 운행시간 ");
			System.out.println("==============================================================================");
			
			int sec = 1;
			for (Map<String, Object> item : rtList) {
				System.out.printf("%2d  : %8s\t", sec, item.get("RT_ID"));
				System.out.printf("%10s\t", item.get("RT_NAME"));
				System.out.printf("%10s\t", String.valueOf(item.get("RT_PRICE")));
				System.out.printf("%4s", String.valueOf(item.get("RT_PNUM")));
				System.out.printf("%6s", String.valueOf(item.get("RT_TIME")));
				System.out.println();
				sec++;
			}

			System.out.print("삭제할 노선을 입력 : ");
			int selectSation = ScanUtil.nextInt();

			if (rtList.size() < selectSation || selectSation <= 0) {
				System.out.println("잘못된 입력");
				return View.MANAGE_ROUTE;
			}

			List<Object> param = new ArrayList<>();
			param.add(rtList.get(selectSation - 1).get("RT_ID"));

			System.out.println("정말 삭제하시겠습니까? (y/n) >>");
			String yn = ScanUtil.nextLine();

			if (yn.equals("n"))
				System.out.println("되돌아 갑니다.");
			else if (yn.equals("y")) {
				int flag = dao.routeDelete(param);
				if (flag == 0)
					System.out.println("노선 삭제 실패");
				else
					System.out.println("노선 삭제 성공");
			} else
			System.out.println("잘못 입력 하셨습니다.");
			
			
		} else if (SelectMenu == 9) {
			//노선 목록
			
			List<Map<String, Object>> rtList = dao.route();

			if (rtList == null) {
				System.out.println("노선 목록이 없습니다.");
				return View.MANAGE_ROUTE;
			}
			
			System.out.println("==============================================================================");
			System.out.println("번호         노선ID\t 노선 이름 \t\t 기준가격 \t\t 플랫폼 \t 운행시간 ");
			System.out.println("==============================================================================");

			int sec = 1;
			for (Map<String, Object> item : rtList) {
				System.out.printf("%2d  : %8s\t", sec, item.get("RT_ID"));
				System.out.printf("%10s\t", item.get("RT_NAME"));
				System.out.printf("%10s\t", String.valueOf(item.get("RT_PRICE")));
				System.out.printf("%4s", String.valueOf(item.get("RT_PNUM")));
				System.out.printf("%6s", String.valueOf(item.get("RT_TIME")));
				System.out.println();
				sec++;
			}
		} else if (SelectMenu == 0)
			return View.MANAGE_MENU;
		return View.MANAGE_ROUTE;

	}

	public int serviceList() {
		List<Map<String, Object>> servicelist = dao.servicelist();
		int sec = 1;
		if (servicelist == null) {
			System.out.println("등록된 노선 목록이 없습니다.");
			return View.MANAGE_MENU;

		} else {
			System.out.println("==============================================================================");
			System.out.println("번호         노선ID      노선명                   비용                   승차홈     소요시간");
			System.out.println("==============================================================================");

			for (Map<String, Object> item : servicelist) {
				System.out.printf("%2d : %16s", sec, item.get("SV_ID"));
				String str = String.valueOf(item.get("SV_DATE"));
				System.out.printf("\t%15s", str.substring(2, 16));
				System.out.printf("\t%8s", item.get("RT_ID"));
				System.out.printf("\t%8s", item.get("BUS_ID"));
				System.out.println();
				sec++;
			}
			return View.MANAGE_SERVICE;
		}
	}

}
