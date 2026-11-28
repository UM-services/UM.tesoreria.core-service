package um.tesoreria.core.service;

import um.tesoreria.core.kotlin.model.ChequeraImpresionCabecera;
import um.tesoreria.core.kotlin.model.ChequeraImpresionDetalle;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Compara un snapshot de impresión recién calculado con el último persistido.
 *
 * <p>El objetivo es que un reprint con el mismo contenido no inserte otra cabecera + N
 * detalles. Se ignoran los identificadores y la {@code fecha} de la cabecera (la fecha sí se
 * actualiza aparte para conservar el "último impreso").</p>
 */
public final class ChequeraImpresionSnapshotComparator {

    private ChequeraImpresionSnapshotComparator() {
    }

    public static boolean sameSnapshot(ChequeraImpresionCabecera cabecera,
                                       List<ChequeraImpresionDetalle> detalles,
                                       ChequeraImpresionCabecera otherCabecera,
                                       List<ChequeraImpresionDetalle> otherDetalles) {
        if (cabecera == null || otherCabecera == null || detalles == null || otherDetalles == null) {
            return false;
        }
        return sameCabecera(cabecera, otherCabecera) && sameDetalles(detalles, otherDetalles);
    }

    static boolean sameCabecera(ChequeraImpresionCabecera a, ChequeraImpresionCabecera b) {
        return Objects.equals(a.getFacultadId(), b.getFacultadId())
                && Objects.equals(a.getTipoChequeraId(), b.getTipoChequeraId())
                && Objects.equals(a.getChequeraSerieId(), b.getChequeraSerieId())
                && sameDecimal(a.getPersonaId(), b.getPersonaId())
                && Objects.equals(a.getDocumentoId(), b.getDocumentoId())
                && Objects.equals(a.getLectivoId(), b.getLectivoId())
                && Objects.equals(a.getGeograficaId(), b.getGeograficaId())
                && Objects.equals(a.getArancelTipoId(), b.getArancelTipoId())
                && Objects.equals(a.getAlternativaId(), b.getAlternativaId())
                && Objects.equals(a.getUsuarioId(), b.getUsuarioId())
                && Objects.equals(a.getVersion(), b.getVersion())
                && Objects.equals(a.getHpum(), b.getHpum())
                && sameDecimal(a.getBecaPorcentaje(), b.getBecaPorcentaje())
                && Objects.equals(a.getBecaResolucion(), b.getBecaResolucion())
                && sameInstant(a.getBecaFecha(), b.getBecaFecha())
                && Objects.equals(a.getBecaUserId(), b.getBecaUserId());
    }

    static boolean sameDetalles(List<ChequeraImpresionDetalle> a, List<ChequeraImpresionDetalle> b) {
        if (a.size() != b.size()) {
            return false;
        }
        List<ChequeraImpresionDetalle> left = a.stream().sorted(detailOrder()).toList();
        List<ChequeraImpresionDetalle> right = b.stream().sorted(detailOrder()).toList();
        for (int index = 0; index < left.size(); index++) {
            if (!sameDetalle(left.get(index), right.get(index))) {
                return false;
            }
        }
        return true;
    }

    private static Comparator<ChequeraImpresionDetalle> detailOrder() {
        return Comparator
                .comparing(ChequeraImpresionDetalle::getProductoId, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(ChequeraImpresionDetalle::getAlternativaId, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(ChequeraImpresionDetalle::getCuotaId, Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    private static boolean sameDetalle(ChequeraImpresionDetalle a, ChequeraImpresionDetalle b) {
        return Objects.equals(a.getFacultadId(), b.getFacultadId())
                && Objects.equals(a.getTipoChequeraId(), b.getTipoChequeraId())
                && Objects.equals(a.getChequeraSerieId(), b.getChequeraSerieId())
                && Objects.equals(a.getProductoId(), b.getProductoId())
                && Objects.equals(a.getAlternativaId(), b.getAlternativaId())
                && Objects.equals(a.getCuotaId(), b.getCuotaId())
                && Objects.equals(a.getTitulo(), b.getTitulo())
                && sameDecimal(a.getTotal(), b.getTotal())
                && Objects.equals(a.getCuotas(), b.getCuotas())
                && Objects.equals(a.getMes(), b.getMes())
                && Objects.equals(a.getAnho(), b.getAnho())
                && Objects.equals(a.getArancelTipoId(), b.getArancelTipoId())
                && sameInstant(a.getVencimiento1(), b.getVencimiento1())
                && sameDecimal(a.getImporte1(), b.getImporte1())
                && sameInstant(a.getVencimiento2(), b.getVencimiento2())
                && sameDecimal(a.getImporte2(), b.getImporte2())
                && sameInstant(a.getVencimiento3(), b.getVencimiento3())
                && sameDecimal(a.getImporte3(), b.getImporte3())
                && Objects.equals(a.getUsuarioId(), b.getUsuarioId())
                && Objects.equals(a.getVersion(), b.getVersion());
    }

    private static boolean sameDecimal(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.compareTo(b) == 0;
    }

    private static boolean sameInstant(OffsetDateTime a, OffsetDateTime b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.toInstant().equals(b.toInstant());
    }

}
