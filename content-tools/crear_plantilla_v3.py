"""Crea una plantilla NUEVA v3; nunca modifica las plantillas/seed de versiones anteriores."""
from pathlib import Path
import json
from openpyxl import Workbook
from openpyxl.styles import Alignment, Font, PatternFill
from openpyxl.worksheet.datavalidation import DataValidation
from convertir_plantilla import HEADERS, FIELDS_V3, convert

HERE = Path(__file__).resolve().parent
MAP = [
    ('nombre', 'Nombre genérico'), ('nombres_comerciales', 'Nombre comercial'),
    ('presentaciones', 'Presentaciones'), ('mecanismo_accion', 'Mecanismo de acción'),
    ('indicaciones', 'Indicaciones'), ('almacenamiento', 'Almacenamiento'),
    ('formas_presentacion', 'Formas de presentación'), ('reconstitucion', 'Reconstitución'),
    ('dosificacion', 'Dosis'), ('dilucion', 'Dilución'), ('vias', 'Vía de administración'),
    ('velocidad_administracion', 'Velocidad de administración'),
    ('contraindicaciones', 'Contraindicaciones'), ('estabilidad', 'Estabilidad de solución'),
    ('sueros_compatibles', 'Sueros compatibles'), ('incompatibilidades', 'Incompatibilidades'),
    ('reacciones_adversas', 'Efectos adversos / observaciones / RAM'),
]


def create(destination: Path):
    wb = Workbook()
    guide = wb.active
    guide.title = 'Instrucciones'
    guide.append(['iDreyK 360 · Farmacología v3', 'Cómo preparar el contenido'])
    for row in [
        ('Compatibilidad', 'Requiere Android iDreyK v0.4 o posterior. No importa directamente en v0.3.'),
        ('PDF del usuario', 'Los 17 conceptos están relacionados con las columnas en Mapa17.'),
        ('Ficha parcial', 'Se admiten celdas vacías: se mostrarán pendientes. Si no aplica, escribe una justificación; no se infiere ausencia de riesgo.'),
        ('Enfermería', 'Completa objetivo_terapeutico, antes_administrar, durante_administracion y despues_administracion solo con datos revisados.'),
        ('Conservación', 'Se mantienen los campos anteriores, interacciones, poblaciones y cuidados. No eliminar columnas.'),
        ('Fuentes', 'Relacionar cada fuente por tipo e ID. Registrar revisor, fecha y licencia cuando corresponda.'),
        ('No validación automática', 'El conversor solo valida formato. No verifica exactitud clínica, permisos ni revisión profesional.'),
        ('Publicar', 'La cuenta lectora no publica. El operador revisa y publica paquetes del canal catalogo-oficial.'),
        ('Límites iniciales', '2 MB por paquete/catálogo agregado, 500 registros totales y 300 por tipo. No hay catálogo ilimitado.'),
        ('Formato', 'Texto literal, sin fórmulas. Puedes eliminar la fila de ejemplo y añadir tus medicamentos.'),
    ]: guide.append(row)
    metadata = wb.create_sheet('Metadatos')
    metadata.append(['clave', 'valor'])
    for key, value in {
        'version_esquema': 3, 'id_paquete': 'mis-farmacos', 'version_contenido': 1,
        'titulo': 'Mi biblioteca de farmacología', 'autor': 'Completar autor o institución',
        'fecha_actualizacion': '2026-11-01', 'licencia': '',
    }.items(): metadata.append([key, value])
    mapping = wb.create_sheet('Mapa17')
    mapping.append(['Número PDF', 'Concepto del PDF', 'Columna / clave JSON'])
    for number, (key, label) in enumerate(MAP, 1): mapping.append([number, label, key])
    for sheet, old_headers in HEADERS.items():
        headers = old_headers if sheet != 'Medicamentos' else old_headers[:7] + FIELDS_V3
        ws = wb.create_sheet(sheet)
        ws.append(headers)
        if sheet == 'Medicamentos':
            ws.append(['medicamento-ejemplo', 'Ficha de ejemplo · completar', '', '', 'borrador', '', ''] + [''] * len(FIELDS_V3))
            validation = DataValidation(type='list', formula1='"borrador,declarada_por_autor"')
            validation.error = 'Usa uno de los estados de revisión documentados.'
            validation.showErrorMessage = True
            ws.add_data_validation(validation)
            validation.add('E2:E301')
    for ws in wb:
        ws.freeze_panes = 'A2'
        ws.sheet_view.showGridLines = False
        ws.auto_filter.ref = ws.dimensions
        ws.row_dimensions[1].height = 34
        for cell in ws[1]:
            cell.fill = PatternFill('solid', fgColor='63418E')
            cell.font = Font(name='Calibri', bold=True, color='FFFFFF', size=11)
            cell.alignment = Alignment(wrap_text=True, vertical='center')
        for row in ws.iter_rows(min_row=2):
            for cell in row:
                cell.fill = PatternFill('solid', fgColor='F5EFFB' if cell.row % 2 == 0 else 'FFFFFF')
                cell.font = Font(name='Calibri', size=11, color='312441')
                cell.alignment = Alignment(wrap_text=True, vertical='top')
            ws.row_dimensions[row[0].row].height = 42
        for column in ws.columns:
            ws.column_dimensions[column[0].column_letter].width = 28
    guide.column_dimensions['A'].width = 26
    guide.column_dimensions['B'].width = 104
    metadata.column_dimensions['A'].width = 28
    metadata.column_dimensions['B'].width = 58
    mapping.column_dimensions['B'].width = 50
    mapping.column_dimensions['C'].width = 34
    destination.parent.mkdir(parents=True, exist_ok=True)
    wb.save(destination)


if __name__ == '__main__':
    output = HERE / 'Plantilla_farmacos_v3.xlsx'
    if output.exists(): raise SystemExit('Ya existe la plantilla v3; no se sobrescribe. Elige otra ruta desde create().')
    create(output)
    convert(output, HERE / 'plantilla_farmaco_v3.json')
    print(output)
