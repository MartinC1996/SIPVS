package com.fiit.sipvs.XmlZadanie.helpers;

import com.fiit.sipvs.XmlZadanie.model.Course;
import com.fiit.sipvs.XmlZadanie.model.Student;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class XmlManipulator {

    public static String path = null;

    private String xml;
    private String xslt;

    public void validateAgainstXSD(Button bt) {
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


        try {
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

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("XSD SCHEMA VALIDATION");
            alert.setContentText("ERROR IN XMLr");
            alert.showAndWait();
        }
    }

    private Element newXmlElements(Document document, String timestampToken) {

        Element encapsulatedTimeStampElement = document.createElement("xades:EncapsulatedTimeStamp");
        Element signatureTimestampElement = document.createElement("xades:SignatureTimeStamp");
        Element unsignedSignaturePropertiesElement = document.createElement("xades:UnsignedSignatureProperties");
        Element unsignedPropertiesElement = document.createElement("xades:UnsignedProperties");

        unsignedPropertiesElement.appendChild(unsignedSignaturePropertiesElement);
        unsignedSignaturePropertiesElement.appendChild(signatureTimestampElement);
        signatureTimestampElement.appendChild(encapsulatedTimeStampElement);
        Text timestampNode = document.createTextNode(timestampToken);
        encapsulatedTimeStampElement.appendChild(timestampNode);
        return unsignedPropertiesElement;
    }

    public String addTimeStamp(byte[] data) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestampToken;
    }

    private StreamResult addToDocument(String signedXml) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(signedXml)));

            Node signatureValue = document.getElementsByTagName("ds:SignatureValue").item(0);
            if (signatureValue == null) {
                System.err.println("SignatureValue is missing");
            }

            String timeStamp = addTimeStamp(Base64.getEncoder().encodeToString(signatureValue.getTextContent().getBytes()).getBytes());

            Node qualifyingProperties = document.getElementsByTagName("xades:QualifyingProperties").item(0);
            if (qualifyingProperties == null) {
                System.err.println("QualifyingProperties is missing");
            }

            qualifyingProperties.appendChild(newXmlElements(document, timeStamp));


            DOMSource source = new DOMSource(document);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter outWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(outWriter);
            transformer.transform(source, streamResult);
            return streamResult;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void saveXmlWithTS() {
        String signedXml = null;
        try {
            signedXml = new String(Files.readAllBytes(Paths.get("C:\\kvalita\\SIPVS\\SIPVS\\src\\main\\resource\\xml\\SignedNew.xml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintStream out = new PrintStream(new FileOutputStream("C:\\kvalita\\SIPVS\\SIPVS\\src\\main\\resource\\xml\\SignedNewTS.xml"))) {
            out.print(addToDocument(signedXml).getWriter());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

            int i = 1;
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
        } catch (Exception e) {
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


                    XadesSigner xades = new XadesSigner(bp, listOfXmls, getClass().getClassLoader().getResource("xml/schema.xsd").getPath(), getClass().getClassLoader().getResource("xml/stylesheet.xsl").getPath(), getClass().getClassLoader().getResource("xml").getPath());
                    xades.sign();
                } catch (IOException v) {
                    System.out.println(v);
                }
            }
        };

        one.start();


    }
}