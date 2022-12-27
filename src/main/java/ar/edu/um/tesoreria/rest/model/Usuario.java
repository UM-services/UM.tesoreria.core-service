/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends Auditable implements Serializable {

	private static final long serialVersionUID = 2314366809415661794L;

	@Id
	@Column(name = "usu_id")
	private String usuarioId;

	private String password = "";
	
	@Column(name = "usu_nombre")
	private String nombre = "";

	@Column(name = "usu_geo_id")
	private Integer geograficaId;

	@Column(name = "usu_impchequera")
	private Byte imprimeChequera;
	
	private Byte numeroOpManual;
	private Byte habilitaOpEliminacion;
	private Byte eliminaChequera;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime lastLog;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long autoId;

}
