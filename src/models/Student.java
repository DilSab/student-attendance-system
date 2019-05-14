package models;

import java.time.LocalDate;
import java.util.HashMap;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

public class Student {
	private SimpleStringProperty studentId;
	private SimpleStringProperty firstName;
	private SimpleStringProperty lastName;
	private HashMap<LocalDate, Boolean> attendanceData = new HashMap<LocalDate, Boolean>();
	private CheckBox select = new CheckBox();
	private SimpleIntegerProperty timesAbsent;

	public Student(String studentId, String firstName, String lastName) {
		super();
		this.studentId = new SimpleStringProperty(studentId);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		select.setVisible(false);
	}

	public HashMap<LocalDate, Boolean> getAttendanceData() {
		return attendanceData;
	}

	public String getStudentId() {
		return studentId.get();
	}

	public String getFirstName() {
		return firstName.get();
	}

	public String getLastName() {
		return lastName.get();
	}

	public Integer getTimesAbsent() {
		return timesAbsent.get();
	}

	public void setTimesAbsent(SimpleIntegerProperty timesAbsent) {
		this.timesAbsent = timesAbsent;
	}

	public void setFirstName(SimpleStringProperty firstName) {
		this.firstName = firstName;
	}

	public void setLastName(SimpleStringProperty lastName) {
		this.lastName = lastName;
	}

	public CheckBox getSelect() {
		return select;
	}

	public void setSelect(CheckBox select) {
		this.select = select;
	}

	public void addNewData(LocalDate localDate, boolean value) {
		attendanceData.put(localDate, value);
	}

	public String toStringWithSemicolon() {
		return getStudentId() + ";" + getFirstName() + ";" + getLastName();
	}

	public void setFirstName(String newValue) {
		SimpleStringProperty newFirstName = new SimpleStringProperty(newValue);
		this.firstName = newFirstName;
	}

	public void setLastName(String newValue) {
		SimpleStringProperty newlastName = new SimpleStringProperty(newValue);
		this.lastName = newlastName;
	}

	public void setStudentId(String newValue) {
		SimpleStringProperty newStudentId = new SimpleStringProperty(newValue);
		this.studentId = newStudentId;
	}
}
