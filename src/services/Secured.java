package services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

/**
 * Defines the Secured annotation
 * @author Kaimin Li
 *
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured { 
	
}
