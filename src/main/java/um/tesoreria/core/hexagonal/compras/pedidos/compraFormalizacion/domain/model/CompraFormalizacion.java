package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orden de Compra (etapa 8): instrumento emitido a partir del acto de adjudicación
 * ({@code compra_adjudicacion}).
 *
 * <p>Snapshotea el proveedor adjudicado, el monto y las condiciones de la oferta (pago, entrega,
 * garantía) y su detalle de ítems, para que el instrumento quede autocontenido. El número
 * {@code OC-AAAA-NNNNNN} se reserva al formalizar.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraFormalizacion {

    private Long compraFormalizacionId;
    private Long compraAdjudicacionId;
    private String numero;
    /** Nº del instrumento firmado fuera del sistema (opcional). */
    private String numeroExterno;
    private Integer ejercicioId;
    private LocalDateTime fecha;
    private Integer proveedorId;
    private String moneda;
    private BigDecimal monto;
    private String condicionPago;
    private String plazoEntrega;
    private String garantia;
    /** Objeto/alcance (HTML del editor). */
    private String objeto;
    private String observacion;
    private Integer usuarioId;
    @Builder.Default
    private List<CompraFormalizacionItem> items = new ArrayList<>();
    private LocalDateTime created;
    private LocalDateTime updated;

}
