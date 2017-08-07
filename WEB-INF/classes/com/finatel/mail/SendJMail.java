/**
 * 
 */
package com.finatel.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * @author ftuser
 *
 */
public class SendJMail {

	private static final Logger LOGGER = Logger.getLogger(SendJMail.class.getName());

	/**
	 * 
	 */
	public SendJMail() {
		// TODO Auto-generated constructor stub
	}

	public static void sendTestMail() {
		String responseTxt = "SENT";
		String errorMessage = null;
		try {
			// final String username = "finatelemailtest@gmail.com";

			final String serverIp = "smtp.bizmail.yahoo.com";

			// final String serverIp = "smtp.mail.yahoo.com";
			final String serverPort = "587";
			final String username = "pat@finateltech.in";
			final String password = "FtP@tT!323#2121";
			final String aliasName = "PAT-FinaTel";

			info("serverIp >>>> " + serverIp);
			info("serverPort >>>> " + serverPort);

			// final String password = "a4daylight#";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", serverIp);
			props.put("mail.smtp.port", serverPort);

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			// "vijay@finateltech.com"
			String[] toAddress = { "g.mohanraj@finateltech.in" };
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, aliasName));
			InternetAddress[] toAddressObj = new InternetAddress[toAddress.length];
			int i = 0;
			for (String toAddr : toAddress) {
				toAddressObj[i] = new InternetAddress(toAddr.trim());
				i++;
			}

			message.setRecipients(Message.RecipientType.TO, toAddressObj);
			message.setSubject("Test");
			// message.setText(mailMessage);
			message.setContent("Test", "text/html");
			info("Sending mail >>>>");
			// Transport trans = session.getTransport("smtp");
			Transport.send(message);
			// session.getTransport("smtp").send(message);
			info("Mail sent >>>>");

			// System.out.println("Done");

		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Error";
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		}

		info("responseTxt >>>> " + responseTxt);

	}

	private static void info(String message) {
		LOGGER.info(message);
	}

	private static void fatal(String message) {
		LOGGER.fatal(message);
	}

	public static void sendMail() {
		// Recipient's email ID needs to be mentioned.
		String to = "g.mohanraj@finateltech.in";

		// Sender's email ID needs to be mentioned
		String from = "pat@oasys.co";
		from = "pat@finateltech.in";

		// Assuming you are sending email from localhost
		String host = "localhost";
		host = "mail.oasys.co";
		// hose = "FtP@tT!3232121";
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject("This is the Subject Line!");

			// Now set the actual message
			message.setText("This is actual message");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// sendMail();
		//sendTestMail();
	}

}
