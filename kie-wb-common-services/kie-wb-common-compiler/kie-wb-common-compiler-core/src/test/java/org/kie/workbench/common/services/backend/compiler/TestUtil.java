/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class TestUtil {

    private static Logger logger = LoggerFactory.getLogger(TestUtil.class);

    public static void copyTree(Path source,
                                Path target) throws IOException {
        FileUtils.copyDirectory(source.toFile(),
                                target.toFile());
    }

    public static void rm(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                rm(c);
            }
        }
        if (!f.delete()) {
            logger.error("Couldn't delete file {}", f);
        }
    }

    public static void writeMavenOutputIntoTargetFolder(final Path tmp,
                                                        final List<String> mavenOutput,
                                                        final String testName) throws Exception {
        //File dir = tmp.toAbsolutePath().toFile();
        File dir = new File(tmp.toAbsolutePath().toString());
        File target = new File(dir.toString() + "/target/");
        if (!target.exists()) {
            logger.info("Creating target folder");
            target.mkdir();
        }
        if (mavenOutput.size() > 0) {
            StringBuilder sb = new StringBuilder(target.toString()).append(testName).append(".test.log");
            File fileOut = new File(sb.toString());
            logger.info("Writing error output on {}", fileOut.toString());
            FileOutputStream fos = new FileOutputStream(fileOut);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            for (String item : mavenOutput) {
                bw.write(item);
                bw.newLine();
            }
            bw.close();
        }
    }

    public static Path createMavenRepo() throws Exception {
        Path mavenRepository = Paths.get(System.getProperty("user.home"),
                "/.m2/repository");
        if (!Files.exists(mavenRepository)) {
            logger.info("Creating a m2_repo into " + mavenRepository);
            if (!Files.exists(Files.createDirectories(mavenRepository))) {
                throw new Exception("Folder not writable in the project");
            }
        }
        return mavenRepository;
    }

    public static Path createAndCopyToDircetory(Path root, String dirName, String copyTree) throws IOException {
        //NIO creation and copy content
        Path dir = Files.createDirectories(Paths.get(root.toString(), dirName));
        TestUtil.copyTree(Paths.get(copyTree), dir);
        return dir;
        //end NIO
    }
}
