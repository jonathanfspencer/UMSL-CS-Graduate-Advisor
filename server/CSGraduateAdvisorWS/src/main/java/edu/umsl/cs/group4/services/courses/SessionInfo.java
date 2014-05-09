package edu.umsl.cs.group4.services.courses;

import java.util.Calendar;

public class SessionInfo {

	private String session;
	private int year;
	
	public SessionInfo(int year, String session) {
		super();
		this.year = year;
		this.session = session;
	}
	
	public static SessionInfo getNextSessionInfo() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		String nextSessionName;
		int nextSessionYear;
		
		if(month < 6) {
			nextSessionName = "Summer";
			nextSessionYear = year;
		} else if (month < 9) {
			nextSessionName = "Fall";
			nextSessionYear = year;
		} else {
			nextSessionName = "Spring";
			nextSessionYear = ++year;
		}
		
		SessionInfo nextSessionInfo = new SessionInfo(nextSessionYear, nextSessionName);
		return nextSessionInfo;
	}
	
	public static SessionInfo getCurrentSessionInfo() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		String sessionName;
		
		if(month < 6) {
			sessionName = "Spring";
		} else if (month < 9) {
			sessionName = "Summer";
		} else {
			sessionName = "Fall";
		}
		
		SessionInfo sessionInfo = new SessionInfo(year, sessionName);
		return sessionInfo;
	}
	
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	

}
