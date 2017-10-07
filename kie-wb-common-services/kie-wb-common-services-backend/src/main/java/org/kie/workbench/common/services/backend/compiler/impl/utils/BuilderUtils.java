package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class BuilderUtils {

    @Inject
    private CompilerMapsHolder compilerMapsHolder;
    @Inject
    private GuvnorM2Repository guvnorM2Repository;
    @Inject
    private Instance<User> identity;

    public Optional<KieAFBuilder> getBuilder(String uri, Path nioPath) {
        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(uri,nioPath,compilerMapsHolder,guvnorM2Repository, KieAFBuilderUtil.getIdentifier(identity));
        return Optional.ofNullable(builder);
    }

}
