/**
 * 
 */
package um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.data.annotation.Immutable;

import um.tesoreria.rest.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.rest.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_articulo_key")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloKey extends Auditable implements Serializable {

	private static final long serialVersionUID = -8530914093311572829L;

	@Id
	private Long articuloId;

	private String nombre;
	private String descripcion;
	private String unidad;
	private BigDecimal precio;
	private Byte inventariable;
	private Long stockMinimo;
	private BigDecimal cuenta;
	private String tipo;
	private Byte directo;
	private Byte habilitado;
	private String search;

}
