# TP4 — Modelos y Simulación

Modelo insumo-producto de Leontief: cálculo del vector de producción total
y análisis del efecto de variaciones en la demanda final, para conjuntos de
2, 3, 4 y 5 sectores.

## Qué hace

Dado un modelo insumo-producto, se cumple `x = (I - A)^-1 · y`, donde `A` es
la matriz de coeficientes técnicos, `y` el vector de demanda final y `x` el
vector de producción total. Cuando la demanda final varía en `Δy`, el efecto
sobre la producción es `Δx = (I - A)^-1 · Δy`.

Todo el álgebra lineal (resolución de sistemas y producto matriz-vector) está
resuelta **sin librerías externas**: eliminación de Gauss con pivoteo parcial
y sustitución hacia atrás, escrita a mano igual que en TP2/TP3. Por cada
ejercicio se muestra además un gráfico de barras (uno por sector) con los
valores resultantes, en una ventana Swing que también se exporta a PNG en
`graficos/`.

También existe, a modo de comparación, una versión equivalente con
[Apache Commons Math](https://commons.apache.org/proper/commons-math/)
(`LUDecomposition` para `(I - A)·x = y`, `RealMatrix.operate` para aplicar una
inversa ya conocida) — está en `LeontiefApacheCommonsMath.java`, **comentada a
propósito**. Para probarla: descomentar esa clase y comentar/borrar
`Leontief.java` (no pueden coexistir, se llaman igual). La dependencia de
Apache Commons Math ya está declarada en el `pom.xml` para cuando haga falta.
Da exactamente los mismos resultados numéricos que la versión sin librería.

## Estructura

```
src/main/java/tp4/
├── Main.java                          # los 4 ejercicios: datos de cada uno y llamadas
├── algebra/
│   └── SistemaLineal.java             # A·x=b por Gauss con pivoteo parcial (sin librerías)
├── leontief/
│   ├── Leontief.java                  # ACTIVA: produccion() y aplicarInversa(), sin librerías
│   └── LeontiefApacheCommonsMath.java # alternativa con Apache Commons Math, comentada
├── reporte/
│   └── Reporte.java                   # impresión formateada de un vector por sector (consola)
└── grafico/
    ├── GraficoBarras.java             # dibuja un vector de valores por sector como barras
    └── VentanaGrafico.java            # ventana Swing + export a PNG
```

## Cómo correrlo

Requiere Java y Maven, y entorno gráfico (usa Swing).

```bash
mvn compile
mvn exec:java -Dexec.mainClass=tp4.Main
```

(o compilar y correr directo: `mvn compile && java -cp target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout) tp4.Main`)

Los resultados numéricos se imprimen por consola y los gráficos quedan en
`graficos/`. El programa queda a la espera hasta que se cierren las ventanas
abiertas.

## Ejercicios

| Ejercicio | Sectores | Dato de partida | Qué calcula |
|-----------|----------|------------------|-------------|
| 1 | Agricultura, Industria | `A`, `y` | `x = (I - A)^-1 · y` |
| 2 | Energía, Transporte, Manufactura | `(I - A)^-1` | Efecto de `Δy = +50` en Transporte sobre los tres sectores |
| 3 | Agricultura, Industria, Transporte, Servicios | `A`, `y` | `x = (I - A)^-1 · y` |
| 4 | Agricultura, Industria, Energía, Transporte, Servicios | `(I - A)^-1` | Efecto de `Δy = +100` en Energía sobre los cinco sectores |
