package de.kogs.timeeater.chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.LoggedWork;
import de.kogs.timeeater.util.Utils;

public class TimeChart extends XYChart<Number, String> {
	
	public static class ExtraData {
		
		public long length;
		private LoggedWork work;
		private Job job;
		
		public ExtraData (long lengthMs, Job job, LoggedWork work) {
			super();
			this.length = lengthMs;
			this.job = job;
			this.work = work;
			
		}
		
		public long getLength() {
			return length;
		}
		
		public void setLength(long length) {
			this.length = length;
		}
		
		public LoggedWork getWork() {
			return work;
		}
		
		public void setWork(LoggedWork work) {
			this.work = work;
		}
		
		public Job getJob() {
			return job;
		}
		
		public void setJob(Job job) {
			this.job = job;
		}
		
	}
	
	private double blockHeight = 10;
	private ReloadChartListener reloadChartListener;
	
	private Line helpLine = new Line();
	private Tooltip helpLineToolTip = new Tooltip();
	private Node chartBackground;
	
	public TimeChart (@NamedArg("xAxis") Axis<Number> xAxis, @NamedArg("yAxis") Axis<String> yAxis, ReloadChartListener  reloadChartListener) {
		this(xAxis, yAxis, FXCollections.<Series<Number, String>>observableArrayList(),reloadChartListener);
	}
	
	public TimeChart (@NamedArg("xAxis") Axis<Number> xAxis, @NamedArg("yAxis") Axis<String> yAxis,
			@NamedArg("data") ObservableList<Series<Number, String>> data, ReloadChartListener  reloadChartListener) {
		super(xAxis, yAxis);
		this.reloadChartListener = reloadChartListener;
		setData(data);
		chartBackground = lookup(".chart-plot-background");
		helpLine.setVisible(false);
		helpLine.setMouseTransparent(true);
		getPlotChildren().add(helpLine);
	}
	
	private static double getLength(Object obj) {
		return ((ExtraData) obj).getLength();
	}
	
	@Override
	protected void layoutPlotChildren() {
		
		for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
			
			Series<Number, String> series = getData().get(seriesIndex);
			
			Iterator<Data<Number, String>> iter = getDisplayedDataIterator(series);
			while (iter.hasNext()) {
				Data<Number, String> item = iter.next();
				double x = getXAxis().getDisplayPosition(item.getXValue());
				double y = getYAxis().getDisplayPosition(item.getYValue());
				if (Double.isNaN(x) || Double.isNaN(y)) {
					continue;
				}
				Node block = item.getNode();
				Rectangle ellipse;
				if (block != null) {
					if (block instanceof StackPane) {
						StackPane region = (StackPane) item.getNode();
						if (region.getShape() == null) {
							ellipse = new Rectangle(getLength(item.getExtraValue()), getBlockHeight());
						} else if (region.getShape() instanceof Rectangle) {
							ellipse = (Rectangle) region.getShape();
						} else {
							return;
						}
						ellipse.setWidth(getLength(item.getExtraValue())
								* ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getXAxis()).getScale()) : 1));
						ellipse.setHeight(getBlockHeight());
//								* ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getYAxis()).getScale()) : 1));
						y -= getBlockHeight() / 2.0;
						
						// Note: workaround for RT-7689 - saw this in ProgressControlSkin
						// The region doesn't update itself when the shape is mutated in place, so we
						// null out and then restore the shape in order to force invalidation.
						region.setShape(null);
						region.setShape(ellipse);
						region.setScaleShape(false);
						region.setCenterShape(false);
						region.setCacheShape(false);
						
						block.setLayoutX(x);
						block.setLayoutY(y);
					}
				}
			}
		}
	}
	
	public double getBlockHeight() {
		return blockHeight;
	}
	
	public void setBlockHeight(double blockHeight) {
		this.blockHeight = blockHeight;
	}
	
	@Override
	protected void dataItemAdded(Series<Number, String> series, int itemIndex, Data<Number, String> item) {
		Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
		getPlotChildren().add(block);
	}
	
	@Override
	protected void dataItemRemoved(final Data<Number, String> item, final Series<Number, String> series) {
		final Node block = item.getNode();
		getPlotChildren().remove(block);
		removeDataItemFromDisplay(series, item);
	}
	
	@Override
	protected void dataItemChanged(Data<Number, String> item) {
	}
	
	@Override
	protected void seriesAdded(Series<Number, String> series, int seriesIndex) {
		for (int j = 0; j < series.getData().size(); j++) {
			Data<Number, String> item = series.getData().get(j);
			Node container = createContainer(series, seriesIndex, item, j);
			getPlotChildren().add(container);
		}
	}
	
	@Override
	protected void seriesRemoved(final Series<Number, String> series) {
		for (XYChart.Data<Number, String> d : series.getData()) {
			final Node container = d.getNode();
			getPlotChildren().remove(container);
		}
		removeSeriesFromDisplay(series);
		
	}
	
	private Node createContainer(Series<Number,String> series, int seriesIndex, final Data<Number, String> item, int itemIndex) {
		Node container = item.getNode();
		
		if (container == null) {
			ExtraData extraData = (ExtraData) item.getExtraValue();
			container = new ChartBar(extraData.getJob(), extraData.getWork(),this,reloadChartListener,item);
			item.setNode(container);
		}
		return container;
	}
	
	@Override
	protected void updateAxisRange() {
		final Axis<Number> xa = getXAxis();
		final Axis<String> ya = getYAxis();
		List<Number> xData = null;
		List<String> yData = null;
		if (xa.isAutoRanging()) {
			xData = new ArrayList<Number>();
		}
		if (ya.isAutoRanging()) {
			yData = new ArrayList<String>();
		}
		if (xData != null || yData != null) {
			for (Series<Number, String> series : getData()) {
				for (Data<Number, String> data : series.getData()) {
					if (xData != null) {
						xData.add(data.getXValue());
						xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
					}
					if (yData != null) {
						yData.add(data.getYValue());
					}
				}
			}
			if (xData != null) {
				xa.invalidateRange(xData);
			}
			if (yData != null) {
				ya.invalidateRange(yData);
			}
		}
	}
	
	public void showHelpLine(Number xValue){
		helpLine.setVisible(true);
		helpLine.toFront();
		
		double xDisplayPosition = getXAxis().getDisplayPosition(xValue);
		
		helpLine.setStartX(xDisplayPosition);
		helpLine.setEndX(xDisplayPosition);
		
		helpLine.setStartY(0);
		helpLine.setEndY(getChartBackground().getBoundsInParent().getHeight());
		
		Bounds localToScreen = helpLine.localToScreen(helpLine.getBoundsInLocal());
		helpLineToolTip.setText(Utils.timeToString(xValue.longValue()));
		helpLineToolTip.show(helpLine, localToScreen.getMaxX(), localToScreen.getMaxY());
		 
	}
	public void hideHelpLine(){
		helpLine.setVisible(false);
		helpLineToolTip.hide();
	}

	public Node getChartBackground() {
		return chartBackground;
	}

	
}