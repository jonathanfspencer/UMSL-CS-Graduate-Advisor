package edu.umsl.cs.group4.services.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umsl.cs.group4.services.courses.Course;
import edu.umsl.cs.group4.services.courses.Course.Offering;

/**
 * A facade to represent a student's schedule by year and session.  The key
 * for the coursesByYear map is the year.  The key for the coursesBySession
 * map is the session.
 * 
 * @author jonathan
 *
 */
public class ScheduleFacade {

	private Map<String,CoursesByYear> coursesByYear;
	
	public class CoursesByYear {
		private Map<String,CoursesBySession> coursesBySession;
		

		public Map<String,CoursesBySession> getCoursesBySession() {
			if(this.coursesBySession == null){
				this.coursesBySession = new HashMap<String,CoursesBySession>();
			}
			return coursesBySession;
		}

		public void setCoursesBySession(Map<String,CoursesBySession> coursesBySession) {
			this.coursesBySession = coursesBySession;
		}
	}
	
	public class CoursesBySession {
		private List<Course> courses;

		public List<Course> getCourses() {
			if(this.courses == null){
				this.courses = new ArrayList<Course>();
			}
			return courses;
		}

		public void setCourses(List<Course> courses) {
			this.courses = courses;
		}
	}
	

	/**
	 * Constructor to build a ScheduleFacade out of a List of Course
	 * @param courses
	 */
	public ScheduleFacade(List<Course> courses) {
		this.coursesByYear = new HashMap<String,CoursesByYear>();
		for(Course course: courses){
			for(Offering offering : course.getOfferings()){
				//see if we need to add a year
				if(!this.coursesByYear.containsKey(offering.getYear())){
					//add the new year
					this.coursesByYear.put(offering.getYear(), new CoursesByYear());
				} 
				//see if we need to add a session
				if((!this.coursesByYear.get(offering.getYear()).getCoursesBySession().containsKey(offering.getSession()))){
					//add the new session
					this.coursesByYear.get(offering.getYear()).getCoursesBySession().put(offering.getSession(), new CoursesBySession());
				}
				//add the course to the session
				this.coursesByYear.get(offering.getYear()).getCoursesBySession().get(offering.getSession()).getCourses().add(course);
			}
		}
	}

	public Map<String,CoursesByYear> getCoursesByYear() {
		if(this.coursesByYear == null){
			this.coursesByYear = new HashMap<String,CoursesByYear>();
		}
		return coursesByYear;
	}

	public void setCoursesByYear(Map<String,CoursesByYear> coursesByYear) {
		this.coursesByYear = coursesByYear;
	}
}
