cat << 'EOF' > extraer_uml.py
import os, re

puml = ["@startuml Gatinder_Auto", "skinparam classAttributeIconSize 0", "skinparam packageStyle rectangle", "left to right direction\n"]
packages = {}

for root, _, files in os.walk("src/main/java"):
    for file in files:
        if file.endswith(".java"):
            path = os.path.join(root, file)
            with open(path, "r", encoding="utf-8", errors="ignore") as f:
                content = f.read()
            
            pkg_m = re.search(r"package\s+([\w\.]+);", content)
            pkg = pkg_m.group(1).split(".")[-1] if pkg_m else "default"
            
            cls_m = re.search(r"(public\s+)?(class|interface|enum)\s+(\w+)", content)
            if not cls_m: continue
            cls_type, cls_name = cls_m.group(2), cls_m.group(3)
            
            body = []
            if cls_type == "enum":
                enums = re.findall(r"([A-Z0-9_]+)", content.split("{")[1].split("}")[0])
                body = [e for e in enums if len(e) > 1]
            else:
                for line in content.splitlines():
                    line = line.strip()
                    if line.startswith("//") or line.startswith("*") or line.startswith("@"): continue
                    
                    # Atributos
                    attr_m = re.search(r"^(private|protected|public)\s+([\w<>\[\],\s]+)\s+(\w+)\s*(?:=.*)?;$", line)
                    if attr_m and not "(" in line:
                        vis = "-" if attr_m.group(1)=="private" else ("#" if attr_m.group(1)=="protected" else "+")
                        body.append(f"    {vis} {attr_m.group(3)}: {attr_m.group(2)}")
                    
                    # Metodos
                    meth_m = re.search(r"^(public|protected|private)\s+([\w<>\[\],\s]+)\s+(\w+)\s*\((.*?)\)", line)
                    if meth_m and cls_name not in line and not line.startswith("class"):
                        vis = "+" if meth_m.group(1)=="public" else ("#" if meth_m.group(1)=="protected" else "-")
                        args = ", ".join([a.strip().split()[-1] for a in meth_m.group(4).split(",") if a.strip()])
                        body.append(f"    {vis} {meth_m.group(3)}({args}): {meth_m.group(2)}")

            if pkg not in packages: packages[pkg] = []
            packages[pkg].append(f"{cls_type} {cls_name} {{\n" + "\n".join(body) + "\n}")

for pkg, classes in packages.items():
    puml.append(f'package "{pkg}" {{')
    for c in classes:
        puml.append(f"  {c}")
    puml.append("}\n")

puml.append("@enduml")

with open("diagrama_gatinder.puml", "w", encoding="utf-8") as f:
    f.write("\n".join(puml))

print(" Diagrama generado con éxito en: diagrama_gatinder.puml")
EOF