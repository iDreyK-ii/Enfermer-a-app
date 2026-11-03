#!/usr/bin/env python3
"""Authored educational content, generic vector geometry and synthetic cases.
This is not a clinical validation or a product instruction for use.
"""
from pathlib import Path
import json

HERE = Path(__file__).resolve().parent
BASE = dict(title='Base Maestra 2.1 · referencia editorial aportada', url='https://github.com/iDreyK-ii/Enfermer-a-app/blob/desarrollo/research/base_maestra_v21/README.md', publisher='Material editorial; revisión clínica pendiente', date='2026-11-03')
WHO = dict(title='OMS/HUG · Hand Hygiene: When and How', url='https://cdn.who.int/media/docs/default-source/integrated-health-services-(ihs)/infection-prevention-and-control/hand-hygiene-when-and-how-leaflet.pdf?sfvrsn=a92dc108_2', publisher='Folleto completo consultado; adaptación educativa propia, sin aval OMS', date='2026-11-03')
CDC = dict(title='CDC · Clinical Safety: Hand Hygiene for Healthcare Workers', url='https://www.cdc.gov/clean-hands/hcp/clinical-safety/', publisher='Secciones de recomendaciones generales y preparación quirúrgica consultadas', date='2026-11-03')
INSN = dict(title='INSN-SB · GP-028/INSN-SB/UE-V.01 · abril 2021', url='https://cdn.www.gob.pe/uploads/document/file/7682305/6505016-rd-n-000087-2021-dg-insnsb-028-gp-administracion-de-medicamentos-edv-vb.pdf', publisher='Cotejo de los diez correctos: p. 6; ámbito: p. 3. No revisión de todas las pautas.', date='2026-11-03')
GCS = dict(title='Glasgow Coma Scale · preguntas frecuentes', url='https://www.glasgowcomascale.org/faq/', publisher='Glasgow Coma Scale · secciones 1–3 consultadas en la fase de escalas', date='2026-11-02')

topics = []
def topic(id, title, area, level, tags, body, sources=None):
    topics.append(dict(id=id, title=title, area=area, level=level, tags=tags, markdown=body.strip(), sources=sources or [BASE], status='EDUCATIVO · REVISIÓN CLÍNICA PENDIENTE', author='Contenido educativo propio · iDreyK 360'))

