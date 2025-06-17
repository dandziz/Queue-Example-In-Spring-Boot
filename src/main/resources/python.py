import xml.etree.ElementTree as ET
from xml.dom import minidom

fields_ = """
private String a;
private List<String> b;
private Long c;
"""

fields = [x.strip() + ";" for x in fields_.split(";") if x.strip()]

root = ET.Element("mxGraphModel")
doc_root = ET.SubElement(root, "root")

ET.SubElement(doc_root, "mxCell", id="0")
ET.SubElement(doc_root, "mxCell", id="1", parent="0")

# Class container
class_cell = ET.SubElement(doc_root, "mxCell", {
    "id": "2",
    "value": "Classname",
    "style": "swimlane;fontStyle=1;align=center;verticalAlign=top;childLayout=stackLayout;horizontal=1;startSize=26;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;",
    "vertex": "1",
    "parent": "1"
})
ET.SubElement(class_cell, "mxGeometry", {
    "x": "190",
    "y": "260",
    "width": "160",
    "height": str(26 * (len(fields) + 2))  # fields + separator + 1 method
}).set("as", "geometry")

# Add fields
y_offset = 26
id_counter = 3
for line in fields:
    line = line.strip().replace(";", "")
    if not line.startswith("private"):
        continue
    _, field_type, field_name = line.split(None, 2)
    field_name = field_name.strip()

    field_value = f"- {field_name}: {field_type}"
    field_cell = ET.SubElement(doc_root, "mxCell", {
        "id": str(id_counter),
        "value": field_value,
        "style": "text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;",
        "vertex": "1",
        "parent": "2"
    })
    ET.SubElement(field_cell, "mxGeometry", {
        "y": str(y_offset),
        "width": "160",
        "height": "26"
    }).set("as", "geometry")
    y_offset += 26
    id_counter += 1

# Separator line
ET.SubElement(doc_root, "mxCell", {
    "id": str(id_counter),
    "value": "",
    "style": "line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;spacingTop=-1;spacingLeft=3;spacingRight=3;rotatable=0;labelPosition=right;points=[];portConstraint=eastwest;strokeColor=inherit;",
    "vertex": "1",
    "parent": "2"
})
ET.SubElement(doc_root[-1], "mxGeometry", {
    "y": str(y_offset),
    "width": "160",
    "height": "8"
}).set("as", "geometry")
y_offset += 8
id_counter += 1

# One method sample
method_cell = ET.SubElement(doc_root, "mxCell", {
    "id": str(id_counter),
    "value": "+ method(type): type",
    "style": "text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;",
    "vertex": "1",
    "parent": "2"
})
ET.SubElement(method_cell, "mxGeometry", {
    "y": str(y_offset),
    "width": "160",
    "height": "26"
}).set("as", "geometry")

xml_pretty = minidom.parseString(ET.tostring(root, encoding="utf-8")).toprettyxml(indent="  ")
print(xml_pretty)
