/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.DomicilioException;
import um.tesoreria.core.exception.ProveedorMovimientoException;
import um.tesoreria.core.exception.ProveedorException;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.model.PersonaSuspendido;
import um.tesoreria.core.model.Proveedor;
import um.tesoreria.core.service.ComprobanteService;
import um.tesoreria.core.service.DomicilioService;
import um.tesoreria.core.service.FacultadService;
import um.tesoreria.core.service.PersonaSuspendidoService;
import um.tesoreria.core.service.ProveedorComprobanteService;
import um.tesoreria.core.service.ProveedorMovimientoService;
import um.tesoreria.core.service.ProveedorService;
import um.tesoreria.core.service.ProveedorValorService;
import um.tesoreria.core.service.ValorMovimientoService;
import um.tesoreria.core.service.ValorService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class NotificacionService {

	@Value("${app.testing}")
	private Boolean testing;

	private final FacultadService facultadService;
	private final DomicilioService domicilioService;
	private final JavaMailSender sender;
	private final PersonaSuspendidoService personaSuspendidoService;
	private final ProveedorMovimientoService proveedorMovimientoService;
	private final ProveedorService proveedorService;
	private final ProveedorComprobanteService proveedorComprobanteService;
	private final ComprobanteService comprobanteService;
	private final ProveedorValorService proveedorValorService;
	private final ValorMovimientoService valorMovimientoService;
	private final ValorService valorService;

	public NotificacionService(FacultadService facultadService,
			DomicilioService domicilioService,
			JavaMailSender sender,
			PersonaSuspendidoService personaSuspendidoService,
			ProveedorMovimientoService proveedorMovimientoService,
			ProveedorService proveedorService,
			ProveedorComprobanteService proveedorComprobanteService,
			ComprobanteService comprobanteService,
			ProveedorValorService proveedorValorService,
			ValorMovimientoService valorMovimientoService,
			ValorService valorService) {
		this.facultadService = facultadService;
		this.domicilioService = domicilioService;
		this.sender = sender;
		this.personaSuspendidoService = personaSuspendidoService;
		this.proveedorMovimientoService = proveedorMovimientoService;
		this.proveedorService = proveedorService;
		this.proveedorComprobanteService = proveedorComprobanteService;
		this.comprobanteService = comprobanteService;
		this.proveedorValorService = proveedorValorService;
		this.valorMovimientoService = valorMovimientoService;
		this.valorService = valorService;
	}

	public String notifyDeudorSuspendido(PersonaSuspendido personaSuspendido) throws MessagingException {
		String data = "";

		Domicilio domicilio = null;
		try {
			domicilio = domicilioService.findByUnique(personaSuspendido.getPersonaId(),
					personaSuspendido.getDocumentoId());
		} catch (DomicilioException e) {
			return "ERROR: Sin correos para ENVIAR";
		}

		Facultad facultad = facultadService.findByFacultadId(personaSuspendido.getFacultadId());

		String mails[] = { "tesoreria", "administracion.sr", "tesoreria", "tesoreria", "martin.gigena", "tesoreria" };

		data = "Estimad@ Estudiante:" + (char) 10;
		data = data + (char) 10;
		data = data + "Le informamos que su inscripción al segundo semestre del" + (char) 10;
		data = data + "presente Ciclo Lectivo no pudo realizarse." + (char) 10;
		data = data + (char) 10;
		data = data + "Por favor comunicarse con Tesorería al mail " + mails[personaSuspendido.getGeograficaId() - 1]
				+ "@um.edu.ar" + (char) 10;
		data = data + "o a la Secretaría de la " + facultad.getNombre() + "." + (char) 10;
		data = data + (char) 10;
		data = data + "Estamos a su disposición para ayudar en la continuidad de sus estudios." + (char) 10;
		data = data + (char) 10;
		data = data + "Atentamente." + (char) 10;
		data = data + (char) 10;
		data = data + "Universidad de Mendoza" + (char) 10;
		data = data + (char) 10;
		data = data + (char) 10
				+ "Por favor no responda este mail, fue generado automáticamente. Su respuesta no será leída."
				+ (char) 10;

		// Envia correo
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		List<String> addresses = new ArrayList<String>();

		if (!testing) {
			if (!domicilio.getEmailPersonal().isEmpty())
				addresses.add(domicilio.getEmailPersonal());
			if (!domicilio.getEmailInstitucional().isEmpty())
				addresses.add(domicilio.getEmailInstitucional());
		} else {
			log.debug("Testing -> daniel.quinterospinto@gmail.com");
			addresses.add("daniel.quinterospinto@gmail.com");
		}

		try {
			helper.setTo(addresses.toArray(new String[0]));
			helper.setText(data);
			helper.setReplyTo("no-reply@um.edu.ar");
			helper.setSubject("Notificación Deuda");
			log.debug("Mensaje -> {}", data);
		} catch (MessagingException e) {
			log.debug("ERROR: No pudo ENVIARSE -> {}", e.getMessage());
			return "ERROR: No pudo ENVIARSE";
		}
		personaSuspendido.setNotificado((byte) 1);
		personaSuspendido = personaSuspendidoService.update(personaSuspendido,
				personaSuspendido.getPersonaSuspendidoId());
		sender.send(message);

		return "Envío de Correo Ok!!";
	}

	public String notifyPagoProveedor(Long proveedorMovimientoId) throws MessagingException {
		String data = "";

		ProveedorMovimiento proveedorMovimiento = null;
		try {
			proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
		} catch (ProveedorMovimientoException e) {
			return "ERROR: No Existe Orden de Pago";
		}
		Proveedor proveedor = null;
		try {
			proveedor = proveedorService.findByProveedorId(proveedorMovimiento.getProveedorId());
		} catch (ProveedorException e) {
			return "ERROR: No Existe Proveedor";
		}

		if (proveedor.getEmail().isEmpty()) {
			return "ERROR: Proveedor SIN Correo Electrónico";
		}

		// Datos de comprobantes
		List<ProveedorComprobante> proveedorComprobantes = proveedorComprobanteService
				.findAllByOrdenPagoId(proveedorMovimientoId);
		List<Long> proveedorMovimientoIds = proveedorComprobantes.stream()
				.map(comprobante -> comprobante.getProveedorMovimientoIdComprobante()).collect(Collectors.toList());
		Map<Long, ProveedorMovimiento> proveedorMovimientos = proveedorMovimientoService
				.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds).stream()
				.collect(Collectors.toMap(ProveedorMovimiento::getProveedorMovimientoId, comprobante -> comprobante));
		Map<Integer, Comprobante> comprobantes = comprobanteService.findAll().stream()
				.collect(Collectors.toMap(Comprobante::getComprobanteId, comprobante -> comprobante));

		// Datos de valores
		List<ProveedorValor> proveedorValors = proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId);
		List<Long> valorMovimientoIds = proveedorValors.stream().map(valor -> valor.getValorMovimientoId())
				.collect(Collectors.toList());
		Map<Long, ValorMovimiento> valorMovimientos = valorMovimientoService
				.findAllByValorMovimientoIdIn(valorMovimientoIds).stream()
				.collect(Collectors.toMap(ValorMovimiento::getValorMovimientoId, valor -> valor));
		Map<Integer, Valor> valors = valorService.findAll().stream()
				.collect(Collectors.toMap(Valor::getValorId, valor -> valor));

		data = proveedor.getRazonSocial() + ":" + (char) 10;
		data = data + (char) 10;
		data = data + "Le informamos que los siguientes comprobantes:" + (char) 10;
		data = data + (char) 10;
		for (ProveedorComprobante proveedorComprobante : proveedorComprobantes) {
			ProveedorMovimiento movimiento = proveedorMovimientos
					.get(proveedorComprobante.getProveedorMovimientoIdComprobante());
			Comprobante comprobante = comprobantes.get(movimiento.getComprobanteId());
			data = data + "          "
					+ MessageFormat.format("Comprobante: {0} - Número: {1}-{2} - Fecha: {3} - Importe: {4}",
							comprobante.getDescripcion(), movimiento.getPrefijo(), movimiento.getNumeroComprobante(),
							movimiento.getFechaComprobante(), proveedorComprobante.getImporte())
					+ (char) 10;
		}
		data = data + (char) 10;
		data = data + "Han sido abonados con los siguientes valores:" + (char) 10;
		data = data + (char) 10;
		for (ProveedorValor proveedorValor : proveedorValors) {
			ValorMovimiento movimiento = valorMovimientos.get(proveedorValor.getValorMovimientoId());
			Valor valor = valors.get(movimiento.getValorId());
			data = data + "          "
					+ MessageFormat.format("Valor: {0} {1} - Número: {2} - Fecha: {3} - Importe: {4}",
							valor.getConcepto(), movimiento.getNombreTitular(), movimiento.getNumero(),
							movimiento.getFechaEmision(), movimiento.getImporte())
					+ (char) 10;
		}
		data = data + (char) 10;
		data = data + "Atentamente." + (char) 10;
		data = data + (char) 10;
		data = data + "Universidad de Mendoza" + (char) 10;
		data = data + (char) 10;
		data = data + (char) 10
				+ "Por favor no responda este mail, fue generado automáticamente. Su respuesta no será leída."
				+ (char) 10;

		// Envia correo
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		List<String> addresses = new ArrayList<String>();

		if (!testing) {
			if (!proveedor.getEmail().isEmpty())
				addresses.add(proveedor.getEmail());
			if (!proveedor.getEmailInterno().isEmpty())
				addresses.add(proveedor.getEmailInterno());
		} else {
			log.debug("Testing -> daniel.quinterospinto@gmail.com");
			addresses.add("daniel.quinterospinto@gmail.com");
		}

		try {
			helper.setTo(addresses.toArray(new String[0]));
			helper.setText(data);
			helper.setReplyTo("no-reply@um.edu.ar");
			helper.setSubject("Notificación Orden Pago");
			log.debug("Mensaje -> {}", data);
		} catch (MessagingException e) {
			log.debug("ERROR: No pudo ENVIARSE -> {}", e.getMessage());
			return "ERROR: No pudo ENVIARSE";
		}
		sender.send(message);

		return "Envío de Correo Ok!!";
	}

}
