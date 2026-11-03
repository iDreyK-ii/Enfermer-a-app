#!/usr/bin/env python3
"""Ilustraciones editoriales 2D originales, no fotografías ni vectores de runtime.
Rasteriza un PNG RGBA individual por dispositivo a 2000x1250. Sin rótulos/números.
Las caritas son decoración explícita; nunca se añaden como partes ni respuestas.
"""
from pathlib import Path
import math, json, hashlib
import cairosvg
from PIL import Image
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
INK='#6c587a'; ROSE='#f1a7c7'; MINT='#a8dfd9'; LILAC='#cbb6e9'; BLUE='#a9d9eb'; CREAM='#ffe4bc'
DEFS='''<defs>
<linearGradient id="glass" x1="0" y1="0" x2="0" y2="1"><stop stop-color="#f6fffd"/><stop offset=".52" stop-color="#d8f2f2"/><stop offset="1" stop-color="#a8d8de"/></linearGradient>
<linearGradient id="lilac" x1="0" y1="0" x2="0" y2="1"><stop stop-color="#ede2fa"/><stop offset=".5" stop-color="#d5c3ee"/><stop offset="1" stop-color="#b598d6"/></linearGradient>
<linearGradient id="pink" x1="0" y1="0" x2=".2" y2="1"><stop stop-color="#ffe4ee"/><stop offset=".5" stop-color="#f3bed5"/><stop offset="1" stop-color="#de91b8"/></linearGradient>
<linearGradient id="mint" x1="0" y1="0" x2=".15" y2="1"><stop stop-color="#e3fbf1"/><stop offset=".55" stop-color="#b2e6dc"/><stop offset="1" stop-color="#84c8c3"/></linearGradient>
<linearGradient id="metal" x1="0" y1="0" x2="0" y2="1"><stop stop-color="#f7fbff"/><stop offset=".42" stop-color="#d6e1eb"/><stop offset="1" stop-color="#9aabbf"/></linearGradient>
<linearGradient id="cream" x1="0" y1="0" x2=".2" y2="1"><stop stop-color="#fff8e8"/><stop offset="1" stop-color="#f3d3a0"/></linearGradient>
<linearGradient id="shell" x1="0" y1="0" x2="0" y2="1"><stop stop-color="#fffafd"/><stop offset=".7" stop-color="#eee4f3"/><stop offset="1" stop-color="#d6c5e2"/></linearGradient>
<linearGradient id="screen" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#e1f5f8"/><stop offset="1" stop-color="#b8d7e2"/></linearGradient>
</defs>'''
class Art:
    def __init__(self,id,angle=0):self.id=id;self.angle=angle;self.svg=[];self.points={};self.face_boxes=[]
    def add(self,s):self.svg.append(s)
    def path(self,d,fill='none',stroke=INK,sw=8,extra=''):
        self.add(f'<path d="{d}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}" stroke-linecap="round" stroke-linejoin="round" {extra}/>')
    def rect(self,x,y,w,h,fill='url(#shell)',r=25,stroke=INK,sw=8,extra=''):
        self.add(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{r}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}" {extra}/>')
    def ell(self,x,y,rx,ry=None,fill='url(#shell)',stroke=INK,sw=8,extra=''):
        self.add(f'<ellipse cx="{x}" cy="{y}" rx="{rx}" ry="{ry or rx}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}" {extra}/>')
    def tube(self,d,color=MINT,w=28):
        self.path(d,stroke=INK,sw=w+12);self.path(d,stroke=color,sw=w);self.path(d,stroke='#fff',sw=max(3,w*.16),extra='opacity=".48"')
    def face(self,x,y,s=1):
        self.face_boxes.append([x-72*s,y-34*s,144*s,90*s])
        self.add(f'<g transform="translate({x} {y}) scale({s})">')
        for xx in [-35,35]:
            self.ell(xx,0,10,15,'#58415f','none',0);self.ell(xx-3,-5,3,4,'#fff','none',0)
            self.ell(xx*1.65,24,14,7,'#f5a7b8','none',0,extra='opacity=".75"')
        self.path('M -13 23 Q 0 41 13 23',stroke='#72516c',sw=6)
        self.add('</g>')
    def p(self,key,x,y):self.points[key]=(x,y)
    def normalized(self,point):
        x,y=point;a=math.radians(self.angle);dx=x-800;dy=y-500
        return [(800+dx*math.cos(a)-dy*math.sin(a))/1600,(500+dx*math.sin(a)+dy*math.cos(a))/1000]
    def export(self,directory):
        directory=Path(directory);directory.mkdir(parents=True,exist_ok=True)
        svg='<svg xmlns="http://www.w3.org/2000/svg" width="2000" height="1250" viewBox="0 0 1600 1000">'+DEFS+f'<g transform="rotate({self.angle} 800 500)">'+''.join(self.svg)+'</g></svg>'
        src=directory/(self.id+'.svg');png=directory/(self.id+'.png');src.write_text(svg)
        cairosvg.svg2png(bytestring=svg.encode(),write_to=str(png),output_width=2000,output_height=1250)
        with Image.open(png) as im:
            im=im.convert('RGBA');im.save(png,optimize=True)
        return {'id':self.id,'file':str(png),'source':str(src),'points':{k:self.normalized(v) for k,v in self.points.items()},'face_boxes_viewbox':self.face_boxes,'rotation':self.angle,'width':2000,'height':1250,'source_sha256':hashlib.sha256(svg.encode()).hexdigest(),'png_sha256':hashlib.sha256(png.read_bytes()).hexdigest()}

