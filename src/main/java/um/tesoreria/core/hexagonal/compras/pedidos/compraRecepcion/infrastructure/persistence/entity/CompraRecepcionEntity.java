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

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "compra_recepcion")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRecepcionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_recepcion_id")
    private Long compraRecepcionId;

    @Column(name = "compra_formalizacion_id", nullable = false)
    private Long compraFormalizacionId;

    @Column(name = "numero", length = 16)
    private String numero;

    @Column(name = "ejercicio_id", nullable = false)
    private Integer ejercicioId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "remito", length = 40)
    private String remito;

    @Column(name = "resultado", nullable = false, length = 20)
    private String resultado;

    @Column(name = "patrimonio", nullable = false, length = 20)
    private String patrimonio;

    @Column(name = "observacion", length = 500)
    private String observacion;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

}
