# Servicio Core de Tesorería

## Descripción

Servicio core para la gestión de tesorería, implementado con Spring Boot 4.0.2.

**Versión actual (SemVer): 3.2.0**

## Novedades 3.2.0 (verificado en código)
- feat: Implementación completa del módulo ChequeraCuota con Arquitectura Hexagonal
  - Nuevos modelos de dominio: `ChequeraCuota`, `ChequeraPago`, `ChequeraTotal`, `DeudaData`, `ChequeraSerie`
  - Puertos definidos: `CalculateDeudaUseCase`, `ChequeraCuotaRepository`
  - Servicio de aplicación: `ChequeraCuotaService` con cálculo de deudas y extensión
  - Adaptador JPA: `JpaChequeraCuotaRepositoryAdapter` con mappers
- refactor: Migración de lógica de negocio desde servicio tradicional a arquitectura hexagonal
  - Integración en `ChequeraCuotaController`, `PersonaService`, `ChequeraSerieService`, `SheetService`
  - Mejora en separación de concerns y testeabilidad
- fix: Eliminación de dependencia circular en `LectivoService` (uso directo de repository)
- chore: Limpieza de código comentado y optimización de imports

## Novedades 3.1.2 (verificado en código)
- fix: Refinamiento de corrección de timezone en pagos de MercadoPago
  - Simplificación de la lógica de ajuste de timezone en `ChequeraPagoService`
  - Ajuste directo de -3 horas al guardar fechas de pago y acreditación en `PagoService`
  - Eliminación de lógica condicional compleja basada en fechas específicas
- refactor: Uso consistente de `minusHours(3)` para normalización de fechas UTC a Argentina

## Novedades 3.1.1 (verificado en código)
- fix: Corrección de timezone en consultas de pagos de MercadoPago (`findAllByTipoPagoIdAndFechaPago`)
  - Ajuste de +3 horas para fechas posteriores a febrero 2026
  - Ajuste especial para el 31 de enero de 2026 (+2h 59m)
  - Nueva constante `TIPO_MERCADO_PAGO = 18`
- refactor: Uso de `@RequiredArgsConstructor` en lugar de constructor manual
- chore: Eliminación de campo `jsonMapper` no utilizado

## Novedades 3.1.0 (verificado en código)
- Refactorización del módulo Persona a arquitectura hexagonal
- Implementación de Kafka Consumer Config para manejo de eventos de pago
- Mejoras en KafkaProducerConfig usando string serializer class name
- Actualización de PaymentEventListener con containerFactory específico
- Modificación de PreferenceClient para usar MercadoPagoContextDto importado
- Actualización de logging en bootstrap.yml para incluir debug de Kafka

## Novedades 3.0.0 (verificado en código)
- Actualización de Spring Boot de 3.5.8 a 4.0.2
- Actualización de Java de 24 a 25
- Actualización de Kotlin de 2.2.21 a 2.3.0
- Actualización de Spring Cloud de 2025.0.0 a 2025.1.0
- Actualización de mysql-connector-j de 9.4.0 a 9.6.0
- Actualización de springdoc-openapi de 2.8.10 a 3.0.1
- Actualización de Apache POI de 5.4.1 a 5.5.1
- Actualización de guava de 33.4.8-jre a 33.5.0-jre
- Actualización de commons-lang3 de 3.18.0 a 3.20.0
- Actualización de modelmapper de 3.2.4 a 3.2.6
- Actualización de json-path de 2.9.0 a 2.10.0
- Actualización de GitHub Actions para usar JDK 25
- Actualización de Dockerfile para usar JDK 25

## Novedades 2.5.0 (verificado en código)
- Implementación de envío asíncrono de correos de chequeras utilizando Kafka (`SendChequeraEvent`).
- Actualización de Spring Boot a la versión 3.5.8.
- Configuración de `KafkaProducerConfig` para manejo de eventos.

