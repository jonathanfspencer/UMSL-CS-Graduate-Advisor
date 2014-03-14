package edu.umsl.cs.group4.services.schedule;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import edu.umsl.cs.group4.services.schedule.beans.Schedule;
import edu.umsl.cs.group4.utility.ContentFetcher;

@Path("schedule")
public class ScheduleResource {

	private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Schedule.xml";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Schedule getSchedule() throws JAXBException {
		Schedule schedule = (Schedule) ContentFetcher.fetchContent(SOURCE_URL, Schedule.class);
        return schedule;
	}
}
