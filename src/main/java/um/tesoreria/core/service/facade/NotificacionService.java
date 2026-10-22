/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.event.CorreoPayload;
import um.tesoreria.core.event.ReferenciaNotificacion;
import um.tesoreria.core.event.Remitente;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.personas.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorException;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.model.PersonaSuspendido;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.service.CorreoEventPublisher;
import um.tesoreria.core.service.PersonaSuspendidoService;
import um.tesoreria.core.service.ProveedorComprobanteService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
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
@RequiredArgsConstructor
public class NotificacionService {

	static final String MENSAJE_ENVIO = "Solicitud de envío en proceso";
	static final String MENSAJE_SIN_CORREOS = "ERROR: Sin correos para ENVIAR";
	private static final String REPLY_TO = "no-reply@um.edu.ar";

	private final FacultadService facultadService;
	private final DomicilioService domicilioService;
	private final PersonaSuspendidoService personaSuspendidoService;
	private final ProveedorMovimientoService proveedorMovimientoService;
	private final ProveedorService proveedorService;
	private final ProveedorComprobanteService proveedorComprobanteService;
	private final ComprobanteService comprobanteService;
	private final ProveedorValorService proveedorValorService;
	private final ValorMovimientoService valorMovimientoService;
	private final ValorService valorService;
	private final CorreoEventPublisher correoEventPublisher;

	public String notifyDeudorSuspendido(PersonaSuspendido personaSuspendido) {
		String data = "";

		Domicilio domicilio;
		try {
			domicilio = domicilioService.findByUnique(personaSuspendido.getPersonaId(),
					personaSuspendido.getDocumentoId());
		} catch (DomicilioException e) {
			return MENSAJE_SIN_CORREOS;
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

		List<String> addresses = destinatarios(domicilio);
		if (addresses.isEmpty()) {
			return MENSAJE_SIN_CORREOS;
		}

		correoEventPublisher.publicar(CorreoPayload.builder()
				.remitente(Remitente.NOTIFICACION)
				.para(addresses)
				.replyTo(REPLY_TO)
				.asunto("Notificación Deuda")
				.cuerpo(data)
				.referencia(referenciaPersonaSuspendido(personaSuspendido))
				.build());

		return MENSAJE_ENVIO;
	}

	public String notifyPagoProveedor(Long proveedorMovimientoId) {
		String data = "";

		ProveedorMovimiento proveedorMovimiento;
		try {
			proveedorMovimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
		} catch (ProveedorMovimientoException e) {
			return "ERROR: No Existe Orden de Pago";
		}
		Proveedor proveedor = null;
		try {
			proveedor = proveedorService.getByProveedorId(proveedorMovimiento.getProveedorId()).get();
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
				.map(ProveedorComprobante::getProveedorMovimientoIdComprobante).collect(Collectors.toList());
		Map<Long, ProveedorMovimiento> proveedorMovimientos = proveedorMovimientoService
				.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds).stream()
				.collect(Collectors.toMap(ProveedorMovimiento::getProveedorMovimientoId, comprobante -> comprobante));
		Map<Integer, Comprobante> comprobantes = comprobanteService.findAll().stream()
				.collect(Collectors.toMap(Comprobante::getComprobanteId, comprobante -> comprobante));

		// Datos de valores
		List<ProveedorValor> proveedorValors = proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId);
		List<Long> valorMovimientoIds = proveedorValors.stream().map(ProveedorValor::getValorMovimientoId)
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

		List<String> addresses = new ArrayList<>();
		if (!proveedor.getEmail().isEmpty())
			addresses.add(proveedor.getEmail());
		if (proveedor.getEmailInterno() != null && !proveedor.getEmailInterno().isEmpty())
			addresses.add(proveedor.getEmailInterno());

		if (addresses.isEmpty()) {
			return MENSAJE_SIN_CORREOS;
		}

		correoEventPublisher.publicar(CorreoPayload.builder()
				.remitente(Remitente.NOTIFICACION)
				.para(addresses)
				.replyTo(REPLY_TO)
				.asunto("Notificación Orden Pago")
				.cuerpo(data)
				.referencia(ReferenciaNotificacion.builder()
						.tipo("PROVEEDOR_MOVIMIENTO")
						.id(proveedorMovimientoId == null ? null : proveedorMovimientoId.toString())
						.build())
				.build());

		return MENSAJE_ENVIO;
	}

	private ReferenciaNotificacion referenciaPersonaSuspendido(PersonaSuspendido personaSuspendido) {
		Long personaSuspendidoId = personaSuspendido.getPersonaSuspendidoId();
		if (personaSuspendidoId == null) {
			try {
				personaSuspendidoId = personaSuspendidoService
						.findByUnique(personaSuspendido.getPersonaId(), personaSuspendido.getDocumentoId())
						.getPersonaSuspendidoId();
			} catch (RuntimeException e) {
				log.warn("No se pudo resolver personaSuspendidoId para la referencia: {}", e.getMessage());
			}
		}
		return ReferenciaNotificacion.builder()
				.tipo("PERSONA_SUSPENDIDO")
				.id(personaSuspendidoId == null ? null : personaSuspendidoId.toString())
				.build();
	}

	private List<String> destinatarios(Domicilio domicilio) {
		List<String> direcciones = new ArrayList<>();
		if (domicilio.getEmailPersonal() != null && !domicilio.getEmailPersonal().isEmpty()) {
			direcciones.add(domicilio.getEmailPersonal());
		}
		if (domicilio.getEmailInstitucional() != null && !domicilio.getEmailInstitucional().isEmpty()) {
			direcciones.add(domicilio.getEmailInstitucional());
		}
		return direcciones;
	}

}
