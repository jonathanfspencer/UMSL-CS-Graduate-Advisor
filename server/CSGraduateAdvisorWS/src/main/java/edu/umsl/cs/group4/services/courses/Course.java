package edu.umsl.cs.group4.services.courses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

	private void addDescriptions(Map<String, Course> courseMap,
			Descriptions descriptions) {
		Iterator<Descriptions.Course> courseIterator = descriptions.getCourse().iterator();
    	while(courseIterator.hasNext()) {
    		Descriptions.Course descriptionCourse = courseIterator.next();
    		if(!descriptionCourse.getSubject().equalsIgnoreCase("CMP SCI") || descriptionCourse.getCourseNumber() < 4000){
    			courseIterator.remove();
    		} else {
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
		for(Rotations.RotationYear rotationYear:rotations.getRotationYear()){
			for(Rotations.RotationYear.Course rotationCourse:rotationYear.getCourse()){
				for(Rotations.RotationYear.Course.RotationTerm rotationTerm:rotationCourse.getRotationTerm()){
					Course thisCourse = courseMap.get(rotationCourse.getCourseNumber());
					if(thisCourse == null){
						 //TODO build a new course and insert in map? we won't have all the info
					} else {
						//update course offerings
						Offering offering = new Offering();
						offering.setYear(Integer.toString(rotationYear.getYear()));
						offering.setSession(rotationTerm.getTerm());
						offering.setTimeCodes(rotationTerm.getTimeCode());
						if(thisCourse.getOfferings() == null) {
							thisCourse.setOfferings(new ArrayList<Offering>());
						}
						thisCourse.getOfferings().add(offering);
					}
				}
			}
		}
		
	}
	
	private void addSchedule(Map<String, Course> courseMap, SimpleSchedule schedule) {
		for(SimpleSchedule.ScheduledCourse scheduledCourse:schedule.getScheduledCourse()){
			//make a new Offering to add to all courses in this list
			Offering offering = new Offering();
			offering.setYear(scheduledCourse.getYear());
			offering.setSession(scheduledCourse.getTerm());
			
			for(SimpleSchedule.ScheduledCourse.Session session:scheduledCourse.getSession()){
				for(SimpleSchedule.ScheduledCourse.Session.Course course:session.getCourse()){
					if(courseMap.containsKey(course.getCourseNumber())){
						//add this offering to the course map
						Course thisCourse = courseMap.get(course.getCourseNumber());
						if(thisCourse.getOfferings() == null) {
							thisCourse.setOfferings(new ArrayList<Offering>());
						}
						thisCourse.getOfferings().add(offering);
					} else {
						//TODO add a new course that might lack information?
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
