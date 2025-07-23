package com.foremind.keycloak.spi;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * Factory for creating InternalIdEventListenerProvider instances.
 * This factory is responsible for creating and configuring the event listener provider.
 */
public class InternalIdEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final String PROVIDER_ID = "internal-id-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new InternalIdEventListenerProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // No initialization required
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post-initialization required
    }

    @Override
    public void close() {
        // No resources to clean up
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
