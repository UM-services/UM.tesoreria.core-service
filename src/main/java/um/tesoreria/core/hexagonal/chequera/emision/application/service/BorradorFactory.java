package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.service.ArancelPorcentajeService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.AlternativaEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.DatosConstruccion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Construye y valida las cuotas del borrador.
 *
 * <p>Es el port fiel de {@code clsCtlChequera.constructChequera} (líneas 333-478) y de
 * {@code clsDAOCheqCuota.validate}. Las diferencias están documentadas: redondeo
 * {@code HALF_UP} a 2 decimales en lugar de {@code CLng} (Q6) y ausencia del
 * {@code SET FOREIGN_KEY_CHECKS = 0} del legacy.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BorradorFactory {

    private final ArancelPorcentajeService arancelPorcentajeService;

    public List<BorradorCuota> construir(List<ChequeraCuota> persistidas, DatosConstruccion datos) {
        List<BorradorCuota> base = new ArrayList<>();
        if (persistidas != null) {
            persistidas.forEach(cuota -> base.add(fromChequeraCuota(cuota)));
        }
        return construirDesdeBorrador(base, datos);
    }

    /**
     * Igual que {@link #construir}, pero partiendo de las cuotas ya volcadas en el
     * borrador (se usa al recalcular beca/arancel o al completar faltantes).
     */
    public List<BorradorCuota> construirDesdeBorrador(List<BorradorCuota> base, DatosConstruccion datos) {
        int arancelTipoIdChequera = datos.getArancelTipoIdChequera() == null || datos.getArancelTipoIdChequera() == 0
                ? datos.getArancelTipoIdSeleccionado()
                : datos.getArancelTipoIdChequera();

        Map<String, BorradorCuota> cuotas = new LinkedHashMap<>();
        if (base != null) {
            base.forEach(cuota -> cuotas.put(cuota.key(), cuota));
        }

        Map<String, BigDecimal> porcentajes = new LinkedHashMap<>();
        OffsetDateTime hoy = OffsetDateTime.now();

        if (datos.getDiseno() != null) {
            for (LectivoCuota diseno : datos.getDiseno()) {
                String key = diseno.getProductoId() + "." + diseno.getAlternativaId() + "." + diseno.getCuotaId();
                BorradorCuota cuota = cuotas.get(key);
                int puntosDescuento = descuentoPagoTermino(datos.getAlternativas(), diseno.getAlternativaId());
                BigDecimal multiplicador = ReglasEmision.multiplicadorDescuentoPagoTermino(puntosDescuento);

                if (cuota == null) {
                    cuota = nuevaDesdeDiseno(diseno, arancelTipoIdChequera, datos.isEnTerminoAnterior(),
                            puntosDescuento, multiplicador);
                    cuotas.put(key, cuota);
                } else {
                    refrescarDesdeDiseno(cuota, diseno, datos.isEnTerminoAnterior(), hoy, puntosDescuento,
                            multiplicador);
                }

                // Regla temporal heredada (clsCtlChequera 451-456)
                cuota.setArancelTipoId(ReglasEmision.periodo(cuota.getAnio(), cuota.getMes())
                        > ReglasEmision.CORTE_ARANCEL_POR_CUOTA
                        ? datos.getArancelTipoIdSeleccionado()
                        : arancelTipoIdChequera);

                aplicarBeca(cuota, datos, porcentajes);
                aplicarDescuentoPagoTermino(cuota, datos);
            }
        }
        return new ArrayList<>(cuotas.values());
    }

    /**
     * Mezcla las ediciones del frontend con las cuotas persistidas del borrador.
     */
    public void aplicarEdiciones(List<BorradorCuota> actuales, List<BorradorCuota> editadas) {
        if (editadas == null || editadas.isEmpty()) {
            return;
        }
        Map<String, BorradorCuota> porClave = new LinkedHashMap<>();
        actuales.forEach(cuota -> porClave.put(cuota.key(), cuota));

        for (BorradorCuota editada : editadas) {
            BorradorCuota actual = porClave.get(editada.key());
            if (actual == null) {
                actual = BorradorCuota.builder()
                        .productoId(editada.getProductoId())
                        .alternativaId(editada.getAlternativaId())
                        .cuotaId(editada.getCuotaId())
                        .pagado((byte) 0)
                        .baja((byte) 0)
                        .manual((byte) 0)
                        .compensada((byte) 0)
                        .modificado((byte) 1)
                        .bloqueado((byte) 0)
                        .build();
                actuales.add(actual);
                porClave.put(actual.key(), actual);
            }

            boolean cambioImporte = cambio(actual.getImporte1Borrador(), editada.getImporte1Borrador())
                    || cambio(actual.getImporte2Borrador(), editada.getImporte2Borrador())
                    || cambio(actual.getImporte3Borrador(), editada.getImporte3Borrador());
            boolean cambioVencimiento = cambio(actual.getVencimiento1(), editada.getVencimiento1())
                    || cambio(actual.getVencimiento2(), editada.getVencimiento2())
                    || cambio(actual.getVencimiento3(), editada.getVencimiento3());
            boolean cambioPeriodo = cambio(actual.getMes(), editada.getMes())
                    || cambio(actual.getAnio(), editada.getAnio());
            boolean cambioBandera = cambio(actual.getBaja(), editada.getBaja())
                    || cambio(actual.getCompensada(), editada.getCompensada());

            if (actual.inmutable()) {
                if (cambioImporte || cambioVencimiento || cambioPeriodo || cambioBandera) {
                    throw new EmisionException(ReglasEmision.CUOTA_INMUTABLE,
                            "La cuota " + actual.key()
                                    + " está pagada, dada de baja, compensada o bloqueada y no se puede modificar");
                }
                continue;
            }

            actual.setMes(valor(editada.getMes(), actual.getMes()));
            actual.setAnio(valor(editada.getAnio(), actual.getAnio()));
            actual.setVencimiento1(valor(editada.getVencimiento1(), actual.getVencimiento1()));
            actual.setVencimiento2(valor(editada.getVencimiento2(), actual.getVencimiento2()));
            actual.setVencimiento3(valor(editada.getVencimiento3(), actual.getVencimiento3()));
            actual.setImporte1Borrador(ReglasEmision.redondear2(valor(editada.getImporte1Borrador(), actual.getImporte1Borrador())));
            actual.setImporte2Borrador(ReglasEmision.redondear2(valor(editada.getImporte2Borrador(), actual.getImporte2Borrador())));
            actual.setImporte3Borrador(ReglasEmision.redondear2(valor(editada.getImporte3Borrador(), actual.getImporte3Borrador())));

            if (editada.getCompensada() != null) {
                actual.setCompensada(editada.getCompensada());
            }
            if (editada.getBaja() != null) {
                actual.setBaja(editada.getBaja());
            }
            if (BorradorCuota.esVerdadero(editada.getManual()) || cambioImporte || cambioVencimiento) {
                actual.setManual((byte) 1);
                actual.setModificado((byte) 1);
            }
            // `bloqueado` refleja el candado de origen (pagado/baja/compensada/manual de la
            // chequera real); editar en el borrador marca `manual` pero no bloquea la cuota.
            actual.setBloqueado((byte) (actual.inmutable() ? 1 : 0));
        }
    }

    public void validar(List<BorradorCuota> cuotas) {
        if (cuotas == null || cuotas.isEmpty()) {
            throw new EmisionException(ReglasEmision.SIN_CUOTAS, "El borrador no tiene cuotas para emitir");
        }
        for (BorradorCuota cuota : cuotas) {
            if (cuota.bloqueada()) {
                continue;
            }
            if (!ReglasEmision.cuotaConsistente(cuota.getImporte1Borrador(), cuota.getImporte2Borrador(),
                    cuota.getImporte3Borrador(), cuota.getVencimiento1(), cuota.getVencimiento2(),
                    cuota.getVencimiento3())) {
                throw new EmisionException(ReglasEmision.CUOTA_INCONSISTENTE,
                        "Cuota inconsistente: " + cuota.key()
                                + ". Se exige importe2 >= importe1, importe3 >= importe2 y vencimientos no decrecientes");
            }
        }
    }

    private BorradorCuota nuevaDesdeDiseno(LectivoCuota diseno, int arancelTipoId, boolean enTerminoAnterior,
                                           int puntosDescuento, BigDecimal multiplicador) {
        BigDecimal importe1Borrador = ReglasEmision.redondear2(diseno.getImporte1());
        return BorradorCuota.builder()
                .productoId(diseno.getProductoId())
                .alternativaId(diseno.getAlternativaId())
                .cuotaId(diseno.getCuotaId())
                .mes(diseno.getMes())
                .anio(diseno.getAnho())
                .vencimiento1(diseno.getVencimiento1())
                .vencimiento2(diseno.getVencimiento2())
                .vencimiento3(diseno.getVencimiento3())
                .importe1Original(ReglasEmision.redondear2(diseno.getImporte1()))
                .importe2Original(ReglasEmision.redondear2(diseno.getImporte2()))
                .importe3Original(ReglasEmision.redondear2(diseno.getImporte3()))
                .importe1Borrador(importe1Borrador)
                .importe2Borrador(ReglasEmision.redondear2(diseno.getImporte2()))
                .importe3Borrador(ReglasEmision.redondear2(diseno.getImporte3()))
                .arancelTipoId(arancelTipoId)
                .tramoId(diseno.getTramoId())
                .descuentoPagoTermino(ReglasEmision.redondear2(BigDecimal.valueOf(enTerminoAnterior ? puntosDescuento : 0)))
                .pagado((byte) 0)
                .baja((byte) 0)
                .manual((byte) 0)
                .compensada((byte) 0)
                .modificado((byte) 0)
                .bloqueado((byte) 0)
                .build();
    }

    private void refrescarDesdeDiseno(BorradorCuota cuota, LectivoCuota diseno, boolean enTerminoAnterior,
                                      OffsetDateTime hoy, int puntosDescuento, BigDecimal multiplicador) {
        if (cero(cuota.getImporte1Original()) && positivo(diseno.getImporte1())
                && !BorradorCuota.esVerdadero(cuota.getCompensada())) {
            cuota.setImporte1Original(ReglasEmision.redondear2(diseno.getImporte1()));
            cuota.setImporte2Original(ReglasEmision.redondear2(diseno.getImporte2()));
            cuota.setImporte3Original(ReglasEmision.redondear2(diseno.getImporte3()));
            cuota.setImporte1Borrador(ReglasEmision.redondear2(diseno.getImporte1()));
            cuota.setImporte2Borrador(ReglasEmision.redondear2(diseno.getImporte2()));
            cuota.setImporte3Borrador(ReglasEmision.redondear2(diseno.getImporte3()));
        }
        boolean vencimientoFuturo = diseno.getVencimiento1() != null && !diseno.getVencimiento1().isBefore(hoy);
        if (cero(cuota.getImporte1Borrador()) && vencimientoFuturo
                && !BorradorCuota.esVerdadero(cuota.getManual())
                && !BorradorCuota.esVerdadero(cuota.getCompensada())) {
            cuota.setMes(diseno.getMes());
            cuota.setAnio(diseno.getAnho());
            cuota.setVencimiento1(diseno.getVencimiento1());
            cuota.setVencimiento2(diseno.getVencimiento2());
            cuota.setVencimiento3(diseno.getVencimiento3());
            cuota.setImporte1Borrador(ReglasEmision.redondear2(cuota.getImporte1Original()));
            cuota.setDescuentoPagoTermino(BigDecimal.valueOf(enTerminoAnterior ? puntosDescuento : 0));
            cuota.setImporte2Borrador(ReglasEmision.redondear2(cuota.getImporte2Original()));
            cuota.setImporte3Borrador(ReglasEmision.redondear2(cuota.getImporte3Original()));
        }
    }

    private void aplicarBeca(BorradorCuota cuota, DatosConstruccion datos, Map<String, BigDecimal> porcentajes) {
        BigDecimal beca = ReglasEmision.becaAplicable(datos.getBecaPorcentaje(), cuota.getProductoId());
        if (BorradorCuota.esVerdadero(cuota.getPagado())
                || !positivo(cuota.getImporte1Borrador())
                || BorradorCuota.esVerdadero(cuota.getCompensada())) {
            return;
        }
        if (BorradorCuota.esVerdadero(cuota.getManual()) && !datos.isCalculaBeca()) {
            return;
        }
        BigDecimal porcentaje = porcentaje(cuota.getArancelTipoId(), cuota.getProductoId(), porcentajes);
        cuota.setImporte1Borrador(ReglasEmision.aplicarBecaYArancel(cuota.getImporte1Original(), beca, porcentaje));
        cuota.setImporte2Borrador(ReglasEmision.aplicarBecaYArancel(cuota.getImporte2Original(), beca, porcentaje));
        cuota.setImporte3Borrador(ReglasEmision.aplicarBecaYArancel(cuota.getImporte3Original(), beca, porcentaje));
    }

    /**
     * Descuento por pago en término del lectivo anterior.
     *
     * <p>Diferencia deliberada con el legacy: allá el descuento se calculaba al agregar la
     * cuota y luego lo pisaba el recálculo de beca/arancel (bug de {@code constructChequera}).
     * Acá se aplica al final, después de beca y arancel, para que el descuento efectivamente
     * llegue al importe emitido.</p>
     */
    private void aplicarDescuentoPagoTermino(BorradorCuota cuota, DatosConstruccion datos) {
        if (!datos.isEnTerminoAnterior()
                || BorradorCuota.esVerdadero(cuota.getPagado())
                || BorradorCuota.esVerdadero(cuota.getCompensada())
                || BorradorCuota.esVerdadero(cuota.getManual())) {
            return;
        }
        int puntos = descuentoPagoTermino(datos.getAlternativas(), cuota.getAlternativaId());
        if (puntos == 0) {
            return;
        }
        cuota.setImporte1Borrador(ReglasEmision.redondear2(cuota.getImporte1Borrador()
                .multiply(ReglasEmision.multiplicadorDescuentoPagoTermino(puntos))));
        cuota.setDescuentoPagoTermino(ReglasEmision.redondear2(BigDecimal.valueOf(puntos)));
    }

    private BigDecimal porcentaje(Integer arancelTipoId, Integer productoId, Map<String, BigDecimal> cache) {
        if (arancelTipoId == null || productoId == null) {
            return BigDecimal.valueOf(100);
        }
        String key = arancelTipoId + "." + productoId;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        BigDecimal porcentaje;
        try {
            porcentaje = arancelPorcentajeService.findByUnique(arancelTipoId, productoId).getPorcentaje();
        } catch (RuntimeException e) {
            log.warn("Sin arancel_porcentaje para {}.{}; se usa 100", arancelTipoId, productoId);
            porcentaje = BigDecimal.valueOf(100);
        }
        cache.put(key, porcentaje);
        return porcentaje;
    }

    private BorradorCuota fromChequeraCuota(ChequeraCuota cuota) {
        return BorradorCuota.builder()
                .chequeraCuotaId(cuota.getChequeraCuotaId())
                .productoId(cuota.getProductoId())
                .alternativaId(cuota.getAlternativaId())
                .cuotaId(cuota.getCuotaId())
                .mes(cuota.getMes())
                .anio(cuota.getAnho())
                .vencimiento1(cuota.getVencimiento1())
                .vencimiento2(cuota.getVencimiento2())
                .vencimiento3(cuota.getVencimiento3())
                .importe1Original(ReglasEmision.redondear2(cuota.getImporte1Original()))
                .importe2Original(ReglasEmision.redondear2(cuota.getImporte2Original()))
                .importe3Original(ReglasEmision.redondear2(cuota.getImporte3Original()))
                .importe1Borrador(ReglasEmision.redondear2(cuota.getImporte1()))
                .importe2Borrador(ReglasEmision.redondear2(cuota.getImporte2()))
                .importe3Borrador(ReglasEmision.redondear2(cuota.getImporte3()))
                .arancelTipoId(cuota.getArancelTipoId())
                .tramoId(cuota.getTramoId())
                .descuentoPagoTermino(ReglasEmision.redondear2(BigDecimal.ZERO))
                .pagado(nz(cuota.getPagado()))
                .baja(nz(cuota.getBaja()))
                .manual(nz(cuota.getManual()))
                .compensada(nz(cuota.getCompensada()))
                .modificado((byte) 0)
                .bloqueado((byte) (BorradorCuota.esVerdadero(cuota.getManual())
                        || BorradorCuota.esVerdadero(cuota.getCompensada())
                        || BorradorCuota.esVerdadero(cuota.getPagado())
                        || BorradorCuota.esVerdadero(cuota.getBaja()) ? 1 : 0))
                .build();
    }

    private Integer descuentoPagoTermino(List<AlternativaEmision> alternativas, Integer alternativaId) {
        if (alternativas == null || alternativaId == null) {
            return 0;
        }
        return alternativas.stream()
                .filter(alternativa -> Objects.equals(alternativa.getAlternativaId(), alternativaId))
                .map(AlternativaEmision::getDescuentoPagoTermino)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0);
    }

    private static boolean cero(BigDecimal valor) {
        return valor == null || valor.compareTo(BigDecimal.ZERO) == 0;
    }

    private static boolean positivo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    private static boolean cambio(Object anterior, Object nuevo) {
        if (nuevo == null) {
            return false;
        }
        if (anterior instanceof BigDecimal anteriorDecimal && nuevo instanceof BigDecimal nuevoDecimal) {
            return anteriorDecimal.compareTo(nuevoDecimal) != 0;
        }
        if (anterior instanceof OffsetDateTime anteriorFecha && nuevo instanceof OffsetDateTime nuevaFecha) {
            return !anteriorFecha.toLocalDate().isEqual(nuevaFecha.toLocalDate());
        }
        return !Objects.equals(anterior, nuevo);
    }

    private static <T> T valor(T nuevo, T anterior) {
        return nuevo == null ? anterior : nuevo;
    }

    private static Byte nz(Byte valor) {
        return valor == null ? 0 : valor;
    }
}
