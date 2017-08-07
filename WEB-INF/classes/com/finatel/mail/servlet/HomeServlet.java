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
@WebServlet("/Home")
public class HomeServlet extends HttpServlet implements AppConstant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5630472135809807936L;

	private static final Logger LOGGER = Logger.getLogger(HomeServlet.class.getName());

	/**
	 * 
	 */
	public HomeServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		info("HomeServlet.init()");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		info("HomeServlet.doGet()");
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

		String url = "/WEB-INF/pages/home.jsp";

		HttpSession session = req.getSession();
		
		info("Session MaxInactiveInterval >>>>> " + session.getMaxInactiveInterval());

		String userName = (String) session.getAttribute(USER_NAME);

		info("userName in HomeServlet >>>>> " + userName + " <<<>>>> Session id: " + session.getId());

		if (userName != null && !userName.isEmpty()) {
			info("Logged in");
			AppUtil.loadValues(req);
			url = "/WEB-INF/pages/home.jsp";
		} else {
			info("Not logged in");
			// session.removeAttribute("USER_NAME");
			url = "/WEB-INF/pages/login.jsp";
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