def syringe():
    a=Art('jeringa',-9)
    a.rect(205,463,900,74,'url(#lilac)',18)
    a.ell(210,500,34,110,'url(#lilac)')
    a.path('M 200 425 Q 187 500 200 570',stroke='#fff',sw=10)
    a.rect(548,353,730,294,'url(#glass)',48)
    # Visible internal rod and separate elastomer seal; no needle attached.
    a.rect(560,470,550,60,'#d7c4ed',14,sw=5)
    a.rect(1100,366,72,268,'#80788f',20,sw=7)
    a.path('M1115 387 V613 M1153 387 V613',stroke='#d0c6d9',sw=6)
    a.rect(520,307,48,386,'url(#glass)',20)
    a.path('M535 330 V660',stroke='#fff',sw=9)
    a.path('M1270 410 L1380 447 L1380 554 L1270 590 Z','url(#glass)')
    a.rect(1270,424,120,152,'url(#glass)',18)
    a.path('M1290 446 V553 M1311 444 V556 M1332 447 V553',stroke='#91bfc7',sw=5)
    a.path('M1352 474 L1455 482 L1455 518 L1352 526 Z','url(#glass)')
    a.ell(1454,500,10,19,'#aad3dd',sw=5)
    a.path('M604 374 H1056',stroke='#fff',sw=16)
    a.path('M596 621 H1068',stroke='#97cdd3',sw=5)
    for i,x in enumerate(range(625,1050,46)):a.path(f'M{x} 384 V{422 if i%2 else 442}',stroke='#7aa7b3',sw=5)
    a.face(839,552,.85)
    for k,x,y in [('cilindro',925,602),('embolo',390,500),('piston',1135,540),('cono',1420,500),('alas',543,339),('escala',813,400),('boton',210,537),('collar',1313,536)]:a.p(k,x,y)
    return a

def needle():
    a=Art('aguja',-10)
    a.path('M 260 330 L 440 365 L 465 438 L 440 510 L 260 546 Z','url(#pink)')
    a.ell(260,438,28,108,'url(#pink)')
    for x in [300,335,370]:a.path(f'M{x} 378 V500',stroke='#d28bab',sw=7)
    a.rect(435,408,100,60,'url(#metal)',13)
    a.path('M535 425 H1310 L1400 438 L1310 451 H535 Z','url(#metal)',sw=6)
    a.path('M1310 425 L1400 438 L1310 451 Z','#adc0d3',sw=4)
    a.face(367,430,.5)
    # Detached original protector, not a recapping instruction.
    a.path('M580 665 L1205 665 Q1290 670 1290 710 Q1290 750 1205 755 H580 Z','url(#glass)')
    a.ell(580,710,15,44,'#d4edf2',sw=6)
    a.path('M640 685 H1190',stroke='#fff',sw=11)
    for k,x,y in [('pabellon',301,502),('canula',925,438),('bisel',1333,437),('punta',1380,438),('protector',1010,721)]:a.p(k,x,y)
    return a

def stethoscope():
    a=Art('estetoscopio')
    a.tube('M570 330 C535 510 625 574 762 594 C810 604 816 670 805 726 C774 879 1011 937 1137 821',LILAC,39)
    a.tube('M945 330 C981 509 905 566 782 594',LILAC,39)
    a.tube('M570 333 L529 232 Q514 193 551 164', '#c8d9e7',27)
    a.tube('M945 333 L987 232 Q1004 193 967 164','#c8d9e7',27)
    a.path('M585 399 Q757 506 930 399',stroke=INK,sw=23)
    a.path('M585 399 Q757 506 930 399',stroke='#d8c8e8',sw=12)
    a.ell(565,154,43,29,'url(#mint)',extra='transform="rotate(-25 565 154)"')
    a.ell(952,154,43,29,'url(#mint)',extra='transform="rotate(25 952 154)"')
    a.rect(1100,687,61,132,'url(#metal)',17)
    a.ell(1130,688,143,116,'url(#metal)')
    a.ell(1130,669,132,109,'url(#pink)')
    a.ell(1130,659,108,88,'url(#glass)',sw=6)
    a.path('M1050 632 Q1100 583 1170 607',stroke='#fff',sw=13)
    a.face(1130,666,.74)
    for k,x,y in [('olivas',558,157),('arcos',965,286),('resorte',756,450),('tubo',816,748),('vastago',1130,795),('diafragma',1131,611),('cabezal',1237,709)]:a.p(k,x,y)
    return a

def catheter():
    a=Art('cateter-periferico',-8)
    a.path('M710 470 L630 320 Q626 302 653 298 L839 320 Q875 326 870 352 L815 470 Z','url(#mint)')
    a.path('M710 531 L625 683 Q621 704 650 708 L840 681 Q876 675 870 651 L815 531 Z','url(#mint)')
    a.path('M672 337 L811 351 M674 667 L812 650',stroke='#fff',sw=10)
    a.rect(285,439,390,120,'url(#glass)',42)
    a.rect(280,434,60,132,'url(#lilac)',17)
    a.rect(565,423,151,154,'url(#shell)',22)
    a.path('M710 446 H858 L905 470 V530 L858 554 H710 Z','url(#glass)')
    a.rect(909,484,410,32,'url(#glass)',9,sw=6)
    a.path('M906 495 H1367 L1420 500 L1367 505 H906 Z','url(#metal)',sw=4)
    a.path('M362 459 H529',stroke='#fff',sw=11)
    a.face(445,500,.58)
    for k,x,y in [('cateter',1173,487),('aguja-introductora',1370,500),('conexion',816,538),('camara',389,465),('alas',737,645)]:a.p(k,x,y)
    return a

