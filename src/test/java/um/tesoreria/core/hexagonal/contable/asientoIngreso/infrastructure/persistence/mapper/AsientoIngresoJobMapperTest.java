package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTareaEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.CuentaFaltante;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AsientoIngresoJobMapperTest {

    private static final OffsetDateTime FECHA = OffsetDateTime.of(2026, 11, 17, 0, 0, 0, 0, ZoneOffset.UTC);

    private final AsientoIngresoJobMapper mapper = new AsientoIngresoJobMapper();

    @Test
    void mapeaElJobCompleto() {
        AsientoIngresoJob job = AsientoIngresoJob.builder()
                .jobId(12L)
                .estado(AsientoIngresoJobEstado.EN_PROCESO)
                .fechaDesde(FECHA)
                .fechaHasta(FECHA.plusDays(1))
                .totalTareas(4)
                .tareasOk(1)
                .tareasError(1)
                .tareasSinMovimientos(1)
                .tareasPendientes(1)
                .usuarioId(7L)
                .heartbeat(FECHA)
                .iniciado(FECHA)
                .build();

        AsientoIngresoJob vuelta = mapper.toDomain(mapper.toEntity(job));

        assertThat(vuelta.getJobId()).isEqualTo(12L);
        assertThat(vuelta.getEstado()).isEqualTo(AsientoIngresoJobEstado.EN_PROCESO);
        assertThat(vuelta.getTotalTareas()).isEqualTo(4);
        assertThat(vuelta.getTareasSinMovimientos()).isEqualTo(1);
        assertThat(vuelta.getUsuarioId()).isEqualTo(7L);
    }

    @Test
    void conservaLasFaltantesEnJson() {
        AsientoIngresoJobTarea tarea = AsientoIngresoJobTarea.builder()
                .tareaId(3L)
                .jobId(12L)
                .fechaContable(FECHA)
                .tipoPagoId(3)
                .estado(AsientoIngresoJobTareaEstado.SIN_MOVIMIENTOS)
                .ordenContable(7)
                .totalDebe(new BigDecimal("100.00"))
                .totalHaber(new BigDecimal("100.00"))
                .faltantes(List.of(CuentaFaltante.builder()
                        .facultadId(1)
                        .facultadNombre("Facultad de Ingeniería")
                        .lectivoId(2026)
                        .lectivoNombre("2026")
                        .tipoChequeraId(1)
                        .tipoChequeraNombre("Anual")
                        .productoId(1)
                        .productoNombre("Matrícula")
                        .geograficaId(1)
                        .geograficaNombre("Sede Central")
                        .build()))
                .build();

        AsientoIngresoJobTarea vuelta = mapper.toDomain(mapper.toEntity(tarea));

        assertThat(vuelta.getEstado()).isEqualTo(AsientoIngresoJobTareaEstado.SIN_MOVIMIENTOS);
        assertThat(vuelta.getFaltantes()).hasSize(1);
        assertThat(vuelta.getFaltantes().get(0).getFacultadNombre()).isEqualTo("Facultad de Ingeniería");
        assertThat(vuelta.getFaltantes().get(0).clave()).isEqualTo("1.2026.1.1");
    }

    @Test
    void sinFaltantesDevuelveListaVacia() {
        AsientoIngresoJobTarea tarea = AsientoIngresoJobTarea.builder()
                .fechaContable(FECHA)
                .tipoPagoId(3)
                .estado(AsientoIngresoJobTareaEstado.OK)
                .build();

        assertThat(mapper.toDomain(mapper.toEntity(tarea)).getFaltantes()).isEmpty();
    }

}
