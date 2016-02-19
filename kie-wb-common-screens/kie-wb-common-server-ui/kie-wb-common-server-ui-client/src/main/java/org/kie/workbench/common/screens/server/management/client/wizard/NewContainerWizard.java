package org.kie.workbench.common.screens.server.management.client.wizard;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.service.SpecManagementService;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.wizard.config.process.ProcessConfigPagePresenter;
import org.kie.workbench.common.screens.server.management.client.wizard.container.NewContainerFormPresenter;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class NewContainerWizard extends AbstractMultiPageWizard {

    private final NewContainerFormPresenter newContainerFormPresenter;
    private final ProcessConfigPagePresenter processConfigPagePresenter;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<NotificationEvent> notification;
    private final Event<ServerTemplateSelected> serverTemplateSelectedEvent;
    private ServerTemplate serverTemplate;

    @Inject
    public NewContainerWizard( final NewContainerFormPresenter newContainerFormPresenter,
                               final ProcessConfigPagePresenter processConfigPagePresenter,
                               final Caller<SpecManagementService> specManagementService,
                               final Event<NotificationEvent> notification,
                               final Event<ServerTemplateSelected> serverTemplateSelectedEvent ) {
        this.newContainerFormPresenter = newContainerFormPresenter;
        this.processConfigPagePresenter = processConfigPagePresenter;
        this.specManagementService = specManagementService;
        this.notification = notification;
        this.serverTemplateSelectedEvent = serverTemplateSelectedEvent;
    }

    @Override
    public String getTitle() {
        return "New Container";
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    public void setServerTemplate( final ServerTemplate serverTemplate ) {
        this.serverTemplate = serverTemplate;
        newContainerFormPresenter.setServerTemplate( serverTemplate );
        pages.clear();
        pages.add( newContainerFormPresenter );
        if ( serverTemplate.getCapabilities().contains( Capability.PROCESS ) ) {
            pages.add( processConfigPagePresenter );
        }
    }

    public void clear() {
        newContainerFormPresenter.clear();
        processConfigPagePresenter.clear();
        pages.clear();
        pages.add( newContainerFormPresenter );
    }

    @Override
    public void close() {
        super.close();
        clear();
    }

    @Override
    public void complete() {
        final Map<Capability, ContainerConfig> mapConfig = new HashMap<Capability, ContainerConfig>();
        if ( getPages().size() == 2 ) {
            mapConfig.put( Capability.PROCESS, processConfigPagePresenter.buildProcessConfig() );
        }
        final ContainerSpec newContainer = newContainerFormPresenter.buildContainerSpec( newContainerFormPresenter.getServerTemplate().getId(),
                                                                                         mapConfig );
        specManagementService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void o ) {
                notification.fire( new NotificationEvent( "New Container created.", NotificationEvent.NotificationType.SUCCESS ) );
                clear();
                NewContainerWizard.super.complete();
                serverTemplateSelectedEvent.fire( new ServerTemplateSelected( serverTemplate, newContainer.getId() ) );
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( "New Container Error.", NotificationEvent.NotificationType.ERROR ) );
                NewContainerWizard.this.pageSelected( 0 );
                NewContainerWizard.this.start();
                return false;
            }
        } ).saveContainerSpec( newContainerFormPresenter.getServerTemplate().getId(), newContainer );
    }

}
