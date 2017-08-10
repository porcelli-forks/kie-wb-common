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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import com.google.common.io.Resources;
import org.eclipse.jgit.api.Git;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGenericKieValidatorTest {

    private TestFileSystem testFileSystem;
    private DefaultGenericKieValidator validator;
    private IOService ioService;
    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();
        validator = testFileSystem.getReference( DefaultGenericKieValidator.class );
        fileSystemTestingUtils.setup();
        ioService = fileSystemTestingUtils.getIoService();
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void testWorks() throws Exception {
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem fs = (JGitFileSystem) ioService.newFileSystem(originRepo,
                                                                               new HashMap<String, Object>() {{
                                                                                   put("init",
                                                                                       Boolean.TRUE);
                                                                                   put("internal",
                                                                                       Boolean.TRUE);
                                                                                   put("listMode",
                                                                                       "ALL");
                                                                               }});

        ioService.startBatch(fs);

        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources/GuvnorM2RepoDependencyExample1/pom.xml").toPath())));
        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/java/org/kie/workbench/common/services/builder/tests/test1/Bean.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources//GuvnorM2RepoDependencyExample1/src/main/java/org/kie/workbench/common/services/builder/tests/test1/Bean.java").toPath())));
        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/META-INF/kmodule.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources//GuvnorM2RepoDependencyExample1/src/main/resources/META-INF/kmodule.xml").toPath())));
        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/rule1.drl"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources//GuvnorM2RepoDependencyExample1/src/main/resources/rule1.drl").toPath())));
        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources//GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl").toPath())));
        ioService.endBatch();

        org.uberfire.java.nio.file.Path tmpRootCloned = Files.createTempDirectory("cloned");
        org.uberfire.java.nio.file.Path tmpCloned = Files.createDirectories(org.uberfire.java.nio.file.Paths.get(tmpRootCloned.toString(),
                                                                                                                 "GuvnorM2RepoDependencyExample1"));
        final File gitClonedFolder = new File(tmpCloned.toFile(), ".clone.git");

        final Git cloned = Git.cloneRepository().setURI(fs.getGit().getRepository().getDirectory().toURI().toString()).setBare(false).setDirectory(gitClonedFolder).call();


        assertNotNull(cloned);
        final Path path = resourcePath( cloned.getRepository().getDirectory().getAbsolutePath()+"/../GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        final URL urlToValidate = this.getClass().getResource( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );

        //Path path2 = Paths.convert(org.uberfire.java.nio.file.Paths.get(originRepo)); this is for test with git://repo
        final List<ValidationMessage> errors = validator.validate( path, Resources.toString( urlToValidate, Charset.forName( "UTF-8" ) ) );

        assertTrue( errors.isEmpty() );
        TestUtil.rm(tmpRootCloned.toFile());
    }

    @Test
    public void validatingAnAlreadyInvalidAssetShouldReportErrors() throws Exception {
        final Path path = resourcePath( "/BuilderExampleBrokenSyntax/src/main/resources/rule1.drl" );
        final URL urlToValidate = this.getClass().getResource( "/BuilderExampleBrokenSyntax/src/main/resources/rule1.drl" );

        final List<ValidationMessage> errors1 = validator.validate( path,
                                                                    Resources.toString( urlToValidate, Charset.forName( "UTF-8" ) ) );

        final List<ValidationMessage> errors2 = validator.validate( path,
                                                                    Resources.toString( urlToValidate, Charset.forName( "UTF-8" ) ) );

        assertFalse( errors1.isEmpty() );
        assertFalse( errors2.isEmpty() );
        assertEquals( errors1.size(),
                      errors2.size() );
    }

    private Path resourcePath( final String resourceName ) throws URISyntaxException, MalformedURLException {
        //final URL url = this.getClass().getResource( resourceName );
        final URL url = new URL("file://"+resourceName);
        return Paths.convert( testFileSystem.fileSystemProvider.getPath( url.toURI() ) );
    }
}
