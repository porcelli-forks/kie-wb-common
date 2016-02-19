package org.kie.server.controller.api.model.runtime.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.Message;
import org.kie.server.controller.api.model.runtime.ServerInstance;

@Portable
public class ServerInstanceImpl extends ServerInstanceKeyImpl implements ServerInstance {

    private String version;
    private final Collection<Message> status = new ArrayList<Message>();
    private final Collection<Container> containers = new ArrayList<Container>();

    public ServerInstanceImpl() {

    }

    public ServerInstanceImpl( final String serverTemplateId,
                               final String serverName,
                               final String serverInstanceId,
                               final String url,
                               final String version,
                               final Collection<Message> status,
                               final Collection<Container> containers ) {
        super( serverTemplateId, serverName, serverInstanceId, url );
        this.version = version;
        this.status.addAll( status );
        this.containers.addAll( containers );
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Collection<Message> getStatus() {
        return status;
    }

    @Override
    public Collection<Container> getContainers() {
        return containers;
    }
}
