/**
 * 
 */
package com.finatel.mail.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.finatel.mail.PropsUtil;
import com.finatel.mail.SendMailTLS;
import com.finatel.mail.exception.FinMailException;
import com.finatel.mail.util.CommUtil;
import com.finatel.mail.util.FreeMarkerUtil;

/**
 * @author ftuser
 *
 */
@WebServlet("/SendMail")
public class SendMailServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(SendMailServlet.class.getName());

	private static final long serialVersionUID = 2522607633859531704L;

	private static final String VERSION_NUMBER = "1.0.0.4";

	public SendMailServlet() {}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		info("SendMailServlet.init()");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		info("SendMailServlet.doGet()");
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
		sendMailNew(req, resp);
	}

	protected void sendMailNew(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		info("SendMailServlet.sendMailNew() - Version " + VERSION_NUMBER);

		String respMsg = null;

		PrintWriter out = null;

		String[] ccAddress = null;

		try {
			resp.setContentType("text/plain");
			out = resp.getWriter();

			String emailType = req.getParameter("emailtype");

			final String property = buildPropertyKey(emailType, "email.id");
			final String toEmail = PropsUtil.getInstance().getProperty(property);

			if (toEmail == null || toEmail.isEmpty())
				throw new FinMailException("To address is empty");

			String fieldNamesProperty = buildPropertyKey(emailType, "field.names");
			String fieldNames = PropsUtil.getInstance().getProperty(fieldNamesProperty);

			StringTokenizer fieldSet = new StringTokenizer(fieldNames, ",", false);

			String fileExtension = "", fileContent="";
			
			Map<String, Object> values = new HashMap<String, Object>();
			
			while (fieldSet.hasMoreTokens()) {
				
				String fieldName = fieldSet.nextToken();
				String fieldValue = req.getParameter(fieldName);
				
				if (!fieldName.isEmpty()) {
					
					values.put(fieldName, fieldValue);
					
					if(fieldName.equals("fileExtension"))
						fileExtension = fieldValue;
					else if(fieldName.equals("fileContent"))
						fileContent = fieldValue;
				}
			}

			String templateNameProperty = buildPropertyKey(emailType, "template.name");
			String templateName = PropsUtil.getInstance().getProperty(templateNameProperty);
			
			String welcomeTemplateNameProperty = buildPropertyKey(emailType, "welcome.template.name");
			String welcomeTemplateName = PropsUtil.getInstance().getProperty(welcomeTemplateNameProperty);

			String mailMessage = FreeMarkerUtil.getContent(templateName, values);

			if (mailMessage == null || mailMessage.isEmpty())
				throw new FinMailException("Email template is empty");
			
			String[] toAddress = null;

			StringTokenizer toEmailSet = new StringTokenizer(toEmail, ",", false);

			int totalTokens = toEmailSet.countTokens();

			toAddress = new String[totalTokens];

			int i = 0;
			while (toEmailSet.hasMoreTokens()) {
				toAddress[i] = toEmailSet.nextToken();
				i++;
			}

			String subject = req.getParameter("subject") == null ? "" :req.getParameter("subject");
			String email = req.getParameter("email");

			respMsg = SendMailTLS.sendMail(email, toAddress, ccAddress, subject, mailMessage,fileContent,fileExtension);

			if (welcomeTemplateName != null && !welcomeTemplateName.isEmpty()) {
				String welcomeMessage = FreeMarkerUtil.getContent(welcomeTemplateName, values);
				if (welcomeMessage != null && welcomeMessage.length() > 0)
					SendMailTLS.sendWelcomeMail(email, email, subject, welcomeMessage);
				else
					info("welcomeMessage is empty");
			}
		} catch (FinMailException e) {
			respMsg = "FAILED, " + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			respMsg = "FAILED, Unable to send mail. Please try later.";
		}

		try {
			out.print(respMsg);
		} catch (Exception e) {
		} finally {
			if (out != null)
				out.close();
		}

	}

	protected void sendMail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String respMsg = null;

		PrintWriter out = null;

		String[] ccAddress = null;

		try {
			resp.setContentType("text/plain");
			out = resp.getWriter();

			// DBUtil dbUtil = new DBUtil(req.getContextPath());
			// dbUtil.getConnection(req);

			String confgId = req.getParameter("confgId");
			confgId = confgId == null ? "1" : confgId.trim();

			String salesTeam = req.getParameter("salesteam");
			salesTeam = salesTeam == null ? "finatel" : salesTeam.trim();

			Integer configIdIntObj = CommUtil.getStringToInt(confgId);
			configIdIntObj = configIdIntObj == null ? 1 : configIdIntObj;

			int configIdInt = configIdIntObj.intValue();

			final String property = buildPropertyKey(salesTeam, "sales.email.id");
			info("property >>>>> " + property);

			final String toEmail = PropsUtil.getInstance().getProperty(property);

			info("toEmail 1 >>>>> " + toEmail);

			// String toEmail =
			// PropsUtil.getInstance().getProperty("sales.email.id");
			// toEmail = toEmail == null ? "" : toEmail.trim();
			// info("toEmail >>>>> " + toEmail);

			if (toEmail.isEmpty()) {
				throw new FinMailException("To address is empty");
			}

			String defaultTemplateName = PropsUtil.getInstance().getProperty("default.template.name");
			String defaultWelcomeTemplateName = PropsUtil.getInstance().getProperty("welcome.template.name");
			String appHostURL = PropsUtil.getInstance().getProperty("app.host.url");

			String name = req.getParameter("name");
			name = name == null ? "" : name.trim();

			String email = req.getParameter("email");
			email = email == null ? "" : email.trim();

			String mobile = req.getParameter("mobile");
			mobile = mobile == null ? "" : mobile.trim();

			String country = req.getParameter("country");
			country = country == null ? "" : country.trim();

			String state = req.getParameter("state");
			state = state == null ? "" : state.trim();

			String subject = req.getParameter("subject");
			subject = subject == null ? "" : subject.trim();

			String message = req.getParameter("message");
			message = message == null ? "" : message.trim();

			String copy = req.getParameter("copy");
			copy = copy == null ? "" : copy.trim();

			String templateName = req.getParameter("templateName");
			templateName = templateName == null ? defaultTemplateName : templateName.trim();

			String welcomeTemplateName = req.getParameter("welcomeTemplateName");
			welcomeTemplateName = welcomeTemplateName == null ? defaultWelcomeTemplateName : welcomeTemplateName.trim();

			info("---------------------- Given Values -----------------------------");
			info("SalesTeam >>>>> " + salesTeam);
			info("Name >>>>> " + name);
			info("Email >>>>> " + email);
			info("To Email >>>>> " + toEmail);
			info("Subject >>>>> " + subject);
			info("Message >>>>> " + message);
			info("Copy >>>>> " + copy);
			info("TemplateName >>>>> " + templateName);
			info("Welcome TemplateName >>>>> " + welcomeTemplateName);
			info("ConfgId >>>>> " + confgId);
			info("ConfigIdInt >>>>> " + configIdInt);
			info("appHostURL >>>>> " + appHostURL);
			info("-----------------------------------------------------------------");

			Map<String, Object> values = new HashMap<String, Object>();
			values.put("name", name);
			values.put("email", email);
			values.put("mobile", mobile);
			values.put("message", message);
			values.put("appHostURL", appHostURL);
			String mailMessage = FreeMarkerUtil.getContent(templateName, values);
			info("mailMessage >>>>> \n " + mailMessage);

			if (mailMessage == null || mailMessage.isEmpty()) {
				throw new FinMailException("Email template is empty");
			}
			String[] toAddress = null;

			// toAddress = new String[] { toEmail };

			StringTokenizer toEMailSt = new StringTokenizer(toEmail, ",", false);

			int totalTokens = toEMailSt.countTokens();

			info("totalTokens >>>>>  " + totalTokens);

			toAddress = new String[totalTokens];

			int i = 0;
			while (toEMailSt.hasMoreTokens()) {
				toAddress[i] = toEMailSt.nextToken();
				i++;
			}

			boolean mailCopiedToSender = false;

			if (!email.isEmpty() && "on".equals(copy)) {
				info("email to be copied >>>>> \n " + email);
				ccAddress = new String[] { email };
				mailCopiedToSender = true;
			}

			respMsg = SendMailTLS.sendMail(configIdInt, toAddress, ccAddress, subject, mailMessage);

			if (!mailCopiedToSender && !email.isEmpty() && !welcomeTemplateName.isEmpty()) {
				String welcomeMessage = FreeMarkerUtil.getContent(welcomeTemplateName, values);
				if (welcomeMessage != null && welcomeMessage.length() > 0) {
					SendMailTLS.sendWelcomeMail(configIdInt, email, subject, welcomeMessage);
				} else {
					info("welcomeMessage is empty");
				}
			} else {
				info("----------------------Else--------------------------");
				info("mailCopiedToSender - " + mailCopiedToSender);
				info("email - " + email);
				info("welcomeTemplateName - " + welcomeTemplateName);
				info("----------------------------------------------------");
			}

		} catch (FinMailException e) {
			respMsg = "FAILED, " + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			respMsg = "FAILED, Unable to send mail. Please try later.";
		}

		try {
			out.print(respMsg);
		} catch (Exception e) {

		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	private static String buildPropertyKey(String prefix, String keyPart) {
		String property = new StringBuilder().append(prefix).append(".").append(keyPart).toString();
		info("property >>>>> " + property);
		return property;
	}

	private static void info(String message) {
		LOGGER.info(message);
	}

	private static void fatal(String message) {
		LOGGER.fatal(message);
	}

}
