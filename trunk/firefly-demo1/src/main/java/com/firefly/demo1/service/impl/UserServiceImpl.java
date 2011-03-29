package com.firefly.demo1.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.firefly.annotation.Component;
import com.firefly.annotation.Inject;
import com.firefly.demo1.model.User;
import com.firefly.demo1.service.UserService;

@Component
public class UserServiceImpl implements UserService {

	@Inject
	private DataSource dataSource;

	@Override
	public List<User> getUsers() {
		Connection connection = null;
		List<User> ret = new ArrayList<User>();
		try {
			connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection
					.prepareStatement("select id, name, password from cms_user");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				User user = new User();
				user.setId(resultSet.getString("id"));
				user.setName(resultSet.getString("name"));
				user.setPassword(resultSet.getString("password"));
				ret.add(user);
			}
			connection.commit();

		} catch (SQLException e) {
			if (connection != null)
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

}
