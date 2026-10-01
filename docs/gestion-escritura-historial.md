# Historial transaccional de escrituras (Gestión) — issue #404

## Objetivo

Registrar, **en la misma transacción** de la operación de negocio, cada alta, edición o baja
con fecha, entidad, clave y valores anteriores/nuevos. Bloquea el cierre de escrituras Java
nuevas de Parámetros/Gestión que dependan de este mecanismo (#405–#409).

## Contrato reutilizable

Puerto de entrada: `RegistrarEscrituraHistorialUseCase`

| Método | valor_anterior | valor_nuevo |
|--------|----------------|-------------|
| `registrarAlta(entidad, clave, nuevo)` | vacío | estado creado |
| `registrarEdicion(entidad, clave, antes, despues)` | estado previo | estado nuevo |
| `registrarBaja(entidad, clave, antes)` | estado previo | vacío |

Implementación: `EscrituraHistorialService` (propagación `REQUIRED` → participa de la TX del caller).

### Convenciones

- **entidad**: nombre estable del agregado/tabla de negocio (`proveedor`, `ejercicio`, `bancaria`, `cuenta`, `articulo`, …).
- **entidadClave**: clave de negocio como texto (`"42"`, o compuesta `"1:2"`). Debe permanecer igual a lo largo del ciclo de vida para ligar el historial.
- **valores**: JSON compacto del estado relevante. No incluir secretos innecesarios; no loguear ni devolver en APIs públicas.
- **Sin actor verificado**: no se inventa ni persiste un usuario como identidad autenticada por el servidor.
- **Sin consulta pública**: no hay controller REST de historial. El puerto de repositorio solo se usa internamente (y en tests).

### Ejemplo de integración

```java
@Service
@RequiredArgsConstructor
public class EjemploEscrituraService {

    private final EjemploRepository repository;
    private final RegistrarEscrituraHistorialUseCase historial;

    @Transactional
    public Ejemplo alta(Ejemplo nuevo) {
        var guardado = repository.save(nuevo);
        historial.registrarAlta("ejemplo", String.valueOf(guardado.getId()), guardado);
        return guardado;
    }

    @Transactional
    public Ejemplo edicion(Long id, Ejemplo cambios) {
        var antes = repository.findById(id).orElseThrow();
        var despues = repository.save(merge(antes, cambios));
        historial.registrarEdicion("ejemplo", String.valueOf(id), antes, despues);
        return despues;
    }

    @Transactional
    public void baja(Long id) {
        var antes = repository.findById(id).orElseThrow();
        repository.deleteById(id);
        historial.registrarBaja("ejemplo", String.valueOf(id), antes);
    }
}
```

Si la operación de negocio lanza y la TX se revierte, **no queda evento** de historial.

## Esquema

Script reproducible: [`docs/sql/V404__gestion_escritura_historial.sql`](sql/V404__gestion_escritura_historial.sql)

Tabla `gestion_escritura_historial` en `tesium`. Aplicar manualmente (o con el proceso de esquema del equipo)
porque el servicio usa `spring.jpa.hibernate.ddl-auto: none`.

## Relación con Auditable

`created` / `updated` de `Auditable` en las entidades de negocio **no sustituyen** este historial:
solo marcan timestamps de fila; el historial captura operación, clave y antes/después.

## Diagrama

Ver [`hexagonal-escrituraHistorial.mmd`](hexagonal-escrituraHistorial.mmd).
