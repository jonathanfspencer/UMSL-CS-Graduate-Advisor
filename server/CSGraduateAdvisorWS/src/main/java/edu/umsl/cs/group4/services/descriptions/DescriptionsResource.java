package edu.umsl.cs.group4.services.descriptions;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.umsl.cs.group4.services.descriptions.beans.Descriptions;
import edu.umsl.cs.group4.services.descriptions.service.DescriptionsService;
import edu.umsl.cs.group4.services.descriptions.service.DescriptionsServiceURLImpl;


/**
 * Root resource (exposed at "descriptions" path)
 */
@Path("descriptions")
public class DescriptionsResource {
	

	@Context
	private HttpServletResponse response;
	
	private DescriptionsService service = new DescriptionsServiceURLImpl();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Descriptions getDescriptions() throws JAXBException {
    	response.addHeader("Access-Control-Allow-Origin","*");
    	JAXBContext context = JAXBContext.newInstance(Descriptions.class);
    	Unmarshaller um = context.createUnmarshaller();
        Descriptions descriptions = (Descriptions) um.unmarshal(service.getDescriptionsSource());
    	return descriptions;
    }
}
