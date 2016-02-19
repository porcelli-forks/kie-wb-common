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
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.remote.card.ContainerCardPresenter;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.uberfire.client.mvp.UberView;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class RemotePresenter {

    public interface View extends UberView<RemotePresenter> {

        void clear();

        void setServerName( final String serverName );

        void addCard( final IsWidget widget );

        void setServerURL( String url );
    }

    private final View view;
    private final Caller<RuntimeManagementService> runtimeManagementService;

    private ServerInstanceKey serverInstanceKey;

    @Inject
    public RemotePresenter( final View view,
                            final Caller<RuntimeManagementService> runtimeManagementService ) {
        this.view = view;
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

    public void refresh() {
        load( serverInstanceKey );
    }

    public void load( final ServerInstanceKey serverInstanceKey ) {
        runtimeManagementService.call( new RemoteCallback<Collection<Container>>() {
            @Override
            public void callback( final Collection<Container> containers ) {
                view.clear();
                view.setServerName( serverInstanceKey.getServerName() );
                view.setServerURL( serverInstanceKey.getUrl() );
                for ( final Container container : containers ) {
                    ContainerCardPresenter newCard = newCard();
                    newCard.setup( container );
                    view.addCard( newCard.getView().asWidget() );
                }
            }
        } ).getContainers( serverInstanceKey.getServerInstanceId() );
    }

    ContainerCardPresenter newCard() {
        return IOC.getBeanManager().lookupBean( ContainerCardPresenter.class ).getInstance();
    }

}
