package edu.umsl.cs.group4.services.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.umsl.cs.group4.services.courses.Course;
import edu.umsl.cs.group4.services.preferences.ScheduleFacade.CoursesBySession;
import edu.umsl.cs.group4.services.preferences.ScheduleFacade.CoursesByYear;
import edu.umsl.cs.group4.services.requirements.Requirements;

@Path("preferences")
public class Preferences {

	private static final String DEFAULT_CREDIT_RANGE_HOURS = "3";
	private static final String FALL_SESSION_NAME = "Fall";
	private static final String SUMMER_SESSION_NAME = "Summer";
	private static final String SPRING_SESSION_NAME = "Spring";
	private int maxClassesPerSemester = 18;  //TODO change Classes to Hours
	private int minClassesPerSemester = 1;   //TODO change Classes to Hours
	private boolean canTakeDayClasses = true;
	private int maxSemestersToComplete = 12;
	private int numberOfHoursCompleted = 0;
	private int numberOfHoursScheduled = 0;
	private int numberOfHoursRemaining = 30;
	private int numberOf6000HoursScheduled = 0;
	private int numberOf5000HoursScheduled = 0;
	private int numberOf4000HoursScheduled = 0;
	private boolean isInternationalStudent = false;
	private List<Course> courses;
	
	
	
	public Preferences() {
		super();
		this.courses = new ArrayList<Course>();
	}

	@Path("/apply")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Preferences applyPreferences(Preferences preferences){
		
		//If requirements have not been met, run the auto-scheduler algorithm
		if(!validatePreferences(preferences).getNotifications().isEmpty()) {
		
			//International students must take a minimum course load
			Requirements requirements = new Requirements();
			if(preferences.getMinClassesPerSemester() == null) {
				preferences.setMinClassesPerSemester(0);
			}
			if(preferences.isInternationalStudent()) {
				if(preferences.getMinClassesPerSemester() < Integer.valueOf(requirements.getInternationalRequiredSemesterHours())) {
					preferences.setMinClassesPerSemester(Integer.valueOf(requirements.getInternationalRequiredSemesterHours()));
				}
			}
			
			//create a ScheduleFacade to represent the schedule
			ScheduleFacade schedule = new ScheduleFacade(preferences.getCourses());
			//Get an ordered list of the years
			List<String> years = new ArrayList<String>();
			Iterator<String> yearIterator = schedule.getCoursesByYear().keySet().iterator();
			while(yearIterator.hasNext()){
				years.add(yearIterator.next());
			}
			Collections.sort(years);
			//For each year
			for(String year : years){
				CoursesByYear coursesByYear = schedule.getCoursesByYear().get(year);
				
				//For each Spring
				CoursesBySession springSessionCourses = coursesByYear.getCoursesBySession().get(SPRING_SESSION_NAME);
				if(springSessionCourses != null) {
					sessionScheduler(year, springSessionCourses, SPRING_SESSION_NAME, preferences, requirements);
				}
				
				//For each Summer
				CoursesBySession summerSessionCourses = coursesByYear.getCoursesBySession().get(SUMMER_SESSION_NAME);
				if(summerSessionCourses != null) {
					sessionScheduler(year, summerSessionCourses, SUMMER_SESSION_NAME, preferences, requirements);
				}
				
				//For each Fall
				CoursesBySession fallSessionCourses = coursesByYear.getCoursesBySession().get(FALL_SESSION_NAME);
				if(fallSessionCourses != null) {
					sessionScheduler(year, fallSessionCourses, FALL_SESSION_NAME, preferences, requirements);
				}
				
			}
			
		}

		return preferences; 
	}

