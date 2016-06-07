package de.kogs.timeeater.controller;

import org.apache.commons.lang.time.DateUtils;

import de.kogs.javafx.decoratedScene.DecoratedScene;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.JobVo;
import de.kogs.timeeater.data.comparator.JobNameComparator;
import de.kogs.timeeater.data.hooks.HookManager;
import de.kogs.timeeater.data.hooks.QuickLink;
import de.kogs.timeeater.util.Utils;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class OverviewController extends Stage implements Initializable {
	public OverviewController () {
		setTitle("Overview Window");
		initStyle(StageStyle.UNDECORATED);
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(OverviewController.class.getResource("/overview.fxml"));
		setMinHeight(162);
		setMinWidth(620);
		try {
			Scene scene = new DecoratedScene((Region) loader.load());
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
	
	@FXML
	private Label summaryWeek;
	
	@FXML
	private Label clock;
	
	@FXML
	private Label noDataLabel;
	
	private Date monday;
	private Date tuesday;
	private Date wednesday;
	private Date thursday;
	private Date friday;
	
	private DateFormat weekDayFormat = new SimpleDateFormat("dd.MM.yy");
	
	private DateFormat clockFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
	
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
		showForDate(new Date());
		
		PauseTransition clockPause = new PauseTransition(Duration.seconds(1));
		clockPause.setOnFinished((e) -> {
			updateClock();
			if (isShowing()) {
				clockPause.play();
			}
		});
		clockPause.play();
		updateClock();
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
		long summaryMondayTime = provider.getTimeForDay(monday);
		summaryMonday.setText(millisToString(summaryMondayTime));
		c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		tuesday = c.getTime();
		tuesdayLabel.setText(weekDayFormat.format(tuesday));
		long summaryTuesdayTime = provider.getTimeForDay(tuesday);
		summaryTuesday.setText(millisToString(summaryTuesdayTime));
		c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		wednesday = c.getTime();
		wednesdayLabel.setText(weekDayFormat.format(wednesday));
		long summaryWednesdayTime = provider.getTimeForDay(wednesday);
		summaryWednesday.setText(millisToString(summaryWednesdayTime));
		c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		thursday = c.getTime();
		thursdayLabel.setText(weekDayFormat.format(thursday));
		long summaryThursdayTime = provider.getTimeForDay(thursday);
		summaryThursday.setText(millisToString(summaryThursdayTime));
		c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		friday = c.getTime();
		fridayLabel.setText(weekDayFormat.format(friday));
		long summaryFridayTime = provider.getTimeForDay(friday);
		summaryFriday.setText(millisToString(summaryFridayTime));
		
		summaryWeek.setText("= " + millisToString(
				summaryMondayTime + summaryTuesdayTime + summaryWednesdayTime + summaryThursdayTime + summaryFridayTime));
		
		contentGrid.getChildren().clear();
		contentGrid.getRowConstraints().clear();
		
		int i = 0;
		
		List<JobVo> jobs = new ArrayList<>(provider.getJobsForRange(monday, friday));
		
		noDataLabel.setVisible(jobs.isEmpty());
		
		Collections.sort(jobs, new JobNameComparator());
		
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
		Button copy = new Button("Copy");
		copy.setOnAction((event) -> {
			copy(Arrays.asList(j));
		});
		
		return copy;
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
	
	@FXML
	private void copyData() {
		List<JobVo> jobs = new ArrayList<>(provider.getJobsForRange(monday, friday));
		copy(jobs);
	}
	

	private void copy(List<JobVo> jobs) {
		Collections.sort(jobs, new JobNameComparator());
		
		StringBuilder builder = new StringBuilder();
		
		for (JobVo job : jobs) {
			builder.append(Utils.millisToHours(job.getWorkTime(monday)));
			builder.append("	");
			builder.append(Utils.millisToHours(job.getWorkTime(tuesday)));
			builder.append("	");
			builder.append(Utils.millisToHours(job.getWorkTime(wednesday)));
			builder.append("	");
			builder.append(Utils.millisToHours(job.getWorkTime(thursday)));
			builder.append("	");
			builder.append(Utils.millisToHours(job.getWorkTime(friday)));
			builder.append("\n");
		}
		
		StringSelection selection = new StringSelection(builder.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
		
		DialogController dialogController = new DialogController("Data Copied",
				jobs.size() + " Jobs where copied to your Clipboard");
	}
	
	private void showDetailsForDate(Date date) {
		DayOverviewController overView = new DayOverviewController(date);
	}
	
	private void updateClock() {
		clock.setText(clockFormat.format(new Date()));
		
		if (DateUtils.isSameDay(currentDate, new Date())) {
			showForDate(currentDate);
		}
		
	}
	
}
