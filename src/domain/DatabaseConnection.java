package domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to connect to a MySQL database
 * @author Kaimin Li
 *
 */
public class DatabaseConnection {
	public Connection getConnection(boolean isQualOpt) throws Exception {

		Connection con = null;
		String url; 
		String user; 
		String password; 
		if(isQualOpt){
			url = "jdbc:mysql://localhost:3307/qualopt";
			user = "testuser";
			password = "admin";
		}else{
			url = "jdbc:mysql://localhost:3306/ghtorrent";
			user = "ght";
			password = null;
		}
		

		try {
			System.out.println("url: " + url + " user = " + user + " pass = " + password);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseConnection.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return con;
	}
}
