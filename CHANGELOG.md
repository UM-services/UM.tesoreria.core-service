# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-08-11
### Added
- Nuevos endpoints: búsqueda por id de MercadoPago, reseteo de marca temporal de contratos, búsqueda optimizada de tipos de chequera, proveedores y pagos.
- Nuevos servicios: `ContratoAutoFixService`, `ProveedorSearchService`, `TipoChequeraSearchService`, `TipoPagoFechaAcreditacionService`, `TipoPagoFechaPagoService`, entre otros.
- Métodos `jsonify()` en modelos y DTOs para trazabilidad y logging estructurado.
- Test unitario para `ProveedorMovimientoController`.

### Changed
- Refactor completo de inyección de dependencias: eliminación de `@Autowired` en favor de constructores explícitos.
- Refactor de repositorios: eliminación del prefijo 'I' en interfaces, siguiendo convenciones de Spring.
- Mejoras de logging: uso de `Slf4j` y serialización JSON en logs.
- Optimizaciones de rendimiento: uso de `CompletableFuture` para consultas paralelas.
- Actualización de dependencias principales: Spring Boot 3.5.4, Kotlin 2.2.0, MySQL Connector 9.4.0.
- Actualización de diagramas de dependencias y documentación automática.

### Removed
- Archivos y configuraciones obsoletas: `TransactionConfig.java`, `RabbitMQConfig.java`, `JpaConfig.java`, `ChequeraMessageDto.kt`, `CuotaPeriodo.java`, `ChequeraCuotaDto.java`, `IChequeraPagoRepository.java`, `application.yml`, `logback-spring.xml`, y tests antiguos.

> Versión y cambios verificados en `pom.xml`, `git diff`, y código fuente.

## [0.0.1-SNAPSHOT] - Versión actual según pom.xml
## [0.1.0] - 2025-08-06
### Changed
- Actualización de dependencias:
  - Spring Boot: 3.5.3 → 3.5.4
  - Spring Boot Log4j2: 3.5.3 → 3.5.4
  - MySQL Connector: 9.3.0 → 9.4.0
- Dockerfile: Se otorgan permisos explícitos al usuario de la aplicación sobre el directorio `/app`.
- `bootstrap.yml`: Se agrega sección de health check para mail y ajustes en métricas.
- `FacultadService`: Se amplía el array de facultades soportadas (ahora incluye la facultad 6).

### Fixed
- Correcciones menores en configuración y permisos para mejorar despliegue y monitoreo.

> Versión actualizada a 0.1.0, basado en `pom.xml` y los cambios en `git diff HEAD`.

## [Unreleased] - Cambios verificables del git log

### Added
...existing code...
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