import os
import re
import shutil

BASE_DIR = r"c:\Users\nadine\Desktop\Projet PI\NadineBack\src\main\java\tn\esprit\projetintegre"
ENTITIES_DIR = os.path.join(BASE_DIR, "entities")
NADINE_ENTITIES_DIR = os.path.join(BASE_DIR, "nadineentities")
REPOSITORIES_DIR = os.path.join(BASE_DIR, "repositories")
NADINE_REPOSITORIES_DIR = os.path.join(BASE_DIR, "repositorynadine")
SERVICES_DIR = os.path.join(BASE_DIR, "services")
NADINE_SERVICES_DIR = os.path.join(BASE_DIR, "servicenadine")
CONTROLLERS_DIR = os.path.join(BASE_DIR, "controllers")
# We assume there might be controllernadine or similar, but the user didn't mention the exact folder name. We'll search for 'nadine' in controllers.

def replace_imports(filepath):
    """Replace all nadine imports with standard imports in a file."""
    if not os.path.exists(filepath):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Replace package declarations
    content = content.replace("package tn.esprit.projetintegre.nadineentities;", "package tn.esprit.projetintegre.entities;")
    content = content.replace("package tn.esprit.projetintegre.repositorynadine;", "package tn.esprit.projetintegre.repositories;")
    content = content.replace("package tn.esprit.projetintegre.servicenadine;", "package tn.esprit.projetintegre.services;")
    
    # Replace imports
    content = content.replace("import tn.esprit.projetintegre.nadineentities", "import tn.esprit.projetintegre.entities")
    content = content.replace("import tn.esprit.projetintegre.repositorynadine", "import tn.esprit.projetintegre.repositories")
    content = content.replace("import tn.esprit.projetintegre.servicenadine", "import tn.esprit.projetintegre.services")
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

def rename_and_move_service(src_path, dest_dir):
    """Move service and rename class to avoid collision."""
    filename = os.path.basename(src_path)
    name, ext = os.path.splitext(filename)
    dest_path = os.path.join(dest_dir, filename)
    
    with open(src_path, 'r', encoding='utf-8') as f:
        content = f.read()

    new_name = name
    if os.path.exists(dest_path):
        # Rename class and file
        new_name = name + "Nadine"
        dest_path = os.path.join(dest_dir, new_name + ext)
        content = re.sub(rf'\bpublic class {name}\b', f'public class {new_name}', content)
        content = re.sub(rf'\bpublic interface {name}\b', f'public interface {new_name}', content)
        # Also fix constructor if any
        content = re.sub(rf'\bpublic {name}\s*\(', f'public {new_name}(', content)

    # Change package to services
    content = content.replace("package tn.esprit.projetintegre.servicenadine;", "package tn.esprit.projetintegre.services;")
    
    with open(dest_path, 'w', encoding='utf-8') as f:
        f.write(content)
        
    print(f"Moved {filename} to {dest_path}")
    os.remove(src_path)

def process_nadine_folders():
    # 1. Nadine entities: If they exist in standard, delete Nadine's. (The user wants them "with" the others, using standard mapped tables is required)
    if os.path.exists(NADINE_ENTITIES_DIR):
        for f in os.listdir(NADINE_ENTITIES_DIR):
            src = os.path.join(NADINE_ENTITIES_DIR, f)
            dest = os.path.join(ENTITIES_DIR, f)
            if os.path.exists(dest):
                os.remove(src) # Delete duplicate
            else:
                # Move unique entity and fix package
                shutil.move(src, dest)
                replace_imports(dest)
        if not os.listdir(NADINE_ENTITIES_DIR):
            os.rmdir(NADINE_ENTITIES_DIR)

    # 2. Nadine repositories
    if os.path.exists(NADINE_REPOSITORIES_DIR):
        for f in os.listdir(NADINE_REPOSITORIES_DIR):
            src = os.path.join(NADINE_REPOSITORIES_DIR, f)
            dest = os.path.join(REPOSITORIES_DIR, f)
            if os.path.exists(dest):
                os.remove(src)
            else:
                shutil.move(src, dest)
                replace_imports(dest)
        if not os.listdir(NADINE_REPOSITORIES_DIR):
            os.rmdir(NADINE_REPOSITORIES_DIR)

    # 3. Nadine services
    if os.path.exists(NADINE_SERVICES_DIR):
        for f in os.listdir(NADINE_SERVICES_DIR):
            src = os.path.join(NADINE_SERVICES_DIR, f)
            rename_and_move_service(src, SERVICES_DIR)
        if not os.listdir(NADINE_SERVICES_DIR):
            os.rmdir(NADINE_SERVICES_DIR)

def resolve_git_conflicts():
    # Recursively find files with conflict markers
    base_proj = r"c:\Users\nadine\Desktop\Projet PI\NadineBack"
    for root, dirs, files in os.walk(base_proj):
        for f in files:
            if not f.endswith('.java') and not f.endswith('.properties'):
                continue
            filepath = os.path.join(root, f)
            try:
                with open(filepath, 'r', encoding='utf-8') as file:
                    content = file.read()
            except:
                continue
                
            if '<<<<<<<' in content and '=======' in content:
                print(f"Resolving conflicts in {filepath}")
                lines = content.split('\n')
                new_lines = []
                in_conflict = False
                head_lines = []
                nadine_lines = []
                current_block = None
                
                for line in lines:
                    if line.startswith('<<<<<<< HEAD'):
                        in_conflict = True
                        current_block = 'HEAD'
                        continue
                    elif line.startswith('======='):
                        current_block = 'NADINE'
                        continue
                    elif line.startswith('>>>>>>>'):
                        in_conflict = False
                        # We merge them by putting HEAD first, then Nadine.
                        # For java imports, we can deduplicate later or just leave them.
                        new_lines.extend(head_lines)
                        
                        # Process nadine lines: if it's a java file, alter method names slightly to avoid duplicate compilation errors
                        if f.endswith('.java'):
                            for i, n_line in enumerate(nadine_lines):
                                # Basic regex for method signature: public Type name(
                                m = re.search(r'(public|protected|private)\s+([\w<>,\s]+)\s+(\w+)\s*\(', n_line)
                                if m and 'class ' not in n_line and 'interface ' not in n_line:
                                    method_name = m.group(3)
                                    # Exclude constructors or keywords
                                    if method_name not in ['if', 'for', 'while', 'switch', 'catch'] and method_name[0].islower():
                                        # rename to methodNadine
                                        nadine_lines[i] = n_line.replace(f" {method_name}(", f" {method_name}Nadine(")
                        
                        new_lines.extend(nadine_lines)
                        
                        head_lines = []
                        nadine_lines = []
                        current_block = None
                        continue
                        
                    if in_conflict:
                        if current_block == 'HEAD':
                            head_lines.append(line)
                        else:
                            nadine_lines.append(line)
                    else:
                        new_lines.append(line)
                
                with open(filepath, 'w', encoding='utf-8') as file:
                    file.write('\n'.join(new_lines))

def global_import_replace():
    base_proj = r"c:\Users\nadine\Desktop\Projet PI\NadineBack\src\main\java"
    for root, dirs, files in os.walk(base_proj):
        for f in files:
            if f.endswith('.java'):
                replace_imports(os.path.join(root, f))

if __name__ == "__main__":
    process_nadine_folders()
    resolve_git_conflicts()
    global_import_replace()
    print("Migration and conflict resolution complete.")
