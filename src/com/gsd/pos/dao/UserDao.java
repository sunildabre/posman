package com.gsd.pos.dao;

import com.gsd.pos.model.User;

public interface UserDao {
	
	public User getUser(String username, String password);
}
