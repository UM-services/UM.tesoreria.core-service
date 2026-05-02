/**
 * 
 */
package um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.repository.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.entity.ProveedorSearchEntity;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.repository.JpaProveedorSearchRepositoryCustom;

/**
 * @author daniel
 *
 */
@Repository
public class JpaProveedorSearchRepositoryCustomImpl implements JpaProveedorSearchRepositoryCustom {

	private final EntityManager entityManager;

	public JpaProveedorSearchRepositoryCustomImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<ProveedorSearchEntity> findAllByStrings(List<String> conditions) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProveedorSearchEntity> criteriaQuery = criteriaBuilder.createQuery(ProveedorSearchEntity.class);
		Root<ProveedorSearchEntity> root = criteriaQuery.from(ProveedorSearchEntity.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		conditions.forEach(condition -> {
			predicates.add(criteriaBuilder.like(root.get("search"), "%" + condition + "%"));
		});
		criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("razonSocial")));
		return entityManager.createQuery(criteriaQuery).setMaxResults(50).getResultList();
	}

}
