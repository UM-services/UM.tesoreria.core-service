package um.tesoreria.core.repository.view.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.model.view.TipoChequeraSearch;
import um.tesoreria.core.repository.view.TipoChequeraSearchRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TipoChequeraSearchRepositoryCustomImpl implements TipoChequeraSearchRepositoryCustom {

    private final EntityManager entityManager;

    public TipoChequeraSearchRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<TipoChequeraSearch> findAllByStrings(List<String> conditions) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TipoChequeraSearch> criteriaQuery = criteriaBuilder.createQuery(TipoChequeraSearch.class);
        Root<TipoChequeraSearch> root = criteriaQuery.from(TipoChequeraSearch.class);

        List<Predicate> predicates = new ArrayList<>();
        conditions.forEach(condition -> {
            predicates.add(criteriaBuilder.like(root.get("search"), "%" + condition + "%"));
        });
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("nombre")));
        return entityManager.createQuery(criteriaQuery).setMaxResults(50).getResultList();
    }

}
