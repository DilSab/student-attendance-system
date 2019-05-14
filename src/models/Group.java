package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Group implements InterfaceGroup<Student> {
	private ObservableList<Student> studentList = FXCollections.observableArrayList();

	private SimpleStringProperty groupName;

	public Group() {
	};

	public Group(String groupName) {
		this.groupName = new SimpleStringProperty(groupName);
	}

	public String getGroupName() {
		return groupName.get();
	}

	@Override
	public ObservableList<Student> getStudents() {
		return studentList;
	}

	@Override
	public void addStudent(Student s) {
		studentList.add(s);

	}

	@Override
	public Student getStudent(String StudentId) {
		for (Student student : studentList) {
			if (student.getStudentId().equals(StudentId)) {
				return student;
			}
		}
		return null;
	}

	public void setGroupName(String newValue) {
		SimpleStringProperty newGroupName = new SimpleStringProperty(newValue);
		this.groupName = newGroupName;
	}
}