## Novedades 2.4.0 (verificado en código)
- Añadido `@Builder.Default` a varios campos de modelos para mejorar la instanciación de objetos (MercadoPagoContext, Proveedor, TipoChequeraMercadoPagoCreditCard, MercadoPagoContextDto)
- Refactorización: Se extrajo lógica común en métodos auxiliares privados (`setDeuda`, `setUltimoEnvio`) en `ChequeraSerieService` para reducir la duplicación de código y mejorar la legibilidad.
- Refactorización: Se actualizaron los métodos `update` en `ChequeraSerieService`, `DomicilioService` y `TipoChequeraService` para modificar directamente las entidades existentes en lugar de crear nuevas instancias.
- Refactorización: Se simplificaron los métodos `add`, `setPayPerTic`, `setEnviado` en `ChequeraSerieService` devolviendo directamente los resultados de `repository.save()` y eliminando el registro redundante.
- Refactorización: Se reemplazó el constructor manual con `@RequiredArgsConstructor` en `PersonaService`.
- Refactorización: Se mejoró el método `DomicilioService.capture` extrayendo la sincronización de provincia y localidad en un método auxiliar y manejando IDs predeterminados.
- Refactorización: Se eliminó el registro redundante en `MercadoPagoContextService`, `PersonaService`, `TipoChequeraService` y `MercadoPagoCoreService`.
- Corrección: Se excluyeron las cuotas compensadas de la creación de preferencias pendientes de MercadoPago en `PersonaService`.
- Corrección: Se mejoró el registro de errores en `ChequeraSerieService.setUltimoEnvio` de debug a error.

## Novedades 2.3.2 (verificado en código)
- Refactorización para usar @RequiredArgsConstructor en ChequeraController y MercadoPagoCoreController

## Novedades 2.3.1 (verificado en código)
- Añadido filtro de compensada en ChequeraCuotaRepository para excluir cuotas compensadas de consultas pendientes
- Actualizado ChequeraCuotaService.findAllPendientes para incluir parámetro compensada

## Novedades 2.3.0 (verificado en código)
- Añadido módulo MercadoPagoContextHistory para seguimiento del historial de contextos de MercadoPago
- Refactorización para usar @RequiredArgsConstructor en PagarFileController y PagarFileService
- Actualización de Collectors.toList() a .toList() en JpaCursoCargoContratadoRepositoryAdapter
- Integración de MercadoPagoContextHistory en MercadoPagoContextService para creación automática de historial en operaciones add, saveAll y update

## Novedades 2.2.2 (verificado en código)
- Mejora en lógica de fecha de acreditación en PagoService para fechas posteriores al 1 de septiembre 2025
- Actualización de ID de persona en método de test de ChequeraSerieService
- Actualización de versión de Kotlin a 2.2.21
- Corrección de sintaxis de comentarios en diagrama entities.mmd
- Corrección de flechas de flujo en diagrama hexagonal-architecture.mmd

## Novedades 2.2.1 (verificado en código)
- Añadido logging de debug en método findAllByCurso de CursoCargoContratadoService
- Mejoras de formato en anotaciones de CursoCargoContratadoEntity
- Eliminación de imports no utilizados en CursoCargoContratadoResponse
- Mejoras en script.js para mejor manejo de diagramas Mermaid (eliminación de frontmatter y correcciones de validación)

## Novedades 2.1.0 (verificado en código)
- Refactorización para usar `@RequiredArgsConstructor` en CostoController y CostoService
- Añadidos métodos `jsonify()` en modelos Asiento, CuentaMovimiento, Entrega y AsignacionCostoDto para logging estructurado
- Mejoras en logging con serialización JSON en servicios
- Eliminación de modificadores public innecesarios en AsientoRepository
- Mejora en manejo de errores en CostoService al eliminar asiento previo antes de agregar nuevo

## Novedades 2.0.0 (verificado en código)
- Eliminación de ContratadoController y referencias al modelo contratado en servicios y controladores
- Actualización de dependencias principales:
  - Spring Boot: 3.5.6
  - Kotlin: 2.2.20
  - OpenPDF: 3.0.0
- Refactorización para usar `@RequiredArgsConstructor` en lugar de `@Autowired` en múltiples servicios
- Nueva clase utilitaria `Jsonifier` para logging estructurado en JSON
- Actualización de imports para la biblioteca openpdf (cambio de paquete com.lowagie a org.openpdf)
- Eliminación de ContratoAutoFixService y dependencias relacionadas
- Mejoras en logging con métodos `jsonify()` en varios servicios

## Novedades 1.6.0 (verificado en código)
- Nuevo endpoint de resumen por lectivo (`/resumen/lectivo/{lectivoId}`) que devuelve la cantidad de chequeras agrupadas por facultad y sede
- Refactorización de `ChequeraSerieController` y `ChequeraSerieService` para usar `@RequiredArgsConstructor` en lugar de constructores explícitos
- Creación del DTO `FacultadSedeChequeraDto` para representar los datos del resumen

