# TP6 — Modelos y Simulación

Modelo de Cole de la bioimpedancia:

```
Z(ω) = R∞ + (R0 − R∞) / (1 + (jωτ)^α)        τ = 1 / (2π·fc)        ω = 2π·f
```

> El enunciado escribe `1 − (jωτ)^α`; se implementó `1 + (jωτ)^α` (forma estándar del modelo de Cole):
> con el signo menos el denominador se anula cerca de ωτ = 1 y la impedancia no tendría sentido físico.

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
