package edu.umsl.cs.group4.services.schedule;

import java.util.Calendar;
import java.util.Iterator;

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
		filterSchedule(schedule);
        return schedule;
	}

	private static void filterSchedule(SimpleSchedule schedule) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Iterator<SimpleSchedule.ScheduledCourse> scheduledCourseIterator = schedule.getScheduledCourse().iterator();
		while(scheduledCourseIterator.hasNext()) {
			SimpleSchedule.ScheduledCourse scheduledCourse = scheduledCourseIterator.next();
			if(Integer.valueOf(scheduledCourse.getYear()) < currentYear){
				scheduledCourseIterator.remove();
			} else {
				Iterator<SimpleSchedule.ScheduledCourse.Session> sessionIterator = scheduledCourse.getSession().iterator();
				while(sessionIterator.hasNext()){
					SimpleSchedule.ScheduledCourse.Session session = sessionIterator.next();
					Iterator<SimpleSchedule.ScheduledCourse.Session.Course> courseIterator = session.getCourse().iterator();
					while(courseIterator.hasNext()){
						SimpleSchedule.ScheduledCourse.Session.Course course = courseIterator.next();
						if(!course.getSubject().equalsIgnoreCase("CMP SCI") || Integer.valueOf(course.getCourseNumber()) < 4000){
							courseIterator.remove();
						}
					}
				}
			}
		}
	}
}
