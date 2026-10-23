# TP6 — Modelos y Simulación

Modelo de Cole de la bioimpedancia:

```
Z(ω) = R∞ + (R0 − R∞) / (1 + (jωτ)^α)        τ = 1 / (2π·fc)        ω = 2π·f
```

> La primera versión del enunciado traía `1 − (jωτ)^α` (errata de tipeo). La versión corregida, *TP6 - MyS - Tipeo corregido - 2026*,
> usa `1 + (jωτ)^α` (la forma estándar del modelo de Cole), que es la que implementa este programa. Con el signo menos el
> denominador se anula cerca de ωτ = 1 y la impedancia no tendría sentido físico.

## Qué hace

Al ejecutarlo se abre una **aplicación gráfica** (Swing + [FlatLaf](https://www.formdev.com/flatlaf/)) con un menú
lateral para elegir el ejercicio, cada uno con su descripción. Todos los gráficos son interactivos
(rueda del mouse: zoom; clic derecho: guardar como PNG, copiar o imprimir).

- **Ejercicio 1**: implementa el modelo (`ModeloCole`, usa `Complex` de Apache Commons Math). El usuario ingresa
  R0, R∞, fc y α (o parte de un tejido de la Tabla 1), ve módulo, fase y Nyquist, y puede calcular Z en una
  frecuencia puntual. Valida los datos (R0 > R∞ ≥ 0, fc > 0, 0 < α ≤ 1).
- **Ejercicio 2**: los 5 tejidos de la Tabla 1 en un mismo espectro, en una tabla editable (cambiar valores, tildar o
  destildar tejidos, agregar o quitar) con rango de frecuencias a elección.
- **Ejercicio 3**: ajusta el modelo a registros reales de la base de Dell'Osa et al. (2 humanos y 2 fantomas de agar,
  ver `datos/README.md`) **o a un CSV propio** (`Abrir CSV propio…`; columnas: f [Hz], Re(Z) [Ω], Im(Z) [Ω]). El
  ajuste es por mínimos cuadrados no lineales (Levenberg-Marquardt de Commons Math, sobre la parte real e imaginaria a
  la vez, con 8 puntos de partida distintos) y se grafica medición vs. ajuste.

Para generar solo los PNG de los 3 ejercicios por consola (sin ventana), con los valores del enunciado, está `tp6.MainConsola`.

## Cómo usar la interfaz

Se abre con `mvn exec:java -Dexec.mainClass=tp6.Main` (desde la carpeta `TP6`). A la izquierda está el **menú**: un clic en
un ejercicio abre su pantalla (el elegido se marca en rojo). Abajo de cada gráfico hay una línea de estado: en verde
cuando todo salió bien y en rojo cuando algún dato no es válido (explica cuál).

### Ejercicio 1 — el modelo con tus parámetros
1. Elegí un **tejido de partida** de la lista (o dejá «Personalizado») para cargar sus valores de la Tabla 1.
2. Cambiá los campos que quieras: **R0** y **R∞** en Ω, **fc** en Hz, **α** entre 0 y 1, y el rango de frecuencias (**f mínima** y **f máxima**, en Hz).
   Se aceptan decimales con punto o coma y notación científica (`1e9`). Si editás un valor a mano, el tejido vuelve a «Personalizado».
3. Apretá **Graficar** (o Enter en cualquier campo). Las pestañas muestran el **módulo**, la **fase** y el **diagrama de Nyquist**.
4. En «Impedancia en una frecuencia» escribí una **f** y apretá **Calcular Z** para ver Z (parte real e imaginaria), el módulo y la fase en ese punto.

Para ver qué hace cada parámetro: cambiá **uno solo** por vez y volvé a graficar (por ejemplo, α = 1 da un semicírculo en Nyquist; cambiar fc no mueve el Nyquist).

### Ejercicio 2 — varios tejidos juntos
1. La tabla arranca con los 5 tejidos de la Tabla 1. **Doble clic** en un valor para editarlo (R0, R∞, fc, α o el nombre).
2. La casilla **Mostrar** decide qué tejidos se grafican.
3. **Agregar tejido** suma una fila nueva; **Quitar seleccionado** borra la fila marcada; **Restaurar Tabla 1** vuelve a los valores originales.
4. Elegí el rango de frecuencias (**f mín** y **f máx**) y apretá **Graficar**.

### Ejercicio 3 — ajuste a datos reales
1. Elegí un **registro** de la lista (2 humanos y 2 fantomas de agar de la base de Dell'Osa et al.): el ajuste se hace solo y se muestra
   a la izquierda con **R0, R∞, fc, α** y el **error medio** (en Ω; más chico = mejor ajuste).
2. Para usar **tu propio archivo**, apretá **Abrir CSV propio…** y elegilo. Formato: una fila por punto, con tres números separados por
   coma, punto y coma, tabulación o espacio: **frecuencia [Hz], Re(Z) [Ω], Im(Z) [Ω]**. La parte imaginaria tiene que ser **negativa**
   (comportamiento capacitivo); el encabezado se ignora y hacen falta al menos 5 filas con frecuencias positivas.
3. Las pestañas muestran **Nyquist**, **módulo** y **fase**, con la medición en puntos grises y el modelo ajustado en línea roja.

### En todos los gráficos
- **Rueda del mouse**: zoom. **Arrastrar** un recuadro: ampliar esa zona.
- **Clic derecho**: guardar como PNG, copiar, imprimir o volver al zoom original.

## Estructura

```
src/main/java/tp6/
├── Main.java                  # abre la aplicación gráfica
├── MainConsola.java           # modo consola: corre los 3 ejercicios y guarda los PNG en graficos/
├── modelo/                    # ModeloCole (Z(f)), AjusteCole (ajuste de parámetros), Tejido
├── datos/                     # Medicion (lee CSV), Registros (los 4 registros incluidos)
├── grafico/                   # Graficos y ConstructorGraficos (JFreeChart)
├── ejercicios/                # Ejercicio1..3: versión de consola
└── ui/                        # interfaz: VentanaPrincipal (menú), PanelEjercicio1..3 y ayudantes
src/main/resources/datos/      # CSV de las mediciones (incluidos en el programa)
datos/                         # README de los datos + exportar_datos.py (desde el .mat de Zenodo)
graficos/                      # PNG generados por la versión de consola
```

## Cómo correrlo

```bash
mvn compile
mvn exec:java -Dexec.mainClass=tp6.Main            # aplicación gráfica
mvn exec:java -Dexec.mainClass=tp6.MainConsola     # solo consola + PNG (desde la carpeta TP6)
```

## Resultados del ajuste (Ejercicio 3)

| Registro | R0 [Ω] | R∞ [Ω] | fc [Hz] | α | error medio [Ω] |
|----------|-------:|-------:|--------:|------:|------:|
| Humano, ID 1 | 211.94 | 125.03 | 44 993 | 0.564 | 1.70 |
| Humano, ID 401 | 272.91 | 177.89 | 39 868 | 0.610 | 1.51 |
| Fantoma de agar, ID 1311 | 148.60 | 67.34 | 16.6 | 0.683 | 2.56 |
| Fantoma de agar, ID 1401 | 71.18 | 58.11 | 45.7 | 0.588 | 0.83 |

El error medio es la raíz del error cuadrático medio entre medición y modelo (parte real e imaginaria juntas).
El registro 1311 es el que peor se ajusta: su arco medido es algo asimétrico y un único arco de Cole no lo
reproduce del todo (se ve en `graficos/Ejercicio3_agar_A_Nyquist.png`).
