# PadelMatch App (Android)

Esta es una app móvil (prototipo / desarrollo) basada en **Kotlin + Jetpack Compose** para gestionar y ver partidos de pádel (incluye la pantalla de “Mis partidos”).

## Pantallas principales

### Mis partidos (`MyMatchesScreen`)

Se muestra en la navegación inferior (tab **“Mis partidos”**) y contiene:

- Encabezado superior: `Mis Partidos` + subtítulo `Seguimiento de tu actividad de pádel`
- Tarjetas de estadísticas (3)
  - Partidos (`matchesPlayed`)
  - Valoración (`rating`)
  - Nivel (`level`)
- Pestañas (tabs)
  - **Próximos**: partidos con `status != completed` (implementación actual: `open` y `full`)
  - **Completados**: partidos con `status == completed`
- Tarjeta de partido (`MatchCard`)
  - Nombre del club / pista y dirección
  - Fecha / hora / duración
  - Personas (actual / máximo) y plazas restantes (`plaza(s) libre(s)`, con pluralización en español)
  - Visualización según estado: `Unirse al Partido` / `Completo` / `Finalizado`

> Nota: “Unirse al Partido” y “ver detalle” se dejan como placeholder con `Toast` ahora mismo. En el futuro hay que conectarlo con el backend y navegar a la pantalla de detalle.

## Stack tecnológico

- UI: Jetpack Compose, Material3
- Navegación: Navigation Compose
- Capa de red: Retrofit + Gson
- Localización: `strings.xml` + formateo de fecha en español

## Alineación con la estructura `matches` del backend

En el proyecto existen dos modelos relacionados con “matches”:

1. **Modelos para UI**: `com.example.padel.data.model.Match` / `Player` / `Court`
   - Su estructura coincide con el `mockData.ts` de Figma Make (por ejemplo: `courtId/date/time/durationMinutes/level/currentPlayers/status/organizer`)
2. **DTO de la capa de red**: `com.example.padel.model.Match`
   - Actualmente solo contiene `id: Int` (aunque `GET matches/` está definido, faltan los campos de respuesta en el DTO)

Estado actual:

- `MyMatchesScreen` ya está alineada con la estructura del prototipo para que la UI y la tarjeta se vean completas
- La respuesta real del backend aún no está mapeada completamente a `com.example.padel.model.Match`, así que el último paso para “conectar completamente con la estructura `matches` del backend” requiere completar el DTO y realizar el mapeo en la capa UI/Repository.

## API del backend (capa de red actual)

- Base URL: `http://10.0.2.2:8000/`
  - `10.0.2.2`: acceso a la máquina host desde un emulador Android
- Autenticación:
  - `POST auth/token`
- Partidos:
  - `GET matches/` (requiere header `Authorization`)
  - `POST matches/` (creación, body usando `MatchCreate`)

Código relacionado:

- `ApiService.kt`
- `RetrofitClient.kt`
- `SessionManager.kt`

## Localización (español)

Se han cambiado los textos de `MyMatchesScreen/MatchCard` a español (por ejemplo: `Mis Partidos`, `Próximos`, `Completados`, `Unirse al Partido`, etc.) y también se ha aplicado el formateo de fecha y la pluralización de “plazas libres”.

> Recomendación: migrar también los textos en inglés hardcodeados de `LoginScreen` a `strings.xml` para mantener una experiencia 100% consistente en español.

## Cómo ejecutar

1. Abre el proyecto con Android Studio y ejecuta (recomendado usar un emulador Android)
2. Inicia el servicio del backend (puerto `8000`)
3. Asegura que `BASE_URL` sea accesible: el emulador debe poder llegar a `10.0.2.2:8000`
4. Abre la app y entra a **“Mis partidos”** desde la navegación inferior

## TODO (recomendaciones a futuro)

- Completar `com.example.padel.model.Match` con los campos que devuelve el backend en `matches`
- Terminar el mapeo entre UI y DTO del backend (reemplazar `SampleMatchRepository` por un repository real)
- Implementar el flujo de red para “Unirse al Partido”
- Implementar pantallas de “Crear partido” y “Detalle del partido” con datos reales (no placeholders)

