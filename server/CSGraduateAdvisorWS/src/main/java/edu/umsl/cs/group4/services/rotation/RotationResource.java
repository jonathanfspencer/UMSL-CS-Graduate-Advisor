package edu.umsl.cs.group4.services.rotation;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import edu.umsl.cs.group4.services.rotation.beans.Rotations;
import edu.umsl.cs.group4.utility.ContentFetcher;

@Path("rotations")
public class RotationResource {
	
	private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Rotation.xml";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public static Rotations getRotation() throws JAXBException  {
		Rotations rotations = (Rotations) ContentFetcher.fetchContent(SOURCE_URL, Rotations.class);
        return extrapolateRotations(rotations,6);
	}
	
	private static Rotations extrapolateRotations(Rotations rotations, int years) {
		return rotations;
	}

}
