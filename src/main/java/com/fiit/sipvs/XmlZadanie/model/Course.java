package com.fiit.sipvs.XmlZadanie.model;

public class Course {

	private String courseTitle;
	private String courseRoom;
	private Integer courseLessons;
	private Boolean courseNewbie;
	private String courseDate;
	private String courseTime;
	
	public Course(String courseTitle, String courseRoom, Integer courseLessons, Boolean courseNewbie,
			String courseDate, String courseTime) {
		this.courseTitle = courseTitle;
		this.courseRoom = courseRoom;
		this.courseLessons = courseLessons;
		this.courseNewbie = courseNewbie;
		this.courseDate = courseDate;
		this.courseTime = courseTime;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public String getCourseRoom() {
		return courseRoom;
	}

	public void setCourseRoom(String courseRoom) {
		this.courseRoom = courseRoom;
	}

	public Integer getCourseLessons() {
		return courseLessons;
	}

	public void setCourseLessons(Integer courseLessons) {
		this.courseLessons = courseLessons;
	}

	public Boolean getCourseNewbie() {
		return courseNewbie;
	}

	public void setCourseNewbie(Boolean courseNewbie) {
		this.courseNewbie = courseNewbie;
	}

	public String getCourseDate() {
		return courseDate;
	}

	public void setCourseDate(String courseDate) {
		courseDate = courseDate;
	}

	public String getCourseTime() {
		return courseTime;
	}

	public void setCourseTime(String courseTime) {
		this.courseTime = courseTime;
	}
	
	
	
	

}
