package edu.umsl.cs.group4.services.descriptions.service;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class DescriptionsServiceFileImpl implements DescriptionsService {

	@Override
	public Source getDescriptionsSource() {
		Source source = null;
		File sourceFile = new File("Courses.xml");
		source = new StreamSource(sourceFile); 
		return source;
	}



}
