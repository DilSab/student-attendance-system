package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Group;
import models.Student;
import javafx.fxml.Initializable;

public class UIController implements Initializable {
	private ObservableList<Group> groupList = FXCollections.observableArrayList();

	@FXML
	private TableView<Group> tableGroups;

	@FXML
	private TableColumn<Group, String> colGroups;

	@FXML
	private TableView<Student> tableStudents;

	@FXML
	private TableColumn<Student, String> colStudentId;

	@FXML
	private TableColumn<Student, String> colFirstName;

	@FXML
	private TableColumn<Student, String> colLastName;

	@FXML
	private TableColumn<Student, String> colAbsent;

	@FXML
	private MenuItem menuImport;

	@FXML
	private MenuItem menuExport;

	@FXML
	private MenuItem menuClose;

	@FXML
	private MenuItem menuAddGroup;

	@FXML
	private MenuItem menuAddStudent;

	@FXML
	private MenuItem menuRemoveGroup;

	@FXML
	private MenuItem menuRemoveStudent;

	@FXML
	private MenuItem menuAbout;

	@FXML
	private CheckMenuItem menuTurnOnEditing;

	@FXML
	private Button buttonSave;

	@FXML
	private Button buttonAttendance;

	@FXML
	private TextField fieldGroupName;

	@FXML
	private TextField fieldFirst;

	@FXML
	private TextField fieldLast;

	@FXML
	private DatePicker dateFrom;

	@FXML
	private DatePicker dateTo;

	@FXML
	private DatePicker datePick;

	private Stage filterStage = new Stage();

	public ObservableList<Student> getStudentsFromSelectedGroup() {
		return tableGroups.getSelectionModel().getSelectedItem().getStudents();
	}