def roller(a,x,y):
    a.rect(x-39,y-78,78,156,'url(#lilac)',19)
    a.rect(x-21,y-55,42,111,'#e9dff5',10,sw=5)
    a.ell(x,y+8,29,31,'url(#pink)',sw=5)
    for dx in [-14,0,14]:a.path(f'M{x+dx} {y-5} V{y+21}',stroke='#b97a9b',sw=4)

def ivset():
    a=Art('equipo-iv')
    a.path('M430 97 L454 187 H406 Z','url(#glass)',sw=7)
    a.rect(391,184,78,37,'url(#mint)',10)
    a.rect(453,224,70,30,'url(#glass)',8,sw=6)
    a.rect(508,207,26,65,'url(#lilac)',8,sw=6)
    a.rect(388,226,84,204,'url(#glass)',28)
    a.path('M391 356 Q430 367 469 356',stroke='#89bdcc',sw=5)
    a.path('M409 250 V318',stroke='#fff',sw=11)
    a.face(430,385,.34)
    a.tube('M430 430 V676 C430 848 631 910 738 784 C836 674 707 576 834 495 C948 423 1087 528 1014 655 C954 757 1170 868 1288 704 V462', '#c3e8e7',23)
    roller(a,430,555)
    a.path('M1267 638 L1209 601 L1181 565 L1206 545 L1235 585 L1300 607 Z','url(#glass)',sw=6)
    a.rect(1164,533,52,32,'url(#pink)',8,sw=6,extra='transform="rotate(-38 1190 549)"')
    a.rect(1267,416,42,65,'url(#glass)',10,sw=6)
    a.rect(1255,379,66,51,'url(#lilac)',12,sw=6)
    a.rect(1265,348,46,36,'url(#glass)',10,sw=6)
    for k,x,y in [('punzon',430,156),('venteo',519,238),('camara',409,299),('tubo',618,848),('roller',430,557),('sitio-y',1230,597),('luer',1290,400)]:a.p(k,x,y)
    return a

def burette():
    a=Art('bureta')
    a.path('M471 90 L492 173 H450 Z','url(#glass)',sw=6)
    a.rect(445,173,53,32,'url(#mint)',8,sw=6)
    a.tube('M472 204 V307 Q472 333 520 333 H651',MINT,19)
    a.rect(451,250,44,47,'url(#lilac)',10,sw=6)
    a.path('M456 265 H485',stroke='#fff',sw=6)
    a.rect(620,326,153,331,'url(#glass)',29)
    a.rect(606,303,180,45,'url(#lilac)',12)
    a.rect(654,270,35,40,'url(#glass)',8,sw=5)
    a.rect(644,249,55,28,'url(#mint)',8,sw=5)
    a.rect(725,269,36,39,'url(#glass)',8,sw=5)
    a.rect(718,252,50,25,'url(#pink)',7,sw=5)
    a.path('M641 372 V555',stroke='#fff',sw=12)
    for i,y in enumerate(range(371,580,27)):a.path(f'M748 {y} H{719 if i%2 else 697}',stroke='#77a2ac',sw=5)
    a.face(689,610,.43)
    a.rect(617,644,158,31,'url(#lilac)',10,sw=6)
    a.rect(666,676,60,121,'url(#glass)',20,sw=6)
    a.path('M674 744 Q696 754 718 744',stroke='#93c1cb',sw=5)
    a.tube('M696 797 V842 C696 919 1091 922 1174 790 V464',MINT,21)
    roller(a,1174,689)
    a.rect(1151,421,46,69,'url(#glass)',9,sw=6)
    a.rect(1139,392,70,40,'url(#lilac)',10,sw=6)
    for k,x,y in [('entrada',472,317),('pinza',473,273),('bureta',653,457),('ventilacion',673,261),('camara-goteo',695,731),('salida',1175,450),('puerto',742,264),('pinza-salida',1174,688)]:a.p(k,x,y)
    return a

def stopcock():
    a=Art('llave-tres-vias',-5)
    # Exactly three branches. The handle is a solid control, not a fourth port.
    a.path('M594 406 H370 V566 H594 Q671 643 713 652 V814 H887 V652 Q929 643 1006 566 H1230 V406 H1006 Q800 273 594 406 Z','url(#glass)')
    a.rect(304,390,117,191,'url(#glass)',23)
    a.rect(1179,390,117,191,'url(#glass)',23)
    a.ell(309,485,23,84,'#d9eef3',sw=6)
    a.ell(1291,485,23,84,'#d9eef3',sw=6)
    a.rect(700,755,200,68,'url(#lilac)',19)
    a.rect(753,818,94,87,'url(#glass)',18)
    for x in [730,758,786,814,842,870]:a.path(f'M{x} 769 V808',stroke='#a58bbd',sw=5)
    a.ell(800,490,158,139,'url(#glass)')
    a.path('M746 445 L636 315 Q614 288 639 271 L677 250 Q694 240 713 263 L800 382 L899 266 Q915 247 936 263 L965 284 Q986 299 971 321 L860 445 Q874 490 851 521 Q803 565 751 525 Q725 498 746 445 Z','url(#pink)')
    a.face(800,485,.63)
    a.path('M478 430 H565 M1038 430 H1140',stroke='#fff',sw=12)
    for k,x,y in [('cuerpo',909,552),('selector',689,297),('puerto-1',362,482),('puerto-2',1243,482),('puerto-3',802,854)]:a.p(k,x,y)
    return a

