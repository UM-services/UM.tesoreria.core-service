package um.tesoreria.core.hexagonal.chequera.lectivoCuota.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.out.LectivoCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.infrastructure.persistence.entity.LectivoCuotaEntity;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.infrastructure.persistence.mapper.LectivoCuotaMapper;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.infrastructure.persistence.repository.JpaLectivoCuotaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaLectivoCuotaRepositoryAdapter implements LectivoCuotaRepository {

    private final JpaLectivoCuotaRepository jpaLectivoCuotaRepository;
    private final LectivoCuotaMapper lectivoCuotaMapper;

    @Override
    public List<LectivoCuota> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return jpaLectivoCuotaRepository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipoChequeraId)
                .stream()
                .map(lectivoCuotaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LectivoCuota> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer alternativaId) {
        return jpaLectivoCuotaRepository.findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(facultadId, lectivoId, tipoChequeraId, alternativaId)
                .stream()
                .map(lectivoCuotaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LectivoCuota> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaIdAndCuotaId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        return jpaLectivoCuotaRepository.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaIdAndCuotaId(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, cuotaId)
                .map(lectivoCuotaMapper::toDomain);
    }

    @Override
    public List<LectivoCuota> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, OffsetDateTime fechaReferencia) {
        return jpaLectivoCuotaRepository.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaIdAndVencimiento2GreaterThanEqualAndImporte2GreaterThan(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, fechaReferencia, BigDecimal.ZERO)
                .stream()
                .map(lectivoCuotaMapper::toDomain)
                .collect(Collectors.toList());
    }
}
