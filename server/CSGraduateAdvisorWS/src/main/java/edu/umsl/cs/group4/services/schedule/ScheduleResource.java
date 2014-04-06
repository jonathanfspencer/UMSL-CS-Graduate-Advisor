package edu.umsl.cs.group4.services.schedule;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import edu.umsl.cs.group4.services.schedule.beans.SimpleSchedule;
import edu.umsl.cs.group4.utility.ContentFetcher;

@Path("schedule")
public class ScheduleResource {

	private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Schedule.xml";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public static SimpleSchedule getSchedule() throws JAXBException {
		SimpleSchedule schedule = (SimpleSchedule) ContentFetcher.fetchContent(SOURCE_URL, SimpleSchedule.class);
        return schedule;
	}
}
