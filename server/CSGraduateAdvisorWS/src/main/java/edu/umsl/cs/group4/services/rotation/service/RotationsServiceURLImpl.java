package edu.umsl.cs.group4.services.rotation.service;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class RotationsServiceURLImpl implements RotationsService {

private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Rotation.xml";
	
	@Override
	public Source getRotationsSource() {
		return new StreamSource(SOURCE_URL);
	}

}
