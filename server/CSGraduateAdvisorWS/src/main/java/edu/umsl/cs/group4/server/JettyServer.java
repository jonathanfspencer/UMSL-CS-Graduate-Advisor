package edu.umsl.cs.group4.server;

import java.net.URL;

import org.eclipse.jetty.ajp.Ajp13SocketConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

	private static final String WEBAPPDIR = "webapp";

	public static void main(String[] args) throws Exception {
        (new JettyServer()).run();
	}

    public void run() throws Exception {
		Server server = new Server(8081);
		
        final String webAppPath = getWebappPath();
		
		WebAppContext ctx = new WebAppContext();
		ctx.setDescriptor(webAppPath + "/WEB-INF/web.xml");
		ctx.setResourceBase(webAppPath);
		ctx.setContextPath("/");
		ctx.setParentLoaderPriority(true);
		server.setHandler(ctx);	 
		
		Ajp13SocketConnector ajp = new Ajp13SocketConnector();
		ajp.setPort(8009);
		server.addConnector(ajp);

		server.start();
		server.join();

    }
    
    private String getWebappPath() {
    	final URL warUrl = this.getClass().getClassLoader().getResource(WEBAPPDIR);
    	
    	return (warUrl != null) ? warUrl.toExternalForm() : "src/main/webapp";
    }
}
