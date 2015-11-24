/**
 *
 */
package de.kogs.timeeater.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.kogs.timeeater.db.entites.Job;

import java.util.Collection;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public interface JobDAO extends CrudRepository<Job, Long> {

	@Query("select j from Job j where j.userID = ?1")
	public Collection<Job> findAllByUser(Long userId);
	
	@Query("select j from Job j where j.userID = ?1 and j.name = ?2")
	public Job findOneByUserAndName(Long userId, String name);
}
