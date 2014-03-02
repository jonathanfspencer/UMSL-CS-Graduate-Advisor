package edu.umsl.cs.group4.services.descriptions.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class DescriptionsServiceURLImpl implements DescriptionsService {

	private static final String SOURCE_URL = "http://comp.umsl.edu/~xml_data/Courses.xml";
	
	@Override
	public Source getDescriptionsSource() {
		return new StreamSource(SOURCE_URL);
	}

	@Override
	public String getDescriptionsString() {
		String out = null;
		Scanner scanner = null;
		try {
			scanner = new Scanner(new URL(SOURCE_URL).openStream(), "UTF-8");
			out = scanner.useDelimiter("\\A").next();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		return out;
	}

	@Override
	public File getDescriptionsFile() {
		// TODO Auto-generated method stub
		return null;
	}

}
