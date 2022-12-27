/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.um.tesoreria.rest.model.view.CuentaSearch;
import ar.edu.um.tesoreria.rest.repository.view.ICuentaSearchRepositoryCustom;

/**
 * @author daniel
 *
 */
public class ICuentaSearchRepositoryCustomImpl implements ICuentaSearchRepositoryCustom {

	@Autowired
	private EntityManager entityManager;

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
		query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
		query.orderBy(criteriabuilder.asc(root.get("numeroCuenta")));
		return entityManager.createQuery(query).setMaxResults(50).getResultList();
	}

}
