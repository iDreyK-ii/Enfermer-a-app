"""Convierte la plantilla XLSX a un paquete JSON literal. No interpreta contenido clínico.
Uso: python convertir_plantilla.py Plantilla.xlsx salida.json
Requiere: pip install openpyxl
"""
from pathlib import Path
from datetime import datetime, date
import json, re, sys
from urllib.parse import urlparse
import jsonschema
from openpyxl import load_workbook

FIELDS=['descripcion','mecanismo_accion','presentaciones','vias','indicaciones','contraindicaciones','precauciones','dosificacion','reconstitucion','dilucion','compatibilidad','administracion','estabilidad','reacciones_adversas','interacciones','monitorizacion','cuidados_enfermeria','poblaciones_especiales','educacion','alertas']
FIELDS_V3=FIELDS+['nombres_comerciales', 'almacenamiento', 'formas_presentacion', 'velocidad_administracion', 'sueros_compatibles', 'incompatibilidades', 'objetivo_terapeutico', 'antes_administrar', 'durante_administracion', 'despues_administracion']
HEADERS={
'Medicamentos':['id','nombre','grupo','sinonimos','revision','revisor','fecha_revision']+FIELDS,
'Casos':['id','titulo','area','revision','revisor','fecha_revision','objetivo','contenido'],
'Protocolos':['id','titulo','area','revision','revisor','fecha_revision','objetivo','contenido'],
'Listas':['id','titulo','area','revision','revisor','fecha_revision','institucion','edicion'],
'Items':['lista_id','orden','titulo','detalle'],
'Fuentes':['tipo','id_registro','titulo','url','editor','fecha_consulta']}

def text(value):
    if value is None:return ''
    if isinstance(value,datetime):return value.date().isoformat()
    if isinstance(value,date):return value.isoformat()
    return str(value).strip()

def rows(wb,sheet,schema_version=2):
    if sheet not in wb.sheetnames:raise ValueError('Falta la hoja '+sheet)
    ws=wb[sheet]
    headers=[text(c.value) for c in ws[1]]
    expected=HEADERS[sheet] if sheet!='Medicamentos' or schema_version<3 else HEADERS[sheet][:7]+FIELDS_V3
    if headers!=expected:raise ValueError('No cambies los encabezados de la hoja '+sheet)
    result=[]
    for line in ws.iter_rows(min_row=2):
        if any(c.data_type=='f' for c in line):raise ValueError(f'No se admiten fórmulas de Excel en {sheet}, fila {line[0].row}. Usa texto literal.')
        values=[text(c.value) for c in line[:len(headers)]]
        if not any(values):continue
        result.append(dict(zip(headers,values)))
    return result

def require_slug(value,label):
    if not re.fullmatch(r'[a-z0-9][a-z0-9_-]{0,99}',value):raise ValueError(label+': usa minúsculas, números, guion y guion bajo.')

def require_date(value,label):
    if not re.fullmatch(r'[0-9]{4}-[0-9]{2}-[0-9]{2}',value):raise ValueError(label+': utiliza AAAA-MM-DD.')
    try:date.fromisoformat(value)
    except ValueError:raise ValueError(label+': utiliza AAAA-MM-DD.')

