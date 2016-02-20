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

package org.kie.workbench.common.screens.server.management.client;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.container.ContainerPresenter;
import org.kie.workbench.common.screens.server.management.client.container.empty.ServerContainerEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.empty.ServerEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateListRefresh;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.navigation.ServerNavigationPresenter;
import org.kie.workbench.common.screens.server.management.client.navigation.template.ServerTemplatePresenter;
import org.kie.workbench.common.screens.server.management.client.remote.RemotePresenter;
import org.kie.workbench.common.screens.server.management.client.wizard.NewContainerWizard;
import org.kie.workbench.common.screens.server.management.client.wizard.NewServerTemplateWizard;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnOpen;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
@WorkbenchScreen(identifier = "ServerManagementBrowser")
public class ServerManagementBrowserPresenter {

    public interface View extends IsWidget {

        void setNavigation( ServerNavigationPresenter.View view );

        void setServerTemplate( ServerTemplatePresenter.View view );

        void setEmptyView( ServerEmptyPresenter.View view );

        void setContent( IsWidget view );
    }

    private final View view;

    private final ServerNavigationPresenter navigationPresenter;

    private final ServerTemplatePresenter serverTemplatePresenter;

    private final ServerEmptyPresenter serverEmptyPresenter;

    private final ServerContainerEmptyPresenter serverContainerEmptyPresenter;

    private final ContainerPresenter containerPresenter;

    private final RemotePresenter remotePresenter;

    private final Caller<SpecManagementService> specManagementService;

    private final Event<ServerTemplateSelected> serverTemplateSelectedEvent;

    @Inject
    public ServerManagementBrowserPresenter( final View view,
                                             final ServerNavigationPresenter navigationPresenter,
                                             final ServerTemplatePresenter serverTemplatePresenter,
                                             final ServerEmptyPresenter serverEmptyPresenter,
                                             final ServerContainerEmptyPresenter serverContainerEmptyPresenter,
                                             final ContainerPresenter containerPresenter,
                                             final RemotePresenter remotePresenter,
                                             final Caller<SpecManagementService> specManagementService,
                                             final Event<ServerTemplateSelected> serverTemplateSelectedEvent,
                                             final NewServerTemplateWizard newServerTemplateWizard,
                                             final NewContainerWizard newContainerWizard ) {
        this.view = view;
        this.navigationPresenter = navigationPresenter;
        this.serverTemplatePresenter = serverTemplatePresenter;
        this.serverEmptyPresenter = serverEmptyPresenter;
        this.serverContainerEmptyPresenter = serverContainerEmptyPresenter;
        this.containerPresenter = containerPresenter;
        this.remotePresenter = remotePresenter;
        this.specManagementService = specManagementService;
        this.serverTemplateSelectedEvent = serverTemplateSelectedEvent;
    }

    @PostConstruct
    public void init() {
        this.view.setNavigation( navigationPresenter.getView() );
    }

    @OnOpen
    public void onOpen() {
        refreshList( new ServerTemplateListRefresh() );
    }

    private void refreshList( @Observes final ServerTemplateListRefresh refresh ) {
        specManagementService.call( new RemoteCallback<Collection<ServerTemplateKey>>() {
            @Override
            public void callback( final Collection<ServerTemplateKey> serverTemplateKeys ) {
                setup( serverTemplateKeys, refresh.getSelectServerTemplateId() );
            }
        } ).listServerTemplateKeys();
    }

    public void onSelected( @Observes final ServerTemplateSelected serverTemplateSelected ) {
        checkNotNull( "serverTemplateSelected", serverTemplateSelected );
        selectServerTemplate( serverTemplateSelected.getServerTemplateKey().getId(), serverTemplateSelected.getContainerId() );
    }

    private void selectServerTemplate( final String serverTemplateId,
                                       final String containerId ) {
        specManagementService.call( new RemoteCallback<ServerTemplate>() {
            @Override
            public void callback( final ServerTemplate serverTemplate ) {
                setup( serverTemplate, containerId );
            }
        } ).getServerTemplate( serverTemplateId );
    }

    public void onSelected( @Observes final ContainerSpecSelected containerSpecSelected ) {
        checkNotNull( "containerSpecSelected", containerSpecSelected );
        this.view.setContent( containerPresenter.getView() );
    }

    public void onSelected( @Observes final ServerInstanceSelected serverInstanceSelected ) {
        checkNotNull( "serverInstanceSelected", serverInstanceSelected );

        this.view.setContent( remotePresenter.getView() );
    }

    public void setup( final Collection<ServerTemplateKey> serverTemplateKeys,
                       final String selectServerTemplateId ) {
        if ( serverTemplateKeys.isEmpty() ) {
            this.view.setEmptyView( serverEmptyPresenter.getView() );
            navigationPresenter.clear();
        } else {
            ServerTemplateKey serverTemplate2BeSelected = null;
            if ( selectServerTemplateId != null ) {
                for ( ServerTemplateKey serverTemplateKey : serverTemplateKeys ) {
                    if ( serverTemplateKey.getId().equals( selectServerTemplateId ) ) {
                        serverTemplate2BeSelected = serverTemplateKey;
                        break;
                    }
                }
            }
            if ( serverTemplate2BeSelected == null ) {
                serverTemplate2BeSelected = serverTemplateKeys.iterator().next();
            }
            navigationPresenter.setup( serverTemplate2BeSelected, serverTemplateKeys );
            serverTemplateSelectedEvent.fire( new ServerTemplateSelected( serverTemplate2BeSelected ) );
        }
    }

    private void setup( final ServerTemplate serverTemplate,
                        final String selectContainerId ) {
        this.view.setServerTemplate( serverTemplatePresenter.getView() );
        ContainerSpec firstContainerSpec = null;
        if ( serverTemplate.getContainersSpec().isEmpty() ) {
            serverContainerEmptyPresenter.setTemplate( serverTemplate );
            this.view.setContent( serverContainerEmptyPresenter.getView() );
            firstContainerSpec = null;
        } else {
            if ( selectContainerId != null ) {
                for ( final ContainerSpec containerSpec : serverTemplate.getContainersSpec() ) {
                    if ( containerSpec.getId().equals( selectContainerId ) ) {
                        firstContainerSpec = containerSpec;
                        break;
                    }
                }
            }
            if ( firstContainerSpec == null ) {
                firstContainerSpec = serverTemplate.getContainersSpec().iterator().next();
            }
        }
        serverTemplatePresenter.setup( serverTemplate, firstContainerSpec );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "OK";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
