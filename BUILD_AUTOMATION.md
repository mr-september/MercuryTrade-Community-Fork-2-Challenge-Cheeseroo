# MercuryTrade Build & Release Automation

This document describes the automated build and release process for MercuryTrade.

## 🚀 Automated Release Process

The project now supports automated builds and releases through GitHub Actions, replacing the manual Windows batch script process.

### Release Workflow

1. **Trigger**: Push a version tag (e.g., `v1.4.3`) or manually run the workflow
2. **Build**: Automatically compiles the Java application using Maven
3. **Package**: Creates both JAR and Windows EXE versions using Launch4j
4. **Release**: Automatically creates a GitHub release with all artifacts

### Creating a New Release

#### Method 1: Git Tag (Recommended)
```bash
# Create and push a new version tag
git tag v1.4.3
git push origin v1.4.3
```

#### Method 2: Manual Workflow Dispatch
1. Go to the Actions tab in the GitHub repository
2. Select "Build and Release" workflow
3. Click "Run workflow"
4. Enter the version tag (e.g., `v1.4.3`)

### Build Artifacts

Each release automatically creates:
- `MercuryTrade-{version}-jar.zip` - JAR file with instructions
- `MercuryTrade-{version}-exe.zip` - Windows executable
- `MercuryTrade-{version}-lang.zip` - Language files
- `MercuryTrade.jar` - Direct JAR download
- `MercuryTrade.exe` - Direct EXE download

## 🔧 Local Development

### Prerequisites
- Java 8 or higher
- Maven 3.6+
- Launch4j (optional, for Windows EXE creation)

### Building Locally

#### Using the Shell Script (Linux/macOS)
```bash
# Full build with packaging (creates zip files)
./build_mercury.sh

# Simple build without packaging
./build_mercury_simple.sh
```

#### Using the Batch Scripts (Windows)
```batch
REM Full build with packaging (creates zip files)
build_mercury.bat

REM Simple build without packaging
build_mercury_simple.bat
```

#### Using Maven Directly
```bash
# Build JAR only
mvn clean package

# Copy to release directory
cp app/target/MercuryTrade.jar release_files/
```

### Installing Launch4j (for EXE creation)

#### Linux
```bash
wget -O launch4j.tgz "https://sourceforge.net/projects/launch4j/files/launch4j-3/3.50/launch4j-3.50-linux-x64.tgz/download"
tar -xzf launch4j.tgz
sudo ln -sf $(pwd)/launch4j/launch4j /usr/local/bin/launch4j
```

#### macOS
```bash
brew install launch4j
```

#### Windows
Download from [Launch4j website](http://launch4j.sourceforge.net/)

## 📋 Continuous Integration

### Build Workflow
- **Triggers**: Push to master/develop, Pull Requests
- **Tests**: Multi-platform builds (Ubuntu, Windows, macOS)
- **Java versions**: 8, 11, 17
- **Artifacts**: Temporary build artifacts for validation

### Code Quality
- **CodeQL**: Automated security scanning
- **Dependabot**: Automated dependency updates
- **Cross-platform**: Ensures builds work on all platforms

## 🔄 Migration from Manual Process

### What Changed
- ✅ **Automated**: No more manual batch script execution
- ✅ **Cross-platform**: Works on Linux, macOS, and Windows
- ✅ **Versioning**: Automatic version management from Git tags
- ✅ **Consistent**: Same build process every time
- ✅ **Fast**: Parallel builds and dependency caching

### What Stays the Same
- Maven build process
- Launch4j for Windows EXE creation
- Same output artifacts
- Version numbering scheme

### Legacy Support
The build scripts have been consolidated for clarity:
- `build_mercury.sh` / `build_mercury.bat` - Full build with packaging
- `build_mercury_simple.sh` / `build_mercury_simple.bat` - Simple build without packaging
- `scripts/install_maven3.ps1` - Maven installation helper (moved to scripts directory)

### What Changed
- ✅ **Consolidated**: Multiple build scripts merged into two clear options
- ✅ **Simplified**: Removed redundant packaging-only scripts
- ✅ **Organized**: Utility scripts moved to scripts/ directory
- ✅ **Preserved**: All functionality maintained, just better organized

## 🐛 Troubleshooting

### Common Issues

#### Build Fails on Tag Push
- Ensure the tag follows semantic versioning (e.g., `v1.4.3`)
- Check that `pom.xml` is valid
- Verify GitHub Actions has necessary permissions

#### Launch4j EXE Creation Fails
- This is non-critical; JAR version will still be created
- Check Launch4j configuration in `release_files/release_config.xml`

#### Missing Dependencies
- Clear Maven cache: `mvn dependency:purge-local-repository`
- Check Java version: `java -version`

### Manual Debugging
```bash
# Test the build process locally
./build_mercury.sh

# Check Maven build only
mvn clean package -DskipTests

# Test Launch4j separately
cd release_files
launch4j release_config.xml
```

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Launch4j Documentation](http://launch4j.sourceforge.net/docs.html)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Java 8 Documentation](https://docs.oracle.com/javase/8/docs/)
