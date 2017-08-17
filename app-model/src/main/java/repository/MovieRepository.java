package repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.Movie;

@Stateless
public class MovieRepository {

	@PersistenceContext
	EntityManager em;

	public Movie add(final Movie employee) {
		em.persist(employee);
		return employee;
	}

	public Movie findById(final Long id) {
		if (id == null) {
			return null;
		}

		return em.find(Movie.class, id);
	}

	public void update(final Movie employee) {
		em.merge(employee);
	}

	@SuppressWarnings("unchecked")
	public List<Movie> findAll(final String orderField) {
		return em.createQuery("Select e From Movie e Order by e." + orderField).getResultList();
	}

	public boolean alreadyExists(final Movie movie) {
		final StringBuilder jpql = new StringBuilder();
		jpql.append("Select 1 From Employee e where e.name = :name");
		if (movie.getId() != null) {
			jpql.append(" And e.id != :id");
		}

		final Query query = em.createQuery(jpql.toString());
		query.setParameter("name", movie.getName());
		if (movie.getId() != null) {
			query.setParameter("id", movie.getId());
		}

		return query.setMaxResults(1).getResultList().size() > 0;
	}

	public boolean existsById(final Long id) {
		return em.createQuery("Select 1 From Movie e where e.id = :id").setParameter("id", id).setMaxResults(1)
				.getResultList().size() > 0;
	}

}
