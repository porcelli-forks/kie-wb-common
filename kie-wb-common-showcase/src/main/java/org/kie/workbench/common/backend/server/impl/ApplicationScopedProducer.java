/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.backend.server.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.IOWatchServiceAllImpl;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

@Startup( StartupType.BOOTSTRAP )
@ApplicationScoped
public class ApplicationScopedProducer {

//    @Inject
//    private IOWatchServiceNonDotImpl watchService;

//    @Inject
//    @Named("clusterServiceFactory")
//    private ClusterServiceFactory clusterServiceFactory;

//    @Inject
//    @Named("luceneConfig")
//    private LuceneConfig config;

//    private IOService ioService;
//    private IOSearchService ioSearchService;

    @Inject
    private AuthenticationService authenticationService;

//    @PostConstruct
//    public void setup() {
//        final IOService service = new IOServiceIndexedImpl( watchService,
//                config.getIndexEngine(),
//                DublinCoreView.class,
//                VersionAttributeView.class,
//                OtherMetaView.class );
//
//        ioService = service;
//    }

//    @Produces
//    @Named("ioStrategy")
//    public IOService ioService() {
//        return ioService;
//    }

    //    @Produces
//    @Named("ioSearchStrategy")
//    public IOSearchService ioSearchService() {
//        return ioSearchService;
//    }
    @Inject
    private IOWatchServiceAllImpl watchService;

    private IOService ioService;

    @PostConstruct
    public void setup() {
        ioService = new IOServiceNio2WrapperImpl( "1", watchService );
    }

    @Produces
    @Named( "ioStrategy" )
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        return authenticationService.getUser();
    }

    @Produces
    public AuthorizationManager getAuthManager() {
        return new RuntimeAuthorizationManager();
    }
}
