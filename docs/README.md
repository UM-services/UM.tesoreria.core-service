# Diagramas de Documentación

**Versión actual del servicio: 3.34.0** (actualizada: 2026-07-10)

Este directorio contiene los diagramas Mermaid generados automáticamente para la documentación del servicio:

## Diagramas de Arquitectura
- `architecture.mmd`: Arquitectura general (controladores, servicios, repositorios, entidades).
- `hexagonal-architecture.mmd`: Arquitectura hexagonal implementada en el caso de uso Curso Cargo Contratado.
- `hexagonal-articulo.mmd`: Arquitectura hexagonal del módulo Artículo (gestión de artículos) - v3.13.0.
- `hexagonal-chequeraCuota.mmd`: Arquitectura hexagonal del módulo ChequeraCuota (20+ casos de uso individuales) - v3.30.0 (refactorización masiva a casos de uso).
- `hexagonal-mercadoPagoContext.mmd`: Arquitectura hexagonal del módulo MercadoPagoContext (contexto de pagos MP) - v3.26.0.
- `hexagonal-auth.mmd`: Arquitectura hexagonal del módulo Auth (autenticación de usuarios).
- `hexagonal-geografica.mmd`: Arquitectura hexagonal del módulo Geografica (entidades geográficas).
- `hexagonal-proveedor.mmd`: Arquitectura hexagonal del módulo Proveedor (gestión de proveedores).
- `hexagonal-cuenta.mmd`: Arquitectura hexagonal del módulo Cuenta (gestión de cuentas contables) - v3.8.0.
- `hexagonal-mercadopago-context-history.mmd`: Historial de contexto de MercadoPago.
- `hexagonal-ubicacion.mmd`: Arquitectura hexagonal del módulo Ubicacion (gestión de ubicaciones) - v3.14.0.
- `hexagonal-ubicacionArticulo.mmd`: Arquitectura hexagonal del módulo UbicacionArticulo (gestión de ubicaciones de artículos) - v3.14.0.
- `hexagonal-dependencia.mmd`: Arquitectura hexagonal del módulo Dependencia (gestión de dependencias) - v3.17.0.
- `hexagonal-facturaPendiente.mmd`: Arquitectura hexagonal del módulo FacturaPendiente (gestión de facturas pendientes) - v3.15.0.
- `hexagonal-facultad.mmd`: Arquitectura hexagonal del módulo Facultad (gestión de facultades) - v3.18.0.
- `hexagonal-lectivoTotalImputacion.mmd`: Arquitectura hexagonal del módulo LectivoTotalImputacion (imputaciones contables por lectivo) - v3.31.0 (nuevo caso de uso FindAllByLectivo + enriquecimiento con asociaciones a Facultad/Lectivo/TipoChequera/Producto/Cuenta).
- `hexagonal-contrato.mmd`: Arquitectura hexagonal del módulo Contrato (gestión de contratos) - v3.19.0.
- `hexagonal-chequeraSerie.mmd`: Arquitectura hexagonal del módulo ChequeraSerie (15+ casos de uso individuales) - v3.30.0 (refactorización masiva a casos de uso).
- `hexagonal-baja.mmd`: Arquitectura hexagonal del módulo Baja (gestión de bajas de chequeras) - v3.21.0.
- `hexagonal-campanha.mmd`: Arquitectura hexagonal del módulo Campanha (gestión de campañas UM Hub) - v3.24.0.
- `hexagonal-chequeraProducto.mmd`: Arquitectura hexagonal del módulo Producto (gestión de productos chequera) - v3.30.0 (nuevo módulo).
- `hexagonal-chequeraTipoChequera.mmd`: Arquitectura hexagonal del módulo TipoChequera (tipos de chequera con 11 casos de uso) - v3.30.0 (nuevo módulo).
- `hexagonal-claseChequera.mmd`: Arquitectura hexagonal del módulo ClaseChequera (clasificación de chequeras) - v3.30.0 (nuevo módulo).
- `hexagonal-lectivo.mmd`: Arquitectura hexagonal del módulo Lectivo (gestión de lectivos con 8 casos de uso) - v3.30.0 (nuevo módulo).
- `hexagonal-reservaVacante.mmd`: Arquitectura hexagonal del módulo ReservaVacante (gestión de reservas de vacantes UM Hub) - v3.32.0 (nuevo UpdateReservaVacanteUseCase con integración de pago MercadoPago).
- `hexagonal-domicilio.mmd`: Arquitectura hexagonal del módulo Domicilio (gestión de domicilios) - v3.24.0.
- `hexagonal-alumnoGuarani.mmd`: Arquitectura hexagonal del módulo AlumnoGuarani (integración con sistema Guarani) - v3.29.0 (simplificación de DTOs y extracción de PreuniversitarioChequeraService).
- `hexagonal-arancelTipo.mmd`: Arquitectura hexagonal del módulo ArancelTipo (gestión de tipos de arancel) - v3.29.0 (migración desde Kotlin legacy).
- `hexagonal-arancelPorcentaje.mmd`: Arquitectura hexagonal del módulo ArancelPorcentaje (porcentajes por producto) - v3.29.0 (migración desde Kotlin legacy).
- `hexagonal-asiento.mmd`: Arquitectura hexagonal del módulo Asiento (asientos contables) - v3.29.0 (migración desde Kotlin legacy).
- `hexagonal-documento.mmd`: Arquitectura hexagonal del módulo Documento (gestión de tipos de documento) - v3.33.0 (nuevo módulo con migración desde Kotlin legacy).
- `hexagonal-persona.mmd`: Arquitectura hexagonal del módulo Persona (gestión de personas) - v3.24.0.

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

## Notas sobre el pipeline
- El pipeline de documentación debe validar la sintaxis Mermaid antes de publicar.
- Si un archivo de diagrama falta o tiene error de sintaxis, debe mostrar una advertencia clara pero no fallar el build.
- Los diagramas pueden ser visualizados en la documentación generada automáticamente.
