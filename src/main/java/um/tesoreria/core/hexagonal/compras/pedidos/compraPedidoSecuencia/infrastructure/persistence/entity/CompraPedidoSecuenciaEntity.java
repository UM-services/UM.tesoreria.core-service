package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.compras.correlativo.SecuenciaAnual;
import um.tesoreria.core.model.Auditable;

/** Correlativo anual de pedidos de compra: una fila por ejercicio. */
@Getter
@Setter
@Entity
@Table(name = "compra_pedido_secuencia")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoSecuenciaEntity extends Auditable implements SecuenciaAnual {

    @Id
    @Column(name = "ejercicio_id")
    private Integer ejercicioId;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "ultimo_numero")
    private Integer ultimoNumero;

}
