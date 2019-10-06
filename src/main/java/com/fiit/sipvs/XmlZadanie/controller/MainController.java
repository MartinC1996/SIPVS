package com.fiit.sipvs.XmlZadanie.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import com.fiit.sipvs.XmlZadanie.helpers.XmlManipulator;
import com.fiit.sipvs.XmlZadanie.model.Course;
import com.fiit.sipvs.XmlZadanie.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

public class MainController implements Initializable {
	
	@FXML
	BorderPane mainBorderPane;
	
	@FXML
	Button addStudentButton;
	
	@FXML
	Button saveXml;
	
	@FXML
	Button validateXml;
	
	@FXML
	Button generateXsl;

	@FXML
	DatePicker datePicker;
	
	@FXML
	TextField courseTime;
	
	@FXML
	TextField courseTitle;
	
	@FXML
	TextField courseRoom;
	
	@FXML
	TextField courseName;
	
	@FXML
	TextField courseLessons;
	
	@FXML
	TextField studentFirstName;
	
	@FXML
	TextField studentLastName;
	
	@FXML
	TextField studentMobile;
	
	@FXML
	CheckBox courseNewbie;
	
	@FXML
	TableView table;
	@FXML
	TableColumn firstNameColumn;
	@FXML
	TableColumn lastNameColumn;
	@FXML
	TableColumn mobileColumn; 
	
	private  ObservableList<Student> students =
	        FXCollections.observableArrayList(
	    
	        );   
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		Init();
	}

	private void Init() {
		addStudentButton.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				addStudentToCourse();

			}

		});
		
		validateXml.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				XmlManipulator xml = new XmlManipulator();
				xml.validateAgainstXSD();

			}

		});
		
		saveXml.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				
				try{
					String ct = courseTitle.getText();
					String cr = courseRoom.getText();
					Integer cl = Integer.parseInt(courseLessons.getText());
					Boolean cn = courseNewbie.isSelected();
					String cd = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					String ctt = courseTime.getText();
					
					System.out.println(ct);
					System.out.println(cr);
					System.out.println(cl);
					System.out.println(cn);
					System.out.println(cd);
					System.out.println(ctt);
					
					Course c = new Course(ct, cr, cl, cn, cd, ctt); 
					
					FileChooser fileChooser = new FileChooser();
					FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)",
							"*.txt");
					fileChooser.getExtensionFilters().add(extFilter);

					// Show save file dialog
					File file = fileChooser.showSaveDialog(mainBorderPane.getScene().getWindow());

					if (file != null) {
						XmlManipulator.generateXml(c, students, file.getAbsolutePath());
					}
					
					
					
				}catch(Exception e) {
					e.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR");
					alert.setHeaderText("IDIOT");
					alert.setContentText("YOU FUCKED UP");

					alert.showAndWait();
				}		
			}

		});
		
		generateXsl.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				xslTransform();

			}

		});
		
		
		
		firstNameColumn.setCellValueFactory(new PropertyValueFactory<Student,String>("firstName"));
		lastNameColumn.setCellValueFactory(new PropertyValueFactory<Student,String>("lastName"));
		mobileColumn.setCellValueFactory(new PropertyValueFactory<Student,String>("mobile"));
		
		table.setItems(students);
		
		courseTime.setText("HH:MM");
	}
	
	
	private void addStudentToCourse() {
		String firstName = studentFirstName.getText();
		String lastName = studentLastName.getText();
		String mobile = studentMobile.getText();
		

		
	
		Student s= new Student(firstName, lastName, mobile);
		System.out.println(firstName);
		System.out.println(lastName);
		System.out.println(mobile);
		students.add(s);
		
		
	}

	private void xslTransform() {

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Source xsl = new StreamSource(getClass().getClassLoader().getResource("xml/stylesheet.xsl").getPath());
			Source xml = new StreamSource(getClass().getClassLoader().getResource("xml/export.xml").getPath()); // TODO replace with XmlManipulator.path
			String html = "D:\\Users\\Pc\\Desktop\\out.html"; // TODO change
			OutputStream outputStream = new FileOutputStream(html);
			Transformer transformer = transformerFactory.newTransformer(xsl);
			transformer.transform(xml, new StreamResult(outputStream));
			outputStream.close();
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
