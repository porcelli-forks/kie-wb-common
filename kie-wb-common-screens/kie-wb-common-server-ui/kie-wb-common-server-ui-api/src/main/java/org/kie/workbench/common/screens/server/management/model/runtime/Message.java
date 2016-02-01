package org.kie.workbench.common.screens.server.management.model.runtime;

import java.util.Collection;

public interface Message {

    Severity getSeverity();

    Collection<String> getMessages();

}
