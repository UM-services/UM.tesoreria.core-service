# Servicio Core de Tesorería

## Descripción

Servicio core para la gestión de tesorería, implementado con Spring Boot 4.1.0.

**Versión actual (SemVer): 3.42.1**

## Novedades 3.42.1 (verificado en código)
- fix(politicaArancelaria): Corregida lógica de `resolveImporteReferencia` en `RecalculateCuotaByUniqueIndexUseCaseImpl`
  - Antes retornaba `cuotaReferencia.getImporte3()` siempre que la cuota en revisión fuera mayor al importe base
  - Ahora retorna `cuotaEnRevision.getImporte3()` para preservar importes customizados por el usuario
- fix(politicaArancelaria): Nuevo try-catch `LectivoCuotaException` en `resolveCuotaReferencia`
  - Cuando `LectivoCuotaService.findCuotaByFecha()` no encuentra cuota de referencia, crea un `LectivoCuota` con importes en cero
  - Previene `LectivoCuotaException` no controlada al no existir cuota lectiva para la fecha
- chore(politicaArancelaria): Añadidos 3 statements de debug logging en `RecalculateCuotaByUniqueIndexUseCaseImpl`

> Basado en análisis profundo de `git diff HEAD` (1 archivo staged, +13/-3 líneas) y `pom.xml` (versión 3.42.0, cambios de nivel patch sobre v3.42.0).

## Novedades 3.42.0 (verificado en código)
- feat(lectivoCuota): Nuevo caso de uso `FindLectivoCuotaByFechaUseCase` para buscar cuotas por fecha de vencimiento
  - Nuevo puerto de entrada: `FindLectivoCuotaByFechaUseCase` con método `findCuotaByFecha(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, fecha)`
  - Nuevo caso de uso: `FindLectivoCuotaByFechaUseCaseImpl` que filtra cuotas por `vencimiento2 >= fecha` y `importe2 > 0`
  - Nuevo método `findCuotaByFecha(...)` en `LectivoCuotaService` con delegación al caso de uso
- feat(lectivoCuota): Enriquecimiento del modelo de dominio `LectivoCuota` con formato ISO 8601
  - Añadida anotación `@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXX")` a campos `vencimiento1`, `vencimiento2`, `vencimiento3`
- feat(lectivoCuota): Nuevo método `findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaId` en `LectivoCuotaRepository`
  - Implementación en `JpaLectivoCuotaRepositoryAdapter` con consulta derivada `Vencimiento2GreaterThanEqualAndImporte2GreaterThan`
- feat(politicaArancelaria): Refactorización de `RecalculateCuotaByUniqueIndexUseCaseImpl`
  - `resolveCuotaReferencia`: ahora busca fallback en `LectivoCuotaService.findCuotaByFecha()` cuando falla `ChequeraCuotaService.getCuotaActual()`
  - `findCuotaEnRevisionFromLectivo`: usa `findCuotaByFecha()` en lugar de `findByUniqueKey()` para mayor precisión temporal
  - Resolución de `ahora` (OffsetDateTime.now()) una sola vez y pasada por parámetro
- fix(lectivoCuota): Nuevo constructor de `LectivoCuotaException` para 6 parámetros + fecha

> Basado en análisis profundo de `git diff HEAD` (11 archivos modificados, +113/-30 líneas) y `pom.xml` (versión 3.41.0 → 3.42.0).

## Novedades 3.41.0 (verificado en código)
- feat(lectivoCuota): Nuevo módulo LectivoCuota con arquitectura hexagonal completa (3 casos de uso)
  - Modelo de dominio: `LectivoCuota` con campos de cuotas lectivas (facultad, lectivo, tipo, producto, alternativa, cuota, importes, vencimientos, tramo)
  - 3 puertos de entrada: `FindLectivoCuotaByUniqueKeyUseCase`, `GetLectivoCuotasByFacultadLectivoTipoUseCase`, `GetLectivoCuotasByTipoUseCase`
  - Puerto de salida: `LectivoCuotaRepository` con consultas por facultad/lectivo/tipo y unique key
  - Servicio de aplicación: `LectivoCuotaService` con delegación a casos de uso
  - Adaptador JPA: `JpaLectivoCuotaRepositoryAdapter` con mapper y entidad migrada desde `core/model/`
- feat(politicaArancelaria): Fallback a `LectivoCuota` cuando `ChequeraCuota` no existe
  - `RecalculateCuotaByUniqueIndexUseCaseImpl`: si no encuentra cuota en chequera, busca en lectivo actual y construye cuota virtual
- refactor: Eliminación de archivos legacy `LectivoCuota.java`, `LectivoCuotaRepository.java`, `LectivoCuotaService.java`
- refactor: Actualización de imports en `PreuniversitarioChequeraService`, `SpoterService`, `PlantillaArancelService`
- fix: Corregido mensaje de `ChequeraCuotaException` (parámetro sobrante)

> Basado en análisis profundo de `git diff HEAD` (21 archivos modificados, +383/-90 líneas) y `pom.xml` (versión 3.40.0 → 3.41.0).

## Novedades 3.40.0 (verificado en código)
- feat(chequeraPago): Enriquecimiento del modelo de dominio `ChequeraPago` con asociaciones a `TipoPago`, `Producto` y `ChequeraCuota`
  - Nuevos campos `TipoPago tipoPago`, `Producto producto`, `ChequeraCuota chequeraCuota` en el modelo de dominio `ChequeraPago`
  - `ChequeraPagoMapper`: inyecta `ProductoMapper` y `ChequeraCuotaMapper` para mapeo completo desde entidad JPA
  - `ChequeraPagoResponse`: expone los 3 objetos asociados en respuestas REST
  - `ChequeraPagoDtoMapper`: mapea las asociaciones del dominio al DTO

> Basado en análisis profundo de `git diff HEAD` (4 archivos staged, +21/-0 líneas) y `pom.xml` (versión 3.38.0 → 3.40.0).

## Novedades 3.39.0 (verificado en código)
- feat(claseChequera): Nuevo campo `tramite` (Byte) en el modelo de dominio `ClaseChequera`
  - `ClaseChequera.tramite`: campo para identificar chequeras de tipo trámite
  - `ClaseChequeraEntity.tramite`: persistencia con valor por defecto `0`
  - `ClaseChequeraRequest`/`ClaseChequeraResponse`: expone el campo en API REST
  - `ClaseChequeraMapper`: mapeo bidireccional del campo `tramite`
  - `ClaseChequeraDtoMapper`: mapeo del campo `tramite` en DTOs
- feat(claseChequera): Nuevo caso de uso `GetAllClaseChequeraByTramiteUseCase`
  - Nuevo puerto de entrada: `GetAllClaseChequeraByTramiteUseCase` con método `getAllClaseChequeraByTramite()`
  - Nuevo caso de uso: `GetAllClaseChequeraByTramiteUseCaseImpl` que filtra por `tramite = (byte) 1`
  - Nuevo método `findAllByTramite(Byte)` en `ClaseChequeraRepository`, `JpaClaseChequeraRepository` y `JpaClaseChequeraRepositoryAdapter`
  - Nuevo método `findAllByTramite()` en `ClaseChequeraService` con delegación al caso de uso
  - Nuevo endpoint: `GET /claseChequera/tramite` retorna todas las chequeras de tipo trámite
- refactor(claseChequeraEntity): `ClaseChequeraEntity` implementa `Jsonifyable` en lugar de definir `jsonify()` manualmente
- fix(docs): Registrado diagrama `hexagonal-politicaArancelaria.mmd` en pipeline `script.js`

> Basado en análisis profundo de `git diff HEAD` (13 archivos staged, +66/-5 líneas) y `pom.xml` (versión 3.38.0 → 3.39.0).

## Novedades 3.38.0 (verificado en código)
- feat(chequeraCuota): Nuevo caso de uso `GetCuotaActualUseCase` para obtener la cuota vigente por fecha
  - Filtra cuotas por `vencimiento1` o `vencimiento2` anterior a `fechaActual`
  - Resuelve `chequeraId` si es null, delegando a `ChequeraSerieService.findByUnique()`
  - Nuevo endpoint: `GET /chequeraCuota/cuotaActual/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}`
- feat(politicaArancelaria): Nuevo parámetro `plazo` (días) en endpoint de recálculo de cuota
  - Endpoint: `GET .../politicaArancelaria/recalculate/cuota/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}/plazo/{dias}`
  - Nuevo DTO `PoliticaArancelariaResponse` con 28 campos + `PoliticaArancelariaDtoMapper`
  - `RecalculateCuotaByUniqueIndexUseCaseImpl`: compara importe3 con cuota de referencia, actualiza vencimiento3 con plazo días
- feat(mercadoPago): Nuevos endpoints batch para contextos MercadoPago
  - `GET /mercadoPagoCore/makeContextsChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}`: crea contextos MP para cuotas pendientes
  - `PUT /mercadoPagoCore/updateContexts`: actualiza lista de contextos MP en batch
- feat(chequeraCuota): Enriquecimiento del `@EntityGraph` con 9 asociaciones anidadas adicionales (evita LazyInitializationException)
- fix(docs): Eliminación de namespaces vacíos en 20 diagramas Mermaid + sanitizador en `script.js`

> Basado en análisis profundo de `git diff HEAD` (39 archivos staged, +284/-160 líneas) y `pom.xml` (versión 3.37.0 → 3.38.0).

## Novedades 3.37.0 (verificado en código)
- feat(chequeraPago): Nuevo módulo ChequeraPago con arquitectura hexagonal completa (12 casos de uso)
  - Modelo de dominio: `ChequeraPago` con 22 campos y método `getCuotaKey()`
  - 12 puertos de entrada: CRUD + búsquedas por chequera, cuota, tipo pago/fecha, pendientes factura, ID MercadoPago, orden, pagado
  - Puerto de salida: `ChequeraPagoRepository` con consultas complejas
  - Controlador REST: `ChequeraPagoController` con 8 endpoints bajo `/chequeraPago/`
  - Migración desde `ChequeraPago.kt` (Kotlin legacy) a dominio hexagonal Java puro
- feat(chequeraTotal): Nuevo módulo ChequeraTotal con arquitectura hexagonal completa (5 casos de uso)
  - Modelo de dominio: `ChequeraTotal` con campos `chequeraTotalId`, `facultadId`, `tipoChequeraId`, `chequeraSerieId`, `productoId`, `total`, `pagado`
  - 5 puertos de entrada: Create, Delete, FindAllByChequera, FindByUnique, Update
  - Puerto de salida: `ChequeraTotalRepository` con 6 métodos de CRUD
  - Controlador REST: `ChequeraTotalController` con 1 endpoint de lectura bajo `/chequeratotal/`
  - Migración desde `ChequeraTotal.java` (legacy) a dominio hexagonal
- feat(politicaArancelaria): Nuevo módulo PoliticaArancelaria bajo `/api/tesoreria/core/politicaArancelaria`
  - Puerto de entrada: `RecalculateCuotaByUniqueIndexUseCase` que delega a `ChequeraCuotaService.findByUnique()`
  - Controlador REST: `PoliticaArancelariaController` con endpoint `GET /recalculate/cuota/...`
  - Dependencia cross-module con `chequeraCuota`
