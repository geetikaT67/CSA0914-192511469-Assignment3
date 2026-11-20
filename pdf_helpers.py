import os
import sys
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak, KeepTogether, Flowable
)
from reportlab.pdfgen import canvas

os.makedirs('Final_PDF', exist_ok=True)

# ----------------------------------------------------------------------
# 1. Numbered Canvas with Running Academic Headers & Footers
# ----------------------------------------------------------------------
class AcademicNumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, total_pages):
        if self._pageNumber == 1:
            return  # Suppress headers/footers on the cover page

        self.saveState()
        self.setFont("Times-Roman", 8.5)
        self.setFillColor(colors.HexColor("#222222"))

        # Running Top Header
        self.drawString(40, 812, "Programming in Java (CSA0914)  |  Assignment 3 – CO3: Exception Handling & Multithreading")
        self.drawRightString(555, 812, "Geetika T (192511469)")
        self.setStrokeColor(colors.HexColor("#AAAAAA"))
        self.setLineWidth(0.5)
        self.line(40, 806, 555, 806)

        # Running Bottom Footer
        self.line(40, 42, 555, 42)
        self.drawString(40, 30, "University Academic Assignment  |  Department of Computer Science & Engineering")
        page_str = f"Page {self._pageNumber} of {total_pages}"
        self.drawRightString(555, 30, page_str)
        self.restoreState()


# ----------------------------------------------------------------------
# 2. Dynamic Table of Contents Tracking
# ----------------------------------------------------------------------
toc_page_numbers = {}

class TOCRegister(Flowable):
    def __init__(self, key):
        super().__init__()
        self.key = key
        self.width = 0
        self.height = 0

    def draw(self):
        toc_page_numbers[self.key] = self.canv._pageNumber


# ----------------------------------------------------------------------
# 3. Document Styles & Palette Setup
# ----------------------------------------------------------------------
styles = getSampleStyleSheet()

# Base paragraph styles
title_style = ParagraphStyle(
    'CoverTitle',
    parent=styles['Normal'],
    fontName='Times-Bold',
    fontSize=22,
    leading=26,
    alignment=1, # Center
    textColor=colors.black,
    spaceAfter=8
)

subtitle_style = ParagraphStyle(
    'CoverSubtitle',
    parent=styles['Normal'],
    fontName='Times-Roman',
    fontSize=12,
    leading=16,
    alignment=1, # Center
    textColor=colors.HexColor("#333333"),
    spaceAfter=15
)

h1_style = ParagraphStyle(
    'AcademicH1',
    parent=styles['Normal'],
    fontName='Times-Bold',
    fontSize=14,
    leading=18,
    textColor=colors.black,
    spaceBefore=12,
    spaceAfter=6,
    keepWithNext=True
)

h2_style = ParagraphStyle(
    'AcademicH2',
    parent=styles['Normal'],
    fontName='Times-Bold',
    fontSize=11.5,
    leading=15,
    textColor=colors.black,
    spaceBefore=9,
    spaceAfter=4,
    keepWithNext=True
)

h3_style = ParagraphStyle(
    'AcademicH3',
    parent=styles['Normal'],
    fontName='Times-Bold',
    fontSize=10,
    leading=13,
    textColor=colors.HexColor("#111111"),
    spaceBefore=6,
    spaceAfter=3,
    keepWithNext=True
)

body_style = ParagraphStyle(
    'AcademicBody',
    parent=styles['Normal'],
    fontName='Times-Roman',
    fontSize=9.5,
    leading=13.5,
    textColor=colors.black,
    spaceAfter=5
)

bullet_style = ParagraphStyle(
    'AcademicBullet',
    parent=styles['Normal'],
    fontName='Times-Roman',
    fontSize=9,
    leading=12.5,
    textColor=colors.black,
    leftIndent=15,
    firstLineIndent=-10,
    spaceAfter=3
)

caption_style = ParagraphStyle(
    'FigureCaption',
    parent=styles['Normal'],
    fontName='Times-Italic',
    fontSize=8.5,
    leading=11.5,
    alignment=1, # Center
    textColor=colors.HexColor("#333333"),
    spaceBefore=3,
    spaceAfter=7
)

viva_q_style = ParagraphStyle(
    'VivaQ',
    parent=styles['Normal'],
    fontName='Times-Bold',
    fontSize=9,
    leading=12,
    textColor=colors.black,
    spaceBefore=3,
    spaceAfter=1
)

viva_a_style = ParagraphStyle(
    'VivaA',
    parent=styles['Normal'],
    fontName='Times-Roman',
    fontSize=8.5,
    leading=11.5,
    textColor=colors.HexColor("#222222"),
    leftIndent=12,
    spaceAfter=4
)

table_text = ParagraphStyle(
    'TableText',
    parent=styles['Normal'],
    fontName='Times-Roman',
    fontSize=8,
    leading=10.5,
    textColor=colors.black
)

table_header = ParagraphStyle(
    'TableHeader',
    parent=styles['Normal'],
    fontName='Times-Bold',
    fontSize=8.5,
    leading=11,
    textColor=colors.black
)

code_style = ParagraphStyle(
    'CodeSnippet',
    parent=styles['Normal'],
    fontName='Courier',
    fontSize=7.5,
    leading=9.5,
    textColor=colors.HexColor("#111111")
)

# Helper function to wrap code in a clean table container
def make_code_box(code_text, width=515):
    p = Paragraph(code_text.replace('\n', '<br/>').replace(' ', '&nbsp;'), code_style)
    t = Table([[p]], colWidths=[width])
    t.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#F8F9FA")),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor("#D0D5DD")),
        ('LEFTPADDING', (0,0), (-1,-1), 7),
        ('RIGHTPADDING', (0,0), (-1,-1), 7),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
    ]))
    return t

# Helper to scale images preserving exact aspect ratios
def make_image_flowable(path, max_width=490, max_height=260):
    from PIL import Image as PILImage
    im = PILImage.open(path)
    w, h = im.size
    aspect = w / h

    # Fit within bounds
    target_w = min(max_width, w)
    target_h = target_w / aspect

    if target_h > max_height:
        target_h = max_height
        target_w = target_h * aspect

    return Image(path, width=target_w, height=target_h)

print("Styles and helper functions defined successfully.")
