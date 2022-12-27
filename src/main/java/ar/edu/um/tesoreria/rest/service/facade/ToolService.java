/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.facade;

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
		try {
			InternetAddress address = new InternetAddress(mail);
			address.validate();
		} catch (AddressException e) {
			return false;
		}
		return true;
	}

}
