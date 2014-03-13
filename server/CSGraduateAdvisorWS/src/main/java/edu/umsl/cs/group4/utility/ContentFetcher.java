package edu.umsl.cs.group4.utility;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class ContentFetcher {
	
	public static Source fetchContentSource(String contentURL){
		return new StreamSource(contentURL);
	}
	
	public static Object fetchContent(String contentURL, Class<?> contentClass) throws JAXBException{
		JAXBContext context = JAXBContext.newInstance(contentClass);
		Unmarshaller um = context.createUnmarshaller();
		Object object = um.unmarshal(fetchContentSource(contentURL));
		return contentClass.cast(object);
	}
}
