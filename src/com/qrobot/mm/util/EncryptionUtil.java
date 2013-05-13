package com.qrobot.mm.util;

/**
 * 加解密处理
 * @author water
 *
 */
public class EncryptionUtil {

	private static String _num_base_ = "0123456789";

	/**
	 * 数据加密
	 * 
	 * @param num
	 *            需要加密的数字串
	 * @return 字符串
	 */
	public static String numEncode(String num) {
		String temp = num.replaceAll(" ", "");
		if (temp.isEmpty())
			return "";
		String strEn = "";
		char row[] = temp.toCharArray();
		for (int i = 0; i < row.length; i++) {
			int ge = 0;
			if (_num_base_.indexOf(row[i]) > -1) {
				ge = Integer.parseInt(String.valueOf(row[i]));
				if (ge >= 6) {
					strEn += String.valueOf(ge - 6);
				} else {
					strEn += String.valueOf(ge + 4);
				}
			} else
				break;
		}
		return strEn;
	}

	/**
	 * 数据解密
	 * 
	 * @param num
	 *            需要解密的数字串
	 * @return 字符串
	 */
	public static String numDecode(String num) {
		String temp = num.replaceAll(" ", "");
		if (temp.isEmpty())
			return "";
		String strDe = "";
		char row[] = temp.toCharArray();
		for (int i = 0; i < row.length; i++) {
			int ge = 0;
			if (_num_base_.indexOf(row[i]) > -1) {
				ge = Integer.parseInt(String.valueOf(row[i]));
				if (ge >= 4) {
					strDe += String.valueOf(ge - 4);
				} else {
					strDe += String.valueOf(ge + 6);
				}
			} else
				break;
		}
		return strDe;
	}

}
