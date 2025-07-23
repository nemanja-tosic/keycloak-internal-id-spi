# GitHub Actions CI/CD for Keycloak SPI

## Overview

This document describes the GitHub Actions workflow implemented for automatically building the Keycloak Internal ID SPI library.

## Workflow File

**Location**: `.github/workflows/build-keycloak-spi.yml`

## Triggers

The workflow is triggered by:

1. **Push Events**: When code is pushed to `main`, `master`, or `develop` branches and files in `packages/keycloak-spi/` are modified
2. **Pull Request Events**: When PRs target `main`, `master`, or `develop` branches and modify files in `packages/keycloak-spi/`
3. **Manual Dispatch**: Can be triggered manually from the GitHub Actions UI
4. **Release Events**: Automatically triggered when a new release is created

## Workflow Steps

### 1. Environment Setup
- **OS**: Ubuntu Latest
- **Java**: OpenJDK 11 (Temurin distribution)
- **Maven**: Included with the Java setup

### 2. Dependency Caching
- Caches Maven dependencies in `~/.m2` directory
- Uses `pom.xml` hash as cache key for efficient builds
- Significantly reduces build time on subsequent runs

### 3. Build Process
```bash
cd packages/keycloak-spi
mvn clean compile test package
```

### 4. Artifact Management
- **Build Artifacts**: Uploaded to GitHub Actions with 30-day retention
- **Release Assets**: Automatically attached to GitHub releases
- **Artifact Name**: `keycloak-internal-id-spi`
- **JAR File**: `keycloak-internal-id-spi-1.0.0.jar`

## Usage Instructions

### Accessing Build Artifacts

1. Navigate to the **Actions** tab in your GitHub repository
2. Select the **Build Keycloak SPI** workflow
3. Click on a successful workflow run
4. Download the `keycloak-internal-id-spi` artifact from the **Artifacts** section
5. Extract the ZIP file to get the JAR

### Creating Releases with Automatic Assets

1. Create a new release in GitHub:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
2. Go to GitHub → Releases → Create a new release
3. Select the tag and publish the release
4. The workflow will automatically build and attach the JAR file

### Manual Workflow Trigger

1. Go to **Actions** tab in GitHub
2. Select **Build Keycloak SPI** workflow
3. Click **Run workflow** button
4. Select branch and click **Run workflow**

## Workflow Features

✅ **Automated Testing**: Runs `mvn test` as part of the build process
✅ **Multi-Branch Support**: Works with main, master, and develop branches
✅ **Path-Based Triggering**: Only runs when Keycloak SPI files change
✅ **Dependency Caching**: Faster builds through Maven cache
✅ **Artifact Upload**: Build results available for download
✅ **Release Integration**: Automatic asset attachment to releases
✅ **Manual Triggering**: On-demand builds via GitHub UI

## Build Output

The workflow produces:
- **JAR File**: `keycloak-internal-id-spi-1.0.0.jar`
- **Build Logs**: Complete Maven build output
- **Test Results**: Unit test execution results
- **Artifact Listing**: Directory contents of `target/` folder

## Troubleshooting

### Build Failures
- Check the workflow logs in the Actions tab
- Verify Java 11 compatibility of dependencies
- Ensure `pom.xml` is valid and dependencies are available

### Artifact Issues
- Artifacts are retained for 30 days only
- Download artifacts before they expire
- Use releases for permanent storage of build outputs

### Cache Issues
- Cache is based on `pom.xml` hash
- Dependency changes will invalidate cache
- Manual cache clearing may be needed for persistent issues

## Security Considerations

- Uses `GITHUB_TOKEN` for release asset uploads
- No external secrets required
- All dependencies are from public Maven repositories
- Build runs in isolated GitHub-hosted runners

## Performance

- **Cold Build**: ~3-5 minutes (first run, no cache)
- **Warm Build**: ~1-2 minutes (with Maven cache)
- **Cache Size**: Typically 50-100MB for Maven dependencies
- **Artifact Size**: ~2-5MB for the shaded JAR

## Maintenance

- Workflow uses latest stable action versions
- Java 11 LTS ensures long-term compatibility
- Maven dependencies are managed via `pom.xml`
- No manual maintenance required for normal operation
