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

import um.tesoreria.core.model.view.CuentaSearch;
import um.tesoreria.core.repository.view.CuentaSearchRepositoryCustom;

/**
 * @author daniel
 *
 */
public class CuentaSearchRepositoryCustomImpl implements CuentaSearchRepositoryCustom {

	private final EntityManager entityManager;

	public CuentaSearchRepositoryCustomImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<CuentaSearch> findAllByStrings(List<String> conditions, Boolean visible) {
		CriteriaBuilder criteriabuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CuentaSearch> query = criteriabuilder.createQuery(CuentaSearch.class);
		Root<CuentaSearch> root = query.from(CuentaSearch.class);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		conditions.forEach(condition -> {
			predicates.add(criteriabuilder.like(root.get("search"), "%" + condition + "%"));
		});
		if (visible) {
			predicates.add(criteriabuilder.equal(root.get("visible"), 1));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]));
		query.orderBy(criteriabuilder.asc(root.get("numeroCuenta")));
		return entityManager.createQuery(query).setMaxResults(50).getResultList();
	}

}
