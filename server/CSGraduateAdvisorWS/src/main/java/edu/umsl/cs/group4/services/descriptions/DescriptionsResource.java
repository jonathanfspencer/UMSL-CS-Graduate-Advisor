package edu.umsl.cs.group4.services.descriptions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.umsl.cs.group4.services.descriptions.beans.Descriptions;
import edu.umsl.cs.group4.services.descriptions.service.DescriptionsService;
import edu.umsl.cs.group4.services.descriptions.service.DescriptionsServiceImpl;


/**
 * Root resource (exposed at "descriptions" path)
 */
@Path("descriptions")
public class DescriptionsResource {
	
	private DescriptionsService service = new DescriptionsServiceImpl();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Descriptions getDescriptions() throws JAXBException {
    	JAXBContext context = JAXBContext.newInstance(Descriptions.class);
    	Unmarshaller um = context.createUnmarshaller();
        Descriptions descriptions = (Descriptions) um.unmarshal(service.getDescriptionsURL());
    	return descriptions;
    }
}
