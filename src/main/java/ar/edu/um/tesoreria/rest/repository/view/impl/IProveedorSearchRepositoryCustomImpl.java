package ar.edu.um.tesoreria.rest.repository.view.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.ProveedorSearch;
import ar.edu.um.tesoreria.rest.repository.view.IProveedorSearchRepositoryCustom;

@Repository
public class IProveedorSearchRepositoryCustomImpl implements IProveedorSearchRepositoryCustom {

	@Autowired
	private EntityManager entityManager;

	@Override
	public List<ProveedorSearch> findAllByStrings(List<String> conditions) {

		CriteriaBuilder criteriabuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProveedorSearch> query = criteriabuilder.createQuery(ProveedorSearch.class);
		Root<ProveedorSearch> root = query.from(ProveedorSearch.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		conditions.forEach(condition -> {
			predicates.add(criteriabuilder.like(root.get("search"), "%" + condition + "%"));
		});
		query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
		query.orderBy(criteriabuilder.asc(root.get("razonSocial")));
		return entityManager.createQuery(query).setMaxResults(50).getResultList();
	}

}
