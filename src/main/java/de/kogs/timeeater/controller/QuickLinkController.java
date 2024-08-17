/**
 *
 */
package de.kogs.timeeater.controller;

import de.kogs.javafx.decoratedScene.DecoratedScene;
import de.kogs.timeeater.data.hooks.HookManager;
import de.kogs.timeeater.data.hooks.QuickLink;
import de.kogs.timeeater.main.TimeEater;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * 
 */
public class QuickLinkController extends Stage implements Initializable {
	public QuickLinkController () {
		setTitle("QuickLinks Window");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(OverviewController.class.getResource("/quickLinks.fxml"));
		initStyle(StageStyle.UNDECORATED);
		try {
			Scene scene = new DecoratedScene((Region) loader.load());
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
	private TableView<QuickLink> table;
	
	@FXML
	private TableColumn<QuickLink, String> patternColumn;
	
	@FXML
	private TableColumn<QuickLink, String> urlColumn;
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateTable();
		
		patternColumn.setCellValueFactory(new PropertyValueFactory<>("pattern"));
		urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
		
	}
	
	private void updateTable() {
		table.getItems().setAll(HookManager.instance().getQuickLinks());
	}
	
	@FXML
	public void add() {
		QuickLink link = editOrCreate(null);
		if (link != null) {
			HookManager.instance().getQuickLinks().add(link);
			HookManager.instance().save();
			updateTable();
		}
	}
	
	@FXML
	public void edit() {
		QuickLink selectedItem = table.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			QuickLink link = editOrCreate(selectedItem);
			if (link != null) {
				HookManager.instance().save();
				updateTable();
			}
		}
	}
	
	private QuickLink editOrCreate(QuickLink link) {
		JTextField pattern = new JTextField();
		JTextField url = new JTextField();
		if (link != null) {
			pattern.setText(link.getPattern());
			url.setText(link.getUrl());
		}
		
		Object[] message = {"Pattern:", pattern, "URL:", url};
		String tilte = link == null ? "Create" : "Edit";
		int option = JOptionPane.showConfirmDialog(null, message, tilte, JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			if (!pattern.getText().trim().isEmpty() && !url.getText().trim().isEmpty()) {
				if (link == null) {
					link = new QuickLink();
				}
				link.setPattern(pattern.getText());
				link.setUrl(url.getText());
				return link;
			}
		}
		return null;
	}
	
	@FXML
	public void remove() {
		QuickLink selectedItem = table.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			int option = JOptionPane.showConfirmDialog(null, "Sure to delete", "Delete", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) {
				HookManager.instance().getQuickLinks().remove(selectedItem);
				HookManager.instance().save();
				updateTable();
			}
		}
	}
	
}
