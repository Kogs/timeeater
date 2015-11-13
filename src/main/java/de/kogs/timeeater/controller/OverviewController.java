package de.kogs.timeeater.controller;

import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.JobVo;
import de.kogs.timeeater.data.hooks.HookManager;
import de.kogs.timeeater.data.hooks.QuickLink;
import de.kogs.timeeater.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

public class OverviewController extends Stage implements Initializable {
	public OverviewController () {
		setTitle("Overview Window");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(OverviewController.class.getResource("/overview.fxml"));
		
		try {
			Scene scene = new Scene((Parent) loader.load());
			scene.getStylesheets().add("style.css");
			scene.getStylesheets().add("overview.css");
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
	
	@FXML
	private CheckBox showActive;
	
	private JobProvider provider;
	
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
	
	@FXML
	private Label summaryMonday;
	@FXML
	private Label summaryTuesday;
	@FXML
	private Label summaryWednesday;
	@FXML
	private Label summaryThursday;
	@FXML
	private Label summaryFriday;
	
	private Date monday;
	private Date tuesday;
	private Date wednesday;
	private Date thursday;
	private Date friday;
	
	private DateFormat weekDayFormat = new SimpleDateFormat("dd.MM.yy");;
	
	private Date currentDate = null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		provider = JobProvider.getProvider();
		
		rangePicker.setOnAction(t -> {
			
			LocalDate ld = rangePicker.getValue();
			Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
			Date res = Date.from(instant);
			showForDate(res);
		});
		showActive.selectedProperty().addListener((obs) -> showForDate(currentDate));
		showForDate(new Date());
		
	}
	
	private void showForDate(Date d) {
		currentDate = d;
		
		// Instant instant = Instant.ofEpochMilli(currentDate.getTime());
		// LocalDate res = LocalDateTime.ofInstant(instant,
		// ZoneId.systemDefault()).toLocalDate();
		// rangePicker.setValue(res);
		
		Calendar c = GregorianCalendar.getInstance(Locale.GERMAN);
		c.setTime(d);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		monday = c.getTime();
		mondayLabel.setText(weekDayFormat.format(monday));
		summaryMonday.setText(millisToString(provider.getTimeForDay(monday)));
		c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		tuesday = c.getTime();
		tuesdayLabel.setText(weekDayFormat.format(tuesday));
		summaryTuesday.setText(millisToString(provider.getTimeForDay(tuesday)));
		c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		wednesday = c.getTime();
		wednesdayLabel.setText(weekDayFormat.format(wednesday));
		summaryWednesday.setText(millisToString(provider.getTimeForDay(wednesday)));
		c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		thursday = c.getTime();
		thursdayLabel.setText(weekDayFormat.format(thursday));
		summaryThursday.setText(millisToString(provider.getTimeForDay(thursday)));
		c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		friday = c.getTime();
		fridayLabel.setText(weekDayFormat.format(friday));
		summaryFriday.setText(millisToString(provider.getTimeForDay(friday)));
		
		contentGrid.getChildren().clear();
		contentGrid.getRowConstraints().clear();
		
		int i = 0;
		
		Collection<JobVo> jobs = showActive.isSelected() ? provider.getJobsForRange(monday, friday) : provider
				.getKownJobs();
		for (JobVo job : jobs) {
			contentGrid.getRowConstraints().add(new RowConstraints(30));
			
			Labeled jobLabel;
			
			QuickLink link = HookManager.instance().getQuickLinkForJob(job);
			
			if (link != null) {
				Hyperlink jobLink = new Hyperlink(job.getName());
				jobLink.setOnAction((e) -> {
					link.fireForJob(job);
				});
				jobLabel = jobLink;
			} else {
				jobLabel = new Label(job.getName());
			}
			
			ImageView overviewImage = new ImageView();
			overviewImage.setPickOnBounds(true);
			overviewImage.getStyleClass().add("jobButton");
			
			overviewImage.setOnMouseClicked((e) -> new JobOverviewController(job));
			
			StackPane labelStack = new StackPane(jobLabel);
			HBox labelBox = new HBox(overviewImage, labelStack);
			
			labelBox.setPadding(new Insets(0, 10, 0, 10));
			
			labelBox.setAlignment(Pos.CENTER_LEFT);
			HBox.setHgrow(labelStack, Priority.ALWAYS);
			
			contentGrid.addRow(i, labelBox, new Label(millisToString(job.getWorkTime(monday))),
					new Label(millisToString(job.getWorkTime(tuesday))), new Label(millisToString(job.getWorkTime(wednesday))),
					new Label(millisToString(job.getWorkTime(thursday))), new Label(millisToString(job.getWorkTime(friday))),
					createJobControls(job));
					
			contentGrid.getRowConstraints().add(new RowConstraints(1));
			Pane seperator = new Pane();
			seperator.getStyleClass().add("seperator");
			contentGrid.add(seperator, 0, i + 1, 7, 1);
			
			i += 2;
			
		}
		contentGrid.addRow(i);
		RowConstraints lastRow = new RowConstraints();
		lastRow.setVgrow(Priority.ALWAYS);
		contentGrid.getRowConstraints().add(i, lastRow);
		
	}
	
	private String millisToString(long millis) {
		if (millis == 0) {
			return "-";
		}
		return Utils.millisToString(millis);
	}
	
	private Node createJobControls(JobVo j) {
		Button delete = new Button("NoAction");
//		delete.setOnAction((event) -> {
//			manager.removeJob(j);
//			showForDate(currentDate);
//		});
		
		return delete;
	}
	
	@FXML
	private void refresh() {
		showForDate(currentDate);
	}
	
	@FXML
	private void today() {
		showForDate(new Date());
	}
	
	@FXML
	private void weekBack() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DAY_OF_WEEK, -7);
		showForDate(cal.getTime());
	}
	
	@FXML
	private void weekForward() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DAY_OF_WEEK, 7);
		showForDate(cal.getTime());
	}
	
	@FXML
	private void showMonday() {
		showDetailsForDate(monday);
	}
	
	@FXML
	private void showTuesday() {
		showDetailsForDate(tuesday);
	}
	
	@FXML
	private void showWednesday() {
		showDetailsForDate(wednesday);
	}
	
	@FXML
	private void showThursday() {
		showDetailsForDate(thursday);
	}
	
	@FXML
	private void showFriday() {
		showDetailsForDate(friday);
	}
	
	private void showDetailsForDate(Date date) {
		DayOverviewController overView = new DayOverviewController(date);
	}
	
}
