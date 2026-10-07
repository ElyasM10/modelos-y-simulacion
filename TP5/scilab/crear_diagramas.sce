// =====================================================================
// TP5 - Ejercicio 4: diagramas en bloques de los 6 sistemas en Scilab/Xcos
//
// Cada sistema  x' = a11*x + a12*y ,  y' = a21*x + a22*y  se arma como:
//   sumador -> integrador (1/s) -> salida, con un lazo de realimentacion
//   (caja de ganancia) por cada coeficiente no nulo.
//
// Este script (1) arma los diagramas por codigo, (2) los guarda como
// diagramas/sistema_X.zcos para abrirlos en Xcos, (3) los simula y
// (4) compara contra la solucion exacta x(t) = expm(A*t)*x0.
// Uso:  scilab -f crear_diagramas.sce      (o exec("crear_diagramas.sce") en la consola)
// =====================================================================
loadXcosLibs(); loadScicos();

DIR = get_absolute_file_path("crear_diagramas.sce");
mkdir(DIR + "diagramas"); mkdir(DIR + "respuestas");

X0 = [1; 2];      // condicion inicial (x0, y0) de todos los sistemas.
                  // No es autovector de ninguna matriz A (con [1;1], en (c) y (e) x(t) = y(t) y las curvas se pisan)
TF = 5;           // tiempo final de simulacion

// (nombre, a11, a12, a21, a22)
SISTEMAS = list( ..
  list("a",   1,   2,  -5,   2), ..
  list("b",  -2,   0,   0,  -3), ..
  list("c",  -3,   2,  -1,   0), ..
  list("d",  10, -18,   6, -11), ..
  list("e",   0,  -1,   1,  -2), ..
  list("f",   0,   1,   0,  -1));

// ------------------------- utilidades de bloques ---------------------
function blk = ubicar(blk, cx, cy, w, h)
  blk.graphics.sz = [w h];
  blk.graphics.orig = [cx - w/2, cy - h/2];     // Xcos: eje Y hacia abajo (coordenadas de pantalla)
endfunction

function blk = bloque_ganancia(g)
  blk = GAINBLK_f("define");
  blk.graphics.exprs = string(g);
  blk.model.rpar = g;
  // La senal de realimentacion fluye de derecha a izquierda: el bloque se espeja
  // (en Xcos: "Mirror"), asi la entrada queda a la derecha y la salida a la izquierda.
  blk.graphics.style = "GAINBLK_f;mirror=true";
endfunction

function blk = bloque_integrador(x0)
  blk = INTEGRAL_m("define");
  blk.graphics.exprs(1) = string(x0);
  blk.model.state = x0;
endfunction

function blk = bloque_suma(n)
  blk = BIGSOM_f("define");
  blk.graphics.exprs = sci2exp(ones(n,1));
  blk.model.rpar = ones(n,1);
  blk.model.in = -ones(n,1);
  blk.graphics.pin = zeros(n,1);
endfunction

function blk = bloque_split(n)
  blk = SPLIT_f("define");
  blk.model.out = -ones(n,1);
  blk.graphics.pout = zeros(n,1);
endfunction

