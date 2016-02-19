package org.kie.server.controller.api.events.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.events.ServerTemplateDeleted;

@Portable
public class ServerTemplateDeletedImpl implements ServerTemplateDeleted {

    private String serverTemplateId;

    public ServerTemplateDeletedImpl() {

    }

    public ServerTemplateDeletedImpl( final String serverTemplateId ) {
        this.serverTemplateId = serverTemplateId;
    }

    @Override
    public String getServerTemplateId() {
        return serverTemplateId;
    }
}