topic('fundamentos', 'Fundamentos y aprendizaje por competencias', 'Fundamentos', 'Inicial', ['enfermería', 'seguridad', 'ética', 'competencias'], '''
# Aprender a cuidar con criterio
La biblioteca se organiza por temas y competencias, no por ciclos universitarios.

## Competencias transversales
- Observar y diferenciar datos de interpretaciones.
- Comunicar incertidumbre y pedir supervisión.
- Documentar fuentes, límites y decisiones del ejercicio.
- Respetar autonomía, privacidad y diversidad.

| Actividad | Evidencia de aprendizaje |
|---|---|
| Leer una ficha | Identificar fuente y estado de revisión |
| Resolver un cálculo | Explicar unidades y sustitución |
| Revisar un caso sintético | Justificar prioridades sin prescribir |

> Marcar una lectura no acredita competencia clínica. No introduzcas personas ni historias reales.
''')
topic('escalas-contexto', 'Escalas: población, versión y datos incompletos', 'Valoración', 'Inicial', ['Glasgow', 'Apgar', 'NRS', 'EVA', 'Braden', 'Norton', 'RASS'], '''
# Un puntaje necesita contexto
## Antes de utilizar un instrumento
1. Identifica versión, población, condiciones y permisos.
2. Distingue autoinforme, observación y transcripción de una ficha externa.
3. Completa los componentes aplicables; no conviertas automáticamente un dato ausente en cero.

| Instrumento | Límite que debes reconocer |
|---|---|
| Glasgow adulto | Un componente no valorable no equivale a 1; no generar total completo |
| NRS | Cero es una respuesta explícita; incapacidad de autoinforme es otra situación |
| Apgar | Ejercicio neonatal; no decide reanimación ni pronóstico individual |
| Braden / Norton / RASS | El registro externo no administra ni valida el instrumento |

La biblioteca conserva los instrumentos ya disponibles; una ficha documental no es una calculadora.
''', [BASE, GCS])
topic('cinco-momentos', 'Los cinco momentos para la higiene de manos', 'Bioseguridad', 'Inicial', ['OMS', 'manos', 'asepsia', 'procedimiento'], '''
# Cuándo realizar higiene de manos
1. Antes de tocar a la persona.
2. Antes de una tarea limpia o aséptica.
3. Después de riesgo de contacto con fluidos corporales.
4. Después de tocar a la persona.
5. Después de tocar su entorno inmediato.

## Momento y método son decisiones distintas
Los momentos indican cuándo actuar. Elegir agua y jabón o fricción depende de la situación, suciedad visible, producto y protocolo aplicable.

Los guantes no sustituyen la higiene. Revisa las indicaciones del establecimiento y la tolerancia de la piel.

> Resumen educativo propio del material OMS/HUG, complementado por CDC; no es una certificación de técnica.
''', [WHO, CDC])
topic('lavado-manos', 'Lavado con agua y jabón: recorrido educativo', 'Bioseguridad', 'Inicial', ['lavado', '40–60 segundos', 'agua', 'jabón', 'temporizador'], '''
# Lavado de manos
El folleto OMS/HUG indica **40–60 segundos para el procedimiento completo**, no solo para una zona de fricción.

## Secuencia de reconocimiento
- Humedecer y aplicar jabón suficiente para las superficies.
- Cubrir palmas, dorsos, espacios interdigitales, dorsos de dedos, pulgares y puntas de los dedos.
- Enjuagar y secar con material de un solo uso; evitar recontaminación al cerrar un grifo manual.

El temporizador reparte el tiempo de forma didáctica. No son tiempos oficiales por paso y terminarlo no demuestra que la técnica haya sido eficaz.

> Sigue formación supervisada y protocolo institucional; el dibujo no evalúa cobertura ni limpieza.
''', [WHO])
topic('friccion-manos', 'Fricción con preparado de base alcohólica', 'Bioseguridad', 'Inicial', ['fricción', '20–30 segundos', 'alcohol', 'higiene'], '''
# Fricción de manos
El folleto OMS/HUG describe **20–30 segundos para el procedimiento completo**.

Usa el producto conforme a su etiqueta. Cubre las superficies de las manos y deja secar; no interpretes un temporizador como prueba de antisepsia.

## Elección del método
El preparado alcohólico no reemplaza el lavado cuando las manos están visiblemente sucias. Las precauciones por microorganismo o brote se consultan en el protocolo vigente; no se deducen solo de esta animación.
''', [WHO, CDC])
topic('preparacion-quirurgica', 'Preparación quirúrgica de manos: límites del simulador', 'Bioseguridad', 'Intermedio', ['quirúrgica', 'fabricante', 'antisepsia', 'protocolo'], '''
# No existe un tiempo universal en esta app
La preparación quirúrgica depende del producto, su actividad, las instrucciones del fabricante y el protocolo institucional.

## Lo que debe comprobarse con supervisión
- Producto y método autorizados.
- Preparación de manos, uñas y antebrazos según protocolo.
- Tiempo indicado, secado y colocación de guantes estériles.
- Integridad de la piel, alergias y prevención de recontaminación.

No se reutiliza el cronómetro de lavado común como técnica quirúrgica. La app no ofrece un tutorial autónomo de preparación para una intervención.
''', [CDC])
topic('epp', 'Precauciones estándar y equipo de protección', 'Bioseguridad', 'Inicial', ['EPP', 'guantes', 'precauciones estándar', 'punzocortantes'], '''
# Protección según el riesgo de exposición
Las precauciones estándar se consideran en toda atención. El equipo se selecciona según la tarea y el riesgo, no como un uniforme idéntico para todos los procedimientos.

## Recorrido educativo
- Identifica posibles contactos con sangre, fluidos, mucosas, piel no íntegra o material contaminado.
- Consulta la selección de guantes, protección de ropa, ojos, cara y vía respiratoria aplicable.
- Aprende colocación y retiro con formación y protocolo; cambiar guantes no sustituye higiene de manos.
- Distingue residuos comunes, biocontaminados y punzocortantes conforme a la norma local.

> No hay una secuencia universal de EPP que la app certifique. Ante exposición accidental, activa el circuito institucional; no improvises tratamiento.
''', [BASE, CDC])
topic('medicacion-segura', 'Diez correctos institucionales y controles educativos', 'Bioseguridad', 'Intermedio', ['INSN-SB', 'correctos', '20 controles', 'medicación'], '''
# Dos listas diferentes
## Lista institucional
La guía **GP-028/INSN-SB/UE-V.01, abril de 2021**, enumera diez correctos en su página 6. Su ámbito descrito es la administración endovenosa a población neonatal y pediátrica en el INSN San Borja.

La aplicación conserva diez ítems: alergias y comprobación de otros medicamentos pertenecen al mismo noveno ítem de la fuente. No se presenta como una lista universal MINSA de veinte correctos.

## Lista educativa iDreyK
Los veinte controles propios ayudan a practicar revisión y comunicación. No son una norma oficial, ni acreditan que una prescripción o una administración sean seguras.

> Completar casillas nunca autoriza una administración. No se importan las pautas farmacológicas de la guía por haber cotejado su lista.
''', [INSN, BASE])
topic('jeringa-cateter', 'Jeringa, aguja y catéter: diferencias esenciales', 'Dispositivos', 'Inicial', ['atlas', 'jeringa', 'émbolo', 'pistón', 'aguja', 'catéter'], '''
# Reconocer no equivale a ejecutar una técnica
| Elemento | Distinción |
|---|---|
| Émbolo | Vástago móvil de la jeringa |
| Pistón | Sello que se desplaza dentro del cilindro |
| Aguja | Cánula metálica con punta y bisel |
| Catéter periférico | Cánula flexible, distinta de su aguja introductora |

El atlas muestra esquemas genéricos: los puertos, alas y mecanismos de seguridad dependen del modelo. No identifica calibres por color ni autoriza elegir un dispositivo.

Consulta el producto y sus instrucciones con supervisión. No se incluye una secuencia invasiva autónoma.
''')
topic('infusion-equipo', 'Equipo IV, bureta y llave de tres vías', 'Dispositivos', 'Intermedio', ['venoclisis', 'goteo', 'bureta', 'Volutrol', 'llave de tres vías'], '''
# El equipo modifica el contexto del cálculo
- El factor de goteo se lee en el equipo o en el enunciado; no se supone 20 ni 60 gotas/mL.
- Bureta y cámara de goteo no son la misma pieza.
- Puertos de acceso, filtros, ventilación y válvulas varían según fabricante.
- En una llave de tres vías no deduzcas el recorrido de fluidos a partir del dibujo genérico de la app.

## Relación dimensional
```formula
gotas/min = volumen (mL) × factor (gotas/mL) / tiempo (min)
```

El cálculo no demuestra compatibilidad, estabilidad, permeabilidad de una línea ni seguridad de la velocidad.
''')
topic('carro-paro', 'Coche de paro: organización y revisión supervisada', 'Cuidados críticos', 'Intermedio', ['coche de paro', 'cartuchera', 'emergencias', 'inventario'], '''
# Reconocimiento del área clínica
El contenido, distribución, reposición y control del coche de paro dependen de la institución, población atendida y protocolo vigente.

## Actividad sin pacientes
1. Identificar señalización, responsables y registro de revisión.
2. Diferenciar categorías de equipos, materiales y medicamentos.
3. Revisar integridad, caducidades y disponibilidad con la lista institucional.
4. Comunicar ausencias; no inventar stock ni sustituir productos.

> Esta ficha no es una lista de medicamentos para reanimación ni una autorización para usar un desfibrilador. Simulación avanzada y protocolos específicos están pendientes.
''')
topic('unidades', 'Aritmética dimensional y redondeo', 'Herramientas', 'Inicial', ['dosis', 'mg', 'g', 'mcg', 'mL', 'UI', 'mEq', 'conversiones'], '''
# Antes de sustituir números
## Conversiones de la misma dimensión
| Relación | Equivalencia matemática |
|---|---|
| Masa | 1 g = 1000 mg; 1 mg = 1000 mcg |
| Volumen | 1 L = 1000 mL |
| Tiempo | 1 h = 60 min |

UI, mEq y masa no se convierten entre sí sin información específica; la calculadora bloquea esa mezcla.

```formula
mL = cantidad del ejercicio / cantidad disponible × volumen disponible
```

Se muestran fórmula, sustitución, unidades y resultado. La operación utiliza decimales; el redondeo final es de presentación, no una pauta de dosificación.

> Todos los resultados son EJERCICIO EDUCATIVO, con cantidades ficticias. Los límites técnicos de entrada no son límites clínicos seguros.
''')
topic('dilucion-concepto', 'Dilución ideal no es reconstitución', 'Herramientas', 'Intermedio', ['C1V1', 'C2V2', 'dilución', 'reconstitución', 'volumen final'], '''
# Alcance de C1V1 = C2V2
```formula
V1 = C2 × V2 / C1
Volumen adicional ideal = V2 − V1
```

Requiere la misma sustancia, unidades compatibles y un modelo de mezcla apropiado con volumen aditivo. **V2 es el volumen final**, no el volumen añadido.

No se calcula reconstitución de polvo suponiendo que el volumen final es igual al diluyente añadido. Tampoco se deducen compatibilidad, estabilidad, osmolaridad ni concentración administrable de esta fórmula.

## Datos insuficientes
Si falta concentración, unidad, volumen final o información del producto, no hay un resultado clínico justificable. La app no rellena esos vacíos con una dosis inventada.
''')
topic('pae', 'PAE: datos, razonamiento y evaluación', 'PAE', 'Inicial', ['PAE', 'ADPIE', 'NANDA-I', 'NOC', 'NIC', 'diagnóstico'], '''
# Cinco etapas conectadas
1. Valorar los datos y su procedencia.
2. Formular problemas o hipótesis de enfermería con justificación.
3. Planificar resultados observables e intervenciones del ejercicio.
4. Documentar lo realizado en la simulación.
5. Comparar con lo esperado y revisar el plan.

| Dato | Ejemplo sintético |
|---|---|
| Subjetivo | La persona ficticia expresa cansancio |
| Objetivo | Se registra una observación simulada con método y momento |
| Fuente | Persona, observación, instrumento o documento ficticio |

Los campos de diagnóstico, NOC y NIC son texto propio del estudiante. No se incluyen taxonomías completas, códigos licenciados ni diagnósticos automáticos.
''')
topic('gordon-abcde', 'Gordon y ABCDE: marcos con finalidades diferentes', 'PAE', 'Intermedio', ['Gordon', 'ABCDE', 'valoración', 'patrones funcionales'], '''
# Elegir un marco y comunicar sus límites
## Gordon: organización de valoración
Percepción y manejo de salud; nutrición y metabolismo; eliminación; actividad y ejercicio; sueño y descanso; cognición y percepción; autopercepción; roles y relaciones; sexualidad y reproducción; adaptación al estrés; valores y creencias.

## ABCDE: reconocimiento prioritario
| Letra | Área de observación del ejercicio |
|---|---|
| A | Vía aérea |
| B | Respiración |
| C | Circulación |
| D | Estado neurológico |
| E | Exposición y entorno, preservando intimidad y temperatura |

El formulario no dirige reanimación ni sustituye una evaluación urgente. Frente a deterioro en un escenario, se practica pedir ayuda y comunicar al equipo según protocolo, no completar casillas antes de actuar.
''')
topic('soapie', 'SOAPIE: registro educativo trazable', 'PAE', 'Intermedio', ['SOAPIE', 'registro', 'subjetivo', 'objetivo', 'evaluación'], '''
# Una nota distingue lo observado de lo interpretado
| Campo | Qué documenta en el ejercicio |
|---|---|
| S | Datos subjetivos |
| O | Datos objetivos |
| A | Análisis y razonamiento del estudiante |
| P | Plan del ejercicio |
| I | Intervenciones realmente simuladas |
| E | Evaluación y respuesta observada en la simulación |

No registres acciones que no se hayan realizado. Identifica fuentes y momento; la exportación lleva la marca EJERCICIO EDUCATIVO y no es una historia clínica.
''')
topic('sbar', 'SBAR: comunicación breve y organizada', 'PAE', 'Inicial', ['SBAR', 'comunicación', 'situación', 'antecedentes'], '''
# Preparar una comunicación de práctica
- **S · Situación:** qué sucede y por qué comunicas.
- **B · Antecedentes:** información relevante, con fuente.
- **A · Valoración:** observaciones y preocupación, diferenciadas de conclusiones no confirmadas.
- **R · Solicitud/recomendación:** qué necesitas del equipo y qué debe aclararse.

No inventes una orden ni conviertas la solicitud en prescripción. En una comunicación real se aplica identificación y verificación institucional; en esta app se usan solo personajes sintéticos.
''')
topic('signos-balance', 'Signos y balance: interpretar límites del registro', 'Valoración', 'Inicial', ['signos vitales', 'tendencias', 'balance', 'ingresos', 'egresos'], '''
# Un registro aislado no reemplaza la valoración
Las tendencias de las fichas de práctica son ficticias y no reciben datos de sensores.

```formula
Balance del intervalo = ingresos registrados − egresos registrados
```

Se necesita el mismo intervalo y unidades consistentes. Cero puede ser un dato; vacío significa pendiente. El resultado no incluye pérdidas no registradas y no indica diuréticos, líquidos ni otras terapias.

Documenta método, momento y contexto. No atribuyas una interpretación clínica automática a colores o curvas del prototipo.
''')
topic('farmacologia-validacion', 'Inventario nominal y validación de medicamentos', 'Farmacología', 'Intermedio', ['DCI', 'DIGEMID', 'PNUME', 'inventario', 'altilice', 'validación'], '''
# Un nombre identificado no es una ficha validada
El inventario separa DCI nominales, combinaciones, formulaciones, biológicos, soluciones de soporte, nutrición y hemocomponentes. La clasificación editorial necesita revisión.

## Estados de trabajo
1. IDENTIFICADO.
2. PENDIENTE-FICHA.
3. FUENTE-ENCONTRADA.
4. VALIDADO-DIGEMID o VALIDADO-GPC, según evidencia y alcance.
5. REVISIÓN-FARMACÉUTICA.
6. PUBLICABLE, solo tras las comprobaciones correspondientes.

En esta entrega los nombres importados permanecen IDENTIFICADO. No se acredita stock, registro peruano ni revisión profesional. “altilice” sigue sin identificar; no se convierte en otro fármaco.

> Las concentraciones nominales citadas como formulación no son datos de producto comprobados. No se publican dosis, diluciones ni compatibilidades a partir de los anexos.
''')
topic('casos-razonamiento', 'Resolver casos sintéticos sin automatizar decisiones', 'Aprendizaje', 'Intermedio', ['casos', 'sepsis', 'TCE', 'HDA', 'ECG', 'rúbrica'], '''
# Recorrido progresivo
Lee los datos disponibles, responde una pregunta y revisa la justificación antes de avanzar. Los siete escenarios nuevos son sintéticos; no reconstruyen historias ni órdenes de los archivos mencionados en la base.

## Rúbrica educativa
- Identifica datos faltantes y límites del instrumento.
- Prioriza reconocimiento de deterioro y comunicación supervisada.
- Distingue datos, hipótesis y órdenes.
- Explica unidades y evita inferir dosis o tratamientos.

Las respuestas correctas se cuentan desde tus elecciones. No equivalen a un examen de habilitación ni a aprobación clínica del contenido.
''')

