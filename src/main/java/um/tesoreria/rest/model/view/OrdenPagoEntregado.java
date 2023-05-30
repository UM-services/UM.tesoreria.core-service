/**
 * 
 */
package um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_orden_pago_entregado")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class OrdenPagoEntregado implements Serializable {
	
	private static final long serialVersionUID = 7247935688796795421L;

	@Id
	private Long ordenPagoId;
	
	private BigDecimal entregado;
}
