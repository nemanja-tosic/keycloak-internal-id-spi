# Keycloak Internal ID Event Listener SPI

This Keycloak EventListener SPI automatically generates an `internal_id` attribute with a UUIDv7 value for every user registration in Keycloak.

## Features

- Automatically generates UUIDv7-based `internal_id` for new user registrations
- Handles both self-registration and admin-created users
- Prevents duplicate `internal_id` generation for existing users
- Uses time-ordered UUIDv7 for better database performance and sorting
- Comprehensive logging for debugging and monitoring

## Architecture

The SPI consists of three main components:

1. **InternalIdEventListenerProvider** - The main event listener that handles user registration events
2. **InternalIdEventListenerProviderFactory** - Factory class for creating provider instances
3. **META-INF/services configuration** - Service registration for Keycloak SPI discovery

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Keycloak 22.0.5 or compatible version

## Building the SPI

### Manual Build

```bash
cd keycloak-spi
mvn clean package
```

This will create a JAR file in the `target/` directory: `keycloak-internal-id-spi-1.0.0.jar`

### Automated Build with GitHub Actions

The project includes a GitHub Actions workflow that automatically builds the Keycloak SPI library. The workflow is triggered:

- **On push/PR** to main branches when files in `keycloak-spi/` are modified
- **Manual dispatch** via GitHub Actions UI
- **On release creation** for automatic release asset upload

#### Workflow Features:
- ✅ Java 11 environment setup
- ✅ Maven dependency caching for faster builds
- ✅ Automated testing and packaging
- ✅ JAR artifact upload (30-day retention)
- ✅ Automatic release asset attachment

#### Accessing Build Artifacts:
1. Go to the **Actions** tab in your GitHub repository
2. Select the **Build Keycloak SPI** workflow
3. Click on a successful workflow run
4. Download the `keycloak-internal-id-spi` artifact from the **Artifacts** section

#### Creating a Release:
1. Create a new release in GitHub
2. The workflow will automatically build and attach the JAR file to the release
3. The JAR will be available as `keycloak-internal-id-spi-1.0.0.jar` in release assets

## Deployment

### 1. Deploy the JAR to Keycloak

Copy the built JAR file to your Keycloak deployment:

```bash
# For standalone Keycloak
cp target/keycloak-internal-id-spi-1.0.0.jar $KEYCLOAK_HOME/providers/

# For containerized Keycloak
# Add to your Dockerfile or mount as volume
COPY keycloak-internal-id-spi-1.0.0.jar /opt/keycloak/providers/
```

### 2. Restart Keycloak

Restart your Keycloak instance to load the new SPI.

### 3. Configure the Event Listener

1. Log in to the Keycloak Admin Console
2. Navigate to your realm settings
3. Go to **Events** → **Config**
4. In the **Event Listeners** section, add `internal-id-event-listener` to the list
5. Save the configuration

## Configuration

The SPI works out of the box with no additional configuration required. It will:

- Listen for `REGISTER` events (user self-registration)
- Listen for admin `CREATE USER` events
- Generate UUIDv7 values using the `com.github.f4b6a3.uuid-creator` library
- Store the generated ID in the `internal_id` user attribute

## Database Schema Changes

The corresponding database changes are handled by the application migrations:

```typescript
// Migration: 2025.07.23T16.07.48.add-internal-id-to-users.ts
// Adds internalId column to users table
```

## User Model Changes

The User domain model has been updated to include the `internalId` field:

```typescript
@Attribute({ type: DataTypes.STRING, allowNull: true })
declare internalId: string | null;
```

## Usage

Once deployed and configured, the SPI will automatically:

1. Detect new user registrations
2. Generate a UUIDv7 for the `internal_id` attribute
3. Store it in the user's attributes
4. Log the operation for monitoring

The `internal_id` can then be accessed:

- Via Keycloak Admin API: `GET /admin/realms/{realm}/users/{user-id}`
- In JWT tokens (if configured in token mappers)
- Through the application's user import process

## Monitoring and Logging

The SPI provides comprehensive logging at INFO level:

- User registration event detection
- Internal ID generation
- Error handling for edge cases

Logs will appear in your Keycloak server logs with the prefix:
```
INFO [com.foremind.keycloak.spi.InternalIdEventListenerProvider]
```

## Troubleshooting

### SPI Not Loading
- Verify the JAR is in the correct `providers/` directory
- Check Keycloak startup logs for SPI loading messages
- Ensure Keycloak has been restarted after deployment

### Events Not Being Processed
- Verify the event listener is configured in the realm settings
- Check that user registration events are being generated
- Review Keycloak server logs for error messages

### Duplicate Internal IDs
The SPI includes protection against duplicate generation:
- Checks if `internal_id` already exists before generating
- Logs when skipping generation for existing users

## Dependencies

- **Keycloak SPI**: 22.0.5 (provided scope)
- **UUID Creator**: 5.3.2 (for UUIDv7 generation)
- **JBoss Logging**: Included with Keycloak

## Development

To modify or extend the SPI:

1. Update the Java source files in `src/main/java/com/foremind/keycloak/spi/`
2. Rebuild with `mvn clean package`
3. Redeploy to Keycloak
4. Restart Keycloak to load changes

## License

This SPI is part of the Foremind project and follows the same licensing terms.
