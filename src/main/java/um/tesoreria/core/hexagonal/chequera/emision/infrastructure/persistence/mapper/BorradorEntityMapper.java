package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.BorradorChequeraEntity;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.BorradorCuotaEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BorradorEntityMapper {

    public BorradorChequera toDomain(BorradorChequeraEntity entity) {
        if (entity == null) {
            return null;
        }
        return BorradorChequera.builder()
                .borradorId(entity.getBorradorId())
                .estado(entity.getEstado())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .facultadId(entity.getFacultadId())
                .lectivoId(entity.getLectivoId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .arancelTipoId(entity.getArancelTipoId())
                .alternativaId(entity.getAlternativaId())
                .cursoId(entity.getCursoId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .fecha(entity.getFecha())
                .hpum(nz(entity.getHpum()))
                .becaPorcentaje(nz(entity.getBecaPorcentaje()))
                .becaResolucion(entity.getBecaResolucion())
                .becaFecha(entity.getBecaFecha())
                .becaUserId(entity.getBecaUserId())
                .calculaBeca(esVerdadero(entity.getCalculaBeca()))
                .emailPersonal(entity.getEmailPersonal())
                .emailInstitucional(entity.getEmailInstitucional())
                .emailCopia(entity.getEmailCopia())
                .incluyeBarras(esVerdadero(entity.getIncluyeBarras()))
                .copiaFacultad(esVerdadero(entity.getCopiaFacultad()))
                .chequeraId(entity.getChequeraId())
                .chequeraSerieIdEmitida(entity.getChequeraSerieIdEmitida())
                .auditoriaId(entity.getAuditoriaId())
                .claveIdempotencia(entity.getClaveIdempotencia())
                .usuarioId(entity.getUsuarioId())
                .version(entity.getVersion())
                .createdAt(toOffset(entity.getCreated()))
                .updatedAt(toOffset(entity.getUpdated()))
                .emitidoAt(toOffset(entity.getEmitidoAt()))
                .cuotas(toCuotasDomain(entity.getCuotas()))
                .build();
    }

    public List<BorradorCuota> toCuotasDomain(List<BorradorCuotaEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .sorted(Comparator.comparing(BorradorCuotaEntity::getProductoId)
                        .thenComparing(BorradorCuotaEntity::getAlternativaId)
                        .thenComparing(BorradorCuotaEntity::getCuotaId))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public BorradorCuota toDomain(BorradorCuotaEntity entity) {
        return BorradorCuota.builder()
                .borradorCuotaId(entity.getBorradorCuotaId())
                .chequeraCuotaId(entity.getChequeraCuotaId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .mes(entity.getMes())
                .anio(entity.getAnio())
                .vencimiento1(entity.getVencimiento1())
                .vencimiento2(entity.getVencimiento2())
                .vencimiento3(entity.getVencimiento3())
                .importe1Original(nz(entity.getImporte1Original()))
                .importe2Original(nz(entity.getImporte2Original()))
                .importe3Original(nz(entity.getImporte3Original()))
                .importe1Borrador(nz(entity.getImporte1Borrador()))
                .importe2Borrador(nz(entity.getImporte2Borrador()))
                .importe3Borrador(nz(entity.getImporte3Borrador()))
                .arancelTipoId(entity.getArancelTipoId())
                .tramoId(entity.getTramoId())
                .descuentoPagoTermino(nz(entity.getDescuentoPagoTermino()))
                .pagado(nz(entity.getPagado()))
                .baja(nz(entity.getBaja()))
                .manual(nz(entity.getManual()))
                .compensada(nz(entity.getCompensada()))
                .modificado(nz(entity.getModificado()))
                .bloqueado(nz(entity.getBloqueado()))
                .build();
    }

    public void apply(BorradorChequera domain, BorradorChequeraEntity entity) {
        entity.setEstado(domain.getEstado() == null ? EstadoBorrador.ABIERTO : domain.getEstado());
        entity.setPersonaId(domain.getPersonaId());
        entity.setDocumentoId(domain.getDocumentoId());
        entity.setFacultadId(domain.getFacultadId());
        entity.setLectivoId(domain.getLectivoId());
        entity.setTipoChequeraId(domain.getTipoChequeraId());
        entity.setArancelTipoId(domain.getArancelTipoId());
        entity.setAlternativaId(domain.getAlternativaId());
        entity.setCursoId(domain.getCursoId());
        entity.setChequeraSerieId(domain.getChequeraSerieId());
        entity.setFecha(domain.getFecha());
        entity.setHpum(domain.getHpum());
        entity.setBecaPorcentaje(domain.getBecaPorcentaje());
        entity.setBecaResolucion(domain.getBecaResolucion());
        entity.setBecaFecha(domain.getBecaFecha());
        entity.setBecaUserId(domain.getBecaUserId());
        entity.setCalculaBeca((byte) (domain.isCalculaBeca() ? 1 : 0));
        entity.setEmailPersonal(domain.getEmailPersonal());
        entity.setEmailInstitucional(domain.getEmailInstitucional());
        entity.setEmailCopia(domain.getEmailCopia());
        entity.setIncluyeBarras((byte) (domain.isIncluyeBarras() ? 1 : 0));
        entity.setCopiaFacultad((byte) (domain.isCopiaFacultad() ? 1 : 0));
        entity.setChequeraId(domain.getChequeraId());
        entity.setChequeraSerieIdEmitida(domain.getChequeraSerieIdEmitida());
        entity.setAuditoriaId(domain.getAuditoriaId());
        entity.setClaveIdempotencia(domain.getClaveIdempotencia());
        entity.setUsuarioId(domain.getUsuarioId());
        entity.setEmitidoAt(domain.getEmitidoAt() == null ? null : domain.getEmitidoAt().toLocalDateTime());
    }

    public void apply(BorradorCuota domain, BorradorCuotaEntity entity) {
        entity.setChequeraCuotaId(domain.getChequeraCuotaId());
        entity.setProductoId(domain.getProductoId());
        entity.setAlternativaId(domain.getAlternativaId());
        entity.setCuotaId(domain.getCuotaId());
        entity.setMes(domain.getMes());
        entity.setAnio(domain.getAnio());
        entity.setVencimiento1(domain.getVencimiento1());
        entity.setVencimiento2(domain.getVencimiento2());
        entity.setVencimiento3(domain.getVencimiento3());
        entity.setImporte1Original(nz(domain.getImporte1Original()));
        entity.setImporte2Original(nz(domain.getImporte2Original()));
        entity.setImporte3Original(nz(domain.getImporte3Original()));
        entity.setImporte1Borrador(nz(domain.getImporte1Borrador()));
        entity.setImporte2Borrador(nz(domain.getImporte2Borrador()));
        entity.setImporte3Borrador(nz(domain.getImporte3Borrador()));
        entity.setArancelTipoId(domain.getArancelTipoId());
        entity.setTramoId(domain.getTramoId());
        entity.setDescuentoPagoTermino(nz(domain.getDescuentoPagoTermino()));
        entity.setPagado(nz(domain.getPagado()));
        entity.setBaja(nz(domain.getBaja()));
        entity.setManual(nz(domain.getManual()));
        entity.setCompensada(nz(domain.getCompensada()));
        entity.setModificado(nz(domain.getModificado()));
        entity.setBloqueado(nz(domain.getBloqueado()));
    }

    private static BigDecimal nz(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static Byte nz(Byte valor) {
        return valor == null ? 0 : valor;
    }

    private static boolean esVerdadero(Byte valor) {
        return valor != null && valor != 0;
    }

    private static OffsetDateTime toOffset(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
