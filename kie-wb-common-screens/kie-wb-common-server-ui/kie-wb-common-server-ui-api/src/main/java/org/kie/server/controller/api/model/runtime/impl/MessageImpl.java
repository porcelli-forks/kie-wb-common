package org.kie.server.controller.api.model.runtime.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.runtime.Message;
import org.kie.server.controller.api.model.runtime.Severity;

@Portable
public class MessageImpl implements Message {

    private Severity severity = Severity.WARN;
    private Collection<String> messages = new ArrayList<String>();

    public MessageImpl() {

    }

    public MessageImpl( final Severity severity,
                        final Collection<String> messages ) {
        this.severity = severity;
        this.messages.addAll( messages );
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity( final Severity severity ) {
        this.severity = severity;
    }

    @Override
    public Collection<String> getMessages() {
        return messages;
    }

    public void addMessage( final String message ) {
        this.messages.add( message );
    }
}
