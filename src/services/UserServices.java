package services;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import domain.*;

@Path("/user")
public class UserServices {

	public static ArrayList<LoginSession> sessions = new ArrayList<LoginSession>();
	public static String currentUserEmail;
	private static Study currentStudy;

	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;
	
	@POST
	@Path("/newuser")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUsers(@FormParam("email") String email, @FormParam("password") String password,
			@FormParam("profession") String profession, @FormParam("institute") String institute,
			@FormParam("mailserver") String mailServer ,@Context HttpServletResponse servletResponse)
			throws Exception {

		User user = new User(email, password, profession, institute, mailServer);
		try {
			DatabaseConnection database = new DatabaseConnection();
			Connection con = database.getConnection();
			String sql = "INSERT INTO USER_PROFILES (email, password, profession, institute, mailserver) "
					+ "values (?, ?, ?, ?, ?)";
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, email);
			st.setString(2, password);
			st.setString(3, profession);
			st.setString(4, institute);
			st.setString(5, mailServer);
			st.execute();
			con.close();
		} catch (Exception e) {
			throw e;
		}
		return Response.created(URI.create("/Userservices/users/" + user.getEmail())).build();
	}

	@POST
	@Path("/login")
	@Produces("application/json")
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
					
					SessionIdentifierGenerator tokenGen = new SessionIdentifierGenerator();
					String token = tokenGen.nextSessionId();
					String tokenJSON = tokenGen.tokenToJSON(token);
					
					sessions.add(new LoginSession(token, email));
					
					System.out.println("There are this many sessions: " + sessions.size());
					return Response.status(Status.ACCEPTED).entity(tokenJSON).build();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return Response.status(Status.FORBIDDEN).build();
	}

	@Secured
	@POST
	@Path("/newstudy")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response newStudy(@FormParam("name") String name, @FormParam("description") String description,
			@FormParam("incentive") String incentive, @FormParam("hasPay") String hasPay) throws Exception {
		
		currentStudy = new Study();
		currentStudy.setOwnerEmail(currentUserEmail);
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
	
	@Secured
	@POST
	@Path("/currentstudy")
	@Consumes({"text/plain,text/html,application/json"})
	public void setCurrentStudy(String studyJSON){
		JsonParser parser = new JsonParser();
		JsonObject studyJ = parser.parse(studyJSON).getAsJsonObject();
		
		currentStudy = new Study();
		currentStudy.setName(studyJ.get("name").getAsString());
		currentStudy.setDescription(studyJ.get("description").getAsString());
		currentStudy.setIncentive(studyJ.get("incentive").getAsString());
		currentStudy.setPaid(studyJ.get("haspay").getAsBoolean());
		
	}
	
	@Secured
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
				study.setOwnerEmail(currentUserEmail);
				study.setPaid(rs.getBoolean("HASPAY"));
				
				allStudies.add(study);
			}

		} catch (Exception e) {
			throw e;
		}
		return allStudies.toString();
	}
	
	@Secured
	@POST
	@Path("/email")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response emailParticipants(@FormParam("senderemail") String sender , @FormParam("surveylink") String surveyLink ,@FormParam("subject") String subject, @FormParam("emailbody") String email) throws Exception{
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.setFrom(new InternetAddress(sender));
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("experimental1499@gmail.com")); //Using my email to test
		generateMailMessage.setSubject(subject);
		
		email = new StringBuilder().append(email).append("<br/><br/><br/>").append("Survey link: ").append(surveyLink)
				.append("<br/>").append(currentStudy.getDescription()).append("<br/>").append(currentStudy.getIncentive()).toString();
		
		generateMailMessage.setContent(email, "text/html");
		
		Transport transport = getMailSession.getTransport("smtp");
		
		transport.connect("smtp.gmail.com", "experimentalsender@gmail.com", "test1499");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
		
		return Response.status(Status.ACCEPTED).build();
	}
}