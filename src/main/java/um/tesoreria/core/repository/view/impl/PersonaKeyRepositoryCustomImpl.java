/**
 * 
 */
package um.tesoreria.core.repository.view.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.repository.view.PersonaKeyRepositoryCustom;

/**
 * @author daniel
 *
 */
@Repository
public class PersonaKeyRepositoryCustomImpl implements PersonaKeyRepositoryCustom {
	
	private final EntityManager entityManager;

	public PersonaKeyRepositoryCustomImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<PersonaKey> findAllByStrings(List<String> conditions) {
		CriteriaBuilder criteriabuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PersonaKey> query = criteriabuilder.createQuery(PersonaKey.class);
		Root<PersonaKey> root = query.from(PersonaKey.class);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		conditions.forEach(condition -> {
			predicates.add(criteriabuilder.like(root.get("search"), "%" + condition + "%"));
		});
		query.select(root).where(predicates.toArray(new Predicate[0]));
		query.orderBy(criteriabuilder.asc(root.get("apellido")), criteriabuilder.asc(root.get("nombre")));
		return entityManager.createQuery(query).setMaxResults(50).getResultList();
	}

}
