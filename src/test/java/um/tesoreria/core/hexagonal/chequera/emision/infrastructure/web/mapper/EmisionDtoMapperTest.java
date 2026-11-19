package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmisionDtoMapperTest {

    private final EmisionDtoMapper mapper = new EmisionDtoMapper();

    @Test
    void exponeLosNombresDeFacultadYTipoDeChequera() {
        BorradorResponse response = mapper.toResponse(BorradorChequera.builder()
                .borradorId(1L)
                .facultadId(5)
                .facultadNombre("Ingeniería")
                .lectivoId(38)
                .tipoChequeraId(2)
                .tipoChequeraNombre("Cuota")
                .build());

        assertEquals("Ingeniería", response.getFacultadNombre());
        assertEquals("Cuota", response.getTipoChequeraNombre());
    }

    @Test
    void marcaComoNoEditablesLasCuotasPagadasDeBajaCompensadasOBloqueadas() {
        BorradorResponse response = mapper.toResponse(BorradorChequera.builder()
                .borradorId(1L)
                .cuotas(List.of(
                        cuota((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0),
                        cuota((byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 1),
                        cuota((byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 1),
                        cuota((byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1),
                        cuota((byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 1)))
                .build());

        assertTrue(response.getCuotas().get(0).isEditable());
        assertFalse(response.getCuotas().get(1).isEditable());
        assertFalse(response.getCuotas().get(2).isEditable());
        assertFalse(response.getCuotas().get(3).isEditable());
        assertFalse(response.getCuotas().get(4).isEditable());
    }

    private BorradorCuota cuota(byte pagado, byte baja, byte manual, byte compensada, byte bloqueado) {
        return BorradorCuota.builder()
                .productoId(1).alternativaId(1).cuotaId(1)
                .importe1Original(BigDecimal.TEN).importe1Borrador(BigDecimal.TEN)
                .pagado(pagado).baja(baja).manual(manual).compensada(compensada)
                .modificado((byte) 0).bloqueado(bloqueado)
                .build();
    }
}
