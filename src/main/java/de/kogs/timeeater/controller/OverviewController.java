package de.kogs.timeeater.controller;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.util.Utils;

public class OverviewController extends Stage implements Initializable {
	public OverviewController() {
		setTitle("Overview Window");

		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(OverviewController.class
				.getResource("/overview.fxml"));

		try {
			Scene scene = new Scene((Parent) loader.load());
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		show();
	}

	@FXML
	private DatePicker rangePicker;

	@FXML
	private GridPane contentGrid;

	@FXML
	private Label rangeLabel;

	private JobManager manager;

	@FXML
	private Label mondayLabel;
	@FXML
	private Label tuesdayLabel;
	@FXML
	private Label wednesdayLabel;
	@FXML
	private Label thursdayLabel;
	@FXML
	private Label fridayLabel;

	private Date monday;
	private Date tuesday;
	private Date wednesday;
	private Date thursday;
	private Date friday;

	private DateFormat weekDayFormat = new SimpleDateFormat("dd.MM.yy");;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		manager = JobManager.instance();
		showForDate(new Date());

	}

	private void showForDate(Date d) {
		Calendar c = GregorianCalendar.getInstance(Locale.GERMAN);
		c.setTime(d);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		monday = c.getTime();
		mondayLabel.setText(weekDayFormat.format(monday));
		c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		tuesday = c.getTime();
		tuesdayLabel.setText(weekDayFormat.format(tuesday));
		c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		wednesday = c.getTime();
		wednesdayLabel.setText(weekDayFormat.format(wednesday));
		c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		thursday = c.getTime();
		thursdayLabel.setText(weekDayFormat.format(thursday));
		c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		friday = c.getTime();
		fridayLabel.setText(weekDayFormat.format(friday));

		
		contentGrid.getChildren().clear();
		contentGrid.getRowConstraints().clear();
		
		int i = 0;
		for (Job job : manager.getKownJobs()) {
			contentGrid.getRowConstraints().add(new RowConstraints(30));
			contentGrid
					.addRow(i,
							new Label(job.getName()),
							new Label(Utils.millisToString(job
									.getWorkTime(monday))),
							new Label(Utils.millisToString(job
									.getWorkTime(tuesday))),
							new Label(Utils.millisToString(job
									.getWorkTime(wednesday))),
							new Label(Utils.millisToString(job
									.getWorkTime(thursday))),
							new Label(Utils.millisToString(job
									.getWorkTime(friday))));
			
			contentGrid.getRowConstraints().add(new RowConstraints(2));
			Pane seperator = new Pane();
			seperator.setStyle("-fx-background-color: black");
			contentGrid.add(seperator, 0, i+1, 6, 1);
			
			i+=2;
			
		}
		contentGrid.addRow(i);
		RowConstraints lastRow = new RowConstraints();
		lastRow.setVgrow(Priority.ALWAYS);
		contentGrid.getRowConstraints().add(i, lastRow);
		
	}
}
