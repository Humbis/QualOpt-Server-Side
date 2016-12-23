package domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
	public Connection getConnection() throws Exception {

		Connection con = null;

		String url = "jdbc:mysql://localhost:3306/qualopt";
		String user = "testuser";
		String password = "admin";

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseConnection.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return con;
	}
}
