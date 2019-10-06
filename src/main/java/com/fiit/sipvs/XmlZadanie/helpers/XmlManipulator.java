package com.fiit.sipvs.XmlZadanie.helpers;

import java.io.InputStream;


import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.fiit.sipvs.XmlZadanie.model.Course;
import com.fiit.sipvs.XmlZadanie.model.Student;

import javafx.collections.ObservableList;

import org.w3c.dom.Document;

public class XmlManipulator {
	
	public static String path = "";
	
	public void validateAgainstXSD()
	{
		File file1 = new File(path);
		File file2 = new File(getClass().getClassLoader().getResource("xml/schema.xsd").getPath());

		InputStream xml = null;
		InputStream xsd = null;
		try {
			xml = new FileInputStream(file1);
			xsd = new FileInputStream(file2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		    
		  
		  
		
	    try
	    {
	        SchemaFactory factory = 
	            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema(new StreamSource(xsd));
	        Validator validator = schema.newValidator();
	        validator.validate(new StreamSource(xml));
	        System.out.println("OK");
	        
	    }
	    catch(Exception e)
	    {
			e.printStackTrace();
	        System.out.println("ERROR");
	    }
	}
	
	public static void generateXml(Course course, ObservableList<Student> students, String path) {

		  try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Aplications");
			doc.appendChild(rootElement);

			Element aplication = doc.createElement("Aplication");
			rootElement.appendChild(aplication);

			// shorten way
			// staff.setAttribute("id", "1");

			Element courseTitle = doc.createElement("CourseTitle");
			courseTitle.setTextContent(course.getCourseTitle());
			aplication.appendChild(courseTitle);

			Element courseRoom = doc.createElement("CourseRoom");
			courseRoom.setTextContent(course.getCourseRoom());
			aplication.appendChild(courseRoom);

			Element courseDate = doc.createElement("CourseDate");
			courseDate.setTextContent(course.getCourseDate());
			aplication.appendChild(courseDate);

			Element courseNewbie = doc.createElement("CourseNewbie");
			courseNewbie.setTextContent(course.getCourseNewbie().toString());
			aplication.appendChild(courseNewbie);

			Element courseLessons = doc.createElement("CourseLessons");
			courseLessons.setTextContent(course.getCourseLessons().toString());
			aplication.appendChild(courseLessons);
			
			Element studentsElement = doc.createElement("Students");
			//studentsElement.setTextContent(course.getCourseLessons().toString());
			aplication.appendChild(studentsElement);
			
			int i = 1 ;
			for (Student student : students) {
				Element studentElement = doc.createElement("Student");
				//studentsElement.setTextContent(course.getCourseLessons().toString());
				
				studentElement.setAttribute("id", String.valueOf(i));
				i++;
				
				Element studentFirstName = doc.createElement("StudentFirstName");
				studentFirstName.setTextContent(student.getFirstName());
				studentElement.appendChild(studentFirstName);
				
				Element studentLastName = doc.createElement("StudentLastName");
				studentLastName.setTextContent(student.getLastName());
				studentElement.appendChild(studentLastName);
				
				Element studentMobile = doc.createElement("StudentMobile");
				studentMobile.setTextContent(student.getMobile());
				studentElement.appendChild(studentMobile);
				
				studentsElement.appendChild(studentElement);
				
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			StreamResult result = new StreamResult(path);
			XmlManipulator.path= path;
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
		}

}
