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

import um.tesoreria.core.model.view.ArticuloKey;
import um.tesoreria.core.repository.view.IArticuloKeyRepositoryCustom;

/**
 * @author daniel
 *
 */
@Repository
public class IArticuloKeyRepositoryCustomImpl implements IArticuloKeyRepositoryCustom {

	@Autowired
	private EntityManager entityManager;

	@Override
	public List<ArticuloKey> findAllByStrings(List<String> conditions) {
		CriteriaBuilder criteriabuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ArticuloKey> query = criteriabuilder.createQuery(ArticuloKey.class);
		Root<ArticuloKey> root = query.from(ArticuloKey.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		conditions.forEach(condition -> {
			predicates.add(criteriabuilder.like(root.get("search"), "%" + condition + "%"));
		});
		query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
		query.orderBy(criteriabuilder.asc(root.get("nombre")), criteriabuilder.asc(root.get("articuloId")));
		return entityManager.createQuery(query).setMaxResults(50).getResultList();
	}

}
