package edu.umsl.cs.group4.services.descriptions.service;

import java.net.MalformedURLException;
import java.net.URL;

public class DescriptionsServiceImpl implements DescriptionsService {


	@Override
	public URL getDescriptionsURL() {
		URL url = null;
		try {
			url = new URL("http://comp.umsl.edu/~xml_data/Courses.xml");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}



}
