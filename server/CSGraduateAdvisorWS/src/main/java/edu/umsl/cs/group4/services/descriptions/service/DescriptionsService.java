package edu.umsl.cs.group4.services.descriptions.service;

import java.io.File;

import javax.xml.transform.Source;


public interface DescriptionsService {

	public Source getDescriptionsSource();
	
	public String getDescriptionsString();
	
	public File getDescriptionsFile();
}
