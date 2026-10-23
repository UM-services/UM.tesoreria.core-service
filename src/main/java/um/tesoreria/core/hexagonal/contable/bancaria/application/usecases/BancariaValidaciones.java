package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Campo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Cbu;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Cuit;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaReferenciasPort;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Reglas compartidas por el alta y la edición. Cada regla de campo recibe lo enviado y lo que vale si el campo no
 * vino: el vacío en el alta, lo guardado en la edición. Un null explícito vacía el campo.
 */
final class BancariaValidaciones {

    /** varchar(64) en banco, número y titular. */
    static final int LARGO_TEXTO = 64;

    private BancariaValidaciones() {
    }

    /**
     * Banco, número o titular: el texto tal cual (sin recortar). La columna es latin1, que en MySQL es cp1252
     * (acepta € y las comillas tipográficas, no un emoji); fuera de eso, o de 64 caracteres, la base rechazaría el valor.
     */
    static String texto(String campo, Campo<String> enviado, String siNoVino) {
        if (!enviado.presente()) {
            return siNoVino;
        }
        String texto = enviado.valor() == null ? "" : enviado.valor();
        if (texto.length() > LARGO_TEXTO) {
            throw new BancariaSolicitudInvalidaException(Codigo.TEXTO_INVALIDO,
                    campo + " no puede superar " + LARGO_TEXTO + " caracteres");
        }
        // El encoder no es seguro entre hilos: se crea uno por llamada.
        if (!Charset.forName("windows-1252").newEncoder().canEncode(texto)) {
            throw new BancariaSolicitudInvalidaException(Codigo.TEXTO_INVALIDO,
                    campo + " tiene caracteres que la base no puede guardar");
        }
        return texto;
    }

    /** Un CUIT distinto de lo guardado se valida y se guarda con guiones; si es el mismo se conserva aunque sea histórico. */
    static String cuit(Campo<String> enviado, String siNoVino, String guardado) {
        String texto = enviado.presente() ? (enviado.valor() == null ? "" : enviado.valor()) : siNoVino;
        if (texto.equals(guardado)) {
            return guardado;
        }
        return Cuit.normalizar(texto).orElseThrow(() -> new BancariaSolicitudInvalidaException(Codigo.CUIT_INVALIDO,
                "cuit no es un CUIT válido (11 dígitos con verificador, con o sin guiones)"));
    }

    /** Un CBU distinto de lo guardado se valida; si es el mismo se conserva aunque sea histórico. */
    static String cbu(Campo<String> enviado, String siNoVino, String guardado) {
        String texto = enviado.presente() ? (enviado.valor() == null ? "" : enviado.valor()) : siNoVino;
        if (!texto.equals(guardado) && !Cbu.esValido(texto)) {
            throw new BancariaSolicitudInvalidaException(Codigo.CBU_INVALIDO,
                    "cbu tiene que tener 22 dígitos con los dos verificadores correctos");
        }
        return texto;
    }

    /** La cuenta contable es obligatoria: la usa CompraService al pagar con un valor de esta cuenta bancaria. */
    static BigDecimal cuenta(Campo<BigDecimal> enviada, BigDecimal siNoVino) {
        BigDecimal cuenta = enviada.valorOSi(siNoVino);
        if (cuenta == null) {
            throw new BancariaSolicitudInvalidaException(Codigo.CUENTA_REQUERIDA,
                    "numeroCuenta es obligatoria: la cuenta bancaria tiene que tener cuenta contable");
        }
        return cuenta;
    }

    /** Solo se consulta lo que cambia: una referencia histórica que no se toca no se revalida. */
    static void validarReferencias(BancariaReferenciasPort referencias, BigDecimal cuentaNueva, BigDecimal cuentaGuardada,
                                   Integer proveedorNuevo, Integer proveedorGuardado) {
        if (!mismaCuenta(cuentaNueva, cuentaGuardada) && !referencias.existeCuenta(cuentaNueva)) {
            throw new BancariaSolicitudInvalidaException(Codigo.CUENTA_INEXISTENTE,
                    "numeroCuenta " + cuentaNueva.toPlainString() + " no existe en el plan de cuentas");
        }
        if (proveedorNuevo != null && !proveedorNuevo.equals(proveedorGuardado)
                && !referencias.existeProveedor(proveedorNuevo)) {
            throw new BancariaSolicitudInvalidaException(Codigo.PROVEEDOR_INEXISTENTE,
                    "proveedorId " + proveedorNuevo + " no existe");
        }
    }

    /** La cuenta contable se compara por valor: BigDecimal.equals distingue 1 de 1.0. */
    static boolean mismaCuenta(BigDecimal a, BigDecimal b) {
        return a == null ? b == null : b != null && a.compareTo(b) == 0;
    }

    /** updated tiene resolución de segundo en la base: se compara sin la parte fraccionaria. */
    static boolean mismoInstante(LocalDateTime a, LocalDateTime b) {
        return Objects.equals(a == null ? null : a.truncatedTo(ChronoUnit.SECONDS),
                b == null ? null : b.truncatedTo(ChronoUnit.SECONDS));
    }
}