def aneroid():
    a=Art('tensiometro')
    a.tube('M628 631 C631 815 1019 870 1018 591',LILAC,29)
    a.tube('M704 633 C742 909 1276 924 1290 629',LILAC,29)
    a.path('M216 273 Q486 204 782 291 L784 656 Q519 721 219 635 Z','url(#mint)')
    a.path('M235 299 Q509 246 757 311 V625 Q498 677 242 613 Z','none','#7fb6b5',5,extra='stroke-dasharray="10 13"')
    a.path('M605 280 L781 301 V643 L605 675 Z','url(#mint)',sw=6)
    a.rect(634,343,110,228,'#8fcac3',18,sw=5)
    for y in range(361,560,22):a.path(f'M650 {y} H727',stroke='#bce9df',sw=5)
    a.face(428,453,.94)
    a.ell(1015,463,172,170,'url(#metal)')
    a.ell(1015,458,145,142,'#fff9f3',sw=7)
    for i in range(21):
        th=math.radians(135+i*13.5);r=126;ri=109 if i%2 else 99
        a.path(f'M{1015+math.cos(th)*r:.2f} {458+math.sin(th)*r:.2f} L{1015+math.cos(th)*ri:.2f} {458+math.sin(th)*ri:.2f}',stroke='#b5a5c2',sw=5)
    # Needle omitted: the drawing is not a calibrated pressure reading.
    a.ell(1015,458,12,12,'#9c8aa9',sw=4)
    a.rect(1256,545,67,84,'url(#metal)',13)
    a.rect(1307,563,57,34,'url(#metal)',8,sw=5)
    a.ell(1290,438,81,112,'url(#pink)')
    a.path('M1251 382 Q1231 432 1249 477',stroke='#fff',sw=11)
    for k,x,y in [('manguito',353,330),('manometro',1020,373),('pera',1296,423),('valvula',1341,578),('tubos',999,837),('cierre',689,521)]:a.p(k,x,y)
    return a

def oximeter():
    a=Art('pulsioximetro',-6)
    a.path('M450 579 Q447 717 623 754 H1058 Q1145 743 1150 650 L1092 590 Z','url(#lilac)')
    a.path('M472 547 Q471 648 615 677 H1067 Q1112 660 1110 589 Z','#746683',sw=6)
    a.path('M448 477 Q439 299 624 272 H1025 Q1155 284 1151 438 L1113 580 Q768 652 451 576 Z','url(#pink)')
    a.path('M521 410 Q537 331 635 328 H1001 Q1064 329 1076 384 L1063 501 Q822 545 538 500 Z','url(#screen)',sw=7)
    a.path('M578 375 H851',stroke='#fff',sw=16)
    a.ell(1138,570,31,51,'url(#metal)',sw=6)
    a.path('M600 618 Q812 657 1046 614',stroke='#b8a6c9',sw=10)
    a.face(758,708,.56)
    for k,x,y in [('pantalla',770,414),('carcasa',483,477),('bisagra',1137,568),('alojamiento',975,625)]:a.p(k,x,y)
    return a

def volume_pump():
    a=Art('bomba-volumetrica')
    a.tube('M450 235 V133 Q450 108 480 108 H605',MINT,18)
    a.tube('M455 687 V785 Q455 864 609 864 H1245',MINT,18)
    a.rect(291,260,1001,448,'url(#shell)',70)
    a.path('M324 299 H1251 V640 Q1251 671 1217 673 H367 Q330 672 330 638 Z','url(#lilac)',sw=7)
    a.rect(362,326,440,216,'url(#screen)',26)
    a.path('M398 356 H682',stroke='#fff',sw=13)
    a.rect(848,327,70,34,'url(#pink)',12,sw=5)
    a.rect(949,327,70,34,'url(#mint)',12,sw=5)
    for x,y in [(894,416),(959,416),(1024,416),(959,478),(1091,478),(1152,416)]:a.rect(x-22,y-21,45,42,'url(#shell)',13,sw=5)
    a.rect(354,562,814,87,'url(#shell)',24,sw=7)
    a.rect(1190,558,47,91,'url(#pink)',15,sw=6)
    a.face(748,603,.47)
    for k,x,y in [('pantalla',552,439),('teclado',960,415),('puerta',480,602),('cierre',1212,600),('indicadores',878,344),('linea',455,757)]:a.p(k,x,y)
    return a

