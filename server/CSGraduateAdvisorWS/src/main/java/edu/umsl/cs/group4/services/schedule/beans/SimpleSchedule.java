package edu.umsl.cs.group4.services.schedule.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="schedule")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class SimpleSchedule {

	public SimpleSchedule(){
//		this.scheduledCourse = new ArrayList<ScheduledCourse>();
	}
	
	protected List<ScheduledCourse> scheduledCourse;
	

	public static class ScheduledCourse {
		protected String term;
		protected String year;
		protected List<Session> session;
		
		public ScheduledCourse(){
			this.session = new ArrayList<Session>();
		}
		
		public static class Session {
			protected String sessionName;
			protected List<Course> course;
			
			public Session(){
//				this.course = new ArrayList<Course>();
			}
			
			public static class Course {
				protected String subject;
				protected String courseNumber;
				protected String courseName;
				protected String catalogNumber;
				
				public Course(){
					
				}

				@XmlElement
				public String getSubject() {
					return subject;
				}

				public void setSubject(String subject) {
					this.subject = subject;
				}

				@XmlElement(name="course_number") 
				public String getCourseNumber() {
					return courseNumber;
				}

				public void setCourseNumber(String courseNumber) {
					this.courseNumber = courseNumber;
				}

				@XmlElement(name="course_name") 
				public String getCourseName() {
					return courseName;
				}

				public void setCourseName(String courseName) {
					this.courseName = courseName;
				}

				@XmlElement(name="catalog_number") 
				public String getCatalogNumber() {
					return catalogNumber;
				}

				public void setCatalogNumber(String catalogNumber) {
					this.catalogNumber = catalogNumber;
				}
			}

			@XmlElement(name="session_name") 
			public String getSessionName() {
				return sessionName;
			}

			public void setSessionName(String sessionName) {
				this.sessionName = sessionName;
			}

			@XmlElement 
			public List<Course> getCourse() {
				return course;
			}

			public void setCourse(List<Course> course) {
				this.course = course;
			}			
		}

		@XmlElement 
		public String getTerm() {
			return term;
		}

		public void setTerm(String term) {
			this.term = term;
		}

		@XmlElement 
		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}

		@XmlElement 
		public List<Session> getSession() {
			return session;
		}

		public void setSession(List<Session> session) {
			this.session = session;
		}		
	}

	@XmlElement(name="scheduled_course")
	public List<ScheduledCourse> getScheduledCourse() {
		return scheduledCourse;
	}


	public void setScheduledCourse(List<ScheduledCourse> scheduledCourse) {
		this.scheduledCourse = scheduledCourse;
	}	
	
}
