import hashlib
import json
import math
from pathlib import Path
import unittest
from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / 'app/src/main/assets'


class AtlasPngTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.master = json.loads((ASSETS/'base_maestra_mvp.json').read_text())
        cls.devices = cls.master['devices']

    def test_every_available_png_has_individual_metadata_and_matching_digest(self):
        available = [d for d in self.devices if d['file']]
        self.assertEqual(5, len(available))
        self.assertEqual(5, len({d['file'] for d in available}))
        for d in available:
            path = ASSETS / d['file']
            self.assertEqual(d, json.loads(path.with_suffix('.json').read_text()))
            self.assertEqual(d['image_sha256'], hashlib.sha256(path.read_bytes()).hexdigest())
            self.assertGreaterEqual(len(d['sources']), 2)
            self.assertIsNone(d['accuracy_approved_by'])
            self.assertFalse(d['clinical_approved'])

    def test_rgba_has_genuine_background_alpha_and_preserves_device_margins(self):
        for d in self.devices:
            if not d['file']:
                continue
            with Image.open(ASSETS/d['file']) as im:
                self.assertEqual('PNG', im.format)
                self.assertEqual('RGBA', im.mode)
                self.assertEqual((d['image_width'], d['image_height']), im.size)
                self.assertEqual(2000, max(im.size))
                a = im.getchannel('A')
                histogram = a.histogram()
                self.assertGreater(histogram[0], im.width*im.height*.25)
                self.assertGreater(sum(histogram[128:]), im.width*im.height*.02)
                self.assertGreater(sum(histogram[1:255]), 0)
                box = a.getbbox()
                self.assertGreater(box[0], 3); self.assertGreater(box[1], 3)
                self.assertLess(box[2], im.width-3); self.assertLess(box[3], im.height-3)

    def test_hotspots_are_normalized_and_on_actual_nontransparent_pixels(self):
        for d in self.devices:
            image = Image.open(ASSETS/d['file']) if d['file'] else None
            try:
                for p in d['parts']:
                    self.assertIn(p['part_status'], ('universal','modelo','opcional'))
                    self.assertTrue(p['part_description'] and p['function'] and p['source_urls'])
                    if p['visible']:
                        self.assertIsNotNone(image)
                        x,y=p['hotspot_x'],p['hotspot_y']
                        self.assertTrue(math.isfinite(x) and math.isfinite(y) and 0 <= x <= 1 and 0 <= y <= 1)
                        alpha=image.getpixel((int(x*image.width),int(y*image.height)))[3]
                        self.assertGreaterEqual(alpha,128,f"{d['id']}/{p['id']}")
                    else:
                        self.assertIsNone(p['hotspot_x']);self.assertIsNone(p['hotspot_y'])
            finally:
                if image: image.close()

    def test_no_vector_geometry_or_rejected_primary_images_are_shipped(self):
        self.assertEqual(5,len(list((ASSETS/'atlas').glob('*.png'))))
        for d in self.devices:
            self.assertTrue(all('shapes' not in p for p in d['parts']))
            if not d['file']:
                self.assertTrue(d['pending_reason'])
                self.assertTrue(all(not p['visible'] for p in d['parts']))
        source=(ROOT/'app/src/main/java/com/idreyk/enfermeria360/ScreensAtlasHygiene.kt').read_text()
        self.assertNotIn('drawRoundRect', source)
        self.assertIn('Recurso visual pendiente',source)

    def test_requested_24_are_not_reclassified_as_future_or_claimed_complete(self):
        rows=json.loads((ROOT/'qa/atlas_v22/inventario_24_png.json').read_text())
        self.assertEqual(list(range(1,25)),[r['number'] for r in rows])
        self.assertEqual(24,len({r['requested_file'] for r in rows}))
        self.assertEqual(5,sum(bool(r['file']) for r in rows))
        self.assertTrue(all(r['status'] in ('PARCIAL','PENDIENTE','BLOQUEADO') for r in rows))
        for r in rows:
            if r['file']:self.assertTrue((ROOT/r['file']).is_file())

    def test_upscaling_is_disclosed_not_claimed_as_native_detail(self):
        for d in self.devices:
            if d['file']:
                p=d['processing']
                self.assertLess(max(p['native_dimensions']),2000)
                self.assertIn('NO detalle nativo',p['processing'])
                self.assertTrue(p['native_sha256'])

    def test_previous_topics_inventory_cases_and_checklists_are_unchanged(self):
        baseline=json.loads((ROOT/'qa/atlas_v22/baseline_conservado.json').read_text())
        for key,digest in baseline['content_sections'].items():
            current=json.dumps(self.master[key],ensure_ascii=False,sort_keys=True,separators=(',',':')).encode()
            self.assertEqual(digest,hashlib.sha256(current).hexdigest(),key)
        for path,digest in baseline['unchanged_files'].items():
            self.assertEqual(digest,hashlib.sha256((ROOT/path).read_bytes()).hexdigest(),path)


if __name__ == '__main__':
    unittest.main()
