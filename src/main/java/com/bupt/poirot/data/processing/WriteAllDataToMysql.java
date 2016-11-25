package com.bupt.poirot.data.processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.mysql.cj.api.jdbc.Statement;
import com.mysql.cj.jdbc.PreparedStatement;

public class WriteAllDataToMysql {
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	private void writeAllDataToMysql(List<SingleData> dataSortByTime) {
		// TODO Auto-generated method stub
		try {
			String dbConnectString = "jdbc:mysql://localhost/hotShows?user=root&password=Poirot373&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=false";
			Connection connection = DriverManager.getConnection(dbConnectString);

			Statement stmt = (Statement) connection.createStatement();

			String createTable = "CREATE TABLE IF NOT EXISTS showCount" + "(showName varchar(200) NOT NULL,"
					+ "id varchar(200) NOT NULL," + "score int NOT NULL," + "time TIMESTAMP NOT NULL,"
					+ "type varchar(30)," + "PRIMARY KEY(id, time)" + ") DEFAULT CHARSET=utf8";

			if (stmt.executeUpdate(createTable) == 0) {
				System.out.println("create table success!");
			}

			String query = "insert into showCount (name, time, x, y, status, speed, direction)"
					+ " values (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name) , time=VALUES(time)";
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			for (SingleData singleData : dataSortByTime) {

				ps.setString(1, singleData.carName);
				ps.setLong(2, singleData.time);
				ps.setFloat(3, singleData.x);
				ps.setFloat(4, singleData.y);
				ps.setBoolean(5, singleData.status);
				ps.setFloat(6, singleData.speed);
				ps.setInt(7, singleData.direction);
				ps.executeUpdate();
			}
			connection.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
}