	@FXML
	void menuPressedClose(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	void menuPressedImport(ActionEvent event) throws IOException {
		Stage stage = new Stage();

		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extensionFilter);

		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			importFromFile(file);
		}
	}

	private void importFromFile(File file) throws IOException {
		String line = "";
		String[] studentInfo;
		String groupName = "";
		int skipped = 0;

		Group foundGroup = tableGroups.getSelectionModel().getSelectedItem();

		BufferedReader reader = new BufferedReader(new FileReader(file));

		while ((line = reader.readLine()) != null) {
			if (line.startsWith(".")) {
				groupName = line.substring(1);
				if ((foundGroup = returnGroup(groupName)) == null) {
					groupList.add(new Group(groupName));
					foundGroup = returnGroup(groupName);
				}
			} else if (line.indexOf(";", (line.indexOf(";") + 1)) != -1 && foundGroup != null) {
				studentInfo = line.split(";");
				foundGroup.addStudent(new Student(studentInfo[0], studentInfo[1], studentInfo[2]));
			} else {
				skipped += 1;
			}
		}
		if (skipped > 0) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText(
					"Wrong Group/Student format in file. Groups' names start with .(dot). Students' id, first name and last name separated with semicolon (Student ID;First name;Last name). Skipped occurances: "
							+ skipped + ".");
			alert.setHeaderText("Wrong format in file");
			alert.show();
		}
		reader.close();
	}

	private Group returnGroup(String groupName) {
		for (Group group : groupList) {
			if (group.getGroupName().equals(groupName)) {
				return group;
			}
		}
		return null;
	}

	@FXML
	void menuPressedExport(ActionEvent event) throws IOException {
		Stage stage = new Stage();

		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extensionFilter);

		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			saveToFile(file);
		}
	}

	private void saveToFile(File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (Group group : groupList) {
			writer.write("." + group.getGroupName());
			writer.newLine();
			for (Student student : group.getStudents()) {
				writer.write(student.toStringWithSemicolon());
				writer.newLine();
			}
		}
		writer.close();
	}

	@FXML
	void menuPressedTurnOnEditing(ActionEvent event) {
		if (menuTurnOnEditing.isSelected()) {
			tableStudents.setEditable(true);
			tableGroups.setEditable(true);
		} else {
			tableStudents.setEditable(false);
			tableGroups.setEditable(false);
		}
	}

	@FXML
	void menuPressedAddGroup(ActionEvent event) {
		Stage addGroupStage = new Stage();
		TextField fieldGName = new TextField();
		fieldGName.setPromptText("Group name");
		Button buttonOk = new Button("Ok");
		buttonOk.setTranslateY(35);
		buttonOk.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				if (!fieldGName.getText().isEmpty()) {
					groupList.add(new Group(fieldGName.getText()));
				}
				addGroupStage.close();
			}
		});
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(fieldGName, buttonOk);
		Scene scene = new Scene(stackPane, 250, 100);
		addGroupStage.initModality(Modality.APPLICATION_MODAL);
		addGroupStage.setAlwaysOnTop(true);
		addGroupStage.setTitle("Add group");
		addGroupStage.setScene(scene);
		addGroupStage.show();
	}

	@FXML
	void menuPressedAddstudent(ActionEvent event) {
		Stage addStudentStage = new Stage();
		TextField fieldStudentId = new TextField();
		fieldStudentId.setTranslateY(-60);
		fieldStudentId.setPromptText("Student ID");
		TextField fieldFName = new TextField();
		fieldFName.setTranslateY(-30);
		fieldFName.setPromptText("First name");
		TextField fieldLName = new TextField();
		fieldLName.setPromptText("Last name");
		Button buttonOk = new Button("Ok");
		buttonOk.setTranslateY(35);
		buttonOk.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				if (!fieldStudentId.getText().isEmpty() && !fieldFName.getText().isEmpty()
						&& !fieldLName.getText().isEmpty()
						&& tableGroups.getSelectionModel().getSelectedItem() != null) {
					tableGroups.getSelectionModel().getSelectedItem().addStudent(
							new Student(fieldStudentId.getText(), fieldFName.getText(), fieldLName.getText()));
				}
				addStudentStage.close();
			}
		});
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(fieldStudentId, fieldFName, fieldLName, buttonOk);
		Scene scene = new Scene(stackPane, 250, 150);
		addStudentStage.initModality(Modality.APPLICATION_MODAL);
		addStudentStage.setAlwaysOnTop(true);
		addStudentStage.setTitle("Add student");
		addStudentStage.setScene(scene);
		addStudentStage.show();
	}

	@FXML
	void menuPressedRemoveGroup(ActionEvent event) {
		if (tableGroups.getSelectionModel().getSelectedItem() != null) {
			tableGroups.getSelectionModel().getSelectedItem().getStudents().clear();
			tableGroups.getItems().remove(tableGroups.getSelectionModel().getSelectedIndex());
			if (tableGroups.getSelectionModel().getSelectedItem() != null) {
				tableStudents.setItems(tableGroups.getSelectionModel().getSelectedItem().getStudents());
			}
		}
	}

	@FXML
	void menuPressedRemoveStudent(ActionEvent event) {
		if (tableStudents.getSelectionModel().getSelectedItem() != null) {
			tableStudents.getItems().remove(tableStudents.getSelectionModel().getSelectedIndex());
		}
	}

	@FXML
	void menuPressedAbout(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setContentText(
				"Program for registering students' attendance. Allows to import and export lists to CSV Files and save attendance during selected interval to pdf.");
		alert.setHeaderText("");
		alert.show();
	}

	@FXML
	void buttonPressedAttendance(ActionEvent event) {
		if (dateFrom.getValue() != null && dateTo.getValue() != null
				&& tableGroups.getSelectionModel().getSelectedItem() != null) {
			try {
				filterStage.close();
				FXMLLoader Loader = new FXMLLoader();
				Loader.setLocation(getClass().getResource("fxml/UIfilter.fxml"));
				try {
					Loader.load();
				} catch (IOException ex) {
					Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex);
				}
				AttendanceController filterController = Loader.getController();
				filterController.setStartDate(dateFrom.getValue());
				filterController.setEndDate(dateTo.getValue());
				filterController.setList(tableGroups.getSelectionModel().getSelectedItem().getStudents());
				Parent root = Loader.getRoot();
				filterStage.setTitle("Attendance");
				filterStage.setScene(new Scene(root));
				filterStage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	void onHiddenSetAbsentCheckBoxes(Event event) {
		if (tableGroups.getSelectionModel().getSelectedItem() != null) {
			boolean checkBoxBool = false;
			for (Student student : tableGroups.getSelectionModel().getSelectedItem().getStudents()) {
				student.getSelect().setVisible(true);
				if (student.getAttendanceData().get(datePick.getValue()) != null) {
					checkBoxBool = student.getAttendanceData().get(datePick.getValue());
					student.getSelect().setSelected(checkBoxBool);
				} else {
					student.getSelect().setSelected(false);
				}
			}
		}
	}

	@FXML
	void buttonPressedSave(ActionEvent event) {
		if (datePick.getValue() != null && tableGroups.getSelectionModel().getSelectedItem() != null) {
			for (Student student : tableGroups.getSelectionModel().getSelectedItem().getStudents()) {
				student.addNewData(datePick.getValue(), student.getSelect().isSelected());
				student.getSelect().setVisible(false);
			}
			datePick.getEditor().clear();
			datePick.setValue(null);
			for (Student student : tableGroups.getSelectionModel().getSelectedItem().getStudents()) {
				student.getSelect().setSelected(false);
			}
		}
	}

	@FXML
	void mouseReleasedShowStudents(MouseEvent event) {
		if (tableGroups.getSelectionModel().getSelectedItem() != null) {
			tableStudents.setItems(tableGroups.getSelectionModel().getSelectedItem().getStudents());
		}
	}

	@FXML
	void OnEditCommitGroupName(TableColumn.CellEditEvent<Group, String> groupCellStringEditEvent) {
		Group group = tableGroups.getSelectionModel().getSelectedItem();
		if (!groupCellStringEditEvent.getNewValue().contains("" + '.')
				&& !groupCellStringEditEvent.getNewValue().contains("" + ';')) {
			group.setGroupName(groupCellStringEditEvent.getNewValue());
		} else {
			group.setGroupName(groupCellStringEditEvent.getOldValue());
			tableGroups.refresh();
		}
	}

	@FXML
	void OnEditCommitStudentId(TableColumn.CellEditEvent<Student, String> studentCellStringEditEvent) {
		Student student = tableStudents.getSelectionModel().getSelectedItem();
		if (!studentCellStringEditEvent.getNewValue().contains("" + '.')
				&& !studentCellStringEditEvent.getNewValue().contains("" + ';')) {
			student.setStudentId(studentCellStringEditEvent.getNewValue());
		} else {
			student.setStudentId(studentCellStringEditEvent.getOldValue());
			tableStudents.refresh();
		}
	}

	@FXML
	void OnEditCommitFirstName(TableColumn.CellEditEvent<Student, String> studentCellStringEditEvent) {
		Student student = tableStudents.getSelectionModel().getSelectedItem();
		if (!studentCellStringEditEvent.getNewValue().contains("" + '.')
				&& !studentCellStringEditEvent.getNewValue().contains("" + ';')) {
			student.setFirstName(studentCellStringEditEvent.getNewValue());
		} else {
			student.setFirstName(studentCellStringEditEvent.getOldValue());
			tableStudents.refresh();
		}
	}

	@FXML
	void OnEditCommitLastName(TableColumn.CellEditEvent<Student, String> studentCellStringEditEvent) {
		Student student = tableStudents.getSelectionModel().getSelectedItem();
		if (!studentCellStringEditEvent.getNewValue().contains("" + '.')
				&& !studentCellStringEditEvent.getNewValue().contains("" + ';')) {
			student.setLastName(studentCellStringEditEvent.getNewValue());
		} else {
			student.setLastName(studentCellStringEditEvent.getOldValue());
			tableStudents.refresh();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		colStudentId.setCellValueFactory(new PropertyValueFactory<Student, String>("studentId"));
		colFirstName.setCellValueFactory(new PropertyValueFactory<Student, String>("firstName"));
		colLastName.setCellValueFactory(new PropertyValueFactory<Student, String>("lastName"));
		colAbsent.setCellValueFactory(new PropertyValueFactory<Student, String>("select"));
		colGroups.setCellValueFactory(new PropertyValueFactory<Group, String>("groupName"));
		colStudentId.setCellFactory(TextFieldTableCell.forTableColumn());
		colFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
		colLastName.setCellFactory(TextFieldTableCell.forTableColumn());
		colGroups.setCellFactory(TextFieldTableCell.forTableColumn());
		colStudentId.prefWidthProperty().bind(tableStudents.widthProperty().divide(18).multiply(5));
		colFirstName.prefWidthProperty().bind(tableStudents.widthProperty().divide(18).multiply(5));
		colLastName.prefWidthProperty().bind(tableStudents.widthProperty().divide(18).multiply(5));
		colAbsent.prefWidthProperty().bind(tableStudents.widthProperty().divide(18).multiply(3));
		colAbsent.setStyle("-fx-alignment: CENTER;");
		tableGroups.setItems(groupList);
	}
}