function blk = bloque_tows(nombre)
  blk = TOWS_c("define");
  blk.graphics.exprs = ["1024"; nombre; "0"];
  blk.model.ipar = [1024; length(nombre); ascii(nombre)'];
endfunction

function blk = bloque_reloj(periodo)
  blk = SampleCLK("define");
  blk.graphics.exprs = [string(periodo); "0"];
  blk.model.rpar = [periodo; 0];
endfunction

function blk = bloque_split_eventos()
  blk = CLKSPLIT_f("define");
endfunction

function [scs_m, k] = agregar(scs_m, blk)
  k = length(scs_m.objs) + 1;
  scs_m.objs(k) = blk;
endfunction

function scs_m = enlazar(scs_m, de, pde, a, pa, xs, ys)
  lk = scicos_link(xx = xs(:), yy = ys(:), ct = [1, 1], from = [de, pde, 0], to = [a, pa, 1]);
  k = length(scs_m.objs) + 1;
  scs_m.objs(k) = lk;
  b = scs_m.objs(de); b.graphics.pout(pde) = k; scs_m.objs(de) = b;
  b = scs_m.objs(a);  b.graphics.pin(pa)  = k; scs_m.objs(a)  = b;
endfunction

function scs_m = enlazar_evento(scs_m, de, pde, a, pa, xs, ys)
  lk = scicos_link(xx = xs(:), yy = ys(:), ct = [5, -1], from = [de, pde, 0], to = [a, pa, 1]);
  k = length(scs_m.objs) + 1;
  scs_m.objs(k) = lk;
  b = scs_m.objs(de); b.graphics.peout(pde) = k; scs_m.objs(de) = b;
  b = scs_m.objs(a);  b.graphics.pein(pa)   = k; scs_m.objs(a)  = b;
endfunction

// ------------------------- armado de un sistema ----------------------
function scs_m = armar_sistema(nombre, a11, a12, a21, a22, x0, tf)

  scs_m = scicos_diagram();
  scs_m.props.tf = tf;
  scs_m.props.title = ["Sistema (" + nombre + ")"; ""];

  // posiciones en coordenadas de pantalla (Y hacia abajo)
  cyX = 80; cyY = 240;                       // renglon de x (arriba) y de y (abajo)
  cxS = 60; cxI = 170; cxSpX = 270; cxSpY = 300; cxW = 400; cxSc = 520;
  laneA11 = cyX - 55; laneA12 = cyX + 50;    // carriles de los 4 lazos posibles
  laneA21 = cyY - 50; laneA22 = cyY + 55;
  cyMid = (cyX + cyY) / 2;

  nInX = double(a11 <> 0) + double(a12 <> 0);   // entradas del sumador de x
  nInY = double(a21 <> 0) + double(a22 <> 0);   // entradas del sumador de y
  nOutX = double(a11 <> 0) + double(a21 <> 0) + 2;   // lazos que salen de x + TOWS + scope
  nOutY = double(a12 <> 0) + double(a22 <> 0) + 2;

  // --- bloques principales ---
  [scs_m, SX]  = agregar(scs_m, ubicar(bloque_suma(nInX), cxS, cyX, 30, 30));
  [scs_m, IX]  = agregar(scs_m, ubicar(bloque_integrador(x0(1)), cxI, cyX, 60, 40));
  [scs_m, SPX] = agregar(scs_m, ubicar(bloque_split(nOutX), cxSpX, cyX, 7, 7));
  [scs_m, WX]  = agregar(scs_m, ubicar(bloque_tows("xs"), cxW, cyX, 60, 30));
  [scs_m, SY]  = agregar(scs_m, ubicar(bloque_suma(nInY), cxS, cyY, 30, 30));
  [scs_m, IY]  = agregar(scs_m, ubicar(bloque_integrador(x0(2)), cxI, cyY, 60, 40));
  [scs_m, SPY] = agregar(scs_m, ubicar(bloque_split(nOutY), cxSpY, cyY, 7, 7));
  [scs_m, WY]  = agregar(scs_m, ubicar(bloque_tows("ys"), cxW, cyY, 60, 30));

  sc = CMSCOPE("define");
  sc.graphics.exprs(1) = "1 1";
  sc.graphics.exprs(2) = "3 5";            // x en verde, y en rojo
  sc.graphics.exprs(8) = string(tf) + " " + string(tf);
  [scs_m, SC] = agregar(scs_m, ubicar(sc, cxSc, cyMid, 40, 50));

  // --- sumador -> integrador -> split (cada renglon) ---
  scs_m = enlazar(scs_m, SX, 1, IX, 1, [cxS+15; cxI-30], [cyX; cyX]);
  scs_m = enlazar(scs_m, IX, 1, SPX, 1, [cxI+30; cxSpX], [cyX; cyX]);
  scs_m = enlazar(scs_m, SY, 1, IY, 1, [cxS+15; cxI-30], [cyY; cyY]);
  scs_m = enlazar(scs_m, IY, 1, SPY, 1, [cxI+30; cxSpY], [cyY; cyY]);

  // --- lazos de realimentacion (uno por coeficiente no nulo) ---
  // puertos de entrada de cada sumador: primero el lazo que llega por arriba, luego el de abajo
  pX = 0; pY = 0; oX = 0; oY = 0;
  yTopX = cyX - 7; yBotX = cyX + 7; yTopY = cyY - 7; yBotY = cyY + 7;

  // a11: x -> SX (por arriba)
  if a11 <> 0 then
    [scs_m, G] = agregar(scs_m, ubicar(bloque_ganancia(a11), cxI, laneA11, 50, 30));
    oX = oX + 1; pX = pX + 1;
    yp = cyX; if nInX == 2 then yp = yTopX; end
    scs_m = enlazar(scs_m, SPX, oX, G, 1, [cxSpX; cxSpX; cxI+25], [cyX; laneA11; laneA11]);
    scs_m = enlazar(scs_m, G, 1, SX, pX, [cxI-25; 25; 25; cxS-15], [laneA11; laneA11; yp; yp]);
  end
  // a12: y -> SX (por abajo)
  if a12 <> 0 then
    [scs_m, G] = agregar(scs_m, ubicar(bloque_ganancia(a12), cxI, laneA12, 50, 30));
    oY = oY + 1; pX = pX + 1;
    yp = cyX; if nInX == 2 then yp = yBotX; end
    scs_m = enlazar(scs_m, SPY, oY, G, 1, [cxSpY; cxSpY; cxI+25], [cyY; laneA12; laneA12]);
    scs_m = enlazar(scs_m, G, 1, SX, pX, [cxI-25; 25; 25; cxS-15], [laneA12; laneA12; yp; yp]);
  end
  // a21: x -> SY (por arriba)
  if a21 <> 0 then
    [scs_m, G] = agregar(scs_m, ubicar(bloque_ganancia(a21), cxI, laneA21, 50, 30));
    oX = oX + 1; pY = pY + 1;
    yp = cyY; if nInY == 2 then yp = yTopY; end
    scs_m = enlazar(scs_m, SPX, oX, G, 1, [cxSpX; cxSpX; cxI+25], [cyX; laneA21; laneA21]);
    scs_m = enlazar(scs_m, G, 1, SY, pY, [cxI-25; 25; 25; cxS-15], [laneA21; laneA21; yp; yp]);
  end
  // a22: y -> SY (por abajo)
  if a22 <> 0 then
    [scs_m, G] = agregar(scs_m, ubicar(bloque_ganancia(a22), cxI, laneA22, 50, 30));
    oY = oY + 1; pY = pY + 1;
    yp = cyY; if nInY == 2 then yp = yBotY; end
    scs_m = enlazar(scs_m, SPY, oY, G, 1, [cxSpY; cxSpY; cxI+25], [cyY; laneA22; laneA22]);
    scs_m = enlazar(scs_m, G, 1, SY, pY, [cxI-25; 25; 25; cxS-15], [laneA22; laneA22; yp; yp]);
  end

  // --- salidas: variable al workspace (TOWS_c) y osciloscopio ---
  oX = oX + 1; scs_m = enlazar(scs_m, SPX, oX, WX, 1, [cxSpX; cxW-30], [cyX; cyX]);
  oY = oY + 1; scs_m = enlazar(scs_m, SPY, oY, WY, 1, [cxSpY; cxW-30], [cyY; cyY]);
  oX = oX + 1; scs_m = enlazar(scs_m, SPX, oX, SC, 1, [cxSpX; 330; 330; cxSc-20], [cyX; cyX; cyMid-10; cyMid-10]);
  oY = oY + 1; scs_m = enlazar(scs_m, SPY, oY, SC, 2, [cxSpY; 330; 330; cxSc-20], [cyY; cyY; cyMid+10; cyMid+10]);

  // --- reloj de muestreo (TOWS_c y el osciloscopio son bloques dirigidos por eventos) ---
  [scs_m, CK]  = agregar(scs_m, ubicar(bloque_reloj(0.02), 470, 25, 30, 30));
  [scs_m, CS1] = agregar(scs_m, ubicar(bloque_split_eventos(), 470, 55, 7, 7));
  [scs_m, CS2] = agregar(scs_m, ubicar(bloque_split_eventos(), 470, 110, 7, 7));
  scs_m = enlazar_evento(scs_m, CK,  1, CS1, 1, [470; 470], [40; 55]);
  scs_m = enlazar_evento(scs_m, CS1, 1, WX,  1, [470; 400; 400], [55; 55; cyX-15]);
  scs_m = enlazar_evento(scs_m, CS1, 2, CS2, 1, [470; 470], [55; 110]);
  scs_m = enlazar_evento(scs_m, CS2, 1, SC,  1, [470; cxSc; cxSc], [110; 110; cyMid-25]);
  scs_m = enlazar_evento(scs_m, CS2, 2, WY,  1, [470; 470; 400; 400], [110; 200; 200; cyY-15]);
endfunction

// ------------------------- programa principal ------------------------
printf("\nTP5 - Ejercicio 4 en Scilab/Xcos\n");
printf("%-8s %-22s %-22s %s\n", "Sistema", "[x y] simulado", "[x y] exacto", "error relativo");

for i = 1:length(SISTEMAS)
  s = SISTEMAS(i);
  nombre = s(1); a11 = s(2); a12 = s(3); a21 = s(4); a22 = s(5);
  A = [a11 a12; a21 a22];

  scs_m = armar_sistema(nombre, a11, a12, a21, a22, X0, TF);

  // rango del osciloscopio segun la solucion exacta (para que se vea bien en Xcos)
  tt = linspace(0, TF, 200); sol = zeros(2, 200);
  for j = 1:200, sol(:, j) = expm(A * tt(j)) * X0; end
  for k = 1:length(scs_m.objs)
    o = scs_m.objs(k);
    if typeof(o) == "Block" & o.gui == "CMSCOPE" then
      lo = min(sol, "c"); hi = max(sol, "c"); mg = 0.1 * (hi - lo + 1);
      o.graphics.exprs(6) = strcat(string(lo - mg), " ");
      o.graphics.exprs(7) = strcat(string(hi + mg), " ");
      scs_m.objs(k) = o;
    end
  end

  // sincroniza los parametros internos de los bloques con lo escrito en exprs
  needcompile = 4; %cpr = list();
  [scs_m, %cpr, needcompile, ok] = do_eval(scs_m, %cpr, struct());

  archivo = DIR + "diagramas/sistema_" + nombre + ".zcos";
  xcosDiagramToScilab(archivo, scs_m);

  // simulacion desde el archivo guardado (sin ventanas)
  clear xs ys
  importXcosDiagram(archivo);
  scicos_simulate(scs_m, list(), "nw");
  tsim = xs.time; xv = xs.values; yv = ys.values;
  fin = [xv(size(xv, 1)); yv(size(yv, 1))];
  ex = expm(A * tsim($)) * X0;      // exacta en el ultimo instante muestreado
  printf("(%s)      [%9.4f %9.4f]   [%9.4f %9.4f]   %.2e\n", nombre, fin(1), fin(2), ex(1), ex(2), norm(fin - ex) / max(1, norm(ex)));

  // grafico de la respuesta simulada (x(t), y(t))
  clf(); plot(tsim, xv, "g-", tsim, yv, "r--");
  a = gca();
  for q = 1:size(a.children(1).children, "*"), a.children(1).children(q).thickness = 2; end
  xtitle("Sistema (" + nombre + "): respuesta simulada en Xcos", "t", "x(t), y(t)");
  legend(["x(t)"; "y(t)"], 2); xgrid();
  xs2png(gcf(), DIR + "respuestas/sistema_" + nombre + ".png");
end

printf("\nListo. Diagramas: %sdiagramas/ (abrir con Xcos)  Respuestas: %srespuestas/\n", DIR, DIR);