	@Path("/validate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ValidationResult validatePreferences(Preferences preferences){
		List<String> messages = new ArrayList<String>();
		Requirements requirements = new Requirements();
		
		//check number of 6000 hours 
		if(preferences.getNumberOf6000HoursScheduled() < Integer.valueOf(requirements.getMin6000Hours())) {
			messages.add("You must take " + (Integer.valueOf(requirements.getMin6000Hours()) - preferences.getNumberOf6000HoursScheduled()) + " more 6000 level credits to graduate.");
		}
		
		//check total number of 5000 and 6000 hours
		int numberOf5000PlusHoursScheduled = preferences.getNumberOf5000HoursScheduled() + preferences.getNumberOf6000HoursScheduled();
		int numberOf5000PlusHoursRequired = Integer.valueOf(requirements.getMinTotalHours()) - Integer.valueOf(requirements.getMax4000Hours());
		if(numberOf5000PlusHoursScheduled < numberOf5000PlusHoursRequired) {
			messages.add("You must take " + (numberOf5000PlusHoursRequired - numberOf5000PlusHoursScheduled) +" more 5000 or higher level credits to graduate.");
		}
		
		//check total number of hours
		int totalHoursScheduled = Integer.valueOf(preferences.getNumberOf4000HoursScheduled()) +
				Integer.valueOf(preferences.getNumberOf5000HoursScheduled()) + 
				Integer.valueOf(preferences.getNumberOf6000HoursScheduled());
		int totalHoursRequired = Integer.valueOf(requirements.getMinTotalHours());
		if(totalHoursScheduled < totalHoursRequired) {
			messages.add("You must take "+ (totalHoursRequired - totalHoursScheduled) + " more credits to graduate.");
		}
		
		//check that all core courses have been taken
		Map<String,String> coreCoursesRemaining = new HashMap<String,String>();
		for(String courseNumber : requirements.getCoreCourses()) {
			coreCoursesRemaining.put(courseNumber, courseNumber);
		}
		for(Course course : preferences.getCourses()) {
			if(course.getScheduledOffering() != null) {
				//remove this course from the remaining core courses if it is a core
				coreCoursesRemaining.remove(course.getNumber());
			}
		}
		if(!coreCoursesRemaining.isEmpty()) {
			for(String coreNumber : coreCoursesRemaining.values()){
				messages.add("You must take " + coreNumber + " to graduate.");
			}
		}
		
		//check if international student has taken enough per semester
		if(preferences.isInternationalStudent()) {
			//create a ScheduleFacade to represent the schedule
			ScheduleFacade schedule = new ScheduleFacade(preferences.getCourses());
			//Get an ordered list of the years
			List<String> years = new ArrayList<String>();
			Iterator<String> yearIterator = schedule.getCoursesByYear().keySet().iterator();
			while(yearIterator.hasNext()){
				years.add(yearIterator.next());
			}
			Collections.sort(years);
			//For each year
			for(String year : years){
				CoursesByYear coursesByYear = schedule.getCoursesByYear().get(year);
				
				//For each Spring
				checkInternationalStudentSessionHours(messages, requirements,
						year, coursesByYear, SPRING_SESSION_NAME);
				
				//For each Fall
				checkInternationalStudentSessionHours(messages, requirements,
						year, coursesByYear, FALL_SESSION_NAME);
				
			}
		}
		
		//TODO warn if a lot of classes are scheduled in a semester
		
		ValidationResult result = new ValidationResult(messages);
		return result;
	}

	private void checkInternationalStudentSessionHours(List<String> messages,
			Requirements requirements, String year, CoursesByYear coursesByYear, String sessionName) {
		CoursesBySession sessionCourses = coursesByYear.getCoursesBySession().get(sessionName);
		//are enough courses scheduled?
		int sessionHours = 0;
		if(sessionCourses != null) {
			for (Course course : sessionCourses.getCourses()) {
				sessionHours += determineCourseHours(course.getCredits());
			}
		}
		if(sessionHours < Integer.valueOf(requirements.getInternationalRequiredSemesterHours())) {
			messages.add("You must take " + (Integer.valueOf(requirements.getInternationalRequiredSemesterHours()) - sessionHours) + " more hours during " + sessionName + " " + year + " to meet the international student minimum hours requirement.");
		}
	}
	

