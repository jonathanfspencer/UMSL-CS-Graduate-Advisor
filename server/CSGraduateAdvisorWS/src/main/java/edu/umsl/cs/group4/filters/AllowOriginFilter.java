package edu.umsl.cs.group4.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

public class AllowOriginFilter implements ContainerResponseFilter {

	/**
	 * Adds the access control allow origin response header
	 */
	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		responseContext.getHeaders().add("Access-Control-Allow-Origin","*");
		responseContext.getHeaders().add("Access-Control-Allow-Headers", "accept, content-type");
	}

}