def syringe_pump():
    a=Art('bomba-jeringa')
    a.rect(247,293,1011,387,'url(#shell)',60)
    a.rect(281,321,901,219,'url(#lilac)',34)
    a.rect(315,348,381,145,'url(#screen)',20)
    a.path('M346 374 H571',stroke='#fff',sw=12)
    for x,y in [(775,385),(838,385),(900,385),(838,454),(968,454),(1070,387)]:a.rect(x-20,y-19,42,39,'url(#shell)',12,sw=5)
    a.rect(941,332,53,24,'url(#pink)',8,sw=5)
    a.rect(1016,332,53,24,'url(#mint)',8,sw=5)
    a.rect(390,570,481,104,'url(#glass)',20,sw=6)
    a.rect(854,570,42,106,'#a49aaf',13,sw=5)
    a.rect(892,603,196,41,'url(#lilac)',9,sw=5)
    a.rect(1069,567,36,113,'url(#lilac)',10,sw=5)
    a.path('M388 594 L328 604 V640 L388 651 Z','url(#glass)',sw=6)
    a.rect(525,542,75,162,'url(#pink)',22,sw=7)
    a.path('M540 559 Q561 549 583 563',stroke='#fff',sw=7)
    a.rect(1105,531,102,180,'url(#mint)',23)
    a.rect(1202,567,109,92,'url(#metal)',19)
    a.tube('M327 622 H215 Q157 623 166 710 Q174 784 278 798 H743',MINT,18)
    a.face(796,513,.4)
    for k,x,y in [('pantalla',483,429),('teclado',835,385),('jeringa',697,622),('sujecion',563,622),('cabezal',1155,622),('brazo',1254,611),('extension',417,798)]:a.p(k,x,y)
    return a

def patient_monitor():
    a=Art('monitor')
    a.path('M615 255 V192 Q615 156 660 156 H947 Q993 156 993 195 V257',stroke=INK,sw=50)
    a.path('M615 255 V192 Q615 156 660 156 H947 Q993 156 993 195 V257',stroke=LILAC,sw=33)
    a.rect(316,246,974,501,'url(#shell)',60)
    a.rect(344,274,808,385,'url(#lilac)',34,sw=7)
    a.rect(374,305,747,322,'url(#screen)',24,sw=6)
    a.path('M415 340 H726',stroke='#fff',sw=17)
    a.rect(1128,258,73,24,'url(#pink)',7,sw=5)
    for y in [354,417,480]:a.rect(1190,y,47,38,'url(#shell)',11,sw=5)
    a.ell(1214,578,42,42,'url(#mint)',sw=6)
    a.rect(378,747,139,32,'url(#lilac)',10,sw=6)
    a.rect(1089,747,137,32,'url(#lilac)',10,sw=6)
    a.face(750,698,.58)
    a.path('M316 524 H292 Q265 524 265 563 V625',stroke=INK,sw=21)
    a.path('M316 524 H292 Q265 524 265 563 V625',stroke=MINT,sw=10)
    for k,x,y in [('pantalla',748,460),('controles',1213,431),('selector',1214,579),('alarma',1162,269),('asa',806,166),('conexion',287,524)]:a.p(k,x,y)
    return a

def cannula():
    a=Art('canula-nasal')
    a.tube('M650 312 C550 229 314 316 310 499 C307 693 588 600 670 725', '#c5e8e8',25)
    a.tube('M957 312 C1087 218 1329 341 1267 519 C1217 662 995 602 922 725','#c5e8e8',25)
    a.tube('M670 725 Q797 810 922 725',MINT,22)
    a.tube('M795 785 Q792 894 965 889 H1294',MINT,25)
    a.rect(625,271,358,94,'url(#glass)',35)
    a.path('M696 279 V206 Q694 164 731 159 Q755 164 750 186 V279 Z','url(#glass)',sw=7)
    a.path('M858 279 V206 Q860 164 823 159 Q799 164 804 186 V279 Z','url(#glass)',sw=7)
    a.ell(733,173,14,7,'#a5cbd5',sw=4)
    a.ell(822,173,14,7,'#a5cbd5',sw=4)
    a.rect(752,724,87,76,'url(#lilac)',23,sw=7)
    a.rect(1268,862,106,55,'url(#lilac)',15,sw=7)
    a.ell(1370,890,13,27,'url(#glass)',sw=5)
    a.face(786,315,.42)
    for k,x,y in [('puntas',727,220),('puente',938,315),('ramales',310,478),('ajustador',795,757),('tubo',1110,889),('conector',1330,889)]:a.p(k,x,y)
    return a

def mask_base(a,kind):
    # EcoLite-type soft nose seal, no invented metal nose clip.
    a.tube('M520 375 C327 162 1230 106 1109 378',LILAC,26)
    a.path('M814 168 Q862 169 918 241 L1103 471 Q1166 582 1027 643 Q817 733 596 637 Q451 578 515 468 L704 237 Q760 170 814 168 Z','url(#mint)')
    a.path('M814 196 Q855 194 895 256 L1065 473 Q1118 552 1008 600 Q814 681 617 598 Q498 547 549 476 L728 257 Q770 195 814 196 Z','url(#glass)',sw=6)
    a.path('M728 270 Q812 216 889 271',stroke='#c9e6df',sw=14)
    a.path('M604 444 Q630 365 692 314',stroke='#fff',sw=14)
    a.face(813,382,.74)
    if kind=='simple' or kind=='venturi':
        for x in [588,1039]:
            for y in [478,511,544]:a.ell(x,y,15,11,'#b0d4db',sw=4)
    else:
        for x in [598,1027]:
            a.ell(x,513,43,46,'url(#shell)',sw=6)
            a.ell(x,513,27,30,'url(#glass)',sw=4)
            a.path(f'M{x-22} 513 H{x+22}',stroke='#9d8aac',sw=5)
    a.rect(775,551,77,89,'url(#glass)',15,sw=6)
    for k,x,y in [('mascara',671,568),('sello',936,646),('cinta',1062,238),('puerto',814,592)]:a.p(k,x,y)

