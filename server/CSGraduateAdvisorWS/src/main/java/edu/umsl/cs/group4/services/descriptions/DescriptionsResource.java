package edu.umsl.cs.group4.services.descriptions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import edu.umsl.cs.group4.services.descriptions.beans.Descriptions;
import edu.umsl.cs.group4.utility.ContentFetcher;


/**
 * Root resource (exposed at "descriptions" path)
 */
@Path("descriptions")
public class DescriptionsResource {
	
	private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Courses.xml";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static Descriptions getDescriptions() throws JAXBException {
    	Descriptions descriptions = (Descriptions) ContentFetcher.fetchContent(SOURCE_URL, Descriptions.class);
    	return descriptions;
    }
}
