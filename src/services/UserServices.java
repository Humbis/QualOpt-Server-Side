package services;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import domain.DatabaseConnection;
import domain.Study;
import domain.User;

@Path("/user")
public class UserServices {

	private static User currentUser;
	private static boolean isLoggedIn = false;
	private static Study currentStudy;

	@POST
	@Path("/newuser")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUsers(@FormParam("email") String email, @FormParam("password") String password,
			@FormParam("profession") String profession, @FormParam("institute") String institute,
			@FormParam("description") String description, @Context HttpServletResponse servletResponse)
			throws Exception {

		User user = new User(email, password, profession, institute, description);
		try {
			DatabaseConnection database = new DatabaseConnection();
			Connection con = database.getConnection();
			String sql = "INSERT INTO USER_PROFILES (email, password, profession, institute, description) "
					+ "values (?, ?, ?, ?, ?)";
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, email);
			st.setString(2, password);
			st.setString(3, profession);
			st.setString(4, institute);
			st.setString(5, description);

			st.execute();
			con.close();
		} catch (Exception e) {
			throw e;
		}
		return Response.created(URI.create("/Userservices/users/" + user.getEmail())).build();
	}

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response login(@FormParam("email") String email, @FormParam("password") String password,
			@Context HttpServletResponse servletResponse) throws Exception {
		try {
			DatabaseConnection database = new DatabaseConnection();
			Connection con = database.getConnection();
			String sql = "SELECT * FROM USER_PROFILES";
			PreparedStatement st = con.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			Logger lgr = Logger.getLogger(DatabaseConnection.class.getName());
			lgr.log(Level.INFO, "email is: " + email + " pass is: " + password);

			while (rs.next()) {
				lgr.log(Level.INFO,
						" db email is: " + rs.getString("EMAIL") + " db pass is: " + rs.getString("PASSWORD"));

				if (rs.getString("EMAIL").equals(email) && rs.getString("PASSWORD").equals(password)) {
					lgr.log(Level.INFO, "Found a match!");
					currentUser = new User();
					currentUser.setEmail(email);
					currentUser.setPassword(password);

					isLoggedIn = true;
					return Response.status(Status.ACCEPTED).build();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return Response.status(Status.FORBIDDEN).build();
	}

	@POST
	@Path("/newstudy")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response newStudy(@FormParam("name") String name, @FormParam("description") String description,
			@FormParam("incentive") String incentive, @FormParam("hasPay") String hasPay) throws Exception {
		
		currentStudy = new Study();
		currentStudy.setOwnerEmail(currentUser.getEmail());
		currentStudy.setDescription(description);
		currentStudy.setIncentive(incentive);
		currentStudy.setName(name);
		
		int sqlHasPay;
		
		if(hasPay != null){
			currentStudy.setPaid(true);
			sqlHasPay = 1;
		}else{
			currentStudy.setPaid(false);
			sqlHasPay = 0;
		}
		
		try {
			DatabaseConnection database = new DatabaseConnection();
			Connection con = database.getConnection();
			String sql = "INSERT INTO STUDY SET U_ID =("
					+ "SELECT ID FROM USER_PROFILES WHERE EMAIL = '" + currentStudy.getOwnerEmail() + "'),"
					+ " NAME = '"+ name +"', "
					+ " DESCRIPTION = '"+ description +"',"
					+ " INCENTIVE = '"+ incentive +"',"
					+ " HASPAY = '"+ sqlHasPay +"';";
			PreparedStatement st = con.prepareStatement(sql);
			st.execute();

		} catch (Exception e) {
			throw e;
		}
		
		return null;
	}
	
	@GET
	@Path("/allstudies")
	@Produces("application/json")
	public String getAllStudies() throws Exception{
		List<Study> allStudies = new ArrayList<Study>();
		try {
			DatabaseConnection database = new DatabaseConnection();
			Connection con = database.getConnection();
			String sql = "SELECT * FROM STUDY";
			PreparedStatement st = con.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				Study study = new Study();
				study.setDescription(rs.getString("DESCRIPTION"));
				study.setName(rs.getString("NAME"));
				study.setIncentive(rs.getString("INCENTIVE"));
				study.setOwnerEmail(currentUser.getEmail());
				study.setPaid(rs.getBoolean("HASPAY"));
				
				allStudies.add(study);
			}

		} catch (Exception e) {
			throw e;
		}
		return allStudies.toString();
	}
}