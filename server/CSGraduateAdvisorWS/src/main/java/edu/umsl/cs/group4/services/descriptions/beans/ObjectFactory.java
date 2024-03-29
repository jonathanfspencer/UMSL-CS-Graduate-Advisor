//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.27 at 10:03:03 PM CST 
//


package edu.umsl.cs.group4.services.descriptions.beans;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the edu.umsl.cs.group4.services.descriptions.beans package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DescriptionsCoursePrerequisiteOrChoiceAndRequired_QNAME = new QName("", "and_required");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.umsl.cs.group4.services.descriptions.beans
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Descriptions }
     * 
     */
    public Descriptions createDescriptions() {
        return new Descriptions();
    }

    /**
     * Create an instance of {@link Descriptions.Course }
     * 
     */
    public Descriptions.Course createDescriptionsCourse() {
        return new Descriptions.Course();
    }

    /**
     * Create an instance of {@link Descriptions.Course.Prerequisite }
     * 
     */
    public Descriptions.Course.Prerequisite createDescriptionsCoursePrerequisite() {
        return new Descriptions.Course.Prerequisite();
    }

    /**
     * Create an instance of {@link Descriptions.Course.Prerequisite.OrChoice }
     * 
     */
    public Descriptions.Course.Prerequisite.OrChoice createDescriptionsCoursePrerequisiteOrChoice() {
        return new Descriptions.Course.Prerequisite.OrChoice();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@XmlElementDecl(namespace = "", name = "and_required", scope = Descriptions.Course.Prerequisite.OrChoice.class)
    public JAXBElement<List<String>> createDescriptionsCoursePrerequisiteOrChoiceAndRequired(List<String> value) {
        return new JAXBElement<List<String>>(_DescriptionsCoursePrerequisiteOrChoiceAndRequired_QNAME, ((Class) List.class), Descriptions.Course.Prerequisite.OrChoice.class, ((List<String> ) value));
    }

}