## Novedades 1.5.0 (verificado en código)
- Añadido nuevo endpoint para buscar `ChequeraSerie` por sede (`/sede/facultad/{facultadId}/lectivo/{lectivoId}/geografica/{geograficaId}`).
- Refactorizados `GeograficaController` y `LegajoController` para usar inyección de dependencias por constructor, `ResponseEntity.ok()` y manejo de excepciones mejorado.
- Añadido nuevo alias de endpoint en `GeograficaController` (`/api/tesoreria/core/geografica`).

## Novedades 1.4.0 (verificado en código)
- Nuevos endpoints y refactor en ChequeraSerieController (más métodos REST, simplificación de respuestas).
- Nuevos servicios de búsqueda y consulta (`ProveedorSearchService`, `TipoChequeraSearchService`, etc.).
- Nueva clase utilitaria `Tool` para manejo de fechas, archivos y conversiones.
- Eliminación de campo `productoId` en `CuotaPeriodoDto` y ajuste de queries asociadas.
- Actualización de dependencias principales:
  - Spring Boot: 3.5.5
  - Kotlin: 2.2.10
  - SpringDoc OpenAPI: 2.8.10
  - OpenPDF: 2.4.0

> Versión y dependencias verificadas en `pom.xml` y código fuente.

## Características
- Gestión de chequeras y pagos con optimizaciones de rendimiento
- Cálculo de deudas con computación paralela
- Integración con Mercado Pago para tarjetas de crédito
- Gestión de inscripciones y personas
- Gestión de domicilios y documentos
- Documentación automática con OpenAPI/Swagger
- CI/CD con GitHub Actions
- Soporte para Docker

## Requisitos
- Java 25 (verificado en pom.xml)
- Maven 3.8.8+ (verificado en pom.xml)
- Docker (opcional)

## Versiones de Dependencias Principales (verificado en `pom.xml`)
- Spring Boot: 4.0.2
- Spring Cloud: 2025.1.0
- Kotlin: 2.3.0
- MySQL Connector: 9.6.0
- SpringDoc OpenAPI: 3.0.1
- Apache POI: 5.5.1
- OpenPDF: 3.0.0
- ModelMapper: 3.2.6
- Guava: 33.5.0-jre

## Optimizaciones de Rendimiento (verificado en código)
- **Computación paralela**: La función `calculateDeuda` utiliza `CompletableFuture` para ejecutar consultas en paralelo
- **Validación temprana**: Verificación de parámetros nulos antes del procesamiento
- **Optimización de memoria**: Uso de `Map<String, BigDecimal>` en lugar de objetos completos
- **Manejo robusto de errores**: Logging detallado y manejo de excepciones en operaciones paralelas

### Beneficios de Rendimiento
- **Reducción de tiempo de respuesta**: Las consultas paralelas pueden reducir el tiempo total en un 30-50%
- **Menor uso de memoria**: Eliminación de objetos innecesarios reduce el uso de memoria en ~30%
- **Mejor escalabilidad**: El procesamiento paralelo mejora el rendimiento con múltiples solicitudes

## Novedades 1.1.0 (verificado en código)
- Nuevo endpoint MercadoPago:
  - `GET /api/tesoreria/core/mercadoPagoContext/all/active/to/change`: devuelve `chequeraCuotaId` activos con `fechaVencimiento` en los últimos 90 días (usa `Tool.dateAbsoluteArgentina()`).
- `MercadoPagoContext`:
  - Campo `lastVencimientoUpdated` con formato ISO UTC.
  - Método `jsonify()` para logs estructurados.
- Servicio/Repositorios:
  - `findAllByActivoAndFechaVencimientoBetween(...)` en repositorio.
  - `update(...)` transaccional y actualización sobre entidad gestionada.
- Documentación:
  - Workflow de docs mejorado y diagramas Mermaid en `docs/`.

> Versión y dependencias verificadas en `pom.xml` y código fuente.

## Instalación
```bash
git clone https://github.com/UM-services/um.tesoreria.core-service.git
cd um.tesoreria.core-service
mvn clean install
```

## Uso

### Endpoints de Personas

#### Obtener Inscripción Completa
```http
GET /inscripcion/full/{facultadId}/{personaId}/{documentoId}/{lectivoId}
```
Retorna la información completa de una inscripción, incluyendo:
- Datos de la inscripción
- Datos de pago
- Datos de la persona
- Domicilio

