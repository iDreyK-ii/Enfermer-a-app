"""Mantenimiento: regenera y REEMPLAZA los ejemplos iniciales y las plantillas.
No ejecutar sobre contenido editorial que quieras conservar.
No es necesario para compilar la app ni para convertir una plantilla existente.
"""
from pathlib import Path
import json
from datetime import date
from openpyxl import Workbook
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.worksheet.datavalidation import DataValidation
from openpyxl.comments import Comment

if __name__ == "__main__":
    R=Path(__file__).resolve().parents[1]
    fields=['descripcion','mecanismo_accion','presentaciones','vias','indicaciones','contraindicaciones','precauciones','dosificacion','reconstitucion','dilucion','compatibilidad','administracion','estabilidad','reacciones_adversas','interacciones','monitorizacion','cuidados_enfermeria','poblaciones_especiales','educacion','alertas']
    base={'version_esquema':1,'id_paquete':'idreyk-inicial','version_contenido':1,'titulo':'Biblioteca inicial de demostración','autor':'Proyecto iDreyK 360 · edición de desarrollo','fecha_actualizacion':'2026-11-01','licencia':'Textos originales de demostración. Las fuentes externas se citan, no se redistribuyen sus documentos. No constituye autorización para incorporar otros contenidos.'}
    meds=[]
    for id_,name in [('paracetamol','Paracetamol'),('ceftriaxona','Ceftriaxona'),('enoxaparina','Enoxaparina'),('omeprazol','Omeprazol'),('furosemida','Furosemida'),('insulina-regular','Insulina humana regular')]:
     meds.append({'id':id_,'nombre':name,'grupo':'Ficha en preparación','sinonimos':[],'revision':'borrador','revisor':'','fecha_revision':'','secciones':{k:'' for k in fields},'fuentes':[]})
    case={'id':'recorrido-mario','titulo':'Una ficha por organizar','area':'Fundamentos · caso ficticio','revision':'borrador','objetivo':'Explorar la valoración, reconocer las etapas del PAE y organizar una nota ficticia.','contenido':'Mario es un personaje ficticio de 72 años. Este caso no plantea una enfermedad ni indica un tratamiento.\n\n1. Explora la ficha de demostración.\n2. Recorre las etapas del PAE.\n3. Escribe una nota con datos inventados.\n4. Comprueba que la nota se conserva al volver.\n\nNo introduzcas identificadores ni registros de pacientes reales.','fuentes':[]}
    safety={'id':'base-5','titulo':'Verificaciones básicas de práctica','area':'Seguridad de la medicación','revision':'borrador','institucion':'Lista educativa, no protocolo institucional','edicion':'Referencia histórica de cinco básicos · revisión documental 2026-11-01','items':[{'titulo':t,'detalle':d} for t,d in [('Persona correcta','Verificación de identidad según el procedimiento institucional.'),('Medicamento correcto','Verificación frente a la prescripción y al producto disponible.'),('Dosis correcta','Comprobación conforme a la prescripción y al procedimiento autorizado.'),('Vía correcta','Verificación de la vía prescrita.'),('Momento correcto','Verificación del momento y horario prescritos.')]],'fuentes':[{'titulo':'ISMP-España · Boletín 53, diciembre de 2023','url':'https://seguridaddelpaciente.sanidad.gob.es/informacion/publicaciones/2023/docs/Boletin_53_Diciembre_2023.pdf','editor':'ISMP-España / Ministerio de Sanidad','fecha_consulta':'2026-11-01'},{'titulo':'E.S.E. BelloSalud · Seguridad en utilización de medicamentos, versión 03, julio 2026','url':'https://www.esebellosalud.gov.co/storage/docs/V9R0xKf98Irm7BP6XdTz.pdf','editor':'E.S.E. BelloSalud, Colombia','fecha_consulta':'2026-11-01'}]}
    seed={**base,'medicamentos':meds,'casos':[case],'listas_seguridad':[safety]}
    (R/'app/src/main/assets/contenido_inicial.json').write_text(json.dumps(seed,ensure_ascii=False,indent=2))
    template={**base,'id_paquete':'mi-biblioteca','titulo':'Mi biblioteca de enfermería','autor':'COMPLETAR AUTOR','licencia':'COMPLETAR permisos y condiciones de uso','medicamentos':[meds[0]],'casos':[],'listas_seguridad':[]}
    (R/'content-tools/plantilla_paracetamol.json').write_text(json.dumps(template,ensure_ascii=False,indent=2))
    example=json.loads(json.dumps(template));example.update(id_paquete='ejemplo-importacion',titulo='Prueba de importación sin información clínica',autor='Demostración técnica',licencia='Texto original de prueba, sin instrucciones clínicas.')
    example['medicamentos'][0]['secciones']['descripcion']='IMPORTACIÓN DE PRUEBA CORRECTA. Este texto solo demuestra que un archivo local puede actualizar una ficha. No es información clínica sobre paracetamol.'
    (R/'content-tools/ejemplo_importacion.json').write_text(json.dumps(example,ensure_ascii=False,indent=2))
    wb=Workbook();ws=wb.active;ws.title='Instrucciones'
    instructions=[['PLANTILLA DE CONTENIDOS · iDreyK 360','Versión de esquema 1'],['1. Trabaja en una copia','No cambies los encabezados de las hojas.'],['2. Completa Metadatos','Cambia id_paquete y aumenta version_contenido en cada actualización.'],['3. Completa Medicamentos','Una fila por medicamento. No mezcles presentaciones con instrucciones incompatibles.'],['4. Fuentes','Una fila por referencia; vincúlala por tipo e id_registro.'],['5. Casos y listas','Son opcionales. Los ítems se vinculan mediante lista_id.'],['6. Estado inicial','Usa borrador. Revisión declarada no equivale a certificación.'],['7. Nunca inventes','No rellenes campos clínicos sin una fuente adecuada y permiso cuando corresponda.'],['8. Importación actual','La app importa JSON. El conversor incluido transforma este XLSX sin interpretar ni inventar texto.'],['9. Google Drive','La sincronización automática es futura, no está implementada.'],['10. Sin pacientes reales','No incluyas datos identificables ni historias clínicas.'],['11. Comprobación','Revisa el JSON y la vista previa antes de confirmar la importación.'],['12. Alcance del respaldo','Solo catálogo; no incluye notas de pacientes ni favoritos.']]
    for row in instructions:ws.append(row)
    ws.column_dimensions['A'].width=36;ws.column_dimensions['B'].width=105
    meta=wb.create_sheet('Metadatos');meta.append(['campo','valor'])
    for k in ['version_esquema','id_paquete','version_contenido','titulo','autor','fecha_actualizacion','licencia']:meta.append([k,template[k]])
    meta.column_dimensions['A'].width=30;meta.column_dimensions['B'].width=95
    med=wb.create_sheet('Medicamentos');headers=['id','nombre','grupo','sinonimos','revision','revisor','fecha_revision']+fields
    med.append(headers);med.append(['paracetamol','Paracetamol','Por completar','','borrador','','']+['' for _ in fields])
    med['D1'].comment=Comment('Separa sinónimos con punto y coma.','iDreyK 360')
    for col in ['H','I','Q','R','X']:med[f'{col}1'].comment=Comment('Texto literal. No agregues fórmulas de Excel ni datos clínicos sin verificar.','iDreyK 360')
    cas=wb.create_sheet('Casos');cas.append(['id','titulo','area','revision','revisor','fecha_revision','objetivo','contenido'])
    listas=wb.create_sheet('Listas');listas.append(['id','titulo','area','revision','revisor','fecha_revision','institucion','edicion'])
    items=wb.create_sheet('Items');items.append(['lista_id','orden','titulo','detalle'])
    sources=wb.create_sheet('Fuentes');sources.append(['tipo','id_registro','titulo','url','editor','fecha_consulta'])
    sources['A1'].comment=Comment('Valores permitidos: medicamento, caso, lista.','iDreyK 360')
    for sheet,col in [(med,'E'),(cas,'D'),(listas,'D')]:
     dv=DataValidation(type='list',formula1='"borrador,declarada_por_autor"',allow_blank=False);dv.error='Selecciona un estado de la lista.';dv.showErrorMessage=True;sheet.add_data_validation(dv);dv.add(f'{col}2:{col}1001')
    dv=DataValidation(type='list',formula1='"medicamento,caso,lista"');sources.add_data_validation(dv);dv.add('A2:A1001')
    for i,sheet in enumerate(wb):
     color=['9670BF','9670BF','DF5F9E','39AEB2','D5A14F','D5A14F','648ECD'][i]
     sheet.sheet_properties.pageSetUpPr.fitToPage=True;sheet.sheet_properties.tabColor=color;sheet.freeze_panes='B2' if sheet.title not in ['Instrucciones','Metadatos'] else 'A2'
     sheet.sheet_view.showGridLines=False
     for cell in sheet[1]:cell.fill=PatternFill('solid',fgColor=color);cell.font=Font(name='Calibri',size=11,bold=True,color='FFFFFF');cell.alignment=Alignment(vertical='center',wrap_text=True)
     sheet.row_dimensions[1].height=36
     for row in sheet.iter_rows(min_row=2,max_row=max(sheet.max_row,20)):
      for cell in row:cell.font=Font(name='Calibri',size=11,color='30273C');cell.alignment=Alignment(vertical='top',wrap_text=True);cell.fill=PatternFill('solid',fgColor='FAF5FC' if cell.row%2==0 else 'FFFFFF')
      sheet.row_dimensions[row[0].row].height=40 if sheet.title=='Instrucciones' else 65
     if sheet.title not in ['Instrucciones','Metadatos']:
      for col in sheet.columns:sheet.column_dimensions[col[0].column_letter].width=30 if col[0].column<=7 else 58
      sheet.auto_filter.ref=sheet.dimensions
    output=R/'content-tools/Plantilla_contenidos_iDreyK_360.xlsx';wb.save(output)
    print('Seed, JSON templates and',output.name,'created.')
