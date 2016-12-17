package com.gsd.pos.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class DBHandler {
	private DataSource dataSource;
	private static final DBHandler dbh = new DBHandler();
	private static final Logger logger = Logger.getLogger(DBHandler.class
			.getName());

	private DBHandler() {
	}

	public static DBHandler getInstance() {
		return dbh;
	}

	public Connection getConnection() throws SQLException {
		if (dataSource == null) {
			initConnection();
		}
		return dataSource.getConnection();
	}

	private void initConnection() throws  SQLException {
		try {
			Context initContext  = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			dataSource = (DataSource)envContext.lookup("jdbc/smsdb");
			logger.debug("Data Source Initiated");
		} catch (NamingException e) {
			e.printStackTrace();
			logger.warn("Could not create Data source!!");
			throw new SQLException(e);
		}
	}


}
