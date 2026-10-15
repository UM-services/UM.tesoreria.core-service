package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "compra_recepcion_item")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRecepcionItemEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_recepcion_item_id")
    private Long compraRecepcionItemId;

    @Column(name = "compra_recepcion_id", nullable = false)
    private Long compraRecepcionId;

    @Column(name = "compra_formalizacion_item_id", nullable = false)
    private Long compraFormalizacionItemId;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "cantidad_recibida", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadRecibida;

    @Column(name = "resultado", nullable = false, length = 20)
    private String resultado;

    @Column(name = "observacion", length = 500)
    private String observacion;

}
