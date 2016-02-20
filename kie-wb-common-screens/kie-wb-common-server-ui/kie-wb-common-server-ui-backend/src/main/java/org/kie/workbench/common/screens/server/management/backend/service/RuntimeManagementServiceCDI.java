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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.impl.service.RuntimeManagementServiceImpl;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;

@Service
@ApplicationScoped
public class RuntimeManagementServiceCDI extends RuntimeManagementServiceImpl
        implements RuntimeManagementService {

    @Inject
    private SpecManagementService specManagementService;

    @Inject
    @Override
    public void setKieServerInstanceManager( KieServerInstanceManager kieServerInstanceManager ) {
        super.setKieServerInstanceManager( kieServerInstanceManager );
    }

    @Inject
    @Override
    public void setTemplateStorage( KieServerTemplateStorage templateStorage ) {
        super.setTemplateStorage( templateStorage );
    }

    @Override
    public Collection<Container> getContainers( final String serverInstanceId ) {
        return Collections.emptyList();
    }

   /*
    * TODO revisit this as most likely it will not be efficient
    */
    @Override
    public ContainerSpecData getContainers( final String serverTemplateId,
                                            final String containerSpecId ) {
        final Collection<Container> containers = new ArrayList<Container>();

        ServerTemplate serverTemplate = getTemplateStorage().load( serverTemplateId );
        if ( serverTemplate == null ) {
            throw new RuntimeException( "No server template found for container spec " + containerSpecId );
        }

        for ( final ServerInstanceKey serverInstance : serverTemplate.getServerInstanceKeys() ) {
            for ( final Container container : getContainers( serverInstance ) ) {
                if ( container.getContainerSpecId().equals( containerSpecId ) &&
                        container.getServerTemplateId().equals( serverTemplateId ) ) {
                    containers.add( container );
                }
            }
        }

        return new ContainerSpecData( specManagementService.getServerTemplate( serverTemplateId ).getContainerSpec( containerSpecId ),
                                      containers );

    }

}
