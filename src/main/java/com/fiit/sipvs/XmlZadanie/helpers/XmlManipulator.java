package com.fiit.sipvs.XmlZadanie.helpers;

import java.io.*;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.fiit.sipvs.XmlZadanie.model.Course;
import com.fiit.sipvs.XmlZadanie.model.Student;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert.AlertType;

import org.w3c.dom.Document;

public class XmlManipulator {
	
	public static String path = null;
	
	private String xml;
	private String xslt;
	
	public void validateAgainstXSD(Button bt)
	{
		if (path == null) {
			path = getClass().getClassLoader().getResource("xml/sample.xml").getPath();
		}
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

	        Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("INFORMATION");
			alert.setHeaderText("XSD SCHEMA VALIDATION");
			alert.setContentText("XML IS VALID");
			alert.showAndWait();

	    }
	    catch(Exception e)
	    {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("XSD SCHEMA VALIDATION");
			alert.setContentText("ERROR IN XMLr");
			alert.showAndWait();
	    }
	}

	public static void addTimestamp() {

		byte[] data = new byte[20]; // TODO replace with signed XML

		byte request[] = null;
		String result = null;
		TimeStampResponse response = null;

		// request
		TimeStampRequestGenerator requestGenerator = new TimeStampRequestGenerator();
		TimeStampRequest timeStampRequest = requestGenerator.generate(TSPAlgorithms.SHA256, data);
		try {
			request = timeStampRequest.getEncoded();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// connection
		OutputStream out;
		HttpURLConnection con;
		try {
			URL url = new URL("http://test.ditec.sk/timestampws/TS.aspx");
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "application/timestamp-query");
			con.setRequestProperty("Content-length", String.valueOf(request.length));
			out = con.getOutputStream();
			out.write(request);
			out.flush();
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("HTTP Error: " + con.getResponseCode() + " - " + con.getResponseMessage());
			}
			InputStream in = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			result = builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// response
		String timestampToken = null;
		try {
			response = new TimeStampResponse(Base64.getDecoder().decode(result));
			timestampToken = new String(Base64.getEncoder().encode(response.getTimeStampToken().getEncoded()));
		}  catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(timestampToken);
	}

	static String ns(String s) {
		return "tt:" + s;
	}
	
	public static void generateXml(Course course, ObservableList<Student> students, String path) {

		  try {
		  	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element application = doc.createElement(ns("Application"));
			  application.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			  application.setAttribute("xsi:schemaLocation", "http://www.example.org/sipvs schema.xsd");
			  application.setAttribute("xmlns:tt", "http://www.example.org/sipvs");
			doc.appendChild(application);

			// shorten way
			// staff.setAttribute("id", "1");

			Element courseTitle = doc.createElement(ns("CourseTitle"));
			courseTitle.setTextContent(course.getCourseTitle());
			application.appendChild(courseTitle);

			Element courseRoom = doc.createElement(ns("CourseRoom"));
			courseRoom.setTextContent(course.getCourseRoom());
			application.appendChild(courseRoom);

			Element courseDate = doc.createElement(ns("CourseDate"));
			courseDate.setTextContent(course.getCourseDate());
			application.appendChild(courseDate);
			
			Element courseTime = doc.createElement(ns("CourseTime"));
			courseTime.setTextContent(course.getCourseTime());
			application.appendChild(courseTime);

			Element courseNewbie = doc.createElement(ns("CourseNewbie"));
			courseNewbie.setTextContent(course.getCourseNewbie().toString());
			application.appendChild(courseNewbie);

			Element courseLessons = doc.createElement(ns("CourseLessons"));
			courseLessons.setTextContent(course.getCourseLessons().toString());
			application.appendChild(courseLessons);
			
			Element studentsElement = doc.createElement(ns("Students"));
			//studentsElement.setTextContent(course.getCourseLessons().toString());
			application.appendChild(studentsElement);
			
			int i = 1 ;
			for (Student student : students) {
				Element studentElement = doc.createElement(ns("Student"));
				//studentsElement.setTextContent(course.getCourseLessons().toString());
				
				studentElement.setAttribute("id", String.valueOf(i));
				i++;
				
				Element studentFirstName = doc.createElement(ns("StudentFirstName"));
				studentFirstName.setTextContent(student.getFirstName());
				studentElement.appendChild(studentFirstName);
				
				Element studentLastName = doc.createElement(ns("StudentLastName"));
				studentLastName.setTextContent(student.getLastName());
				studentElement.appendChild(studentLastName);
				
				Element studentMobile = doc.createElement(ns("StudentMobile"));
				studentMobile.setTextContent(student.getMobile());
				studentElement.appendChild(studentMobile);
				
				studentsElement.appendChild(studentElement);
				
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			StreamResult result = new StreamResult(path);
			XmlManipulator.path = path;
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
	
	public void xslTransform(String saveFilePath) {

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Source xsl = new StreamSource(getClass().getClassLoader().getResource("xml/stylesheet.xsl").getPath());
			Source xml = new StreamSource(getClass().getClassLoader().getResource("xml/sample2.xml").getPath()); 
			
			OutputStream outputStream = new FileOutputStream(saveFilePath);
			Transformer transformer = transformerFactory.newTransformer(xsl);
			transformer.transform(xml, new StreamResult(outputStream));
			outputStream.close();
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	
	public void xmlSigner(final BorderPane bp) {
	 
	
		Thread one = new Thread() {
		    public void run() {
		        try {
		        	String[] listOfXmls = new String[2];
		        	listOfXmls[0] = getClass().getClassLoader().getResource("xml/sample.xml").getPath();
		        	listOfXmls[1] = getClass().getClassLoader().getResource("xml/sample2.xml").getPath();
		        	
		        	
		    		XadesSigner xades = new XadesSigner(bp, listOfXmls, getClass().getClassLoader().getResource("xml/schema.xsd").getPath(), getClass().getClassLoader().getResource("xml/stylesheet.xsl").getPath(),getClass().getClassLoader().getResource("xml").getPath());
		    		xades.sign();
		        } catch(IOException v) {
		            System.out.println(v);
		        }
		    }  
		};

		one.start();
	
	
	
	
	
	}
}
