package service;

import java.sql.Timestamp;
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

	// 구현 완료**
	public int adlogin() {
		System.out.println("관리자 로그인 ");
		while (true) {
			System.out.print("아이디 입력>>> ");
			String adIdInput = ScanUtil.nextLine().trim();

			// 아이디 검증
			boolean adIdValid = false;

			List<Map<String, Object>> adminlist = dao.adminlist();

			for (Map<String, Object> item : adminlist) {
				String adminID = item.get("AD_ID").toString().trim();
				if (adIdInput.equals(adminID)) {
					adIdValid = true;
					break; // 아이디 true면 비밀번호 입력으로 넘어감
				}
			}

			if (!adIdValid) {
				System.out.println("관리자ID 입력오류!!\n다시 입력해주세요.");
				System.out.println("===========================================================");
			} else {
				while (true) {
					System.out.print("비밀번호 입력>>> ");
					String adPwInput = ScanUtil.nextLine().trim();

					// 비밀번호 검증
					boolean adPwValid = false;

					for (Map<String, Object> item : adminlist) {
						String adminID = item.get("AD_ID").toString().trim();
						String adminPW = item.get("AD_PW").toString().trim();
						if (adIdInput.equals(adminID) && adPwInput.equals(adminPW)) {
							adPwValid = true;
							break; // 비밀번호 true면 로그인 성공!
						}
					}
					if (!adPwValid) {
						System.out.println("잘못된 비밀번호입니다. 다시 입력해주세요.");
						System.out.println("===========================================================");
					} else {
						Controller.sessionStorage.put("login", true);
						Controller.sessionStorage.put("loginInfo", adminlist.get(0).get("AD_ID").toString().trim());
						return View.MANAGE_MENU; // 로그인 성공 시 메소드 종료
					}
				}
			}
		}
	}

	// 앞으로 구현해야 할 것

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
			System.out.println("1. 운행설정 | 2. 버스설정 | 3. 정류장설정 | 4. 노선설정 | 7. 매출 관리 | 0.홈");
			System.out.println("==============================================================================");
			System.out.print(">>메뉴 번호 입력 : ");
			int SelectMenu = ScanUtil.nextInt();

			switch (SelectMenu) {
			case 1:
				return View.MANAGE_SERVICE;
			case 3:
				return View.MANAGE_STATION;
			case 7:
				return View.MANAGE_SALES;
			default:
				return View.HOME;
			}

		}
	}

	public int serviceMenu() {
		System.out.println("==============================================================================");
		System.out.println("메뉴를 선택해주세요.");
		System.out.println("1. 운행 추가 | 2. 운행삭제 | 0.홈");
		System.out.println("==============================================================================");
		System.out.print(">>메뉴 번호 입력 : ");
		int SelectMenu = ScanUtil.nextInt();

		switch (SelectMenu) {
		case 1:
			return View.MANAGE_SERVICE_INSERT;
		case 2:
			return View.MANAGE_SERVICE_DELETE;
		default:
			return View.HOME;
		}
	}

	// 구현 완료**
	public int submitsv() {
		System.out.println("===========================================================");
		System.out.println("** 1. 노선을 선택해주세요 (노선 ID를 입력해주세요) **");
		System.out.println();
		System.out.println("==============================================================================");
		System.out.println("노선ID\t\t노선명\t\t비용\t\t승차홈\t\t소요시간");
		System.out.println("==============================================================================");
		List<Map<String, Object>> routelist = dao.route();

		if (routelist == null) {
			System.out.println("등록된 노선 목록이 없습니다.");
			System.out.println("===========================================================");
			return View.MANAGE_MENU;

		}

		int sec = 1;
		// 버스노선 리스트들이 출력됨
		for (Map<String, Object> item : routelist) {
			System.out.print(sec + "  : \t" + item.get("RT_ID"));
			System.out.print("\t\t" + item.get("RT_NAME"));
			System.out.print("\t\t" + item.get("RT_PRICE"));
			System.out.print("\t\t" + item.get("RT_PNUM"));
			System.out.print("\t\t" + item.get("RT_TIME"));
			System.out.println();
			sec++;
		}

		List<Object> param = new ArrayList<>();

		System.out.println("==============================================================================");

		int rtIdInput;

		while (true) {
			System.out.print("노선 선택 : ");
			rtIdInput = ScanUtil.nextInt();

			if (routelist.size() <= rtIdInput || rtIdInput < 0) {
				System.out.println("노선 입력오류!!\n리스트에 있는 노선ID를 확인해주시기 바랍니다.");
				System.out.println("===========================================================");
			} else {
				System.out.println(">>선택된 노선 : " + routelist.get(rtIdInput - 1).get("RT_NAME"));
				param.add(routelist.get(rtIdInput - 1).get("RT_ID"));
				break;

			}
		}

		System.out.println("==============================================================================");
		System.out.println();
		System.out.println("** 2. 운행일자를 입력해주세요 (예시:20230810) **");
		String dateInput = "";
		while (true) {
			System.out.print("운행날짜 입력 : ");
			dateInput = ScanUtil.nextLine().trim();

			if (dateInput.length() != 8) {
				System.out.println(" 형식에 맞춰 다시 입력하세요. (예시:20230810)");
			} else {
				break;
			}
		}

		System.out.println("==============================================================================");
		System.out.println();
		System.out.println("** 3. 운행시간을 입력해주세요 (04:00 ~ 24:00) **");
		// 운행시간: 04:00 ~ 24:00로 제한

		List<Object> idparam = null;

		try {
			System.out.print("운행시간 입력 : ");
			String timeInput = ScanUtil.nextLine();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");

			idparam = new ArrayList<>();
			idparam.add(dateInput.substring(2));

			LocalDateTime date = LocalDateTime.parse(dateInput + " " + timeInput, formatter);
			Timestamp dt = Timestamp.valueOf(date);

			param.add(dt);
		} catch (Exception e) {
			System.out.println("");
			return View.MANAGE_MENU;
		}

		System.out.println("==============================================================================");

		System.out.println();
		System.out.println("** 4. 운행버스를 입력해주세요 (예시: b00001) **");
		System.out.println("==============================================================================");
		System.out.println("버스ID\t\t차량번호\t\t버스등급");
		System.out.println("==============================================================================");
		// DB에 등록된 BUS 테이블 조회
		List<Map<String, Object>> buslist = dao.buslist();
		sec = 1;
		for (Map<String, Object> item : buslist) {
			System.out.print(sec + " :\t" + item.get("BUS_ID"));
			System.out.print("\t\t" + item.get("BUS_NUM"));
			System.out.print("\t\t" + item.get("BUS_GRADE"));
			System.out.println();
			sec++;
		}

		System.out.println("==============================================================================");
		while (true) {
			System.out.print("버스 선택 : ");
			int busInput = ScanUtil.nextInt();

			if (buslist.size() <= busInput || busInput < 0) {
				System.out.println("버스 입력오류!!\n리스트에 있는 버스를 선택해주시기 바랍니다.");
				System.out.println("===========================================================");

			} else {
				System.out.println(">>선택된 노선 : " + buslist.get(busInput - 1).get("BUS_NUM"));
				param.add(buslist.get(busInput - 1).get("BUS_ID"));
				break;
			}
		}

		// SV_ID 생성
		Map<String, Object> ID = dao.getsvId(idparam);
		String str = (String) (param.get(0));
		String svID = str.trim() + ID.get("ID");
		param.add(svID);

		System.out.println("********운행(SERVICE) 테이블에 등록된 데이터********");
		for (Object p : param) {
			System.out.println(p);
		}

		dao.insert(param);
		System.out.println("운행 노선 등록이 완료되었습니다.");

		return View.MANAGE_MENU;
	}

	public int deletesv() {
		// 기등록된 운행정보 호출하여 리스트 출력
		System.out.println("===========================================================");
		System.out.println("** 1. 삭제할 운행정보를 선택해주세요 (운행 ID를 입력해주세요) **");
		System.out.println("==============================================================================");
		System.out.println("운행ID\t\t\t운행일시\t\t\t\t노선ID\t\t버스ID");
		System.out.println("==============================================================================");
		List<Map<String, Object>> servicelist = dao.servicelist();

		if (servicelist == null) {
			System.out.println("등록된 운행 버스 목록이 없습니다.");
			System.out.println("===========================================================");
			return 1;

		} else {
			// 버스노선 리스트들이 출력됨
			for (Map<String, Object> item : servicelist) {
				System.out.print(item.get("SV_ID"));
				System.out.print("\t\t" + item.get("SV_DATE"));
				System.out.print("\t\t" + item.get("RT_ID"));
				System.out.print("\t\t" + item.get("BUS_ID"));
				System.out.println();

			}

			// 운행ID 입력시 삭제
			List<Object> param = new ArrayList<>();

			System.out.println("==============================================================================");

			int svIdInput;

			while (true) {
				System.out.print("운행ID 입력: ");
				svIdInput = ScanUtil.nextInt();

				if (servicelist.size() <= svIdInput || svIdInput < 0) {
					System.out.println("운행ID 입력오류!!\n리스트에 있는 운행ID를 확인해주시기 바랍니다.");
					System.out.println("===========================================================");
				} else {
					System.out.println(">>삭제할 운행ID : " + svIdInput);
					param.add(svIdInput);
					break;

				}
			}

			System.out.println("운행 노선 삭제가 완료되었습니다.");
			dao.delete(param);

			return View.MANAGE_MENU;
		}

	}

	public int stationMenu() {
		System.out.println("==============================================================================");
		System.out.println("메뉴를 선택해주세요.");
		System.out.println("1. 정류장 추가 | 2. 정류장 삭제 | 0.홈");
		System.out.println("==============================================================================");
		System.out.print(">>메뉴 번호 입력 : ");
		int StationMenu = ScanUtil.nextInt();

		switch (StationMenu) {
		case 1:
			return View.MANAGE_STATION_INSERT;
		case 2:
			return View.MANAGE_STATION_DELETE;
		default:
			return View.HOME;
		}
	}

	public int submitst() {
		System.out.println("===========================================================");
		System.out.println("** 1. 정류장 번호를 입력해주세요 (예시: s0001) **");
		System.out.println();
		System.out.println("==============================================================================");

		List<Object> param = new ArrayList<>();

		String stIdInput = "";
		while (true) {
			System.out.print("정류장 번호 입력 : ");
			stIdInput = ScanUtil.nextLine().trim();

			if (stIdInput.length() != 5) {
				System.out.println(" 형식에 맞춰 다시 입력하세요. (예시:s0001)");
			} else {
				System.out.println(">>입력된 정류장ID : " + stIdInput);
				param.add(stIdInput);
				break;
			}
		}
		System.out.println("==============================================================================");
		System.out.println();
		System.out.println("** 2. 정류장 이름을 입력해주세요 (예시:대전) **");
		String stNameInput = "";
		while (true) {
			System.out.print("정류장 이름 입력 : ");
			stNameInput = ScanUtil.nextLine().trim();

			if (stNameInput.length() != 2) {
				System.out.println(" 형식에 맞춰 다시 입력하세요. (예시: 대전)");
			} else {
				System.out.println(">>입력된 정류장 이름 : " + stNameInput);
				param.add(stNameInput);
				break;
			}
		}

		System.out.println("==============================================================================");
		System.out.println();
		System.out.println("** 3. 정류장 주소를 입력해주세요 (예시:대전 중구 용전동) **");
		String stLocInput = "";
		while (true) {
			System.out.print("정류장 이름 입력 : ");
			stLocInput = ScanUtil.nextLine().trim();

			if (stLocInput==null) {
				System.out.println("형식에 맞춰 다시 입력하세요. (예시: 대전 중구 용전동)");
			} else {
				System.out.println(">>입력된 정류장 주소 : " + stLocInput);
				param.add(stLocInput);
				break;
			}
		}

		System.out.println("********정류장(STATION) 테이블에 등록된 데이터********");
		for (Object p : param) {
			System.out.println(p);
		}

		dao.stInsert(param);
		System.out.println("정류장 등록이 완료되었습니다.");

		return View.MANAGE_MENU;
	}

	

	public int deletest() {
		// 기등록된 정류장 정보 호출하여 리스트 출력
		System.out.println("===========================================================");
		System.out.println("** 1. 삭제할 정류장 정보를 선택해주세요 (정류장 ID를 입력해주세요) **");
		System.out.println("==============================================================================");
		System.out.println("정류장ID\t\t\t정류장이름\t\t\t\t정류장주소");
		System.out.println("==============================================================================");
		List<Map<String, Object>> stationlist = dao.stationlist();

		if (stationlist == null) {
			System.out.println("등록된  정류장 목록이 없습니다.");
			System.out.println("===========================================================");
			return 1;

		} else {
			// 정류장 리스트들이 출력됨
			for (Map<String, Object> item : stationlist) {
				System.out.print(item.get("ST_ID"));
				System.out.print("\t\t" + item.get("ST_NAME"));
				System.out.print("\t\t" + item.get("ST_LOC"));
				System.out.println();

			}

			// 정류장ID 입력시 삭제
			List<Object> param = new ArrayList<>();

			System.out.println("==============================================================================");

			String stIdInput;

			while (true) {
				System.out.print("정류장ID 입력: ");
				stIdInput = ScanUtil.nextLine();

				if (stationlist.equals(stIdInput)) {
					System.out.println("정류장ID 입력오류!!\n리스트에 있는 정류장ID를 확인해주시기 바랍니다.");
					System.out.println("===========================================================");
				} else {
					System.out.println(">>삭제할 정류장ID : " + stIdInput);
					param.add(stIdInput);
					break;

				}
			}

			System.out.println("정류장 삭제가 완료되었습니다.");
			dao.stDelete(param);

			return View.MANAGE_MENU;
		}

	}

}
