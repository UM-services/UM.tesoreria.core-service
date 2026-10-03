package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.serialization;

import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentMap;
import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonEscrituraValorSerializerTest {

    private final JacksonEscrituraValorSerializer serializer = new JacksonEscrituraValorSerializer();

    @Test
    void nullQuedaNull() {
        assertThat(serializer.serialize(null)).isNull();
    }

    @Test
    void stringSeSerializaComoJsonValido() {
        assertThat(serializer.serialize("texto")).isEqualTo("\"texto\"");
    }

    @Test
    void objetoAJsonCompacto() {
        Map<String, Object> valor = new LinkedHashMap<>();
        valor.put("id", 1);
        valor.put("nombre", "X");
        assertThat(serializer.serialize(valor)).isEqualTo("{\"id\":1,\"nombre\":\"X\"}");
    }

    @Test
    void fechasEnIsoDecimalesPlanosYClavesOrdenadas() {
        // El formato queda fijo en filas que no se reescriben: fechas legibles, con offset, montos planos
        // y claves ordenadas (el mismo estado da el mismo texto en cualquier corrida)
        Map<String, Object> valor = new LinkedHashMap<>();
        valor.put("created", OffsetDateTime.parse("2026-10-02T10:15:30-03:00"));
        valor.put("vencimiento", LocalDate.of(2026, 10, 2));
        valor.put("importe", new BigDecimal("1E+2"));

        assertThat(serializer.serialize(valor)).isEqualTo(
                "{\"created\":\"2026-10-02T10:15:30-03:00\",\"importe\":100,\"vencimiento\":\"2026-10-02\"}");
    }

    @Test
    void valorNoSerializable_lanzaIllegalArgumentSinValoresEnElMensaje() {
        // Un estado que no se puede serializar aborta el registro en vez de guardar historial vacío
        assertThatThrownBy(() -> serializer.serialize(new SinPropiedades()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("No se pudo serializar el valor de historial (SinPropiedades")
                .hasNoCause();
    }

    @Test
    void entidadJpa_seRechaza() {
        // La entidad administrada se pisa con el save del caller: hay que pasar una copia del estado
        assertThatThrownBy(() -> serializer.serialize(new ProveedorEntity()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ProveedorEntity");
    }

    @Test
    void entidadJpaAnidada_tambienSeRechaza() {
        // Una copia superficial que envuelve la entidad la serializaría después del save
        assertThatThrownBy(() -> serializer.serialize(Map.of("proveedor", new ProveedorEntity())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ProveedorEntity");
    }

    @Test
    void coleccionesDeHibernateAnidadas_seRechazan() {
        // Una copia superficial que guarda la colección de la entidad: lista y mapa de Hibernate.
        // Se rechazan por tipo, antes de leer (o cargar) el contenido.
        var conLista = new CopiaSuperficial(new PersistentBag<>(), Map.of());
        var conMapa = new CopiaSuperficial(List.of(), new PersistentMap<>());

        assertThatThrownBy(() -> serializer.serialize(conLista))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PersistentBag");
        assertThatThrownBy(() -> serializer.serialize(conMapa))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PersistentMap");
        // Las colecciones comunes siguen pasando
        assertThat(serializer.serialize(new CopiaSuperficial(List.of("a"), Map.of("a", "b"))))
                .isEqualTo("{\"lista\":[\"a\"],\"mapa\":{\"a\":\"b\"}}");
    }

    @Test
    void clavesDecimalesQueComparanIgual_seRechazanEnVezDePerderEntradas() {
        // Al ordenar claves, 1.0 y 1.00 compararían igual y una entrada pisaría a la otra sin aviso
        Map<Object, Object> valor = new LinkedHashMap<>();
        valor.put(new BigDecimal("1.0"), "PRIMERO");
        valor.put(new BigDecimal("1.00"), "SEGUNDO");

        assertThatThrownBy(() -> serializer.serialize(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Clave de Map no permitida")
                .hasMessageContaining("BigDecimal");
    }

    @Test
    void entidadJpaComoClaveDeMap_seRechaza() {
        // Como clave se guardaría con su toString(), sin el id del registro
        Map<ProveedorEntity, String> porProveedor = new TreeMap<>(Comparator.comparing(ProveedorEntity::getRazonSocial));
        porProveedor.put(new ProveedorEntity(), "x");

        assertThatThrownBy(() -> serializer.serialize(Map.of("porProveedor", porProveedor)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ProveedorEntity");
    }

    @Test
    void clavesPermitidas_textoEnterosEnumsYBooleanos() {
        Map<String, Object> valor = new LinkedHashMap<>();
        valor.put("texto", Map.of("nombre", 1));
        valor.put("enteros", Map.of(7L, 2));
        valor.put("enums", Map.of(Operacion.ALTA, 3));
        valor.put("booleanos", Map.of(true, 4));

        assertThat(serializer.serialize(valor)).isEqualTo(
                "{\"booleanos\":{\"true\":4},\"enteros\":{\"7\":2},\"enums\":{\"ALTA\":3},\"texto\":{\"nombre\":1}}");
    }

    @Test
    void clavesDistintasConElMismoNombreEnJson_seRechazan() {
        // 1, 1L y "1" son claves distintas para el TreeMap pero las tres se escriben como "1"
        Map<Object, String> valor = new TreeMap<>(
                Comparator.comparing((Object k) -> k.getClass().getName()).thenComparing(Object::toString));
        valor.put(1, "INTEGER");
        valor.put(1L, "LONG");
        valor.put("1", "STRING");

        assertThatThrownBy(() -> serializer.serialize(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("claves que se repiten");
    }

    enum Operacion { ALTA }

    record CopiaSuperficial(List<String> lista, Map<String, String> mapa) {
    }

    static class SinPropiedades {
    }
}
