package edu.umsl.cs.group4.services.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
	private List<String> coreCoursesRemaining;
	private List<Course> courses;
	private boolean complete = false;
	
	
	
	public Preferences() {
		super();
		this.coreCoursesRemaining = new ArrayList<String>();
		this.courses = new ArrayList<Course>();
	}

	@Path("/apply")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Preferences applyPreferences(Preferences preferences){
		
		//If requirements have been met, exit
		//Else,
		//TODO the big nasty algorithm
		
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
				sessionScheduler(year, springSessionCourses, SPRING_SESSION_NAME, preferences);
			}
			
			//For each Summer
			CoursesBySession summerSessionCourses = coursesByYear.getCoursesBySession().get(SUMMER_SESSION_NAME);
			if(summerSessionCourses != null) {
				sessionScheduler(year, summerSessionCourses, SUMMER_SESSION_NAME, preferences);
			}
			
			//For each Fall
			CoursesBySession fallSessionCourses = coursesByYear.getCoursesBySession().get(FALL_SESSION_NAME);
			if(fallSessionCourses != null) {
				sessionScheduler(year, fallSessionCourses, FALL_SESSION_NAME, preferences);
			}
			
		}
		
		//check to see if requirements have been met
		Requirements requirements = new Requirements();
		if(preferences.getCoreCoursesRemaining().size() == 0 && preferences.getNumberOf6000HoursScheduled() > Integer.valueOf(requirements.getMin6000Hours()) && (preferences.getNumberOf5000HoursScheduled() + preferences.getNumberOf6000HoursScheduled()) >= (Integer.valueOf(requirements.getMinTotalHours()) - Integer.valueOf(requirements.getMax4000Hours()))) {
			preferences.setComplete(true);
		}
		return preferences;
	}

	private void sessionScheduler(String year, CoursesBySession sessionCourses, String sessionName, Preferences preferences) {
		//Determine how many units are scheduled this semester
		int sessionHours = 0; //TODO use this in the conditionals below
		for(Course course : sessionCourses.getCourses()){
			if (course.getScheduledOffering() != null){
				if(course.getScheduledOffering().getYear().equals(year) && course.getScheduledOffering().getSession().equalsIgnoreCase(sessionName)){
					String courseCredits = course.getCredits().split("-")[0];	//A course might have a credits value of "1-3," so take the low value
					sessionHours += Integer.valueOf(courseCredits);
				}
			}
		}
		//If room and needed, schedule core courses
		if(preferences.getNumberOfHoursRemaining() > 0 && sessionHours < preferences.getMaxClassesPerSemester() && !preferences.getCoreCoursesRemaining().isEmpty()) {
			//TODO try to schedule some core courses
			//if successful, decrement numberOfHoursRemaining and increment corresponding hour counters for 4000 or 5000 level classes
		}
		//If room and needed, schedule a 6000 course
		if(preferences.getNumberOfHoursRemaining() > 0 && sessionHours < preferences.getMaxClassesPerSemester() && preferences.getNumberOf6000HoursScheduled() < 3) {
			//TODO try to schedule a 6000 level course
			//if successful, decrement numberOfHoursRemaining and increment numberOf6000HoursScheduled 
		}
		//If room and needed, schedule 5000 courses
		if(preferences.getNumberOfHoursRemaining() > 0 && sessionHours < preferences.getMaxClassesPerSemester() && preferences.getNumberOf5000HoursScheduled() < 15) {
			//TODO try to schedule some 5000 level classes
			//if successful, decrement numberOfHoursRemaining and increment numberOf5000HoursScheduled
		}
		//If room and needed, schedule 4000 courses
		if(preferences.getNumberOfHoursRemaining() > 0 && sessionHours < preferences.getMaxClassesPerSemester() && preferences.getNumberOf4000HoursScheduled() < 12) {
			//TODO try to schedule some 4000 level classes
			//if successful, decrement numberOfHoursRemaining and increment numberOf4000HoursScheduled
		}
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

	public List<String> getCoreCoursesRemaining() {
		return coreCoursesRemaining;
	}

	public void setCoreCoursesRemaining(List<String> coreCoursesRemaining) {
		this.coreCoursesRemaining = coreCoursesRemaining;
	}

	/**
	 * Answers the question of whether a complete schedule has been generated
	 * @return <b>true</b> if a complete schedule was generated, <b>false</b> if not
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * State whether the generated schedule is complete according to the requirements
	 * @param complete = <b>true</b> if the schedule is complete, <b>false</b> otherwise
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
}
