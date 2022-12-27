/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;

import ar.edu.um.tesoreria.rest.model.view.pk.TipoPagoFechaPk;
import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_tipo_pago_fecha")
@IdClass(TipoPagoFechaPk.class)
@Immutable
public class TipoPagoFecha implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3399823604076980175L;
	
	@Id
	private Integer tipoPagoId;
	
	@Id
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaAcreditacion;

}
