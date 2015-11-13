/**
 *
 */
package de.kogs.timeeater.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.kogs.timeeater.db.entites.User;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public interface UserDAO extends CrudRepository<User, Long> {

	@Query
	public User findOneByLogin(String login);
	
}
