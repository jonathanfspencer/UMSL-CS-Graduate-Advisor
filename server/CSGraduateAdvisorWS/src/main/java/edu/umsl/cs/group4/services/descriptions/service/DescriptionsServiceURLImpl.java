package edu.umsl.cs.group4.services.descriptions.service;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class DescriptionsServiceURLImpl implements DescriptionsService {

	private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Courses.xml";
	
	@Override
	public Source getDescriptionsSource() {
		return new StreamSource(SOURCE_URL);
	}

}