# Generic vector geometry: 600 × 380 coordinate system, labels remain live Compose text.
def shape(kind, *points):
    return dict(kind=kind, points=list(points))
def part(id, name, function, safety, x, y, *shapes):
    return dict(id=id, name=name, function=function, safety=safety, x=x, y=y, shapes=list(shapes))
def device(id, name, note, parts):
    return dict(id=id, name=name, area='Dispositivos', model_note=note, parts=parts, sources=[BASE])
L=lambda *p: shape('line',*p)
R=lambda *p: shape('rect',*p)
O=lambda *p: shape('oval',*p)
P=lambda *p: shape('poly',*p)
devices=[
 device('jeringa','Jeringa','Esquema de jeringa de tres piezas. Graduaciones simbólicas: no sirven para medir.',[
  part('cilindro','Cilindro o cuerpo','Contiene el volumen y permite observarlo.','La escala y capacidad se comprueban en el producto.',365,150,R(200,150,450,245)),
  part('embolo','Émbolo: vástago móvil','Transmite el movimiento al pistón.','No es sinónimo del sello o pistón de este modelo.',130,197,L(80,197,244,197),L(80,160,80,235)),
  part('piston','Pistón o sello','Sella y se desplaza dentro del cilindro.','Distinguir el sello del vástago; diseño dependiente del modelo.',249,230,R(240,155,258,240)),
  part('cono','Cono de conexión','Permite la conexión compatible prevista por el fabricante.','No inferir compatibilidad de conectores por este dibujo.',486,194,P(450,172,495,186,495,210,450,227)),
  part('alas','Aletas de apoyo','Proporcionan apoyo para los dedos.','No representan una técnica de manipulación estéril.',199,260,L(200,125,200,270)),
  part('escala','Graduaciones','Indican la escala propia del dispositivo.','Aquí son solo símbolos, sin capacidad o exactitud metrológica.',325,245,*[L(x,225,x,243) for x in (285,310,335,360,385,410)]),
 ]),
 device('aguja','Aguja','Componentes de una aguja genérica. No se representa calibre, longitud ni técnica de punción.',[
  part('pabellon','Pabellón o conector','Conecta la cánula al sistema compatible.','Verificar el tipo de conexión real.',140,195,R(80,165,200,225)),
  part('canula','Cánula metálica','Constituye el cuerpo de la aguja.','No confundir con el catéter flexible.',360,194,L(200,192,510,192),L(200,200,510,200)),
  part('bisel','Bisel','Terminación oblicua de la cánula.','Geometría ilustrativa; no muestra orientación para punción.',510,192,L(510,200,535,182)),
  part('punta','Punta','Extremo distal de la aguja.','Punzocortante: evitar exposición y seguir gestión institucional de residuos.',538,180,L(510,192,535,182)),
  part('protector','Protector, separado en el esquema','Protege antes del uso según diseño.','El dibujo no recomienda reencapuchar ni reutilizar.',310,300,R(190,280,430,320)),
 ]),
 device('estetoscopio','Estetoscopio','Modelo acústico ilustrativo de doble cara; otras cabezas y tubos son diferentes.',[
  part('olivas','Olivas','Contactan con los oídos del usuario.','Ajuste e higiene según fabricante.',130,65,O(110,50,150,80),O(440,50,480,80)),
  part('binaurales','Tubos binaurales','Conducen el sonido hacia las olivas.','No asumir orientación a partir de esta vista plana.',196,148,P(130,80,180,135,210,205),P(460,80,410,135,380,205)),
  part('tubo','Tubo flexible','Transmite el sonido desde la pieza torácica.','Revisar integridad y limpieza del material.',295,255,P(210,205,230,245,290,265,350,245,380,205),P(290,265,290,310,440,310,485,270)),
  part('cabeza','Pieza torácica','Aloja las superficies de auscultación.','La elección de cara depende del modelo y la formación.',487,225,O(440,180,540,280)),
  part('diafragma','Diafragma','Superficie de captación en este modelo.','No demuestra interpretación de ruidos.',488,205,O(450,190,530,270)),
  part('campana','Campana, según modelo','Segunda superficie de la cabeza de doble cara, mostrada aparte.','No todos los modelos tienen campana independiente.',135,290,O(100,255,170,325)),
 ]),
 device('tensiometro','Tensiómetro aneroide','Esquema manual. Tamaño de manguito, verificación y técnica requieren formación; no es un monitor digital.',[
  part('manguito','Manguito','Envuelve la zona de medición.','Elegir el tamaño por especificaciones, no por este dibujo.',176,76,R(60,70,295,195)),
  part('vejiga','Vejiga inflable interior','Recibe aire dentro del manguito.','Capa didáctica visible; disposición real según modelo.',177,140,R(95,105,260,165)),
  part('tubos','Tubos','Conectan manguito, pera y manómetro.','Buscar integridad según protocolo; no interpretar presión en el esquema.',370,210,P(295,155,360,155,360,285,445,285),P(295,112,395,112,395,70,460,70)),
  part('pera','Pera de insuflación','Permite introducir aire en el circuito.','No se indican presión o velocidad de inflado.',485,288,O(445,250,535,325)),
  part('valvula','Válvula de control','Controla la salida de aire en el modelo manual.','Mecanismo y mantenimiento dependen del fabricante.',430,285,R(410,272,445,298)),
  part('manometro','Manómetro','Muestra presión en el dispositivo real.','Dial simbólico, sin calibración ni lectura.',503,70,O(460,25,550,115),L(505,70,525,48)),
 ]),
 device('cateter-periferico','Catéter venoso periférico','Modelo genérico con aguja introductora. Alas, puerto y protección varían; no representa inserción.',[
  part('cateter','Catéter: cánula flexible','Es la cánula flexible del dispositivo.','Es distinta de la aguja metálica; no se indica uso, calibre ni tiempo de permanencia.',407,181,R(295,179,495,201)),
  part('aguja-introductora','Aguja introductora metálica','Acompaña al catéter en este diseño.','No es el catéter; no se representa su manipulación o reinserción.',525,192,L(220,190,525,190),L(520,190,533,182)),
  part('conexion','Conector o cubo','Conecta con el sistema previsto.','Verificar integridad y compatibilidad del modelo.',271,191,R(215,165,295,215)),
  part('camara','Cámara de visualización, según modelo','Permite observar el retorno en determinados diseños.','No equivale a comprobar por sí sola la seguridad del acceso.',156,192,R(105,170,215,210)),
  part('alas','Alas, según modelo','Superficies laterales del cuerpo.','No todos los modelos las incluyen.',255,251,P(220,215,215,270,290,270,285,215)),
  part('puerto','Puerto adicional, según modelo','Acceso adicional presente en ciertos diseños.','No asumir su presencia, esterilidad o forma de uso.',256,133,R(240,115,275,165)),
 ]),
 device('equipo-iv','Equipo de venoclisis','Equipo genérico por gravedad. Factor de goteo, filtros y puertos deben comprobarse en su etiqueta.',[
  part('punzon','Punzón de conexión','Conecta al recipiente compatible previsto.','No representa una aguja para punción del paciente.',110,50,P(98,80,105,25,115,25,122,80)),
  part('camara','Cámara de goteo','Permite observar el goteo en este diseño.','No se deduce el factor de goteo por el tamaño dibujado.',110,135,R(80,80,140,180)),
  part('tubo','Tubo de conducción','Conduce el fluido por el sistema.','No se representa purgado, compatibilidad ni conexión clínica.',174,270,P(110,180,110,270,260,270,330,215,465,240,495,300)),
  part('regulador','Regulador de rodillo','Elemento mecánico de control del equipo.','Su posición dibujada no indica una velocidad real.',253,270,R(232,245,275,292),O(240,251,267,278)),
  part('puerto','Puerto de acceso, según modelo','Acceso adicional del sistema.','El diseño y método de acceso dependen del fabricante.',347,211,R(330,192,365,230)),
  part('conector','Conector distal','Conexión final prevista en el equipo.','Verificar compatibilidad; no se muestra una conexión al paciente.',495,322,R(480,300,512,343)),
 ]),
 device('bureta','Bureta / equipo tipo Volutrol','Bureta volumétrica genérica; Volutrol se conserva como término de estudio. No se representa un producto certificado.',[
  part('entrada','Línea de entrada','Conduce hacia la cámara graduada.','Método de llenado y volumen permitido según fabricante.',240,30,L(240,15,240,70)),
  part('pinza','Pinza de entrada','Control mecánico de la línea en este esquema.','Posición ilustrativa; no demuestra un circuito abierto o cerrado.',240,51,R(225,35,255,62)),
  part('bureta','Cámara graduada o bureta','Recipiente graduado del sistema.','No confundir con la cámara de goteo. Escala simbólica sin volumen medible.',180,165,R(180,70,300,255),*[L(270,y,295,y) for y in (100,125,150,175,200,225)]),
  part('ventilacion','Ventilación/filtro, según modelo','Componente de determinados diseños.','No abrir, cerrar ni sustituir basándose en este esquema.',324,108,P(300,98,345,98,345,115,300,115)),
  part('camara-goteo','Cámara de goteo','Zona de observación inferior en el modelo ilustrado.','No identifica un factor de gotas/mL.',240,281,R(218,255,262,305)),
  part('salida','Línea de salida y conexión','Conduce desde el conjunto hacia el sistema previsto.','No sustituye comprobación de conexiones y control de la infusión.',415,330,P(240,305,240,330,460,330),R(460,317,500,344)),
 ]),
 device('llave-tres-vias','Llave de tres vías','Vista esquemática: la posición dibujada NO indica qué trayectos están abiertos. Consultar marcas e instrucciones del producto.',[
  part('cuerpo','Cuerpo central','Aloja el mecanismo del dispositivo.','Integridad y compatibilidad se verifican en el producto.',300,185,O(245,130,355,240)),
  part('selector','Mando selector','Permite modificar las conexiones según el diseño real.','No deducir dirección de flujo de este dibujo.',274,125,P(300,185,255,110,275,100,320,175)),
  part('puerto-1','Conexión lateral 1','Uno de los tres accesos.','Conexión y tapón dependen del modelo; no es una indicación de uso.',145,185,R(95,168,245,202)),
  part('puerto-2','Conexión lateral 2','Segundo acceso del conjunto.','No asumir compatibilidad por la apariencia.',455,185,R(355,168,505,202)),
  part('puerto-3','Conexión inferior 3','Tercer acceso del conjunto.','No representa una vía del paciente ni una conexión permitida.',300,304,R(282,240,318,330)),
 ])]


