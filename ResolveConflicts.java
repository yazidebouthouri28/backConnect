import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class ResolveConflicts {
    private static final String BASE_DIR = "c:\\Users\\nadine\\Desktop\\Projet PI\\NadineBack\\src\\main\\java\\tn\\esprit\\projetintegre";
    private static final String ENTITIES_DIR = BASE_DIR + "\\entities";
    private static final String NADINE_ENTITIES_DIR = BASE_DIR + "\\nadineentities";
    private static final String REPOSITORIES_DIR = BASE_DIR + "\\repositories";
    private static final String NADINE_REPOSITORIES_DIR = BASE_DIR + "\\repositorynadine";
    private static final String SERVICES_DIR = BASE_DIR + "\\services";
    private static final String NADINE_SERVICES_DIR = BASE_DIR + "\\servicenadine";

    public static void main(String[] args) {
        try {
            processNadineFolders();
            resolveGitConflicts();
            globalImportReplace();
            System.out.println("Migration and conflict resolution complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processNadineFolders() throws IOException {
        // 1. Nadine entities
        File nadineEntDir = new File(NADINE_ENTITIES_DIR);
        if (nadineEntDir.exists() && nadineEntDir.isDirectory()) {
            for (File f : nadineEntDir.listFiles()) {
                File dest = new File(ENTITIES_DIR, f.getName());
                if (dest.exists()) {
                    f.delete();
                } else {
                    Files.move(f.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    replaceImports(dest);
                }
            }
            if (nadineEntDir.listFiles().length == 0) nadineEntDir.delete();
        }

        // 2. Nadine repositories
        File nadineRepoDir = new File(NADINE_REPOSITORIES_DIR);
        if (nadineRepoDir.exists() && nadineRepoDir.isDirectory()) {
            for (File f : nadineRepoDir.listFiles()) {
                File dest = new File(REPOSITORIES_DIR, f.getName());
                if (dest.exists()) {
                    f.delete();
                } else {
                    Files.move(f.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    replaceImports(dest);
                }
            }
            if (nadineRepoDir.listFiles().length == 0) nadineRepoDir.delete();
        }

        // 3. Nadine services
        File nadineServDir = new File(NADINE_SERVICES_DIR);
        if (nadineServDir.exists() && nadineServDir.isDirectory()) {
            for (File f : nadineServDir.listFiles()) {
                renameAndMoveService(f, new File(SERVICES_DIR));
            }
            if (nadineServDir.listFiles().length == 0) nadineServDir.delete();
        }
    }

    private static void replaceImports(File file) throws IOException {
        if (!file.exists()) return;
        String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");

        content = content.replace("package tn.esprit.projetintegre.nadineentities;", "package tn.esprit.projetintegre.entities;");
        content = content.replace("package tn.esprit.projetintegre.repositorynadine;", "package tn.esprit.projetintegre.repositories;");
        content = content.replace("package tn.esprit.projetintegre.servicenadine;", "package tn.esprit.projetintegre.services;");

        content = content.replace("import tn.esprit.projetintegre.nadineentities", "import tn.esprit.projetintegre.entities");
        content = content.replace("import tn.esprit.projetintegre.repositorynadine", "import tn.esprit.projetintegre.repositories");
        content = content.replace("import tn.esprit.projetintegre.servicenadine", "import tn.esprit.projetintegre.services");

        Files.write(file.toPath(), content.getBytes("UTF-8"));
    }

    private static void renameAndMoveService(File srcFile, File destDir) throws IOException {
        String filename = srcFile.getName();
        String name = filename.replace(".java", "");
        File destFile = new File(destDir, filename);

        String content = new String(Files.readAllBytes(srcFile.toPath()), "UTF-8");
        String newName = name;

        if (destFile.exists()) {
            newName = name + "Nadine";
            destFile = new File(destDir, newName + ".java");
            content = content.replaceAll("\\bpublic class " + name + "\\b", "public class " + newName);
            content = content.replaceAll("\\bpublic interface " + name + "\\b", "public interface " + newName);
            content = content.replaceAll("\\bpublic " + name + "\\s*\\(", "public " + newName + "(");
        }

        content = content.replace("package tn.esprit.projetintegre.servicenadine;", "package tn.esprit.projetintegre.services;");
        Files.write(destFile.toPath(), content.getBytes("UTF-8"));
        srcFile.delete();
    }

    private static void resolveGitConflicts() throws IOException {
        File baseProj = new File("c:\\Users\\nadine\\Desktop\\Projet PI\\NadineBack");
        Files.walk(baseProj.toPath())
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".java") || p.toString().endsWith(".properties"))
            .forEach(p -> {
                try {
                    String content = new String(Files.readAllBytes(p), "UTF-8");
                    if (content.contains("<<<<<<<") && content.contains("=======")) {
                        System.out.println("Resolving conflicts in " + p);
                        List<String> lines = Arrays.asList(content.split("\\r?\\n"));
                        List<String> newLines = new ArrayList<>();
                        boolean inConflict = false;
                        List<String> headLines = new ArrayList<>();
                        List<String> nadineLines = new ArrayList<>();
                        String currentBlock = null;

                        Pattern methodPattern = Pattern.compile("(public|protected|private)\\s+([\\w<>,\\[\\]\\s]+)\\s+(\\w+)\\s*\\(");

                        for (String line : lines) {
                            if (line.startsWith("<<<<<<< HEAD")) {
                                inConflict = true;
                                currentBlock = "HEAD";
                                continue;
                            } else if (line.startsWith("=======")) {
                                currentBlock = "NADINE";
                                continue;
                            } else if (line.startsWith(">>>>>>>")) {
                                inConflict = false;
                                newLines.addAll(headLines);

                                if (p.toString().endsWith(".java")) {
                                    for (int i = 0; i < nadineLines.size(); i++) {
                                        String nLine = nadineLines.get(i);
                                        Matcher m = methodPattern.matcher(nLine);
                                        if (m.find() && !nLine.contains("class ") && !nLine.contains("interface ")) {
                                            String methodName = m.group(3);
                                            List<String> keywords = Arrays.asList("if", "for", "while", "switch", "catch", "return");
                                            if (!keywords.contains(methodName) && Character.isLowerCase(methodName.charAt(0))) {
                                                nadineLines.set(i, nLine.replace(" " + methodName + "(", " " + methodName + "Nadine("));
                                            }
                                        }
                                    }
                                }
                                newLines.addAll(nadineLines);

                                headLines.clear();
                                nadineLines.clear();
                                currentBlock = null;
                                continue;
                            }

                            if (inConflict) {
                                if ("HEAD".equals(currentBlock)) headLines.add(line);
                                else nadineLines.add(line);
                            } else {
                                newLines.add(line);
                            }
                        }

                        Files.write(p, String.join("\n", newLines).getBytes("UTF-8"));
                    }
                } catch (Exception e) {
                    // Ignore
                }
            });
    }

    private static void globalImportReplace() throws IOException {
        File baseProj = new File("c:\\Users\\nadine\\Desktop\\Projet PI\\NadineBack\\src\\main\\java");
        Files.walk(baseProj.toPath())
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".java"))
            .forEach(p -> {
                try {
                    replaceImports(p.toFile());
                } catch (Exception e) {}
            });
    }
}