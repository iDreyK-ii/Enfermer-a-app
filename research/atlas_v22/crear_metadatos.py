#!/usr/bin/env python3
"""Autoría editorial v2.2: posiciones revisadas en estos píxeles, no en esquemas anteriores.
No atribuye aprobación humana. Los candidatos rechazados carecen de archivo/hotspots.
"""
from pathlib import Path
import json
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
OUT=ROOT/'app/src/main/assets/atlas'
DATE='2026-11-03'
processing={x['id']:x for x in json.loads((HERE/'procesamiento_png.json').read_text())}
def source(title,url,publisher,scope='Texto consultado completo; síntesis anatómica, no autorización clínica.'):
    return dict(title=title,url=url,publisher=publisher,date=DATE,review_scope=scope)
B=source('Jeringas y agujas: catálogo técnico (2010)','https://www.medline.com/media/catalog/Docs/MKT/OMNIFIX.PDF','B. Braun · alojado por Medline','Folleto completo; segunda referencia técnica del mismo fabricante, no revisión independiente.')
J=source('Componentes del equipo IV','https://jhrml-bd.com/product/components-of-iv-infusion-set','JMI Hospital Requisite Manufacturing','Anatomía y variantes; no certifica la configuración TrueCare.')
S={
'jeringa':[source('Omnifix Luer Lock Solo · tres piezas','https://catalogs.bbraun.com/en-01/p/PRID00012432/omnifix-luer-lock-solo','B. Braun'),B],
'aguja':[source('Sterican · aguja hipodérmica estándar','https://catalogs.bbraun.com/en-01/p/PRID00015595/sterican','B. Braun'),B],
'estetoscopio':[source('Anatomy of a stethoscope','https://multimedia.3m.com/mws/media/1346109O/littmann-anatomy-of-a-stethoscope-poster-pdf.pdf','3M Littmann'),source('Manual Classic III · variantes de campana','https://stethoscope.eu/blog/en/how-to-hygiene/user-manual-3m-littmann-classic-iii-stethoscope/','stethoscope.eu','Texto completo; fuente secundaria sobre un modelo concreto. La imagen no es una reproducción exacta del Classic III.')],
'equipo-iv':[source('TCBINF001 · equipos por gravedad','https://www.tcbiomedix.com/our-products/iv-administration-sets/gravity-iv-administration-sets','TrueCare Biomedix'),J,source('TCBINF001 · ficha técnica del distribuidor','https://www.henryscheinmedical.com/us-en/medical/p/medical-surgical-supplies/iv-irrigation-products/administration-set-iv-male-ll/1373590','Henry Schein')],
'tensiometro':[source('Prosphyg 760 · dos tubos y repuestos','https://www.adctoday.com/products/760','ADC'),source('Prosphyg · IFU español, revisión 21','https://www.adctoday.com/sites/default/files/literature/9360N-00%20rev%2021%20Spanish.pdf','ADC','Manual completo; segunda referencia del mismo fabricante, no contraste independiente.')],
'cateter-periferico':[source('Introcan Safety 3 · instrucciones','https://www.bbraun.co.uk/content/dam/b-braun/en_gb/website-6/documents/products/therapies/infusion-therapy/introcan-safety-3-/Introcan%20Safety%203%20IFU.pdf','B. Braun','Extracto completo recuperado; segunda fuente y aprobación visual pendientes.')],
'bureta':[source('Burette IV set · serie SMD 129','https://www.smd-medical.com/catalogue/iv-therapy-vascular-access/12-infusion-set-burette-type-catalogue.pdf','SMD Medical'),J],
'llave-tres-vias':[source('Discofix C · catálogo','https://www.bbraun-vetcare.com/content/dam/catalog/bbraun/bbraunProductCatalog/BVC_WEB_01/en-ag/b129/discofix-c-stopcocksmanifolds-youraccesstoincreasedsafety.pdf','B. Braun','Solo fragmento indexado; cotejo íntegro y segunda fuente pendientes.')]
}
def part(id,name,fun,safety,x=None,y=None,status='universal',description=None):
    return dict(id=id,name=name,function=fun,safety=safety,hotspot_x=x,hotspot_y=y,visible=x is not None,part_status=status,part_description=description or name)
