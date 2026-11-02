import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
import sys
import jsonschema

ROOT=Path(__file__).resolve().parents[2]
sys.path.insert(0,str(ROOT/'content-tools'))
from convertir_plantilla import convert, FIELDS_V3
spec=importlib.util.spec_from_file_location('publisher',ROOT/'backend/publicar_paquete.py')
publisher=importlib.util.module_from_spec(spec);spec.loader.exec_module(publisher)

class PublicationTests(unittest.TestCase):
    def draft(self):
        pack=json.loads((ROOT/'content-tools/plantilla_farmaco_v3.json').read_text())
        pack['id_paquete']='catalogo-oficial'
        return pack
    def test_workbook_v3_converts_without_dropping_any_new_field(self):
        with tempfile.TemporaryDirectory() as directory:
            path=Path(directory)/'catalogo.json'
            convert(ROOT/'content-tools/Plantilla_farmacos_v3.xlsx',path)
            content=json.loads(path.read_text())
            self.assertEqual(3,content['version_esquema'])
            self.assertEqual(set(FIELDS_V3),set(content['medicamentos'][0]['secciones']))
            self.assertEqual('borrador',content['medicamentos'][0]['revision'])
    def test_partial_fields_remain_legal_without_claiming_clinical_readiness(self):
        pack=self.draft();pack['medicamentos'][0]['secciones']={'antes_administrar':''}
        record=publisher.validate(json.dumps(pack))
        self.assertFalse(record['published'])
    def test_draft_cannot_be_published(self):
        with self.assertRaises(ValueError):publisher.validate(json.dumps(self.draft()),True)
    def test_private_backup_or_wrong_channel_cannot_be_published(self):
        for key,value in [('tipo_paquete','respaldo_catalogo'),('id_paquete','respaldo-personal')]:
            pack=self.draft();pack[key]=value
            with self.assertRaises((ValueError,jsonschema.ValidationError)):publisher.validate(json.dumps(pack))
    def test_hash_is_of_original_utf8_not_reserialized_json(self):
        raw=json.dumps(self.draft(),ensure_ascii=False,indent=4)
        record=publisher.validate(raw)
        self.assertEqual(raw,record['package_json'])
        self.assertEqual(publisher.hashlib.sha256(raw.encode()).hexdigest(),record['sha256'])
    def test_unknown_fields_and_wrong_types_are_rejected(self):
        for sections in [{'inventado':'x'},{'antes_administrar':123}]:
            pack=self.draft();pack['medicamentos'][0]['secciones']=sections
            with self.assertRaises(jsonschema.ValidationError):publisher.validate(json.dumps(pack))
    def test_ids_are_unique_within_each_collection(self):
        pack=self.draft();pack['medicamentos']*=2
        with self.assertRaises(ValueError):publisher.validate(json.dumps(pack))
    def test_references_cannot_include_credentials(self):
        pack=self.draft();pack['medicamentos'][0]['fuentes']=[{'titulo':'QA','url':'https://usuario:clave@example.invalid/qa'}]
        with self.assertRaises(ValueError):publisher.validate(json.dumps(pack))
    def test_network_is_not_part_of_validation(self):
        old=publisher.urllib.request.build_opener
        publisher.urllib.request.build_opener=lambda *a: (_ for _ in ()).throw(AssertionError('No contactar red'))
        try:publisher.validate(json.dumps(self.draft()))
        finally:publisher.urllib.request.build_opener=old

if __name__=='__main__':unittest.main(verbosity=2)
