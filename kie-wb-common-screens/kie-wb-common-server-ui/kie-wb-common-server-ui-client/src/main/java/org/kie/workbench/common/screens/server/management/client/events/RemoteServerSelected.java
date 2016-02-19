package org.kie.workbench.common.screens.server.management.client.events;

import org.kie.server.controller.api.model.runtime.ServerInstanceKey;

/**
 * TODO: update me
 */
public class RemoteServerSelected {

    private final ServerInstanceKey serverInstanceKey;

    public RemoteServerSelected( final ServerInstanceKey serverInstanceKey ) {
        this.serverInstanceKey = serverInstanceKey;
    }
}
