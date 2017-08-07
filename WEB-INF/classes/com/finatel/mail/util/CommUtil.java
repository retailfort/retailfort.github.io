/**
 * 
 */
package com.finatel.mail.util;

/**
 * @author ftuser
 *
 */
public class CommUtil {

	// private static final Logger LOGGER =
	// Logger.getLogger(CommUtil.class.getName());

	/**
	 * 
	 */
	public CommUtil() {
		// TODO Auto-generated constructor stub
	}

	public static Integer getStringToInt(String str) {
		try {
			return new Integer(Integer.parseInt(str));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
