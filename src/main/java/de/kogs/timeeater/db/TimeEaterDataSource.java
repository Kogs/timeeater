/**
 *
 */
package de.kogs.timeeater.db;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class TimeEaterDataSource extends BasicDataSource {

	/**
	 * 
	 */
	public TimeEaterDataSource () {
		load();
	}
	
	private void load() {
		setUrl(ConnectionInfo.url);
		setUsername(ConnectionInfo.username);
		setPassword(ConnectionInfo.password);
	}
	
}
