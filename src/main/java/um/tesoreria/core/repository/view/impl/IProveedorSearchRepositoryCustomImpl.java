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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.ProveedorSearch;
import um.tesoreria.core.repository.view.IProveedorSearchRepositoryCustom;

/**
 * @author daniel
 *
 */
@Repository
public class IProveedorSearchRepositoryCustomImpl implements IProveedorSearchRepositoryCustom {

	@Autowired
	private EntityManager entityManager;

	@Override
	public List<ProveedorSearch> findAllByStrings(List<String> conditions) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProveedorSearch> criteriaQuery = criteriaBuilder.createQuery(ProveedorSearch.class);
		Root<ProveedorSearch> root = criteriaQuery.from(ProveedorSearch.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		conditions.forEach(condition -> {
			predicates.add(criteriaBuilder.like(root.get("search"), "%" + condition + "%"));
		});
		criteriaQuery.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("razonSocial")));
		return entityManager.createQuery(criteriaQuery).setMaxResults(50).getResultList();
	}

}
