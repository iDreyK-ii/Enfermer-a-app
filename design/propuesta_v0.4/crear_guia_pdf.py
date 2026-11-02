"""Dossier autocontenido del diseño elegido. Requiere reportlab; sin red."""
from pathlib import Path
from xml.sax.saxutils import escape
from reportlab.pdfgen import canvas
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.platypus import Paragraph
from PIL import Image

HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
for name,file in [('Body','nunito_sans_400.ttf'),('Bold','outfit_600.ttf')]:
    pdfmetrics.registerFont(TTFont(name,str(ROOT/'app/src/main/res/font'/file)))
W,H=A4
OUT=HERE/'Diseno_y_arquitectura_v0.4.pdf'
c=canvas.Canvas(str(OUT),pagesize=A4,pageCompression=1)
c.setTitle('iDreyK 360 · Diseño, datos y entrega v0.4')
c.setAuthor('Proyecto iDreyK 360 · Desarrollo local')
INK=colors.HexColor('#30253E'); MUTED=colors.HexColor('#725E82'); PURPLE=colors.HexColor('#76529A'); PINK=colors.HexColor('#D64792'); TEAL=colors.HexColor('#188B92'); SOFT=colors.HexColor('#F2EAF8')
page=0

def para(text,x,y,width,size=11,leading=15,font='Body',color=INK):
    style=ParagraphStyle('p',fontName=font,fontSize=size,leading=leading,textColor=color)
    p=Paragraph(text,style);_,h=p.wrap(width,H);p.drawOn(c,x,y-h);return y-h

def label(text,x,y,color=PURPLE,size=10):
    c.setFillColor(color);c.setFont('Bold',size);c.drawString(x,y,text)

def begin(kicker,title,subtitle):
    global page
    page+=1
    c.setFillColor(colors.HexColor('#FCFAFD'));c.rect(0,0,W,H,fill=1,stroke=0)
    c.setFillColor(PINK);c.roundRect(40,H-46,25,5,2,fill=1,stroke=0)
    label('iDreyK 360',74,H-47,INK,15);label('DESARROLLO  /  v0.4',W-189,H-44,MUTED,9)
    label(kicker,40,H-86)
    para(title,40,H-98,W-80,29,32,'Bold')
    para(subtitle,40,H-143,W-80,10,14,color=MUTED)
    c.setStrokeColor(colors.HexColor('#E7DCEC'));c.line(40,48,W-40,48)
    label('1 NOV 2026  ·  Diseño propuesto y comprobaciones locales',40,31,MUTED,8)
    label(f'{page:02}',W-56,31,MUTED,10)

def note(title,body,x,y,width=182):
    y=para(title,x,y,width,13,16,'Bold',PURPLE)-7
    return para(body,x,y,width,10.5,14)-20

def picture(name,x,y,width,maxheight):
    with Image.open(HERE/name) as im: iw,ih=im.size
    scale=min(width/iw,maxheight/ih);ww=iw*scale;hh=ih*scale
    c.drawImage(str(HERE/name),x,y-hh,width=ww,height=hh,mask='auto')
    return y-hh

def panel(title,body,x,y,width,height,fill=SOFT):
    c.setFillColor(fill);c.roundRect(x,y-height,width,height,12,fill=1,stroke=0)
    para(title,x+16,y-14,width-32,12,15,'Bold',PURPLE)
    para(body,x+16,y-38,width-32,10.5,14)

begin('01 / INICIO ELEGIDO','Todo a mano, sin saturar','La opción de inicio que elegiste conserva la identidad del Prototipo B.')
picture('01_inicio.png',40,655,285,580)
y=653
for title,body in [
 ('Bienvenida e identidad','Rosa, lavanda y turquesa. Saludo personal, ilustración y acceso al perfil.'),
 ('Búsqueda primero','Medicamentos, módulos y recursos. La búsqueda también contempla marcas documentadas y sinónimos.'),
 ('Farmacología prioritaria','Una entrada destacada a las fichas, sin esconder PAE, escalas, cálculos, apuntes, protocolos y multimedia.'),
 ('Actualización explícita','Consultar novedades abre el conector opcional. Importar requiere ver los cambios y confirmarlos.'),
 ('Navegación estable','Módulos, fármacos, inicio, favoritos y perfil. El modo ligero conserva las mismas funciones.'),
]: y=note(title,body,353,y,200)
para('MAQUETA, NO CAPTURA DEL APK<br/>La app usa componentes Android adaptables. No se afirma una coincidencia píxel a píxel.',353,y+2,200,9,12,color=MUTED)
c.showPage()

