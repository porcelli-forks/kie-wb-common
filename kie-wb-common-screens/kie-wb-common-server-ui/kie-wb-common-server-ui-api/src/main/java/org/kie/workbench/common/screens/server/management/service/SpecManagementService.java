/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.service;

import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.server.management.model.spec.ContainerConfig;
import org.kie.workbench.common.screens.server.management.model.spec.ContainerSpec;
import org.kie.workbench.common.screens.server.management.model.spec.ServerConfig;
import org.kie.workbench.common.screens.server.management.model.spec.ServerTemplate;

@Remote
public interface SpecManagementService extends RuleCapabilitiesService {

    void saveContainerSpec( final String serverTemplateId,
                            final ContainerSpec containerSpec );

    void saveServerTemplate( final ServerTemplate serverTemplate );

    ServerTemplate getServerTemplate( final String serverTemplateId );

    Collection<String> listServerTemplateNames();

    Collection<ServerTemplate> listServerTemplates();

    void deleteContainerSpec( final String serverTemplateId,
                              final String containerSpecId );

    void deleteServerTemplate( final String serverTemplateId );

    void copyServerTemplate( final String serverTemplateId,
                             final String newServerTemplateId,
                             final String newServerTemplateName );

    void updateContainerConfig( final String serverTemplateId,
                                final String containerSpecId,
                                final ContainerConfig containerConfig );

    void updateServerTemplateConfig( final String serverTemplateId,
                                     final String containerSpecId,
                                     final ServerConfig serverTemplateConfig );

}
