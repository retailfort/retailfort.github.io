/**
 * 
 */
package com.finatel.mail.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * @author ftuser
 *
 */
public class AppUtil {

	private static final Logger LOGGER = Logger.getLogger(AppUtil.class.getName());

	/**
	 * 
	 */
	public AppUtil() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isValidAdminUser(HttpServletRequest req, String username, String password) {
		boolean validAdminUser = false;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rslt = null;
		DBUtil dbUtil = null;
		try {
			dbUtil = new DBUtil(req);
			conn = dbUtil.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rslt = stmt.executeQuery("SELECT * FROM admin_user");
			if (rslt.next()) {
				String dbUn = rslt.getString("userName");
				dbUn = dbUn == null ? "" : dbUn.trim();
				String dbPwd = rslt.getString("password");
				dbPwd = dbPwd == null ? "" : dbPwd.trim();

				if (dbUn.equals(username) && dbPwd.equals(password)) {
					validAdminUser = true;
				}
			}
		} catch (Exception e) {
			fatal("Exception at isValidAdminUser(1) >>>> " + e.toString());
		} finally {
			try {
				close(rslt);
				close(stmt);
				close(conn);
			} catch (Exception e) {

			}
		}
		return validAdminUser;
	}

	/**
	 * @param req
	 */
	public static void loadValues(HttpServletRequest req) {

		try {
			DBUtil dbUtil = new DBUtil(req);
			ArrayList<Hashtable<String, String>> valueList = dbUtil
					.getValues("SELECT * FROM key_value ORDER BY orderCode");
			req.setAttribute("KEY_VALUE_LIST", valueList);
		} catch (Exception e) {
			fatal("Exception at loadValues(1) >>>>> " + e.toString());
		}
	}

	private static void close(ResultSet rslt) {
		try {
			if (rslt != null) {
				rslt.close();
			}
		} catch (Exception e) {

		}
	}

	private static void close(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {

		}
	}

	private static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {

		}
	}

	private static void info(String message) {
		LOGGER.info(message);
	}

	private static void fatal(String message) {
		LOGGER.fatal(message);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