begin('02 / FICHA ELEGIDA, TEXTO CORREGIDO','Primero, lo esencial','Se mantiene la ficha que elegiste; se corrigen encuadre, títulos y legibilidad.')
picture('02_ficha_legible.png',40,655,285,570)
y=653
for title,body in [
 ('Estado siempre visible','Nombre, favorito y borrador o revisión declarada. La maqueta no contiene indicaciones ni dosis clínicas.'),
 ('Lectura de enfermería','Objetivo, antes de administrar, preparación/administración y vigilancia. Tú aportarás el contenido progresivamente.'),
 ('Tu PDF, sin recortes','Los 17 conceptos están en Ficha técnica. Otros conserva los campos anteriores; Fuentes reúne la procedencia.'),
 ('Fichas parciales','Los espacios vacíos se muestran pendientes. No se convierten en “sin riesgo” ni en “no aplica”.'),
 ('Edición y trazabilidad','La edición es local y vuelve el registro a borrador. Se conserva la separación entre contenido y revisión.'),
]: y=note(title,body,353,y,200)
para('Imagen demostrativa creada con IA y corregida. No representa revisión clínica ni aval de DIGEMID.',353,y+1,200,9,12,color=MUTED)
c.showPage()

begin('03 / DATOS Y CUENTAS','Dos niveles, una biblioteca','SQLite para consultar en el teléfono; Supabase para cuentas y publicación editorial.')
rows=[
 ('SQLite · teléfono','Catálogo local, favoritos, práctica y cuaderno. Funciona sin cuenta obligatoria; archivos privados aparte.'),
 ('PostgreSQL · Supabase','Borradores, paquetes versionados y permisos editoriales. El lector solo puede descargar publicaciones habilitadas.'),
 ('Supabase Auth','Correo e identidad de la cuenta; acceso por código. No se crea una tabla propia de contraseñas en Android.'),
 ('Supabase Storage','PDF y archivos fuente en un espacio editorial privado. Subir un PDF no lo transforma en una ficha publicada.'),
]
y=655
for title,body in rows:
 panel(title,body,40,y,W-80,88);y-=99
label('CÓMO LLEGA UN MEDICAMENTO NUEVO',40,y-5,TEAL,11)
y-=26
for number,title,body in [
 ('1','Completar','Plantilla XLSX/JSON parcial; IDs estables, sin rehacer la estructura.'),
 ('2','Revisar y publicar','Panel privado Supabase Studio y herramienta del operador. Fuentes, licencia y revisión documentadas.'),
 ('3','Consultar e importar','La app comprueba la versión e integridad y muestra una vista previa antes de actualizar SQLite.'),
]:
 label(number,43,y-10,PINK,16);y=para(f'{title}: {body}',66,y,W-110,10.5,14)-12
para('ESTADO REAL: no hay un proyecto Supabase conectado. Hace falta crearlo, aplicar permisos y configurar correo. No se suben notas, planes o archivos personales del teléfono.',40,100,W-80,10,13,'Bold',PURPLE)
c.showPage()

begin('04 / ESTRUCTURA FARMACOLÓGICA','Tus 17 conceptos, conservados','El nombre identifica la ficha; los demás datos se guardan como secciones opcionales.')
rows=[('01','Nombre genérico','nombre'),('02','Nombre comercial','nombres_comerciales'),('03','Presentaciones','presentaciones'),('04','Mecanismo de acción','mecanismo_accion'),('05','Indicaciones','indicaciones'),('06','Almacenamiento','almacenamiento'),('07','Formas de presentación','formas_presentacion'),('08','Reconstitución','reconstitucion'),('09','Dosis','dosificacion'),('10','Dilución','dilucion'),('11','Vía de administración','vias'),('12','Velocidad de administración','velocidad_administracion'),('13','Contraindicaciones','contraindicaciones'),('14','Estabilidad de solución','estabilidad'),('15','Sueros compatibles','sueros_compatibles'),('16','Incompatibilidades','incompatibilidades'),('17','Efectos adversos / RAM','reacciones_adversas')]
y=650
c.setFillColor(PURPLE);c.roundRect(40,y-28,W-80,28,6,fill=1,stroke=0)
for x,txt in [(52,'N.º'),(90,'Concepto del PDF'),(338,'Clave del contenido')]:label(txt,x,y-18,colors.white,10)
y-=28
for idx,(num,title,key) in enumerate(rows):
 c.setFillColor(colors.white if idx%2 else SOFT);c.rect(40,y-23,W-80,23,fill=1,stroke=0)
 label(num,52,y-15,MUTED,9);para(escape(title),90,y-4,242,10,12);para(key,338,y-4,200,9,12,color=MUTED);y-=23
