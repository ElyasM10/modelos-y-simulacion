# TP3 — Modelos y Simulación

Ajuste de conjuntos de datos experimentales por cuadrados mínimos: recta,
exponencial, polinomios de grado 1 a 4, y la ley de potencia de la Tercera
Ley de Kepler.

## Qué hace

Todos los ajustes se resuelven planteando las ecuaciones normales de
cuadrados mínimos y resolviendo el sistema lineal resultante por eliminación
de Gauss con pivoteo parcial, implementado desde cero (sin librerías
externas de álgebra). Para los modelos no lineales (exponencial y potencial)
se aplica antes una linealización logarítmica. Para cada ajuste se calcula
además el coeficiente de correlación de Pearson (r) y se genera un gráfico
con los datos originales y la curva ajustada (ventana Swing + export a PNG
en `graficos/`).

## Estructura

```
src/main/java/tp3/
├── Main.java                     # los 4 ejercicios, con sus datos y llamadas
├── algebra/
│   └── SistemaLineal.java        # A·x=b por Gauss con pivoteo parcial
├── ajuste/
│   ├── CuadradosMinimos.java     # arma las ecuaciones normales (A, b) y ajusta
│   ├── Pearson.java              # coeficiente de correlación
│   └── FormatoEcuacion.java      # arma el string "f(x) = ..." de cada modelo
└── grafico/
    ├── Curva.java
    ├── GraficoAjuste.java        # dibuja los datos + la(s) curva(s) ajustada(s)
    └── VentanaGraficoAjuste.java # ventana Swing + export a PNG
```

## Cómo correrlo

Requiere Java y Maven.

```bash
mvn compile
mvn exec:java -Dexec.mainClass=tp3.Main
```

(o compilar y correr directo: `mvn compile && java -cp target/classes tp3.Main`)

Los resultados numéricos se imprimen por consola y los gráficos quedan en
`graficos/`.

## Ejercicios

| Ejercicio | Modelo | Método |
|-----------|--------|--------|
| 1 | Recta f(x)=a·x+b | Cuadrados mínimos directo, sobre 3 conjuntos de datos |
| 2 | Exponencial y=C·e^(A·x) | Linealización con ln(y), luego recta |
| 3 | Polinomios grado 1 a 4 | Cuadrados mínimos con matriz Σx^(i+j) |
| 4 | Potencial T=C·x^A (3ª Ley de Kepler) | Linealización con ln(x), ln(T), luego recta |

El desarrollo completo de cómo se llega a cada resultado (ecuaciones
normales explícitas, pasos de Gauss, cálculo de Pearson) está en
`Desarrollo-TP3.txt/pdf` y `TP3-Informe-Completo.pdf`, en la carpeta
`UNI-5TOANIO/` (un nivel arriba de este repo, no versionado con el código).
