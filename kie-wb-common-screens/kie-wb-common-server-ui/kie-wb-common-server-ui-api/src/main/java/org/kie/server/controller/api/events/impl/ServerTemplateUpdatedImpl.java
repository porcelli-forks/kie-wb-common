package org.kie.server.controller.api.events.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.spec.ServerTemplate;

@Portable
public class ServerTemplateUpdatedImpl implements ServerTemplateUpdated {

    private ServerTemplate serverTemplate;

    public ServerTemplateUpdatedImpl() {

    }

    public ServerTemplateUpdatedImpl( final ServerTemplate serverTemplate ) {
        this.serverTemplate = serverTemplate;
    }

    @Override
    public ServerTemplate getServerTemplate() {
        return serverTemplate;
    }
}
