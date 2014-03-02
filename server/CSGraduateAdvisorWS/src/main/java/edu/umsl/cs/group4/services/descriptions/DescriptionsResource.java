package edu.umsl.cs.group4.services.descriptions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
	
	private DescriptionsService service = new DescriptionsServiceURLImpl();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDescriptions() throws JAXBException {
    	JAXBContext context = JAXBContext.newInstance(Descriptions.class);
    	Unmarshaller um = context.createUnmarshaller();
        Descriptions descriptions = (Descriptions) um.unmarshal(service.getDescriptionsSource());
        return Response.ok(descriptions).header("Access-Control-Allow-Origin","*").build();
    }
}
