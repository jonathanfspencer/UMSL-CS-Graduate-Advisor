package edu.umsl.cs.group4.server;

import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {
	private static final String WEBAPPDIR = "/src/main/webapp/";
	public static void main(String[] args) throws Exception {
		Server server = new Server(8081);
		
		final URL warUrl = JettyServer.class.getClassLoader().getResource(WEBAPPDIR);
		final String warUrlString = warUrl.toExternalForm();
		
		org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
 
		
		WebAppContext ctx = new WebAppContext();
		ctx.setDescriptor(warUrlString + "/WEB-INF/web.xml");
		ctx.setResourceBase(warUrlString);
		ctx.setContextPath("/");
		ctx.setParentLoaderPriority(true);
		server.setHandler(ctx);	 

		server.start();
		server.join();
	}
}
