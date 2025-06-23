# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - Cambios verificables del git log

### Added
- **Nuevo modelo TipoChequeraSearch** (commit: 5f3d934, 17 de junio 2025)
  - Modelo de vista para búsquedas optimizadas de tipos de chequera (35 líneas)
  - Incluye repositorio, servicio y controlador asociados
  - Soporte para búsquedas por condiciones de texto
- **Optimización de rendimiento en `calculateDeuda`** (commit: c6632e9, 10 de junio 2025)
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
- Nuevo endpoint para obtener información completa de inscripciones `GET /inscripcion/full/{facultadId}/{personaId}/{documentoId}/{lectivoId}` (commit: 833a92b)

### Changed
- **Refactoring de controladores** (commit: 3d6fd9d, 17 de junio 2025)
  - Eliminación de mapeos hardcodeados en `CursoCargoContratadoController`
  - Limpieza de imports en `ContratoToolController` removiendo anotación `@Auto` no utilizada
  - Agregado imports de Jackson en `ContratoToolService` para procesamiento JSON
  - Actualización de documentación en CHANGELOG.md y README.md con cambios recientes
- **Refactoring masivo de repositorios** (commit: 5f3d934, 17 de junio 2025)
  - Eliminación del prefijo 'I' en 98 interfaces de repositorios siguiendo convenciones de Spring
  - Actualización de SpringDoc OpenAPI de 2.8.8 a 2.8.9
  - Actualización de OpenPDF de 2.0.5 a 2.2.1
  - Mejora en controladores de TipoChequera
- **Mejorado el rendimiento de `calculateDeuda`** (commit: c6632e9, 10 de junio 2025)
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

## [0.0.1-SNAPSHOT] - Versión actual según pom.xml

### Nota
La versión actual del proyecto es 0.0.1-SNAPSHOT según el pom.xml. Esta es la única versión que puede ser verificada directamente del código fuente.

### Dependencias Principales (verificado en pom.xml)
- Spring Boot: 3.5.0
- Spring Cloud: 2025.0.0
- Kotlin: 2.1.21
- Java: 21
- MySQL Connector: 9.3.0
- SpringDoc OpenAPI: 2.8.9
- Apache POI: 5.4.1
- OpenPDF: 2.2.1
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
- `447b4b2` - Merge PR #141: Refactoring de repositorios y optimización de rendimiento (17 de junio 2025)
- `3d6fd9d` - refactor(controllers): remove hardcoded request mappings and optimize imports (17 de junio 2025)
- `9cab730` - Merge PR #139: Refactoring eliminación de prefijo 'I' en repositorios y actualización de documentación (17 de junio 2025)
- `5f3d934` - refactor(repositories): remove 'I' prefix from repository interfaces (17 de junio 2025)
- `8a93581` - Merge PR #137: Optimización de rendimiento en calculatedeuda con computación paralela (10 de junio 2025)
- `c6632e9` - perf(calculateDeuda): implement parallel computation with CompletableFuture (10 de junio 2025)
- `a7e078b` - Merge PR #135: Actualización de documentación y limpieza de código (3 de junio 2025)
- `cc5be90` - docs(changelog): actualiza documentación con cambios verificables (3 de junio 2025)
- `ab3bc99` - Merge PR #133: Cambio emailpagador inicial
- `62acf40` - fix emailPagador
- `1e8a299` - Merge PR #131: Limpieza de código y mejoras en gestión de domicilios
- `42b667c` - chore(cleanup): remove obsolete files and improve SpoterService
- `1cc4146` - Merge PR #129: Refactorización y mejoras en gestión de domicilios y documentos
- `a8b9de1` - refactor(domicilio): improve dependency injection and add pagador endpoint
- `7ddfee9` - Merge PR #127: Feature implementación de endpoint para obtener información completa de inscripciones
- `833a92b` - feat(inscripcion): add full endpoint for complete inscription information

### Información No Verificable
Las siguientes versiones mencionadas en el CHANGELOG anterior no pueden ser verificadas directamente del código fuente y han sido removidas:
- Versión 1.0.0 (2024-03-19)
- Versión 0.2.0 (2024-03-11) 
- Versión 0.1.0 (2024-01-01)

Estas versiones no aparecen en el historial de git ni en el pom.xml actual, por lo que no pueden ser confirmadas como verificables. 