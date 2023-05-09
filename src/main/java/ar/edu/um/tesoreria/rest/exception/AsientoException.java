/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;
import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class AsientoException extends RuntimeException {

	private static final long serialVersionUID = -7288621090252322455L;

    public AsientoException(Long asientoId) {
        super(MessageFormat.format("Cannot find Asiento {0}", asientoId));
    }

    public AsientoException(OffsetDateTime fecha, Integer orden) {
        super(MessageFormat.format("Cannot find Asiento {0}/{1}", fecha, orden));
    }

}
