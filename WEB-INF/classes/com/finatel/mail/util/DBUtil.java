/**
 * 
 */
package com.finatel.mail.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * @author ftuser
 *
 */
public class DBUtil {

	private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());

	private Connection connction = null;

	private String dbFilePath = "fincontactdb";

	private String contextPath = null;

	private HttpServletRequest request = null;

	/**
	 * 
	 */
	public DBUtil(String contextPath) {
		this.contextPath = contextPath;
	}

	public DBUtil(HttpServletRequest req) {
		this.request = req;
	}

	public Connection getConnection() throws Exception {
		Connection conn = null;
		conn = getConnection(request.getServletContext().getRealPath("/"));
		if (conn == null) {
			throw new Exception("DB not connected");
		}
		return conn;
	}

	public Connection getConnection(HttpServletRequest req) {
		return getConnection(req.getServletContext().getRealPath("/"));
	}

	public Connection getConnection(String appRealPath) {
		Connection conn = null;
		try {

			// realPath >>>> E:\java\IDE\workspace1\JavaMail\app\webcontent\web\
			// WEB-INF\finmaildb.sqlite
			String pathSep = File.separator;
			StringBuilder sb = new StringBuilder();
			if (appRealPath.lastIndexOf(pathSep) > -1) {
				appRealPath = appRealPath.substring(0, appRealPath.length() - 1);
			}
			sb.append(appRealPath).append(pathSep).append("WEB-INF").append(pathSep).append("finmaildb.sqlite");

			String dbPath = sb.toString();

			info("appRealPath >>>> " + appRealPath);
			info("dbPath >>>> " + dbPath);
			Class.forName("org.sqlite.JDBC");
			// String url =
			// "jdbc:sqlite:E:/java/IDE/workspace1/JavaMail/app/webcontent/web/WEB-INF/finmaildb.sqlite";

			String url = "jdbc:sqlite:" + dbPath;

			conn = DriverManager.getConnection(url);

			info("Connection to SQLite has been established.");

		} catch (Exception e) {
			fatal(e.toString());
		}
		return conn;
	}

	/**
	 * @param query
	 * @param values
	 * @return
	 */
	public int setValues(String query, String[] values) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		//
		if (query == null || query.length() <= 0) {
			return -1;
		}
		int rowCnt = 0;
		int len = 0;

		try {

			conn = getConnection();

			pstmt = conn.prepareStatement(query);
			if (values != null) {
				len = values.length;
				for (int i = 1; i <= len; i++) {
					pstmt.setString(i, values[i - 1]);
				}
			}
			rowCnt = pstmt.executeUpdate();
		} catch (Exception e) {
			fatal("Exception at setValues(1) >>>> " + e.toString());
		} finally {
			close(pstmt);
			close(conn);
		}

		return rowCnt;
	}

	/**
	 * @param queryKey
	 * @param values
	 * @param checkLabel
	 * @return
	 */
	public synchronized ArrayList<Hashtable<String, String>> getValues(String queryKey, String[] values,
			boolean checkLabel) {
		ArrayList<Hashtable<String, String>> recordList = null;
		PreparedStatement pstmt = null;
		ResultSet rslt = null;
		Connection conn = null;
		Hashtable<String, String> hashtable = null;
		ResultSetMetaData rsmd = null;
		int rowCnt = 0;
		int len = 0;
		try {
			recordList = new ArrayList<Hashtable<String, String>>();
			// logger.info("QUERY " + queryKey);

			conn = getConnection();
			pstmt = conn.prepareStatement(queryKey);

			if (values != null) {
				len = values.length;
				for (int i = 1; i <= len; i++) {
					pstmt.setString(i, values[i - 1]);
				}
			}

			rslt = pstmt.executeQuery();
			//
			rsmd = rslt.getMetaData();
			rowCnt = rsmd.getColumnCount();
			while (rslt.next()) {
				hashtable = new Hashtable<String, String>();
				for (int i = 1; i < rowCnt + 1; i++) {
					String value = rslt.getString(i);
					if (value != null && value.trim().length() > 0) {
						// String columnName = rsmd.getColumnLabel(i);
						String columnName = rsmd.getColumnLabel(i);
						columnName = columnName == null ? "" : columnName.trim();
						// System.out.println("columnName: " + columnName);
						if (checkLabel && columnName.isEmpty()) {
							columnName = rsmd.getColumnName(i);
							columnName = columnName == null ? "" : columnName.trim();
						}
						// System.out.println("columnName: " + columnName);
						if (!columnName.isEmpty()) {
							hashtable.put(columnName, value);
						}
					}
				}
				recordList.add(hashtable);
			}
			// gsmJUtils.ps("VALUES TAKEN");
		} catch (Exception e) {
			fatal("Exception at getValues(Conn Obj and 2 Params) Exception " + e.toString());
		} finally {
			try {
				close(rslt);
				close(pstmt);
				close(conn);
			} catch (Exception e) {
				//
			}
		}

		return recordList;
	}

	public synchronized ArrayList<Hashtable<String, String>> getValues(String queryKey) {

		return getValues(queryKey, null);
	}

	public synchronized ArrayList<Hashtable<String, String>> getValues(String queryKey, String[] values) {
		ArrayList<Hashtable<String, String>> recordList = null;
		PreparedStatement pstmt = null;
		ResultSet rslt = null;
		Connection conn = null;
		Hashtable<String, String> hashtable = null;
		ResultSetMetaData rsmd = null;
		int rowCnt = 0;
		int len = 0;
		try {
			recordList = new ArrayList<Hashtable<String, String>>();
			// logger.info("QUERY " + queryKey);

			conn = getConnection();

			pstmt = conn.prepareStatement(queryKey);

			if (values != null) {
				len = values.length;
				for (int i = 1; i <= len; i++) {
					pstmt.setString(i, values[i - 1]);
				}
			}

			rslt = pstmt.executeQuery();
			//
			rsmd = rslt.getMetaData();
			rowCnt = rsmd.getColumnCount();
			while (rslt.next()) {
				hashtable = new Hashtable<String, String>();
				for (int i = 1; i < rowCnt + 1; i++) {
					String value = rslt.getString(i);
					if (value != null && value.trim().length() > 0) {
						// String columnName = rsmd.getColumnLabel(i);
						String columnName = rsmd.getColumnName(i);
						// System.out.println("columnName: " + columnName);
						hashtable.put(columnName, value);
					}
				}
				recordList.add(hashtable);
			}
			// gsmJUtils.ps("VALUES TAKEN");
		} catch (Exception e) {
			fatal("Exception at getValues(Conn Obj and 2 Params) Exception " + e.toString());
		} finally {
			try {
				close(rslt);
				close(pstmt);
				close(conn);
			} catch (Exception e) {
				//
			}
		}

		return recordList;
	}

	public synchronized String getValue(String queryKey, String[] values, String fieldName) {
		//
		ArrayList<Hashtable<String, String>> recordList = null;
		String rtnValue = "";
		Connection conn = null;
		try {
			conn = getConnection();
			recordList = getValues(queryKey, values);
			if (recordList != null && !recordList.isEmpty()) {
				Hashtable<String, String> hT = recordList.get(0);
				rtnValue = hT.get(fieldName);
			}
		} catch (Exception e) {
			fatal("Exception at getValues(3 Params) Exception " + e.toString());
		} finally {
			try {
				close(conn);
			} catch (Exception e) {
				//
			}
		}

		return rtnValue;
	}

	public boolean isExists(String qry, String[] values) {
		boolean exists = false;
		try {
			List<Hashtable<String, String>> list = getValues(qry, values);
			exists = (list != null && !list.isEmpty());
		} catch (Exception e) {
			fatal("Exception at isExists(1) >>>> " + e.toString());
		}
		return exists;
	}

	private void close(ResultSet rslt) {
		// logger.info("<<<< Closing ResultSet >>>>");
		try {
			if (rslt != null) {
				rslt.close();
			}
		} catch (Exception e) {
			//
		}
	}

	private void close(PreparedStatement pstmt) {
		// logger.info("<<<< Closing PreparedStatement >>>>");
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (Exception e) {
			//
		}
	}

	private void close(Connection conn) {
		// logger.info("<<<< Closing Connection >>>>");
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			//
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
