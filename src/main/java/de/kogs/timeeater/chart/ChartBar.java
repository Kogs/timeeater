/**
 *
 */
package de.kogs.timeeater.chart;

import de.kogs.timeeater.chart.TimeChart.ExtraData;
import de.kogs.timeeater.data.JobProvider;
import de.kogs.timeeater.data.JobVo;
import de.kogs.timeeater.data.LoggedWork;
import de.kogs.timeeater.util.Utils;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.Calendar;
import java.util.Date;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class ChartBar extends StackPane {
	
	private static boolean modifyRunning = false;
	private static Tooltip infoTip = new Tooltip("Info");
	private static final int tipOffset = 10;
	
	private JobVo job;
	private LoggedWork work;
	
	private ContextMenu contextMenu;
	private ReloadChartListener reloadChartListener;
	private TimeChart chart;
	private Data<Number, String> data;
	private boolean active;
	private boolean snapToMinutes = false;
	
	public ChartBar (JobVo job, LoggedWork work, TimeChart chart, ReloadChartListener reloadChartListener,
			Data<Number, String> data, boolean active) {
		this.job = job;
		this.work = work;
		this.chart = chart;
		this.reloadChartListener = reloadChartListener;
		this.data = data;
		this.active = active;
		
		getStyleClass().add("time-bar");
		
		setOnMouseEntered((e) -> {
			infoTip.show(this, e.getScreenX() + tipOffset, e.getScreenY() + tipOffset);
			infoTip.setText(Utils.timeToString(work.getLogStart()) + " to " + Utils.timeToString(work.getLogEnd()) + " -> "
					+ Utils.millisToString(work.getLogEnd() - work.getLogStart()));
		});
		setOnMouseMoved((e) -> {
			infoTip.setAnchorX(e.getScreenX() + tipOffset);
			infoTip.setAnchorY(e.getScreenY() + tipOffset);
		});
		setOnMouseExited((e) -> {
			infoTip.hide();
		});
		
		setOnMouseClicked((e) -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				showContextMenu();
			}
		});
		
	}
	
	private void showContextMenu() {
		if (modifyRunning) {
			return;
		}
		if (contextMenu == null) {
			initContextMenu();
		}
		
		Point2D bounds = localToScreen(0, 0);
		System.out.println(getBoundsInParent().getHeight());
		contextMenu.show(this, bounds.getX(), bounds.getY() + getBoundsInParent().getHeight());
		
	}
	
	private void initContextMenu() {
		contextMenu = new ContextMenu();
		
		MenuItem item0 = new MenuItem("Move");
		item0.setOnAction(e -> move());
		item0.setDisable(active);
		MenuItem item1 = new MenuItem("Move Start");
		item1.setOnAction(e -> startResizeStart());
		MenuItem item2 = new MenuItem("Move End");
		item2.setDisable(active);
		item2.setOnAction(e -> startResizeEnd());
		MenuItem item3 = new MenuItem("Split");
		item3.setOnAction(e -> split());
		item3.setDisable(active);
		MenuItem item4 = new MenuItem("Merge with previous");
		item4.setOnAction(e -> connectPrevs());
		item4.setDisable(active);
		MenuItem item5 = new MenuItem("Merge with next");
		item5.setOnAction(e -> connectNext());
		item5.setDisable(active);
		
		MenuItem item6 = new MenuItem("Delete");
		item6.setOnAction(e -> delete());
		item6.setDisable(active);
		
		contextMenu.getItems().addAll(item0, item1, item2, new SeparatorMenuItem(), item3, item4, item5,
				new SeparatorMenuItem(), item6);
	}
	
	public long snapValue(long value) {
		if (snapToMinutes) {
			
			Date whateverDateYouWant = new Date(value);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(whateverDateYouWant);
			
			int unroundedMinutes = calendar.get(Calendar.MINUTE);
			int mod = unroundedMinutes % 5;
			calendar.add(Calendar.MINUTE, mod < 8 ? -mod : (15 - mod));
			return calendar.getTimeInMillis();
		}
		return value;
		
	}
	
	private void connectPrevs() {
		LoggedWork previousWork = job.getPreviousWork(work);
		if (previousWork != null) {
			previousWork.setLogEnd(work.getLogEnd());
			job.getWorks().remove(work);
			reloadChartListener.reload();
		}
	}
	
	private void connectNext() {
		LoggedWork nextWork = job.getNextWork(work);
		if (nextWork != null) {
			work.setLogEnd(nextWork.getLogEnd());
			job.getWorks().remove(nextWork);
			reloadChartListener.reload();
		}
	}
	
	@SuppressWarnings("unused")
	private void move() {
		new ModifyAction() {
			@Override
			protected void update(Number xValue, ExtraData extra) {
				long halfLength = extra.getLength() / 2;
				
				long start = snapValue(xValue.longValue() - halfLength);
				long end = snapValue(xValue.longValue() + halfLength);
				
				boolean moveAllow = true;
				// Move is allowed anywhere
				// LoggedWork previousWork = job.getPreviousWork(work);
				// if (previousWork != null && (start <
				// previousWork.getLogEnd())) {
				// moveAllow = false;
				// }
				// LoggedWork nextWork = job.getNextWork(work);
				// if (nextWork != null && (end > nextWork.getLogStart())) {
				// moveAllow = false;
				// }
				
				if (moveAllow) {
					chart.showHelpLine(xValue);
					data.setXValue(start);
					extra.setLength(end - start);
				}
			}
			
			@Override
			protected void submit(ExtraData extra) {
				work.setLogStart(data.getXValue().longValue());
				work.setLogEnd(work.getLogStart() + extra.getLength());
			}
		};
		
	}
	
	@SuppressWarnings("unused")
	private void startResizeEnd() {
		
		new ModifyAction() {
			
			@Override
			protected void update(Number xValue, ExtraData extra) {
				long end = xValue.longValue();
				end = snapValue(Math.max(end, work.getLogStart()));
				LoggedWork nextWork = job.getNextWork(work);
				if (nextWork != null) {
					end = snapValue(Math.min(end, nextWork.getLogStart()));
				}
				long length = end - work.getLogStart();
				
				chart.showHelpLine(end);
				
				extra.setLength(length);
			}
			
			@Override
			protected void submit(ExtraData extra) {
				work.setLogEnd(work.getLogStart() + extra.getLength());
			}
		};
	}
	
	@SuppressWarnings("unused")
	private void startResizeStart() {
		new ModifyAction() {
			
			@Override
			protected void update(Number xValue, ExtraData extra) {
				Long start = xValue.longValue();
				start = snapValue(Math.min(start, work.getLogEnd()));
				
				LoggedWork prevWork = job.getPreviousWork(work);
				if (prevWork != null) {
					start = snapValue(Math.max(start, prevWork.getLogEnd()));
				}
				
				chart.showHelpLine(start);
				
				data.setXValue(start);
				extra.setLength(work.getLogEnd() - start);
				
				chart.layoutPlotChildren();
			}
			
			@Override
			protected void submit(ExtraData extra) {
				work.setLogStart(data.getXValue().longValue());
			}
		};
	}
	
	private void split() {
		if (work.getLogEnd() == null) {
			return;
		}
		long duration = (work.getLogEnd() - work.getLogStart()) / 2;
		
		LoggedWork newWork = new LoggedWork();
		newWork.setLogStart(work.getLogStart() + duration);
		newWork.setLogDate(new Date(work.getLogStart() + duration));
		newWork.setLogEnd(work.getLogEnd());
		job.getWorks().add(newWork);
		
		work.setLogEnd(work.getLogEnd() - duration);
		
		reloadChartListener.reload();
		
	}
	
	private void delete() {
		job.getWorks().remove(work);
		reloadChartListener.reload();
	}
	
	private abstract class ModifyAction {
		
		public ModifyAction () {
			
			Node chartBackground = chart.getChartBackground();
			ExtraData extra = (ExtraData) data.getExtraValue();
			
			EventHandler<? super MouseEvent> moveListener = event -> {
				modifyRunning = true;
				update(chart.getXAxis().getValueForDisplay(event.getX()), extra);
				chart.layoutPlotChildren();
			};
			chartBackground.addEventHandler(MouseEvent.MOUSE_MOVED, moveListener);
			
			chartBackground.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					if (e.getButton() == MouseButton.PRIMARY) {
						submit(extra);
						JobProvider.getProvider().save();
					}
					chartBackground.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
					chartBackground.removeEventHandler(MouseEvent.MOUSE_MOVED, moveListener);
					chart.hideHelpLine();
					reloadChartListener.reload();
					modifyRunning = false;
				}
			});
		}
		
		protected abstract void update(Number xValue, ExtraData extra);
		
		protected abstract void submit(ExtraData extra);
	}
	
}
