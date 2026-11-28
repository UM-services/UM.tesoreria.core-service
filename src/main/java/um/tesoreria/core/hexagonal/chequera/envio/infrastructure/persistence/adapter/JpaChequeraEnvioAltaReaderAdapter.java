package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.ChequeraEnvioAltaEntity;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.persistence.entity.DomicilioEntity;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.service.facade.ToolService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Padrón de envío masivo: una consulta Criteria sobre la vista {@code vw_chequera_alta} con los
 * joins de persona (obligatorio), tipo de chequera (obligatorio), domicilio y tipo de arancel
 * (opcionales), igual que el SQL anterior.
 *
 * <p>{@code emailPersonalValido}/{@code emailInstitucionalValido} se resuelven con la misma
 * validación del resto del sistema ({@link ToolService#mailValidate}).</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JpaChequeraEnvioAltaReaderAdapter implements ChequeraEnvioAltaReader {

    private final EntityManager entityManager;
    private final ToolService toolService;

    @Override
    public List<ChequeraEnvioAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId,
                                                                                 Integer facultadId,
                                                                                 Integer geograficaId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<ChequeraEnvioAltaEntity> alta = query.from(ChequeraEnvioAltaEntity.class);
        Join<ChequeraEnvioAltaEntity, PersonaEntity> persona = alta.join("persona", JoinType.INNER);
        Join<ChequeraEnvioAltaEntity, TipoChequeraEntity> tipoChequera = alta.join("tipoChequera", JoinType.INNER);
        Join<ChequeraEnvioAltaEntity, ArancelTipoEntity> arancelTipo = alta.join("arancelTipo", JoinType.LEFT);
        Join<ChequeraEnvioAltaEntity, DomicilioEntity> domicilio = alta.join("domicilio", JoinType.LEFT);

        proyectar(query, builder, alta, persona, tipoChequera, arancelTipo, domicilio)
                .where(builder.and(
                        builder.equal(alta.get("lectivoId"), lectivoId),
                        builder.equal(alta.get("facultadId"), facultadId),
                        builder.equal(alta.get("geograficaId"), geograficaId)))
                .orderBy(builder.asc(persona.get("apellido")),
                        builder.asc(persona.get("nombre")),
                        builder.asc(alta.get("chequeraSerieId")));

        List<ChequeraEnvioAlta> altas = altas(entityManager.createQuery(query).getResultList());
        log.debug("Envío masivo: {} series para lectivo {} facultad {} sede {}",
                altas.size(), lectivoId, facultadId, geograficaId);
        return altas;
    }

    @Override
    public Optional<ChequeraEnvioAlta> findByChequeraId(Long chequeraId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<ChequeraEnvioAltaEntity> alta = query.from(ChequeraEnvioAltaEntity.class);
        Join<ChequeraEnvioAltaEntity, PersonaEntity> persona = alta.join("persona", JoinType.INNER);
        Join<ChequeraEnvioAltaEntity, TipoChequeraEntity> tipoChequera = alta.join("tipoChequera", JoinType.INNER);
        Join<ChequeraEnvioAltaEntity, ArancelTipoEntity> arancelTipo = alta.join("arancelTipo", JoinType.LEFT);
        Join<ChequeraEnvioAltaEntity, DomicilioEntity> domicilio = alta.join("domicilio", JoinType.LEFT);

        proyectar(query, builder, alta, persona, tipoChequera, arancelTipo, domicilio)
                .where(builder.equal(alta.get("chequeraId"), chequeraId));

        return altas(entityManager.createQuery(query).getResultList()).stream().findFirst();
    }

    private CriteriaQuery<Tuple> proyectar(CriteriaQuery<Tuple> query, CriteriaBuilder builder,
                                           Root<ChequeraEnvioAltaEntity> alta,
                                           Join<ChequeraEnvioAltaEntity, PersonaEntity> persona,
                                           Join<ChequeraEnvioAltaEntity, TipoChequeraEntity> tipoChequera,
                                           Join<ChequeraEnvioAltaEntity, ArancelTipoEntity> arancelTipo,
                                           Join<ChequeraEnvioAltaEntity, DomicilioEntity> domicilio) {
        return query.multiselect(
                alta.get("chequeraId").alias("chequeraId"),
                alta.get("facultadId").alias("facultadId"),
                alta.get("tipoChequeraId").alias("tipoChequeraId"),
                alta.get("chequeraSerieId").alias("chequeraSerieId"),
                alta.get("alternativaId").alias("alternativaId"),
                alta.get("personaId").alias("personaId"),
                alta.get("documentoId").alias("documentoId"),
                alta.get("lectivoId").alias("lectivoId"),
                alta.get("geograficaId").alias("geograficaId"),
                persona.get("apellido").alias("apellido"),
                persona.get("nombre").alias("nombre"),
                tipoChequera.get("nombre").alias("tipoChequera"),
                arancelTipo.get("descripcion").alias("arancelTipo"),
                domicilio.get("emailPersonal").alias("emailPersonal"),
                domicilio.get("emailInstitucional").alias("emailInstitucional"),
                alta.get("enviado").alias("enviado"));
    }

    private List<ChequeraEnvioAlta> altas(List<Tuple> filas) {
        List<ChequeraEnvioAlta> altas = new ArrayList<>(filas.size());
        for (Tuple fila : filas) {
            altas.add(alta(fila));
        }
        return altas;
    }

    private ChequeraEnvioAlta alta(Tuple fila) {
        String emailPersonal = fila.get("emailPersonal", String.class);
        String emailInstitucional = fila.get("emailInstitucional", String.class);
        Byte enviado = fila.get("enviado", Byte.class);
        return new ChequeraEnvioAlta(
                fila.get("chequeraId", Long.class),
                fila.get("facultadId", Integer.class),
                fila.get("tipoChequeraId", Integer.class),
                fila.get("chequeraSerieId", Long.class),
                fila.get("alternativaId", Integer.class),
                fila.get("personaId", BigDecimal.class),
                fila.get("documentoId", Integer.class),
                fila.get("lectivoId", Integer.class),
                fila.get("geograficaId", Integer.class),
                fila.get("apellido", String.class),
                fila.get("nombre", String.class),
                fila.get("tipoChequera", String.class),
                fila.get("arancelTipo", String.class),
                emailPersonal,
                emailInstitucional,
                esValido(emailPersonal),
                esValido(emailInstitucional),
                enviado != null && enviado != 0);
    }

    private boolean esValido(String email) {
        return email != null && !email.isBlank() && Boolean.TRUE.equals(toolService.mailValidate(email.trim()));
    }

}
