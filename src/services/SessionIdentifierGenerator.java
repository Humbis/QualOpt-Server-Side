package services;

import java.security.SecureRandom;
import java.math.BigInteger;
import com.google.gson.JsonObject;

/**
 * This is used to generate login session tokens.
 * @author Kaimin Li
 *
 */
public final class SessionIdentifierGenerator {
	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		String token = new BigInteger(130, random).toString(32);
		return token;
	}

	//Helper method used to convert the token to JSON format
	public String tokenToJSON(String token){
		JsonObject j = new JsonObject();
		j.addProperty("token", token);
		return j.toString();
	}
}