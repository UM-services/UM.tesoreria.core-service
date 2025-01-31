/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_tipo_pago_fecha_acreditacion")
@Immutable
public class TipoPagoFechaAcreditacion implements Serializable {

	@Id
	private String uniqueId;

	private Integer tipoPagoId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaAcreditacion;

}
