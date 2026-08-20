# Misión — UM.tesoreria.core-service

> Documento de constitución. Define **qué es** este servicio y **para quién**.
> No describe cómo está implementado (ver `tech-stack.md`) ni en qué orden avanzamos (ver `roadmap.md`).
>
> Versión del servicio al redactar: **4.1.1** · Fecha: **2026-08-19**

---

## 1. Propósito

**El core de tesorería es la fuente única de verdad transaccional de la Universidad de Mendoza para todo lo que involucra dinero de alumnos, y el puente formal entre el sistema académico Guaraní y esa realidad económica.**

Se desdobla en dos responsabilidades inseparables:

### 1.1 Núcleo transaccional de tesorería

El servicio es dueño del modelo de dominio del dinero académico y administrativo:

- **Chequeras**: series, tipos, clases, alternativas, productos, totales, bajas y reemplazos.
- **Cuotas**: importes en tres tramos con sus vencimientos (`importe1/2/3`, `vencimiento1/2/3`), importes originales, estado de pago, compensación y recálculo por política arancelaria.
- **Pagos**: registración, imputación, tipos de pago, conciliación con facturación electrónica y con MercadoPago.
- **Deuda**: cálculo de deuda por persona, por lectivo y por producto, incluyendo la deuda que habilita o bloquea actos académicos.
- **Contabilidad y compras asociadas**: cuentas, asientos, movimientos, proveedores, comprobantes, órdenes de pago.

Ninguna otra pieza del ecosistema tiene autoridad sobre estos datos. Si un importe, un vencimiento o un estado de pago difiere entre dos sistemas, **el core tiene razón**.

### 1.2 Puente Guaraní ↔ Tesorería

Guaraní es la autoridad académica; el core traduce esa realidad a consecuencias económicas:

- Alta de alumnos y datos personales provenientes de Guaraní (`AlumnoGuarani`, `PersonalesResultado`).
- Mapeo de propuestas académicas a tipos de chequera (`GuaraniPropuestaTipoChequera`).
- Ubicaciones y responsables académicos (`GuaraniUbicacion`, `Facultad.guaraniResponsableAcademica`).
- **Beneficios por requisitos de ingreso** (`GuaraniBeneficio`): los requisitos que un ingresante presenta en Guaraní deben traducirse en un porcentaje de bonificación sobre el arancel.
- Habilitación para rendir examen en función de la deuda (`DeudaExamen`, `TesoreriaEstado`).
- Flujo preuniversitario completo: de un alumno en Guaraní a una chequera emitida y enviada.

> **Estado real al 2026-08-19:** el punto de *beneficios por requisitos* está **incompleto**. `GuaraniBeneficio` existe como módulo hexagonal con persistencia y API REST, pero **ningún otro componente del servicio lo consume**: `PreuniversitarioChequeraDetailsCreator.createCuotas()` y `SpoterService` (líneas 180-206) copian importes desde `LectivoCuota` sin aplicar bonificación alguna, con código casi idéntico entre sí.
>
> **Cerrar esa brecha en el camino preuniversitario es el trabajo activo** (ver `features/beneficio-requisitos-ingreso/spec.md`). El camino de recálculo por política arancelaria (`RecalculateCuotaByUniqueIndexUseCaseImpl`) y el mecanismo paralelo de `ArancelPorcentaje` quedan como deuda conocida y explícitamente fuera de alcance por ahora.

---

## 2. Lo que este servicio NO es

Delimitar importa tanto como declarar. El core **no**:

- Expone interfaz de usuario. No hay pantallas, no hay decisiones de UX acá.
- Es dueño del dato académico. Guaraní lo es; el core lo consume y lo refleja.
- Toma decisiones de negocio de política arancelaria por sí mismo. Ejecuta las políticas que tesorería define; no las inventa.
- Es un gateway de pagos. Integra con MercadoPago, pero el procesamiento del pago ocurre afuera.
- Reemplaza a `facultad-service` ni a `umhub`. Colabora con ellos vía REST.

---

## 3. Audiencia

El servicio no tiene usuarios directos, pero sí destinatarios claros. Cada decisión de diseño debe poder justificarse ante al menos uno de estos tres.

### 3.1 Otros microservicios del ecosistema UM — *audiencia primaria*

Consumidores directos de la API REST bajo `/api/tesoreria/core/**`:

