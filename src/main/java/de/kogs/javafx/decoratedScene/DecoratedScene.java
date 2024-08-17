/**
 *
 */
package de.kogs.javafx.decoratedScene;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 
 */
public class DecoratedScene extends Scene {
	
	private static final double HEADLINE_HEIGHT = 25d;
	
	private Label title = new Label();
	// private ImageView titleImg = new ImageView();
	
	private BooleanProperty isMaximized = new SimpleBooleanProperty(false);
	
	private double minimizedX;
	
	private double minimizedY;
	
	private double minimizedWidth;
	
	private double minimiedHeight;
	
	private double xPos;
	
	private double yPos;
	
	private double mouseStartPosX;
	private double mouseStartPosY;
	private double stagePosX;
	private double stagePosY;
	private double frameHeight;
	private double frameWidth;
	private Pos resizeLocation;
	
	public DecoratedScene (Region innerRoot) {
		super(new AnchorPane(innerRoot));
		AnchorPane root = (AnchorPane) getRoot();
		root.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		AnchorPane.setTopAnchor(innerRoot, HEADLINE_HEIGHT);
		AnchorPane.setLeftAnchor(innerRoot, 3d);
		AnchorPane.setRightAnchor(innerRoot, 3d);
		AnchorPane.setBottomAnchor(innerRoot, 5d);
		
		Rectangle innerClip = new Rectangle();
		innerClip.widthProperty().bind(innerRoot.widthProperty());
		innerClip.heightProperty().bind(innerRoot.heightProperty());
		innerRoot.setClip(innerClip);
		
		AnchorPane headline = new AnchorPane();
		headline.setPrefHeight(HEADLINE_HEIGHT);
		headline.setMaxHeight(HEADLINE_HEIGHT);
		headline.getStyleClass().addAll("frameborder", "headline");
		
		headline.setOnMousePressed(e -> {
			Window w = getWindow();
			if (w != null) {
				xPos = w.getX() - e.getScreenX();
				yPos = w.getY() - e.getScreenY();
			}
		});
		headline.setOnMouseDragged(e -> {
			Window w = getWindow();
			if (!e.isConsumed() && w != null) {
				w.setX(e.getScreenX() + xPos);
				w.setY(e.getScreenY() + yPos);
			}
		});
		
		headline.getChildren().add(title);
		title.setAlignment(Pos.CENTER);
		title.getStyleClass().add("title");
		AnchorPane.setTopAnchor(title, 0d);
		AnchorPane.setLeftAnchor(title, 0d);
		AnchorPane.setRightAnchor(title, 0d);
		AnchorPane.setBottomAnchor(title, 0d);
		
		HBox frameControls = new HBox();
		frameControls.getStyleClass().add("frameControls");
		headline.getChildren().add(frameControls);
		AnchorPane.setTopAnchor(frameControls, 0d);
		AnchorPane.setRightAnchor(frameControls, 0d);
		
		Button closeButton = new Button();
		closeButton.getStyleClass().add("close");
		closeButton.setPrefSize(25, 25);
		closeButton.setMinSize(25, 25);
		closeButton.setOnAction((e) -> {
			if (getWindow() != null) {
				getWindow().hide();
			}
		});
		
		Button maxButton = new Button();
		maxButton.getStyleClass().add("max");
		maxButton.setPrefSize(25, 25);
		maxButton.setMinSize(25, 25);
		maxButton.setOnAction((e) -> {
			Window window = getWindow();
			if (window != null) {
				if (isMaximized.get()) {
					AnchorPane.setLeftAnchor(innerRoot, 3d);
					AnchorPane.setRightAnchor(innerRoot, 3d);
					AnchorPane.setBottomAnchor(innerRoot, 5d);
					AnchorPane.setLeftAnchor(headline, 3d);
					AnchorPane.setRightAnchor(headline, 3d);
					window.setX(minimizedX);
					window.setY(minimizedY);
					window.setWidth(minimizedWidth);
					window.setHeight(minimiedHeight);
					isMaximized.set(false);
				} else {
					ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(
							window.getX() + window.getWidth() / 2, window.getY() + window.getHeight() / 2, 1, 1);
					Screen s;
					if (screensForRectangle.isEmpty()) {
						s = Screen.getPrimary();
					} else {
						s = screensForRectangle.get(0);
					}
					minimizedX = window.getX();
					minimizedY = window.getY();
					minimizedWidth = window.getWidth();
					minimiedHeight = window.getHeight();
					
					Rectangle2D bounds = s.getVisualBounds();
					window.setX(bounds.getMinX());
					window.setY(bounds.getMinY());
					window.setWidth(bounds.getWidth());
					window.setHeight(bounds.getHeight());
					
					AnchorPane.setLeftAnchor(innerRoot, 0d);
					AnchorPane.setRightAnchor(innerRoot, 0d);
					AnchorPane.setBottomAnchor(innerRoot, 0d);
					AnchorPane.setLeftAnchor(headline, 0d);
					AnchorPane.setRightAnchor(headline, 0d);
					
					isMaximized.set(true);
				}
			}
		});
		
		Button minButton = new Button();
		minButton.getStyleClass().add("min");
		minButton.setPrefSize(25, 25);
		minButton.setMinSize(25, 25);
		minButton.setOnAction((e) -> {
			if (getWindow() != null && getWindow() instanceof Stage) {
				((Stage) getWindow()).setIconified(true);
			}
		});
		
		frameControls.getChildren().addAll(minButton, maxButton, closeButton);
		
		root.getChildren().add(headline);
		AnchorPane.setTopAnchor(headline, 0d);
		AnchorPane.setLeftAnchor(headline, 3d);
		AnchorPane.setRightAnchor(headline, 3d);
		
		Region leftBorder = new Region();
		leftBorder.visibleProperty().bind(isMaximized.not());
		AnchorPane.setTopAnchor(leftBorder, 0d);
		AnchorPane.setLeftAnchor(leftBorder, 0d);
		AnchorPane.setBottomAnchor(leftBorder, 5d);
		leftBorder.setMinWidth(3);
		leftBorder.setMaxWidth(3);
		leftBorder.setPrefWidth(3);
		leftBorder.getStyleClass().addAll("frameborder", "leftborder");
		root.getChildren().add(leftBorder);
		
		leftBorder.setOnMousePressed((e) -> {
		
		});
		
		Region rightBorder = new Region();
		rightBorder.visibleProperty().bind(isMaximized.not());
		AnchorPane.setTopAnchor(rightBorder, 0d);
		AnchorPane.setRightAnchor(rightBorder, 0d);
		AnchorPane.setBottomAnchor(rightBorder, 5d);
		rightBorder.setMinWidth(3);
		rightBorder.setMaxWidth(3);
		rightBorder.setPrefWidth(3);
		rightBorder.getStyleClass().addAll("frameborder", "rightborder");
		root.getChildren().add(rightBorder);
		
		Region bottomBorder = new Region();
		bottomBorder.visibleProperty().bind(isMaximized.not());
		AnchorPane.setLeftAnchor(bottomBorder, 0d);
		AnchorPane.setRightAnchor(bottomBorder, 0d);
		AnchorPane.setBottomAnchor(bottomBorder, 0d);
		bottomBorder.setMinHeight(5);
		bottomBorder.setMaxHeight(5);
		bottomBorder.setPrefHeight(5);
		bottomBorder.getStyleClass().addAll("frameborder", "bottomborder");
		root.getChildren().add(bottomBorder);
		
		root.setOnMouseMoved(this::onMouseMoved);
		root.setOnMouseDragged(this::onMouseDragged);
		windowProperty().addListener((obs, oldV, newV) -> {
			init(newV);
		});
		
		getStylesheets().add("decoratedScene.css");
	}
	
