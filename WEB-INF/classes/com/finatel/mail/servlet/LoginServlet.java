/**
 * 
 */
package com.finatel.mail.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.finatel.mail.util.AppConstant;
import com.finatel.mail.util.AppUtil;

/**
 * @author ftuser
 *
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet implements AppConstant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 263994357327001195L;

	private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

	private static final String VERSION_NUMBER = "1.0.0.4";

	/**
	 * 
	 */
	public LoginServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		info("LoginServlet.init()");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		info("LoginServlet.doGet()");
		doPost(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	// http://localhost:7070/finmail/SendMail?name=Raj&email=gmraj2005@gmail.com&subject=Test&message=Test
	// Message&confgId=1&templateName=tabserveWebSite.html
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		info("LoginServlet.doPost() - Version " + VERSION_NUMBER);

		String url = "/WEB-INF/pages/login.jsp";

		HttpSession session = null;

		try {
			session = req.getSession();

			String cmd = req.getParameter("cmd");

			info("cmd >>>>> " + cmd);

			if ("PROCESS-LOGIN".equals(cmd)) {
				String username = req.getParameter("username");
				username = username == null ? "" : username.trim();
				String password = req.getParameter("password");
				password = password == null ? "" : password.trim();

				info("username >>>>> " + username);
				// info("password >>>>> " + password);

				boolean isValidAdminUser = AppUtil.isValidAdminUser(req, username, password);

				info("isValidAdminUser >>>>> " + isValidAdminUser);

				if (isValidAdminUser) {
					info("Session value has been set. Session id: " + session.getId());
					session.setAttribute(USER_NAME, username);
					// setting session to expiry in 30 mins
					session.setMaxInactiveInterval(30 * 60);
					AppUtil.loadValues(req);
					url = "/WEB-INF/pages/home.jsp";
				} else {
					info("Session value has NOT been set");
					// session.removeAttribute("USER_NAME");
					req.setAttribute("error", "Invalid username/password");
				}

			} else if ("PROCESS-LOGOUT".equals(cmd)) {
				session.invalidate();				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		req.getRequestDispatcher(url).forward(req, resp);
	}

	private static void info(String message) {
		LOGGER.info(message);
	}

	private static void fatal(String message) {
		LOGGER.fatal(message);
	}

}
