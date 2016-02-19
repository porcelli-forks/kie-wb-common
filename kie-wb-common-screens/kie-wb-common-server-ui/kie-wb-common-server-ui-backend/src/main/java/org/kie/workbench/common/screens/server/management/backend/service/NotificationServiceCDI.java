/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.backend.service;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;


import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.service.NotificationService;

@ApplicationScoped
public class NotificationServiceCDI implements NotificationService {

    @Inject
    private Event<ServerTemplateUpdated> serverTemplateUpdatedEvent;

    @Inject
    private Event<ServerTemplateDeleted> serverTemplateDeletedEvent;

    @Override
    public void notify(ServerTemplate serverTemplate, ContainerSpec containerSpec, List<Container> containers) {

    }

    @Override
    public void notify(ServerTemplateUpdated serverTemplateUpdated) {

        serverTemplateUpdatedEvent.fire(serverTemplateUpdated);
    }

    @Override
    public void notify(ServerTemplateDeleted serverTemplateDeleted) {
        serverTemplateDeletedEvent.fire(serverTemplateDeleted);
    }
}
