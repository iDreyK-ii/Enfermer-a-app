"""Regenera el esquema documental JSON Schema 2020-12; la app añade reglas agregadas."""
import json
from pathlib import Path
from convertir_plantilla import FIELDS

def string(limit=140, minimum=0):return {'type':'string','minLength':minimum,'maxLength':limit}
def optional_date():return {'anyOf':[{'const':''},{'type':'string','format':'date','maxLength':10}]}
slug={'type':'string','pattern':'^[a-z0-9][a-z0-9_-]{0,99}$'}
source={'type':'object','required':['titulo','url'],'properties':{'titulo':string(500,1),'url':{**string(2000,1),'format':'uri','pattern':'^https://'},'editor':string(500),'fecha_consulta':optional_date()}}
common={'id':slug,'revision':{'enum':['borrador','declarada_por_autor'],'default':'borrador'},'revisor':string(),'fecha_revision':optional_date(),'fuentes':{'type':'array','maxItems':30,'items':{'$ref':'#/$defs/fuente'}},'_paquete':slug,'_autor':string(140,1),'_actualizacion':{'type':'string','format':'date','maxLength':10},'_licencia':string(2000)}
review={'if':{'required':['revision'],'properties':{'revision':{'const':'declarada_por_autor'}}},'then':{'required':['revisor','fecha_revision','fuentes'],'properties':{'revisor':string(140,1),'fecha_revision':{'type':'string','format':'date','maxLength':10},'fuentes':{'type':'array','minItems':1}}}}
med={'type':'object','required':['id','nombre','secciones'],'properties':{**common,'nombre':string(140,1),'grupo':string(),'sinonimos':{'type':'array','maxItems':30,'items':string(120)},'secciones':{'type':'object','additionalProperties':False,'properties':{key:string(16000) for key in FIELDS}}},'allOf':[review]}
case={'type':'object','required':['id','titulo'],'properties':{**common,'titulo':string(140,1),'area':string(),'objetivo':string(4000),'contenido':string(60000)},'allOf':[review]}
safety={'type':'object','required':['id','titulo','items'],'properties':{**common,'titulo':string(140,1),'area':string(),'institucion':string(500),'edicion':string(),'items':{'type':'array','minItems':1,'maxItems':100,'items':{'type':'object','required':['titulo'],'properties':{'titulo':string(180,1),'detalle':string(3000)}}}},'allOf':[review]}
props={'version_esquema':{'const':1,'type':'integer'},'tipo_paquete':{'enum':['contenido','respaldo_catalogo'],'default':'contenido'},'id_paquete':slug,'version_contenido':{'type':'integer','minimum':1,'maximum':9999999},'titulo':string(140,1),'autor':string(140,1),'fecha_actualizacion':{'type':'string','format':'date','maxLength':10},'licencia':string(2000)}
for key,definition in [('medicamentos','medicamento'),('casos','caso'),('listas_seguridad','lista')]:props[key]={'type':'array','maxItems':300,'items':{'$ref':'#/$defs/'+definition}}
schema={'$schema':'https://json-schema.org/draft/2020-12/schema','title':'Paquete local iDreyK 360 — esquema 1','description':'Contenido literal. La aplicación valida además UTF-8, 2 MB, 500 registros totales, IDs únicos por tipo, fechas reales, URL HTTPS sin credenciales, versiones enteras sin decimales y límites del catálogo agregado. Una revisión declarada requiere licencia del paquete; no equivale a certificación clínica. Los respaldos deben conservar los metadatos originales de cada ficha.','type':'object','required':['version_esquema','id_paquete','version_contenido','titulo','autor','fecha_actualizacion'],'properties':props,'anyOf':[{'required':[key],'properties':{key:{'minItems':1}}} for key in ['medicamentos','casos','listas_seguridad']],'$defs':{'fuente':source,'medicamento':med,'caso':case,'lista':safety}}
Path(__file__).with_name('esquema_contenido_v1.schema.json').write_text(json.dumps(schema,ensure_ascii=False,indent=2),encoding='utf-8')

# The original schema remains published for existing v0.2 files. v2 is explicit
# so older applications reject new protocol packages rather than dropping them.
import copy
schema2=copy.deepcopy(schema)
schema2['title']='Paquete local iDreyK 360 — esquema 2'
schema2['properties']['version_esquema']['const']=2
schema2['properties']['protocolos']={'type':'array','maxItems':300,'items':{'$ref':'#/$defs/protocolo'}}
schema2['$defs']['protocolo']=copy.deepcopy(case)
schema2['anyOf'].append({'required':['protocolos'],'properties':{'protocolos':{'minItems':1}}})
schema2['anyOf'].append({'required':['tipo_paquete'],'properties':{'tipo_paquete':{'const':'respaldo_catalogo'}}})
schema2['description'] += ' Esquema 2 añade protocolos. Una exportación puede ser un respaldo vacío. v0.3 lee esquemas 1 y 2; v0.2 no admite esquema 2.'
for definition in ['medicamento','caso','lista','protocolo']:
    schema2['$defs'][definition]['properties']['derivado_de']={'type':'object','properties':{'paquete':string(100),'autor':string(140),'actualizacion':optional_date(),'licencia':string(2000)}}
Path(__file__).with_name('esquema_contenido_v2.schema.json').write_text(json.dumps(schema2,ensure_ascii=False,indent=2),encoding='utf-8')

# v3 conserva el formato del catálogo y añade los conceptos del PDF y lectura de enfermería.
from convertir_plantilla import FIELDS_V3
schema3=copy.deepcopy(schema2)
schema3['title']='Paquete iDreyK 360 — esquema 3 (app v0.4+)'
schema3['properties']['version_esquema']['const']=3
schema3['$defs']['medicamento']['properties']['secciones']['properties']={key:string(16000) for key in FIELDS_V3}
schema3['description'] += ' Esquema 3 añade campos farmacológicos y de enfermería. Las secciones son opcionales; vacío significa pendiente. Requiere app v0.4 o posterior; no sustituye revisión clínica.'
Path(__file__).with_name('esquema_contenido_v3.schema.json').write_text(json.dumps(schema3,ensure_ascii=False,indent=2),encoding='utf-8')
