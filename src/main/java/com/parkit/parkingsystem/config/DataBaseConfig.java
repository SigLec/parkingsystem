package com.parkit.parkingsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Class to make connection with database
 */

public class DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	/*
	 * Get connection with database with url, user and password informations
	 * 
	 * @return connection to the database
	 */

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/prod?serverTimezone=UTC", "root", "rootroot");
	}
	/*
	 * Close connection with database
	 * 
	 * @ param con instance of Connection
	 */

	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	/*
	 * Close preparedStatement
	 * 
	 * @param ps instance of PreparedStatement
	 */

	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}
	/*
	 * Close resultSet
	 * 
	 * @param rs instance of ResultSet
	 */

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}
}
