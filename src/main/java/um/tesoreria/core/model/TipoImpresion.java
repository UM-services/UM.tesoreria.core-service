package um.tesoreria.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Tabla legacy `tipoimpresion` (columnas abreviadas tim_id / tim_nombre, mismo estilo que el
 * resto de las tablas migradas de VB6). Usada para mostrar, por ejemplo, "Rapipago" en el
 * encabezado del Estado de Chequera.
 *
 */
@Data
@Entity
@Table(name = "tipoimpresion")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TipoImpresion {

    @Id
    @Column(name = "tim_id")
    private Integer tipoImpresionId;

    @Column(name = "tim_nombre")
    private String nombre;

}