def simple_mask():
    a=Art('mascarilla-simple');mask_base(a,'simple')
    a.tube('M814 641 V748 Q811 880 1057 870 H1291',MINT,24)
    a.rect(1271,846,103,48,'url(#lilac)',13,sw=6)
    a.p('orificios',1039,510);a.p('tubo',1080,870);a.p('conector',1320,870)
    return a

def reservoir_mask():
    a=Art('mascarilla-reservorio');mask_base(a,'reservorio')
    a.rect(779,638,71,43,'url(#glass)',10,sw=6)
    a.path('M787 679 Q736 714 717 796 Q690 905 815 931 Q950 904 915 795 Q897 715 842 679 Z','url(#glass)',sw=7)
    a.path('M751 796 Q728 860 784 895',stroke='#fff',sw=12)
    a.tube('M849 656 H963 Q1050 659 1050 756 Q1048 865 1230 852 H1370',MINT,22)
    a.rect(1351,829,83,46,'url(#lilac)',11,sw=6)
    a.p('valvulas',1028,513);a.p('valvula-reservorio',815,658);a.p('reservorio',827,822);a.p('tubo',1104,831)
    return a

def venturi_mask():
    a=Art('mascarilla-venturi');mask_base(a,'venturi')
    a.rect(773,638,82,89,'url(#glass)',16,sw=6)
    for y in range(650,720,14):a.path(f'M780 {y} H849',stroke='#8dbbc7',sw=4)
    a.path('M778 730 H851 L845 819 H784 Z','url(#lilac)',sw=6)
    a.rect(771,726,88,20,'url(#lilac)',7,sw=5)
    a.rect(788,751,18,48,'#c1dfe6',6,sw=4)
    a.rect(823,751,18,48,'#c1dfe6',6,sw=4)
    a.tube('M813 821 V856 Q811 914 1070 893 H1305',MINT,22)
    a.rect(1284,869,99,48,'url(#lilac)',11,sw=6)
    a.p('orificios',588,511);a.p('corrugado',814,686);a.p('adaptador',840,800);a.p('entrada-aire',797,775);a.p('tubo',1122,891)
    return a

def bvm():
    a=Art('bvm')
    # One complete assembly: mask, patient valve, bag, inlet and reservoir.
    a.path('M263 515 Q228 534 195 614 Q176 674 247 701 Q350 739 447 683 Q482 654 444 606 L385 527 Z','url(#mint)')
    a.path('M269 536 Q235 558 217 612 Q198 656 259 674 Q352 709 423 666 Q446 650 419 613 L371 545 Z','url(#glass)',sw=6)
    a.rect(291,440,72,105,'url(#glass)',14,sw=6)
    a.path('M312 434 V376 H488 V453 H362 Z','url(#glass)',sw=7)
    a.rect(347,341,77,51,'url(#shell)',13,sw=6)
    a.rect(371,316,32,30,'url(#pink)',8,sw=5)
    a.ell(478,414,28,45,'url(#lilac)',sw=6)
    a.path('M513 349 Q628 239 831 297 Q926 323 959 412 Q921 510 812 536 Q617 584 512 477 Z','url(#mint)')
    a.path('M545 332 Q672 241 810 301',stroke=INK,sw=27)
    a.path('M545 332 Q672 241 810 301',stroke=LILAC,sw=15)
    a.path('M561 375 Q622 324 687 331',stroke='#fff',sw=14)
    a.face(715,426,.84)
    a.rect(942,374,68,79,'url(#glass)',16,sw=6)
    a.path('M1010 391 Q1154 315 1328 371 Q1418 407 1360 465 Q1197 556 1009 435 Z','url(#glass)',sw=7)
    a.path('M1072 397 Q1201 359 1301 390',stroke='#fff',sw=14)
    a.tube('M974 452 V607 Q967 780 1158 759 H1348',MINT,20)
    a.rect(1324,738,79,42,'url(#lilac)',10,sw=6)
    for k,x,y in [('mascara',303,635),('valvula-paciente',415,415),('bolsa',849,447),('entrada',974,410),('reservorio',1229,453),('tubo',1202,759),('limite-presion',385,343),('asa',651,289)]:a.p(k,x,y)
    return a

def foley(three=False):
    a=Art('foley-tres-vias' if three else 'foley-dos-vias')
    a.tube('M426 665 H715 C934 665 1067 540 1212 376 L1400 197','#eedfc8',41)
    a.path('M1155 366 Q1124 316 1182 286 Q1229 267 1263 302 Q1326 334 1278 393 Q1230 445 1180 424 Z','url(#glass)',sw=7)
    a.tube('M1197 389 L1261 324','#eadbc8',30)
    # Distal closed rounded tip and two drainage eyes, beyond the balloon.
    a.ell(1334,263,12,26,'#a9c6cd',sw=4,extra='transform="rotate(44 1334 263)"')
    a.ell(1374,223,12,24,'#a9c6cd',sw=4,extra='transform="rotate(44 1374 223)"')
    a.path('M247 602 L433 641 V690 L247 728 Z','url(#cream)',sw=7)
    a.ell(250,665,24,62,'url(#cream)',sw=6)
    a.path('M535 645 Q460 535 375 489 L351 523 Q448 580 478 657 Z','url(#cream)',sw=7)
    a.rect(295,457,100,61,'url(#lilac)',15,sw=6,extra='transform="rotate(29 345 487)"')
    a.ell(303,465,12,27,'url(#shell)',sw=5,extra='transform="rotate(29 303 465)"')
    if three:
        a.path('M531 686 Q470 789 371 831 L346 796 Q423 755 474 674 Z','url(#cream)',sw=7)
        a.path('M374 769 L233 802 L252 883 L397 831 Z','url(#mint)',sw=7)
        a.ell(246,843,15,40,'url(#glass)',sw=5,extra='transform="rotate(-13 246 843)"')
        a.p('irrigacion',315,828)
    a.face(340,663,.44)
    for k,x,y in [('drenaje',276,665),('balon',1205,326),('valvula',342,488),('cuerpo',884,626),('ojos',1334,264),('punta',1393,204)]:a.p(k,x,y)
    return a

