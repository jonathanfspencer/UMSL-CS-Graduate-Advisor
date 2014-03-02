package edu.umsl.cs.group4.services.descriptions.service;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class DescriptionsServiceFileImpl implements DescriptionsService {

	private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Courses.xml";
	
	@Override
	public Source getDescriptionsSource() {
		Source source = null;
		File file = getDescriptionsFile();
		source = new StreamSource(file); 
		return source;
	}

	@Override
	public String getDescriptionsString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public File getDescriptionsFile() {
		File file = new File(SOURCE_URL);
		return file;
	}



}
