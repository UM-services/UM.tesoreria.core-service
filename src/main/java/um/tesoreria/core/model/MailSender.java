/**
 * 
 */
package um.tesoreria.core.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Table
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MailSender extends Auditable implements Serializable {

	private static final long serialVersionUID = -2763719924653391295L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long mailSenderId;

	private String ipAddress;
	private Long port;
	private Byte enabled = 0;

}
