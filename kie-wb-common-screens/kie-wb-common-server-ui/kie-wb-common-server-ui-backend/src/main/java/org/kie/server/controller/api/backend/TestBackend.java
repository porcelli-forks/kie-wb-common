package org.kie.server.controller.api.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.controller.api.events.ServerTemplateDeleted;
import org.kie.server.controller.api.events.ServerTemplateUpdated;
import org.kie.server.controller.api.events.impl.ServerTemplateDeletedImpl;
import org.kie.server.controller.api.events.impl.ServerTemplateUpdatedImpl;
import org.kie.server.controller.api.model.ContainerSpecData;
import org.kie.server.controller.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.MergeMode;
import org.kie.server.controller.api.model.ReleaseId;
import org.kie.server.controller.api.model.RuntimeStrategy;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.Message;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.runtime.Severity;
import org.kie.server.controller.api.model.runtime.impl.ContainerImpl;
import org.kie.server.controller.api.model.runtime.impl.MessageImpl;
import org.kie.server.controller.api.model.runtime.impl.ServerInstanceImpl;
import org.kie.server.controller.api.model.runtime.impl.ServerInstanceKeyImpl;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ScannerStatus;
import org.kie.server.controller.api.model.spec.ServerConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.model.spec.impl.ContainerSpecImpl;
import org.kie.server.controller.api.model.spec.impl.ProcessConfigImpl;
import org.kie.server.controller.api.model.spec.impl.RuleConfigImpl;
import org.kie.server.controller.api.model.spec.impl.ServerConfigImpl;
import org.kie.server.controller.api.model.spec.impl.ServerTemplateImpl;
import org.kie.server.controller.api.model.spec.impl.ServerTemplateKeyImpl;
import org.kie.server.controller.api.service.RuleCapabilitiesService;
import org.kie.server.controller.api.service.RuntimeManagementService;
import org.kie.server.controller.api.service.SpecManagementService;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class TestBackend implements SpecManagementService,
                                    RuntimeManagementService,
                                    RuleCapabilitiesService {

    @Inject
    private Event<ServerTemplateDeleted> serverTemplateDeletedEvent;

    @Inject
    private Event<ServerTemplateUpdated> serverTemplateUpdatedEvent;

    final Map<String, ServerTemplate> serverTemplates = new HashMap<String, ServerTemplate>();
    final Map<String, ServerInstance> serverInstanceMap = new HashMap<String, ServerInstance>();

    public TestBackend() {
        final ContainerSpec containerSpec1 = new ContainerSpecImpl( "My Container 1",
                                                                    "My Container 1",
                                                                    new ServerTemplateKeyImpl( "MyTemplate", "MyTemplate" ),
                                                                    new ReleaseId( "org.kie:test:LATEST" ),
                                                                    KieContainerStatus.STARTED,
                                                                    new HashMap<Capability, ContainerConfig>() {{
                                                                        put( Capability.RULE, new RuleConfigImpl( 1000L, ScannerStatus.DISPOSED ) );
                                                                        put( Capability.PROCESS, new ProcessConfigImpl( RuntimeStrategy.PER_REQUEST, "my kbase", "default", MergeMode.OVERRIDE_ALL ) );
                                                                    }} );

        final ContainerSpec containerSpec2 = new ContainerSpecImpl( "My Container 2",
                                                                    "My Container 2",
                                                                    new ServerTemplateKeyImpl( "MyTemplate", "MyTemplate" ),
                                                                    new ReleaseId( "org.kie:demo:LATEST" ),
                                                                    KieContainerStatus.STARTED,
                                                                    new HashMap<Capability, ContainerConfig>() {{
                                                                        put( Capability.RULE, new RuleConfigImpl( 1000L, ScannerStatus.DISPOSED ) );
                                                                        put( Capability.PROCESS, new ProcessConfigImpl( RuntimeStrategy.PER_REQUEST, "xxxx", "yyyyy", MergeMode.OVERRIDE_ALL ) );
                                                                    }} );

        final Collection<ServerInstanceKey> serverInstanceKeys = new ArrayList<ServerInstanceKey>();

        {
            final Collection<Container> containers = new ArrayList<Container>();

            containers.add( new ContainerImpl( containerSpec1.getId(),
                                               "container1",
                                               new ServerInstanceKeyImpl( "MyTemplate",
                                                                          "127.0.0.1",
                                                                          "127.0.0.1",
                                                                          "http://localhost:8080/" ),
                                               Collections.<Message>emptyList(),
                                               new ReleaseId( "org.kie:test:1.8.1" ),
                                               "http://localhost:8080/container1" ) );

            final ServerInstance serverInstance1 = new ServerInstanceImpl( "MyTemplate",
                                                                           "localhost",
                                                                           "127.0.0.1",
                                                                           "http://localhost:8080/",
                                                                           "6.4.x",
                                                                           Collections.<Message>emptyList(),
                                                                           containers );
            serverInstanceMap.put( serverInstance1.getServerInstanceId(), serverInstance1 );

            serverInstanceKeys.add( serverInstance1 );
        }

        {
            final Collection<Container> containers = new ArrayList<Container>();

            containers.add( new ContainerImpl( containerSpec1.getId(),
                                               "container2",
                                               new ServerInstanceKeyImpl( "MyTemplate",
                                                                          "10.37.119.252",
                                                                          "10.37.119.252",
                                                                          "http://10.37.119.252:8080/" ),
                                               Collections.<Message>emptyList(),
                                               new ReleaseId( "org.kie:test:1.8.1" ),
                                               "http://10.37.119.252:8080/container2" ) );

            final Collection<Message> messages = new ArrayList<Message>();
            messages.add( new MessageImpl( Severity.ERROR, Arrays.asList( "SUPER ERROR FAULT!", "SUPER ERROR FAULT2!" ) ) );
            messages.add( new MessageImpl( Severity.WARN, Arrays.asList( "not responding!" ) ) );

            containers.add( new ContainerImpl( containerSpec2.getId(),
                                               "container2",
                                               new ServerInstanceKeyImpl( "MyTemplate",
                                                                          "10.37.119.252",
                                                                          "10.37.119.252",
                                                                          "http://10.37.119.252:8080/" ),
                                               messages,
                                               new ReleaseId( "org.kie:demo:0.1.0" ),
                                               "http://10.37.119.252:8080/container2" ) );

            final Collection<Message> serverMessages = new ArrayList<Message>();
            messages.add( new MessageImpl( Severity.ERROR, Arrays.asList( "SUPER ERROR FAULT!", "SUPER ERROR FAULT2!" ) ) );
            serverMessages.add( new MessageImpl( Severity.WARN, Arrays.asList( "not responding!" ) ) );

            final ServerInstance serverInstance = new ServerInstanceImpl( "MyTemplate",
                                                                          "kiedev-01",
                                                                          "10.37.119.252",
                                                                          "http://10.37.119.252:8080/",
                                                                          "6.4.x",
                                                                          serverMessages,
                                                                          containers );
            serverInstanceMap.put( serverInstance.getServerInstanceId(), serverInstance );

            serverInstanceKeys.add( serverInstance );
        }

        final ServerTemplate serverTemplate1 = new ServerTemplateImpl( "MyTemplate",
                                                                       "MyTemplate",
                                                                       new ArrayList<Capability>() {{
                                                                           add( Capability.PROCESS );
                                                                           add( Capability.RULE );
                                                                       }},
                                                                       Collections.<Capability, ServerConfig>emptyMap(),
                                                                       new ArrayList<ContainerSpec>() {{
                                                                           add( containerSpec1 );
                                                                           add( containerSpec2 );
                                                                       }},
                                                                       serverInstanceKeys );

        serverTemplates.put( serverTemplate1.getId(), serverTemplate1 );
    }

    @Override
    public boolean isNewServerTemplateIdValid( final String serverTemplateId ) {
        return !serverTemplates.containsKey( serverTemplateId );
    }

    private int saveContainerSpec = 0;

    @Override
    public void saveContainerSpec( final String serverTemplateId,
                                   final ContainerSpec containerSpec ) {
        saveContainerSpec++;
        if ( saveContainerSpec % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
        final ServerTemplate template = serverTemplates.get( serverTemplateId );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        if ( template.getContainerSpec( containerSpec.getId() ) != null ) {
            template.deleteContainerSpec( containerSpec.getId() );
        }
        template.addContainerSpec( containerSpec );
    }

    private int saveServerTemplate = 0;

    @Override
    public void saveServerTemplate( final ServerTemplate serverTemplate ) {
        saveServerTemplate++;
        if ( saveServerTemplate % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
        final ServerTemplate template = serverTemplates.get( serverTemplate.getId() );
        if ( template != null ) {
            throw new RuntimeException( "Server template already exists" );
        }
        serverTemplates.put( serverTemplate.getId(), serverTemplate );
        serverTemplateUpdatedEvent.fire( new ServerTemplateUpdatedImpl( serverTemplate ) );
    }

    @Override
    public ServerTemplate getServerTemplate( final String serverTemplateId ) {
        return serverTemplates.get( serverTemplateId );
    }

    @Override
    public Collection<ServerTemplateKey> listServerTemplateKeys() {
        final Collection<ServerTemplateKey> result = new ArrayList<ServerTemplateKey>();
        for ( ServerTemplate serverTemplate : serverTemplates.values() ) {
            result.add( serverTemplate );
        }
        return result;
    }

    @Override
    public Collection<ServerTemplate> listServerTemplates() {
        return serverTemplates.values();
    }

    @Override
    public Collection<ContainerSpec> listContainerSpec( final String serverTemplateId ) {
        final ServerTemplate template = serverTemplates.get( serverTemplateId );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }
        return template.getContainersSpec();
    }

    @Override
    public void deleteContainerSpec( final ContainerSpecKey containerSpecKey ) {
        final ServerTemplate template = serverTemplates.get( containerSpecKey.getServerTemplateKey().getId() );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        template.deleteContainerSpec( containerSpecKey.getId() );
    }

    @Override
    public void deleteServerTemplate( final String serverTemplateId ) {
        serverTemplates.remove( serverTemplateId );
        serverTemplateDeletedEvent.fire( new ServerTemplateDeletedImpl( serverTemplateId ) );
    }

    @Override
    public void copyServerTemplate( final String serverTemplateId,
                                    final String newServerTemplateId,
                                    final String newServerTemplateName ) {
        if ( serverTemplates.containsKey( newServerTemplateId ) ) {
            throw new RuntimeException( "Server template already exists" );
        }
        final ServerTemplate serverTemplate = serverTemplates.get( serverTemplateId );

        final Map<Capability, ServerConfig> configMap = new HashMap<Capability, ServerConfig>();
        for ( final Map.Entry<Capability, ServerConfig> entry : serverTemplate.getConfigs().entrySet() ) {
            configMap.put( entry.getKey(), copy( entry.getValue() ) );
        }

        final Collection<ContainerSpec> containerSpecs = new ArrayList<ContainerSpec>( serverTemplate.getContainersSpec() );
        for ( final ContainerSpec entry : serverTemplate.getContainersSpec() ) {
            containerSpecs.add( copy( entry, newServerTemplateId, newServerTemplateName ) );
        }

        final ServerTemplate copy = new ServerTemplateImpl( newServerTemplateId,
                                                            newServerTemplateName,
                                                            new ArrayList<Capability>( serverTemplate.getCapabilities() ),
                                                            configMap,
                                                            containerSpecs );
        serverTemplates.put( copy.getId(), copy );
    }

    private ContainerSpec copy( final ContainerSpec origin,
                                final String newServerTemplateId,
                                final String newServerTemplateName ) {
        final Map<Capability, ContainerConfig> configMap = origin.getConfigs();
        for ( Map.Entry<Capability, ContainerConfig> entry : origin.getConfigs().entrySet() ) {
            configMap.put( entry.getKey(), copy( entry.getValue() ) );
        }
        return new ContainerSpecImpl( origin.getId(),
                                      origin.getContainerName(),
                                      new ServerTemplateKeyImpl( newServerTemplateId, newServerTemplateName ),
                                      new ReleaseId( origin.getReleasedId().toString() ),
                                      origin.getStatus(),
                                      configMap );
    }

    private ContainerConfig copy( final ContainerConfig _value ) {
        if ( _value instanceof RuleConfig ) {
            final RuleConfig value = (RuleConfig) _value;
            return new RuleConfigImpl( value.getPollInterval(), value.getScannerStatus() );
        } else if ( _value instanceof ProcessConfig ) {
            final ProcessConfig value = (ProcessConfig) _value;
            return new ProcessConfigImpl( value.getRuntimeStrategy(), value.getKBase(), value.getKSession(), value.getMergeMode() );
        }
        return null;
    }

    private ServerConfig copy( final ServerConfig value ) {
        return new ServerConfigImpl();
    }

    private int updateContainer = 0;

    @Override
    public ContainerConfig updateContainerConfig( final ContainerSpecKey containerSpecKey,
                                                  final ContainerConfig containerConfig ) {
        checkNotNull( "containerSpecKey", containerSpecKey );
        checkNotNull( "containerConfig", containerConfig );
        updateContainer++;
        if ( updateContainer % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }

        final ServerTemplate template = serverTemplates.get( containerSpecKey.getServerTemplateKey().getId() );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        final ContainerSpec containerSpec = template.getContainerSpec( containerSpecKey.getId() );
        if ( containerSpec == null ) {
            throw new RuntimeException( "Container spec doesn't exists" );
        }
        if ( containerConfig instanceof RuleConfig ) {
            containerSpec.getConfigs().put( Capability.RULE, containerConfig );
        } else if ( containerConfig instanceof ProcessConfig ) {
            containerSpec.getConfigs().put( Capability.PROCESS, containerConfig );
        }

        return containerConfig;
    }

    @Override
    public ServerConfig updateServerTemplateConfig( final ServerTemplateKey serverTemplateKey,
                                                    final ServerConfig serverTemplateConfig ) {
        final ServerTemplate template = serverTemplates.get( serverTemplateKey.getId() );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        return null;
    }

    private int startContainer = 0;

    @Override
    public void startContainer( final ContainerSpecKey containerSpecKey ) {
        startContainer++;
        if ( startContainer % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
        final ServerTemplate template = serverTemplates.get( containerSpecKey.getServerTemplateKey().getId() );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        final ContainerSpec containerSpec = template.getContainerSpec( containerSpecKey.getId() );
        if ( containerSpec == null ) {
            throw new RuntimeException( "Container spec doesn't exists" );
        }
        ( (ContainerSpecImpl) containerSpec ).setStatus( KieContainerStatus.STARTED );
    }

    private int stopContainer = 0;

    @Override
    public void stopContainer( final ContainerSpecKey containerSpecKey ) {
        stopContainer++;
        if ( stopContainer % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
        final ServerTemplate template = serverTemplates.get( containerSpecKey.getServerTemplateKey().getId() );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        final ContainerSpec containerSpec = template.getContainerSpec( containerSpecKey.getId() );
        if ( containerSpec == null ) {
            throw new RuntimeException( "Container spec doesn't exists" );
        }
        ( (ContainerSpecImpl) containerSpec ).setStatus( KieContainerStatus.STOPPED );
    }

    @Override
    public boolean isContainerIdValid( final String serverTemplateId,
                                       final String containerId ) {
        if ( !isValidJavaIdentifier( containerId ) ) {
            return false;
        }
        final ServerTemplate template = serverTemplates.get( serverTemplateId );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        return template.getContainerSpec( containerId ) == null;
    }

    private int scanNow = 0;

    @Override
    public void scanNow( final ContainerSpecKey containerSpecKey ) {
        scanNow++;
        if ( scanNow % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
    }

    private boolean isValidJavaIdentifier( String s ) {
        // an empty or null string cannot be a valid identifier
        if ( s == null || s.length() == 0 ) {
            return false;
        }

        char[] c = s.toCharArray();
        if ( !Character.isJavaIdentifierStart( c[ 0 ] ) ) {
            return false;
        }

        for ( int i = 1; i < c.length; i++ ) {
            if ( !Character.isJavaIdentifierPart( c[ i ] ) ) {
                return false;
            }
        }

        return true;
    }

    private int startScanner = 0;

    @Override
    public void startScanner( final ContainerSpecKey containerSpecKey,
                              final int interval ) {
        startScanner++;
        if ( startScanner % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
    }

    private int stopScanner = 0;

    @Override
    public void stopScanner( final ContainerSpecKey containerSpecKey ) {
        stopScanner++;
        if ( stopScanner % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
    }

    private int upgrade = 0;

    @Override
    public void versionUpgrade( final ContainerSpecKey containerSpecKey,
                                final String version ) {
        upgrade++;
        if ( upgrade % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
        final ServerTemplate template = serverTemplates.get( containerSpecKey.getServerTemplateKey().getId() );
        if ( template == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        final ContainerSpec containerSpec = template.getContainerSpec( containerSpecKey.getId() );
        if ( containerSpec == null ) {
            throw new RuntimeException( "Container spec doesn't exists" );
        }
        ( (ContainerSpecImpl) containerSpec ).setReleaseId( new ReleaseId( containerSpec.getReleasedId().getGroupId(), containerSpec.getReleasedId().getArtifactId(), version ) );
    }

    @Override
    public Collection<ServerTemplateKey> getServerInstanceKey( final String serverTemplateId ) {
        return null;
    }

    @Override
    public Collection<ServerInstance> getServerInstances( final String serverTemplateId ) {
        return serverInstanceMap.values();
    }

    @Override
    public Collection<Container> getContainers( final String serverInstanceId ) {
        final ServerInstance serverInstance = serverInstanceMap.get( serverInstanceId );
        if ( serverInstance == null ) {
            throw new RuntimeException( "Server template doesn't exists" );
        }

        return serverInstance.getContainers();
    }

    @Override
    public ContainerSpecData getContainers( final ContainerSpecKey containerSpecKey ) {
        final Collection<Container> containers = new ArrayList<Container>();

        for ( final ServerInstance serverInstance : serverInstanceMap.values() ) {
            for ( final Container container : serverInstance.getContainers() ) {
                if ( container.getContainerSpecId().equals( containerSpecKey.getId() ) &&
                        container.getServiceInstanceKey().getServerTemplateId().equals( containerSpecKey.getServerTemplateKey().getId() ) ) {
                    containers.add( container );
                }
            }
        }

        return new ContainerSpecData( serverTemplates.get( containerSpecKey.getServerTemplateKey().getId() ).getContainerSpec( containerSpecKey.getId() ),
                                      containers );

    }
}