	private void sessionScheduler(String year, CoursesBySession sessionCourses, String sessionName, Preferences preferences, Requirements requirements) {
		//Determine how many units are scheduled this semester
		int sessionHours = 0; //TODO use this in the conditionals below
		for(Course course : sessionCourses.getCourses()){
			if (course.getScheduledOffering() != null){
				if(course.getScheduledOffering().getYear().equals(year) && course.getScheduledOffering().getSession().equalsIgnoreCase(sessionName)){
					sessionHours += determineCourseHours(course.getCredits());
				}
			}
		}
		//If room and needed, schedule core courses
		if(scheduleMoreCoreCourses(preferences, sessionHours)) {
			//TODO try to schedule some core courses
			//if successful, decrement numberOfHoursRemaining and increment corresponding hour counters for 4000 or 5000 level classes
			for(Course course : sessionCourses.getCourses()) {
				
				if(	course.getStatus().equals("N") &&
					Arrays.asList(requirements.getCoreCourses()).contains(course.getNumber()) && 
					preferences.getNumberOfHoursRemaining() > 0 && 
					sessionHours < preferences.getMaxClassesPerSemester()) {
					
					course.setStatus("S");
					
					// Set the scheduled offering of this course to this session and year
					Course.Offering scheduledOffering = new Course.Offering();
					scheduledOffering.setSession(sessionName);
					scheduledOffering.setYear(year);					
					course.setScheduledOffering(scheduledOffering);
					
					// Modify credit counters
					Integer courseCredits = determineCourseHours(course.getCredits());
					preferences.setNumberOfHoursRemaining(preferences.getNumberOfHoursRemaining() - courseCredits);
					preferences.setNumberOfHoursScheduled(preferences.getNumberOfHoursScheduled() + courseCredits);
					sessionHours += courseCredits;
					
					// Increment Course level counters
					Integer courseNum = Integer.parseInt(course.getNumber());
					if(courseNum >= 4000 && courseNum < 5000) {
						preferences.setNumberOf4000HoursScheduled(preferences.getNumberOf4000HoursScheduled() + 1);
					} else if(courseNum >= 5000 && courseNum< 6000) {
						preferences.setNumberOf5000HoursScheduled(preferences.getNumberOf5000HoursScheduled() + 1);
					} else if(courseNum >= 6000) {
						preferences.setNumberOf6000HoursScheduled(preferences.getNumberOf6000HoursScheduled() + 1);
					}
					
				}
				//if we have scheduled enough courses for this session, do not schedule more
				if(!scheduleMoreCoreCourses(preferences, sessionHours)) {
					break;
				}
			}
		}
		//If room and needed, schedule a 6000 course
		if(scheduleMore6000Courses(preferences, sessionHours)) {
			//TODO try to schedule a 6000 level course
			//if successful, decrement numberOfHoursRemaining and increment numberOf6000HoursScheduled
			
			int required6000Hours = Integer.parseInt(requirements.getMin6000Hours());
			
			for(Course course : sessionCourses.getCourses()) {
				
				if(	course.getStatus().equals("N") &&
					course.getNumber().startsWith("6")  && 
					preferences.getNumberOf6000HoursScheduled() < required6000Hours &&
					preferences.getNumberOfHoursRemaining() > 0 && 
					sessionHours < preferences.getMaxClassesPerSemester()) {
					
					course.setStatus("S");
					
					// Set the scheduled offering of this course to this session and year
					Course.Offering scheduledOffering = new Course.Offering();
					scheduledOffering.setSession(sessionName);
					scheduledOffering.setYear(year);					
					course.setScheduledOffering(scheduledOffering);
					
					// Modify credit counters
					Integer courseCredits = determineCourseHours(course.getCredits());
					preferences.setNumberOfHoursRemaining(preferences.getNumberOfHoursRemaining() - courseCredits);
					preferences.setNumberOfHoursScheduled(preferences.getNumberOfHoursScheduled() + courseCredits);
					sessionHours += courseCredits;
					
					// Increment Course level counters
					preferences.setNumberOf6000HoursScheduled(preferences.getNumberOf6000HoursScheduled() + 1);
				}
				//if we have scheduled enough courses for this session, do not schedule any more
				if(!scheduleMore6000Courses(preferences, sessionHours)) {
					break;
				}
			}
		}
		//If room and needed, schedule 5000 courses
		if(scheduleMore5000Courses(preferences, requirements, sessionHours)) {
			//TODO try to schedule some 5000 level classes
			//if successful, decrement numberOfHoursRemaining and increment numberOf5000HoursScheduled
			
			for(Course course : sessionCourses.getCourses()) {
				
				if(	course.getStatus().equals("N") &&
					course.getNumber().startsWith("5")  && 
					preferences.getNumberOfHoursRemaining() > 0 && 
					sessionHours < preferences.getMaxClassesPerSemester()) {
					
					course.setStatus("S");
					
					// Set the scheduled offering of this course to this session and year
					Course.Offering scheduledOffering = new Course.Offering();
					scheduledOffering.setSession(sessionName);
					scheduledOffering.setYear(year);					
					course.setScheduledOffering(scheduledOffering);
					
					// Modify credit counters
					Integer courseCredits = determineCourseHours(course.getCredits());
					preferences.setNumberOfHoursRemaining(preferences.getNumberOfHoursRemaining() - courseCredits);
					preferences.setNumberOfHoursScheduled(preferences.getNumberOfHoursScheduled() + courseCredits);
					sessionHours += courseCredits;
					
					// Increment Course level counters
					preferences.setNumberOf5000HoursScheduled(preferences.getNumberOf5000HoursScheduled() + 1);
				}
				//if we have scheduled enough courses for this session, do not schedule any more
				if(!scheduleMore5000Courses(preferences, requirements, sessionHours)) {
					break;
				}
			}
			
		}
		//If room and needed, schedule 4000 courses
		if(scheduleMore4000Classes(preferences, requirements, sessionHours)) {
			//TODO try to schedule some 4000 level classes
			//if successful, decrement numberOfHoursRemaining and increment numberOf4000HoursScheduled
			
			for(Course course : sessionCourses.getCourses()) {
				
				if(	course.getStatus().equals("N") &&
					course.getNumber().startsWith("4")  && 
					preferences.getNumberOfHoursRemaining() > 0 && 
					sessionHours < preferences.getMaxClassesPerSemester() && 
					preferences.getNumberOf4000HoursScheduled() < 12) {
					
					course.setStatus("S");
					
					// Set the scheduled offering of this course to this session and year
					Course.Offering scheduledOffering = new Course.Offering();
					scheduledOffering.setSession(sessionName);
					scheduledOffering.setYear(year);					
					course.setScheduledOffering(scheduledOffering);
					
					// Modify credit counters
					Integer courseCredits = determineCourseHours(course.getCredits());
					preferences.setNumberOfHoursRemaining(preferences.getNumberOfHoursRemaining() - courseCredits);
					preferences.setNumberOfHoursScheduled(preferences.getNumberOfHoursScheduled() + courseCredits);
					sessionHours += courseCredits;
					
					// Increment Course level counters
					preferences.setNumberOf4000HoursScheduled(preferences.getNumberOf4000HoursScheduled() + 1);
				}
				//if we have scheduled enough classes for this session, do not schedule any more
				if(!scheduleMore4000Classes(preferences, requirements, sessionHours)) {
					break;
				}
			}
		}
	}

