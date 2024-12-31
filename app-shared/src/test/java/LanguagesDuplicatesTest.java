import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LanguagesDuplicatesTest {
    private final static Pattern pattern = Pattern.compile("\\S{2}\\.lang");

    @Test
    public void anyDuplicatesFoundTest() {
        URL path = this.getClass().getResource("lang");
        if (path == null) {
            Assert.fail("Path to lang folder was incorrect");
        }
        File langFolder = new File(path.getPath());
        if (langFolder.exists() && langFolder.isDirectory()) {
            File[] files = langFolder.listFiles();
            if (files == null) {
                Assert.fail("lang files list is null");
            }
            for (File file : files) {
                String name = file.getName();
                if (!pattern.matcher(name).matches()) {
                    Assert.fail("Wrong file inside lang folder");
                }
                containsDuplicates(file);

            }
        } else {
            Assert.fail("lang folder not found");
        }
    }

    private boolean containsDuplicates(File file) {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        if (file.exists() && file.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNumber = 0;
                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                        continue;
                    }
                    String[] split = line.split("=");
                    String key = split[0].trim();
                    String value = split[1].trim();
                    if (keys.contains(key)) {
                        if (values.get(keys.indexOf(key)).equals(value)) {
                            Assert.fail("Duplicate key and value found: " + key + "(" + file.getName() + ":" + lineNumber + ")");
                        }
                        Assert.fail("Duplicate key found: " + key + "(" + file.getName() + ":" + lineNumber + ")");
                    }
//                    if (values.contains(value)) {
//                        Assert.fail("Duplicate value found: " + value + "(" + file.getName() + ":" + lineNumber + ")");
//                    }
                    keys.add(key);
                    values.add(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        }
        return false;
    }

    @Test
    public void checkAllFilesComparedToEn() {
        Map<String, List<String>> filesAndTheirLines = new HashMap<>();

        URL path = this.getClass().getResource("lang");
        if (path == null) {
            Assert.fail("Path to lang folder was incorrect");
        }
        File langFolder = new File(path.getPath());
        if (langFolder.exists() && langFolder.isDirectory()) {
            File[] files = langFolder.listFiles();
            if (files == null) {
                Assert.fail("lang files list is null");
            }
            for (File file : files) {
                String name = file.getName();
                if (!pattern.matcher(name).matches()) {
                    Assert.fail("Wrong file inside lang folder");
                }

                List<String> lines = fetchAllLines(file);
                filesAndTheirLines.put(name, lines);

            }
        } else {
            Assert.fail("lang folder not found");
        }

        for (Map.Entry<String, List<String>> entry : filesAndTheirLines.entrySet()) {
            if (entry.getKey().equals("en.lang")) {
                continue;
            }

            List<String> enLines = filesAndTheirLines.get("en.lang");
            List<String> lines = entry.getValue();

            int i = 0;
            for (String enLine : enLines) {
                if (lines.size() < i +1) {
                    Assert.fail(entry.getKey() + " failed. Lines are missing stopped at line " + i);
                }
                if (!enLine.equals(lines.get(i))) {
                    Assert.fail(entry.getKey() + " failed. Different line was found that it should be. should be = " + enLine + " ; found = " + lines.get(i));
                }
                i++;
            }
        }

    }

    public List<String> fetchAllLines(File file) {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        if (file.exists() && file.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNumber = 0;
                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                        continue;
                    }
                    String[] split = line.split("=");
                    String key = split[0].trim();
                    String value = split[1].trim();
                    keys.add(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        }
        return keys;
    }
}
