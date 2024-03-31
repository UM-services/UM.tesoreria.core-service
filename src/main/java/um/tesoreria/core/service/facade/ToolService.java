/**
 * 
 */
package um.tesoreria.core.service.facade;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import org.springframework.stereotype.Service;

/**
 * @author daniel
 *
 */
@Service
public class ToolService {

	public Boolean mailvalidate(String mail) {

		if (mail.toLowerCase().contains("ñ")) {
			return false;
		}
		try {
			InternetAddress address = new InternetAddress(mail);
			address.validate();
		} catch (AddressException e) {
			return false;
		}
		return true;
	}

}
