package edu.umsl.cs.group4.services.rotation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.umsl.cs.group4.services.rotation.beans.Rotations;
import edu.umsl.cs.group4.services.rotation.service.RotationsService;
import edu.umsl.cs.group4.services.rotation.service.RotationsServiceURLImpl;

@Path("rotations")
public class RotationResource {
	
	private RotationsService service = new RotationsServiceURLImpl();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRotation() throws JAXBException  {
		JAXBContext context = JAXBContext.newInstance(Rotations.class);
		Unmarshaller um = context.createUnmarshaller();
		Rotations rotations = (Rotations) um.unmarshal(service.getRotationsSource());
		return Response.ok(rotations).header("Access-Control-Allow-Origin","*").build();
	}
}
