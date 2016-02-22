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

package org.kie.workbench.common.screens.server.management.backend.storage;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.storage.InMemoryKieServerTemplateStorage;

@ApplicationScoped
public class ServerTemplateVFSStorage implements KieServerTemplateStorage {    // TODO for now use the inmemory storage

    private final KieServerTemplateStorage storage;

    public ServerTemplateVFSStorage() {
        storage = InMemoryKieServerTemplateStorage.getInstance();
    }

    @Override
    public ServerTemplate store( final ServerTemplate serverTemplate ) {
        return storage.store( serverTemplate );
    }

    @Override
    public List<ServerTemplateKey> loadKeys() {
        return storage.loadKeys();
    }

    @Override
    public List<ServerTemplate> load() {
        return storage.load();
    }

    @Override
    public ServerTemplate load( final String identifier ) {
        return storage.load( identifier );
    }

    @Override
    public boolean exists( final String identifier ) {
        return storage.exists( identifier );
    }

    @Override
    public ServerTemplate update( final ServerTemplate serverTemplate ) {
        return storage.update( serverTemplate );
    }

    @Override
    public ServerTemplate delete( final String identifier ) {
        return storage.delete( identifier );
    }
}