def q(scene, prompt, options, correct, feedback, rubric):
    return dict(scene=scene, prompt=prompt, options=options, correct=correct, feedback=feedback, rubric=rubric)
def case(id, title, area, intro, questions):
    return dict(id=id, title=title, area=area, intro=intro, questions=questions, sources=[BASE], status='SINTÉTICO · REVISIÓN DOCENTE PENDIENTE')
cases=[
 case('diabetes-itu-hta','Diabetes, ITU e hipertensión','Medicina interna','Persona ficticia A, adulta. El enunciado reúne antecedentes de diabetes e hipertensión y una consulta por síntomas urinarios. No se confirma un diagnóstico ni se prescribe.',[
  q('Expresa ardor al orinar. En una hoja simulada figura temperatura 38,1 °C, sin hora de registro.','¿Qué conviene documentar primero con claridad?',['Separar relato, medición y fuente; aclarar el momento','Escribir que la infección ya está confirmada','Añadir una glucemia no medida'],0,'El síntoma referido, la medición y la fuente son datos diferentes. Falta tiempo y contexto; no deben inventarse resultados.','Distingue dato subjetivo, objetivo y pendiente.'),
  q('Otra hoja contiene una orden de insulina sin tipo ni concentración legibles.','¿Puede la app completar esa información?',['Sí, usar la presentación más frecuente','No; señalar la ambigüedad y solicitar aclaración supervisada','Sí, convertir UI en mL sin concentración'],1,'Un nombre incompleto no permite deducir tipo, concentración ni pauta. Este caso no incluye una orden calculable.','Reconoce información insuficiente y no fabrica una dosis.'),
  q('Se prepara una entrega de turno ficticia.','¿Qué comunicación es más trazable?',['Me parece que todo está bien','Copiar la orden incompleta como verificada','Usar SBAR, describir datos y pendientes, y solicitar aclaración'],2,'Una comunicación estructurada diferencia antecedentes, observaciones y solicitud. No convierte una hipótesis en diagnóstico.','Comunica incertidumbre de forma explícita.'),
 ]),
 case('tce-uci','TCE y observación en UCI','Cuidados críticos','Escenario ficticio de una persona adulta con traumatismo craneoencefálico, intubación y sedación documentadas en el enunciado. No se indica ajustar tratamiento.',[
  q('La respuesta verbal no puede explorarse por intubación.','¿Cómo se representa en Glasgow?',['Asignar V=1 para obtener total','Señalar no valorable y no generar un total completo','Asignar V=5 porque no puede hablar'],1,'No valorable no equivale a ausencia de respuesta. Se conservan los componentes disponibles y el motivo; la sedación también limita la valoración.','Reconoce un componente no valorable.'),
  q('Se compara con un registro anterior realizado en otras condiciones de sedación.','¿Qué evita una falsa comparación?',['Sumar los dos totales','Ignorar los tratamientos que interfieren','Documentar condiciones, momento y componentes comparables'],2,'Los números sin condiciones de observación pueden ser engañosos. El ejercicio no infiere evolución neurológica solo por una suma.','Explicita factores de confusión.'),
  q('El enunciado agrega un deterioro observado respecto del estado previo.','¿Cuál es la prioridad educativa?',['Comunicar de inmediato y activar la evaluación supervisada prevista','Completar todas las casillas antes de pedir ayuda','Modificar por cuenta propia la infusión'],0,'Ante deterioro se practica reconocimiento y comunicación oportuna según el protocolo del centro. La app no dirige reanimación ni titula medicamentos.','Prioriza ayuda y valoración sin prescripción autónoma.'),
 ]),
 case('hda','Hemorragia digestiva alta','Emergencias','Caso sintético adulto con relato de vómito con sangre, palidez y mareo. Los datos sirven para aprender reconocimiento de riesgo y registro, no para decidir una terapia.',[
  q('El nuevo registro simulado muestra PA 92/58 mmHg y frecuencia 116/min.','¿Qué razonamiento es prudente?',['Tratarlo como un dato sin importancia','Reconocer posible deterioro y solicitar valoración inmediata del equipo','Elegir un expansor y una dosis desde el inventario'],1,'La combinación de síntomas y observaciones exige reconocer riesgo y comunicar. El ejercicio no confirma una causa ni selecciona fluidos o fármacos.','Prioriza reconocimiento de inestabilidad.'),
  q('En el inventario aparece concentrado de hematíes.','¿Cómo se clasifica?',['Como hemocomponente, no como una DCI farmacológica','Como cualquier antibiótico','Como suplemento alimentario'],0,'El inventario distingue hemocomponentes, medicamentos y nutrición. Esta clasificación no autoriza una transfusión ni establece sus requisitos.','Diferencia categorías de productos.'),
  q('El estudiante redacta la evaluación del PAE.','¿Qué debe registrar?',['Una mejoría no observada para cerrar el plan','Una orden histórica como recomendación universal','Datos observados, acciones simuladas y respuesta realmente disponible'],2,'El registro debe seguir lo que ofrece el caso. Si falta una respuesta o un dato, permanece pendiente.','No inventa evolución ni resultados.'),
 ]),
 case('pancreatitis','Pancreatitis: dolor y balance','Medicina interna','Escenario sintético adulto con pancreatitis mencionada en el enunciado. Se practica dolor, balance y formulación de un objetivo del PAE, sin pautas de tratamiento.',[
  q('La persona puede autoinformar y elige 7 en una escala numérica 0–10.','¿Qué se registra?',['EVA de 7 mm como si se hubiera medido','NRS 7/10, contexto y momento','Un dolor observado, sin considerar su respuesta'],1,'NRS es autoinforme numérico. No se sustituye por una medida analógica en milímetros ni por otra escala.','Identifica el instrumento realmente utilizado.'),
  q('En el mismo intervalo ficticio se registran 1200 mL de ingresos y 900 mL de egresos.','¿Qué significa la resta?',['300 mL de balance registrado; no implica por sí solo tratamiento','300 mL que deben eliminarse con un medicamento','Un diagnóstico automático de sobrecarga'],0,'1200 − 900 = 300 mL. El resultado solo reúne los registros del intervalo; no incluye pérdidas no consignadas ni prescribe conductas.','Separa cálculo e interpretación clínica.'),
  q('Se escribe un resultado esperado del ejercicio.','¿Qué redacción permite evaluar?',['Estará bien','Se curará por completo en una hora','Describir un indicador, un momento y cómo se comparará con el dato inicial'],2,'Un resultado del plan necesita criterios observables y plazo del ejercicio. No se promete curación ni se copia una taxonomía licenciada.','Propone evaluación observable con texto propio.'),
 ]),
 case('neumonia-asma','Neumonía / asma: observación respiratoria','Respiratorio','Escenario ficticio adulto con antecedente de asma y síntomas respiratorios. La actividad no distingue diagnósticos por sí sola ni prescribe oxígeno o broncodilatadores.',[
  q('Se observa mayor esfuerzo respiratorio y el monitor simulado muestra una lectura baja de saturación.','¿Qué evita depender solo del dispositivo?',['Ignorar a la persona y repetir el número','Considerar clínica y calidad de señal, y pedir valoración oportuna','Elegir una dosis a partir del color del monitor'],1,'Una señal puede ser inexacta, pero un deterioro observado requiere atención y comunicación; verificar no debe servir para demorar ayuda.','Integra observación y límites de medición.'),
  q('El estudiante revisa la letra B de ABCDE.','¿Qué área está organizando?',['Respiración','Balance del día','Búsqueda de fármacos por marca'],0,'B organiza la observación de la respiración. El formulario es un marco de aprendizaje y no una guía autónoma de intervención.','Reconoce la finalidad del marco.'),
  q('En SBAR falta un antecedente de alergias en el caso.','¿Qué es correcto?',['Escribir sin alergias para completar','Copiar datos de otro caso','Señalar no disponible y solicitar verificación'],2,'Dato ausente no significa ausencia de alergia. La documentación debe conservar esa incertidumbre.','No transforma un vacío en una negación.'),
 ]),
 case('sepsis-shock','Sepsis / shock: reconocimiento y comunicación','Emergencias','Simulación adulta con infección sospechada, cambio del estado mental y deterioro circulatorio en el relato. No diagnostica sepsis ni implementa un protocolo terapéutico.',[
  q('El estado de la persona ficticia empeora entre dos observaciones.','¿Qué se practica primero?',['Esperar a completar la biblioteca','Reconocer el deterioro y activar ayuda según el escenario supervisado','Obtener un puntaje para autorizar el tratamiento'],1,'La observación de deterioro necesita comunicación y evaluación oportuna. Un puntaje no es requisito para pedir ayuda.','Prioriza reconocimiento y escalamiento.'),
  q('Una infusión de la hoja de práctica omite concentración y unidad de velocidad.','¿Se puede calcular con seguridad?',['Sí, suponiendo la concentración habitual','Sí, con el peso solamente','No; faltan datos y se solicita una orden completa y legible'],2,'El sistema bloquea entradas incompletas o dimensionalmente incompatibles. No reconstruye prescripciones históricas.','Identifica límites matemáticos y documentales.'),
  q('Se solicita un SBAR sintético.','¿Qué es más útil?',['Presentar situación, antecedentes relevantes, observaciones y solicitud concreta','Listar marcas de medicamentos sin contexto','Afirmar un diagnóstico definitivo que el caso no confirma'],0,'La comunicación precisa describe lo observado y lo que debe evaluarse. No inventa resultados ni sustituye juicio profesional.','Comunica sin sobrediagnosticar.'),
 ]),
 case('ecg','ECG: señal, artefacto y límites','Cardiovascular','Caso sintético sobre un trazado ilustrativo. La app no recibe electrocardiogramas reales, no identifica arritmias ni propone tratamientos.',[
  q('La línea animada del módulo cambia al moverse un sensor ficticio del relato.','¿Qué debe reconocerse?',['La animación demuestra una arritmia','Artefacto y señal son posibilidades que requieren verificación contextual','La línea indica qué fármaco administrar'],1,'Una animación es una representación didáctica. Artefactos, conexiones y contexto requieren revisión; no se emite un diagnóstico desde el dibujo.','Distingue señal ilustrativa y dato clínico.'),
  q('No hay velocidad, calibración ni derivación documentadas.','¿Qué queda limitado?',['La interpretación y las mediciones del trazado','Solo el color de la imagen','Nada: cualquier dibujo sirve como ECG clínico'],0,'Sin condiciones del registro no se justifican mediciones ni interpretación clínica. La animación no está calibrada como un ECG.','Identifica metadatos esenciales ausentes.'),
  q('El relato incorpora pérdida súbita de respuesta.','¿Qué evita una demora peligrosa?',['Medir todos los intervalos del dibujo antes de avisar','Buscar una escala avanzada','Pedir ayuda y activar el procedimiento institucional con personal capacitado'],2,'El estado de la persona tiene prioridad sobre analizar una ilustración. El módulo no enseña una reanimación autónoma.','Prioriza reconocimiento y ayuda, no diagnóstico automático.'),
 ])]

