/**
 *
 */
package de.kogs.timeeater.data.hooks;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
@HookConfig
public class ScheduledJob extends Hook {
	
	/**
	 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
	 */
	private final class HookConfigGuiScheduledJob extends HookConfigGui {
		
		private VBox listBox;
		private TextField startHour = new TextField();;
		private TextField startMin = new TextField();;
		private TextField endHour = new TextField();;
		private TextField endMin = new TextField();;
		private BooleanBinding binding;
		
		public HookConfigGuiScheduledJob () {
			listBox = new VBox();
			
			startHour.setPromptText("Start Stunde");
			startMin.setPromptText("Start Minute");
			endHour.setPromptText("Ende Stunde");
			endMin.setPromptText("Ende Minute");
			
			binding = Bindings.and(startHour.textProperty().isNotEmpty(), startMin.textProperty().isNotEmpty())
					.and(endHour.textProperty().isNotEmpty()).and(endMin.textProperty().isNotEmpty());
					
			listBox.getChildren().addAll(startHour, startMin, endHour, endMin);
		}
		
		@Override
		public BooleanBinding submitSupportedBinding() {
			return binding;
		}
		
		@Override
		public void submit() {
			getProperties().put("startHour", startHour.getText());
			getProperties().put("endHour", endHour.getText());
			getProperties().put("startMin", startMin.getText());
			getProperties().put("endMin", endMin.getText());
		}
		
		@Override
		public Node getGui() {
			return listBox;
		}
	}
	
	/**
	 * 
	 */
	public ScheduledJob () {
		getProperties().put("weekdays", Calendar.MONDAY + "," + Calendar.TUESDAY);
		getProperties().put("startHour", "10");
		getProperties().put("endHour", "10");
		getProperties().put("startMin", "0");
		getProperties().put("endMin", "15");
	}
	
	/* (non-Javadoc)
	 * @see de.kogs.timeeater.data.hooks.Hook#action()
	 */
	@Override
	public boolean action() {
		/**
		 * Return true if the Job should be activeded
		 */
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if (getWeekdays().contains(cal.get(Calendar.DAY_OF_WEEK))) {
			int hour = cal.get(Calendar.HOUR);
			int min = cal.get(Calendar.MINUTE);
			if ((hour >= getStartHour() && hour <= getEndHour()) && (min >= getStartMinute() && min <= getEndMinute())) {
				return true;
			}
		}
		return false;
	}
	
	public Integer getEndMinute() {
		return Integer.parseInt(getProperties().get("endMin"));
	}
	
	public Integer getStartMinute() {
		return Integer.parseInt(getProperties().get("startMin"));
	}
	
	public Integer getEndHour() {
		return Integer.parseInt(getProperties().get("endHour"));
	}
	
	public Integer getStartHour() {
		return Integer.parseInt(getProperties().get("startHour"));
	}
	
	public List<Integer> getWeekdays() {
		List<Integer> weekDays = new ArrayList<>();
		String string = getProperties().get("weekdays");
		for (String s : string.split(",")) {
			weekDays.add(Integer.parseInt(s));
		}
		return weekDays;
	}
	
	@Override
	public HookConfigGui getGuiContent() {
		
		return new HookConfigGuiScheduledJob();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScheduledJob in " + getStartHour() + ":" + getStartMinute() + " - " + getEndHour() + ":" + getEndMinute();
	}
	
}
