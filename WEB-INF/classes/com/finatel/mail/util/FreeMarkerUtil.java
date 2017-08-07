/**
 * 
 */
package com.finatel.mail.util;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.finatel.mail.PropsUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 * @author ftuser
 *
 */
public class FreeMarkerUtil {

	private static final Logger LOGGER = Logger.getLogger(FreeMarkerUtil.class.getName());

	/**
	 * 
	 */
	public FreeMarkerUtil() {
		// TODO Auto-generated constructor stub
	}

	public static String getContent(String templateName, Map<String, Object> values) {
		info("FreeMarkerUtil - getContent()");
		String content = null;
		try {
			Configuration cfg = new Configuration();
			// Where do we load the templates from:
			// cfg.setClassForTemplateLoading(FreeMarkerUtil.class,
			// "templates");
			
			
			String filePath = PropsUtil.getInstance().getProperty("ftl.template.path");
			
			cfg.setDirectoryForTemplateLoading(new File(filePath));

			// Some other recommended settings:
			cfg.setIncompatibleImprovements(new Version(2, 3, 20));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setLocale(Locale.US);
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			Template template = cfg.getTemplate(templateName);
			Writer output = new StringWriter();
			template.process(values, output);
			content = output.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void main(String... strings) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("name", "Mohanraj");
		values.put("email", "g.mohanraj@finateltech.in");
		values.put("message", "Test");

		String content = getContent("finatelWebSite.html", values);
		System.out.println(content);
	}

	private static void info(String message) {
		LOGGER.info(message);
	}

	private static void fatal(String message) {
		LOGGER.fatal(message);
	}
}
