/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.dto;

import java.io.Serializable;

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
public class SpoterDataResponse implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -7669419835887737680L;

	private boolean status;
	private String message = "";
	private Integer facultadId;
	private Integer tipoChequeraId;
	private Long chequeraSerieId;

}
