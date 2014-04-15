package edu.umsl.cs.group4.services.preferences;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.umsl.cs.group4.services.courses.Course;

@Path("preferences")
public class Preferences {

	private Integer maxClassesPerSemester;
	private Integer minClassesPerSemester;
	private Boolean canTakeDayClasses;
	private Integer maxSemestersToComplete;
	private Integer numberOfHoursCompleted;
	private Integer numberOfHoursScheduled;
	private Integer numberOfHoursRemaining;
	private Integer numberOf6000HoursScheduled;
	private Integer numberOf5000HoursScheduled;
	private Integer numberOf4000HoursScheduled;
	private List<Course> courses;
	
	@Path("/apply")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Preferences applyPreferences(Preferences preferences){
		
		//If requirements have been met, exit
		//Else,
		//TODO the big nasty algorithm
//		Requirements requirements = new Requirements();
		//Loop through courses to 
			//Separate courses by year and session
		//For each year
			//For each Spring
				//Determine how many units are scheduled
				//If room and needed, schedule core courses
				//If room and needed, schedule a 6000 course
				//If room and needed, schedule 5000 courses
				//If room and needed, schedule 4000 courses
			//For each Summer
			//For each Fall
		return preferences;
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
}