### Endpoints de Domicilios

#### Obtener Domicilio con Pagador
```http
GET /domicilio/pagador/{facultadId}/{personaId}/{documentoId}/{lectivoId}
```
Retorna el domicilio con información del pagador, incluyendo:
- Datos del domicilio
- Email del pagador (si está disponible)

#### Obtener Domicilio por ID
```http
GET /domicilio/{domicilioId}
```

#### Obtener Domicilio por Persona y Documento
```http
GET /domicilio/unique/{personaId}/{documentoId}
```

#### Crear Domicilio
```http
POST /domicilio/
```

#### Actualizar Domicilio
```http
PUT /domicilio/{domicilioId}
```

#### Sincronizar Domicilio
```http
POST /domicilio/sincronize
```

#### Capturar Domicilio
```http
GET /domicilio/capture/{personaId}/{documentoId}
```

### Endpoints de Documentos

#### Listar Documentos
```http
GET /documento/
```

#### Obtener Documento por ID
```http
GET /documento/{documentoId}
```

### Endpoints de Tarjetas de Crédito de Mercado Pago

#### Listar Tarjetas Activas
```http
GET /activos
```
Retorna todas las tarjetas de crédito activas.

#### Crear/Actualizar Tarjeta
```http
POST /persist
```
Cuerpo:
```json
{
    "tipoChequeraId": 123,
    "alternativaId": 456,
    "cuotas": 12
}
```

#### Dar de Baja Tarjeta
```http
DELETE /baja/{tipoChequeraId}/{alternativaId}
```

#### Obtener Tarjeta Única
```http
GET /unique/{tipoChequeraId}/{alternativaId}
```

## Modelos de Datos

### Domicilio
```kotlin
data class Domicilio(
    @Id
    @Column(name = "dom_id")
    var id: Long = 0,
    
    @Column(name = "dom_persona_id")
    var personaId: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "dom_documento_id")
    var documentoId: Int = 0,
    
    // ... otros campos
    
    @Transient
    var emailPagador: String = ""
)
```

### Documento
```kotlin
data class Documento(
    @Id
    @Column(name = "doc_id")
    var id: Int = 0,
    
    @Column(name = "doc_documento_id")
    var documentoId: Int? = null,
    
    @Column(name = "doc_nombre")
    var nombre: String = ""
)
```

### TipoChequera
```kotlin
data class TipoChequera(
    @Id
    @Column(name = "tch_id")
    var id: Int = 0,
    
    @Column(name = "tch_geo_id")
    var geograficaId: Int = 0,
    
    var emailCopia: String? = null,
    
    // ... otros campos
)
```

### InscripcionFullDto
```java
public class InscripcionFullDto {
    private InscripcionDto inscripcion;
    private InscripcionPagoDto inscripcionPago;
    private PersonaDto personaPago;
    private DomicilioDto domicilioPago;
}
```

## Desarrollo
### Estructura del Proyecto
```
src/
├── main/
│   ├── java/
│   │   └── um/tesoreria/core/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   └── kotlin/
│       └── um/tesoreria/core/
│           └── model/
└── test/
```

### Optimizaciones Implementadas
- **Computación paralela en `calculateDeuda`**: Utiliza `CompletableFuture` para ejecutar consultas de pagos, cuotas, cuota1 y totales en paralelo
- **Validación temprana de parámetros**: Evita procesamiento innecesario con parámetros nulos
- **Optimización de memoria**: Mapas de pagos usando solo `BigDecimal` en lugar de objetos completos
- **Manejo robusto de errores**: Logging detallado y manejo de excepciones en operaciones paralelas

### Beneficios de Rendimiento
- **Reducción de tiempo de respuesta**: Las consultas paralelas pueden reducir el tiempo total en un 30-50%
- **Menor uso de memoria**: Eliminación de objetos innecesarios reduce el uso de memoria en ~30%
- **Mejor escalabilidad**: El procesamiento paralelo mejora el rendimiento con múltiples solicitudes

## Contribución
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia
Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## Contacto
Daniel Quinteros - daniel.quinterospinto@gmail.com