def ngtube():
    a=Art('sonda-nasogastrica')
    d='M357 311 C498 288 411 757 697 803 C942 842 1209 704 1174 516 C1141 341 952 353 892 434 C796 562 1062 748 1320 779'
    a.tube(d,'#d2e8df',34)
    a.path(d,stroke='#a3a7d0',sw=6)
    a.path('M212 242 L354 282 L375 340 L213 365 Q191 303 212 242 Z','url(#pink)',sw=7)
    a.ell(212,303,18,61,'url(#pink)',sw=6)
    a.face(291,306,.4)
    for i,(x,y) in enumerate([(444,473),(452,536),(477,610),(511,675),(557,738)]):a.path(f'M{x-14} {y+6} L{x+14} {y-6}',stroke='#8498ae',sw=5)
    for x,y,ang in [(1174,755,16),(1213,767,12),(1253,774,9),(1293,779,5)]:a.ell(x,y,12,7,'#89b4bf',sw=3,extra=f'transform="rotate({ang} {x} {y})"')
    for k,x,y in [('conector',235,330),('tubo',744,803),('marcas',480,610),('linea',1143,441),('ojos',1253,774),('punta',1321,778)]:a.p(k,x,y)
    return a

def ett():
    a=Art('tubo-endotraqueal')
    d='M380 237 C348 394 504 662 743 734 C922 791 1189 721 1339 561'
    a.tube(d,'#cce9e7',79)
    a.path(d,stroke='#8eb4ce',sw=7)
    a.path('M303 146 L418 169 L396 272 L284 247 Z','url(#lilac)',sw=7)
    a.ell(359,155,59,22,'url(#glass)',sw=6,extra='transform="rotate(12 359 155)"')
    # Expanded cuff for anatomy recognition, not a pressure/volume demonstration.
    a.path('M1098 621 Q1113 559 1171 585 L1233 632 Q1262 691 1193 734 Q1127 762 1081 705 Z','url(#glass)',sw=7)
    a.path('M1131 658 L1184 692',stroke='#bddddc',sw=48)
    a.path('M1124 611 Q1145 600 1167 616',stroke='#fff',sw=11)
    a.ell(1334,563,40,21,'#aacad1',sw=6,extra='transform="rotate(-48 1334 563)"')
    a.ell(1288,607,23,13,'#91bac5',sw=4,extra='transform="rotate(-43 1288 607)"')
    a.tube('M561 637 Q599 468 788 469 H964',MINT,14)
    a.ell(1050,469,85,38,'url(#lilac)',sw=6)
    a.rect(1130,449,70,40,'url(#shell)',10,sw=6)
    for x,y,ang in [(419,476,-23),(457,550,-35),(503,604,-44)]:a.path(f'M{x-30} {y} H{x+30}',stroke='#8b9eac',sw=5,extra=f'transform="rotate({ang} {x} {y})"')
    a.face(1050,468,.37)
    for k,x,y in [('tubo',767,742),('conector',358,216),('balon',1147,717),('linea',753,471),('piloto',994,469),('valvula',1170,468),('murphy',1289,608),('bisel',1335,564),('marcas',459,548)]:a.p(k,x,y)
    return a

def trach():
    a=Art('traqueostomia')
    a.tube('M748 406 C767 575 902 728 1147 756', '#cdebe9',94)
    a.path('M981 673 Q1006 611 1060 635 L1118 692 Q1156 754 1118 810 Q1080 848 1018 807 Z','url(#glass)',sw=7)
    a.path('M1023 731 L1095 753',stroke='#b6dcd9',sw=61)
    a.ell(1148,756,26,47,'#abcbd1',sw=6,extra='transform="rotate(-5 1148 756)"')
    # Clear flange with tie holes, no speaking valve or subglottic suction port.
    a.path('M448 341 Q598 306 688 343 Q749 320 820 345 Q920 310 1060 341 L1035 461 Q927 486 826 455 Q749 484 681 457 Q554 485 471 459 Z','url(#glass)',sw=7)
    a.rect(492,365,53,61,'#d8f2ef',18,sw=5)
    a.rect(964,365,53,61,'#d8f2ef',18,sw=5)
    a.rect(694,281,117,155,'url(#lilac)',29,sw=7)
    a.ell(751,284,57,26,'url(#glass)',sw=6)
    a.ell(751,282,36,14,'#a9c9d3',sw=5)
    a.path('M711 317 V373 M791 317 V373',stroke='#e8dcf4',sw=9)
    a.tube('M779 495 Q597 522 540 637 Q483 752 339 698',MINT,15)
    a.ell(281,665,74,33,'url(#lilac)',sw=6,extra='transform="rotate(25 281 665)"')
    a.rect(165,608,75,39,'url(#shell)',10,sw=6,extra='transform="rotate(25 203 627)"')
    a.face(612,399,.4)
    for k,x,y in [('canula',844,621),('placa',924,424),('orificios',991,396),('conector',751,326),('cuff',1047,661),('linea',528,659),('piloto',284,665),('valvula',202,627),('canula-interna',751,281)]:a.p(k,x,y)
    return a

