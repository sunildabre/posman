package com.gsd.pos.utils;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class Utils {
	
	public static String make4CharsLong(int in) {
		StringBuffer sb = new StringBuffer();
		String s = String.valueOf(in);
		if (s.length() > 4) {
			return "9999";
		}
		if (s.length() == 4) {
			return s;
		}
		for (int i = 0; i < (4 - s.length()); i++) {
			sb.append("0");
		}
		sb.append(s);
		System.out.println(" Length [" + sb.toString() + "]");
		return sb.toString();
	}
	
	
	public static void main(String args[]) {
		DateTime dt = new DateTime();
		DateTime dt1 = dt.minusHours(25);
		Period p = new Period(dt1, dt);
		System.out.println(String.format("months/days/hours %d %d %d", p.getMonths() , p.getDays() , p.getHours()));
	}


}
