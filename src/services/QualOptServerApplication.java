package services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/services")
public class QualOptServerApplication extends Application{
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	private Set<Object> singletons = new HashSet<Object>();

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}
