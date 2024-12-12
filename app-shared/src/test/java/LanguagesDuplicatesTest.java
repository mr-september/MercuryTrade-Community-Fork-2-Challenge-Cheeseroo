import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
                    if (values.contains(value)) {
                        Assert.fail("Duplicate value found: " + value + "(" + file.getName() + ":" + lineNumber + ")");
                    }
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
}