insn_items=[
'Paciente correcto.', 'Medicamento correcto.', 'Dosis correcta.', 'Vía de administración correcta.', 'Horario correcto.',
'Verificar fecha de caducidad.', 'Registrar todos los medicamentos administrados.',
'Informar e instruir al paciente sobre medicamentos que está recibiendo.',
'Investigar si el paciente padece de alergias a algún medicamento. Comprobar que el paciente no este ingiriendo ningún otro medicamento que no esté prescrito.',
'Antes de preparar y administrar un medicamento realiza el lavado de manos.'
]
own_items=[
'Identificación ficticia y concordancia del enunciado', 'Orden legible, completa y vigente en el ejercicio',
'Diferenciar dato ausente, negación e incertidumbre', 'Alergias y antecedentes que deben verificarse',
'Nombre del principio activo y producto exacto por confirmar', 'Presentación, concentración y unidad documentadas',
'Cantidad indicada: no inventar ni inferir una dosis', 'Vía escrita y coherente, sin asumir compatibilidad',
'Horario, intervalo y duración explícitos', 'Cálculo dimensional y comprobación independiente del ejercicio',
'Peso, edad y población cuando sean pertinentes', 'Integridad, conservación y caducidad por verificar',
'Reconstitución: fuente del producto y volumen final conocidos', 'Diluyente y compatibilidad: no asumir por ausencia de datos',
'Velocidad, equipo y factor de goteo cuando correspondan', 'Riesgos, interacciones y duplicidades por revisar con el equipo',
'Higiene de manos, entorno y precauciones aplicables', 'Información, comprensión y participación de la persona ficticia',
'Registro de lo realmente simulado y respuesta observada', 'Comunicación de discrepancias y consulta de fuentes/protocolo'
]
checklists=[
 dict(id='insnsb-10',title='Diez correctos · INSN San Borja',scope='Guía institucional GP-028/INSN-SB/UE-V.01, abril 2021, p. 6. Ámbito neonatal/pediátrico e intravenoso descrito en p. 3. Cita de diez ítems, no lista universal MINSA.', official=True,items=insn_items,sources=[INSN]),
 dict(id='idreyk-20',title='Veinte controles educativos · iDreyK',scope='Lista propia para práctica. No es una norma oficial MINSA/OMS ni un protocolo institucional. Marcar casillas no autoriza administrar.',official=False,items=own_items,sources=[BASE])]

