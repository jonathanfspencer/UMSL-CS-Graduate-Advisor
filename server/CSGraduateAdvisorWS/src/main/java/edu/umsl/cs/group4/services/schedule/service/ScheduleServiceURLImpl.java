package edu.umsl.cs.group4.services.schedule.service;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class ScheduleServiceURLImpl implements ScheduleService {

private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Schedule.xml";
	
	@Override
	public Source getScheduleSource() {
		return new StreamSource(SOURCE_URL);
	}

}
