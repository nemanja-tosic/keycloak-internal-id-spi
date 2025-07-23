package com.foremind.keycloak.spi;

import com.github.f4b6a3.uuid.UuidCreator;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * EventListener SPI that generates an internal_id attribute with UUIDv7
 * for every user registration in Keycloak.
 */
public class InternalIdEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = Logger.getLogger(InternalIdEventListenerProvider.class);
    private static final String INTERNAL_ID_ATTRIBUTE = "internal_id";

    private final KeycloakSession session;

    public InternalIdEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        // Handle user registration events
        if (EventType.REGISTER.equals(event.getType())) {
            logger.infof("User registration event detected for user: %s", event.getUserId());
            addInternalIdToUser(event.getRealmId(), event.getUserId());
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        // Handle admin events for user creation
        if (OperationType.CREATE.equals(adminEvent.getOperationType()) &&
            ResourceType.USER.equals(adminEvent.getResourceType())) {

            logger.infof("Admin user creation event detected: %s", adminEvent.getResourcePath());

            // Extract user ID from resource path (format: users/{userId})
            String resourcePath = adminEvent.getResourcePath();
            if (resourcePath != null && resourcePath.startsWith("users/")) {
                String userId = resourcePath.substring("users/".length());
                addInternalIdToUser(adminEvent.getRealmId(), userId);
            }
        }
    }

    /**
     * Adds internal_id attribute with UUIDv7 to the specified user
     */
    private void addInternalIdToUser(String realmId, String userId) {
        try {
            RealmModel realm = session.realms().getRealm(realmId);
            if (realm == null) {
                logger.errorf("Realm not found: %s", realmId);
                return;
            }

            UserModel user = session.users().getUserById(realm, userId);
            if (user == null) {
                logger.errorf("User not found: %s", userId);
                return;
            }

            // Check if internal_id already exists
            String existingInternalId = user.getFirstAttribute(INTERNAL_ID_ATTRIBUTE);
            if (existingInternalId != null && !existingInternalId.isEmpty()) {
                logger.infof("User %s already has internal_id: %s", userId, existingInternalId);
                return;
            }

            // Generate UUIDv7
            UUID internalId = UuidCreator.getTimeOrderedEpoch();
            String internalIdString = internalId.toString();

            // Set the internal_id attribute
            user.setSingleAttribute(INTERNAL_ID_ATTRIBUTE, internalIdString);

            logger.infof("Generated internal_id %s for user %s", internalIdString, userId);

        } catch (Exception e) {
            logger.errorf(e, "Error adding internal_id to user %s in realm %s", userId, realmId);
        }
    }

    @Override
    public void close() {
        // No resources to clean up
    }
}
