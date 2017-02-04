/**
 *
 */
package de.kogs.timeeater.controller;

import de.kogs.javafx.decoratedScene.DecoratedScene;
import de.kogs.timeeater.data.JobManager;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.Settings;
import de.kogs.timeeater.main.TimeEater;
import de.kogs.timeeater.util.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class StartController extends Stage implements Initializable {
	
	private Runnable onStarted;
	
	@FXML
	private Button startButton;
	@FXML
	private Label statusLabel;
	
	@FXML
	private TextField driverTxt;
	
	@FXML
	private TextField urlTxt;
	
	@FXML
	private TextField userTxt;
	
	@FXML
	private PasswordField pwTxt;
	
	@FXML
	private TabPane datasourceTabPane;
	
	@FXML
	private TextField jsonFolderTxt;
	
	@FXML
	private CheckBox wakeUp;
	
	@FXML
	private ComboBox<Number> wakeUpInterval;
	
	private boolean starting = false;
	
//	private UserDAO userDAO;
	
	public StartController (Runnable onStarted) {
		this.onStarted = onStarted;
		setTitle("TimeEater");
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(OverviewController.class.getResource("/startScreen.fxml"));
		initStyle(StageStyle.UNDECORATED);
		try {
			Scene scene = new DecoratedScene((Region) loader.load());
			scene.getStylesheets().add("style.css");
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getIcons().add(TimeEater.STAGE_ICON);
		show();
		setOnHidden((e) -> {
			if (!starting) {
				Platform.exit();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		driverTxt.setText(Settings.getProperty("driver", "org.postgresql.Driver"));
		urlTxt.setText(Settings.getProperty("url", "jdbc:postgresql://localhost:5432/timeeater"));
		userTxt.setText(Settings.getProperty("user", "timeeater"));
		pwTxt.setText(Settings.getProperty("password", "timeeater"));
		datasourceTabPane.getTabs().get(0).setDisable(true);
		datasourceTabPane.getSelectionModel().select(1);
		jsonFolderTxt.setText(Settings.getProperty("json.folder", System.getProperty("user.dir") + "\\conf\\"));
		
		wakeUpInterval.setConverter(Utils.longToMillisConverter);
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(1)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(15)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(30)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(60)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(90)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(120)));
		wakeUpInterval.getItems().add(Long.valueOf(TimeUnit.MINUTES.toMillis(180)));
		wakeUpInterval.getSelectionModel()
				.select(Long.valueOf(Settings.getProperty("wakeup.interval", String.valueOf(TimeUnit.MINUTES.toMillis(30)))));
				
		wakeUpInterval.disableProperty().bind(wakeUp.selectedProperty().not());
		wakeUp.setSelected(Boolean.parseBoolean(Settings.getProperty("wakeup.active", "false")));
		
	}
	
	@FXML
	private void searchJsonFolder() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select Json Save Folder");
		chooser.setInitialDirectory(new File(jsonFolderTxt.getText()));
		File jsonFolder = chooser.showDialog(this);
		jsonFolderTxt.setText(jsonFolder.getPath());
	}
	
	@FXML
	private void start() {
		starting = true;
		Settings.setProperty("wakeup.active", Boolean.toString(wakeUp.isSelected()));
		Number wakeUpInter = wakeUpInterval.getSelectionModel().getSelectedItem();
		Settings.setProperty("wakeup.interval", wakeUpInter.toString());
		if (wakeUp.isSelected()) {
			WakeUpController.showWakeUpIn(wakeUpInter.longValue());
		}
		
		switch (datasourceTabPane.getSelectionModel().getSelectedIndex()) {
			case 0 :
				startWithDatabase();
				break;
			case 1 :
				startWithJson();
				break;
				
			default:
				break;
		}
		
	}
	
	private void startWithJson() {
		Settings.setProperty("json.folder", jsonFolderTxt.getText());
		
		JobProvider.initProvider(new JobManager());
		if (onStarted != null) {
			onStarted.run();
		}
		close();
	}
	
	private void startWithDatabase() {
//		startButton.setDisable(true);
//		statusLabel.setText("Update Database. Please wait");
//		
//		ConnectionInfo.driver = driverTxt.getText();
//		ConnectionInfo.url = urlTxt.getText();
//		ConnectionInfo.username = userTxt.getText();
//		ConnectionInfo.password = pwTxt.getText();
//		
//		Settings.setProperty("dirver", driverTxt.getText());
//		Settings.setProperty("url", urlTxt.getText());
//		Settings.setProperty("user", userTxt.getText());
//		Settings.setProperty("password", pwTxt.getText());
//		
//		Thread dataBaseUpdater = new Thread(() -> {
//			try {
//				UpdateDB.updateDB();
//				Platform.runLater(() -> statusLabel.setText("Database Updated. Starting db Connection now"));
//				
//				final AbstractApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
//				context.registerShutdownHook();
//				userDAO = context.getBean(UserDAO.class);
//				
//				if (Settings.booleanValue("autologin", false)) {
//					if (!realLogin(Settings.getProperty("eater.user"), Settings.getProperty("eater.password"))) {
//						login("User or password incorect");
//					}
//				} else {
//					login();
//				}
//				
//				JobProvider.initProvider(new JobDatabase(context));
//				
//				Platform.runLater(() -> {
//					statusLabel.setText("Connected");
//					startButton.setDisable(true);
//					if (onStarted != null) {
//						onStarted.run();
//					}
//					
//					close();
//					
//				});
//			} catch (Exception e) {
//				Platform.runLater(() -> {
//					statusLabel.setText("Database Update Failed. Please try again.");
//					startButton.setDisable(false);
//				});
//				e.printStackTrace();
//			}
//		});
//		
//		dataBaseUpdater.start();
	}
	
//	private void login() {
//		login(null);
//	}
//	
//	private void login(String optinalMsg) {
//		JTextField user = new JTextField();
//		JPasswordField pw = new JPasswordField();
//		JCheckBox stayLogedIn = new JCheckBox("Stay Logedin");
//		Object[] message = {optinalMsg, "Username:", user, "Password:", pw, stayLogedIn};
//		
//		String[] buttons = {"Login", "Register", "Cancel"};
//		
//		int option = JOptionPane.showOptionDialog(null, message, "Login", JOptionPane.PLAIN_MESSAGE,
//				JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[2]);
//		if (option == 0) {
//			// login
//			if (!realLogin(user.getText(), new String(pw.getPassword()))) {
//				login("User or password incorect");
//			} else {
//				if (stayLogedIn.isSelected()) {
//					Settings.setProperty("autologin", "true");
//					Settings.setProperty("eater.user", user.getText());
//					Settings.setProperty("eater.password", new String(pw.getPassword()));
//				}
//			}
//		} else if (option == 1) {
//			// register
//			register();
//		} else {
//			Platform.exit();
//		}
//	}
//	
//	private boolean realLogin(String userName, String password) {
//		User aUser = userDAO.findOneByLogin(userName);
//		if (aUser != null && aUser.getPassword().equals(password)) {
//			SessionHandler.isLogedIn = true;
//			// TODO VO
//			SessionHandler.logedInUser = aUser;
//			return true;
//		}
//		return false;
//	}
//	
//	private void register(String optinalMsg, String loginText) {
//		
//		JTextField login = new JTextField();
//		if (loginText != null) {
//			login.setText(loginText);
//		}
//		
//		JPasswordField pw = new JPasswordField();
//		JPasswordField pwRepeat = new JPasswordField();
//		Object[] message = {optinalMsg, "Username:", login, "Password:", pw, "Repeat:", pwRepeat};
//		
//		int option = JOptionPane.showConfirmDialog(null, message, "Register", JOptionPane.OK_CANCEL_OPTION,
//				JOptionPane.PLAIN_MESSAGE);
//		if (option == JOptionPane.OK_OPTION) {
//			String pwString = new String(pw.getPassword());
//			if (!pwString.trim().isEmpty() && pwString.equals(new String(pwRepeat.getPassword()))) {
//				if (userDAO.findOneByLogin(login.getText()) == null) {
//					User newUser = new User();
//					newUser.setLogin(login.getText());
//					newUser.setName(login.getText());
//					newUser.setPassword(pwString);
//					userDAO.save(newUser);
//					login();
//				} else {
//					register("User Login exists already", login.getText());
//					System.err.println("User already exists");
//				}
//			} else {
//				register("Password does not match", login.getText());
//				System.err.println("Pw not matching");
//			}
//		} else {
//			login();
//		}
//	}
//	
//	private void register() {
//		register(null, null);
//	}
//	
}
