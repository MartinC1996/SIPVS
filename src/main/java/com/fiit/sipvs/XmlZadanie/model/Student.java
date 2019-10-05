package com.fiit.sipvs.XmlZadanie.model;

import javafx.beans.property.SimpleStringProperty;

public class Student {

	private SimpleStringProperty  firstName;
	private SimpleStringProperty  lastName;
	private SimpleStringProperty  mobile;
	
	public String getFirstName() {
		return firstName.get();
	}
	

	public String getLastName() {
		return lastName.get();
	}
	

	
	public String getMobile() {
		return mobile.get();
	}
	

	
	public Student(String firstName, String lastName, String mobile) {
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.mobile = new SimpleStringProperty(mobile);
	}
	
	
	
}
