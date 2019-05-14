package models;

import javafx.collections.ObservableList;

public interface InterfaceGroup<T> {
	public ObservableList<T> getStudents();

	public void addStudent(T s);

	public T getStudent(String studentId);

	public void setGroupName(String newValue);
}
