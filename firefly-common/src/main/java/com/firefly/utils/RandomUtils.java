package com.firefly.utils;

abstract public class RandomUtils {
	public static final String ALL_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static long random(long min, long max) {
		return Math.round(ThreadLocalRandom.current().nextDouble()
				* (max - min) + min);
	}

	public static int randomSegment(String conf) {
		String[] tops = StringUtils.split(conf, ":");
		int[] iTops = new int[tops.length];
		int total = 0;
		for (int i = 0; i < tops.length; i++) {
			iTops[i] = Integer.parseInt(tops[i].trim());
			total += iTops[i];
			iTops[i] = total;
		}
		int rand = (int)random(0, total - 1);
		for (int i = 0; i < iTops.length; i++) {
			if (rand < iTops[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 *            生成字符串的长度
	 * @return 指定长度的随机字符串
	 */
	public static String randomString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int index = (int)random(0, ALL_CHAR.length() - 1);
			sb.append(ALL_CHAR.charAt(index));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String conf = "1:1:32:200:16:30";
		System.out.println(randomSegment(conf));

		System.out.println(random(0, 5));
		System.out.println(randomString(16));
	}
}