- refactor(lectivo): `Lectivo` y `LectivoEntity` implementan `Jsonifyable` en lugar de definir `jsonify()` manualmente
- refactor(lectivo): DTOs migrados de `@Data` a `@Getter`/`@Setter`
- refactor(facade): `ChequeraService`, `PagoService` y `SpoterService` actualizados para usar servicios hexagonales `ChequeraPagoService` y `ChequeraTotalService`
- refactor(chequeraCuota): Eliminación de `ChequeraPago` duplicado, `ChequeraCuotaMapper` legacy, renombrado de caso de uso
- refactor(facturacionElectronica): `FacturacionElectronica.chequeraPago` cambiado a tipo `ChequeraPagoEntity` (hexagonal)
- refactor(docs): Namespace restructuring en 20 diagramas Mermaid existentes
- feat(docs): Nuevos diagramas Mermaid `hexagonal-chequeraPago.mmd`, `hexagonal-chequeraTotal.mmd`, `hexagonal-politicaArancelaria.mmd`

> Basado en análisis profundo de `git diff HEAD` (104 archivos staged, +1927/-859 líneas, incluyendo migración de ChequeraPago y ChequeraTotal a hexagonal + nuevo módulo PoliticaArancelaria) y `pom.xml` (versión 3.36.0 → 3.37.0).

## Novedades 3.36.0 (verificado en código)
- feat(compras/proveedorMovimiento): Nuevo módulo ProveedorMovimiento con arquitectura hexagonal completa (13 casos de uso)
  - Modelo de dominio: `ProveedorMovimiento` con campos para gestión de movimientos de proveedores
  - 13 puertos de entrada: CRUD + búsquedas por orden de pago, proveedor, comprobante, rango de fechas, eliminables, asignables, disponibles, ajuste de costos
  - Puerto de salida: `ProveedorMovimientoRepository` con consultas complejas
  - Controlador REST: `ProveedorMovimientoController` con 6 endpoints bajo `/proveedorMovimiento/`
- feat(compras/comprobante): Nuevo módulo Comprobante con arquitectura hexagonal completa (6 casos de uso)
  - Modelo de dominio: `Comprobante` con campos para tipos de comprobantes (AFIP, punto de venta, letra)
  - 6 puertos de entrada: Consultas por ID, tipo de transacción, orden de pago, y combinaciones
  - Controlador REST: `ComprobanteController` con rutas `/comprobante` y `/api/tesoreria/core/comprobante`
- feat(contable/cuentaMovimiento): Nuevo módulo CuentaMovimiento con arquitectura hexagonal completa (11 casos de uso)
  - Modelo de dominio: `CuentaMovimiento` con campos para asientos contables, implementa `Jsonifyable`
  - 11 puertos de entrada: CRUD + búsquedas por cuenta, asiento, rango de fechas, apertura; eliminación por ID, lista y asiento
  - Controlador REST: `CuentaMovimientoController` con rutas `/cuentaMovimiento` y `/api/tesoreria/core/cuentaMovimiento`
- feat(track): Nuevo módulo Track con arquitectura hexagonal completa (4 casos de uso)
  - Modelo de dominio: `Track` con campos `trackId`, `descripcion`, implementa `Jsonifyable`
  - 4 puertos de entrada: Create, GetAll, GetById, Delete
  - Controlador REST: `TrackController` con rutas `/track` y `/api/tesoreria/core/track`, endpoints CRUD completos
- refactor(compras): Reorganización de módulos Articulo, Proveedor y FacturaPendiente bajo `hexagonal/compras/`
- refactor(chequera): Reorganización del módulo Baja bajo `hexagonal/chequera/baja/`
- refactor(legacy): Eliminación de controllers, servicios, repositorios y modelos Kotlin legacy
- feat(docs): Nuevos diagramas Mermaid para módulos ProveedorMovimiento, Comprobante, CuentaMovimiento y Track; actualizados diagramas de baja, articulo, proveedor y facturaPendiente

> Basado en análisis profundo de `git diff HEAD` (200+ archivos staged, incluyendo reestructuración masiva + 4 nuevos módulos hexagonales) y `pom.xml` (versión 3.35.0 → 3.36.0).

