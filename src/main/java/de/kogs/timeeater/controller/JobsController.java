/**
 *
 */
package de.kogs.timeeater.controller;

import de.kogs.javafx.decoratedScene.DecoratedScene;
import de.kogs.timeeater.cells.InfoCell;
import de.kogs.timeeater.cells.StringConverterCell;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.JobVo;
import de.kogs.timeeater.main.TimeEater;
import de.kogs.timeeater.util.Utils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * 
 */
public class JobsController extends Stage implements Initializable {
	
	public JobsController () {
		setTitle("Jobs");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(JobOverviewController.class.getResource("/jobs.fxml"));
		initStyle(StageStyle.UNDECORATED);
		try {
			Region root = (Region) loader.load();
			root.getStyleClass().add("overview");
			Scene scene = new DecoratedScene(root);
			scene.getStylesheets().add("style.css");
			scene.getStylesheets().add("overview.css");
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getIcons().add(TimeEater.STAGE_ICON);
		show();
		

	}
	

	
	@FXML
	private TableView<JobVo> jobsTableView;
	
	@FXML
	private TableColumn<JobVo, JobVo> infoColumn;
	
	@FXML
	private TableColumn<JobVo, String> nameColumn;
	
	@FXML
	private TableColumn<JobVo, Number> runningColumn;
	
	@FXML
	private TableColumn<JobVo, Date> lastActiveColumn;
	
	@FXML
	private Button deleteButton;
	
	@FXML
	private Button newButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		jobsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		infoColumn.setSortable(false);
		infoColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
		infoColumn.setCellFactory(param -> new InfoCell());
		
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		runningColumn.setCellValueFactory(new PropertyValueFactory<>("fullWorkTime"));
		runningColumn.setCellFactory(param -> new StringConverterCell<>(Utils.longToMillisConverter));
		
		lastActiveColumn.setCellValueFactory(new PropertyValueFactory<>("lastActiveDate"));
		
		JobProvider provider = JobProvider.getProvider();
		jobsTableView.getItems().addAll(provider.getKownJobs());
	}
	
	@FXML
	private void deleteJob() {

	}
	
	@FXML
	private void newJob() {
	
	}
	
}
