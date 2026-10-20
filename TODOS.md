# TODOS

## Contabilidad

### Validar la jerarquía al imputar movimientos

**What:** En `CreateCuentaMovimientoUseCaseImpl` y en la edición de movimientos, rechazar la imputación a una cuenta que tiene cuentas hijas (o es integradora).

**Why:** El alta de cuentas (#407) controla que el padre no tenga movimientos solo en ese momento; después nada impide imputar un asiento al padre. La FK `movcon → plancta` no cubre esta regla.

**Pros:** Cierra en Java el invariante "las cuentas con hijas no reciben movimientos".

**Cons:** Cambia el comportamiento de los asientos que hoy se aceptan; VB6 escribe `movcon` directo y sigue sin control.

**Context:** Surgió en la revisión técnica de #407 (2026-10-16), hallazgo O-3: `CreateCuentaMovimientoUseCaseImpl.java:17` guarda sin validar jerarquía. Empezar por `CuentaJerarquia` (rango de descendientes) del slice `contable/cuenta`, que agrega #407. Es cross-slice (`cuentaMovimiento` → `cuenta`).

**Effort:** M
**Priority:** P2
**Depends on:** #407 mergeado; acuerdo en la revisión sobre asientos existentes y VB6.

## Completed
