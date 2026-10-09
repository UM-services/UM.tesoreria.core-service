# Diagramas de Documentación

**Versión actual del servicio: 8.0.0** (actualizada: 2026-10-08)

Este directorio contiene los diagramas Mermaid generados automáticamente para la documentación del servicio:

## Diagramas de Arquitectura
- `architecture.mmd`: Arquitectura general (controladores, servicios, repositorios, entidades).
- `hexagonal-architecture.mmd`: Arquitectura hexagonal implementada en el caso de uso Curso Cargo Contratado.
- `hexagonal-articulo.mmd`: Arquitectura hexagonal del módulo Artículo (gestión de artículos) - v8.0.0 (validación, baja segura con referencias, historial #404 y puertos de otros slices).
- `hexagonal-comprobante.mmd`: Arquitectura hexagonal del módulo Comprobante (tipos de comprobantes AFIP) - v3.36.0 (nuevo módulo, 6 casos de uso).
- `hexagonal-cuentaMovimiento.mmd`: Arquitectura hexagonal del módulo CuentaMovimiento (asientos contables) - v3.36.0 (nuevo módulo, 11 casos de uso).
- `hexagonal-proveedorMovimiento.mmd`: Arquitectura hexagonal del módulo ProveedorMovimiento (movimientos de proveedores) - v3.36.0 (nuevo módulo, 13 casos de uso).
- `hexagonal-track.mmd`: Arquitectura hexagonal del módulo Track (seguimiento) - v3.36.0 (nuevo módulo, 4 casos de uso).
- `hexagonal-chequeraCuota.mmd`: Arquitectura hexagonal del módulo ChequeraCuota (21 casos de uso individuales) - v3.38.0 (nuevo caso de uso GetCuotaActualUseCase + endpoint cuotaActual).
- `hexagonal-mercadoPagoContext.mmd`: Arquitectura hexagonal del módulo MercadoPagoContext (contexto de pagos MP) - v3.26.0.
- `hexagonal-auth.mmd`: Arquitectura hexagonal del módulo Auth (autenticación de usuarios) - v4.7.0 (puerto `ChangePasswordUseCase`, endpoints `change-password`/`me/{userId}` y updates dirigidos `updateLastLog`/`updateCredentials` en `UsuarioAuthRepository`).
- `hexagonal-geografica.mmd`: Arquitectura hexagonal del módulo Geografica (entidades geográficas).
- `hexagonal-proveedor.mmd`: Arquitectura hexagonal del módulo Proveedor (gestión de proveedores) - v3.36.0 (reubicado bajo `compras/`).
- `hexagonal-cuenta.mmd`: Arquitectura hexagonal del módulo Cuenta (gestión de cuentas contables) - v3.8.0.
- `hexagonal-mercadopago-context-history.mmd`: Historial de contexto de MercadoPago.
- `hexagonal-ubicacion.mmd`: Arquitectura hexagonal del módulo Ubicacion (gestión de ubicaciones) - v8.0.0 (nuevo puerto público `GetUbicacionByIdUseCase`).
- `hexagonal-ubicacionArticulo.mmd`: Arquitectura hexagonal del módulo UbicacionArticulo (gestión de ubicaciones de artículos) - v8.0.0 (asignación idempotente, borrado de vínculos en la baja del artículo, historial #404).
- `hexagonal-dependencia.mmd`: Arquitectura hexagonal del módulo Dependencia (gestión de dependencias) - v3.17.0.
- `hexagonal-facturaPendiente.mmd`: Arquitectura hexagonal del módulo FacturaPendiente (gestión de facturas pendientes) - v3.36.0 (reubicado bajo `compras/`).
- `hexagonal-facultad.mmd`: Arquitectura hexagonal del módulo Facultad (gestión de facultades) - v3.50.0 (caso de uso de responsable académica).
- `hexagonal-lectivoTotalImputacion.mmd`: Arquitectura hexagonal del módulo LectivoTotalImputacion (imputaciones contables por lectivo) - v3.31.0 (nuevo caso de uso FindAllByLectivo + enriquecimiento con asociaciones a Facultad/Lectivo/TipoChequera/Producto/Cuenta).
- `hexagonal-contrato.mmd`: Arquitectura hexagonal del módulo Contrato (gestión de contratos) - v3.19.0.
- `hexagonal-chequeraSerie.mmd`: Arquitectura hexagonal del módulo ChequeraSerie (consulta preuniversitaria desde datos Guaraní, incompletas por `claseChequeraId`, chequeras por usuario con deuda vencida y chequeras por las tres asignaciones del usuario —facultad, sede y clase— en `/usuario/{userId}/lectivo/{lectivoId}/asignaciones`) - v4.8.0.
- `hexagonal-estadoChequera.mmd`: Arquitectura hexagonal del módulo EstadoChequera bajo `chequera/` (estado de la chequera como JSON de solo lectura para el PDF "Estado de Chequera" de report-service: `GET /api/tesoreria/core/chequera/estado/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}` — sin `{debitoTipoId}` desde `6.0.0` —, puerto `GetEstadoChequeraUseCase` de 4 argumentos, records `EstadoChequera`/`ProductoEstado`/`CuotaEstado`/`DebitoEstado` con `tipoDebito` (débitos de todos los tipos VISA + Directo), sin persistencia propia y sin salida del `generateEstadoPdf` eliminado en `5.0.0`) - v5.0.0 (nuevo módulo; v6.0.0 retira `debitoTipoId` de la ruta y agrega `tipoDebito`).
- `hexagonal-escrituraHistorial.mmd`: Arquitectura hexagonal del módulo EscrituraHistorial (historial transaccional de altas/ediciones/bajas de Gestión; contrato `RegistrarEscrituraHistorialUseCase` con propagación `MANDATORY`; sin consulta pública ni actor verificado; script `docs/sql/V404__gestion_escritura_historial.sql`) - v6.2.0 (nuevo módulo, issue #404).
- `gestion-escritura-historial.md`: Contrato reutilizable y guía de integración del historial de escrituras (#404).
- `gestion-articulo-ubicacion.md`: Guía para consumidores de las escrituras de artículo y ubicacionArticulo (#405): reglas, errores, recuperación de fallos parciales, baja segura e historial, con ejemplos ejecutados.
- `hexagonal-baja.mmd`: Arquitectura hexagonal del módulo Baja (gestión de bajas de chequeras) - v3.36.0 (reubicado bajo `chequera/`).
- `hexagonal-campanha.mmd`: Arquitectura hexagonal del módulo Campanha (gestión de campañas UM Hub) - v3.24.0.
- `hexagonal-chequeraProducto.mmd`: Arquitectura hexagonal del módulo Producto (gestión de productos chequera) - v3.30.0 (nuevo módulo).
- `hexagonal-chequeraTipoChequera.mmd`: Arquitectura hexagonal del módulo TipoChequera (tipos de chequera con búsqueda por condiciones y geográfica) - v3.49.0.
- `hexagonal-guaraniPropuestaTipoChequera.mmd`: Arquitectura hexagonal del módulo GuaraniPropuestaTipoChequera (asignación de tipo de chequera a propuesta y lectivo, con tipo de chequera enriquecido) - v3.49.0.
- `hexagonal-claseChequera.mmd`: Arquitectura hexagonal del módulo ClaseChequera (clasificación de chequeras) - v4.7.0 (puerto de entrada `GetClaseChequeraByIdUseCase`, `ClaseChequeraService.findById`, alias `/api/tesoreria/core/clasechequera` y firmas reales del puerto de salida sincronizadas).
- `hexagonal-lectivo.mmd`: Arquitectura hexagonal del módulo Lectivo (gestión de lectivos con 8 casos de uso) - v3.30.0 (nuevo módulo).
- `hexagonal-reservaVacante.mmd`: Arquitectura hexagonal del módulo ReservaVacante (gestión de reservas de vacantes UM Hub) - v3.32.0 (nuevo UpdateReservaVacanteUseCase con integración de pago MercadoPago).
- `hexagonal-consulta.mmd`: Arquitectura hexagonal del módulo Consulta (UM Hub): datos personales + domicilio/contacto y deuda agregada por **número de documento sin tipo** (`GET /api/tesoreria/core/umhub/consulta/persona/{numeroDocumento}` y `.../deuda[?extended=]`), DTOs filtrados sin `password`/`cbu`/`cuit`/`emailPagador`, fusión de todos los tipos del mismo titular (caso LE/LC=DNI) y regla anti-colisión de identidad - v6.1.0.
- `hexagonal-domicilio.mmd`: Arquitectura hexagonal del módulo Domicilio (gestión de domicilios) - v4.2.1 (`Domicilio`/`DomicilioEntity` implementan `Jsonifyable`, captura defensiva de correos).
- `hexagonal-alumnoGuarani.mmd`: Arquitectura hexagonal del módulo AlumnoGuarani (integración con sistema Guaraní y creación de datos personales) - v3.50.0.
- `hexagonal-arancelTipo.mmd`: Arquitectura hexagonal del módulo ArancelTipo (gestión de tipos de arancel) - v3.29.0 (migración desde Kotlin legacy).
- `hexagonal-arancelPorcentaje.mmd`: Arquitectura hexagonal del módulo ArancelPorcentaje (porcentajes por producto) - v3.29.0 (migración desde Kotlin legacy).
- `hexagonal-asiento.mmd`: Arquitectura hexagonal del módulo Asiento (asientos contables) - v3.29.0 (migración desde Kotlin legacy).
- `hexagonal-documento.mmd`: Arquitectura hexagonal del módulo Documento bajo `personas`, incluyendo búsqueda por tipo Guaraní y rutas REST compatibles - v3.50.1.
- `hexagonal-usuario.mmd`: Arquitectura hexagonal del módulo Usuario bajo `usuarios/` (gestión de usuarios) - v6.3.0 (`GET /usuario/search`, puertos `GetUsuarioByIdUseCase`/`FindUsuariosBySearchUseCase`, campos `administrador`/`usuarioExterno`/`dependenciaId`, `UsuarioMapper.updateEntity` y PUT estricto con `@Valid`).
- `hexagonal-usuarioChequeraFacultad.mmd`: Arquitectura hexagonal del módulo UsuarioChequeraFacultad bajo `usuarios/` (facultades de chequera por usuario) - v4.7.0 (nuevo slice en `4.5.0`; en `4.7.0` agrega administración `POST /` idempotente y `DELETE /user/{userId}/facultad/{facultadId}` con validación de referencias vía puertos de `usuario`/`facultad`).
- `hexagonal-usuarioChequeraClaseChequera.mmd`: Arquitectura hexagonal del módulo UsuarioChequeraClaseChequera bajo `usuarios/` (clases de chequera habilitadas por usuario para filtrar la consulta de chequeras) - v4.7.0 (nuevo slice: `GET /user/{userId}`, `POST /` idempotente y `DELETE /user/{userId}/claseChequera/{claseChequeraId}` con validación vía puertos de `usuario`/`claseChequera`).
- `hexagonal-usuarioChequeraGeografica.mmd`: Arquitectura hexagonal del módulo UsuarioChequeraGeografica bajo `usuarios/` (sedes geográficas asignadas al usuario) - v4.7.0 (nuevo slice: `GET /user/{userId}`, `POST /` idempotente y `DELETE /user/{userId}/geografica/{geograficaId}` con validación vía puertos de `usuario`/`geografica`).
- `hexagonal-permiso.mmd`: Arquitectura hexagonal del módulo Permiso (catálogo de claves `modulo.accion`) - v6.3.0 (nuevo slice con CRUD en `/api/tesoreria/core/permiso`).
- `hexagonal-rol.mmd`: Arquitectura hexagonal del módulo Rol (roles de permisos) - v6.3.0 (nuevo slice con CRUD en `/api/tesoreria/core/rol`).
- `hexagonal-rolPermiso.mmd`: Arquitectura hexagonal del módulo RolPermiso (matriz rol×permiso) - v6.3.0 (nuevo slice: `GET /rol/{rolId}`, `POST /` idempotente y `DELETE /rol/{rolId}/permiso/{permisoId}`).
- `hexagonal-usuarioRol.mmd`: Arquitectura hexagonal del módulo UsuarioRol (roles del usuario) - v6.3.0 (nuevo slice: `GET /user/{userId}`, `POST /` idempotente y `DELETE /user/{userId}/rol/{rolId}`).
- `hexagonal-usuarioPermiso.mmd`: Arquitectura hexagonal del módulo UsuarioPermiso (override individual por usuario) - v6.3.0 (nuevo slice: `GET /user/{userId}`, `PUT /user/{userId}/permiso/{permisoId}` con `{ otorgado: 1|0 }` y `DELETE`).
- `hexagonal-permisoEfectivo.mmd`: Arquitectura hexagonal del módulo PermisoEfectivo (bundle efectivo del usuario) - v6.3.0 (nuevo slice `GET /api/tesoreria/core/permisoEfectivo/usuario/{userId}`, composición de puertos de entrada de los slices de permisos + puente con los flags legacy de `usuario`).
- `hexagonal-compraPedido.mmd`: Arquitectura hexagonal del módulo CompraPedido (pedido de compra: circuito de presentación/aprobación/rechazo/descarte, correlativo al presentar y línea de tiempo) - v7.0.0 (nuevo en `6.3.0`; en `7.0.0` renombra `autorizar`→`aprobar`, agrega `descartar`, `POST /search`/`dependenciaIds`, campos `fechaEnvio`/`rechazoMotivo`/`descartadoMotivo` y los estados `PENDIENTE_ENVIO`/`ENVIADO`/`RECHAZADO`/`DESCARTADO`).
- `hexagonal-compraPedidoItem.mmd`: Arquitectura hexagonal del módulo CompraPedidoItem (detalle del pedido, alta/edición a través del pedido) - v6.3.0 (nuevo slice, `GET /api/tesoreria/core/compraPedidoItem/pedido/{compraPedidoId}`).
- `hexagonal-compraPedidoSecuencia.mmd`: Arquitectura hexagonal del módulo CompraPedidoSecuencia (correlativo anual atómico con `LAST_INSERT_ID`) - v6.3.0 (nuevo slice interno, sin REST).
- `hexagonal-compraPedidoAutorizante.mmd`: Arquitectura hexagonal del módulo CompraPedidoAutorizante (dependencias habilitadas por usuario autorizante de envío, tabla puente `compra_pedido_autorizante_dependencia` por SQL directo) - v7.0.0 (nuevo slice: `GET /dependencias/{autorizanteId}`, `POST` idempotente y `DELETE /{autorizanteId}/{dependenciaId}` en `/api/tesoreria/core/compraPedidoAutorizante`).
- `hexagonal-compraPedidoHistorial.mmd`: Arquitectura hexagonal del módulo CompraPedidoHistorial (línea de tiempo de estados del pedido, estado como texto para no acoplar slices) - v7.0.0 (nuevo slice: `GET /api/tesoreria/core/compraPedidoHistorial/{compraPedidoId}`; el pedido registra el evento en la misma transacción).
- `hexagonal-persona.mmd`: Arquitectura hexagonal del módulo Persona bajo `personas` - v6.1.0 (sugerencias de personas con chequeras por usuario, v4.6.0; nuevo puerto de entrada `GetPersonasByNumeroUseCase` y método `findAllByPersonaId` en `PersonaRepository`/`JpaPersonaRepositoryAdapter` para consultar todas las filas de un número de documento sin exigir el tipo).
- `hexagonal-chequeraPago.mmd`: Arquitectura hexagonal del módulo ChequeraPago (gestión de pagos de chequeras con 12 casos de uso) - v3.40.0 (enriquecimiento con asociaciones TipoPago, Producto, ChequeraCuota).
- `hexagonal-chequeraTotal.mmd`: Arquitectura hexagonal del módulo ChequeraTotal (totales de chequeras con 5 casos de uso) - v3.37.0 (nuevo módulo).
- `hexagonal-politicaArancelaria.mmd`: Arquitectura hexagonal del módulo PoliticaArancelaria (recálculo de cuotas por política arancelaria) - v3.42.0 (refactorización de RecalculateCuotaByUniqueIndexUseCaseImpl, fallback a LectivoCuota con importes cero).
- `hexagonal-lectivoCuota.mmd`: Arquitectura hexagonal del módulo LectivoCuota (cuotas lectivas por facultad/lectivo/tipo) - v3.42.0 (nuevo caso de uso FindLectivoCuotaByFechaUseCase, formato ISO 8601).
- `hexagonal-setup.mmd`: Arquitectura hexagonal del módulo Setup (configuración de tesorería) - v3.43.0 (nuevo módulo).
- `hexagonal-deudaExamen.mmd`: Arquitectura hexagonal del módulo DeudaExamen (validación de habilitación para rendir exámenes) - v3.46.0 (integración con TesoreriaEstadoFacultadService para habilitación manual).
- `hexagonal-guaraniBeneficio.mmd`: Arquitectura hexagonal del módulo GuaraniBeneficio (beneficios de estudiantes Guarani) - v3.47.0 (nuevo listado completo).
- `hexagonal-tesoreriaEstado.mmd`: Arquitectura hexagonal del módulo TesoreriaEstado (consulta de estado de tesorería del estudiante en el servicio de facultad) - v3.46.0 (nuevo módulo, consumer REST).
- `hexagonal-guaraniUbicacion.mmd`: Arquitectura hexagonal del módulo GuaraniUbicacion (CRUD y consulta por ubicación Guaraní) - v3.50.0.

- `deployment.mmd`: Diagrama de despliegue del microservicio.

## Diagramas de Datos
- `dependencies.mmd`: Diagrama de dependencias del proyecto.
- `entities.mmd`: Diagrama entidad-relación (ER) de los principales modelos de dominio.

## Diagramas de Secuencia
- `sequence-example.mmd`: Ejemplo de diagrama de secuencia para una petición típica.
- `sequence-alta-usuario.mmd`: Alta de usuario.
- `sequence-baja-usuario.mmd`: Baja de usuario.
- `sequence-chequera-serie-sede.mmd`: Chequera serie sede.
- `sequence-comprobante.mmd`: Comprobante.
- `sequence-consulta-articulos.mmd`: Consulta de artículos.
- `sequence-liquidacion-sueldos.mmd`: Liquidación de sueldos.
- `sequence-mercadopago-to-change.mmd`: MercadoPago to change.
- `sequence-movimientos-cuenta.mmd`: Movimientos de cuenta.
- `sequence-pago-chequera.mmd`: Pago de chequera.
- `sequence-reemplazo-chequera.mmd`: Reemplazo de chequera.

## Reglas para la creación de Diagramas Mermaid
- **No incluir namespaces vacíos**: En diagramas de clase (`classDiagram`), nunca incluir bloques vacíos como `namespace infrastructure { }`. Mermaid v10 arroja `Syntax Error` si un namespace no contiene al menos una clase.
- **Formato de genéricos**: Utilizar tildes `~` para tipos genéricos (ej. `List~String~` en lugar de `<String>`).
- **Anotaciones con espacios**: En `classDiagram`, escribir las anotaciones como `<< interface >>`, `<< JpaRepository >>`, `<< external >>` (con espacios interiores). La forma apretada `<<interface>>` es interpretada por el navegador como etiquetas HTML al inyectar el `.mmd` con `innerHTML` en `index.html`, lo que corrompe el texto y produce `Syntax error in text` en el visor, aunque el CLI valide el archivo sin errores.
- **Validación automática**: `docs/script.js` sanitiza bloques `namespace` vacíos al vuelo y escapa el contenido antes de inyectarlo, pero los archivos `.mmd` deben guardarse limpios.

## Notas sobre el pipeline
- El workflow `.github/workflows/generate-docs.yml` valida automáticamente todos los archivos `docs/*.mmd` antes de publicar.
- Si un archivo de diagrama falta o tiene error de sintaxis, debe mostrar una advertencia clara pero no fallar el build.
- Los diagramas pueden ser visualizados en la documentación generada automáticamente.
