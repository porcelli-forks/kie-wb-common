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
package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;

/**
 * Maven output converter
 */
public class MavenOutputConverter {

    public static List<ValidationMessage> convertIntoValidationMessage(List<String> mavenOutput,
                                                                       String filter) {
        if (mavenOutput.size() > 0) {
            List<ValidationMessage> validationMsgs = new ArrayList<>(mavenOutput.size());
            for (String item : mavenOutput) {
                if (item.contains(filter)) {
                    ValidationMessage msg = new ValidationMessage();
                    msg.setText(item);
                    validationMsgs.add(msg);
                }
            }
            return validationMsgs;
        }
        return Collections.emptyList();
    }

    public static List<ValidationMessage> convertIntoValidationMessage(List<String> mavenOutput) {
        if (mavenOutput.size() > 0) {
            List<ValidationMessage> validationMsgs = new ArrayList<>(mavenOutput.size());
            for (String item : mavenOutput) {
                ValidationMessage msg = new ValidationMessage();
                msg.setText(item);
                validationMsgs.add(msg);
            }
            return validationMsgs;
        }
        return Collections.emptyList();
    }

    public static List<BuildMessage> convertIntoBuildMessage(List<String> mavenOutput) {
        if (mavenOutput.size() > 0) {
            List<BuildMessage> buildMsgs = new ArrayList<>(mavenOutput.size());
            for (String item : mavenOutput) {
                BuildMessage msg = new BuildMessage();
                msg.setText(item);
                buildMsgs.add(msg);
            }
            return buildMsgs;
        }
        return Collections.emptyList();
    }

    public static BuildResults convertIntoBuildResults(List<String> mavenOutput) {
        BuildResults buildRs = new BuildResults();
        if (mavenOutput.size() > 0) {
            for (String item : mavenOutput) {
                BuildMessage msg = new BuildMessage();
                msg.setText(item);
                buildRs.addBuildMessage(msg);
            }
        }
        return buildRs;
    }

    public static IncrementalBuildResults convertIntoIncrementalBuildResults(List<String> mavenOutput) {
        IncrementalBuildResults incrmBuildRes = new IncrementalBuildResults();
        if (mavenOutput.size() > 0) {
            for (String item : mavenOutput) {
                BuildMessage msg = new BuildMessage();
                msg.setText(item);
                incrmBuildRes.addAddedMessage(msg);
            }
        }
        return incrmBuildRes;
    }
}
