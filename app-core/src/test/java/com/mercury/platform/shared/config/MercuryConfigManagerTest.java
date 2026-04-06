package com.mercury.platform.shared.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MercuryConfigManagerTest {

    private static final byte[] LOCAL_UPDATER_BYTES = "test-local-updater".getBytes(StandardCharsets.UTF_8);

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateRequiredBootstrapArtifactsForEmptyMercuryChatConfig() throws Exception {
        Path configDirectory = this.tempDir.resolve("MercuryChat");

        MercuryConfigManager manager = new MercuryConfigManager(this.createSource(configDirectory));

        manager.bootstrapConfiguration(new TestUpdaterClassLoader());

        assertTrue(Files.isDirectory(configDirectory));
        assertTrue(Files.isRegularFile(configDirectory.resolve("configuration.json")));
        assertTrue(Files.isDirectory(configDirectory.resolve("temp")));
        assertTrue(Files.isDirectory(configDirectory.resolve("icons")));
        assertArrayEquals(LOCAL_UPDATER_BYTES, Files.readAllBytes(configDirectory.resolve("local-updater.jar")));
    }

    @Test
    void shouldMigrateLegacyMercuryTradeDataBeforeEnsuringBootstrapArtifacts() throws Exception {
        Path configDirectory = this.tempDir.resolve("MercuryChat");
        Path legacyDirectory = this.tempDir.resolve("MercuryTrade");
        Path legacyConfigFile = legacyDirectory.resolve("configuration.json");
        Path legacyMarkerFile = legacyDirectory.resolve("legacy-marker.txt");

        Files.createDirectories(legacyDirectory.resolve("icons"));
        Files.write(legacyConfigFile, "[{\"name\":\"LegacyProfile\",\"selected\":true}]".getBytes(StandardCharsets.UTF_8));
        Files.write(legacyMarkerFile, "legacy-data".getBytes(StandardCharsets.UTF_8));

        MercuryConfigManager manager = new MercuryConfigManager(this.createSource(configDirectory));

        manager.bootstrapConfiguration(new TestUpdaterClassLoader());

        assertTrue(Files.isRegularFile(configDirectory.resolve("configuration.json")));
        assertEquals(
                new String(Files.readAllBytes(legacyConfigFile), StandardCharsets.UTF_8),
                new String(Files.readAllBytes(configDirectory.resolve("configuration.json")), StandardCharsets.UTF_8)
        );
        assertEquals("legacy-data", new String(Files.readAllBytes(configDirectory.resolve("legacy-marker.txt")), StandardCharsets.UTF_8));
        assertTrue(Files.isDirectory(configDirectory.resolve("temp")));
        assertTrue(Files.isDirectory(configDirectory.resolve("icons")));
        assertArrayEquals(LOCAL_UPDATER_BYTES, Files.readAllBytes(configDirectory.resolve("local-updater.jar")));
    }

    @Test
    void shouldRepairMissingTempAndUpdaterArtifactsForExistingConfigDirectory() throws Exception {
        Path configDirectory = this.tempDir.resolve("MercuryChat");
        Path configurationFile = configDirectory.resolve("configuration.json");
        String configJson = "[{\"name\":\"ExistingProfile\",\"selected\":true}]";

        Files.createDirectories(configDirectory.resolve("icons"));
        Files.write(configurationFile, configJson.getBytes(StandardCharsets.UTF_8));

        MercuryConfigManager manager = new MercuryConfigManager(this.createSource(configDirectory));

        manager.bootstrapConfiguration(new TestUpdaterClassLoader());

        assertEquals(configJson, new String(Files.readAllBytes(configurationFile), StandardCharsets.UTF_8));
        assertTrue(Files.isDirectory(configDirectory.resolve("temp")));
        assertTrue(Files.isDirectory(configDirectory.resolve("icons")));
        assertArrayEquals(LOCAL_UPDATER_BYTES, Files.readAllBytes(configDirectory.resolve("local-updater.jar")));
    }

    private ConfigurationSource createSource(Path configDirectory) {
        return new TestConfigurationSource(
                configDirectory.toString(),
                configDirectory.resolve("configuration.json").toString()
        );
    }

    private static final class TestConfigurationSource extends ConfigurationSource {
        private TestConfigurationSource(String configurationPath, String configurationFilePath) {
            super(configurationPath, configurationFilePath);
        }
    }

    private static final class TestUpdaterClassLoader extends ClassLoader {
        @Override
        public InputStream getResourceAsStream(String name) {
            if ("app/local-updater.jar".equals(name)) {
                return new ByteArrayInputStream(LOCAL_UPDATER_BYTES);
            }
            return super.getResourceAsStream(name);
        }
    }
}
