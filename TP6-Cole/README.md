# TP6 - Modelo de Cole (Java + Maven, interfaz con JavaFX)

Un solo punto de entrada (`tp6.Main`). Para elegir el modo, comentá una de estas dos líneas en `Main.java`:

    ModoConsola.ejecutar();   // consola: imprime resultados y guarda los PNG en graficos/
    Ventana.abrir();          // interfaz gráfica JavaFX (una pestaña por ejercicio)

Requiere Java 21 (por ejemplo `sdk use java 21.0.7-tem`).

Ejecutar:

    mvn javafx:run

Estructura (`src/main/java/tp6/`). La consola y la interfaz son independientes: no se importan entre sí y
solo comparten el modelo matemático (`modelo/Cole`).

- `Main.java`                       elige consola o gui (comentando una línea)
- `modelo/Cole.java`                modelo de Cole, ajuste (método de Ayllon) y lectura de CSV
- `consola/`                        versión consola: `ModoConsola`, `Ejercicio1Consola`, `Ejercicio2Consola`,
                                    `Ejercicio3Consola` y `GraficoPng` (dibuja y guarda los PNG)
- `ui/`                             versión con interfaz gráfica (JavaFX)
    - `Ventana.java`                pestañas, una por ejercicio
    - `ejercicio1/`                 `PanelEjercicio1` + `FormularioParametros`
    - `ejercicio2/`                 `PanelEjercicio2`
    - `ejercicio3/`                 `PanelEjercicio3`
    - `componentes/`                `GraficoFx`, `Tarjeta`, `BarraResultado`
- `src/main/resources/estilos/estilo.css`  estilo de la interfaz
- `src/main/resources/datos/`       registros de Dell'Osa et al. (CSV: f [Hz], Re, Im)

Las imágenes de la consola se guardan en `graficos/` (se crea sola).