P=part
DATA={
'jeringa':('Jeringa','Jeringa hipodérmica de tres piezas, conexión Luer Lock','Omnifix de tres piezas como referencia anatómica. Ilustración genérica; no reproduce un tamaño comercial ni sirve para medir. No todos los modelos llevan sello separado.',[
P('cilindro','Cilindro o cuerpo','Contiene el volumen y permite observarlo.','Comprobar capacidad y escala en el producto real.',.46,.585),
P('embolo','Émbolo: vástago móvil','Transmite el movimiento al pistón.','El vástago no es el sello de elastómero.',.16,.44),
P('piston','Pistón o sello','Sella y se desplaza dentro del cilindro.','Separado del vástago en el modelo de tres piezas.',.676,.55,'modelo'),
P('cono','Cono Luer','Constituye el extremo de conexión.','No deducir compatibilidad ni una vía de uso por esta imagen.',.915,.582,'modelo'),
P('alas','Aletas de apoyo','Ofrecen apoyo para los dedos.','No representan una técnica estéril.',.224,.356),
P('escala','Graduaciones','Permiten leer el volumen en el dispositivo real.','Marcas ilustrativas sin números ni valor metrológico.',.53,.419),
P('boton','Apoyo para el pulgar','Es el extremo de apoyo del vástago.','No confundir con las aletas del cilindro.',.108,.44),
P('collar','Collar Luer Lock','Rodea el cono y participa en la fijación compatible.','La rosca de fijación es interna; no todas las jeringas llevan collar.',.835,.56,'modelo')]),
'aguja':('Aguja','Aguja hipodérmica estándar con protector','Referencia Sterican estándar, no aguja de seguridad. No se declara calibre/longitud ni se infiere por el color del hub. La funda separada no recomienda reencapuchar.',[
P('pabellon','Pabellón o conector','Une la cánula a una conexión compatible.','Color y conectores se verifican en el producto real.',.249,.251),
P('canula','Cánula metálica','Forma el cuerpo tubular de la aguja.','No es el catéter flexible.',.566,.482),
P('bisel','Bisel','Es la terminación oblicua del extremo distal.','Geometría ilustrativa; no enseña técnica de punción.',.837,.637),
P('punta','Punta','Es el extremo distal punzante.','Riesgo punzocortante; seguir manejo y descarte institucional.',.882,.669),
P('protector','Protector separado','Protege el dispositivo antes del uso según diseño.','No reencapuchar ni reutilizar por imitación de esta vista.',.51,.677,'modelo'),
P('lumen','Lumen interno','Es el conducto interior de la cánula.','No visible en esta vista: sin hotspot ni pregunta visual.',status='universal')]),
'estetoscopio':('Estetoscopio','Estetoscopio acústico binaural de doble cabezal','Anatomía acústica común contrastada con 3M. No se afirma réplica de un modelo ni un sistema de doble lumen. La cara posterior de la campana no se muestra.',[
P('olivas','Olivas','Son los extremos que contactan con los oídos.','Ajuste e higiene según fabricante.',.866,.249),
P('arcos','Tubos binaurales','Conectan el tubo flexible con las olivas.','Revisar integridad y orientación conforme al manual.',.6815,.3135),
P('resorte','Resorte del conjunto auricular','Contribuye a mantener la tensión del conjunto.','Forma y mecanismo dependientes del modelo.',.43,.2612,'modelo'),
P('tubo','Tubo flexible','Conduce el sonido entre cabezal y conjunto auricular.','No inferir el número de lúmenes internos.',.423,.857),
P('vastago','Unión o vástago del cabezal','Une el tubo al cabezal.','Selección de cara activa dependiente del modelo.',.771,.754,'modelo'),
P('diafragma','Diafragma','Es la membrana de la cara visible del cabezal.','No es una campana abierta; revisar integridad e higiene.',.844,.669),
P('cabezal','Cuerpo del cabezal','Aloja las superficies acústicas.','No deducir ajustes de presión por esta imagen.',.85,.8),
P('campana','Campana / cara posterior','Es una cara acústica alternativa en determinados modelos.','No se ve su apertura: sin hotspot ni prueba de identificación.',status='modelo')]),
'equipo-iv':('Equipo de venoclisis','Equipo de administración intravenosa por gravedad','Referencia TCBINF001: un roller, un sitio Y y conector distal; sin regulador adicional. El filtro interno no se identifica visualmente. No precompleta el factor de goteo de ninguna calculadora.',[
P('punzon','Punzón protegido','Es el extremo de acceso al recipiente compatible.','El protector sigue colocado; no enseñar inserción.',.23,.177),
P('venteo','Venteo y tapa','Permite la función de venteo prevista en el modelo.','No todos los equipos son ventilados.',.308,.075,'modelo'),
P('camara','Cámara de goteo','Permite observar el goteo en un equipo real.','Factor y uso se leen en el envase y las instrucciones.',.45,.174),
P('tubo','Tubo flexible','Constituye la línea de conducción.','Revisar integridad; la imagen no autoriza conexiones.',.597,.6233),
P('roller','Regulador de rodillo','Comprime el tubo para regularlo según diseño.','No es una bomba ni un regulador de precisión.',.473,.439),
P('sitio-y','Sitio de acceso en Y','Es un acceso adicional de este modelo.','Uso y compatibilidades requieren indicación y protocolo.',.69,.788,'modelo'),
P('luer','Conector distal protegido','Conecta al sistema compatible previsto.','No inferir esterilidad ni compatibilidad por la imagen.',.94,.53,'modelo'),
P('filtro','Filtro interno de la cámara','Es un componente declarado por el fabricante.','No visible/resuelto en esta imagen; sin hotspot ni pregunta.',status='modelo')]),
 'tensiometro':('Tensiómetro aneroide','Esfigmomanómetro aneroide de bolsillo con manguito','Referencia anatómica ADC Prosphyg 760 con dos tubos y vejiga interna. No es una representación calibrada ni una reproducción exacta. El manual de referencia excluye uso neonatal; no extrapolar tallas.',[
P('manguito','Manguito','Es la envoltura del conjunto inflable.','Seleccionar talla y revisar estado según fabricante.',.312,.369),
P('manometro','Manómetro','Muestra la presión del sistema en el equipo real.','La esfera ilustrada no permite una lectura de presión.',.639,.467),
P('pera','Pera de inflado','Genera la presión de aire en el sistema.','No es un dispositivo para vía intravenosa.',.858,.60),
P('valvula','Válvula de liberación','Permite liberar aire según diseño.','No inferir una técnica de medición de esta imagen.',.742,.65),
P('tubos','Tubos de aire','Conectan los elementos del sistema neumático.','No conectar con líneas intravasculares; revisar fugas.',.629,.754,'modelo'),
P('cierre','Sistema de cierre','Sujeta la envoltura.','Diseño dependiente del manguito.',.401,.552,'modelo'),
P('vejiga','Vejiga inflable interna','Es la bolsa de aire dentro del manguito de referencia.','Oculta: no se rotula como si fuera la tela ni se pregunta su ubicación.',status='modelo')]),
}
old={d['id']:d for d in json.loads((ROOT/'research/base_maestra_v21/contenido.json').read_text())['devices']}
rejected={
'cateter-periferico':('Catéter venoso periférico','Catéter IV periférico corto / bránula','Introcan Safety 3 alado sin puerto lateral. No mezclar con bránulas con torre de inyección.','Candidato rechazado: falso fondo de transparencia y componente metálico interno no sustentado.'),
'bureta':('Bureta','Equipo de infusión con bureta graduada','Referencia SMD 129, no mezcla con Dosifix; dos pinzas y cámara de goteo inferior.','Candidato rechazado: cámara/tramo desconectado y continuidad del circuito no coherente.'),
'llave-tres-vias':('Llave de tres vías','Llave de paso de tres vías','Referencia de configuración Discofix C: dos conexiones hembra y una macho. No inferir flujo por la posición de la manija.','Candidato rechazado: cuatro ramas visibles; no corresponde a una llave de tres vías.')}
for id,(name,technical,note,reason) in rejected.items():
    parts=[]
    for p in old[id]['parts']:
        if id=='cateter-periferico' and p['id']=='puerto': continue
        parts.append(P(p['id'],p['name'],p['function'],p['safety'].split(' Recurso visual pendiente:')[0]+' Recurso visual pendiente: no hay punto de identificación.',status='modelo'))
    DATA[id]=(name,technical,note,parts)
