package com.fiit.sipvs.XmlZadanie.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.util.StringConverter;

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
	Button signXml;

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
	
	private XmlManipulator xmlManipulator = new XmlManipulator();
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		Init();
	}

	private void Init() {
	    datePicker.setPromptText("yyyy-MM-dd");
	    
	    StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                      DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };   
        datePicker.setConverter(converter);
	    
		addStudentButton.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				addStudentToCourse();

			}

		});
		
		validateXml.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				xmlManipulator.validateAgainstXSD(generateXsl);
				
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
							"*.xml");
					fileChooser.getExtensionFilters().add(extFilter);

					// Show save file dialog
					File file = fileChooser.showSaveDialog(mainBorderPane.getScene().getWindow());

					if (file != null) {
						XmlManipulator.generateXml(c, students, file.getAbsolutePath());
						validateXml.setDisable(false);
						signXml.setDisable(false);
					}
					
					
					
				}catch(Exception e) {
					e.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR");
					alert.setHeaderText("CAREFULL");
					alert.setContentText("Check Date Format, Time Format. Also Course Lessons must be integer");

					alert.showAndWait();
				}		
			}

		});
		
		generateXsl.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML files (*.html)",
						"*.html");
				fileChooser.getExtensionFilters().add(extFilter);

				// Show save file dialog
				File file = fileChooser.showSaveDialog(mainBorderPane.getScene().getWindow());

				if (file != null) {
					xmlManipulator.xslTransform(file.getAbsolutePath());
				}

			}

		});
		
		signXml.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				
				xmlManipulator.xmlSigner(mainBorderPane);

			}

		});
		
		
		
		firstNameColumn.setCellValueFactory(new PropertyValueFactory<Student,String>("firstName"));
		lastNameColumn.setCellValueFactory(new PropertyValueFactory<Student,String>("lastName"));
		mobileColumn.setCellValueFactory(new PropertyValueFactory<Student,String>("mobile"));
		
		table.setItems(students);
		
		courseTime.setText("21:00:00");
		
		validateXml.setDisable(true);
		generateXsl.setDisable(true);
		signXml.setDisable(true);
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

}
