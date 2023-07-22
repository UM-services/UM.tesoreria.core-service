package um.tesoreria.rest.repository.view.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.view.OrdenPagoDetalle;
import um.tesoreria.rest.model.view.PersonaKey;
import um.tesoreria.rest.repository.view.OrdenPagoDetalleRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrdenPagoDetalleRepositoryCustomImpl implements OrdenPagoDetalleRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<OrdenPagoDetalle> findAllByStrings(List<String> conditions) {
        CriteriaBuilder criteriabuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrdenPagoDetalle> query = criteriabuilder.createQuery(OrdenPagoDetalle.class);
        Root<OrdenPagoDetalle> root = query.from(OrdenPagoDetalle.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        conditions.forEach(condition -> {
            predicates.add(criteriabuilder.like(root.get("ordenPagoDetalle"), "%" + condition + "%"));
        });
        query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(query).setMaxResults(50).getResultList();
    }
}
