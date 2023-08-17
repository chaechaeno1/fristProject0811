package util;

public class TicketPriceCalc {
	/*	일반 1배수, 우등 1.5배 프리미엄 2배
	 * 	성인 1배수, 어린이 0.7배수
	 * 	
	 */
	
	public int price(int rt_price, String grade, String age_div) {
		double grade_multi = 1.;
		double age_div_multi = 1.;
		if (grade.equals("p"))
			grade_multi = 2.;
		else if (grade.equals("u"))
			grade_multi = 1.5;
		if(age_div.equals("어린이"))
			age_div_multi = 0.7;
		return  ((int) (rt_price * grade_multi * age_div_multi / 100)) * 100;
	}
}
