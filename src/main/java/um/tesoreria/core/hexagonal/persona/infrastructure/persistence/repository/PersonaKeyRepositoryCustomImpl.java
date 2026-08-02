package um.tesoreria.core.hexagonal.persona.infrastructure.persistence.repository;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaKeyEntity;

@Repository
@RequiredArgsConstructor
public class PersonaKeyRepositoryCustomImpl implements PersonaKeyRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<PersonaKeyEntity> findAllByStrings(List<String> conditions) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PersonaKeyEntity> query = criteriaBuilder.createQuery(PersonaKeyEntity.class);
        Root<PersonaKeyEntity> root = query.from(PersonaKeyEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        conditions.forEach(condition -> predicates.add(
                criteriaBuilder.like(root.get("search"), "%" + condition + "%")));
        query.select(root).where(predicates.toArray(Predicate[]::new));
        query.orderBy(criteriaBuilder.asc(root.get("apellido")), criteriaBuilder.asc(root.get("nombre")));
        return entityManager.createQuery(query).setMaxResults(50).getResultList();
    }
}
