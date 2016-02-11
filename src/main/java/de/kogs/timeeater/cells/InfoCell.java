/**
 *
 */
package de.kogs.timeeater.cells;

import de.kogs.timeeater.controller.JobOverviewController;
import de.kogs.timeeater.data.JobVo;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class InfoCell extends TableCell<JobVo, JobVo> {
	
	/**
	 * 
	 */
	public InfoCell () {
		ImageView overviewImage = new ImageView();
		overviewImage.setPickOnBounds(true);
		overviewImage.getStyleClass().add("jobButton");
		
		setGraphic(overviewImage);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		setOnMouseClicked((e) -> openJob());
	}
	
	private void openJob() {
		JobVo job = getItem();
		if (job != null) {
			new JobOverviewController(job);
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(JobVo item, boolean empty) {
		super.updateItem(item, empty);
	}
	
}
