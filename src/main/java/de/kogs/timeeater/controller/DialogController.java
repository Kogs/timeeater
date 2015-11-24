package de.kogs.timeeater.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DialogController extends Stage implements Initializable {

	private String title;

	private String message;

	/**
	 * 
	 */
	public DialogController(String title, String message) {

		this.message = message;
		this.title = title;
		setTitle(title);
		initStyle(StageStyle.UNDECORATED);
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(LoggerController.class.getResource("/dialog.fxml"));
		try {
			Scene scene = new Scene((Parent) loader.load());
			scene.getStylesheets().add("style.css");
			scene.getRoot().getStyleClass().add("popupDialog");
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	private Label titleLabel;

	@FXML
	private Label messageLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		titleLabel.setText(title);
		messageLabel.setText(message);
		PauseTransition pause = new PauseTransition(Duration.seconds(4));
		pause.setOnFinished((e) -> {
			close();
		});
		pause.play();


//		FontMetrics metrics = Toolkit.getToolkit().getFontLoader()
//				.getFontMetrics(titleLabel.getFont());
//
//		setWidth(metrics.computeStringWidth(title) + 30);


		setX(0);
		setY(0);
		show();
	}

}
