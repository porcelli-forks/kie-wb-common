package org.kie.workbench.common.screens.server.management.client.events;

import org.kie.server.controller.api.model.spec.ServerTemplateKey;

/**
 * TODO: update me
 */
public class ServerTemplateSelected {
    private final ServerTemplateKey serverTemplateKey;

    public ServerTemplateSelected( final ServerTemplateKey serverTemplateKey ) {
        this.serverTemplateKey = serverTemplateKey;
    }

    public ServerTemplateKey getServerTemplateKey() {
        return serverTemplateKey;
    }
}
