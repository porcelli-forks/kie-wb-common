/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.guvnor.common.services.project.builder.model.BuildResults;

/**
 * Test Observer for Build events
 */
@ApplicationScoped
public class BuildResultsObserver {

    private volatile BuildResults buildResults;

    public void onBuildResults(final @Observes BuildResults results) {
        this.buildResults = results;
    }

    public BuildResults getBuildResults() {
        return buildResults;
    }
}
