package edu.umsl.cs.group4.services.schedule;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.umsl.cs.group4.services.schedule.beans.Schedule;
import edu.umsl.cs.group4.services.schedule.service.ScheduleService;
import edu.umsl.cs.group4.services.schedule.service.ScheduleServiceURLImpl;

@Path("schedule")
public class ScheduleResource {

	private ScheduleService service = new ScheduleServiceURLImpl();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSchedule() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Schedule.class);
		Unmarshaller um = context.createUnmarshaller();
		Schedule schedule = (Schedule) um.unmarshal(service.getScheduleSource());
		return Response.ok(schedule).header("Access-Control-Allow-Origin","*").build();
	}
}