for id,(name,technical,note,parts) in DATA.items():
    available=id in processing
    info=processing.get(id)
    file=info['file'] if available else None
    asset_id=Path(file).stem if file else 'pendiente_'+id.replace('-','_')
    sources=S[id]
    for p in parts:
        p['source_urls']=[s['url'] for s in sources]
        p['review_status']='COTEJO-DOCUMENTAL-ASISTIDO; APROBACIÓN-PROFESIONAL-PENDIENTE'
    d=dict(asset_id=asset_id,id=id,name=name,display_name_es=name,technical_name=technical,area='Dispositivos',file=file,
        view='Vista de producto en tres cuartos; conjunto completo' if available else 'PENDIENTE',
        image_available=available,image_width=info['export_dimensions'][0] if available else 0,image_height=info['export_dimensions'][1] if available else 0,
        image_sha256=info['sha256'] if available else '',alt_text=f'{technical}. '+('Imagen generada con IA y recortada, no fotografía de un fabricante. Etiquetas en interfaz; alternativa textual en la lista de partes.' if available else 'Recurso visual pendiente. No se muestra un sustituto esquemático.'),
        model_note=note,parts=parts,sources=sources,source_urls=[s['url'] for s in sources],review_date=DATE,
        review_status='REVISIÓN-ASISTIDA; APROBACIÓN-PROFESIONAL-PENDIENTE' if available else 'RECHAZADO; REGENERACIÓN-PENDIENTE',accuracy_approved_by=None,
        technical_reviewer='Asistente IA: cotejo documental y visual, no profesional acreditado',clinical_approved=False,
        pending_reason='' if available else rejected[id][3],processing=info,
        checks=['Identificar el modelo y consultar sus instrucciones.','Comprobar integridad, conexión y condiciones del producto real.','Esta imagen no confirma esterilidad, calibración, compatibilidad ni competencia técnica.'],
        revision='atlas-v22.1')
    (OUT/(asset_id+'.json')).write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n')
print('5 metadatos con PNG, 3 pendientes; ninguna aprobación clínica.')
