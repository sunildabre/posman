package com.gsd.pos.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.gsd.pos.dao.UserDao;
import com.gsd.pos.model.User;

public class UserDaoImpl implements UserDao {

	private static final Logger logger = Logger.getLogger(UserDaoImpl.class
			.getName());

	@Override
	public User getUser(String username, String password) {
		PreparedStatement st = null;
		Connection con = null;
		User u = null;
		try {
			String sql = "select * from users where lower(username) = ? and password = ?  ";
			logger.trace("Executing sql " + sql);
			con = DBHandler.getInstance().getConnection();
			st = con.prepareStatement(sql);
			st.setString(1, username);
			st.setString(2, password);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				 u = create(rs);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warn(ex);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException se) {
			}
		}
		return u;
	}

	private User create(ResultSet rs) throws SQLException {
		User u = new User();
		u.setFirstName(rs.getString("first_name"));
		u.setUsername(rs.getString("username"));
		u.setLastName(rs.getString("last_name"));
		u.setUserId(rs.getLong("user_id"));
		return u;
	}
}
