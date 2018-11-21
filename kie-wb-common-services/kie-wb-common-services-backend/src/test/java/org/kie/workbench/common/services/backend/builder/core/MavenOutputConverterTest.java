/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.builder.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.backend.builder.util.MavenOutputConverter;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.FileSystemTestingUtils;

/**
 * TODO: update me
 */
public class MavenOutputConverterTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    private Path path;

    @BeforeClass
    public static void init() throws IOException {
        fileSystemTestingUtils.setup();
    }

    @AfterClass
    public static void after() {
        fileSystemTestingUtils.cleanup();
    }

    @Before
    public void setup() throws IOException {
        final FileSystem fs = fileSystemTestingUtils.getFileSystem();

        this.path = fs.getRootDirectories().iterator().next();
    }

    @Test
    public void testErrors() throws IOException {
        final List<String> output = fromInputStream2String(getClass().getResourceAsStream("/output-test.log"));

        final BuildResults results = MavenOutputConverter.convertIntoBuildResults(output,
                                                                                  path,
                                                                                  "/some/path/target/maven/e6d197dd-1768-4e7b-9e1a-6938ff33dbb6_0/Afgsdgsdfg/");

        results.getMessages().forEach((msg) -> {
            System.out.println(msg.toString());
        });

//        assertThat(results.getErrorMessages()).hasSize(13);
//        assertThat(results.getErrorMessages().get(0).getText()).isEqualTo(" [ERROR] xxxxx cannot be resolved to a type");
//
//        final BuildMessage buildMessage = results.getErrorMessages().get(0);
//        assertThat(buildMessage.getPath()).isEqualTo(convert(path.resolve("src/main/java/mortgages/mortgages/Applicant.java")));
//        assertThat(buildMessage.getColumn()).isEqualTo(15);
//        assertThat(buildMessage.getLine()).isEqualTo(21);
//        assertThat(buildMessage.getLevel()).isEqualTo(Level.ERROR);
//
//        assertThat(results.getErrorMessages().get(1).getText()).isEqualTo("Unable to resolve ObjectType 'Applicant'");
//        assertThat(results.getErrorMessages().get(1).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/No bad credit checks.rdrl")));
//        assertThat(results.getErrorMessages().get(2).getText()).isEqualTo("Unable to resolve ObjectType 'Applicant'");
//        assertThat(results.getErrorMessages().get(2).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/No bad credit checks.rdrl")));
//
//        assertThat(results.getErrorMessages().get(3).getText()).isEqualTo("Unable to resolve ObjectType 'Applicant'");
//        assertThat(results.getErrorMessages().get(3).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/Underage.rdrl")));
//
//        assertThat(results.getErrorMessages().get(4).getText()).isEqualTo("Unable to resolve ObjectType 'Applicant'");
//        assertThat(results.getErrorMessages().get(4).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/CreditApproval.rdslr")));
//
//        assertThat(results.getErrorMessages().get(5).getText()).isEqualTo("Unable to Analyse Expression applicant.setApproved(true);:");
//        assertThat(results.getErrorMessages().get(6).getText()).isEqualTo("[Error: unable to resolve method using strict-mode: org.drools.core.spi.KnowledgeHelper.applicant()]");
//        assertThat(results.getErrorMessages().get(7).getText()).isEqualTo("[Near : {... applicant.setApproved(true); ....}]");
//        assertThat(results.getErrorMessages().get(8).getText()).isEqualTo("             ^");
//        assertThat(results.getErrorMessages().get(9).getText()).isEqualTo("[Line: 5, Column: 0]");
//        assertThat(results.getErrorMessages().get(5).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/CreditApproval.rdslr")));
//        assertThat(results.getErrorMessages().get(6).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/CreditApproval.rdslr")));
//        assertThat(results.getErrorMessages().get(7).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/CreditApproval.rdslr")));
//        assertThat(results.getErrorMessages().get(8).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/CreditApproval.rdslr")));
//        assertThat(results.getErrorMessages().get(9).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/CreditApproval.rdslr")));
//
//        assertThat(results.getErrorMessages().get(10).getText()).isEqualTo("Unable to resolve ObjectType 'Applicant'");
//        assertThat(results.getErrorMessages().get(10).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/RegexDslRule.rdslr")));
//
//        assertThat(results.getErrorMessages().get(11).getText()).isEqualTo("Rule Compilation error Only a type can be imported. mortgages.mortgages.Applicant resolves to a package");
//        assertThat(results.getErrorMessages().get(11).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/Dummy rule.drl")));
//
//        assertThat(results.getErrorMessages().get(12).getText()).isEqualTo("Error importing : 'mortgages.mortgages.Applicant'");
//        assertThat(results.getErrorMessages().get(12).getPath()).isEqualTo(convert(path.resolve("src/main/resources/mortgages/mortgages/No bad credit checks.rdrl")));
    }

    private List<String> fromInputStream2String(InputStream inputStream) throws IOException {
        final List<String> result = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        }
        return result;
    }
}