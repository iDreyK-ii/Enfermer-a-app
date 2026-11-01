"""Pruebas técnicas del conversor; ningún dato de prueba es contenido clínico."""
import contextlib
import io
import json
from pathlib import Path
import tempfile
import unittest
from datetime import date
from openpyxl import load_workbook
import jsonschema
from convertir_plantilla import convert

ROOT=Path(__file__).resolve().parent

class ConverterTests(unittest.TestCase):
    def setUp(self):
        self.tmp=tempfile.TemporaryDirectory()
        self.source=Path(self.tmp.name)/'entrada.xlsx'
        self.output=Path(self.tmp.name)/'salida.json'
        self.wb=load_workbook(ROOT/'Plantilla_contenidos_iDreyK_360.xlsx')
    def tearDown(self):self.wb.close();self.tmp.cleanup()
    def run_conversion(self):
        self.wb.save(self.source)
        with contextlib.redirect_stdout(io.StringIO()):result=convert(self.source,self.output)
        schema=json.loads((ROOT/'esquema_contenido_v1.schema.json').read_text())
        jsonschema.validate(result,schema,format_checker=jsonschema.FormatChecker())
        return result
    def test_template_round_trip(self):
        p=self.run_conversion();self.assertEqual(p['medicamentos'][0]['id'],'paracetamol');self.assertEqual(len(p['medicamentos'][0]['secciones']),20)
    def test_formulas_are_rejected(self):
        self.wb['Medicamentos']['H2']='=1+1'
        with self.assertRaisesRegex(ValueError,'fórmulas'):self.run_conversion()
        self.assertFalse(self.output.exists())
    def test_literal_text_not_interpreted(self):
        text='<script>dato ficticio</script>\nDilución y valoración: solo caracteres de prueba.'
        self.wb['Medicamentos']['H2']=text
        self.assertEqual(self.run_conversion()['medicamentos'][0]['secciones']['descripcion'],text)
    def test_decimal_version_rejected(self):
        self.wb['Metadatos']['B4']='1.5'
        with self.assertRaisesRegex(ValueError,'entero positivo'):self.run_conversion()
    def test_duplicate_drug_id_rejected(self):
        sheet=self.wb['Medicamentos']
        for c in sheet[2]:sheet.cell(3,c.column,c.value)
        with self.assertRaisesRegex(ValueError,'duplicado'):self.run_conversion()
    def test_unlinked_reference_rejected(self):
        values=['medicamento','id-inexistente','Fuente ficticia','https://example.org/qa','QA','2026-11-01']
        for i,v in enumerate(values,1):self.wb['Fuentes'].cell(2,i,v)
        with self.assertRaisesRegex(ValueError,'ID que no existe'):self.run_conversion()
    def test_twenty_items_is_a_configurable_example_not_a_standard(self):
        values=['lista-20','Lista ficticia para probar el formato','QA','borrador','','','Institución ficticia','Prueba']
        for i,v in enumerate(values,1):self.wb['Listas'].cell(2,i,v)
        for n in range(1,21):
            for i,v in enumerate(['lista-20',n,f'Ítem ficticio {n}','Prueba del formato, no protocolo'],1):self.wb['Items'].cell(n+1,i,v)
        self.assertEqual(len(self.run_conversion()['listas_seguridad'][0]['items']),20)
    def test_excel_date_preserved_as_iso(self):
        self.wb['Metadatos']['B7']=date(2026,11,1)
        self.assertEqual(self.run_conversion()['fecha_actualizacion'],'2026-11-01')
    def test_changed_headers_rejected(self):
        self.wb['Medicamentos']['A1']='IDENTIFICADOR'
        with self.assertRaisesRegex(ValueError,'encabezados'):self.run_conversion()
    def test_review_without_sources_rejected(self):
        self.wb['Medicamentos']['E2']='declarada_por_autor'
        self.wb['Medicamentos']['F2']='Revisor ficticio'
        self.wb['Medicamentos']['G2']='2026-11-01'
        with self.assertRaisesRegex(ValueError,'fuentes'):self.run_conversion()

if __name__=='__main__':unittest.main(verbosity=2)
