package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import controller.Controller;
import dao.MemberDAO;
import util.ScanUtil;
import util.View;

public class MemberService {
	private static MemberService instance = null;

	private MemberService() {
	}

	public static MemberService getInstance() {
		if (instance == null)
			instance = new MemberService();
		return instance;
	}

	MemberDAO dao = MemberDAO.getInstance();

	public int login() {
		if ((boolean) Controller.sessionStorage.get("login")) {
			System.out.println("이미 로그인되어있습니다.");
			return View.HOME;
		}
		System.out.println("==============================================================================");
		System.out.println("		로그인	");
		System.out.println("==============================================================================");
		System.out.print("아이디 >> ");
		String id = ScanUtil.nextLine();
		System.out.print("비밀번호 >> ");
		String pass = ScanUtil.nextLine();

		List<Object> param = new ArrayList<>();
		param.add(id);
		param.add(pass);
		
		Map<String, Object> row = dao.login(param);

		if (row == null) {
			System.out.println("아이디/비밀번호가 일치하지 않습니다.");
			return View.HOME;
		} else {
			Controller.sessionStorage.put("login", true);
			Controller.sessionStorage.put("loginInfo", row.get("CST_ID"));
			System.out.println(row.get("CST_ID") + "님 환영합니다!");
			return View.HOME;
		}
	}

	public int signUp() {
		List<Object> param = new ArrayList<>();
		
		System.out.println("-- 회원가입 --");
		System.out.println("되돌아가기 : 0");
		System.out.print("아이디 >> ");
		String id = ScanUtil.nextLine();
		
		if(id.equals("0"))
			return View.HOME;
		
		param.add(id);
		
		List<Map<String, Object>> checkList = dao.checkedID(param);
		
		if(checkList != null) {
			System.out.println("이미 있는 아이디 입니다.");
			return View.MEMBER_SIGNUP;
		}
		
		System.out.print("비밀번호 >> ");
		String pass = ScanUtil.nextLine();
		
		System.out.print("이름 >> ");
		String name = ScanUtil.nextLine();
		System.out.print("전화번호 >> ");
		String tel = ScanUtil.nextLine();
		System.out.print("나이 >> ");
		int age = ScanUtil.nextInt();

		System.out.println("ID : " + id + ", 이름 : " + name + ", 전화번호 : " + tel + ", 나이 : " + age);
		System.out.print("입력한 정보가 맞습니까? (y/n)");
		String yn = ScanUtil.nextLine();
		if (yn.equals("y")) {
			param.add(pass);
			param.add(name);
			param.add(tel);
			param.add(age);

			int result = dao.signUp(param);
			if(result != 0) {
				System.out.println(name + "님, 회원가입이 완료 되었습니다.");
			}
		}
		return View.HOME;
	}
}