	private boolean scheduleMore4000Classes(Preferences preferences,
			Requirements requirements, int sessionHours) {
		return preferences.getNumberOfHoursRemaining() > 0 && 
				sessionHours < preferences.getMaxClassesPerSemester() && 
				sessionHours < preferences.getMinClassesPerSemester() &&
				preferences.getNumberOf4000HoursScheduled() < Integer.valueOf(requirements.getMax4000Hours());
	}

	private boolean scheduleMore5000Courses(Preferences preferences,
			Requirements requirements, int sessionHours) {
		return preferences.getNumberOfHoursRemaining() > 0 && 
				sessionHours < preferences.getMaxClassesPerSemester() && 
				sessionHours < preferences.getMinClassesPerSemester() && 
				(preferences.getNumberOf5000HoursScheduled() + preferences.getNumberOf6000HoursScheduled()) >= 
					(Integer.valueOf(requirements.getMinTotalHours()) - Integer.valueOf(requirements.getMax4000Hours()));
	}

	private boolean scheduleMore6000Courses(Preferences preferences,
			int sessionHours) {
		return preferences.getNumberOfHoursRemaining() > 0 && sessionHours < preferences.getMaxClassesPerSemester() && preferences.getNumberOf6000HoursScheduled() < 3 && sessionHours < preferences.getMinClassesPerSemester();
	}

