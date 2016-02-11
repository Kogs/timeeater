/**
 *
 */
package de.kogs.timeeater.cells;

import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class StringConverterCell<S, T> extends TableCell<S, T> {
	
	private StringConverter<T> conveter;
	

	public StringConverterCell (StringConverter<T> conveter) {
		this.conveter = conveter;
	}
	

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty) {
			setText(conveter.toString(item));
		}
	}
	
}
