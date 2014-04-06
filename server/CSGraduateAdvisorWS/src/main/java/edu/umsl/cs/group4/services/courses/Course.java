package edu.umsl.cs.group4.services.courses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import edu.umsl.cs.group4.services.descriptions.DescriptionsResource;
import edu.umsl.cs.group4.services.descriptions.beans.Descriptions;
import edu.umsl.cs.group4.services.rotation.RotationResource;
import edu.umsl.cs.group4.services.rotation.beans.Rotations;
import edu.umsl.cs.group4.services.schedule.ScheduleResource;
import edu.umsl.cs.group4.services.schedule.beans.SimpleSchedule;

@Path("courses")
public class Course {
	
	private static final int MINIMUM_COURSE_NUMBER = 4000;
	private static final String ADVISOR_SUBJECT = "CMP SCI";
	private String number;
	private String name;
	private String description;
	private String credits;
	private Descriptions.Course.Prerequisite prequisite;
	private List<Offering> offerings;
	
	public static class Offering {
		private String year;
		private List<String> timeCodes;
		private String session;
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public List<String> getTimeCodes() {
			return timeCodes;
		}
		public void setTimeCodes(List<String> timeCodes) {
			this.timeCodes = timeCodes;
		}
		public String getSession() {
			return session;
		}
		public void setSession(String session) {
			this.session = session;
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Course> getCourses() throws JAXBException{
		//build a map of course numbers and courses
		Map<String,Course> courseMap = new HashMap<String,Course>();		
		
		//first build a list of all the courses
		Descriptions descriptions = DescriptionsResource.getDescriptions();
		addDescriptions(courseMap,descriptions);
		
		//second add information about when courses are always offered
		Rotations rotations = RotationResource.getRotation();
		addRotations(courseMap,rotations);
		
		//finally add information about additional offered courses
		SimpleSchedule schedule = ScheduleResource.getSchedule();
		addSchedule(courseMap,schedule);		
		
		//return the final list of courses
		return courseMap.values();
	}

	private void addDescriptions(Map<String, Course> courseMap, Descriptions descriptions) {
		for(Descriptions.Course descriptionCourse:descriptions.getCourse()){
			//make sure course meets advising system requirements
    		if(descriptionCourse.getSubject().equalsIgnoreCase(ADVISOR_SUBJECT) && descriptionCourse.getCourseNumber() >= MINIMUM_COURSE_NUMBER){
    			Course newCourse = new Course();
    			newCourse.setCredits(descriptionCourse.getCredit());
    			newCourse.setDescription(descriptionCourse.getCourseDescription());
    			newCourse.setName(descriptionCourse.getCourseName());
    			newCourse.setNumber(Integer.toString(descriptionCourse.getCourseNumber()));
    			newCourse.setPrequisite(descriptionCourse.getPrerequisite());
    			courseMap.put(newCourse.getNumber(),newCourse);
    		}
    	}
	}

	private void addRotations(Map<String, Course> courseMap, Rotations rotations) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		for(Rotations.RotationYear rotationYear:rotations.getRotationYear()){
			if(rotationYear.getYear() >= currentYear){
				for(Rotations.RotationYear.Course rotationCourse:rotationYear.getCourse()){
					if(rotationCourse.getSubject().equalsIgnoreCase(ADVISOR_SUBJECT) && Integer.valueOf(rotationCourse.getCourseNumber()) >= MINIMUM_COURSE_NUMBER ){
						Course mapCourse = courseMap.get(rotationCourse.getCourseNumber());
						if(mapCourse != null){
							//There is a match in the course map, so add Offering information
							List<Offering> offerings = new ArrayList<Offering>();
							for(Rotations.RotationYear.Course.RotationTerm rotationTerm:rotationCourse.getRotationTerm()){
								Offering offering = new Offering();
								offering.setYear(Integer.toString(rotationYear.getYear()));
								offering.setSession(rotationTerm.getTerm());
								offering.setTimeCodes(rotationTerm.getTimeCode());
								offerings.add(offering);
							}	
							if(mapCourse.getOfferings() == null) {
								mapCourse.setOfferings(offerings);
							} else {
								mapCourse.getOfferings().addAll(offerings);
							}
						} else {
							//TODO build a new course and insert in map? we won't have all the info
						}
					}
				}
			}
			
		}
		
	}
	
	private void addSchedule(Map<String, Course> courseMap, SimpleSchedule schedule) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		for(SimpleSchedule.ScheduledCourse scheduledCourse:schedule.getScheduledCourse()){
			if(Integer.valueOf(scheduledCourse.getYear()) >= currentYear){
				//make a new Offering to add to all courses in this list
				Offering offering = new Offering();
				offering.setYear(scheduledCourse.getYear());
				offering.setSession(scheduledCourse.getTerm());
				
				for(SimpleSchedule.ScheduledCourse.Session session:scheduledCourse.getSession()){
					for(SimpleSchedule.ScheduledCourse.Session.Course scheduleCourse:session.getCourse()){
						if(scheduleCourse.getSubject().equalsIgnoreCase(ADVISOR_SUBJECT) && Integer.valueOf(scheduleCourse.getCourseNumber()) >= MINIMUM_COURSE_NUMBER){
							Course mapCourse = courseMap.get(scheduleCourse.getCourseNumber());
							if(mapCourse != null){
								//add this offering to the course map								
								if(mapCourse.getOfferings() == null) {
									mapCourse.setOfferings(new ArrayList<Offering>());
								}
								mapCourse.getOfferings().add(offering);
							} else {
								//TODO add a new course that might lack information?
							}
						}
					}
				}
			}
		}
	}

	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCredits() {
		return credits;
	}
	public void setCredits(String credits) {
		this.credits = credits;
	}
	public Descriptions.Course.Prerequisite getPrequisite() {
		return prequisite;
	}
	public void setPrequisite(Descriptions.Course.Prerequisite prequisite) {
		this.prequisite = prequisite;
	}

	public List<Offering> getOfferings() {
		return offerings;
	}

	public void setOfferings(List<Offering> offerings) {
		this.offerings = offerings;
	}
	
}
