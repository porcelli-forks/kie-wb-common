package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class BuilderUtils {

    @Inject
    private CompilerMapsHolder compilerMapsHolder;
    @Inject
    private GuvnorM2Repository guvnorM2Repository;

    public Optional<KieAFBuilder> getBuilder(Path nioPath, Boolean indexing) {
        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(nioPath,compilerMapsHolder,guvnorM2Repository,indexing);
        return Optional.ofNullable(builder);
    }

}
