/**
 * 
 */
package com.finatel.mail.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.finatel.mail.util.DBUtil;

/**
 * @author ftuser
 *
 */
@WebServlet("/Config")
public class ConfigServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5342492837209069632L;

	/**
	 * 
	 */

	private static final Logger LOGGER = Logger.getLogger(ConfigServlet.class.getName());

	/**
	 * 
	 */
	public ConfigServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		info("ConfigServlet.init()");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		info("ConfigServlet.doGet()");
		doPost(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String cmd = req.getParameter("cmd");
		cmd = cmd == null ? "" : cmd.trim();
		String keyFld = req.getParameter("keyFld");
		keyFld = keyFld == null ? "" : keyFld.trim();
		String valueFld = req.getParameter("valueFld");
		valueFld = valueFld == null ? "" : valueFld.trim();

		info("cmd >>>>>> " + cmd);
		info("keyFld >>>>>> " + keyFld);
		info("valueFld >>>>>> " + valueFld);
		if ("updateKey".equals(cmd)) {
			updateKeyValue(req, resp, keyFld, valueFld);
		} else if ("addNewKey".equals(cmd)) {
			addNewKeyValue(req, resp);
		}else if ("addNewKeyConfig".equals(cmd)) {
			String newKeyName = req.getParameter("newKeyName");
			newKeyName = newKeyName == null ? "" : newKeyName.trim();
			loadFields(req, newKeyName);
			String url = "/WEB-INF/pages/newKeyNameConfig.jsp";
			req.getRequestDispatcher(url).forward(req, resp);
		}
		// loadFields(req);

	}

	/**
	 * @param req
	 * @param resp
	 */
	private void addNewKeyValue(HttpServletRequest req, HttpServletResponse resp) {
		String newKeyName = req.getParameter("newKeyName");
		newKeyName = newKeyName == null ? "" : newKeyName.trim();
		try {
			DBUtil dbUtil = new DBUtil(req);
			boolean exists = dbUtil.isExists("SELECT * FROM key_config WHERE keyName=?", new String[] { newKeyName });
			if (exists) {
				info("addNewKeyValue Key exists >>>>> " + newKeyName);
				printOutput(resp, "EXISTS");
			} else {
				info("addNewKeyValue Key doesnot exists >>>>> " + newKeyName);
				printOutput(resp, "NOTEXISTS");
			}
		} catch (Exception e) {
			fatal("Exception at addNewKeyValue(1) >>>>> " + e.toString());
		}

	}

	/**
	 * @param req
	 * @param resp
	 * @param keyFld
	 * @param valueFld
	 */
	private void updateKeyValue(HttpServletRequest req, HttpServletResponse resp, String keyFld, String valueFld) {
		info("Inside updateKeyValue()");
		String rslt = "NOT-UPDATED";
		try {
			DBUtil dbUtil = new DBUtil(req);
			String updSQL = "UPDATE key_value SET keyValue=? WHERE keyName=?";
			int rowCnt = dbUtil.setValues(updSQL, new String[] { valueFld, keyFld });
			rslt = "UPDATED";
			info("KeyValue Updated for " + keyFld + "  rowCnt >>>> " + rowCnt + "  valueFld " + valueFld);
		} catch (Exception e) {
			fatal("Exception at updateKeyValue(1) >>>> " + e.toString());
		}

		printOutput(resp, rslt);

	}

	/**
	 * @param resp
	 * @param msg
	 */
	private void printOutput(HttpServletResponse resp, String msg) {
		PrintWriter out = null;
		try {
			resp.setContentType("text/plain");
			out = resp.getWriter();
			out.print(msg);
			out.flush();
		} catch (Exception e) {
			fatal("Exception at printOutput(1) >>>> " + e.toString());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * @param req
	 * @param newKeyName
	 */
	private void loadFields(HttpServletRequest req, String newKeyName) {
		Map<Integer, String> keyValueList = new LinkedHashMap<Integer, String>();
		DBUtil dbUtil = new DBUtil(req);
		String maxValueStr = dbUtil.getValue("SELECT MAX(orderCode) AS orderCode FROM key_value", null, "orderCode");
		int maxValue = 1;
		if (maxValueStr != null || !maxValueStr.isEmpty()) {
			maxValue = Integer.valueOf(maxValueStr).intValue() + 1;
		}
		keyValueList.put(maxValue, newKeyName + ".sales.email.id");
		maxValue++;
		keyValueList.put(maxValue, newKeyName + ".server.ip");
		maxValue++;
		keyValueList.put(maxValue, newKeyName + ".server.port");
		maxValue++;
		keyValueList.put(maxValue, newKeyName + ".sender.email");
		maxValue++;
		keyValueList.put(maxValue, newKeyName + ".sender.alias");
		maxValue++;
		keyValueList.put(maxValue, newKeyName + ".sender.password");
		maxValue++;
		keyValueList.put(maxValue, newKeyName + ".template.name");
		maxValue++;
		keyValueList.put(maxValue, newKeyName + ".welcome.template.name");
		maxValue++;
		keyValueList.put(maxValue, newKeyName + ".field.names");
		maxValue++;
		
//		List<String> keysList = new ArrayList<String>();
//		keysList.add(newKeyName + ".server.ip");
//		keysList.add(newKeyName + ".server.port");
//		keysList.add(newKeyName + ".sender.email");
//		keysList.add(newKeyName + ".sender.alias");
//		keysList.add(newKeyName + ".sender.password");
//		keysList.add(newKeyName + ".template.name");
//		keysList.add(newKeyName + ".welcome.template.name");
//		keysList.add(newKeyName + ".field.names");

		req.setAttribute("KEYS_MAP", keyValueList);
	}

	private static void info(String message) {
		LOGGER.info(message);
	}

	private static void fatal(String message) {
		LOGGER.fatal(message);
	}

}
