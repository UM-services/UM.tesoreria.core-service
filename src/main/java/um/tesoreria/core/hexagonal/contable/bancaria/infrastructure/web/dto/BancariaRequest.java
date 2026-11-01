package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Distingue un campo ausente de uno en null: Jackson solo llama al setter de los campos que vienen en el cuerpo.
 * Los ids van como BigDecimal y no como Integer/Long: Jackson trunca 12.7 a 12 y la cuenta quedaría asociada al
 * proveedor equivocado; el mapper exige enteros exactos. updated es la precondición opcional de la edición.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BancariaRequest {
    private final Set<String> presentes = new HashSet<>();

    private BigDecimal bancariaId;
    private String banco;
    private String numero;
    private String cbu;
    private String titular;
    private String cuit;
    private BigDecimal numeroCuenta;
    private BigDecimal proveedorId;
    private String updated;

    public boolean presente(String campo) {
        return presentes.contains(campo);
    }

    public BigDecimal getBancariaId() {
        return bancariaId;
    }

    public void setBancariaId(BigDecimal bancariaId) {
        this.bancariaId = bancariaId;
        presentes.add("bancariaId");
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
        presentes.add("banco");
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
        presentes.add("numero");
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
        presentes.add("cbu");
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
        presentes.add("titular");
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
        presentes.add("cuit");
    }

    public BigDecimal getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(BigDecimal numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
        presentes.add("numeroCuenta");
    }

    public BigDecimal getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(BigDecimal proveedorId) {
        this.proveedorId = proveedorId;
        presentes.add("proveedorId");
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
        presentes.add("updated");
    }
}
