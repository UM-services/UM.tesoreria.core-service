# Roadmap — UM.tesoreria.core-service

> Documento de constitución. Define **en qué orden avanza el servicio completo**.
> No es el plan de una feature: cada feature activa tiene su propia spec bajo `features/`.
>
> Versión del servicio al redactar: **4.1.1** · Fecha: **2026-08-19**

---

## Cómo se organiza el trabajo

```
specs/
  mission.md                          qué es el servicio y para quién
  tech-stack.md                       con qué está construido y qué huecos tiene (H1–H8)
  roadmap.md                          ESTE archivo: en qué orden avanzamos
  features/
    <nombre-feature>/
      spec.md                         la feature: contexto, criterios, fases, tests
      decisiones.md                   decisiones de negocio con su justificación
```

**Una feature entra al roadmap como una línea**, no como un plan desarrollado. El detalle vive en `features/<nombre>/spec.md`. Así el roadmap se lee de una sentada y no envejece cada vez que cambia una fase.

**Convención:** `[ ]` pendiente · `[~]` en curso · `[x]` hecho · `🔒` bloqueado.

---

## Ahora — trabajo activo

### R1 `[~]` Beneficio por requisitos de ingreso en cuotas preuniversitarias

**Spec:** `features/beneficio-requisitos-ingreso/spec.md` · **Decisiones:** `features/beneficio-requisitos-ingreso/decisiones.md`
· **Issues:** [#338 cálculo y persistencia](https://github.com/UM-services/UM.tesoreria.core-service/issues/338) → correo/PDF con beneficios

Los requisitos de ingreso que un aspirante presenta en Guaraní deben traducirse en un porcentaje de bonificación sobre el arancel. Hoy el porcentaje se puede cargar pero no llega a ningún importe.

Cierra parcialmente el hueco **H4** de `tech-stack.md`. Se trabaja en dos issues secuenciales:
primero F0–F4 y calidad de Core (cálculo); después F5/F7 y la integración documental con
sender-service (correo/PDF). El segundo no puede comenzar hasta que el cálculo esté desplegado.

**Bloqueo previo a F3:** verificar contra un alta real en staging si Guaraní popula `RequisitoPresentadoGuarani.requisitoRel`. Si viene nulo, el filtro de elegibilidad cambia de diseño.

---

## Después — sin fecha, ordenado por riesgo

Lo que sigue sale de los huecos declarados en `tech-stack.md` §6. **No está comprometido**: es la cola de la que se elige cuando R1 termine.

### R2 `[ ]` Extender la red de tests al resto del cálculo de dinero

**Hueco:** H1 — 19 archivos de test contra ~1476 de producción, y cero cobertura sobre los caminos que deciden cuánta plata debe un alumno.

R1 deja cubierto su propio camino y un umbral de JaCoCo acotado a tres paquetes. R2 extiende el mismo método a lo que quedó afuera: `ChequeraCuotaService`, `LectivoCuotaService`, `GetDeudaExamenUseCaseImpl`, `calculateDeuda` y su ejecución paralela con `CompletableFuture`, `calculateCodigoBarras`, `ChequeraTotal` y `ChequeraPago`.

**Por qué va primero de los pendientes:** contradice de frente el principio 2 de `mission.md` ("ninguna regla de precios sin test"), y sin esta red cualquier trabajo de R3 o R4 es una apuesta.

### R3 `[ ]` Esquema de base de datos versionado

**Hueco:** H3 — no hay Flyway ni Liquibase; el esquema MySQL vive fuera del repositorio y cada módulo nuevo implicó un `CREATE TABLE` manual.

Baseline del esquema actual, migración de los tests de H2 con `ddl-auto` a Flyway sobre un MySQL real, y regla de proceso: todo módulo nuevo trae su migración versionada.

**Va después de R2** porque migrar esquema sin tests que cubran la persistencia es apostar dos veces.

### R4 `[ ]` Cierre del legacy

**Hueco:** H2 — ~391 archivos Java legacy más 60 Kotlin conviviendo con 1085 hexagonales.

Un módulo por PR, de menor a mayor acoplamiento. El orden lo sugiere lo que hoy contamina código hexagonal: `ChequeraAlternativa` y `LectivoAlternativa` (Kotlin) y `LectivoTotal`, que `PreuniversitarioChequeraDetailsCreator` importa directamente. Después conciliación bancaria, impresión y reemplazos. Cierra con eliminar `core/kotlin/` y sacar Kotlin del `pom.xml`.

**Regla:** ningún módulo migra sin tests. Migrar sin red es cambiar deuda de lugar.

### R5 `[ ]` Coherencia de los mecanismos de descuento

**Hueco:** H4, la parte que R1 **no** cierra.

Quedan dos caminos de determinación de importes que no conocen beneficios: `RecalculateCuotaByUniqueIndexUseCaseImpl` (recálculo por política arancelaria) y `ArancelPorcentaje`, que sigue sin consumidores. Después de R1 el sistema tiene un descuento que se aplica al crear y se ignora al recalcular. Hay que unificarlo o declarar explícitamente que son mundos separados.

### R6 `[ ]` Robustez del código de barras

**Hueco:** H8 — `calculateCodigoBarras` genera códigos corruptos en silencio: diferencias negativas que solo se loguean en `debug`, sin control de desborde de los campos de 5 y 7 dígitos, y dos modos de redondeo distintos en la misma función.

R1 lo evita por construcción pero no lo corrige. Cualquier camino que modifique importes de a un tramo lo sigue exponiendo, en particular `RecalculateCuotaByUniqueIndexUseCaseImpl`, que escribe solo `importe3`.

### R7 `[ ]` Observabilidad de negocio

**Hueco:** H5 — hay Actuator y `micrometer-registry-prometheus`, pero no hay métricas propias ni tracing. El síntoma está en el historial: se agregan `log.debug` ad hoc para diagnosticar producción.

Métricas de negocio (recálculos ejecutados, beneficios aplicados, importe bonificado total), logging estructurado en reemplazo de los debug ad hoc, y alertas sobre fallos de la integración con Guaraní.

---

## Fuera del roadmap

| Tema | Motivo |
|---|---|
| Sistemas heredados en VB6 | Externo al servicio. Ver H6 |

---

## Documentos relacionados

- `mission.md` — propósito, audiencia y principios
- `tech-stack.md` — stack, restricciones y huecos declarados H1–H8
- `features/beneficio-requisitos-ingreso/spec.md` — feature activa
