package com.mercury.platform.shared.config;

import com.google.gson.reflect.TypeToken;
import com.mercury.platform.shared.AsSubscriber;
import com.mercury.platform.shared.config.configration.*;
import com.mercury.platform.shared.config.configration.impl.*;
import com.mercury.platform.shared.config.configration.impl.adr.AdrConfigurationServiceMock;
import com.mercury.platform.shared.config.descriptor.*;
import com.mercury.platform.shared.config.json.JSONHelper;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MercuryConfigManager implements ConfigManager, AsSubscriber {
    private static final String CURRENT_CONFIGURATION_FOLDER_NAME = "MercuryChat";
    private static final String LEGACY_CONFIGURATION_FOLDER_NAME = "MercuryTrade";
    private static final String CONFIGURATION_FILE_NAME = "configuration.json";
    private static final String TEMP_DIRECTORY_NAME = "temp";
    private static final String ICONS_DIRECTORY_NAME = "icons";
    private static final String LOCAL_UPDATER_FILE_NAME = "local-updater.jar";
    private static final String LOCAL_UPDATER_RESOURCE_PATH = "app/local-updater.jar";

    private Logger logger = LogManager.getLogger(MercuryConfigManager.class.getSimpleName());

    private ConfigurationSource dataSource;
    private JSONHelper jsonHelper;
    private List<ProfileDescriptor> profileDescriptors;
    private ProfileDescriptor selectedProfile;
    private FramesConfigurationService framesConfigurationService;
    private PlainConfigurationService<ApplicationDescriptor> applicationConfigurationService;
    private PlainConfigurationService<VulkanDescriptor> vulkanConfigurationService;
    private PlainConfigurationService<NotificationSettingsDescriptor> notificationConfigurationService;
    private PlainConfigurationService<TaskBarDescriptor> taskBarConfigurationService;
    private PlainConfigurationService<ScannerDescriptor> scannerConfigurationService;
    private KeyValueConfigurationService<String, SoundDescriptor> soundConfigurationService;
    private KeyValueConfigurationService<String, Float> scaleConfigurationService;
    private PlainConfigurationService<HotKeysSettingsDescriptor> hotKeyConfigurationService;
    private ListConfigurationService<StashTabDescriptor> stashTabConfigurationService;
    private IconBundleConfigurationService iconBundleConfigurationService;
    private AdrConfigurationService adrConfigurationService;

    private List<BaseConfigurationService> services = new ArrayList<>();

    public MercuryConfigManager(ConfigurationSource dataSource) {
        this.dataSource = dataSource;
        this.jsonHelper = new JSONHelper(dataSource.getConfigurationFilePath());
        this.subscribe();
    }

    @Override
    public FramesConfigurationService framesConfiguration() {
        return this.framesConfigurationService;
    }

    @Override
    public PlainConfigurationService<ApplicationDescriptor> applicationConfiguration() {
        return this.applicationConfigurationService;
    }

    @Override
    public PlainConfigurationService<VulkanDescriptor> vulkanConfiguration() {
        return this.vulkanConfigurationService;
    }

    @Override
    public PlainConfigurationService<NotificationSettingsDescriptor> notificationConfiguration() {
        return this.notificationConfigurationService;
    }

    @Override
    public PlainConfigurationService<TaskBarDescriptor> taskBarConfiguration() {
        return this.taskBarConfigurationService;
    }

    @Override
    public PlainConfigurationService<ScannerDescriptor> scannerConfiguration() {
        return this.scannerConfigurationService;
    }

    @Override
    public KeyValueConfigurationService<String, SoundDescriptor> soundConfiguration() {
        return this.soundConfigurationService;
    }

    @Override
    public KeyValueConfigurationService<String, Float> scaleConfiguration() {
        return this.scaleConfigurationService;
    }

    @Override
    public AdrConfigurationService adrConfiguration() {
        return this.adrConfigurationService;
    }

    @Override
    public ListConfigurationService<StashTabDescriptor> stashTabConfiguration() {
        return this.stashTabConfigurationService;
    }

    @Override
    public IconBundleConfigurationService iconBundleConfiguration() {
        return this.iconBundleConfigurationService;
    }

    @Override
    public PlainConfigurationService<HotKeysSettingsDescriptor> hotKeysConfiguration() {
        return this.hotKeyConfigurationService;
    }

    @Override
    public List<ProfileDescriptor> profiles() {
        return this.profileDescriptors;
    }


    public void load() {
        try {
            this.bootstrapConfiguration();
            this.services.clear();
            this.profileDescriptors = this.jsonHelper.readArrayData(new TypeToken<List<ProfileDescriptor>>() {
            });
            if (this.profileDescriptors == null) {
                this.profileDescriptors = new ArrayList<>();
                ProfileDescriptor defaultProfile = this.getDefaultProfile();
                this.selectedProfile = defaultProfile;
                this.profileDescriptors.add(defaultProfile);
                this.jsonHelper.writeListObject(this.profileDescriptors, new TypeToken<List<ProfileDescriptor>>() {
                });
            } else {
                this.selectedProfile = this.profileDescriptors.stream()
                        .filter(ProfileDescriptor::isSelected)
                        .findAny().orElse(null);
                if (this.selectedProfile == null) {
                    ProfileDescriptor defaultProfile = this.getDefaultProfile();
                    this.selectedProfile = defaultProfile;
                    this.profileDescriptors.add(defaultProfile);
                    this.jsonHelper.writeListObject(this.profileDescriptors, new TypeToken<List<ProfileDescriptor>>() {
                    });
                }
            }

            this.framesConfigurationService = new FramesConfigurationServiceImpl(selectedProfile);
            this.soundConfigurationService = new SoundConfigurationService(selectedProfile);
            this.applicationConfigurationService = new ApplicationConfigurationService(selectedProfile);
            this.vulkanConfigurationService = new VulkanConfigurationService(selectedProfile);
            this.taskBarConfigurationService = new TaskBarConfigurationService(selectedProfile);
            this.scannerConfigurationService = new ScannerConfigurationService(selectedProfile);
            this.notificationConfigurationService = new NotificationConfigurationService(selectedProfile);
            this.scaleConfigurationService = new ScaleConfigurationService(selectedProfile);
            this.stashTabConfigurationService = new StashTabConfigurationService(selectedProfile);
            this.hotKeyConfigurationService = new HotKeyConfigurationService(selectedProfile);
            this.adrConfigurationService = new AdrConfigurationServiceMock(selectedProfile);
            this.iconBundleConfigurationService = new IconBundleConfigurationServiceImpl(selectedProfile);

            this.services.add((BaseConfigurationService) this.framesConfigurationService);
            this.services.add((BaseConfigurationService) this.soundConfigurationService);
            this.services.add((BaseConfigurationService) this.applicationConfigurationService);
            this.services.add((BaseConfigurationService) this.vulkanConfigurationService);
            this.services.add((BaseConfigurationService) this.scannerConfigurationService);
            this.services.add((BaseConfigurationService) this.taskBarConfigurationService);
            this.services.add((BaseConfigurationService) this.notificationConfigurationService);
            this.services.add((BaseConfigurationService) this.scaleConfigurationService);
            this.services.add((BaseConfigurationService) this.stashTabConfigurationService);
            this.services.add((BaseConfigurationService) this.hotKeyConfigurationService);
            this.services.add((BaseConfigurationService) this.adrConfigurationService);
            this.services.add((BaseConfigurationService) this.iconBundleConfigurationService);

            for (BaseConfigurationService item : this.services) {
                item.validate();
            }


            this.jsonHelper.writeListObject(this.profileDescriptors, new TypeToken<List<ProfileDescriptor>>() {
            });
        } catch (IOException e) {
            logger.error("Error while processing file:{}", dataSource.getConfigurationPath(), e);
        }
    }

    void bootstrapConfiguration() throws IOException {
        this.bootstrapConfiguration(Thread.currentThread().getContextClassLoader());
    }

    void bootstrapConfiguration(ClassLoader classLoader) throws IOException {
        File configFolder = new File(this.dataSource.getConfigurationPath());
        File configFile = new File(this.dataSource.getConfigurationFilePath());
        File tempFolder = new File(configFolder, TEMP_DIRECTORY_NAME);
        File iconFolder = new File(configFolder, ICONS_DIRECTORY_NAME);
        File updaterFile = new File(configFolder, LOCAL_UPDATER_FILE_NAME);

        this.migrateLegacyConfigurationIfNeeded(configFolder, configFile, iconFolder);
        this.ensureDirectoryExists(configFolder, "configuration");
        this.ensureDirectoryExists(tempFolder, TEMP_DIRECTORY_NAME);
        this.ensureDirectoryExists(iconFolder, ICONS_DIRECTORY_NAME);
        this.ensureFileExists(configFile, CONFIGURATION_FILE_NAME);
        this.ensureUpdaterExists(classLoader, updaterFile);
    }

    private void migrateLegacyConfigurationIfNeeded(File configFolder, File configFile, File iconFolder) throws IOException {
        if (configFolder.exists() && configFile.exists() && iconFolder.exists()) {
            return;
        }

        File legacyFolder = this.getLegacyConfigurationFolder(configFolder);
        if (legacyFolder == null || !legacyFolder.exists()) {
            return;
        }

        logger.info("Legacy configuration found at {}. Migrating...", legacyFolder.getPath());
        FileUtils.copyDirectory(legacyFolder, configFolder);
        logger.info("Migration from MercuryTrade to MercuryChat completed successfully.");
    }

    private File getLegacyConfigurationFolder(File configFolder) {
        String configurationPath = this.dataSource.getConfigurationPath();
        if (!configurationPath.contains(CURRENT_CONFIGURATION_FOLDER_NAME)) {
            return null;
        }

        String legacyPath = configurationPath.replace(CURRENT_CONFIGURATION_FOLDER_NAME, LEGACY_CONFIGURATION_FOLDER_NAME);
        if (legacyPath.equals(configurationPath)) {
            return null;
        }

        File legacyFolder = new File(legacyPath);
        if (legacyFolder.equals(configFolder)) {
            return null;
        }
        return legacyFolder;
    }

    private void ensureDirectoryExists(File directory, String description) throws IOException {
        if (directory.exists()) {
            return;
        }

        if (!directory.mkdirs() && !directory.exists()) {
            throw new IOException("Unable to create " + description + " directory at " + directory.getPath());
        }
    }

    private void ensureFileExists(File file, String description) throws IOException {
        if (file.exists()) {
            return;
        }

        File parent = file.getParentFile();
        if (parent != null) {
            this.ensureDirectoryExists(parent, description + " parent");
        }

        if (!file.createNewFile() && !file.exists()) {
            throw new IOException("Unable to create " + description + " at " + file.getPath());
        }
    }

    private void ensureUpdaterExists(ClassLoader classLoader, File updaterFile) throws IOException {
        if (updaterFile.exists()) {
            return;
        }

        ClassLoader resolvedClassLoader = classLoader != null ? classLoader : MercuryConfigManager.class.getClassLoader();
        try (InputStream resourceAsStream = resolvedClassLoader.getResourceAsStream(LOCAL_UPDATER_RESOURCE_PATH)) {
            if (resourceAsStream != null) {
                FileUtils.copyInputStreamToFile(resourceAsStream, updaterFile);
            }
        }
    }


    @Override
    public void subscribe() {
        MercuryStoreCore.saveConfigSubject.subscribe(state -> {
            this.jsonHelper.writeListObject(this.profileDescriptors, new TypeToken<List<ProfileDescriptor>>() {
            });
        });
        MercuryStoreCore.toDefaultSubject.subscribe(state -> {
            this.services.forEach(BaseConfigurationService::toDefault);
        });
        MercuryStoreCore.changeProfileSubject.subscribe(profile -> {
            this.selectedProfile.setSelected(false);
            this.selectedProfile = profile;
            profile.setSelected(true);
            this.services.forEach(service -> {
                service.setSelectedProfile(profile);
                service.validate();
            });
            this.jsonHelper.writeListObject(this.profileDescriptors, new TypeToken<List<ProfileDescriptor>>() {
            });
        });
    }

    private ProfileDescriptor getDefaultProfile() {
        ProfileDescriptor defaultProfile = new ProfileDescriptor();
        defaultProfile.setSelected(true);
        defaultProfile.setName("Profile1");
        return defaultProfile;
    }
}
