# Gato Terror — Minecraft Bedrock

Addon en desarrollo para Minecraft Bedrock. El proyecto se mantiene aislado en esta carpeta.

## Incluido
- Behavior Pack y Resource Pack con manifiestos enlazados.
- Entidad invocable `gato:gato_terror`.
- 100 puntos de vida, 8 de daño, persecución y salto hacia el objetivo.
- Reglas de aparición natural en superficie, con poca luz y en biomas del Overworld.
- Modelo 3D inicial y controlador de renderizado.\n- Modelo fuente `cat_in_the_hat.glb` y conversor a geometría de cubos para Bedrock.
- GitHub Actions para validar la sintaxis JSON y empaquetar el archivo `.mcaddon`.\n- Flujo manual **Convert Gato Terror GLB to Bedrock** para convertir el modelo fuente, generar la textura y volver a empaquetar.

## Convertir el modelo 3D\n1. Abre la pestaña [Actions](https://github.com/chetosflaminhot2014-cmd/Prevee/actions).\n2. Selecciona **Convert Gato Terror GLB to Bedrock**.\n3. Pulsa **Run workflow** y espera a que termine.\n4. El flujo intenta guardar la geometría y textura generadas en el repositorio y publica un artefacto `.mcaddon`.\n\nLa conversión usa vóxeles/cubos para aproximar la malla original y conserva colores base de materiales; no conserva el rig ni las animaciones del GLB. Se debe revisar el resultado dentro de Minecraft antes de considerarlo terminado.\n\n## Instalar la compilación
1. Abre la pestaña Actions del repositorio.
2. Entra a la ejecución de **Build Gato Terror Minecraft Addon**.
3. Descarga el artefacto `Gato-Terror-mcaddon`.
4. Abre el archivo descargado con Minecraft Bedrock.
5. Activa Behavior Pack y Resource Pack en un mundo de prueba.
6. Prueba con `/summon gato:gato_terror`.

## Pendiente
- Textura personalizada `GatoTerrorRP/textures/entity/gato.png`.
- Animaciones y sonidos personalizados.
- Huevo de aparición con icono propio.
- Pruebas dentro de la versión exacta de Minecraft Bedrock.
- Ajustar la frecuencia de aparición tras probarlo en el juego.

La aparición natural y el comportamiento se han añadido al código, pero no se deben considerar verificados hasta que la compilación y una prueba dentro de Minecraft terminen correctamente.
