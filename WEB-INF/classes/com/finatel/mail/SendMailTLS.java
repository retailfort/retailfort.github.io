/**
 * 
 */
package com.finatel.mail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.finatel.mail.exception.FinMailException;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * @author ftuser
 *
 */
public class SendMailTLS {

	private static final Logger LOGGER = Logger.getLogger(SendMailTLS.class.getName());

	/**
	 * 
	 */
	public SendMailTLS() {
		//
	}

	public static String sendMail(String teamName, final String[] toAddress, String[] ccAddress, final String subject,
			final String mailMessage,String fileContent,String fileExtension) {
		
		String responseTxt = "SENT";
		String errorMessage = null;
		
		try {
			
			if (toAddress == null || toAddress.length == 0)
				throw new FinMailException("To address is empty");

			final String serverIp = PropsUtil.getInstance().getProperty("server.ip");
			final String serverPort = PropsUtil.getInstance().getProperty("server.port");
			final String username = PropsUtil.getInstance().getProperty("sender.email");
			final String password = PropsUtil.getInstance().getProperty("sender.password");
			final String aliasName = PropsUtil.getInstance().getProperty("sender.alias");

			if (serverIp == null || serverPort == null)
				throw new FinMailException("Email settings not configured for teamName : " + teamName);

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

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, aliasName));
			InternetAddress[] toAddressObj = new InternetAddress[toAddress.length];
			int i = 0;
			for (String toAddr : toAddress) {
				toAddressObj[i] = new InternetAddress(toAddr.trim());
				i++;
			}

			if (ccAddress != null && ccAddress.length > 0) {

				InternetAddress[] ccAddressObj = new InternetAddress[ccAddress.length];
				i = 0;
				for (String ccAddr : ccAddress) {
					ccAddressObj[i] = new InternetAddress(ccAddr.trim());
					i++;
				}

				message.setRecipients(Message.RecipientType.CC, ccAddressObj);
			}

			message.setRecipients(Message.RecipientType.TO, toAddressObj);
			message.setSubject(subject);
			
			BodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setContent(mailMessage,"text/html");

	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);

	        System.out.println("fileExtension : "+fileExtension);
	        
	        if(fileContent != null && !fileContent.equals("") && fileExtension != null && !fileExtension.equals("")){
	        	
	        	messageBodyPart = new MimeBodyPart();
	        	
	        	InputStream is = new ByteArrayInputStream(Base64.decode(fileContent));
	        	
	        	final File tempFile = File.createTempFile("sample", "tmp");
	        	tempFile.deleteOnExit();
	        	
	        	FileOutputStream out = new FileOutputStream(tempFile);
	        	IOUtils.copy(is, out);
	        	
	        	DataSource source = new FileDataSource(tempFile);
	        	
	        	messageBodyPart.setDataHandler(new DataHandler(source));
	        	
	        	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy:HH:mm:ss");
	        	
	        	messageBodyPart.setFileName("finatel-career-attachment-"+sdf.format(new Date())+"."+fileExtension);
	        	multipart.addBodyPart(messageBodyPart);
	        }

	        message.setContent(multipart);
	         
			info("Sending mail >>>>");
			Transport.send(message);
			info("Mail sent >>>>");
		} catch (FinMailException e) {
			errorMessage = e.getMessage();
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Error";
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		}

		info("responseTxt >>>> " + responseTxt);

		return responseTxt;
	}

	public static void sendWelcomeMail(String teamName, final String toAddress, final String subject,
			final String mailMessage) {
		String responseTxt = "SENT";
		String errorMessage = null;
		try {
			// final String username = "finatelemailtest@gmail.com";

			if (toAddress == null || toAddress.isEmpty()) {
				throw new FinMailException("sendWelcomeMail - To address is empty");
			}

			final String serverIp = PropsUtil.getInstance().getProperty("server.ip");
			final String serverPort = PropsUtil.getInstance().getProperty("server.port");
			final String username = PropsUtil.getInstance().getProperty("sender.email");
			final String password = PropsUtil.getInstance().getProperty("sender.password");
			final String aliasName = PropsUtil.getInstance().getProperty("sender.alias");

			info("toAddress >>>> " + toAddress);
			info("serverIp >>>> " + serverIp);
			info("serverPort >>>> " + serverPort);

			if (serverIp == null || serverPort == null) {
				throw new FinMailException("Email settings not configured for teamName : " + teamName);
			}

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

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, aliasName));
			InternetAddress[] toAddressObj = new InternetAddress[] { new InternetAddress(toAddress.trim()) };

			message.setRecipients(Message.RecipientType.TO, toAddressObj);
			message.setSubject(subject);
			message.setContent(mailMessage, "text/html");
			info("Sending Welcome mail >>>>");
			Transport.send(message);
			info("Welcome Mail sent >>>>");

		} catch (FinMailException e) {
			errorMessage = e.getMessage();
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Error";
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		}

		info("Welcome Mail responseTxt >>>> " + responseTxt);

	}
	/**
	 * @param confgId
	 * @param toAddress
	 * @param ccAddress
	 * @param subject
	 * @param mailMessage
	 * @return
	 */
	public static String sendMail(int confgId, final String[] toAddress, String[] ccAddress, final String subject,
			final String mailMessage) {
		String responseTxt = "SENT";
		String errorMessage = null;
		try {
			// final String username = "finatelemailtest@gmail.com";

			if (toAddress == null || toAddress.length == 0) {
				throw new FinMailException("To address is empty");
			}

			final String serverIp = PropsUtil.getInstance().getProperty(getProperty(confgId, "server.ip"));
			final String serverPort = PropsUtil.getInstance().getProperty(getProperty(confgId, "server.port"));
			final String username = PropsUtil.getInstance().getProperty(getProperty(confgId, "sender.email"));
			final String password = PropsUtil.getInstance().getProperty(getProperty(confgId, "sender.password"));
			final String aliasName = PropsUtil.getInstance().getProperty(getProperty(confgId, "sender.alias"));

			info("serverIp >>>> " + serverIp);
			info("serverPort >>>> " + serverPort);

			if (serverIp == null || serverPort == null) {
				throw new FinMailException("Email settings not configured for configId : " + confgId);
			}

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

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, aliasName));
			InternetAddress[] toAddressObj = new InternetAddress[toAddress.length];
			int i = 0;
			for (String toAddr : toAddress) {
				toAddressObj[i] = new InternetAddress(toAddr.trim());
				i++;
			}

			if (ccAddress != null && ccAddress.length > 0) {

				InternetAddress[] ccAddressObj = new InternetAddress[ccAddress.length];
				i = 0;
				for (String ccAddr : ccAddress) {
					ccAddressObj[i] = new InternetAddress(ccAddr.trim());
					i++;
				}

				message.setRecipients(Message.RecipientType.CC, ccAddressObj);
			}

			message.setRecipients(Message.RecipientType.TO, toAddressObj);
			message.setSubject(subject);
			// message.setText(mailMessage);
			message.setContent(mailMessage, "text/html");
			info("Sending mail >>>>");
			Transport.send(message);
			info("Mail sent >>>>");

			// System.out.println("Done");

		} catch (FinMailException e) {
			errorMessage = e.getMessage();
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Error";
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		}

		info("responseTxt >>>> " + responseTxt);

		return responseTxt;
	}

	/**
	 * @param confgId
	 * @param toAddress
	 * @param subject
	 * @param mailMessage
	 */
	public static void sendWelcomeMail(int confgId, final String toAddress, final String subject,
			final String mailMessage) {
		String responseTxt = "SENT";
		String errorMessage = null;
		try {
			// final String username = "finatelemailtest@gmail.com";

			if (toAddress == null || toAddress.isEmpty()) {
				throw new FinMailException("sendWelcomeMail - To address is empty");
			}

			final String serverIp = PropsUtil.getInstance().getProperty(getProperty(confgId, "server.ip"));
			final String serverPort = PropsUtil.getInstance().getProperty(getProperty(confgId, "server.port"));
			final String username = PropsUtil.getInstance().getProperty(getProperty(confgId, "sender.email"));
			final String password = PropsUtil.getInstance().getProperty(getProperty(confgId, "sender.password"));
			final String aliasName = PropsUtil.getInstance().getProperty(getProperty(confgId, "sender.alias"));

			info("toAddress >>>> " + toAddress);
			info("serverIp >>>> " + serverIp);
			info("serverPort >>>> " + serverPort);

			if (serverIp == null || serverPort == null) {
				throw new FinMailException("Email settings not configured for configId : " + confgId);
			}

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

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, aliasName));
			InternetAddress[] toAddressObj = new InternetAddress[] { new InternetAddress(toAddress.trim()) };

			message.setRecipients(Message.RecipientType.TO, toAddressObj);
			message.setSubject(subject);
			message.setContent(mailMessage, "text/html");
			info("Sending Welcome mail >>>>");
			Transport.send(message);
			info("Welcome Mail sent >>>>");

		} catch (FinMailException e) {
			errorMessage = e.getMessage();
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Error";
			responseTxt = "FAILED," + errorMessage;
			fatal(responseTxt);
		}

		info("Welcome Mail responseTxt >>>> " + responseTxt);

	}

	private static String getProperty(int configId, String keyPart) {
		return new StringBuilder().append("email_").append(configId).append(".").append(keyPart).toString();
	}

	private static String getProperty(String teamName, String keyPart) {
		return new StringBuilder().append(teamName).append(".").append(keyPart).toString();
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
		/*
		 * 
		 * new String[] { "gmraj2005@gmail.com" }
		 */
		String[] ccAddress = new String[] { "gmraj2005@gmail.com" };

		String respMsg = sendMail(1, new String[] { "g.mohanraj@finateltech.in" }, ccAddress, "Test Mail",
				"Hi,\nWelcome Mail");
		System.out.println(respMsg);
	}

}
