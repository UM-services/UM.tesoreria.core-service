/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.internal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class AsientoInternal implements Serializable {

	private static final long serialVersionUID = 6992457116099538153L;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContable;
	
	private Integer ordenContable;
	private BigDecimal debe = BigDecimal.ZERO;
	private BigDecimal haber = BigDecimal.ZERO;

}