## Novedades 3.35.0 (verificado en código)
- refactor(usuario): Migración completa del módulo Usuario a arquitectura hexagonal
  - Modelo de dominio: `Usuario` con campos `userId`, `login`, `password`, `nombre`, `geograficaId`, `imprimeChequera`, `numeroOpManual`, `habilitaOpEliminacion`, `eliminaChequera`, `modificaChequera`, `lastLog`, `googleMail`, `activo`
  - Puertos de entrada (6): `CreateUsuarioUseCase`, `FindUsuarioByGoogleMailUseCase`, `FindUsuarioByLoginUseCase`, `FindUsuarioByPasswordUseCase`, `UpdateLastLogUseCase`, `UpdateUsuarioUseCase`
  - Puerto de salida: `UsuarioRepository` con métodos CRUD y búsquedas
  - Casos de uso: Implementaciones completas en `application/usecases/`
  - Servicio de aplicación: `UsuarioService` con delegación a casos de uso
  - Adaptador JPA: `JpaUsuarioRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `UsuarioEntity` reemplazando `Usuario.kt` (Kotlin legacy)
  - Repositorio JPA: `JpaUsuarioRepository` con consultas específicas
  - Mapper: `UsuarioMapper` para conversión entidad ↔ dominio
  - Controlador REST: `UsuarioController` con endpoints CRUD
  - DTOs: `UsuarioRequest`, `UsuarioResponse`
  - DTO Mapper: `UsuarioDtoMapper` para conversión DTO ↔ dominio
  - Excepción: `UsuarioException` movida a `hexagonal/usuario/application/exception/`
- refactor(usuario): Eliminación de `Usuario.kt` (Kotlin legacy) del paquete `core/kotlin/model/`
- refactor(usuario): Eliminación de `UsuarioRepository.java` (legacy) del paquete `core/repository/`
- refactor(usuario): Eliminación de `UsuarioService.java` (legacy) del paquete `core/service/`
- refactor(usuario): Eliminación de `UsuarioController.java` (legacy) del paquete `core/controller/`
- refactor(auth): Actualización de `UsuarioAuthMapper` para usar `UsuarioEntity` en lugar de `Usuario` legacy
- refactor(auth): Actualización de `JpaUsuarioAuthRepositoryAdapter` para usar `JpaUsuarioRepository` en lugar de `UsuarioRepository` legacy
- refactor(model): Actualización de `UsuarioChequeraFacultad` para usar `UsuarioEntity` en lugar de `Usuario` legacy
- feat(docs): Nuevo diagrama Mermaid `hexagonal-usuario.mmd` para el módulo Usuario

> Basado en análisis profundo de `git diff HEAD` (30 archivos staged, +1000/-200 líneas, incluyendo migración completa del módulo Usuario a hexagonal) y `pom.xml` (versión 3.34.0 → 3.35.0).

## Novedades 3.34.0 (verificado en código)
- feat(chequeraCuota): Enriquecimiento del modelo de dominio `ChequeraCuota` con asociaciones a `Facultad` y `TipoChequera`
  - Nuevos campos `Facultad facultad` y `TipoChequera tipoChequera` en dominio, mapper, DTOs y respuesta REST
  - `ChequeraCuotaMapper` inyecta `FacultadMapper` y `TipoChequeraMapper` para mapeo completo
  - Actualización de constructores en `PreuniversitarioChequeraService` y `SpoterService`
- feat(facultad): Nuevo campo `guaraniResponsableAcademica` (Integer) en Facultad (domain, entity, mapper, DTOs)
- feat(util): Nueva interfaz `Jsonifyable` con método `jsonify()` por defecto — `ChequeraCuota` la implementa en lugar de definir `jsonify()` manualmente
- refactor(chequeraCuota): Añadido `@Slf4j` y logging debug en `GetChequeraCuotaByUniqueUseCaseImpl`
- fix(mappers): Null-safety en 16 mappers de persistencia hexagonal — previene `NullPointerException` en campos opcionales

> Basado en análisis profundo de `git diff HEAD` (16 archivos staged, +200/-161 líneas) y `pom.xml` (versión 3.33.0 → 3.34.0).

## Novedades 3.33.1 (verificado en código)
- fix(docs): Corregida sintaxis de genéricos Mermaid en 20 diagramas
  - Cambio de `~` (tilde) a `<>` (angle brackets) en todos los diagramas `hexagonal-*.mmd` y `sequence-consulta-articulos.mmd`
  - Sincroniza con sintaxis estándar Mermaid v10+, mejorando compatibilidad con parsers

> Basado en `git diff HEAD` (20 archivos modificados, +431/-431 líneas, cambios puramente sintácticos en documentación).

## Novedades 3.33.0 (verificado en código)
- feat(documento): Nuevo módulo Documento con arquitectura hexagonal completa
  - Modelo de dominio: `Documento` con campos `documentoId`, `nombre`
  - 2 puertos de entrada (`GetAllDocumentosUseCase`, `GetDocumentoByIdUseCase`), 2 casos de uso
  - Servicio de aplicación: `DocumentoService` con métodos `findAll()`, `findByDocumentoId(Integer)`
  - Adaptador JPA: `JpaDocumentoRepositoryAdapter` con `DocumentoMapper`, `DocumentoEntity` (Java reemplazando `Documento.kt`)
  - Controlador REST: `DocumentoController` con endpoints `GET /documento/` y `GET /documento/{documentoId}`
  - DTOs: `DocumentoRequest`, `DocumentoResponse`, `DocumentoDtoMapper`
  - `DocumentoException` movida a `hexagonal/documento/application/exception/`
- feat(preuniversitario): Integración de `DocumentoService` en `PreuniversitarioChequeraService.create()`
  - Validación de `tipoDocumento` > 1 contra BD con fallback a documentoId=1
  - Previene errores de integridad referencial al crear chequeras desde Guarani
- feat(docs): Nuevo diagrama Mermaid `hexagonal-documento.mmd` para el módulo Documento

> Basado en análisis profundo de `git diff HEAD` (21 archivos, +337/-120 líneas, incluyendo migración completa del módulo Documento a hexagonal + integración en PreuniversitarioChequeraService) y `pom.xml` (versión 3.32.2 → 3.33.0).

## Novedades 3.32.2 (verificado en código)
- fix(domicilio): Null-safety en campos email de `JpaDomicilioRepositoryAdapter` y `DomicilioMapper`
  - `JpaDomicilioRepositoryAdapter.update()`: Asigna string vacío si `emailPersonal` o `emailInstitucional` es null al actualizar domicilio en BD
  - `DomicilioMapper.toEntity()`: Asigna string vacío si `emailPersonal` o `emailInstitucional` es null en el mapeo dominio → entidad
  - Previene `NullPointerException` en persistencia de domicilios cuando los campos email no están informados

> Basado en análisis profundo de `git diff HEAD` (2 archivos modificados, +8/-8 líneas) y `pom.xml` (versión 3.32.1 → 3.32.2).

## Novedades 3.32.1 (verificado en código)
- fix(preuniversitario): Null-safety en creación de persona/domicilio y envío de chequera preuniversitaria
  - `PreuniversitarioChequeraService.create()`: Maneja `PersonaException` y `DomicilioException` con retorno null controlado
  - `CreatePreuniversitarioUseCaseImpl.createPreuniversitario()`: Valida null de `chequeraSerie` antes de enviar mail
  - Evita `NullPointerException` y envío de chequeras incompletas cuando falla la creación de persona o domicilio desde Guarani

> Basado en análisis profundo de `git diff HEAD` (2 archivos modificados, +17/-0 líneas) y `pom.xml` (versión 3.32.0 → 3.32.1).

## Novedades 3.32.0 (verificado en código)
- feat(mercadopagoContext): Integración de pago MercadoPago con ReservaVacante en `ProcessPaymentEventUseCaseImpl`
  - Nuevo flujo condicional: si `MercadoPagoContext.reservaVacanteId != null`, procesa el pago como reserva de vacante en lugar de cuota
  - Al recibir evento `approved`, actualiza el estado de `ReservaVacante` a `"pagado"` vía `ReservaVacanteService`
  - Nuevo campo `reservaVacanteId` (UUID) en `PaymentProcessedEvent`
- feat(reservaVacante): Nuevo `UpdateReservaVacanteUseCase` con implementación y adaptador JPA
  - Nuevo método `updateReservaVacante(ReservaVacante, UUID)` en `ReservaVacanteService`
  - Nuevo método `update(ReservaVacante, UUID)` en `ReservaVacanteRepository` y `JpaReservaVacanteRepositoryAdapter`
- feat(docs): Diagrama hexagonal-reservaVacante.mmd actualizado con UpdateReservaVacanteUseCase

> Basado en análisis profundo de `git diff HEAD` (8 archivos modificados, +68/-15 líneas) y `pom.xml` (versión 3.31.0 → 3.32.0).

## Novedades 3.31.0 (verificado en código)
- feat(lectivoTotalImputacion): Nuevo caso de uso `FindAllByLectivoUseCase` y endpoint `GET /lectivo/{lectivoId}`
  - Nuevo puerto de entrada: `FindAllByLectivoUseCase` con método `findAllByLectivo(Integer lectivoId)`
  - Nueva implementación: `FindAllByLectivoUseCaseImpl`
  - Nuevo método `findAllByLectivo(Integer)` en `LectivoTotalImputacionRepository` y adaptador JPA
  - Nuevo endpoint: `GET /api/tesoreria/core/lectivototalimputacion/lectivo/{lectivoId}`
  - Nueva ruta base alternativa: `/api/tesoreria/core/lectivototalimputacion`
- feat(lectivoTotalImputacion): Enriquecimiento del modelo de dominio con asociaciones a `Facultad`, `Lectivo`, `TipoChequera`, `Producto` y `Cuenta`
  - `LectivoTotalImputacion` domain model: nuevos campos `facultad`, `lectivo`, `tipoChequera`, `producto`, `cuenta`
  - `LectivoTotalImputacionEntity`: corregido `@JoinColumn` de `columnDefinition` a `name` (fix JPA)
  - `LectivoTotalImputacionResponse`: expone objetos completos de asociaciones en respuestas REST
  - `LectivoTotalImputacionMapper`: inyecta 5 mappers de dominio para mapear asociaciones
- feat(lectivoTotalImputacion): Refactorización de controller con `ResponseEntity.ok()` y `ResponseEntity.notFound().build()`
- feat(tests): 8 nuevos tests unitarios y de integración para el módulo LectivoTotalImputacion (controller, service, use cases, mappers, repository)
- feat(deps): Nuevas dependencias `spring-boot-starter-data-jpa-test` y `spring-boot-starter-webmvc-test`
- fix(test): Agregado parámetro H2 `INIT=SET REFERENTIAL_INTEGRITY FALSE` en test application.yml

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados, +1071/-14 líneas) y `pom.xml` (versión 3.30.0 → 3.31.0).

## Novedades 3.30.0 (verificado en código)
- feat(producto): Nuevo módulo Producto con arquitectura hexagonal completa
  - Modelo de dominio con campos `productoId`, `nombre`
  - 2 puertos de entrada, casos de uso, DTOs, mapper, controlador REST, excepción
  - `ProductoEntity.java` reemplaza `Producto.kt` (Kotlin legacy)
- feat(tipoChequera): Nuevo módulo TipoChequera con arquitectura hexagonal completa
  - Modelo de dominio con 11 campos, relaciones a `Geografica` y `ClaseChequera`
  - 11 puertos de entrada, 11 casos de uso individuales, DTOs, mapper, controlador REST (9 endpoints), excepción
  - `TipoChequeraEntity.java` reemplaza `TipoChequera.kt` (Kotlin legacy)
- feat(lectivo): Nuevo módulo Lectivo con arquitectura hexagonal completa
  - Modelo de dominio con campos `lectivoId`, `nombre`, `fechaInicio`, `fechaFinal`
  - 8 puertos de entrada, 8 casos de uso individuales, DTOs, mapper, controlador REST (7 endpoints), excepción
  - `LectivoEntity.java` reemplaza `Lectivo.kt` (Kotlin legacy)
- feat(lectivoTotalImputacion): Nuevo módulo LectivoTotalImputacion con arquitectura hexagonal completa
  - Modelo de dominio con campos `lectivoTotalImputacionId`, `facultadId`, `lectivoId`, `tipoChequeraId`, `productoId`, `cuenta`
  - 4 puertos de entrada, 4 casos de uso, DTOs, mapper, controlador REST, excepción
- feat(claseChequera): Nuevo módulo ClaseChequera con arquitectura hexagonal completa
  - Modelo de dominio con campos `claseChequeraId`, `nombre`, `preuniversitario`, `grado`, `posgrado`, `curso`, `secundario`, `titulo`
  - 4 puertos de entrada, casos de uso, DTOs, mapper, controlador REST, excepción
  - `ClaseChequeraEntity.java` reemplaza `ClaseChequeraEntity.kt` (Kotlin legacy)
- feat(chequeraCuota): Migración completa a casos de uso individuales (20+)
  - Modelo de dominio enriquecido: nuevos campos `importe1Original`, `importe2/2Original`, `importe3/3Original`, `vencimiento2/3`, `codigoBarras`, `i2Of5`, `manual`, `tramoId`, `arancelTipoId`, relaciones a `Producto` y `ChequeraSerie`
  - Nuevos DTOs (`ChequeraCuotaRequest`/`Response`), `ChequeraCuotaDtoMapper`, `ChequeraCuotaCalculoDeudaService`
  - `ChequeraCuotaDeudaService` simplificado con `@RequiredArgsConstructor` y `.peek()`
- feat(chequeraSerie): Migración completa a casos de uso individuales (15+)
  - Modelo de dominio enriquecido: +20 nuevos campos, relaciones a `ArancelTipo`, `Domicilio`, `Facultad`, `Geografica`, `Lectivo`, `Persona`, `TipoChequera`
  - Nuevos DTOs (`ChequeraSerieRequest`/`Response`), `ChequeraSerieDtoMapper`, `JpaChequeraSerieRepositoryAdapter`
- feat(arancelTipo): Migración a 9 casos de uso individuales con repositorio y mapper propios
- feat(arancelPorcentaje): Migración a 4 casos de uso individuales con repositorio y mapper propios
- refactor(package): Reorganización masiva de módulos bajo `hexagonal/chequera/`
  - `arancelTipo`, `arancelPorcentaje`, `chequeraCuota`, `chequeraSerie`, `producto`, `tipoChequera` movidos a `hexagonal/chequera/`
  - Actualización de imports en +130 archivos del proyecto
- refactor(cuenta): Reorganización de paquete a `hexagonal/contable/cuenta/`
- refactor(exception): 9 excepciones movidas de `core/exception/` a sus respectivos módulos hexagonales
- chore(docs): Nuevos diagramas Mermaid para Producto, TipoChequera, Lectivo; actualizados ChequeraSerie (de flowchart a classDiagram), ChequeraCuota (a classDiagram), ClaseChequera

> Basado en análisis profundo de `git diff HEAD` (146 archivos, +4387/-1264 líneas, incluyendo migración masiva de 6 módulos a hexagonal + paquete contable) y `pom.xml` (versión 3.29.0 → 3.30.0).

## Novedades 3.29.0 (verificado en código)
- feat(Tool): Actualizados mappings de propuesta a facultadId en `Tool.convert2Tesium()`
  - Nuevas propuestas: 70, 111, 125-134 → facultad 1; 72, 121-123 → facultad 2; 74, 120 → facultad 3; 75, 112-118 → facultad 4; 68, 124 → facultad 5
  - Eliminados mappings legacy (109, 73, 108) reemplazados por valores actualizados
  - Amplía cobertura de carreras/sedes del sistema Guarani
- refactor(arancelTipo): Migración completa del módulo ArancelTipo a arquitectura hexagonal
  - Eliminación de `ArancelTipo.kt` (Kotlin) y creación de `ArancelTipoEntity.java` con Lombok
  - `ArancelTipoService` y `ArancelTipoController` movidos a `hexagonal/arancelTipo/`
  - Actualización de referencias en todas las vistas JPA y servicios facade
- refactor(arancelPorcentaje): Migración completa del módulo ArancelPorcentaje a arquitectura hexagonal
  - Eliminación de `ArancelPorcentaje.kt` (Kotlin) y creación de `ArancelPorcentajeEntity.java`
  - Nuevos `ArancelPorcentajeService` y `ArancelPorcentajeController` en `hexagonal/arancelPorcentaje/`
- refactor(asiento): Migración completa del módulo Asiento a arquitectura hexagonal
  - Eliminación de `Asiento.kt` (Kotlin) y creación de `AsientoEntity.java` con Lombok Builder
  - `AsientoService` y `AsientoException` movidos a `hexagonal/asiento/`
  - `ContabilidadService` y `CostoService` actualizados para usar `AsientoEntity`
- refactor(alumnoGuarani): Simplificación del mapper DTO y extracción de lógica de chequera
  - Eliminación de 7 DTOs de infraestructura (ContactoGuarani, DocumentoPrincipalGuarani, PersonaGuarani, PropuestaGuarani, PropuestaTipoGuarani, TipoDocumentoGuarani, UbicacionGuarani)
  - `AlumnoGuaraniDtoMapper` simplificado: eliminados 7 métodos de mapeo recursivo
  - Nueva lógica de creación de chequera extraída a `PreuniversitarioChequeraService`
- refactor(baja): `BajaException` migrado a `hexagonal/baja/application/exception/`
- refactor(chequeraSerie): `ChequeraSerieMapper` movido a `hexagonal/chequeraSerie/infrastructure/web/mapper/`
- chore(docs): Nuevos diagramas Mermaid para módulos ArancelTipo, ArancelPorcentaje y Asiento

> Basado en análisis profundo de `git diff HEAD` (64 archivos modificados, +693/-1075 líneas, incluyendo cambios locales en Tool.java) y `pom.xml` (versión 3.28.0 → 3.29.0).

## Novedades 3.28.0 (verificado en código)
- feat(guarani): Nuevo módulo `alumnoGuarani` con arquitectura hexagonal para integración con sistema Guarani
  - Modelo de dominio `AlumnoGuarani` (aggregate root) con sub-modelos `PersonaGuarani`, `DocumentoPrincipalGuarani`, `TipoDocumentoGuarani`, `ContactoGuarani`, `UbicacionGuarani`, `PropuestaGuarani`, `PropuestaTipoGuarani`
  - Puertos de entrada: `CreatePreuniversitarioUseCase`, `CheckAllPreuniversitarioWithoutChequeraUseCase`
  - Casos de uso: `CreatePreuniversitarioUseCaseImpl` (orquestación completa de creación de chequera preuniversitaria) y `CheckAllPreuniversitarioWithoutChequeraUseCaseImpl` (detección de preuniversitarios sin chequera)
  - Servicio de aplicación: `AlumnoGuaraniService`
  - Controlador REST: `AlumnoGuaraniController` con endpoints `POST /api/tesoreria/core/guarani/alumno/create/preuniversitario` y `POST /api/tesoreria/core/guarani/alumno/desmarcar/enviadas`
  - DTOs: `AlumnoGuaraniRequest`, `AlumnoDeteccionRequest`, `ChequeraContextFromGuaraniInternal`, y DTOs para cada sub-modelo
  - Mapper: `AlumnoGuaraniDtoMapper` con mapeo recursivo del árbol anidado DTO ↔ dominio
- feat(guarani): Nueva utilidad `Tool.convert2Tesium()` para conversión de ubicación/propuesta Guarani a contexto Tesoreria
- chore(deps): Eliminada dependencia explícita `spring-boot-starter-log4j2` (gestionada por Spring Boot parent POM)

> Basado en análisis profundo de `git diff HEAD` (29 archivos modificados, +1205/-12 líneas) y `pom.xml` (versión 3.27.0 → 3.28.0).

## Novedades 3.27.0 (verificado en código)
- feat(reservaVacante): Extendido `vencimiento` de 2 a 60 días en la creación de reserva de vacante
  - Cambio de `plusDays(2)` a `plusDays(60)` en `CreateReservaVacanteUseCaseImpl.createReservaVacante()`
  - Proporciona ventana más flexible para completar el pago de la reserva
- feat(reservaVacante): `importe` personalizable desde `ReservaVacanteRequest`
  - Nuevo campo `BigDecimal importe` en `ReservaVacanteRequest`
  - Si se especifica y es distinto de cero, sobreescribe `Campanha.getValorReserva()`
- feat(reservaVacante): Nuevo método `jsonify()` en `ReservaVacanteRequest` para logging estructurado
- feat(reservaVacante): Nuevo log debug con `jsonify()` de `ReservaVacanteRequest` en `CreateReservaVacanteUseCaseImpl`
- fix(domicilio): Agregado `@JsonFormat` faltante en campo `fecha` de `Domicilio`, `DomicilioRequest` y `DomicilioResponse`
  - Completa la corrección ISO 8601 iniciada en v3.26.1 para el módulo Domicilio

> Basado en análisis profundo de `git diff HEAD --cached` (5 archivos modificados) y `pom.xml` (versión 3.26.1 → 3.27.0).

## Novedades 3.26.1 (verificado en código)
- fix(core): Extendida corrección ISO 8601 a todas las entidades del proyecto (61 archivos)
  - Migración del patrón `@JsonFormat` de `yyyy-MM-dd'T'HH:mm:ssZ` a `yyyy-MM-dd'T'HH:mm:ssXX` en todos los modelos Java y Kotlin
  - Afecta modelos de dominio, entidades JPA, DTOs y vistas en `model/`, `kotlin/model/` y `hexagonal/*/entity/`
  - Garantiza cumplimiento completo con el estándar ISO 8601 en todas las fechas serializadas

> Basado en análisis profundo de `git diff HEAD` (61 archivos modificados, +115/-115 líneas) y `pom.xml` (versión 3.26.0 → 3.26.1).

## Novedades 3.26.0 (verificado en código)
- feat(mercadopagoContext): Nuevo `FindActiveByReservaVacanteIdUseCase` para recuperar contexto MP activo por `reservaVacanteId` (UUID)
  - Nuevo puerto de entrada, implementación, método en repositorio (`findByReservaVacanteIdAndActivo`), método JPA y adaptador
- feat(mercadopagoContext): Nuevo `PreferenceVacanteClient` Feign client para crear preferencias de vacantes
- feat(mercadopagoContext): `MercadoPagoContext` domain model con `jsonify()` para logging estructurado
- feat(reservaVacante): Integración con MercadoPago al crear reserva — nuevo `MercadoPagoCoreService.createContextVacante()` y `PreferenceVacanteClient.createPreference()`
- feat(reservaVacante): Nuevo campo `Campanha campanha` en modelo de dominio `ReservaVacante`
- feat(reservaVacante): `ReservaVacanteResponse` mejorado con `initPoint` desde MercadoPagoContext
  - `ReservaVacanteDtoMapper` ahora inyecta `MercadoPagoContextService` para resolver `initPoint`
- feat(reservaVacante): `FindReservaVacanteUseCaseImpl` ahora enriquece con `Campanha` y logging con `jsonify()`
- fix(chequeraCuota/mercadopagoContext): Formato de fecha ISO 8601 unificado a `yyyy-MM-dd'T'HH:mm:ssXX` en todas las entidades
- fix(mercadopagoContext): Corregida lógica `isCuotaAvailable()` — comparación `!=` → `==` para campos `pagado`, `baja`, `compensada`
- refactor(mercadopagoContext): Renombrado `makeContext()` → `makeContextCuota()` en `MercadoPagoCoreService` y `MercadoPagoCoreController`
- refactor(reservaVacante): Eliminado `@Transactional` de `CreateReservaVacanteUseCaseImpl`
- refactor(dto): `UMPreferenceMPDto` extendido con campo `ReservaVacante`

> Basado en análisis profundo de `git diff HEAD` (21 archivos modificados, +166/-19 líneas) y `pom.xml` (versión 3.25.0 → 3.26.0).

## Novedades 3.25.0 (verificado en código)
- feat(mercadoPagoContext): New `reservaVacanteId` (UUID) field and domain associations `ChequeraCuota`/`ReservaVacante`
  - `MercadoPagoContext.domain.model.MercadoPagoContext`: nuevos campos `reservaVacanteId`, `chequeraCuota`, `reservaVacante`
  - `MercadoPagoContextEntity`: nuevo campo `reservaVacanteId`, relaciones `@OneToOne` a `ChequeraCuotaEntity` y `ReservaVacanteEntity`
  - `MercadoPagoContextMapper`: mapeo bidireccional con inyección de `ChequeraCuotaMapper` y `ReservaVacanteMapper`
  - `MercadoPagoContextRequest`/`MercadoPagoContextResponse`: exponen `reservaVacanteId` en API REST
  - `MercadoPagoContextDtoMapper`: mapeo de `reservaVacanteId` y asociaciones de dominio
- feat(chequeraCuota): Nueva entidad Java `ChequeraCuotaEntity` reemplazando `ChequeraCuotaEntity.kt` (Kotlin)
  - `ChequeraCuotaEntity.java` en `hexagonal/chequeraCuota/infrastructure/persistence/entity/` con Lombok
  - Extiende `Auditable`, mapeo a tabla `chequera_cuota` con `@UniqueConstraint`
  - `ChequeraCuotaMapper.toDomain()` actualizado para usar nueva entidad Java
  - +20 archivos actualizados (servicios, controladores, facade)
- refactor(chequeraSerie): Modelo `ChequeraSerie` movido al módulo hexagonal correcto
  - De `hexagonal/chequeraCuota/domain/model/` a `hexagonal/chequeraSerie/domain/model/`
  - Imports actualizados en `ChequeraCuotaService`, `CalculateDeudaUseCase`, `GetDeudaPersonaUseCaseImpl`, `ChequeraSerieMapper`

> Basado en análisis profundo de `git diff HEAD` (30+ archivos modificados, +410/-230 líneas) y `pom.xml` (versión 3.24.0 → 3.25.0).

## Novedades 3.24.0 (verificado en código)
- feat(reservaVacante): Nuevo `FindReservaVacanteUseCase` con endpoint `GET /{reservaVacanteId}`
  - Nuevo puerto de entrada: `FindReservaVacanteUseCase` con método `findByReservaVacanteId(UUID)`
  - Nuevo caso de uso: `FindReservaVacanteUseCaseImpl` que busca reserva + persona + domicilio
  - Nuevo endpoint en `ReservaVacanteController`: `GET /api/tesoreria/core/umhub/reservaVacante/{reservaVacanteId}`
  - Integración con `PersonaService` y `DomicilioService` para enriquecer respuesta
- feat(reservaVacante): Nuevos campos `importe` (BigDecimal) y `vencimiento` (OffsetDateTime)
  - `CreateReservaVacanteUseCaseImpl`: importe desde `Campanha.getValorReserva()`, vencimiento a 2 días UTC-3
- feat(campanha): Nuevo campo `valorReserva` (BigDecimal) en modelo `Campanha`, entity, mappers, DTOs y adaptador
- refactor(domicilio): Migración completa del módulo Domicilio a arquitectura hexagonal
  - `Domicilio` (Kotlin) → `Domicilio` (dominio Java) en `hexagonal/domicilio/domain/model/`
  - `DomicilioService` movido a `hexagonal/domicilio/application/service/`
  - Método `add(Domicilio, boolean)` → `create(Domicilio)`, `update(Domicilio, Long, boolean)` → `update(Long, Domicilio)`
  - `DomicilioException` movido a `hexagonal/domicilio/application/exception/`
- refactor(persona): Migración del modelo `PersonaEntity` → `Persona` domain model en `hexagonal/persona/`
  - Método `add(PersonaEntity)` → `create(Persona)` con retorno `Persona`
- refactor(mercadoPagoContext): Migración completa a hexagonal
  - `MercadoPagoContext` → `hexagonal/mercadoPagoContext/domain/model/`
  - `MercadoPagoContextService` → `hexagonal/mercadoPagoContext/application/service/`
- refactor(chequeraCuota): Renombrado `ChequeraCuota` (Kotlin) → `ChequeraCuotaEntity` (Java) en servicios
- refactor(logging): Eliminación de 15 métodos privados `log*()` con Jackson manual → `jsonify()` inline
- refactor(payPerTic): `PayPerTicFileService` migrado a `@RequiredArgsConstructor`, tipos `Integer` → `int`
- refactor(sincronize): Actualizado para nueva API `DomicilioService` hexagonal (create/update)
- chore(banner): Simplificación de banner.txt
- chore(deps): Actualización de dependencias: log4j2 4.0.5→4.1.0, mysql-connector 9.6.0→9.7.0, guava 33.5.0→33.6.0-jre, springdoc 3.0.2→3.0.3, openpdf 3.0.3→3.0.5, nueva propiedad lombok.version 1.18.38

> Basado en análisis profundo de `git diff HEAD` (20+ archivos modificados), `pom.xml` y cambios locales no commiteados.

## Novedades 3.23.0 (verificado en código)
- feat(campanha): Nuevo campo `created` (LocalDateTime) en modelo `Campanha`
  - `Campanha.domain.model.Campanha`: nuevo campo `created` para fecha de creación
  - `CampanhaResponse`: expone el campo `created` en respuestas REST
  - `CampanhaMapper` y `CampanhaDtoMapper`: mapeo bidireccional del campo `created`
- refactor(campanha): Actualización parcial (partial update) en `JpaCampanhaRepositoryAdapter.update()`
  - Cambio de reemplazo completo de entidad a carga de entidad existente con actualización selectiva de campos no nulos

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +20/-8 líneas).

## Novedades 3.22.0 (verificado en código)
- feat(campanha): Nuevo módulo Campanha con arquitectura hexagonal completa bajo `hexagonal/umhub/campanha/`
  - Modelo de dominio: `Campanha` con campos `campanhaId` (UUID), `nombre`, `activa`
  - Puertos de entrada: `CreateCampanhaUseCase`, `GetCampanhaByIdUseCase`, `GetAllCampanhasUseCase`, `UpdateCampanhaUseCase`, `DeleteCampanhaUseCase`
  - Puerto de salida: `CampanhaRepository` con métodos CRUD
  - Casos de uso: `CreateCampanhaUseCaseImpl`, `GetCampanhaByIdUseCaseImpl`, `GetAllCampanhasUseCaseImpl`, `UpdateCampanhaUseCaseImpl`, `DeleteCampanhaUseCaseImpl`
  - Servicio de aplicación: `CampanhaService` con delegación a casos de uso
  - Adaptador JPA: `JpaCampanhaRepositoryAdapter` con `CampanhaMapper` para conversión dominio ↔ entidad
  - Entidad JPA: `CampanhaEntity` extends `Auditable`, mapeo a tabla `campanha`
  - Repositorio JPA: `JpaCampanhaRepository` con consultas por ID
  - Controlador REST: `CampanhaController` con endpoints CRUD bajo `GET/POST/PUT/DELETE /api/tesoreria/umhub/campanha/`
  - DTOs: `CampanhaRequest`, `CampanhaResponse`
  - DTO Mapper: `CampanhaDtoMapper` para conversión DTO ↔ dominio
  - Excepción: `CampanhaException`
- chore(deps): Actualización de Spring Boot 4.0.6 → 4.1.0, Kotlin 2.3.21 → 2.4.0, Spring Cloud 2025.1.0 → 2025.1.2
- refactor(service): Migración de `ChequeraIncompletaService` a inyección por constructor con `@RequiredArgsConstructor`

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados, +488/-6 líneas).

## Novedades 3.21.2 (verificado en código)
- fix(model): Agregada anotación `@Serial` a `serialVersionUID` en clases serializables
  - `PersonaBeneficiario`, `ChequeraIncompleta`, `ChequeraKey`, `ChequeraPreuniv` ahora usan `@Serial` para metadatos de serialización
- refactor(model): Renombrado de campo `personaEntity` a `persona` en clases de modelo para consistencia de nomenclatura
  - `PersonaBeneficiario`, `ChequeraIncompleta`, `ChequeraKey`, `ChequeraPreuniv`: campo `personaEntity` → `persona`
  - `SheetService`: Referencias actualizadas de `getPersonaEntity()` → `getPersona()`
  - Consistencia con la convención de `ContratoEntity` establecida en v3.20.0

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +17/-9 líneas).

## Novedades 3.21.1 (verificado en código)
- fix(tables): Agregadas anotaciones `@Table(name = ...)` faltantes en `BajaEntity` y `ChequeraSerieEntity`
  - `BajaEntity` con `@Table(name = "baja")` para mapeo explícito de tabla
  - `ChequeraSerieEntity` con `@Table(name = "chequera_serie")` para referencia explícita de tabla
- fix(chequeraSerie): Excluidas chequeras "dadas de baja" en consulta `findLastPreuniversitario`
  - Cambio de `findFirstBy...` (resultado único) a `findAllBy...` (lista) con filtro post-query de bajas
  - Nueva dependencia `BajaService` en `ChequeraSerieService` para verificar registros de baja
  - Previene retorno de chequeras con soft-delete como último preuniversitario
- chore(deps): Eliminado versionado explícito de `commons-lang3` (gestionado por Spring Boot parent POM 4.0.6)

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +21/-10 líneas).

## Novedades 3.21.0 (verificado en código)
- refactor(chequeraSerie): Migración completa del módulo ChequeraSerie a arquitectura hexagonal con Java
  - Eliminación de `ChequeraSerie.kt` (Kotlin) y creación de `ChequeraSerieEntity.java` con anotaciones Lombok
  - `ChequeraSerieService` y `ChequeraSerieController` movidos a `hexagonal/chequeraSerie/`
  - Actualización de referencias en +25 archivos (`PersonaService`, `ChequeraCuota`/`ChequeraCuotaReemplazo`/`ChequeraCuotaDeuda` en Kotlin, todos los servicios facade)
  - Eliminación de repositorio legacy `ChequeraSerieRepository.java`
- refactor(baja): Migración completa del módulo Baja a arquitectura hexagonal con Java
  - Eliminación de `Baja.kt` (Kotlin) y creación de `BajaEntity.java` con anotaciones Lombok
  - `BajaService` y `BajaController` movidos a `hexagonal/baja/`
  - Eliminación de repositorio legacy `BajaRepository.java`

> Basado en análisis profundo de `git diff HEAD` (34 archivos modificados, +598/-554 líneas).

## Novedades 3.20.0 (verificado en código)
- refactor(contrato): Renombrado de campo `personaEntity` a `persona` en `ContratoEntity`
  - Campo de relación `@ManyToOne` renombrado para consistencia de nomenclatura
  - `SheetService` actualizado para usar `getPersona()` en generación de hojas de cálculo
- refactor(chequeraSerie): Renombrado de campo `personaEntity` a `persona` en `ChequeraSerieAlta` y `ChequeraSerieAltaFull`
  - Consistencia de nomenclatura con `ContratoEntity`
  - `ChequeraSerieAltaFullService` actualizado con null-safe `Objects.requireNonNull()`

> Basado en análisis profundo de `git diff HEAD` (5 archivos modificados, +8/-7 líneas).

## Novedades 3.19.1 (verificado en código)
- fix(chequeraCuota): Agregado filtro `baja=0` en consulta de cuotas filtradas para excluir registros soft-deleted
  - Nueva query `findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndImporte1GreaterThan` en `ChequeraCuotaRepository`
  - `ChequeraCuotaService` actualizado para usar la nueva query con `baja = (byte) 0`
  - Previene retorno de registros con soft-delete en resultados de cuotas

> Basado en análisis profundo de `git diff HEAD` (2 archivos modificados, +12/-2 líneas).

## Novedades 3.19.0 (verificado en código)
- feat(contrato): Nuevo módulo Contrato con arquitectura hexagonal completa
  - Modelo de dominio: `Contrato` con campos `contratoId`, `personaId`, `documentoId`, `desde`, `facultadId`, `planId`, `materiaId`, `geograficaId`, `cargoMateriaId`, `primerVencimiento`, `cargo`, `montoFijo`, `canonMensual`, `canonMensualSinAjuste`, `hasta`, `canonMensualLetras`, `canonTotal`, `canonTotalLetras`, `meses`, `mesesLetras`, `ajuste`
  - Puertos de entrada (9): `CreateContratoUseCase`, `DeleteContratoUseCase`, `GetAllContratosAjustablesUseCase`, `GetAllContratosByFacultadUseCase`, `GetAllContratosByPersonaUseCase`, `GetAllContratosVigentesUseCase`, `GetContratoByIdUseCase`, `SaveAllContratosUseCase`, `UpdateContratoUseCase`
  - Puerto de salida: `ContratoRepository` con métodos CRUD y búsquedas por facultad, persona, ajustable y vigente
  - Casos de uso: Implementaciones completas en `application/usecases/` con inyección de dependencias
  - Servicio de aplicación: `ContratoService` con delegación a casos de uso y retornos `Optional`
  - Adaptador JPA: `JpaContratoRepositoryAdapter` con mapeo dominio ↔ entidad
  - Repositorio JPA: `JpaContratoRepository` con consultas especializadas
  - Mapper: `ContratoMapper` para conversión entidad ↔ dominio
  - Controlador REST: `ContratoController` con endpoints `GET /contrato/ajustable/{referencia}`, `GET /contrato/vigente/{referencia}`, `GET /contrato/persona/{personaId}/{documentoId}`, `GET /contrato/{contratoId}`, `PUT /contrato/{contratoId}`, `PUT /contrato/`
  - DTOs: `ContratoRequest` y `ContratoResponse`
  - DTO Mapper: `ContratoDtoMapper` para conversión DTO ↔ dominio
- refactor(contrato): Migración completa de módulo Contrato a arquitectura hexagonal
  - Eliminación de `Contrato.java` (modelo legacy) del paquete `core/model/`
  - Eliminación de `ContratoRepository.java` del paquete `core/repository/`
  - Eliminación de `ContratoService.java` del paquete `core/service/`
  - Eliminación de `ContratoController.java` del paquete `core/controller/`
  - Actualización de ~10+ archivos que referenciaban `um.tesoreria.core.model.Contrato` para usar la nueva estructura hexagonal
  - Migración de `ContratoEntity` de `@Data` a `@Getter`/`@Setter`/`@Builder` para consistencia

> Basado en análisis profundo de `git diff HEAD` (39 archivos, +793/-229 líneas).

## Novedades 3.18.0 (verificado en código)
- feat(facultad): Nuevo módulo Facultad con arquitectura hexagonal completa
  - Modelo de dominio: `Facultad` con campos `facultadId`, `nombre`, `codigoempresa`, `server`, `dbadm`, `dsn`, `cuentacontable`, `apiserver`, `apiport`, `fechaAuditoria`, `usuarioAuditoria`
  - Puertos de entrada: `GetAllFacultadesUseCase`, `GetFacultadByIdUseCase`, `GetFacultadesFiltradasUseCase`
  - Puerto de salida: `FacultadRepository` con métodos `findAll()`, `findAllIn(List<Integer>)`, `findById(Integer)`
  - Casos de uso: `GetAllFacultadesUseCaseImpl`, `GetFacultadByIdUseCaseImpl`, `GetFacultadesFiltradasUseCaseImpl`
  - Servicio de aplicación: `FacultadService` con métodos `findAll()`, `findFacultades()`, `findByFacultadId(Integer)`, y métodos legacy `findAllByLectivoId`, `findAllByPersona`, `findAllByDisenho`
  - Adaptador JPA: `JpaFacultadRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `FacultadEntity` con anotaciones Lombok, `@Table(name = "facultad")`, herencia de `Auditable`
  - Repositorio JPA: `JpaFacultadRepository` con `findAllByFacultadIdIn` y `findByFacultadId`
  - Mapper: `FacultadMapper` para conversión entidad → dominio
  - Controlador REST: `FacultadController` con endpoints `GET /facultad/`, `GET /facultad/facultades`, `GET /facultad/{facultadId}`, y legacy `GET /facultad/lectivo/{lectivoId}`, `GET /facultad/bypersona/{personaId}/{documentoId}/{lectivoId}`, `GET /facultad/disenho/{lectivoId}/{geograficaId}`
  - DTO: `FacultadResponse` para respuestas HTTP
  - DTO Mapper: `FacultadDtoMapper` para conversión dominio → DTO
- refactor(facultad): Migración completa de módulo Facultad a arquitectura hexagonal
  - Eliminación de `Facultad.kt` (Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `FacultadRepository.java` del paquete `core/repository/`
  - Eliminación de `FacultadService.java` del paquete `core/service/`
  - Eliminación de `FacultadController.java` del paquete `core/controller/`
  - Actualización de ~20+ archivos que referenciaban `um.tesoreria.core.kotlin.model.Facultad` para usar `FacultadEntity` del nuevo módulo hexagonal

> Basado en análisis profundo de `git diff HEAD` (nuevos archivos creados y eliminaciones de legacy).

## Novedades 3.17.0 (verificado en código)
- feat(dependencia): Nuevo módulo Dependencia con arquitectura hexagonal completa
  - Modelo de dominio: `Dependencia` con campos `dependenciaId`, `nombre`, `acronimo`, `facultadId`, `geograficaId`, `cuentaHonorariosPagar`, y relaciones a `Facultad`, `Geografica`, `Cuenta`
  - Puertos de entrada: `GetAllDependenciasUseCase`, `GetDependenciaByIdUseCase`, `UpdateDependenciaUseCase`
  - Puerto de salida: `DependenciaRepository` con métodos `findAll()`, `findById(Integer)`, `save(Dependencia)`
  - Casos de uso: `GetAllDependenciasUseCaseImpl`, `GetDependenciaByIdUseCaseImpl`, `UpdateDependenciaUseCaseImpl`
  - Servicio de aplicación: `DependenciaService` con métodos `findAll()`, `findByDependenciaId(Integer)`, `update(Integer, Dependencia)`
  - Adaptador JPA: `JpaDependenciaRepositoryAdapter` con mapeo dominio ↔ entidad y relaciones `@OneToOne`
  - Repositorio JPA: `JpaDependenciaRepository` con `findAllByOrderByNombre()` y `findByDependenciaId(Integer)`
  - Controlador REST: `DependenciaController` con endpoints `GET /dependencia/`, `GET /dependencia/{dependenciaId}`, `PUT /dependencia/{dependenciaId}`
  - DTOs: `DependenciaRequest` y `DependenciaResponse`
  - DTO Mapper: `DependenciaDtoMapper` para conversión DTO ↔ dominio
- refactor(dependencia): Migración completa de módulo Dependencia a arquitectura hexagonal
  - Eliminación de `Dependencia.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `DependenciaRepository.java` del paquete `core/repository/`
  - Eliminación de `DependenciaService.java` del paquete `core/service/`
  - Eliminación de `DependenciaController.java` del paquete `core/controller/`

### Fixed
- fix(proveedor): Manejo de campos nulos en creación y actualización de proveedores
  - Validación de `emailInterno` en `CreateProveedorUseCaseImpl.createProveedor()` con asignación a string vacío si es nulo
  - Validación de `emailInterno` en `UpdateProveedorUseCaseImpl.updateProveedor()` con asignación a string vacío si es nulo
  - Validación de `habilitado` en `CreateProveedorUseCaseImpl.createProveedor()` con asignación a `(byte) 0` si es nulo
  - Validación de `habilitado` en `UpdateProveedorUseCaseImpl.updateProveedor()` con asignación a `(byte) 0` si es nulo

### Changed
- refactor(proveedor): Migración de `ProveedorRequest` de `@Data` a `@Getter`/`@Setter`/`@Builder` para consistencia
  - Nuevas anotaciones: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` reemplazando `@Data`

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados, +421/-197 líneas, incluyendo cambios locales no commiteados).

## Novedades 3.16.0 (verificado en código)
- feat(articulo): Enriquecimiento de respuestas con objeto `Cuenta` vía `CuentaService`
  - Nuevo campo `Cuenta cuenta` en `ArticuloSearchResponse` para incluir datos de cuenta asociada
  - Nuevo campo `articuloId` en `ArticuloRequest` para especificar ID en creación
  - Los endpoints `GET /articulo/{id}`, `POST /articulo/search` y `GET /articulo/page` ahora resuelven y adjuntan el objeto `Cuenta` completo
- refactor(articulo): Cambio de `@Data` a `@Getter`/`@Setter` en `Articulo` para mayor control de mutabilidad
- refactor(articulo): Renombrado de campo `cuenta` a `numeroCuenta` en `ArticuloSearch`, `ArticuloKey`, `ArticuloSearchResponse` y mappers
  - Compatibilidad BD asegurada vía `@Column(name = "cuenta")`
- refactor(dto): Uso de importación `OffsetDateTime` en lugar de FQCN en `ArticuloSearch` y `ArticuloSearchResponse`

> Basado en análisis profundo de `git diff HEAD` (8 archivos modificados, +72/-27 líneas).

## Novedades 3.15.0 (verificado en código)
- feat(facturaPendiente): Nuevo módulo FacturaPendiente con arquitectura hexagonal completa
  - Modelo de dominio: `FacturaPendiente` con campos `proveedorMovimientoId`, `razonSocial`, `cuit`, `cbu`, `fechaComprobante`, `fechaVencimiento`, `observaciones`, `comprobante`, `debita`, `prefijo`, `numeroComprobante`, `importeFactura`, `importePagado`
  - Puertos de entrada: `GetFacturasPendientesUseCase` con método `getFacturasPendientes(OffsetDateTime, OffsetDateTime)`
  - Puerto de salida: `FacturaPendienteRepository` con métodos `updateFechaPagoInProveedorPago()`, `findFacturasPendientes(OffsetDateTime, OffsetDateTime)`
  - Caso de uso: `GetFacturasPendientesUseCaseImpl` con lógica de negocio
  - Servicio de aplicación: `FacturaPendienteService` con método `findAllFacturasPendientesBetweenDates`
  - Adaptador JPA: `JpaFacturaPendienteRepositoryAdapter` con mapeo dominio ↔ entidad
  - Entidad JPA: `FacturaPendienteEntity` con anotaciones Lombok e `@Immutable`
  - Repositorio JPA: `JpaFacturaPendienteRepository` con consultas nativas
- feat(sheet): Actualización de `SheetService` para nuevos campos en hojas de cálculo
  - Agregado campo `cbu` en generación de hojas
  - Agregado campo `fechaVencimiento` con formato UTC
  - Agregado campo `observaciones` (concepto de factura)
  - Migración de uso de `jsonify()` a utilitaria `Jsonifier`
- refactor(facturaPendiente): Migración completa a arquitectura hexagonal
  - Eliminación de `FacturaPendiente.kt` (Kotlin) de `core/kotlin/model/view/`
  - Eliminación de `FacturaPendienteService.java` de `core/service/view/`
  - Migración de `FacturaPendienteRepository.kt` a Java en `hexagonal/facturaPendiente/infrastructure/persistence/repository/`
  - Actualización de `SheetService` para usar nuevo servicio hexagonal
- Removed: Eliminación de tests obsoletos `ChequeraCuotaControllerTest.java`, `ProveedorMovimientoControllerTest.java`

> Basado en análisis profundo de `git diff HEAD` (13 archivos modificados, +120/-156 líneas).

## Novedades 3.14.0 (verificado en código)
- feat(ubicacion): Nuevo módulo Ubicacion con arquitectura hexagonal
  - Modelo de dominio: `Ubicacion` con campos `ubicacionId`, `nombre`, `dependenciaId`, `geograficaId`
  - Puertos de entrada: `GetAllUbicacionesUseCase`, `GetUbicacionesBySedeUseCase`
  - Puerto de salida: `UbicacionRepository` con métodos `findAll()`, `findAllByGeograficaId(Integer)`
  - Casos de uso: `GetAllUbicacionesUseCaseImpl`, `GetUbicacionesBySedeUseCaseImpl`
  - Servicio de aplicación: `UbicacionService` con métodos `findAll()`, `findAllBySede(Integer)`
  - Adaptador JPA: `JpaUbicacionRepositoryAdapter` con mapeo a entidad JPA
  - Mapper: `UbicacionMapper` para conversión dominio ↔ entidad
  - Entidad JPA: `UbicacionEntity` con anotaciones Lombok
  - Controlador REST: `UbicacionController` con endpoints GET
  - DTO: `UbicacionResponse` para respuestas HTTP
- feat(ubicacionArticulo): Nuevo módulo UbicacionArticulo con arquitectura hexagonal
  - Modelo de dominio: `UbicacionArticulo` con relaciones a `Ubicacion`, `Articulo`, `Cuenta`
  - Puertos de entrada: `GetAllUbicacionArticulosUseCase`, `GetUbicacionArticuloUseCase`, `GetUbicacionArticulosByArticuloUseCase`, `SaveUbicacionArticuloUseCase`
  - Puerto de salida: `UbicacionArticuloRepository` con métodos CRUD y búsquedas
  - Casos de uso: `GetAllUbicacionArticulosUseCaseImpl`, `GetUbicacionArticuloUseCaseImpl`, `GetUbicacionArticulosByArticuloUseCaseImpl`, `SaveUbicacionArticuloUseCaseImpl`
  - Servicio de aplicación: `UbicacionArticuloService` con métodos `findAll()`, `save()`, `findAllByArticuloId()`, `getByUbicacionAndArticulo()`
  - Adaptador JPA: `JpaUbicacionArticuloRepositoryAdapter` con lógica de upsert
  - Mapper: `UbicacionArticuloMapper` para conversión dominio ↔ entidad
  - Entidad JPA: `UbicacionArticuloEntity` con restricción única `(ubicacionId, articuloId)`
  - Controlador REST: `UbicacionArticuloController` con endpoints CRUD
  - DTOs: `UbicacionArticuloRequest`, `UbicacionArticuloResponse`
- refactor(core): `SheetService` actualizado para usar nuevos modelos de dominio
- refactor(model): `Entrega.kt` actualizado con cambios en modelo
- refactor(model): `FacturaPendiente.kt` actualizado con nuevos campos
- refactor(dto): `AsignacionCostoDto` y `CostoParameterDto` actualizados

> Basado en análisis profundo de `git diff HEAD` (47 archivos modificados, +723/-246 líneas).

## Novedades 3.13.0 (verificado en código)
- feat(articulo): Implementación de paginación y búsqueda en módulo Artículo
  - Nuevo puerto de entrada: `GetPaginatedArticulosUseCase` con método `getPaginated(int page, int size)`
  - Nuevo caso de uso: `GetPaginatedArticulosUseCaseImpl` con implementación de paginación usando `Pageable`
  - Nuevo caso de uso: `SearchArticulosUseCaseImpl` para búsqueda de artículos por criterio
  - Nuevo modelo: `ArticuloSearch` para criterios de búsqueda con campo `search`
  - Nuevos métodos en `ArticuloRepository`: `findPaginated(Pageable)`, `search(String criterio)`
  - Nuevo endpoint en `ArticuloController`: `GET /articulo/page?page=X&size=Y` que retorna `PaginatedResponse<ArticuloResponse>`
  - Nuevo endpoint en `ArticuloController`: `GET /articulo/search?criterio=X` para búsqueda
  - Nuevo DTO: `ArticuloSearchResponse` para respuestas de búsqueda
- feat(proveedor): Mejoras en módulo Proveedor
  - Actualización de `ProveedorSearch` con nuevos campos para búsquedas avanzadas
  - Nuevos endpoints en `ProveedorController` para búsquedas
- feat(articulo): Completitud de migración hexagonal (commit afbeb02)
  - `ArticuloKeyService.java` eliminado (funcionalidad movida a casos de uso específicos)
  - `ArticuloKey.java`, `ArticuloKeyRepository.java`, `ArticuloKeyRepositoryCustom.java` movidos a `persistence/repository/`
  - Mappers actualizados para soportar nuevos modelos

> Basado en análisis profundo de `git diff HEAD` (24 archivos modificados, +160/-45 líneas) y commits afbeb02, 4b9e84e.

## Novedades 3.12.0 (verificado en código)
- feat(articulo): Completitud de migración de módulo Artículo a arquitectura hexagonal
  - Nuevos modelos de dominio: `Articulo` y `ArticuloSearch` con Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Puertos de entrada: `CreateArticuloUseCase`, `DeleteArticuloUseCase`, `GetAllArticulosUseCase`, `GetArticuloByIdUseCase`, `GetNewArticuloUseCase`, `SearchArticulosUseCase`, `UpdateArticuloUseCase`
  - Puerto de salida: `ArticuloRepository` con métodos `create`, `findById`, `findAll`, `update`, `deleteById`, `findLast`
  - Casos de uso: Implementaciones completas en `application/usecases/` (`CreateArticuloUseCaseImpl`, `DeleteArticuloUseCaseImpl`, etc.)
  - Servicio de aplicación: `ArticuloService` refactorizado con inyección de casos de uso y `Optional<Articulo>` en retornos
  - Adaptador JPA: `JpaArticuloRepositoryAdapter` con implementación completa de `ArticuloRepository`
  - Mappers: `ArticuloMapper` (dominio ↔ JPA Entity) y `ArticuloDtoMapper` (dominio ↔ DTO)
  - DTOs: `ArticuloRequest`, `ArticuloResponse`, `ArticuloSearchResponse` para entrada/salida
  - Controlador REST: `ArticuloController` migrado con endpoints CRUD y manejo de `ResponseEntity`
  - `JpaArticuloRepository` simplificado a solo consultas específicas
- refactor(core): `CostoParameterDto` y `CostoParameterService` actualizados para usar modelo de dominio `Articulo`

> Basado en análisis profundo de `git diff HEAD` (25 archivos modificados).

## Novedades 3.11.0 (verificado en código)
- feat(articulo): Migración de módulo Artículo a arquitectura hexagonal
  - Nueva entidad JPA: `ArticuloEntity` en `hexagonal/articulo/infrastructure/persistence/entity/` con anotaciones Lombok (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Nuevo repositorio: `JpaArticuloRepository` en `hexagonal/articulo/infrastructure/persistence/repository/` con métodos `findByArticuloId`, `findTopByOrderByArticuloIdDesc`
  - Nuevo servicio de aplicación: `ArticuloService` en `hexagonal/articulo/application/service/` con lógica de negocio
  - Nuevo controlador REST: `ArticuloController` en `hexagonal/articulo/infrastructure/web/controller/` migrado con `@RequiredArgsConstructor`
  - Eliminación de `Articulo.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `ArticuloRepository.java` del paquete `core/repository/`
  - Eliminación de `ArticuloService.java` del paquete `core/service/`
  - Eliminación de `ArticuloController.java` del paquete `core/controller/`
  - Actualización de referencias en `EntregaDetalle.kt`, `ProveedorArticulo.kt`, `UbicacionArticulo.java`, `AsignacionCostoDto.java`, `CostoParameterDto.java`, `CostoParameterService.java`
- fix(auth): Corrección de espacio extra en `ResponseStatusException` en `AuthController`

> Basado en análisis profundo de `git diff HEAD` (14 archivos modificados, +156/-98 líneas).

## Novedades 3.10.0 (verificado en código)
- feat(proveedor): Mejora de modelo de datos y refactorización de DTOs en módulo Proveedor
  - Renombrado de campo `cuenta` a `numeroCuenta` en `ProveedorSearch`, `ProveedorSearchEntity` y `ProveedorSearchResponse` para mayor claridad
  - Nuevo campo `cbu` en `ProveedorSearch`, `ProveedorSearchEntity`, `ProveedorResponse` y `ProveedorSearchResponse`
  - Nuevo campo `Cuenta cuenta` (objeto dominio) en `ProveedorResponse` y `ProveedorSearchResponse`
  - Refactorización de DTOs (`ProveedorResponse`, `ProveedorSearchResponse`) para usar patrón Builder de Lombok (`@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Refactorización de `ProveedorDtoMapper` para usar `builder()` en lugar de setters manuales
  - Actualización de `ProveedorMapper` para mapear nuevos campos (`numeroCuenta`, `cbu`)
  - Cambio de anotación en `ProveedorSearchEntity` y DTOs: de `@Data` a `@Getter`/`@Setter` para mayor control

> Basado en análisis profundo de `git diff HEAD` (6 archivos modificados, +67/-44 líneas).

## Novedades 3.9.0 (verificado en código)
- feat(proveedor): Implementación de paginación en módulo Proveedor
  - Nuevo puerto de entrada: `GetPaginatedProveedoresUseCase` con método `getPaginatedProveedores(int page, int size)`
  - Nuevo modelo genérico: `PaginatedResponse<T>` en `core/model/` para respuestas paginadas
  - Nuevo caso de uso: `GetPaginatedProveedoresUseCaseImpl` con implementación de paginación usando `Pageable`
  - Actualización de puerto de salida `ProveedorRepository` con método `findAll(Pageable pageable)`
  - Nuevo endpoint en `ProveedorController`: `GET /proveedor/page?page=X&size=Y` que retorna `PaginatedResponse<ProveedorResponse>`
  - Actualización de `ProveedorService` con método `getPaginated(int page, int size)`

> Basado en análisis profundo de `git diff HEAD` (7 archivos modificados, +101 líneas).

## Novedades 3.8.0 (verificado en código)
- feat(cuenta): Implementación completa de casos de uso CRUD para módulo Cuenta
  - Nuevos casos de uso: `CreateCuentaUseCase`, `DeleteCuentaUseCase`, `GetAllCuentasUseCase`, `GetCuentaByCuentaContableIdUseCase`, `GetCuentaByNumeroCuentaUseCase`, `RecalculaGradosUseCase`, `SaveAllCuentasUseCase`, `SearchCuentasUseCase`, `UpdateCuentaUseCase`
  - Nuevos DTOs: `CuentaRequest`, `CuentaResponse`, `CuentaSearchResponse`
  - Nuevo mapper: `CuentaDtoMapper` para conversión dominio ↔ DTO
  - Controlador `CuentaController` actualizado con todos los endpoints REST
- feat(proveedor): Mejora de módulo Proveedor con búsqueda avanzada
  - Nueva entidad `ProveedorSearchEntity` para búsquedas
  - Nuevo repositorio `JpaProveedorSearchRepository` con consultas personalizadas
  - Refactorización de paquetes: adaptador movido a `infrastructure/persistence/adapter/`
  - Actualización de `ProveedorMapper` y `GetAllProveedoresUseCaseImpl`
- refactor(service): Actualización de `BalanceService`, `ContabilidadService`, `SheetService` para usar nuevas estructuras hexagonales
- refactor(model): Actualización de `Ejercicio.kt` con cambios en modelo

> Basado en análisis profundo de `git diff HEAD` (47 archivos modificados, +894/-447 líneas).

## Novedades 3.7.0 (verificado en código)
- feat(hexagonal): Nuevo módulo Cuenta con arquitectura hexagonal
  - Entidad JPA: `CuentaEntity` con anotaciones Lombok (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
  - Repositorio: `CuentaRepository` con métodos `findAllByGradoAndNumeroCuentaGreaterThan`, `findAllByNumeroCuentaIn`, `findAllByGradoAndNumeroCuentaBetween`
  - Servicio de aplicación: `CuentaService` con lógica de negocio y método `recalculaGrados()`
  - Controlador REST: `CuentaController` con endpoints CRUD y búsquedas
  - Integración con `CuentaSearchService` para búsquedas avanzadas por condiciones
  - Relación `@OneToOne` con `GeograficaEntity`
- refactor(cuenta): Migración completa de módulo Cuenta a arquitectura hexagonal
  - Eliminación de `Cuenta.kt` (modelo Kotlin) del paquete `core/kotlin/model/`
  - Eliminación de `CuentaRepository.java` del paquete `core/repository/`
  - Eliminación de `CuentaService.java` del paquete `core/service/`
  - Eliminación de `CuentaController.java` del paquete `core/controller/`
  - Nueva estructura en `hexagonal/cuenta/` con capas domain, application, infrastructure
  - Actualización de servicios externos (`BalanceService`, `CompraService`, `ContabilidadService`) para usar nueva estructura
- refactor(model): Migración de modelos Kotlin a Java en paquete `core/kotlin/model/`
  - `Articulo.kt`, `Bancaria.kt`, `BancoMovimiento.kt`, `CuentaMovimiento.kt`, `Dependencia.kt`, `Setup.kt`, `Valor.kt`
  - Actualización de `UbicacionArticulo.java` y `CuentaMovimientoAsiento.java`

> Basado en análisis profundo de `git diff HEAD` (19 archivos modificados, +161/-146 líneas).

## Novedades 3.6.0 (verificado en código)
- feat(hexagonal): Nuevo módulo Auth con arquitectura hexagonal para autenticación
  - Modelo de dominio: `UsuarioAuth` con validación de login y password (SHA-256)
  - Caso de uso: `LoginUseCaseImpl` con lógica de autenticación robusta
  - Servicio de aplicación: `AuthService` como fachada del dominio
  - Adaptador JPA: `JpaUsuarioAuthRepositoryAdapter` con mappers
  - Controlador REST: `AuthController` con endpoints de login
  - DTOs: `LoginRequest` y `LoginResponse` para entrada/salida
- feat(hexagonal): Nuevo módulo Geografica con arquitectura hexagonal
  - Modelo de dominio: `Geografica` para entidades geográficas
  - Casos de uso: `GetAllGeograficasUseCase`, `GetGeograficaByIdUseCase`, `GetGeograficasBySedeUseCase`
  - Servicio de aplicación: `GeograficaService` con integración a servicios existentes
  - Adaptador JPA: `JpaGeograficaRepositoryAdapter` con `GeograficaEntity`
  - Controlador REST: `GeograficaController` migrado a arquitectura hexagonal
  - DTO: `GeograficaResponse` para respuestas HTTP
- feat(hexagonal): Implementación completa del módulo Proveedor con arquitectura hexagonal
  - Modelos de dominio: `Proveedor` y `ProveedorSearch` con Lombok
  - Puertos de entrada: `CreateProveedorUseCase`, `DeleteProveedorUseCase`, `GetAllProveedoresUseCase`, `GetLastProveedorUseCase`, `GetProveedorByCuitUseCase`, `GetProveedorByIdUseCase`, `SearchProveedoresUseCase`, `UpdateProveedorUseCase`
  - Puerto de salida: `ProveedorRepository` con métodos CRUD completos
  - Casos de uso: Implementaciones completas en `application/usecases/`
  - Servicio de aplicación: `ProveedorService` refactorizado con `Optional<Proveedor>` en retornos
  - Adaptador JPA: `JpaProveedorRepositoryAdapter` con implementación completa
  - Mappers: `ProveedorMapper` (dominio ↔ JPA) y `ProveedorDtoMapper` (dominio ↔ DTO)
  - DTOs: `ProveedorRequest`, `ProveedorResponse`, `ProveedorSearchResponse`
  - Controlador REST: `ProveedorController` migrado con manejo de `ResponseEntity` y `Optional`
- refactor(model): Migración de `Geografica.kt` (Kotlin) a `GeograficaEntity.java`
  - Eliminación de modelo Kotlin en paquete `core/kotlin/model/`
  - Creación de entidad JPA en `hexagonal/geografica/infrastructure/persistence/entity/`
  - Actualización de `CursoHaberes.java` para usar `GeograficaEntity` en lugar de `Geografica`
- refactor(proveedor): Reestructuración completa de paquetes de Proveedor a arquitectura hexagonal
  - Migración de servicios y repositorios al paquete `hexagonal/proveedor/`
  - Nuevos modelos de dominio reemplazando entidades en capa de dominio
  - Mappers agregados para conversión entre capas

> Basado en análisis profundo de `git diff HEAD` (55 archivos modificados).

## Novedades 3.5.2 (verificado en código)
- chore(deps): Actualización de Spring Boot de 4.0.2 a 4.0.5
- chore(deps): Actualización de Kotlin de 2.3.10 a 2.3.20
- chore(deps): Actualización de log4j2 de 4.0.2 a 4.0.5
- chore(deps): Actualización de springdoc-openapi de 3.0.1 a 3.0.2
- chore(deps): Actualización de openpdf de 3.0.0 a 3.0.3
- chore(deps): Actualización de json-path de 2.10.0 a 3.0.0
- chore(ci): Actualización de GitHub Actions (checkout v4→v6, setup-java v4→v5, cache v4→v5, login-action v3→v4, metadata-action v5→v6, buildx-action v3→v4, build-push-action v6→v7, deploy-pages v4→v5)
- feat(deps): Nueva dependencia commons-fileupload 1.6.0

## Novedades 3.5.1 (verificado en código)
- refactor(model): Migración de FacturacionElectronica de Kotlin a Java
  - Eliminación de FacturacionElectronica.kt (modelo Kotlin)
  - Creación de FacturacionElectronica.java con anotaciones Lombok (@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor)
  - Nuevo método jsonify() para logging estructurado
- refactor(controller): Uso de ResponseEntity.ok() en lugar de new ResponseEntity<>() en FacturacionElectronicaController
- refactor(service): Uso de utilitaria Jsonifier en lugar de JsonMapper directamente en FacturacionElectronicaService
- refactor(config): Nueva configuración de Jackson en bootstrap.yml (fail-on-null-for-primitives: false)

## Novedades 3.5.0 (verificado en código)
- refactor(deps): Actualización de Kotlin de 2.3.0 a 2.3.10
- refactor(http): Migración masiva de RestTemplate a RestClient en todos los consumers externos (~14 archivos)
- feat(model): Nuevos campos de becario (hpum, becaPorcentaje, becaResolucion, becaFecha, becaUserId) en ChequeraImpresionCabecera y ChequeraSerie
- feat(model): Nuevo campo hpum en PersonaEntity
- feat(hexagonal): Nueva estructura skeleton MatriculacionContext con arquitectura hexagonal
- refactor(controller): Migración de EjercicioController a inyección con @RequiredArgsConstructor
- fix(exception): Renombrado BajaFacultadNotFoundException a BajaFacultadException
- chore(cleanup): Eliminación de MatriculaController, MatriculaRepository y modelo Matricula
- fix(api): Eliminación de endpoint sincronizeMatricula en SincronizeController
- feat(api): Nuevo endpoint `/habilitados` en ArancelTipoController para obtener tipos de arancel habilitados
  - Nuevo método `findAllHabilitados()` en ArancelTipoService
  - Nuevo método `findAllByHabilitado()` en ArancelTipoRepository
  - Retorna solo registros con campo `habilitado = 1`
- refactor: Migración a inyección por constructor con `@RequiredArgsConstructor` en ArancelTipoService
  - Reemplazo de `@Resource` por campos `final` + constructor Lombok
- refactor: Uso de `ResponseEntity.ok()` en lugar de `new ResponseEntity<>()` en CostoController
- fix: Agregado `assert` en CostoService para validación de objeto Entrega

## Novedades 3.3.2 (verificado en código)
- refactor: Migración a inyección por constructor con `@RequiredArgsConstructor` en ArancelTipoController, FacultadController y TipoChequeraSedeController
  - Reemplazo de `@Resource`/`@Autowired` por `final` + constructor Lombok
  - Uso de `ResponseEntity.ok()` en lugar de `new ResponseEntity<>()`
- fix(api): Agregado manejo de excepciones con `ResponseStatusException` en controladores
  - Devolución de HTTP 400 Bad Request para errores de negocio
- fix(transaction): Agregado `@Transactional` en MercadoPagoContextService.processPaymentEvent()

## Novedades 3.3.1 (verificado en código)
- fix(performance): Optimización de rendimiento en actualización de último login
  - Implementación de query directa con `@Modifying` en `UsuarioRepository`
  - Refactorización de `UsuarioService.updateLastLog()` para reducir overhead de persistencia
  - Corrección de manejo transaccional en `MercadoPagoContextService.processPaymentEvent()`

## Novedades 3.3.0 (verificado en código)
- feat: Implementación de batch processing y optimizaciones de rendimiento en PersonaService
  - Nuevos métodos batch en ChequeraCuotaRepository para consultas masivas por IDs
  - Nuevo método `findAllByChequeraCuotaIds()` en MercadoPagoContextService
  - Implementación de "Self-Healing" para corrección automática de chequeraId nulos
  - Reducción de N+1 queries mediante carga masiva de contextos
  - Optimización en creación de preferencias con reutilización de contextos existentes
- refactor: Migración de configuración Kafka a nueva API Jackson (JacksonJsonDeserializer)
  - Actualización de paquetes de `com.fasterxml.jackson` a `tools.jackson`
  - Simplificación de configuración del ObjectMapper
- feat: Nuevo método sobrecargado `makeContext()` en MercadoPagoCoreService
  - Permite reutilizar contextos existentes evitando duplicados

## Novedades 3.2.0 (verificado en código)
- feat: Implementación completa del módulo ChequeraCuota con Arquitectura Hexagonal
  - Nuevos modelos de dominio: `ChequeraCuota`, `ChequeraPagoEntity`, `ChequeraTotalEntity`, `DeudaData`, `ChequeraSerie`
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
- Spring Boot: 4.1.0
- Spring Cloud: 2025.1.2
- Kotlin: 2.4.0
- MySQL Connector: 9.7.0
- SpringDoc OpenAPI: 3.0.3
- Apache POI: 5.5.1
- OpenPDF: 3.0.5
- ModelMapper: 3.2.6
- Guava: 33.6.0-jre
- json-path: 3.0.0

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
│   │       ├── hexagonal/
│   │       │   ├── articulo/                 # Módulo Artículo (v3.13.0)
│   │       │   ├── asiento/                  # Módulo Asiento (v3.29.0)
│   │       │   ├── auth/                     # Módulo Auth (v3.6.0)
│   │       │   ├── baja/                     # Módulo Baja (v3.21.0)
│   │       │   ├── chequera/                 # Módulos agrupados de chequera (v3.30.0)
│   │       │   │   ├── arancelPorcentaje/    # Módulo ArancelPorcentaje (v3.29.0)
│   │       │   │   ├── arancelTipo/          # Módulo ArancelTipo (v3.29.0)
│   │       │   │   ├── chequeraCuota/        # Módulo ChequeraCuota (v3.2.0)
│   │       │   │   ├── chequeraSerie/        # Módulo ChequeraSerie (v3.21.0)
│   │       │   │   ├── claseChequera/        # Módulo ClaseChequera (v3.30.0)
│   │       │   │   ├── producto/             # Módulo Producto (v3.30.0)
│   │       │   │   └── tipoChequera/         # Módulo TipoChequera (v3.30.0)
│   │       │   ├── contable/                 # Módulos contables (v3.30.0)
│   │       │   │   └── cuenta/               # Módulo Cuenta (v3.8.0)
│   │       │   ├── contrato/                 # Módulo Contrato (v3.19.0)
│   │       │   ├── dependencia/              # Módulo Dependencia (v3.17.0)
│   │       │   ├── documento/                # Módulo Documento (v3.33.0)
│   │       │   ├── domicilio/                # Módulo Domicilio (v3.24.0)
│   │       │   ├── facultad/                 # Módulo Facultad (v3.18.0)
│   │       │   ├── facturaPendiente/         # Módulo FacturaPendiente (v3.15.0)
│   │       │   ├── geografica/               # Módulo Geografica (v3.6.0)
│   │       │   ├── guarani/alumnoGuarani/    # Módulo AlumnoGuarani (v3.29.0)
│   │       │   ├── lectivo/                  # Módulo Lectivo (v3.30.0)
│   │       │   ├── lectivoTotalImputacion/   # Módulo LectivoTotalImputacion (v3.31.0)
│   │       │   ├── mercadoPagoContext/       # Módulo MercadoPagoContext (v3.24.0)
│   │       │   ├── persona/                  # Módulo Persona (v3.24.0)
│   │       │   ├── proveedor/                # Módulo Proveedor (v3.9.0)
│   │       │   ├── ubicacion/                # Módulo Ubicacion (v3.14.0)
│   │       │   ├── ubicacionArticulo/        # Módulo UbicacionArticulo (v3.14.0)
│   │       │   ├── umhub/campanha/           # Módulo Campanha (v3.24.0)
│   │       │   ├── umhub/reservaVacante/     # Módulo ReservaVacante (v3.24.0)
│   │       │   └── matriculacionContext/
│   │       ├── model/                 # Modelos legacy
│   │       ├── service/               # Servicios legacy
│   │       ├── controller/             # Controladores legacy
│   │       └── repository/            # Repos legacy
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

[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.2-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-purple.svg)](https://kotlinlang.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8.8+-orange.svg)](https://maven.apache.org/)
[![Versión](https://img.shields.io/badge/versión-3.33.1-blue.svg)]()

## Documentación
- [Documentación en GitHub Pages](https://um-services.github.io/UM.tesoreria.core-service/)
- [Wiki del Proyecto](https://github.com/UM-services/UM.tesoreria.core-service/wiki)


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
- Java 25
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