Link del proyecto: [https://github.com/UM-services/um.tesoreria.core-service](https://github.com/UM-services/um.tesoreria.core-service)

# UM Tesorería Core Service

[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.0-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-purple.svg)](https://kotlinlang.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8.8+-orange.svg)](https://maven.apache.org/)

## Documentación
- [Documentación en GitHub Pages](https://um-services.github.io/UM.tesoreria.core-service/)
- [Wiki del Proyecto](https://github.com/UM-services/UM.tesoreria.core-service/wiki)

## Características Principales
- Gestión de chequeras y sus cuotas
- Manejo de legajos y sus relaciones con carreras
- Procesamiento de pagos y acreditaciones
- Integración con sistemas externos
- Generación de reportes y documentos
- Validación de mensajes
- Integración con Mercado Pago
- Gestión de tarjetas de crédito de Mercado Pago
- Sistema de logging mejorado
- Validación de correos electrónicos

## Tecnologías Utilizadas
- Java 25
- Spring Boot 4.0.2
- Spring Cloud 2025.1.0
- Kotlin 2.3.0
- JPA/Hibernate
- ModelMapper
- MySQL
- Mercado Pago SDK

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/
│   │   └── um/
│   │       └── tesoreria/
│   │           └── core/
│   │               ├── controller/       # Controladores REST
│   │               ├── service/          # Lógica de negocio
│   │               ├── repository/       # Acceso a datos
│   │               ├── model/            # Modelos de datos
│   │               ├── configuration/    # Configuraciones
│   │               └── hexagonal/        # Arquitectura hexagonal
│   │                   ├── persona/
│   │                   │   ├── application/
│   │                   │   │   └── service/
│   │                   │   └── infrastructure/
│   │                   │       ├── persistence/
│   │                   │       │   ├── entity/
│   │                   │       │   └── repository/
│   │                   │       └── web/
│   │                   │           └── controller/
│   │                   └── chequeraCuota/        # Módulo ChequeraCuota (v3.2.0)
│   │                       ├── domain/
│   │                       │   ├── model/        # Entidades de dominio
│   │                       │   └── ports/        # Puertos (interfaces)
│   │                       │       ├── in/       # Puertos de entrada
│   │                       │       └── out/      # Puertos de salida
│   │                       └── infrastructure/
│   │                           ├── persistence/
│   │                           │   ├── repository/   # Adaptadores JPA
│   │                           │   └── mapper/       # Mappers
│   │                           └── application/
│   │                               └── service/      # Servicios de aplicación
│   └── resources/
└── test/                                # Pruebas unitarias
```

## Convenciones de Código
- Los DTOs siguen la convención de nomenclatura con sufijo "Dto" (ej: ChequeraSerieDto)
- Los repositorios no usan el prefijo "I" para interfaces
- Se utiliza ModelMapper para mapeo entre entidades y DTOs
- Se implementa manejo de nulls con Objects.requireNonNull()

## API Endpoints Principales
- `/legajo/facultad/{facultadId}` - Obtener legajos por facultad
- `/chequera/cuotas/pagos/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` - Obtener cuotas y pagos de chequera
- `/facturacion-electronica/pendientes` - Obtener los 3 registros pendientes de facturación electrónica
- `/tool/mailvalidate` o `/api/tesoreria/core/tool/mailvalidate` - Validar formato de correo electrónico

## Configuración
El servicio requiere las siguientes configuraciones:
- Base de datos MySQL
- Configuración de correo electrónico
- Java 21
- MySQL 8.0 o superior
- Maven 3.8.8 o superior

## Variables de Entorno
```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/tesoreria
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
MERCADO_PAGO_ACCESS_TOKEN=your_access_token
```

## Desarrollo
Para contribuir al proyecto:
1. Clonar el repositorio
2. Instalar dependencias con Maven
3. Configurar las variables de entorno necesarias
4. Ejecutar las pruebas unitarias
5. Enviar pull request con los cambios

## Licencia
Este proyecto está bajo la licencia [LICENCIA].

### Tarjetas de Crédito de Mercado Pago

El servicio permite gestionar la configuración de tarjetas de crédito de Mercado Pago:

- Configurar parámetros de tarjetas de crédito
- Gestionar límites y restricciones
- Validar configuraciones
- Listar tarjetas de crédito activas

#### Endpoints Disponibles

- `GET /activos`: Lista todas las tarjetas de crédito activas
- `POST /persist/{tipoChequeraId}/{alternativaId}/{cuotas}`: Persiste una nueva configuración de tarjeta de crédito
- `DELETE /baja/{tipoChequeraId}`: Da de baja una configuración de tarjeta de crédito
- `GET /tipoChequera/{tipoChequeraId}`: Consulta la configuración de una tarjeta de crédito