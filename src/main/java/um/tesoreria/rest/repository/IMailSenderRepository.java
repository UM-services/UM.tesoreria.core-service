/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.MailSender;

/**
 * @author daniel
 *
 */
@Repository
public interface IMailSenderRepository extends JpaRepository<MailSender, Long> {

	public List<MailSender> findAllByEnabledAndMailSenderIdGreaterThanOrderByMailSenderId(Byte enabled,
			Long mailSenderId);

	public List<MailSender> findAllByEnabledAndMailSenderIdLessThanOrderByMailSenderId(Byte enabled, Long mailSenderId);

	public Optional<MailSender> findTopByEnabledOrderByMailSenderId(Byte enabled);

}
