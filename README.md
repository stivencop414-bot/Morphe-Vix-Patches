# Stiven Patches

Parches independientes para Ditu, ViX y TV Colombia Play, reunidos en un único bundle para Morphe. Este proyecto no está afiliado a las aplicaciones ni a Morphe y no distribuye APK ni XAPK.

## Compatibilidad

| App | Paquete | Versión compatible | Código | SHA-256 del certificado |
| --- | --- | --- | --- | --- |
| Ditu | `com.caracol.streaming` | `2.8.1` | `4731` | `6cc4605aa7b9ee71a34e0f8125a9050330563d795e87ae54b5c792868c188895` |
| ViX | `com.univision.prendetv` | `4.52.1_mobile` | `7902` | `eb6f3b76c6110bb945f88530ff5742634e48223c382931c319daceb431fe666d` |
| TV Colombia Play | `colombia.tv.plus` | `1015` | `20` | `58469bf5383c2348f5082835eef797d179d9f065cb8e64c409e0e7382967dce2` |

Las versiones y los certificados compatibles se comprueban al seleccionar la aplicación. Los parches modifican el comportamiento del cliente; no conceden suscripciones ni licencias DRM. Los anuncios integrados en la señal de un canal pueden seguir apareciendo y las superficies protegidas por Widevine pueden impedir las capturas.

## Parches

<!-- PATCHES_START -->
La lista completa se genera con cada publicación.
<!-- PATCHES_END -->

## Uso

En Morphe Manager 2.6.0 o posterior, añade como fuente `https://github.com/stivencop414-bot/Morphe-Vix-Patches` y selecciona una copia compatible del APK o XAPK obtenido por medios autorizados. Después elige los parches y genera el APK localmente. La validación de parches no sustituye una prueba de reproducción, inicio de sesión y estabilidad en un dispositivo.

## Compilación

Requiere JDK 21, Android SDK y dependencias de Morphe, incluido `morphe-patcher` 2.4.0. Ejecuta `./gradlew :patches:buildAndroid :patches:generatePatchesList --no-daemon`. La primera tarea incorpora `classes.dex` al bundle `.mpp`. Las publicaciones de la rama principal se generan con Semantic Release y GitHub Actions.

## Licencia y procedencia

[GPL-3.0](LICENSE), con las condiciones adicionales de [NOTICE](NOTICE). El código procede de los repositorios [Ditu](https://github.com/stivencop414-bot/Morphe-Ditu-Patches), [ViX](https://github.com/stivencop414-bot/Morphe-Vix-Patches) y [TV Colombia Play](https://github.com/stivencop414-bot/TV-Colombia-Play-Patches---for-Morphe).
