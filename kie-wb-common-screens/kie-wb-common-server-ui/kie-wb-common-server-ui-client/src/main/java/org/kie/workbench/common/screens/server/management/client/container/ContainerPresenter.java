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

package org.kie.workbench.common.screens.server.management.client.container;

import java.util.Collection;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.workbench.common.screens.server.management.client.container.config.process.ContainerProcessConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.config.rules.ContainerRulesConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.ContainerRemoteStatusPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.empty.ContainerStatusEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.events.RefreshRemoteServers;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class ContainerPresenter {

    public interface View extends UberView<ContainerPresenter> {

        void clear();

        void setContainerName( final String containerName );

        void setGroupIp( final String groupIp );

        void setArtifactId( final String artifactId );

        void setStatus( final IsWidget view );

        void setProcessConfig( final ContainerProcessConfigPresenter.View view );

        void setRulesConfig( final ContainerRulesConfigPresenter.View view );

        void setContainerStopState( final State state );

        void setContainerStartState( final State state );

        void confirmRemove( final Command command );
    }

    private final View view;

    private final ContainerRemoteStatusPresenter containerRemoteStatusPresenter;

    private final ContainerStatusEmptyPresenter containerStatusEmptyPresenter;

    private final ContainerProcessConfigPresenter containerProcessConfigPresenter;

    private final ContainerRulesConfigPresenter containerRulesConfigPresenter;

    private final Caller<RuntimeManagementService> runtimeManagementService;

    private final Caller<SpecManagementService> specManagementService;

    private final Event<ServerTemplateSelected> serverTemplateSelectedEvent;

    private final Event<NotificationEvent> notification;

    private ContainerSpec containerSpec;

    @Inject
    public ContainerPresenter( final View view,
                               final ContainerRemoteStatusPresenter containerRemoteStatusPresenter,
                               final ContainerStatusEmptyPresenter containerStatusEmptyPresenter,
                               final ContainerProcessConfigPresenter containerProcessConfigPresenter,
                               final ContainerRulesConfigPresenter containerRulesConfigPresenter,
                               final Caller<RuntimeManagementService> runtimeManagementService,
                               final Caller<SpecManagementService> specManagementService,
                               final Event<ServerTemplateSelected> serverTemplateSelectedEvent,
                               final Event<NotificationEvent> notification ) {
        this.view = view;
        this.containerRemoteStatusPresenter = containerRemoteStatusPresenter;
        this.containerStatusEmptyPresenter = containerStatusEmptyPresenter;
        this.containerProcessConfigPresenter = containerProcessConfigPresenter;
        this.containerRulesConfigPresenter = containerRulesConfigPresenter;
        this.runtimeManagementService = runtimeManagementService;
        this.specManagementService = specManagementService;
        this.serverTemplateSelectedEvent = serverTemplateSelectedEvent;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        view.init( this );
        view.setStatus( containerRemoteStatusPresenter.getView() );
        view.setRulesConfig( containerRulesConfigPresenter.getView() );
        view.setProcessConfig( containerProcessConfigPresenter.getView() );
    }

    public View getView() {
        return view;
    }

    public void onRefresh( @Observes final RefreshRemoteServers refresh ) {
        load( checkNotNull( "refresh", refresh ).getContainerSpecKey() );
    }

    public void load( @Observes final ContainerSpecSelected containerSpecSelected ) {
        load( checkNotNull( "containerSpecSelected", containerSpecSelected ).getContainerSpecKey() );
    }

    public void refresh() {
        load( containerSpec );
    }

    public void load( final ContainerSpecKey containerSpecKey ) {
        checkNotNull( "containerSpecKey", containerSpecKey );
        runtimeManagementService.call( new RemoteCallback<ContainerSpecData>() {
            @Override
            public void callback( final ContainerSpecData content ) {
                checkNotNull( "content", content );
                setup( content.getContainerSpec(), content.getContainers() );
            }
        } ).getContainers( containerSpecKey.getServerTemplateKey().getId(),
                           containerSpecKey.getId() );
    }

    private void setup( final ContainerSpec containerSpec,
                        final Collection<Container> containers ) {
        this.containerSpec = checkNotNull( "containerSpec", containerSpec );
        view.clear();
        if ( containers.isEmpty() ) {
            containerStatusEmptyPresenter.setup( containerSpec );
            view.setStatus( containerStatusEmptyPresenter.getView() );
        } else {
            view.setStatus( containerRemoteStatusPresenter.getView() );
            containerRemoteStatusPresenter.setup( containers );
        }

        view.setContainerName( containerSpec.getContainerName() );
        view.setGroupIp( containerSpec.getReleasedId().getGroupId() );
        view.setArtifactId( containerSpec.getReleasedId().getArtifactId() );
        containerRulesConfigPresenter.setVersion( containerSpec.getReleasedId().getVersion() );
        containerProcessConfigPresenter.disable();

        updateStatus( containerSpec.getStatus() != null ? containerSpec.getStatus() : KieContainerStatus.STOPPED );

        for ( Map.Entry<Capability, ContainerConfig> entry : containerSpec.getConfigs().entrySet() ) {
            switch ( entry.getKey() ) {
                case RULE:
                    setupRuleConfig( (RuleConfig) entry.getValue() );
                    break;
                case PROCESS:
                    setupProcessConfig( (ProcessConfig) entry.getValue() );
                    break;
            }
        }
    }

    private void updateStatus( final KieContainerStatus status ) {
        switch ( status ) {
            case CREATING:
            case STARTED:
                view.setContainerStartState( State.ENABLED );
                view.setContainerStopState( State.DISABLED );
                break;
            case STOPPED:
            case DISPOSING:
            case FAILED:
                view.setContainerStartState( State.DISABLED );
                view.setContainerStopState( State.ENABLED );
                break;
        }
    }

    private void setupProcessConfig( final ProcessConfig value ) {
        containerProcessConfigPresenter.setup( containerSpec, value );
    }

    private void setupRuleConfig( final RuleConfig value ) {
        containerRulesConfigPresenter.setup( containerSpec, value );
    }

    public void removeContainer() {
        view.confirmRemove( new Command() {
            @Override
            public void execute() {
                specManagementService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( final Void response ) {
                        notification.fire( new NotificationEvent( "Container deleted.", NotificationEvent.NotificationType.SUCCESS ) );
                        serverTemplateSelectedEvent.fire( new ServerTemplateSelected( containerSpec.getServerTemplateKey() ) );
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( final Object o,
                                          final Throwable throwable ) {
                        notification.fire( new NotificationEvent( "Failed to delete container.", NotificationEvent.NotificationType.ERROR ) );
                        serverTemplateSelectedEvent.fire( new ServerTemplateSelected( containerSpec.getServerTemplateKey() ) );
                        return false;
                    }
                } ).deleteContainerSpec( containerSpec.getServerTemplateKey().getId(), containerSpec.getId() );
            }
        } );
    }

    public void stopContainer() {
        specManagementService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void response ) {
                updateStatus( KieContainerStatus.STOPPED );
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( "Stop container failed.", NotificationEvent.NotificationType.ERROR ) );
                updateStatus( KieContainerStatus.STARTED );
                return false;
            }
        } ).stopContainer( containerSpec );
    }

    public void startContainer() {
        specManagementService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void response ) {
                updateStatus( KieContainerStatus.STARTED );
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( "Start container failed.", NotificationEvent.NotificationType.ERROR ) );
                updateStatus( KieContainerStatus.STOPPED );
                return false;
            }
        } ).startContainer( containerSpec );
    }
}