panel('Además: el enfoque de enfermería','Objetivo terapéutico; antes, durante y después de administrar. Los 20 campos previos se conservan y se añaden 10: 30 secciones opcionales en total, además de la identidad y fuentes.',40,199,W-80,91)
para('Formato 3 para v0.4+. Se siguen leyendo formatos 1/2. Los seis medicamentos iniciales permanecen en borrador: no se inventaron monografías, dosis o controles.',40,91,W-80,10,13,color=MUTED)
c.showPage()

begin('05 / RENDIMIENTO Y COMPROBACIÓN','Más fluidez, sin quitar funciones','La política adapta el movimiento; no promete rendimiento medido en todos los teléfonos.')
y=655
for title,body in [
 ('AUTOMÁTICO','Reduce efectos en equipos con poca RAM, ahorro de batería o presión de memoria. Considera señales Android, no un benchmark.'),
 ('LIGERO','Detiene movimiento decorativo y reduce sombras. Mantiene contenido, buscador, cálculos, formularios y navegación.'),
 ('COMPLETO','Habilita los efectos disponibles, pero sigue respetando accesibilidad, memoria, batería y segundo plano.'),
]: panel(title,body,40,y,W-80,85);y-=96
label('COMPROBADO LOCALMENTE',40,350,TEAL,11)
metrics=[('106','Android / JVM'),('24','Pruebas de archivos'),('17','Permisos PostgreSQL'),('5','Espejo SQLite')]
for idx,(n,caption) in enumerate(metrics):
 x=40+idx*130;c.setFillColor(SOFT);c.roundRect(x,267,123,67,9,fill=1,stroke=0)
 label(n,x+12,302,PURPLE,23);para(caption,x+12,290,103,8.5,11,color=MUTED)
para('APK ensamblado. Lint: 0 errores y 24 advertencias. Misma firma de desarrollo que v0.2/v0.3; APK anteriores preservados.',40,247,W-80,11,15)
label('ANTES DE LLAMARLO PRODUCCIÓN',40,188,PINK,11)
para('Probar instalación y actualización en teléfonos reales; comprobar varias gamas, texto grande y accesibilidad; configurar y probar correo/Auth/Storage; cerrar privacidad y gestión de cuentas; revisar y autorizar contenido clínico. La prueba local no sustituye ninguna de estas tareas.',40,169,W-80,11,15)
para('Datos personales del dispositivo: no se sincronizan por iniciar sesión. Consulta del catálogo: manual, con confirmación; no push, retiros automáticos ni reemplazo silencioso.',40,91,W-80,9.5,13,color=MUTED)
c.showPage()

begin('06 / ENTREGA Y SIGUIENTE PASO','Listo para continuar y exportar','Esta fase entrega una base compilada; no declara terminado el aplicativo ni activa servicios externos.')
y=655
for title,body in [
 ('1. Ver e instalar','Enfermeria_iDreyK_360_v0.4_debug.apk. Edición de desarrollo, Android 8.0+. Actualizar sin desinstalar; no borrar datos ni forzar una vuelta a v0.3.'),
 ('2. Conservar el proyecto','Android_iDreyK_360_codigo.zip incluye Kotlin, SQL, diseño, plantillas, guías, pruebas e historial portátil. La clave privada de firma no se distribuye.'),
 ('3. Completar fichas a tu ritmo','Plantilla_farmacos_iDreyK_v3.xlsx y Plantilla_farmaco_iDreyK_v3.json. Admite datos parciales. Al actualizar un ID se reemplaza la ficha confirmada: no omitir datos que deban conservarse.'),
 ('4. Activar cuentas y publicación','Crear un proyecto propio en Supabase Studio; seguir backend/README.md para SQL/RLS, correo, clave publicable y compilación. La clave secreta queda solo con el operador.'),
 ('5. Dar continuidad al desarrollo','Leer docs/ORDEN_DE_COMPILACION_v0.4.md y AGENTS.md. Mantener Android/Compose y las ocho tablas SQLite. Cambiar contenido compatible no requiere otro APK.'),
]:y=note(title,body,40,y,W-80)
para('FUENTES TÉCNICAS<br/><link href="https://supabase.com/docs/guides/auth/auth-email-passwordless" color="#76529A">Supabase: códigos de correo</link> · <link href="https://supabase.com/docs/guides/database/secure-data" color="#76529A">RLS y permisos</link> · <link href="https://supabase.com/docs/guides/getting-started/api-keys" color="#76529A">Claves de API</link><br/>Estructura clínica: PDF en blanco aportado por el usuario, conservado en design/propuesta_v0.4/. Evidencias del proyecto: qa/RESULTADOS_v0.4.md.',40,118,W-80,9,12,color=MUTED)
c.showPage();c.save();print(OUT)
