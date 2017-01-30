package services;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import domain.LoginSession;

/**
 * This filter is annotated with "Secured", which triggers whenever a method annotated with "Secured" is called.
 * This filter makes sure that the client has a token in the current session pool.
 * If the client fails to provide a valid token an unauthorized HTTP status is returned
 * @author Kaimin Li
 *
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();
        System.out.println("User sent the token: " + token);
        try {

            // Validate the token
            validateToken(token);

        } catch (Exception e) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private void validateToken(String token) throws Exception {
        // Check if it was issued by the server
        // Throw an Exception if the token is invalid
    	System.out.println("There are this many sessions: " + UserServices.sessions.size());
    	for (LoginSession l : UserServices.sessions){
    		System.out.println(l.token);
    		if(l.token.equals(token)){
    			System.out.println("Found a match for tokens");
    			UserServices.currentUserEmail = l.email;
    			return;
    		}
    	}
    	throw new NotAuthorizedException("Token did not match current session pool");
    }
}
