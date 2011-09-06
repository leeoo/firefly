package com.firefly.utils;

abstract public class RandomUtils {
	public static long random(long min, long max) {
		return Math.round(Math.random() * (max - min) + min);
	}

	public static int getMyRandom(String conf) {
		String[] tops = StringUtils.split(conf, ":");
		int[] iTops = new int[tops.length];
		int total = 0;
		for (int i = 0; i < tops.length; i++) {
			iTops[i] = Integer.parseInt(tops[i].trim());
			total += iTops[i];
			iTops[i] = total;
		}
		int rand = (int) random(0, total - 1);
		for (int i = 0; i < iTops.length; i++) {
			if (rand < iTops[i]) {
				return i;
			}
		}
		return -1;
	}

	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = (int) random(0, base.length() - 1);
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
//		int size = 5;
//		int i = (int) random(0, size - 1);
//		System.out.println("random num 1: " + i);
//		i++;
//		i = i < size ? i : (i & (size - 1));
//		System.out.println(i);

		String conf = "1:1:32:20:16:30";
		System.out.println(getMyRandom(conf));

		System.out.println(random(0,1));
		System.out.println(getRandomString(16));
	}
}
