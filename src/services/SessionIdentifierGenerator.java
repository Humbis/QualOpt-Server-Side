package services;

import java.security.SecureRandom;
import java.math.BigInteger;
import com.google.gson.JsonObject;

public final class SessionIdentifierGenerator {
	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		String token = new BigInteger(130, random).toString(32);
		return token;
	}

	public String tokenToJSON(String token){
		JsonObject j = new JsonObject();
		j.addProperty("token", token);
		return j.toString();
	}
}