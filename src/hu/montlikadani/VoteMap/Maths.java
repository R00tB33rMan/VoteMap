package hu.montlikadani.VoteMap;

import java.util.Random;

public class Maths {
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException var2) {
			return false;
		}
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt(max - min + 1) + min;
		return randomNum;
	}

	public static boolean isBetween(int a, int b, int c) {
		return b > a ? c > a && c < b : c > b && c < a;
	}
}