	private void init(Window window) {
		title.textProperty().unbind();
		
		if (window != null) {
			if (window instanceof Stage) {
				Stage stage = (Stage) window;
				
				title.textProperty().bind(stage.titleProperty());
			}
		}
	}
	
	private void onMouseMoved(MouseEvent e) {
		double height = getWindow().getHeight();
		double width = getWindow().getWidth();
		Cursor cursor = Cursor.DEFAULT;
		double sidePadding = 5;
		
		if (e.getX() < sidePadding && e.getY() > sidePadding && e.getY() < height - sidePadding) {
			cursor = Cursor.H_RESIZE;
			resizeLocation = Pos.CENTER_LEFT;
		} else if (e.getX() < sidePadding && e.getY() <= sidePadding) {
			cursor = Cursor.NW_RESIZE;
			resizeLocation = Pos.TOP_LEFT;
		} else if (e.getX() < sidePadding && e.getY() >= height - sidePadding) {
			cursor = Cursor.SW_RESIZE;
			resizeLocation = Pos.BOTTOM_LEFT;
		} else if (e.getX() > sidePadding && e.getY() < sidePadding && e.getX() < width - sidePadding) {
			cursor = Cursor.S_RESIZE;
			resizeLocation = Pos.TOP_CENTER;
		} else if (e.getX() >= width - sidePadding && e.getY() < sidePadding) {
			cursor = Cursor.SW_RESIZE;
			resizeLocation = Pos.TOP_RIGHT;
		} else if (e.getX() >= width - sidePadding && e.getY() > sidePadding && e.getY() < height - sidePadding) {
			cursor = Cursor.H_RESIZE;
			resizeLocation = Pos.CENTER_RIGHT;
		} else if (e.getX() >= width - sidePadding && e.getY() > height - sidePadding) {
			cursor = Cursor.NW_RESIZE;
			resizeLocation = Pos.BOTTOM_RIGHT;
		} else if (e.getX() < width - sidePadding && e.getX() > sidePadding && e.getY() > height - sidePadding) {
			cursor = Cursor.S_RESIZE;
			resizeLocation = Pos.BOTTOM_CENTER;
		} else {
			resizeLocation = null;
			cursor = Cursor.DEFAULT;
		}
		
		mouseStartPosX = e.getScreenX();
		mouseStartPosY = e.getScreenY();
		stagePosX = getWindow().getX();
		stagePosY = getWindow().getY();
		frameHeight = getHeight();
		frameWidth = getWidth();
		setCursor(cursor);
		
	}
	
