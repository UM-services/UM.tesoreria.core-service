/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorArticuloAsignable implements Serializable {

	private static final long serialVersionUID = 1028715801885196427L;
	
	private List<Long> proveedorMovimientoIds;
	private Boolean asignables = true;

}
