import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        final String RESOURCE_FILE_PATH = "C:/example/";
        Map<String, String> variables = new HashMap<>();

        search(RESOURCE_FILE_PATH, variables);
        replace(RESOURCE_FILE_PATH, variables);
    }

    private static void replace(String directoryPath, Map<String, String> resultMap) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && !file.getName().startsWith("R$") && !file.getName().startsWith("R$styleable")) {
                        replaceConstants(file, resultMap);
                    }
                }
            }
        } else {
            System.out.println("”казанной директории не существует.");
        }
    }

    private static void search(String directoryPath, Map<String, String> resultMap) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().startsWith("R$") && !file.getName().startsWith("R$styleable")) {
                        searchConstants(file, resultMap);
                    }
                }
            }
        } else {
            System.out.println("”казанной директории не существует.");
        }
    }


    private static void replaceConstants(File file, Map<String, String> resultMap) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                Pattern pattern = Pattern.compile("\\b(\\d{10})\\b");
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    String number = matcher.group(1);

                    if (resultMap.containsKey(number)) {
                        line = line.replace(number, resultMap.get(number));
                    }
                }

                builder.append(line).append("\n");
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void searchConstants(File file, Map<String, String> resultMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Pattern pattern = Pattern.compile("\\b(\\d{10})\\b");
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    String number = matcher.group(1);
                    String variableName = findVariable(file, line, number);
                    resultMap.put(number, variableName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String findVariable(File file, String line, String number) {
        int startIndex = line.indexOf("public static final int");

        if (startIndex != -1) {
            String nameLine = line.substring(startIndex + "public static final int".length());

            int index = nameLine.indexOf(" = ");

            if (index != -1) {
                String variableName = "R." + file.getName().substring(2, file.getName().indexOf(".java")) + "." + nameLine.substring(0, index).trim();

                return variableName;
            }
        }
        return "";
    }

}
