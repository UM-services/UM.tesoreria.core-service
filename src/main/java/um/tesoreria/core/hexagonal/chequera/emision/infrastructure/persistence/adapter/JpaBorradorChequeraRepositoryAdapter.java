package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorSede;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.BorradorChequeraEntity;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.BorradorCuotaEntity;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.mapper.BorradorEntityMapper;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository.JpaBorradorChequeraRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JpaBorradorChequeraRepositoryAdapter implements BorradorChequeraRepository {

    private final JpaBorradorChequeraRepository repository;
    private final BorradorEntityMapper mapper;

    @Override
    @Transactional
    public BorradorChequera save(BorradorChequera borrador) {
        BorradorChequeraEntity entity;
        if (borrador.getBorradorId() == null) {
            entity = new BorradorChequeraEntity();
            entity.setCuotas(new ArrayList<>());
        } else {
            entity = repository.findById(borrador.getBorradorId())
                    .orElseThrow(() -> new EmisionException(ReglasEmision.SIN_DATOS,
                            "Borrador " + borrador.getBorradorId() + " inexistente"));
            if (borrador.getVersion() != null && entity.getVersion() != null
                    && !entity.getVersion().equals(borrador.getVersion())) {
                throw new EmisionException(ReglasEmision.BORRADOR_DESACTUALIZADO,
                        "El borrador fue modificado por otra sesión (versión enviada "
                                + borrador.getVersion() + ", actual " + entity.getVersion() + ")");
            }
        }
        mapper.apply(borrador, entity);
        sincronizarCuotas(entity, borrador.getCuotas());
        entity = repository.saveAndFlush(entity);
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BorradorChequera> findById(Long borradorId) {
        return repository.findById(borradorId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BorradorChequera> findByClaveIdempotencia(String claveIdempotencia) {
        if (claveIdempotencia == null || claveIdempotencia.isBlank()) {
            return Optional.empty();
        }
        return repository.findByClaveIdempotencia(claveIdempotencia).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorradorChequera> findAllByUsuarioIdAndEstado(String usuarioId, EstadoBorrador estado) {
        if (usuarioId == null || usuarioId.isBlank()) {
            return List.of();
        }
        return repository.findAllByUsuarioIdAndEstadoOrderByBorradorIdDesc(usuarioId, estado)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(Long borradorId) {
        repository.deleteById(borradorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BorradorChequera> findAbiertoBySerie(Integer facultadId, Integer tipoChequeraId,
                                                        Long chequeraSerieId) {
        if (facultadId == null || tipoChequeraId == null || chequeraSerieId == null) {
            return Optional.empty();
        }
        return repository.findFirstByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndEstadoOrderByBorradorIdDesc(
                facultadId, tipoChequeraId, chequeraSerieId, EstadoBorrador.ABIERTO).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BorradorChequera> findAbiertoByNueva(BigDecimal personaId, Integer documentoId,
                                                         Integer lectivoId, Integer facultadId,
                                                         Integer tipoChequeraId) {
        if (personaId == null || documentoId == null || lectivoId == null || facultadId == null
                || tipoChequeraId == null) {
            return Optional.empty();
        }
        return repository
                .findFirstByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadIdAndTipoChequeraIdAndChequeraSerieIdIsNullAndEstadoOrderByBorradorIdDesc(
                        personaId, documentoId, lectivoId, facultadId, tipoChequeraId, EstadoBorrador.ABIERTO)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorradorChequera> findAllByLectivoIdAndFacultadIdAndEstado(Integer lectivoId, Integer facultadId,
                                                                          EstadoBorrador estado) {
        if (lectivoId == null || facultadId == null || estado == null) {
            return List.of();
        }
        return repository.findAllByLectivoIdAndFacultadIdAndEstadoOrderByBorradorIdAsc(lectivoId, facultadId, estado)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorradorSede> findAllSedesByLectivoIdAndEstado(Integer lectivoId, EstadoBorrador estado) {
        if (lectivoId == null || estado == null) {
            return List.of();
        }
        return repository.findAllByLectivoIdAndEstadoOrderByBorradorIdAsc(lectivoId, estado).stream()
                .map(entity -> new BorradorSede(entity.getFacultadId(), entity.getTipoChequeraId()))
                .toList();
    }

    @Override
    @Transactional
    public int descartarAnterioresA(OffsetDateTime limite) {
        List<BorradorChequeraEntity> vencidos = repository.findAllByEstadoAndUpdatedBefore(
                EstadoBorrador.ABIERTO, limite.toLocalDateTime());
        vencidos.forEach(borrador -> borrador.setEstado(EstadoBorrador.DESCARTADO));
        repository.saveAll(vencidos);
        if (!vencidos.isEmpty()) {
            log.info("Borradores de emisión descartados por retención: {}", vencidos.size());
        }
        return vencidos.size();
    }

    /**
     * Sincroniza las cuotas por clave funcional para no chocar con la unique
     * (borrador, producto, alternativa, cuota) al reinsertar.
     */
    private void sincronizarCuotas(BorradorChequeraEntity entity, List<BorradorCuota> cuotas) {
        List<BorradorCuota> nuevas = cuotas == null ? List.of() : cuotas;
        Map<String, BorradorCuotaEntity> existentes = new LinkedHashMap<>();
        entity.getCuotas().forEach(cuota -> existentes.put(clave(cuota), cuota));

        Map<String, BorradorCuota> deseadas = new LinkedHashMap<>();
        nuevas.forEach(cuota -> deseadas.put(clave(cuota), cuota));

        Iterator<BorradorCuotaEntity> iterator = entity.getCuotas().iterator();
        while (iterator.hasNext()) {
            BorradorCuotaEntity actual = iterator.next();
            if (!deseadas.containsKey(clave(actual))) {
                iterator.remove();
                existentes.remove(clave(actual));
            }
        }

        for (BorradorCuota cuota : deseadas.values()) {
            BorradorCuotaEntity target = existentes.get(clave(cuota));
            if (target == null) {
                target = new BorradorCuotaEntity();
                target.setBorrador(entity);
                entity.getCuotas().add(target);
            }
            mapper.apply(cuota, target);
        }
    }

    private static String clave(BorradorCuotaEntity cuota) {
        return cuota.getProductoId() + "." + cuota.getAlternativaId() + "." + cuota.getCuotaId();
    }

    private static String clave(BorradorCuota cuota) {
        return cuota.getProductoId() + "." + cuota.getAlternativaId() + "." + cuota.getCuotaId();
    }
}
