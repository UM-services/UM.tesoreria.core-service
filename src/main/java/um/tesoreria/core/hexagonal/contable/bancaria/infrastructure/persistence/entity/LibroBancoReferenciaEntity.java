package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lectura mínima de libro_banco para saber si una cuenta bancaria tiene movimientos. Mismos nombres lógicos que
 * BancoMovimiento.kt (en minúscula). La clave real de la tabla es (lib_cub_id, lib_fecha, lib_orden); lib_id es un
 * auto-incremental indexado, que alcanza como identidad de una entidad que solo se consulta con exists y count.
 */
@Getter
@Entity
@Table(name = "libro_banco")
@NoArgsConstructor
public class LibroBancoReferenciaEntity {

    @Id
    @Column(name = "lib_id")
    private Long bancoMovimientoId;

    @Column(name = "lib_cub_id")
    private Long bancariaId;
}
