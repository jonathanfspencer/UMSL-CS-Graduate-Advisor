package edu.umsl.cs.group4.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(8081);
		
		org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
 
		
		WebAppContext ctx = new WebAppContext();
		ctx.setDescriptor("src/main/webapp/WEB-INF/web.xml");
		ctx.setResourceBase("src/main/webapp/");
		ctx.setContextPath("/");
		ctx.setParentLoaderPriority(true);
		server.setHandler(ctx);	 

		server.start();
		server.join();
	}
}
