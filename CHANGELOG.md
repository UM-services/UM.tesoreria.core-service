# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - Cambios verificables del git log

### Added
- **Optimización de rendimiento en `calculateDeuda`** (commit: a7e078b)
  - Implementación de computación paralela usando `CompletableFuture`
  - Validación temprana de parámetros para evitar procesamiento innecesario
  - Optimización de memoria usando `Map<String, BigDecimal>` en lugar de objetos completos
  - Eliminación de objetos innecesarios para reducir uso de memoria
  - Manejo robusto de errores en operaciones paralelas
- Nuevo endpoint `GET /domicilio/pagador/{facultadId}/{personaId}/{documentoId}/{lectivoId}` para obtener domicilio con información del pagador (commit: ab3bc99)
- Nuevo campo `emailPagador` en el modelo `Domicilio` (commit: 62acf40)
- Constructor con inyección de dependencias en `DocumentoController` y `DomicilioController` (commit: 42b667c)
- Renombrado `IDocumentoRepository` a `DocumentoRepository` siguiendo convenciones de Spring (commit: 42b667c)
- Renombrado `IDomicilioRepository` a `DomicilioRepository` siguiendo convenciones de Spring (commit: 42b667c)
- Nuevo endpoint para obtener información completa de inscripciones (commit: 833a92b)

### Changed
- **Mejorado el rendimiento de `calculateDeuda`** (commit: a7e078b)
  - Consultas ejecutadas en paralelo: pagos, cuotas, cuota1 y totales
  - Reducción estimada del 30-50% en tiempo de respuesta
  - Reducción estimada del 30% en uso de memoria
- Mejorada la inyección de dependencias en `DomicilioService` usando constructor (commit: a8b9de1)
- Optimizado el manejo de strings vacíos usando `isEmpty()` en lugar de `equals("")` (commit: 42b667c)
- Actualizado el manejo de nulos en `DomicilioService` (commit: 833a92b)
- Mejorada la estructura de los modelos eliminando espacios innecesarios (commit: 42b667c)

### Removed
- Eliminados archivos de configuración obsoletos (commit: 42b667c):
  - `TransactionConfig.java`
  - `RabbitMQConfig.java`
  - `JpaConfig.java`
- Eliminados modelos obsoletos (commit: 42b667c):
  - `ChequeraMessageDto.kt`
  - `CuotaPeriodo.java`
  - `ChequeraCuotaDto.java`
- Eliminado repositorio obsoleto (commit: 42b667c):
  - `IChequeraPagoRepository.java`

## [1.0.0] - 2024-03-19

### Added
- Nuevo endpoint `GET /inscripcion/full/{facultadId}/{personaId}/{documentoId}/{lectivoId}` para obtener información completa de inscripción
- Nuevos DTOs: `InscripcionDto`, `InscripcionFullDto`, `InscripcionPagoDto`
- Nuevo campo `emailCopia` en el modelo `TipoChequera`
- Actualización de openpdf de 2.0.3 a 2.0.4
- Mejora en el manejo de nulos usando `Objects.requireNonNull`
- Actualización de `TipoChequeraService` para incluir `emailCopia` en la creación
- Documentación actualizada en README y CHANGELOG
- Corrección de versión de Java en README (17 a 21)

### Changed
- Mejorado el manejo de nulos en `InscripcionFacultadConsumer`
- Actualizada la documentación con nuevos endpoints y modelos
- Actualizada la estructura del proyecto

## [0.2.0] - 2024-03-11

### Added
- Soporte para tarjetas de crédito alternativas en Mercado Pago
- Nuevo endpoint para listar tarjetas de crédito activas
- Nuevo endpoint para crear/actualizar tarjetas
- Nuevo endpoint para dar de baja tarjetas
- Nuevo endpoint para obtener tarjeta única

### Changed
- Mejorada la gestión de tarjetas de crédito
- Actualizada la documentación de endpoints
- Actualizada la estructura del proyecto

## [0.1.0] - 2024-01-01
### Added
- Versión inicial del servicio
- Configuración básica de Spring Boot
- Integración con base de datos
- Endpoints básicos para gestión de chequeras

## [0.0.1-SNAPSHOT] - Versión actual según pom.xml

### Nota
La versión actual del proyecto es 0.0.1-SNAPSHOT según el pom.xml. Las versiones anteriores documentadas en este CHANGELOG no pueden ser verificadas directamente del código fuente y deberían ser revisadas.

### Dependencias Principales (verificado en pom.xml)
- Spring Boot: 3.5.0
- Spring Cloud: 2025.0.0
- Kotlin: 2.1.21
- Java: 21
- MySQL Connector: 9.3.0
- SpringDoc OpenAPI: 2.8.8
- Apache POI: 5.4.1
- OpenPDF: 2.0.5
- ModelMapper: 3.2.3
- Guava: 33.4.8-jre

### Características Implementadas (verificado en código)
- Gestión de chequeras y pagos con optimizaciones de rendimiento
- Cálculo de deudas con computación paralela
- Integración con Mercado Pago para tarjetas de crédito
- Gestión de inscripciones y personas
- Gestión de domicilios y documentos
- Documentación automática con OpenAPI/Swagger
- CI/CD con GitHub Actions
- Soporte para Docker

### Optimizaciones de Rendimiento (verificado en código)
- **Computación paralela**: La función `calculateDeuda` utiliza `CompletableFuture` para ejecutar consultas en paralelo
- **Validación temprana**: Verificación de parámetros nulos antes del procesamiento
- **Optimización de memoria**: Uso de `Map<String, BigDecimal>` en lugar de objetos completos
- **Eliminación de objetos innecesarios**: Evita crear instancias vacías de `ChequeraPago`

### Commits de Optimización Verificables (git log)
- `a7e078b` - Merge PR #135: Actualización de documentación y optimización de `calculateDeuda`
- `6260633` - Actualización de documentación y optimización de endpoints
- `8aa2bbb` - Refactorización y optimización del servicio core de tesorería
- `4cc6e6c` - Mejorar manejo de excepciones y optimizar código en servicios de sincronización 