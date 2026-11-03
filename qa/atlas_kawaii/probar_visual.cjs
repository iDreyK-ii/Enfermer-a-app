// DOM/JavaScript verification only. This does not measure browser layout, contrast or native Android.
// NODE_PATH=.cache/visual-qa/node_modules node qa/atlas_kawaii/probar_visual.cjs <gallery.html>
const fs = require('node:fs');
const assert = require('node:assert/strict');
const crypto = require('node:crypto');
const { JSDOM, VirtualConsole } = require('jsdom');
const filename = process.argv[2];
if (!filename) throw new Error('Supply the generated HTML file path');
const errors = [];
const consoleProxy = new VirtualConsole();
consoleProxy.on('jsdomError', e => errors.push(String(e)));
const dom = new JSDOM(fs.readFileSync(filename, 'utf8'), {
  runScripts: 'dangerously', virtualConsole: consoleProxy,
  beforeParse(w) {
    // jsdom does not implement modal layout. Test events/state, not visual behavior.
    w.HTMLDialogElement.prototype.showModal = function () { this.setAttribute('open', ''); };
    w.HTMLDialogElement.prototype.close = function () { this.removeAttribute('open'); };
  }
});
const d = dom.window.document;
assert.equal(d.querySelectorAll('script[src],link[rel=stylesheet],iframe').length, 0);
assert.equal(d.querySelectorAll('#inventory tbody tr').length, 24);
assert.equal(d.querySelectorAll('.asset').length, 24);
const installed = d.querySelectorAll('#instalado .capture').length;
const renders = d.querySelectorAll('#pantallas .capture').length;
for (const image of d.querySelectorAll('img[src]')) assert(image.src.startsWith('data:image/png;base64,'));
let choices = 0, visible = 0, hidden = 0;
for (const card of d.querySelectorAll('.asset')) {
  for (const b of card.querySelectorAll('.part-choice')) {
    b.click(); choices++;
    assert(b.classList.contains('active'));
    assert(card.querySelector('.part-detail strong').textContent.length > 5);
    const pin = card.querySelector('.pin');
    if (pin.hidden) hidden++;
    else {
      visible++;
      assert(parseFloat(pin.style.left) >= 0 && parseFloat(pin.style.left) <= 100);
      assert(parseFloat(pin.style.top) >= 0 && parseFloat(pin.style.top) <= 100);
    }
  }
}
assert(visible >= 100); assert(hidden >= 4);
for (const b of d.querySelectorAll('[data-bg]')) {
  b.click(); assert.equal(d.documentElement.style.getPropertyValue('--canvas'), b.dataset.bg);
}
const search = d.querySelector('#filter');
search.value = 'oxímetro'; search.dispatchEvent(new dom.window.Event('input'));
assert.equal([...d.querySelectorAll('#inventory tbody tr')].filter(r => !r.hidden).length, 1);
search.value = ''; search.dispatchEvent(new dom.window.Event('input'));
assert.equal([...d.querySelectorAll('#inventory tbody tr')].filter(r => !r.hidden).length, 24);
for (const b of d.querySelectorAll('.open-image')) {
  b.click(); assert(d.querySelector('#lightbox').hasAttribute('open'));
  assert.equal(d.querySelector('#large').src, b.querySelector('img').src);
  d.querySelector('#closebox').click(); assert(!d.querySelector('#lightbox').hasAttribute('open'));
}
assert.deepEqual(errors, []);
console.log(JSON.stringify({
  scope: 'DOM and inline JavaScript only; no browser layout/accessibility/performance assertion',
  html_sha256: crypto.createHash('sha256').update(fs.readFileSync(filename)).digest('hex'),
  assets: 24, inventory_rows: 24, apk_captures: installed, robolectric_renders: renders,
  part_choices: choices, visible_points: visible, hidden_parts: hidden,
  background_switches: 4, filter_and_modals: 'passed', script_errors: errors
}, null, 2));
dom.window.close();
