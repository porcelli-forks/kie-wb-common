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

package org.kie.workbench.common.screens.server.management.client.remote;

import java.util.Collection;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.remote.empty.RemoteEmptyPresenter;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.uberfire.client.mvp.UberView;

import static org.kie.workbench.common.screens.server.management.client.util.Convert.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class RemotePresenter {

    public interface View extends UberView<RemotePresenter> {

        void clear();

        void setServerName( final String serverName );

        void setServerURL( String url );

        void setEmptyView( final RemoteEmptyPresenter.View view );

        void setStatusPresenter( final RemoteStatusPresenter.View view );
    }

    private final View view;
    private final RemoteStatusPresenter remoteStatusPresenter;
    private final RemoteEmptyPresenter remoteEmptyPresenter;
    private final Caller<RuntimeManagementService> runtimeManagementService;

    private ServerInstanceKey serverInstanceKey;

    @Inject
    public RemotePresenter( final View view,
                            final RemoteStatusPresenter remoteStatusPresenter,
                            final RemoteEmptyPresenter remoteEmptyPresenter,
                            final Caller<RuntimeManagementService> runtimeManagementService ) {
        this.view = view;
        this.remoteStatusPresenter = remoteStatusPresenter;
        this.remoteEmptyPresenter = remoteEmptyPresenter;
        this.runtimeManagementService = runtimeManagementService;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void onSelect( @Observes final ServerInstanceSelected serverInstanceSelected ) {
        checkNotNull( "serverInstanceSelected", serverInstanceSelected );
        this.serverInstanceKey = serverInstanceSelected.getServerInstanceKey();
        refresh();
    }

    public void onSelect( @Observes final ServerInstanceUpdated serverInstanceUpdated ) {
        checkNotNull( "serverInstanceUpdated", serverInstanceUpdated );
        final ServerInstanceKey updatedServerInstanceKey = toKey( serverInstanceUpdated.getServerInstance() );
//        if ( serverInstanceKey.equals( updatedServerInstanceKey ) ) {
            serverInstanceKey = updatedServerInstanceKey;
            loadContent( serverInstanceUpdated.getServerInstance().getContainers() );
//        }
    }

    public void refresh() {
        load( serverInstanceKey );
    }

    public void load( final ServerInstanceKey serverInstanceKey ) {
        runtimeManagementService.call( new RemoteCallback<Collection<Container>>() {
            @Override
            public void callback( final Collection<Container> containers ) {
                loadContent( containers );
            }
        } ).getContainersByServerInstance( serverInstanceKey.getServerTemplateId(),
                                           serverInstanceKey.getServerInstanceId() );
    }

    private void loadContent( final Collection<Container> containers ) {

        view.clear();
        view.setServerName( serverInstanceKey.getServerName() );
        view.setServerURL( serverInstanceKey.getUrl() );
        if ( containers.isEmpty() ) {
            view.setEmptyView( remoteEmptyPresenter.getView() );
        } else {
            remoteStatusPresenter.setup( containers );
            view.setStatusPresenter( remoteStatusPresenter.getView() );
        }

    }

}
