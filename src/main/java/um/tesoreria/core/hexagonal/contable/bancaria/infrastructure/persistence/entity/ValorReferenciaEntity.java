package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lectura mínima de valores para saber si una cuenta bancaria se usó. Mismos nombres lógicos que ValorMovimiento.kt
 * (en minúscula): dos entidades sobre una tabla no pueden nombrar distinto una columna.
 */
@Getter
@Entity
@Table(name = "valores")
@NoArgsConstructor
public class ValorReferenciaEntity {

    @Id
    @Column(name = "val_id")
    private Long valorMovimientoId;

    @Column(name = "val_cuentaorigen")
    private Long bancariaIdOrigen;
}
