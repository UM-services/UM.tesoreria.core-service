/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.facade;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.DomicilioNotFoundException;
import ar.edu.um.tesoreria.rest.exception.FacultadNotFoundException;
import ar.edu.um.tesoreria.rest.exception.ProveedorMovimientoNotFoundException;
import ar.edu.um.tesoreria.rest.exception.ProveedorNotFoundException;
import ar.edu.um.tesoreria.rest.extern.consumer.AlumnoExamenFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.NotificacionFacultadConsumer;
import ar.edu.um.tesoreria.rest.model.Comprobante;
import ar.edu.um.tesoreria.rest.model.Domicilio;
import ar.edu.um.tesoreria.rest.model.Facultad;
import ar.edu.um.tesoreria.rest.model.NotificacionExamen;
import ar.edu.um.tesoreria.rest.model.PersonaSuspendido;
import ar.edu.um.tesoreria.rest.model.Proveedor;
import ar.edu.um.tesoreria.rest.model.ProveedorComprobante;
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento;
import ar.edu.um.tesoreria.rest.model.ProveedorValor;
import ar.edu.um.tesoreria.rest.model.Valor;
import ar.edu.um.tesoreria.rest.model.ValorMovimiento;
import ar.edu.um.tesoreria.rest.service.ComprobanteService;
import ar.edu.um.tesoreria.rest.service.DomicilioService;
import ar.edu.um.tesoreria.rest.service.FacultadService;
import ar.edu.um.tesoreria.rest.service.NotificacionExamenService;
import ar.edu.um.tesoreria.rest.service.PersonaSuspendidoService;
import ar.edu.um.tesoreria.rest.service.ProveedorComprobanteService;
import ar.edu.um.tesoreria.rest.service.ProveedorMovimientoService;
import ar.edu.um.tesoreria.rest.service.ProveedorService;
import ar.edu.um.tesoreria.rest.service.ProveedorValorService;
import ar.edu.um.tesoreria.rest.service.ValorMovimientoService;
import ar.edu.um.tesoreria.rest.service.ValorService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class NotificacionService {

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private AlumnoExamenFacultadConsumer alumnoExamenFacultadConsumer;

	@Autowired
	private NotificacionExamenService notificacionExamenService;

	@Autowired
	private NotificacionFacultadConsumer notificacionFacultadConsumer;

	@Autowired
	private DomicilioService domicilioService;

	@Autowired
	private JavaMailSender sender;

	@Autowired
	private PersonaSuspendidoService personaSuspendidoService;

	@Autowired
	private ProveedorMovimientoService proveedorMovimientoService;

	@Autowired
	private ProveedorService proveedorService;

	@Autowired
	private ProveedorComprobanteService proveedorComprobanteService;

	@Autowired
	private ComprobanteService comprobanteService;

	@Autowired
	private ProveedorValorService proveedorValorService;

	@Autowired
	private ValorMovimientoService valorMovimientoService;

	@Autowired
	private ValorService valorService;

	public void notifyByPersona(BigDecimal personaId, Integer documentoId, Integer facultadId) {
		try {
			Facultad facultad = facultadService.findByFacultadId(facultadId);
			if (!facultad.getApiserver().equals("")) {
				Integer cantidad_inscripciones = alumnoExamenFacultadConsumer.cantidad48horas(facultad.getApiserver(),
						facultad.getApiport(), personaId, documentoId, facultadId);
				if (cantidad_inscripciones > 0) {
					List<NotificacionExamen> emails = notificacionExamenService
							.findAllByFacultadId(facultad.getFacultadId());
					if (emails.size() > 0) {
						notificacionFacultadConsumer.examen48horas(facultad.getApiserver(), facultad.getApiport(),
								personaId, documentoId, facultadId, emails);
					}
				}
			}
		} catch (FacultadNotFoundException e) {
			log.debug("ERROR: Sin Facultad");
		}
	}

	public String notifyDeudorSuspendido(PersonaSuspendido personaSuspendido) throws MessagingException {
		String data = "";

		Domicilio domicilio = null;
		try {
			domicilio = domicilioService.findByUnique(personaSuspendido.getPersonaId(),
					personaSuspendido.getDocumentoId());
		} catch (DomicilioNotFoundException e) {
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

		if (!domicilio.getEmailPersonal().equals(""))
			addresses.add(domicilio.getEmailPersonal());
		if (!domicilio.getEmailInstitucional().equals(""))
			addresses.add(domicilio.getEmailInstitucional());

//		addresses.add("daniel.quinterospinto@gmail.com");

		try {
			helper.setTo(addresses.toArray(new String[addresses.size()]));
			helper.setText(data);
			helper.setReplyTo("no-reply@um.edu.ar");
			helper.setSubject("Notificación Deuda");
			log.debug("Mensaje -> {}", data);
		} catch (MessagingException e) {
			e.printStackTrace();
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
		} catch (ProveedorMovimientoNotFoundException e) {
			return "ERROR: No Existe Orden de Pago";
		}
		Proveedor proveedor = null;
		try {
			proveedor = proveedorService.findByProveedorId(proveedorMovimiento.getProveedorId());
		} catch (ProveedorNotFoundException e) {
			return "ERROR: No Existe Proveedor";
		}

		if (proveedor.getEmail().equals("")) {
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
		List<ProveedorValor> proveedorValors = proveedorValorService.findAllByOrdenPagoId(proveedorMovimientoId);
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

		if (!proveedor.getEmail().equals(""))
			addresses.add(proveedor.getEmail());
		if (!proveedor.getEmailInterno().equals(""))
			addresses.add(proveedor.getEmailInterno());

//		addresses.add("daniel.quinterospinto@gmail.com");

		try {
			helper.setTo(addresses.toArray(new String[addresses.size()]));
			helper.setText(data);
			helper.setReplyTo("no-reply@um.edu.ar");
			helper.setSubject("Notificación Orden Pago");
			log.debug("Mensaje -> {}", data);
		} catch (MessagingException e) {
			e.printStackTrace();
			return "ERROR: No pudo ENVIARSE";
		}
		sender.send(message);

		return "Envío de Correo Ok!!";
	}

}