	private void onMouseDragged(MouseEvent me) {
		
		Window w = getWindow();
		double minHeight = 20;
		double minWidth = 20;
		
		if (w instanceof Stage) {
			Stage stage = (Stage) getWindow();
			minHeight = stage.getMinHeight();
			minWidth = stage.getMinWidth();
		}
		
		if (me.getButton() == MouseButton.PRIMARY && resizeLocation != null) {
			me.consume();
			switch (resizeLocation) {
				case BOTTOM_CENTER : {
					w.setHeight(Math.max((frameHeight + me.getScreenY() - mouseStartPosY), minHeight));
					break;
				}
				case BOTTOM_LEFT : {
					w.setHeight(Math.max((frameHeight + (me.getScreenY() - mouseStartPosY)), minHeight));
					if (frameWidth + (mouseStartPosX - me.getScreenX()) >= minWidth) {
						w.setWidth(Math.max((frameWidth + (mouseStartPosX - me.getScreenX())), minWidth));
						w.setX(stagePosX - (mouseStartPosX - me.getScreenX()));
					}
					break;
				}
				case BOTTOM_RIGHT : {
					w.setHeight(Math.max((frameHeight + me.getScreenY() - mouseStartPosY), minHeight));
					w.setWidth(Math.max((frameWidth + me.getScreenX() - mouseStartPosX), minWidth));
					break;
				}
				case CENTER_LEFT : {
					if (frameWidth + (mouseStartPosX - me.getScreenX()) >= minWidth) {
						w.setWidth(Math.max((frameWidth + (mouseStartPosX - me.getScreenX())), minWidth));
						w.setX(stagePosX - (mouseStartPosX - me.getScreenX()));
					}
					break;
				}
				case CENTER_RIGHT : {
					w.setWidth(Math.max((frameWidth + me.getScreenX() - mouseStartPosX), minWidth));
					break;
				}
				case TOP_CENTER : {
					if (frameHeight + (mouseStartPosY - me.getScreenY()) >= minHeight) {
						w.setHeight(Math.max((frameHeight + (mouseStartPosY - me.getScreenY())), minHeight));
						w.setY(stagePosY - (mouseStartPosY - me.getScreenY()));
					}
					break;
				}
				case TOP_LEFT : {
					if (frameWidth + (mouseStartPosX - me.getScreenX()) >= minWidth) {
						w.setWidth(Math.max((frameWidth + (mouseStartPosX - me.getScreenX())), minWidth));
						w.setX(stagePosX - (mouseStartPosX - me.getScreenX()));
					}
					if (frameHeight + (mouseStartPosY - me.getScreenY()) >= minHeight) {
						w.setHeight(Math.max((frameHeight + (mouseStartPosY - me.getScreenY())), minHeight));
						w.setY(stagePosY - (mouseStartPosY - me.getScreenY()));
					}
					break;
				}
				case TOP_RIGHT : {
					w.setWidth(Math.max((frameWidth + (me.getScreenX() - mouseStartPosX)), minWidth));
					if (frameHeight + (mouseStartPosY - me.getScreenY()) >= minHeight) {
						w.setHeight(Math.max((frameHeight + (mouseStartPosY - me.getScreenY())), minHeight));
						w.setY(stagePosY - (mouseStartPosY - me.getScreenY()));
					}
					break;
				}
				default:
					break;
			}
		}
	}
	
}
