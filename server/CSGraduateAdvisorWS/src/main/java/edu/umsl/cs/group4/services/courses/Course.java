package edu.umsl.cs.group4.services.courses;

import java.util.ArrayList;
import java.util.Arrays;
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
import javax.xml.bind.annotation.XmlTransient;

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
	@SuppressWarnings("unused")
	private static final String COURSE_STATUS_SCHEDULED = "S";
	private static final String COURSE_STATUS_NULL = "N";
	@SuppressWarnings("unused")
	private static final String COURSE_STATUS_TAKEN = "T";
	@SuppressWarnings("unused")
	private static final String COURSE_STATUS_WAIVED = "W";
	private String number;
	private String name;
	private String description;
	private String credits;
	private Descriptions.Course.Prerequisite prequisite;
	private Collection<Offering> offerings;
	private String status = COURSE_STATUS_NULL;
	private Offering scheduledOffering;
	
	public static class Offering {
		private String year;
		private Collection<String> timeCodes;
		private String session;
		
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public Collection<String> getTimeCodes() {
			if(this.timeCodes == null){
				this.timeCodes = new ArrayList<String>();
			}
			return this.timeCodes;
		}
		public void setTimeCodes(Collection<String> timeCodes) {
			this.timeCodes = timeCodes;
		}
		
		@XmlTransient
		public void addTimeCode(String timeCode){
			if(this.timeCodes == null){
				this.timeCodes = new ArrayList<String>();
				this.timeCodes.add(timeCode);
			} else {
				boolean found = false;
				for(String oldCode : this.timeCodes) {
					if(oldCode.equalsIgnoreCase(timeCode)) {
						found = true;
					}
				}
				if(!found){
					this.timeCodes.add(timeCode);
				}
			}
			
			
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
							mapCourse.addOfferings(offerings);
						} else {
							//TODO build a new course and insert in map? we won't have all the info
						}
					}
				}
			}
			
		}
		
	}
	
	/*
	 * Standardizes term representation to either Spring, Summer, or Fall
	 */
	private String translateScheduleTerm(String term) {
		switch(term) {
		case "SP":
			return "Spring";
		case "SS":
			return "Summer";
		case "FS":
			return "Fall";
		}
		
		// Should only happen if the term abbreviations change.
		return "";
	}
	private void addSchedule(Map<String, Course> courseMap, SimpleSchedule schedule) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		for(SimpleSchedule.ScheduledCourse scheduledCourse:schedule.getScheduledCourse()){
			if(Integer.valueOf(scheduledCourse.getYear()) >= currentYear){
				//make a new Offering to add to all courses in this list
				Offering offering = new Offering();
				offering.setYear(scheduledCourse.getYear());
				offering.setSession(translateScheduleTerm(scheduledCourse.getTerm()));
				offering.getTimeCodes().add("S"); // Add a time-code, so it looks like there is an offering of this class. Even though it doesn't mean anything.
				
				for(SimpleSchedule.ScheduledCourse.Session session:scheduledCourse.getSession()){
					for(SimpleSchedule.ScheduledCourse.Session.Course scheduleCourse:session.getCourse()){
						if(scheduleCourse.getSubject().equalsIgnoreCase(ADVISOR_SUBJECT) && Integer.valueOf(scheduleCourse.getCourseNumber()) >= MINIMUM_COURSE_NUMBER){
							Course mapCourse = courseMap.get(scheduleCourse.getCourseNumber());
							if(mapCourse != null){
								//add this offering to the course map								
								mapCourse.addOffering(offering);
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

	public Collection<Offering> getOfferings() {
		if(this.offerings == null) {
			this.offerings = new ArrayList<Offering>();
		}
		return this.offerings;
	}

	public void setOfferings(Collection<Offering> offerings) {
		this.offerings = offerings;
	}

	@XmlTransient
	public void addOffering(Offering newOffering) {
		addOfferings(Arrays.asList(new Offering[]{newOffering}));
	}
	
	@XmlTransient
	/**
	 * Add new offerings and reconcile them with the current offerings
	 * @param offerings
	 */
	public void addOfferings(Collection<Offering> offerings) {
		if(this.offerings == null) {
			this.offerings = offerings;
		} else {
			Map<String,Offering> reconciledOfferings = new HashMap<String,Offering>();
			for(Offering oldOffering : this.offerings){
				reconciledOfferings.put(oldOffering.getYear() + oldOffering.getSession(), oldOffering);
			}
			for(Offering newOffering : offerings) {
				Offering tempOffering = reconciledOfferings.put(newOffering.getYear()+newOffering.getSession(), newOffering);
				if(tempOffering != null) {
					for(String code : tempOffering.getTimeCodes()){
						newOffering.addTimeCode(code);
					}
				}
			}
			this.offerings = reconciledOfferings.values();
		}
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Offering getScheduledOffering() {
		return scheduledOffering;
	}

	public void setScheduledOffering(Offering scheduledOffering) {
		this.scheduledOffering = scheduledOffering;
	}
	
}
