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
import org.kie.workbench.common.screens.server.management.model.runtime.Container;
import org.kie.workbench.common.screens.server.management.model.runtime.ServerInstance;

@Remote
public interface RuntimeManagementService {

    Collection<String> getServerInstanceNames( final String serverTemplateId );

    Collection<ServerInstance> getServerInstances( final String serverTemplateId );

    Collection<Container> getContainers( final String serverInstanceId );

}
