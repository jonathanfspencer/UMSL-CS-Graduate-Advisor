package edu.umsl.cs.group4.services.schedule.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="schedule")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class SimpleSchedule {

	@XmlElement(name="scheduled_course")
	public List<ScheduledCourse> scheduledCourse;
	

	public class ScheduledCourse {
		@XmlAttribute public String term;
		@XmlAttribute public String year;
		@XmlAttribute public List<Session> session;
		
		public class Session {
			@XmlAttribute(name="session_name") public String sessionName;
			@XmlAttribute public List<Course> course;
			
			public class Course {
				@XmlAttribute public String subject;
				@XmlAttribute(name="course_number") public String courseNumber;
				@XmlAttribute(name="course_name") public String courseName;
				@XmlAttribute(name="catalog_number") public String catalogNumber;
				
			}			
		}		
	}	
	
}
