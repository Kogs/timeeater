/**
 *
 */
package de.kogs.timeeater.db.update;

import de.kogs.timeeater.db.ConnectionInfo;
import liquibase.Liquibase;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class UpdateDB {


	
	public static String changelog = "liquibase/changelog.xml";
	public static void updateDB() throws ClassNotFoundException, SQLException, LiquibaseException {
		Class.forName(ConnectionInfo.driver);
		
		Connection connection = DriverManager.getConnection(ConnectionInfo.url, ConnectionInfo.username,
				ConnectionInfo.password);
		JdbcConnection jdbcConnection = new JdbcConnection(connection);
		PostgresDatabase databaseConnection = new PostgresDatabase();
		databaseConnection.setConnection(jdbcConnection);
		
		Liquibase liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), databaseConnection);
		liquibase.update((String) null);
		connection.close();
		
	}
	

}