- `facultad-service` (consumido en sentido inverso por 19 consumers `*FacultadConsumer` vía `FacultadUrlResolver`)
- `umhub` (campañas, reserva de vacante)
- Aplicaciones web y móviles de la universidad

**Qué significa para nosotros:** el contrato REST es un compromiso público. Romperlo es un evento *major* de SemVer, no un detalle de implementación — como ocurrió en la 4.0.0 al cambiar la respuesta de `POST /guarani/alumno/create/personales` y eliminar un endpoint. Las rutas duplicadas que existen (`/documento` junto a `/api/tesoreria/core/documento`) son deuda intencional para no romper clientes, y se mantienen hasta que se verifique que nadie las usa.

### 3.2 Personal de tesorería y administración de las facultades

No tocan la API, pero son la razón por la que existe casi toda la lógica de negocio: recálculo de cuotas vencidas, política arancelaria, emisión e impresión de chequeras, altas y habilitaciones manuales, conciliación.

**Qué significa para nosotros:** el sistema debe dejar lugar al criterio humano en vez de imponerse. Por eso existen el flag `manual` en `TesoreriaEstado` (habilitar a un alumno a rendir aunque tenga deuda) y `ChequeraCuota.manual`. Cuando una regla automática y una decisión manual chocan, **gana la decisión manual** — y esa regla vale como invariante de diseño.

### 3.3 El equipo de desarrollo UM-services

Quienes mantienen y extienden el core.

**Qué significa para nosotros:** invertir en tests, en documentación (diagramas Mermaid por módulo, OpenAPI) y en cerrar la migración hexagonal es trabajo legítimo del roadmap, con dueño explícito, y no un lujo que se posterga cuando aprieta el calendario.

---

## 4. Principios que gobiernan las decisiones

Cuando haya que elegir entre dos caminos, estos principios desempatan. Están ordenados: el de arriba gana.

1. **La corrección del dinero está por encima de todo.** Un importe mal calculado le cuesta plata a un alumno o a la universidad. Ante la duda entre rendimiento y exactitud, gana exactitud. Todo cálculo monetario usa `BigDecimal` con escala y redondeo explícitos — nunca `double`, nunca redondeo implícito.

2. **Ninguna regla de precios sin test.** Toda lógica que determine un importe, un descuento, un vencimiento o un estado de deuda entra acompañada de pruebas que cubran sus ramas. Es la regla que rescata al servicio de su hueco de cobertura actual, y no admite excepción "por ser un cambio chico".

3. **El dominio no sabe de infraestructura.** Los modelos de `domain/model` no conocen JPA, HTTP ni Jackson. Los casos de uso dependen de puertos, no de adaptadores. La arquitectura hexagonal existente es el estándar; el código legacy es la excepción a extinguir, no un patrón alternativo válido.

4. **El contrato REST es un compromiso.** Cambios compatibles → *minor*. Cambios que rompen un cliente → *major*, documentado en el README con la justificación verificada en código.

5. **El criterio humano prevalece sobre la automatización.** Las habilitaciones y ajustes manuales de tesorería no se pisan con recálculos automáticos.

6. **Verificar antes de afirmar.** La convención del README —cada nota de versión respaldada por `git diff`, código y `pom.xml`— se extiende a todo el proyecto. Nada se declara hecho sin evidencia.

---

## 5. Cómo medimos el éxito

| Dimensión | Cómo se ve el éxito |
|---|---|
| Corrección | Cero incidentes de importe o deuda mal calculados que lleguen a un alumno |
| Confianza | Cobertura real sobre los caminos de cálculo de dinero; hoy es el punto más débil |
| Contratos | Los servicios consumidores no se rompen entre versiones *minor* |
| Coherencia | Un solo modelo por entidad de negocio: hexagonal, sin gemelo legacy |
| Trazabilidad | Un beneficio, un descuento o un recálculo se puede explicar y reproducir a partir de los datos |

---

## 6. Documentos relacionados

- `tech-stack.md` — stack, restricciones y huecos declarados H1–H8
- `roadmap.md` — en qué orden avanza el servicio completo
- `features/beneficio-requisitos-ingreso/spec.md` — feature activa
- `../docs/*.mmd` — diagramas Mermaid por módulo hexagonal
- `../README.md` — historial de versiones verificado en código
