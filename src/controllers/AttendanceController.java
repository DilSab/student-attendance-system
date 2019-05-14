package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Student;

public class AttendanceController implements Initializable {
	private ObservableList<Student> filteredList = FXCollections.observableArrayList();
	private ObservableList<String> datesList = FXCollections.observableArrayList();
	private LocalDate startDate;
	private LocalDate endDate;

	@FXML
	private TableView<String> tableDates;

	@FXML
	private TableColumn<String, String> colDates;

	@FXML
	private TableView<Student> tableFilter;

	@FXML
	private TableColumn<Student, String> colStudentId;

	@FXML
	private TableColumn<Student, String> colFirstName;

	@FXML
	private TableColumn<Student, String> colLastName;

	@FXML
	private TableColumn<Student, Integer> colTimesAbsent;

	@FXML
	private TextField fieldDatesName;

	@FXML
	private Button buttonSaveToPdf;
	
	@FXML
	private CheckBox checkBoxWithDates;

	@FXML
	void buttonPressedSaveToPdf(ActionEvent event) throws FileNotFoundException, DocumentException {
		Stage stage = new Stage();

		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
		fileChooser.getExtensionFilters().add(extensionFilter);

		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			saveToPdf(file);
		}
	}

	private void saveToPdf(File file) throws FileNotFoundException, DocumentException {
		Document document = new Document();

		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		document.add(new Paragraph("From " + startDate.toString() + " to " + endDate.toString()));
		document.add(new Paragraph(" "));
		Chunk glue = new Chunk(new VerticalPositionMark());

		PdfPTable table = new PdfPTable(1);

		Phrase p = new Phrase();
		LocalDate date;
		if (checkBoxWithDates.isSelected()) {
			for (Student student : filteredList) {
				p.add("Student: " + student.getStudentId() + " " + student.getFirstName() + " "
						+ student.getLastName());
				p.add(glue);
				date = startDate;
				while (!date.isEqual(endDate)) {
					if (student.getAttendanceData().get(date) != null
							&& student.getAttendanceData().get(date)) {
						p.add(Chunk.NEWLINE);
						p.add(" " + date.toString());
					}
					date = date.plusDays(1);
				}
				p.add(glue);
				p.add("Times absent: " + student.getTimesAbsent());
				table.addCell(new Paragraph(p));
				p.clear();
			}
		} else {
			for (Student student : filteredList) {
				p.add("Student: " + student.getStudentId() + " " + student.getFirstName() + " "
						+ student.getLastName());
				p.add(glue);
				p.add("Times absent: " + student.getTimesAbsent());
				table.addCell(new Paragraph(p));
				p.clear();
			}
		}
		document.add(table);
		document.close();
	}

	@FXML
	void MouseReleasedShowDates(MouseEvent event) {
		if (!datesList.isEmpty()) {
			datesList.clear();
		}
		if (tableFilter.getSelectionModel().getSelectedItem() != null) {
			Student selectedStudent = tableFilter.getSelectionModel().getSelectedItem();
			fieldDatesName.setText("Student:  " + selectedStudent.getStudentId() + " " + selectedStudent.getFirstName()
					+ " " + selectedStudent.getLastName());
			LocalDate date = startDate;
			while (!date.isEqual(endDate)) {
				if (selectedStudent.getAttendanceData().get(date) != null
						&& selectedStudent.getAttendanceData().get(date)) {
					datesList.add(date.toString());
				}
				date = date.plusDays(1);
			}

		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		colStudentId.setCellValueFactory(new PropertyValueFactory<Student, String>("studentId"));
		colFirstName.setCellValueFactory(new PropertyValueFactory<Student, String>("firstName"));
		colLastName.setCellValueFactory(new PropertyValueFactory<Student, String>("lastName"));
		colTimesAbsent.setCellValueFactory(new PropertyValueFactory<Student, Integer>("timesAbsent"));
		colDates.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
		colStudentId.prefWidthProperty().bind(tableFilter.widthProperty().divide(4));
		colFirstName.prefWidthProperty().bind(tableFilter.widthProperty().divide(4));
		colLastName.prefWidthProperty().bind(tableFilter.widthProperty().divide(4));
		colTimesAbsent.prefWidthProperty().bind(tableFilter.widthProperty().divide(4));
		tableFilter.setItems(filteredList);
		tableDates.setItems(datesList);
	}

	public void setStartDate(LocalDate localDate) {
		this.startDate = localDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public void setList(ObservableList<Student> students) {
		int absent = 0;
		LocalDate date = startDate;
		for (Student student : students) {
			date = startDate;
			while (!date.isEqual(endDate)) {
				if (student.getAttendanceData().get(date) != null && student.getAttendanceData().get(date)) {
					absent += 1;
				}
				date = date.plusDays(1);
			}
			SimpleIntegerProperty simpleInt = new SimpleIntegerProperty(absent);
			student.setTimesAbsent(simpleInt);
			if (absent > 0) {
				filteredList.add(student);
			}
			absent = 0;
		}
	}
}
