package org.kie.server.controller.api.model.runtime.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.ReleaseId;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.Message;

@Portable
public class ContainerImpl extends ContainerKeyImpl
        implements Container {

    private final Collection<Message> messages = new ArrayList<Message>();
    private ReleaseId resolvedReleasedId;
    private String url;

    public ContainerImpl() {

    }

    public ContainerImpl( final String containerSpecId,
                          final String containerName,
                          final ServerInstanceKeyImpl serverInstanceKey,
                          final Collection<Message> messages,
                          final ReleaseId resolvedReleasedId,
                          final String url ) {
        super( containerSpecId, containerName, serverInstanceKey );
        this.messages.addAll( messages );
        this.resolvedReleasedId = resolvedReleasedId;
        this.url = url;
    }

    @Override
    public Collection<Message> getMessages() {
        return messages;
    }

    @Override
    public ReleaseId getResolvedReleasedId() {
        return resolvedReleasedId;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
