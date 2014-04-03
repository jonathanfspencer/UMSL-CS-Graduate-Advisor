package edu.umsl.cs.group4.services.rotation;


import java.util.Calendar;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import edu.umsl.cs.group4.services.rotation.beans.Rotations;
import edu.umsl.cs.group4.services.rotation.beans.Rotations.RotationYear;
import edu.umsl.cs.group4.services.rotation.beans.Rotations.RotationYear.Course;
import edu.umsl.cs.group4.utility.ContentFetcher;

@Path("rotations")
public class RotationResource {
	
	private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Rotation.xml";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public static Rotations getRotation() throws JAXBException  {
		Rotations rotations = (Rotations) ContentFetcher.fetchContent(SOURCE_URL, Rotations.class);
		filterRotations(rotations);
        return rotations;
	}

	private static void filterRotations(Rotations rotations) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Iterator<RotationYear> rotationYearIterator = rotations.getRotationYear().iterator();
		while (rotationYearIterator.hasNext()){
			RotationYear rotationYear = rotationYearIterator.next();
			if(rotationYear.getYear() < currentYear){
				rotationYearIterator.remove();
			} else {
				Iterator<Course> courseIterator = rotationYear.getCourse().iterator();
				while (courseIterator.hasNext()){
					Course course = courseIterator.next();
					if(!course.getSubject().equalsIgnoreCase("CMP SCI") || Integer.valueOf(course.getCourseNumber()) < 4000 ){
						courseIterator.remove();
					}
				}
			}
			
		}
		
	}
}
