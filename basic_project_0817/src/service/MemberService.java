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
		System.out.println("-- 회원가입 --");
		System.out.print("아이디 >> ");
		String id = ScanUtil.nextLine();
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
			List<Object> param = new ArrayList<>();
			param.add(id);
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
