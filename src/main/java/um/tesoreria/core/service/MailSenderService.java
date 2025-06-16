/**
 * 
 */
package um.tesoreria.core.service;

import java.util.LinkedList;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.MailSenderException;
import um.tesoreria.core.model.MailSender;
import um.tesoreria.core.repository.MailSenderRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class MailSenderService {

	@Autowired
	private MailSenderRepository repository;

	public List<MailSender> findAllByEnabledAndMailSenderIdGreaterThan(Byte enabled, Long mailSenderId) {
		return repository.findAllByEnabledAndMailSenderIdGreaterThanOrderByMailSenderId(enabled, mailSenderId);
	}

	public List<MailSender> findAllByEnabledAndMailSenderIdLessThan(Byte enabled, Long mailSenderId) {
		return repository.findAllByEnabledAndMailSenderIdLessThanOrderByMailSenderId(enabled, mailSenderId);
	}

	public MailSender findTopByEnabled(Byte enabled) {
		return repository.findTopByEnabledOrderByMailSenderId(enabled)
				.orElseThrow(() -> new MailSenderException(enabled));
	}

	public List<MailSender> saveAll(List<MailSender> senders) {
		return repository.saveAll(senders);
	}

	@Transactional
	public MailSender findSender() {
		List<MailSender> senders = new LinkedList<>();
		MailSender sender = null;
		senders.add(sender = this.findTopByEnabled((byte) 1));
		senders.addAll(this.findAllByEnabledAndMailSenderIdGreaterThan((byte) 0, sender.getMailSenderId()));
		senders.addAll(this.findAllByEnabledAndMailSenderIdLessThan((byte) 0, sender.getMailSenderId()));
		if (senders.size() > 1) {
			MailSender senderToEnabled = senders.get(1);
			sender.setEnabled((byte) 0);
			senderToEnabled.setEnabled((byte) 1);
			senders = this.saveAll(senders);
		}
		log.debug("Sender -> {}", sender);
		return sender;
	}

}
