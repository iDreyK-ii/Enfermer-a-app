"""Corrección tipográfica de la maqueta elegida. Conserva la selección original.
No es una captura del APK ni una nueva ronda de propuestas generadas.
"""
from pathlib import Path
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
FONT = ROOT / 'app/src/main/res/font'
source = Image.open(HERE / '02_ficha_farmacologica.png').convert('RGB')
reference = source.crop((1544, 50, 2090, 1140)).resize((1080, 2156), Image.Resampling.LANCZOS)
image = Image.new('RGB', (1080, 2156))
draw = ImageDraw.Draw(image)
for y in range(2156):
    f = y / 2155
    top, bottom = (110, 77, 156), (242, 211, 232)
    color = tuple(round(a * (1-f) + b*f) for a, b in zip(top, bottom))
    draw.line((0, y, 1080, y), fill=color)

def font(size, bold=False): return ImageFont.truetype(str(FONT / ('outfit_600.ttf' if bold else 'nunito_sans_400.ttf')), size)
def text(position, value, size=38, color='#30243E', bold=False): draw.text(position, value, font=font(size,bold), fill=color)
def card(box):
    draw.rounded_rectangle(box, radius=38, fill='#FEFCFF', outline='#E4D9EA', width=2)
def centered(box,value,size=33,fill='#593471',background='#F4EAF8'):
    draw.rounded_rectangle(box,radius=30,fill=background)
    b=draw.textbbox((0,0),value,font=font(size,True)); w=b[2]-b[0];h=b[3]-b[1]
    text(((box[0]+box[2]-w)/2,(box[1]+box[3]-h)/2-6),value,size,fill,True)

# Preserve the chosen icon art, while replacing all malformed generated lettering.
draw.line([(68,48),(38,80),(68,112)],fill='white',width=7)
text((105,45),'Farmacología',53,'white',True)
points=[]
for i in range(10):
    a=-math.pi/2 + i*math.pi/5;r=31 if i%2==0 else 14
    points.append((988+math.cos(a)*r,80+math.sin(a)*r))
draw.line(points+[points[0]],fill='white',width=4)
card((28,170,1052,539))
image.paste(reference.crop((65,215,350,500)),(57,211))
text((394,223),'Ceftriaxona',65,bold=True)
text((396,313),'Ficha de demostración',36)
draw.rounded_rectangle((391,386,1008,446),radius=20,fill='#FFF1D5')
text((409,395),'BORRADOR · NO USO CLÍNICO',28,'#875117',True)
text((396,464),'Contenido pendiente de revisión',29,'#6B5A78')
for box,label,selected in [
    ((28,562,296,631),'Lo esencial',True),
    ((309,562,609,631),'Ficha técnica',False),
    ((622,562,800,631),'Otros',False),
    ((813,562,1052,631),'Fuentes',False),
]: centered(box,label,32,'white' if selected else '#593471','#63358A' if selected else '#F4EAF8')
blocks=[
    (660,'Objetivo del tratamiento','Pendiente de documentar',696),
    (908,'Antes de administrar','Controles por completar',946),
    (1156,'Preparación y\nadministración','Datos por revisar',1193),
    (1404,'Vigilancia y alertas','Pendiente de revisión',1440),
]
for y,title,subtitle,original_y in blocks:
    card((28,y,1052,y+218))
    image.paste(reference.crop((57,original_y,218,original_y+160)),(58,y+28))
    if '\n' in title:
        text((248,y+20),title,40,bold=True)
        text((248,y+139),subtitle,33,'#75647F')
    else:
        text((248,y+41),title,42,bold=True)
        text((248,y+107),subtitle,34,'#75647F')
    draw.line([(980,y+93),(997,y+108),(980,y+124)],fill='#68427C',width=5)
centered((28,1670,1052,1778),'Ver ficha técnica · 17 apartados',42,'#573071','#FEFCFF')
text((282,1804),'Fuentes y revisión visibles',32,'#61466F')
# The selected lower navigation is retained, not regenerated.
image.paste(reference.crop((0,1848,1080,2132)),(0,1872))
image.save(HERE/'02_ficha_legible.png',optimize=True)
print(HERE/'02_ficha_legible.png')
