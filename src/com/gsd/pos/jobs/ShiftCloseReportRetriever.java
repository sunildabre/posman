package com.gsd.pos.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;

import com.google.gson.Gson;
import com.gsd.pos.dao.SiteDao;
import com.gsd.pos.dao.impl.SitesDaoImpl;
import com.gsd.pos.model.ShiftReport;
import com.gsd.pos.model.Site;

public class ShiftCloseReportRetriever implements Runnable {
	private SiteDao dao;
	private static final Logger logger = Logger
			.getLogger(ShiftCloseReportRetriever.class.getName());
	static {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
	}

	public void run() {
		logger.debug("Starting report retrieval thread ...");
		retrieveAndStoreReportsForAll();
	}
	
	
	public void retrieveAndStoreReportsForAll() {
		logger.debug("Starting report retrieval ...");
		dao = new SitesDaoImpl();
		List<Site> sites = dao.getSites();
		logger.debug("Got [" + sites.size() + "] sites");
		for (Site s : sites) {
			retrieveAndStoreReports(s);
		}
	}

	public void retrieveAndStoreReports(Long siteId) {
		retrieveAndStoreReports(dao.getSite(siteId));
	}

	public void retrieveAndStoreReports(Site s) {
		DateTime now = new DateTime();
		try {
			logger.debug(String.format("Processing site [%s]  ", s.getName()));
			if ((s.getIp() == null) || (s.getIp().isEmpty())) {
				logger.warn(String.format(
						"Ip for site [%s] is not set,cannot fetch ",
						s.getName()));
				return;
			}
			logger.debug("Last Collected date is ["
					+ ((s.getLastCollectedDate() == null) ? null : s.getLastCollectedDate()) + "]");
			DateTime dt = (s.getLastCollectedDate() == null) ? now.minusDays(7)
					: new DateTime(s.getLastCollectedDate());
			long reportsToRetrieve = Days.daysBetween(dt.toDateMidnight() , now.toDateMidnight() ).getDays();
//			long reportsToRetrieve = new Duration(dt, now).getStandardDays();
			logger.debug(String.format("Collecting from [%s] , sending [%s] requests ", dt.toString(),
					reportsToRetrieve));
			int repeat = (int) reportsToRetrieve;
			for (int i = 0; i <= repeat; i++) {
				String d = dt.toString("yyyy.MM.dd");
				StringBuffer r = retrieveReport(s, d);
				Gson gson = new Gson();
				ShiftReport report = gson.fromJson(r.toString(),
						ShiftReport.class);
				report.setSiteId(s.getSiteId());
				if ((report.getStartTime() == null) || (report.getEndTime() == null)) {
					logger.debug("No shift information found !!");
					dt = dt.plusDays(1);
					continue;
				}
				if (new Duration(new DateTime(report.getStartTime()), new DateTime(report.getEndTime()))
						.getStandardDays() > 7) {
					logger.debug("Shift Not Closed!!");
					dt = dt.plusDays(1);
					continue;
				}
				boolean saved = dao.saveReport(report, false);
				logger.debug("Report "
						+ ((saved) ? " inserted" : " already existed!"));
				dt = dt.plusDays(1);
			}
		} catch (Exception e) {
			logger.warn(String.format("Error fetching for site [%s] ", s.getName()));
			logger.warn(e.getMessage());
			try {
				dao.updateSiteReason(s.getSiteId(), e.getMessage());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}



	private StringBuffer retrieveReport(Site site, String date)
			throws MalformedURLException, IOException, ProtocolException,
			Exception {
		URL url = new URL("https://" + site.getIp()
				+ "/reports?name=shift_close&date=" + date);
		logger.debug("Connecting to [" + url.toString() + "]");
		System.out.println("Connecting to [" + url.toString() + "]");

		HttpsURLConnection conn = (HttpsURLConnection) url
				.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		if (conn.getResponseCode() != 200) {
			throw new Exception("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		StringBuffer r = new StringBuffer();
		String res = null;
		while ((res = in.readLine()) != null) {
			r.append(res);
		}
		conn.disconnect();
		return r;
	}

	
	public static void main(String[] args) throws MalformedURLException, ProtocolException, IOException, Exception {
		String certpath = System.getProperty("posman.home", "/Users/sunildabre/Documents/workspace/posman") + 
				File.separator + 	"posagent.ks" ;
		logger.debug("Cert path [" + certpath + "]");
		System.setProperty("javax.net.ssl.keyStore", certpath);
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
		System.setProperty("javax.net.ssl.trustStore", certpath);

		Site site = new Site();
		//801 washingtons
		site.setIp("96.91.220.2");
		ShiftCloseReportRetriever retriever = new  ShiftCloseReportRetriever();
		StringBuffer s = retriever.retrieveReport(site, "2016.12.14");
		System.out.println(s.toString());
	}
}