	private boolean scheduleMoreCoreCourses(Preferences preferences, int sessionHours) {
		return preferences.getNumberOfHoursRemaining() > 0 && sessionHours < preferences.getMaxClassesPerSemester() && sessionHours < preferences.getMinClassesPerSemester();
	}

	private int determineCourseHours(String courseCredits) {
		if(courseCredits.contains("-")) {
			courseCredits = DEFAULT_CREDIT_RANGE_HOURS;	//A course might have a credits value of "1-3," so make it the default
		}
		return Integer.valueOf(courseCredits);
	}

	public Integer getMaxClassesPerSemester() {
		return maxClassesPerSemester;
	}

	public void setMaxClassesPerSemester(Integer maxClassesPerSemester) {
		this.maxClassesPerSemester = maxClassesPerSemester;
	}

	public Integer getMinClassesPerSemester() {
		return minClassesPerSemester;
	}

	public void setMinClassesPerSemester(Integer minClassesPerSemester) {
		this.minClassesPerSemester = minClassesPerSemester;
	}

	public Boolean getCanTakeDayClasses() {
		return canTakeDayClasses;
	}

	public void setCanTakeDayClasses(Boolean canTakeDayClasses) {
		this.canTakeDayClasses = canTakeDayClasses;
	}

	public Integer getMaxSemestersToComplete() {
		return maxSemestersToComplete;
	}

	public void setMaxSemestersToComplete(Integer maxSemestersToComplete) {
		this.maxSemestersToComplete = maxSemestersToComplete;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public Integer getNumberOfHoursCompleted() {
		return numberOfHoursCompleted;
	}

	public void setNumberOfHoursCompleted(Integer numberOfHoursCompleted) {
		this.numberOfHoursCompleted = numberOfHoursCompleted;
	}

	public Integer getNumberOfHoursScheduled() {
		return numberOfHoursScheduled;
	}

	public void setNumberOfHoursScheduled(Integer numberOfHoursScheduled) {
		this.numberOfHoursScheduled = numberOfHoursScheduled;
	}

	public Integer getNumberOfHoursRemaining() {
		return numberOfHoursRemaining;
	}

	public void setNumberOfHoursRemaining(Integer numberOfHoursRemaining) {
		this.numberOfHoursRemaining = numberOfHoursRemaining;
	}

	public Integer getNumberOf6000HoursScheduled() {
		return numberOf6000HoursScheduled;
	}

	public void setNumberOf6000HoursScheduled(Integer numberOf6000HoursScheduled) {
		this.numberOf6000HoursScheduled = numberOf6000HoursScheduled;
	}

	public Integer getNumberOf5000HoursScheduled() {
		return numberOf5000HoursScheduled;
	}

	public void setNumberOf5000HoursScheduled(Integer numberOf5000HoursScheduled) {
		this.numberOf5000HoursScheduled = numberOf5000HoursScheduled;
	}

	public Integer getNumberOf4000HoursScheduled() {
		return numberOf4000HoursScheduled;
	}

	public void setNumberOf4000HoursScheduled(Integer numberOf4000HoursScheduled) {
		this.numberOf4000HoursScheduled = numberOf4000HoursScheduled;
	}

	public boolean isInternationalStudent() {
		return isInternationalStudent;
	}

	public void setInternationalStudent(boolean isInternationalStudent) {
		this.isInternationalStudent = isInternationalStudent;
	}
	
}
