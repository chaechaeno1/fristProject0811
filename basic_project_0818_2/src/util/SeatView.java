package util;

public class SeatView {
	public void nm(boolean[] seat) {
		System.out.println("---------------");
		System.out.println("|      출입구 >>");
		System.out.println("---------------");
		for (int i = 1; i <= 40; i = i + 4) {
			System.out.printf("|%2d|%2d|  |%2d|%2d|\n", i, i + 1, i + 2, i + 3);
			System.out.println("|" + (seat[i] ? " O" : " X") + "|" + (seat[i + 1] ? " O" : " X") + "|  |"
					+ (seat[i + 2] ? " O" : " X") + "|" + (seat[i + 2] ? " O" : " X") + "|");
		}
		System.out.println("---------------");
		System.out.println("|41|42|43|44|45|");
		System.out.println("|" + (seat[41] ? " O" : " X") + "|" + (seat[42] ? " O" : " X") + "|"
				+ (seat[43] ? " O" : " X") + "|" + (seat[44] ? " O" : " X") + "|" + (seat[45] ? " O" : " X") + "|");
		System.out.println("---------------");
	}

	public void ud(boolean[] seat) {
		System.out.println("-------------");
		System.out.println("|    출입구 >>");
		for (int i = 1; i <= 24; i = i + 3) {
			System.out.println("-------------");
			System.out.printf("|%2d|%2d|  |%2d|\n", i, i + 1, i + 2);
			System.out.println("|" + (seat[i] ? " O" : " X") + "|" + (seat[i + 1] ? " O" : " X") + "|  |"
					+ (seat[i + 2] ? " O" : " X") + "|");
		}
		System.out.println("-------------");
		System.out.println("|25|26|27|28|");
		System.out.println("|" + (seat[25] ? " O" : " X") + "|" + (seat[26] ? " O" : " X") + "|"
				+ (seat[27] ? " O" : " X") + "|" + (seat[28] ? " O" : " X") + "|");
		System.out.println("-------------");
	}

	public void pr(boolean[] seat) {
		System.out.println("----------");
		System.out.println("| 출입구 >>");
		for (int i = 1; i <= 18; i = i + 2) {
			System.out.println("----------");
			System.out.printf("|%2d|  |%2d|\n", i, i + 1);
			System.out.println("|" + (seat[i] ? " O" : " X") + "|  |" + (seat[i + 1] ? " O" : " X") + "|");
		}
		System.out.println("----------");
		System.out.println("|19|20|21|");
		System.out.println(
				"|" + (seat[19] ? " O" : " X") + "|" + (seat[20] ? " O" : " X") + "|" + (seat[21] ? " O" : " X"));
		System.out.println("----------");
	}
}
