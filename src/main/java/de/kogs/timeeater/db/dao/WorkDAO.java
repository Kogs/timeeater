/**
 *
 */
package de.kogs.timeeater.db.dao;

import org.springframework.data.repository.CrudRepository;

import de.kogs.timeeater.db.entites.Work;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public interface WorkDAO extends CrudRepository<Work, Long> {

}
