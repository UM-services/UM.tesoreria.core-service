package um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.personas.documento.infrastructure.persistence.entity.DocumentoEntity;
import um.tesoreria.core.hexagonal.personas.documento.infrastructure.persistence.repository.JpaDocumentoRepository;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaSugerencia;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaSugerenciaRepository;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.entity.UsuarioChequeraFacultadEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sugerencias de persona para un usuario: personas que tienen al menos una chequera serie en alguna
 * de las facultades asignadas al usuario ({@code EXISTS chequera_serie ⋈ usuario_chequera_facultad})
 * y cuyo apellido o nombre contiene todos los términos.
 *
 * <p>Se resuelve con Criteria y proyección de tupla (no se hidratan entidades ni asociaciones). El
 * orden replica el del legacy: primero las que empiezan por el primer término, después las que lo
 * tienen como palabra, después el resto.</p>
 */
@Repository
@RequiredArgsConstructor
public class JpaPersonaSugerenciaRepositoryAdapter implements PersonaSugerenciaRepository {

    private final EntityManager entityManager;
    private final JpaDocumentoRepository documentoRepository;

    @Override
    public List<PersonaSugerencia> findByUsuarioAndTerms(Long userId, List<String> terms, int limite) {
        if (terms == null || terms.isEmpty()) {
            return List.of();
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<PersonaEntity> persona = query.from(PersonaEntity.class);

        List<Predicate> condiciones = new ArrayList<>();
        condiciones.add(builder.exists(chequeraDeFacultadesDelUsuario(query, builder, persona, userId)));
        for (String term : terms) {
            String patron = "%" + escapeLike(term) + "%";
            condiciones.add(builder.or(
                    builder.like(persona.get("apellido"), patron, '!'),
                    builder.like(persona.get("nombre"), patron, '!')));
        }

        String primerTermino = escapeLike(terms.getFirst());
        Expression<Integer> prioridad = builder.<Integer>selectCase()
                .when(builder.like(persona.get("apellido"), primerTermino + "%", '!'), 0)
                .when(builder.like(persona.get("apellido"), "% " + primerTermino + "%", '!'), 1)
                .otherwise(2);

        query.multiselect(
                        persona.get("personaId").alias("personaId"),
                        persona.get("documentoId").alias("documentoId"),
                        persona.get("apellido").alias("apellido"),
                        persona.get("nombre").alias("nombre"))
                .where(condiciones.toArray(Predicate[]::new))
                .orderBy(builder.asc(prioridad),
                        builder.asc(persona.get("apellido")),
                        builder.asc(persona.get("nombre")),
                        builder.asc(persona.get("personaId")),
                        builder.asc(persona.get("documentoId")));

        List<Tuple> filas = entityManager.createQuery(query).setMaxResults(limite).getResultList();
        Map<Integer, String> documentos = documentos(filas);
        return filas.stream()
                .map(fila -> new PersonaSugerencia(
                        fila.get("personaId", BigDecimal.class),
                        fila.get("documentoId", Integer.class),
                        documentos.get(fila.get("documentoId", Integer.class)),
                        fila.get("apellido", String.class),
                        fila.get("nombre", String.class)))
                .toList();
    }

    /** {@code EXISTS (chequera_serie de las facultades del usuario para esta persona)}. */
    private Subquery<Integer> chequeraDeFacultadesDelUsuario(CriteriaQuery<?> query, CriteriaBuilder builder,
                                                             Root<PersonaEntity> persona, Long userId) {
        Subquery<Integer> existe = query.subquery(Integer.class);
        Root<ChequeraSerieEntity> serie = existe.from(ChequeraSerieEntity.class);

        Subquery<Integer> facultades = existe.subquery(Integer.class);
        Root<UsuarioChequeraFacultadEntity> asignada = facultades.from(UsuarioChequeraFacultadEntity.class);
        facultades.select(asignada.get("facultadId"))
                .where(builder.equal(asignada.get("userId"), userId));

        existe.select(builder.literal(1)).where(
                builder.equal(serie.get("personaId"), persona.get("personaId")),
                builder.equal(serie.get("documentoId"), persona.get("documentoId")),
                serie.get("facultadId").in(facultades));
        return existe;
    }

    private Map<Integer, String> documentos(List<Tuple> filas) {
        Set<Integer> ids = new LinkedHashSet<>();
        for (Tuple fila : filas) {
            Integer documentoId = fila.get("documentoId", Integer.class);
            if (documentoId != null) {
                ids.add(documentoId);
            }
        }
        return documentoRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(DocumentoEntity::getDocumentoId, DocumentoEntity::getNombre, (a, b) -> a));
    }

    private static String escapeLike(String term) {
        return term.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }

}