modules=[
 ('biblioteca','Biblioteca Markdown','HECHO','Lectura estructurada, filtros, búsqueda y progreso local; conjunto inicial, no toda una carrera.'),
 ('herramientas','Herramientas matemáticas','HECHO','Decimales y unidades para ejercicios. No validan dosis, tratamientos o rangos clínicos.'),
 ('atlas','Atlas vectorial','PARCIAL','Ocho dispositivos genéricos interactivos; cotejo de modelos de fabricante y revisión experta pendientes.'),
 ('bioseguridad','Bioseguridad','PARCIAL','Cinco momentos, cronómetro y listas diferenciadas. Técnica supervisada y protocolo local siguen siendo necesarios.'),
 ('cuaderno','PAE / SOAPIE / SBAR','PARCIAL','Editor, persistencia y Markdown funcionales. Impresión/PDF implementados, pendientes de prueba en dispositivo. Sin taxonomías completas ni diagnósticos automáticos.'),
 ('casos','Casos progresivos','PARCIAL','Siete escenarios sintéticos funcionales; revisión docente y validación clínica pendientes.'),
 ('biblioteca-escalas','Escalas y tests','PARCIAL','Se conservan los instrumentos ya funcionales y 44 fichas documentales; no son 44 calculadoras.'),
 ('instrumento/braden','Braden completo','BLOQUEADO-POR-VALIDACIÓN','Permiso de uso digital/reproducción no aportado. Solo registro externo; no administra el cuestionario.'),
 ('registro-escala/norton','Norton completo','BLOQUEADO-POR-VALIDACIÓN','Versión y derechos digitales pendientes de cotejo. Registro externo no equivale a aplicación.'),
 ('instrumento/rass','RASS administrado','BLOQUEADO-POR-VALIDACIÓN','Versión, traducción y derechos de uso digital pendientes. No se inventa un formulario.'),
 ('inventario','Inventario nominal','HECHO','Nombres y relaciones editoriales importados en IDENTIFICADO; no son monografías validadas.'),
 ('','Monografías DIGEMID producto a producto','BLOQUEADO-POR-VALIDACIÓN','Fuentes exactas, permisos, revisión clínica/farmacéutica y publicación: fase posterior.'),
 ('nube','Cuentas y contenido remoto','PARCIAL','Infraestructura anterior conservada; OTP real aplazado. APK de pruebas sin nube configurada.'),
 ('','Especialidades y simulación avanzada','FALTA','Backlog priorizado, sin botones de una función inexistente.'),
]
data=dict(topics=topics,devices=devices,cases=cases,checklists=checklists,modules=[dict(route=a,name=b,status=c,note=d) for a,b,c,d in modules])
(HERE/'contenido.json').write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
print('Contenido educativo:',len(topics),'temas;',len(devices),'vectores;',len(cases),'casos;',sum(len(c['questions']) for c in cases),'preguntas.')
