/**
 *
 */
package de.kogs.timeeater.chart;

import de.kogs.timeeater.data.Job;
import de.kogs.timeeater.data.LoggedWork;
import javafx.scene.layout.StackPane;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class ChartBar extends StackPane {
	
	private Job job;
	private LoggedWork work;
	
	/**
	 * 
	 */
	public ChartBar (Job job, LoggedWork work) {
		this.job = job;
		this.work = work;
		
		getStyleClass().add("time-bar");
		
		setOnMouseEntered(e -> {
		
		});
		setOnMouseExited((e) -> {
		
		});
		
	}
	

}
