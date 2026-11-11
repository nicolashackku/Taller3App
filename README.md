# Taller 3

Basado en los ejemplos de clase 09 (Mapas), 08 (Localización) y 11 (Firestore & Storage).

## Configuración antes de ejecutar
1. Crear un proyecto en Firebase, registrar la app con el paquete `com.example.taller3` y reemplazar `app/google-services.json` por el descargado.
2. Activar Authentication (correo/contraseña), Cloud Firestore y Storage.
3. Publicar las reglas de `server/firestore.rules` y `server/storage.rules`.
4. El token de Mapbox está en `app/src/main/res/values/mapbox_access_token.xml` (el del ejemplo de clase); se puede cambiar por uno propio.

## APK
`app/build/outputs/apk/debug/app-debug.apk` (`./gradlew assembleDebug`).