def cvc():
    a=Art('cvc-triple')
    for y,color in [(244,ROSE),(427,BLUE),(610,LILAC)]:
        a.tube(f'M377 {y} C560 {y} 527 438 727 457', '#d2ebea',28)
        a.rect(231,y-36,145,72,'url(#glass)',18,sw=6)
        a.rect(211,y-42,57,84,color,16,sw=6)
        a.rect(453,y-28,64,80,color,14,sw=6)
        a.path(f'M466 {y+1} H502',stroke='#fff',sw=7)
    a.path('M688 401 L794 399 L839 423 V496 L794 519 L688 517 Z','url(#glass)',sw=7)
    a.path('M721 399 L683 332 H804 L783 400 Z','url(#mint)',sw=6)
    a.path('M722 520 L684 588 H804 L783 519 Z','url(#mint)',sw=6)
    a.ell(743,352,16,10,'#d8f2ea',sw=4);a.ell(743,568,16,10,'#d8f2ea',sw=4)
    a.tube('M839 459 C1084 380 1379 393 1357 617 C1342 788 1112 849 910 799', '#d8e4e8',35)
    a.path('M910 799 L867 788',stroke=INK,sw=43)
    a.path('M910 799 L867 788',stroke='#d8e4e8',sw=31)
    a.ell(865,788,9,17,'#9abac7',sw=4,extra='transform="rotate(-70 865 788)"')
    a.ell(931,803,13,7,'#8eacb7',sw=3);a.ell(986,810,13,7,'#8eacb7',sw=3)
    for x,y,ang in [(996,422,-10),(1067,415,0),(1133,421,8),(1201,437,20)]:a.path(f'M{x} {y-15} V{y+15}',stroke='#8994a7',sw=5,extra=f'transform="rotate({ang} {x} {y})"')
    a.face(754,459,.44)
    for k,x,y in [('ramales',576,427),('conectores',301,242),('pinzas',484,455),('fijacion',746,567),('cateter',1312,731),('marcas',1133,421),('orificios',932,803),('punta',867,789)]:a.p(k,x,y)
    return a

def defibrillator():
    a=Art('desfibrilador')
    a.path('M685 288 V205 Q683 164 728 164 H965 Q1013 164 1013 209 V288',stroke=INK,sw=49)
    a.path('M685 288 V205 Q683 164 728 164 H965 Q1013 164 1013 209 V288',stroke=LILAC,sw=32)
    a.rect(325,277,1051,479,'url(#shell)',57)
    a.rect(359,311,682,316,'url(#lilac)',29,sw=7)
    a.rect(384,335,632,264,'url(#screen)',20,sw=6)
    a.path('M419 368 H705',stroke='#fff',sw=16)
    a.ell(1162,392,56,56,'url(#mint)',sw=7)
    a.path('M1140 410 L1180 375',stroke='#799d9e',sw=10)
    for x,col in [(1090,'url(#cream)'),(1193,'url(#pink)'),(1290,'url(#lilac)')]:a.rect(x-30,492,63,58,col,16,sw=6)
    for x in [412,501,590,679,768,857,946]:a.rect(x-22,647,48,29,'url(#shell)',8,sw=5)
    a.rect(1074,634,237,66,'url(#lilac)',14,sw=6)
    a.path('M1102 660 H1286',stroke='#807087',sw=9)
    a.face(716,710,.47)
    # Optional external paddles parked together, connected to the device.
    a.tube('M337 611 C186 679 170 505 251 434 C309 378 270 275 226 256',LILAC,18)
    for x,y in [(221,262),(431,215)]:
        a.rect(x-82,y+49,151,64,'url(#metal)',19,sw=7)
        a.path(f'M{x-65} {y+55} V{y+4} Q{x-62} {y-32} {x-23} {y-32} H{x+24} Q{x+58} {y-31} {x+58} {y+4} V{y+55} Z','url(#lilac)',sw=7)
        a.rect(x-29,y-10,54,30,'url(#pink)',9,sw=5)
    a.tube('M430 329 Q505 404 325 503',LILAC,18)
    for k,x,y in [('pantalla',699,473),('selector',1162,392),('controles',1193,520),('palas',213,339),('cable',208,566),('asa',835,165),('registrador',1190,666)]:a.p(k,x,y)
    return a

ALL=[stethoscope,syringe,needle,catheter,ivset,burette,stopcock,aneroid,oximeter,volume_pump,syringe_pump,patient_monitor,cannula,simple_mask,reservoir_mask,venturi_mask,bvm,lambda:foley(False),lambda:foley(True),ngtube,ett,trach,cvc,defibrillator]
if __name__=='__main__':
    import argparse
    p=argparse.ArgumentParser();p.add_argument('--out',type=Path,default=ROOT/'.cache/atlas-kawaii');p.add_argument('--ids',nargs='*');args=p.parse_args()
    arts=[f() for f in ALL];arts=[a for a in arts if not args.ids or a.id in args.ids]
    result=[a.export(args.out) for a in arts]
    (args.out/'ilustraciones.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps([{k:v for k,v in r.items() if k in ('id','file','width','height')} for r in result],ensure_ascii=False))
