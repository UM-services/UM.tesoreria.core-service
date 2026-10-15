package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "compra_referencia")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraReferenciaEntity extends Auditable {

    @Id
    @Column(name = "ejercicio_id")
    private Integer ejercicioId;

    @Column(name = "importe", nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

}
