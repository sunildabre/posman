package com.gsd.pos.manager;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.gsd.pos.jobs.JobManager;

public class StartUp implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(StartUp.class);
	private ServletContext context = null;

	public StartUp() {
	}

	public void contextInitialized(ServletContextEvent e) {
		logger.debug("Application starting!!");
		ServletContext ctx = e.getServletContext();
		String prefix = ctx.getRealPath("/");
		String filePath = "WEB-INF" + System.getProperty("file.separator") + "log4j.properties";
		try {
			PropertyConfigurator.configure(prefix + filePath);
		} catch (Exception e1) {
			logger.debug("Could not configure log4j , path was [" + filePath + "]");
			e1.printStackTrace();
		}
		System.out.println("Log4J Logging started for application: " + prefix + filePath);
		
		String certpath = System.getProperty("posman.home", "/Users/sunildabre/Documents/workspace/posman") + 
				File.separator + 	"posagent.ks" ;
		logger.debug("Cert path [" + certpath + "]");
		System.setProperty("javax.net.ssl.keyStore", certpath);
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
		System.setProperty("javax.net.ssl.trustStore", certpath);
		
		logger.debug("Application started!!");
//		JobManager.getInstance().start();
	}

	
	
	public void contextDestroyed(ServletContextEvent event) {
		logger.debug("Application stopped!!");
		JobManager.getInstance().stop();
		this.context = null;
	}
}
