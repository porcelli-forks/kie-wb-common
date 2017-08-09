/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.validation.asset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Charsets;
import org.eclipse.jgit.api.Git;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.workbench.common.services.backend.builder.af.AFBuilder;
import org.kie.workbench.common.services.backend.builder.af.nio.DefaultAFBuilder;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.JGitUtils;
import org.kie.workbench.common.services.backend.compiler.nio.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.JGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.OutputLogAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.impl.kie.KieDefaultMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.utils.MavenOutputConverter;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

@ApplicationScoped
public class ValidatorBuildService {

    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found. Consequentially validation cannot be performed.\nPlease check the necessary external dependencies for this project are configured correctly.";

    private IOService ioService;
    private KieProjectService projectService;
    private GuvnorM2Repository guvnorM2Repository;
    private CompilerMapsHolder compilerMapsHolder;
    private Logger logger = LoggerFactory.getLogger(ValidatorBuildService.class);
    //private AFCompiler afCompiler;

    public ValidatorBuildService() {
        //CDI proxies
    }

    @Inject
    public ValidatorBuildService(final @Named("ioStrategy") IOService ioService,
                                 final KieProjectService projectService,
                                 final GuvnorM2Repository guvnorM2Repository,
                                 final CompilerMapsHolder compilerMapsHolder) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.guvnorM2Repository = guvnorM2Repository;
        //this.afCompiler = getCompiler();
        this.compilerMapsHolder = compilerMapsHolder;
    }

    private AFCompiler getCompiler() {
        // we create the compiler in this weird mode to use the gitMap used internally
        AFCompiler innerDecorator = new KieAfterDecorator(new OutputLogAfterDecorator(new KieDefaultMavenCompiler()));
        AFCompiler outerDecorator = new JGITCompilerBeforeDecorator(innerDecorator,
                                                                    compilerMapsHolder);
        return outerDecorator;
    }

    private AFBuilder getBuilder(Project project) {
        AFBuilder builder = compilerMapsHolder.getBuilder(project.getProjectName());
        if (builder == null) {
            AFBuilder newBuilder = new DefaultAFBuilder(project.getRootPath().toURI().toString(),
                                                        guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.LOCAL_M2_REPO_NAME),
                                                        getCompiler());
            compilerMapsHolder.addBuilder(project.getProjectName(), newBuilder);
            builder = newBuilder;
        }
        return builder;
    }

    public List<ValidationMessage> validate(final Path resourcePath,
                                            final String content) {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(content.getBytes(Charsets.UTF_8));
            final List<ValidationMessage> results = doValidation(resourcePath,
                                                                 inputStream);
            return results;
        } catch (NoClassDefFoundError e) {
            return error(MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                              e.getLocalizedMessage()));
        } catch (Throwable e) {
            return error(e.getLocalizedMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public List<ValidationMessage> validate(final Path resourcePath) {
        InputStream inputStream = null;
        try {
            inputStream = ioService.newInputStream(Paths.convert(resourcePath));
            final List<ValidationMessage> results = doValidation(resourcePath,
                                                                 inputStream);
            return results;
        } catch (NoClassDefFoundError e) {
            return error(MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                              e.getLocalizedMessage()));
        } catch (Throwable e) {
            return error(e.getLocalizedMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    //@MAXWasHere*/
    private List<ValidationMessage> doValidation(final Path resourcePath,
                                                 final InputStream inputStream) throws NoProjectException {
        Optional<KieProject> project = project(resourcePath);
        if(!project.isPresent()) {
            throw new NoProjectException();
        }
        KieProject kieProject = project.get();
        org.uberfire.java.nio.file.Path path  = Paths.convert(project.get().getRootPath());
        JGitFileSystem fs = (JGitFileSystem) path.getFileSystem();
        Git git = compilerMapsHolder.getGit(fs);
        if(git == null){
            //one build discarded to create the git in compiler map
            AFBuilder builder = compilerMapsHolder.getBuilder(kieProject.getProjectName());
            builder.build();
            git = compilerMapsHolder.getGit(fs);
            if(git == null){
                logger.error("Git not constructed in the JGitDecorator");
                throw new RuntimeException("Git repo not found");
            }
        }

        try {
                return writeFileChangeAndBuild(resourcePath,
                                               inputStream,
                                               project.get(),
                                               path);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            throw new NoProjectException();
        } finally {
            if(git != null) {
                git.revert();
            }
        }
    }

    private List<ValidationMessage> writeFileChangeAndBuild(Path resourcePath,
                                                            InputStream inputStream,
                                                            KieProject project,
                                                            org.uberfire.java.nio.file.Path path) throws IOException {
        synchronized (path.getFileSystem()) {
            Files.copy(inputStream,
                       java.nio.file.Paths.get(resourcePath.toURI().toString()),
                       StandardCopyOption.REPLACE_EXISTING);
        }
        AFBuilder builder = getBuilder(project);
        CompilationResponse res = builder.build();
        List<ValidationMessage> validationMsgs = MavenOutputConverter.convertIntoValidationMessage(res.getMavenOutput().get());
        return validationMsgs;
    }

    private Optional<KieProject> project(final Path resourcePath) throws NoProjectException {
        final KieProject project = projectService.resolveProject(resourcePath);
        return Optional.ofNullable(project);
    }

    private ArrayList<ValidationMessage> error(final String errorMessage) {
        return new ArrayList<ValidationMessage>() {{
            add(new ValidationMessage(Level.ERROR,
                                      errorMessage));
        }};
    }
}
