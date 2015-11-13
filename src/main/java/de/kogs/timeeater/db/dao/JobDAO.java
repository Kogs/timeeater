/**
 *
 */
package de.kogs.timeeater.db.dao;

import org.springframework.data.repository.CrudRepository;

import de.kogs.timeeater.db.entites.Job;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public interface JobDAO extends CrudRepository<Job, Long> {

}
