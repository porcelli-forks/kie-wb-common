package org.kie.workbench.common.screens.server.management.client.util;

import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;

public final class Convert {

    private Convert() {
    }

    public static ServerInstanceKey toKey( final ServerInstance serverInstance ) {
        return new ServerInstanceKey( serverInstance.getServerInstanceId(),
                                      serverInstance.getServerName(),
                                      serverInstance.getServerTemplateId(),
                                      serverInstance.getUrl() );
    }

}