def convert(source,destination):
    wb=load_workbook(source,data_only=False,read_only=False,keep_links=False)
    if 'Metadatos' not in wb.sheetnames:raise ValueError('Falta la hoja Metadatos.')
    meta={}
    for row in wb['Metadatos'].iter_rows(min_row=2,max_col=2):
        if any(c.data_type=='f' for c in row):raise ValueError('No se admiten fórmulas en Metadatos.')
        if row[0].value is not None:meta[text(row[0].value)]=text(row[1].value)
    for k in ['version_esquema','id_paquete','version_contenido','titulo','autor','fecha_actualizacion','licencia']:
        if k not in meta:raise ValueError('Falta el metadato '+k)
    if meta['version_esquema'] not in ('1','2','3'):raise ValueError('Solo se admiten esquemas 1, 2 y 3.')
    schema_version=int(meta['version_esquema'])
    if schema_version==1 and 'Protocolos' in wb.sheetnames and rows(wb,'Protocolos'):raise ValueError('Los protocolos requieren esquema 2; no se omiten datos silenciosamente.')
    if not meta['version_contenido'].isdigit() or not 1<=int(meta['version_contenido'])<=9999999:raise ValueError('version_contenido debe ser un entero positivo de hasta siete dígitos.')
    require_slug(meta['id_paquete'],'id_paquete');require_date(meta['fecha_actualizacion'],'fecha_actualizacion')
    if not meta['titulo'] or not meta['autor']:raise ValueError('Completa el título y el autor del paquete.')
    root={**meta,'version_esquema':schema_version,'version_contenido':int(meta['version_contenido']),'medicamentos':[],'casos':[],'listas_seguridad':[]}
    if schema_version>=2:root['protocolos']=[]
    refs={}
    for row in rows(wb,'Fuentes'):
        if row['tipo'] not in ('medicamento','caso','lista','protocolo'):raise ValueError('Tipo de fuente no reconocido: '+row['tipo'])
        if not row['titulo'] or not row['url'].startswith('https://'):raise ValueError('Cada fuente necesita título y URL https://.')
        u=urlparse(row['url'])
        if not u.hostname or u.username is not None or any(c.isspace() for c in row['url']):raise ValueError('Cada fuente requiere una URL HTTPS completa sin credenciales ni espacios.')
        if row['fecha_consulta']:require_date(row['fecha_consulta'],'fecha_consulta')
        key=(row['tipo'],row['id_registro']);refs.setdefault(key,[]).append({k:row[k] for k in ['titulo','url','editor','fecha_consulta']})
    items={}
    for row in rows(wb,'Items'):
        if not row['titulo']:raise ValueError('Cada ítem necesita un título.')
        if not row['orden'].isdigit() or int(row['orden'])<1:raise ValueError('El orden de los ítems debe ser un entero positivo.')
        items.setdefault(row['lista_id'],[]).append((int(row['orden']),{'titulo':row['titulo'],'detalle':row['detalle']}))
    seen=set()
    sheets=[('Medicamentos','medicamento','medicamentos'),('Casos','caso','casos'),('Listas','lista','listas_seguridad')]
    if schema_version>=2 and 'Protocolos' in wb.sheetnames:sheets.append(('Protocolos','protocolo','protocolos'))
    for sheet,kind,target in sheets:
        for row in rows(wb,sheet,schema_version):
            require_slug(row['id'],sheet+' id')
            key=(kind,row['id'])
            if key in seen:raise ValueError('ID duplicado: '+str(key))
            seen.add(key)
            if not row.get('nombre',row.get('titulo')):raise ValueError('Completa el nombre o título de '+row['id'])
            revision=row['revision'] or 'borrador'
            if revision not in ('borrador','declarada_por_autor'):raise ValueError('Estado de revisión inválido: '+revision)
            entry={k:row[k] for k in (['id','nombre','grupo'] if kind=='medicamento' else ['id','titulo','area'])}
            entry.update(revision=revision,revisor=row['revisor'],fecha_revision=row['fecha_revision'],fuentes=refs.get(key,[]))
            if entry['fecha_revision']:require_date(entry['fecha_revision'],'fecha_revision')
            if revision=='declarada_por_autor':
                if not entry['revisor'] or not entry['fuentes'] or not meta['licencia']:raise ValueError('Una revisión declarada necesita revisor, fuentes y licencia.')
                require_date(entry['fecha_revision'],'fecha_revision')
            if kind=='medicamento':entry.update(sinonimos=[s.strip() for s in row['sinonimos'].split(';') if s.strip()],secciones={k:row[k] for k in (FIELDS_V3 if schema_version==3 else FIELDS)})
            elif kind in ('caso','protocolo'):entry.update(objetivo=row['objetivo'],contenido=row['contenido'])
            else:
                values=items.get(row['id'],[])
                if not 1<=len(values)<=100:raise ValueError('Cada lista necesita entre 1 y 100 ítems.')
                if len({n for n,_ in values})!=len(values):raise ValueError('Hay órdenes repetidos en la lista '+row['id'])
                entry.update(institucion=row['institucion'],edicion=row['edicion'],items=[v for _,v in sorted(values,key=lambda p:p[0])])
            root[target].append(entry)
    if any(k not in seen for k in refs):raise ValueError('Hay fuentes asociadas a un ID que no existe en las hojas de contenido.')
    if any(('lista',key) not in seen for key in items):raise ValueError('Hay ítems que apuntan a una lista inexistente.')
    targets=['medicamentos','casos','listas_seguridad']+(['protocolos'] if schema_version>=2 else [])
    count=sum(len(root[k]) for k in targets)
    if not 1<=count<=500:raise ValueError('Se requiere de 1 a 500 registros.')
    if any(len(root[k])>300 for k in targets):raise ValueError('Máximo 300 registros de cada tipo por paquete.')
    schema_path=Path(__file__).with_name(f'esquema_contenido_v{schema_version}.schema.json')
    jsonschema.validate(root,json.loads(schema_path.read_text(encoding='utf-8')),format_checker=jsonschema.FormatChecker())
    output=json.dumps(root,ensure_ascii=False,indent=2)
    if len(output.encode('utf-8'))>2000000:raise ValueError('El JSON supera 2 MB; divide la biblioteca.')
    Path(destination).write_text(output,encoding='utf-8')
    print(f'Conversión estructural completada: {count} registros → {destination}')
    print('No se han verificado exactitud clínica, vigencia ni permisos. Revisa el archivo antes de importarlo.')
    return root

if __name__=='__main__':
    if len(sys.argv)!=3:raise SystemExit('Uso: python convertir_plantilla.py Plantilla.xlsx salida.json')
    try:convert(sys.argv[1],sys.argv[2])
    except Exception as e:raise SystemExit('No se generó un paquete válido: '+str(e))
