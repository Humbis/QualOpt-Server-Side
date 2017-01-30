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
			url = "jdbc:mysql://localhost:3306/qualopt";
			user = "testuser";
			password = "admin";
		}else{
			url = "jdbc:mysql://127.0.0.1:3306/ghtorrent";
			user = "ght";
			password = "";
		}
		

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
