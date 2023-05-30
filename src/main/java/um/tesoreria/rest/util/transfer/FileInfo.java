/**
 * 
 */
package um.tesoreria.rest.util.transfer;

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
public class FileInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 203015754036659479L;

	private String filename;
	private String base64;
}
