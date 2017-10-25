/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.test;

import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.data.modeller.service.impl.DataObjectFormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.impl.BusinessProcessFormModelHandler;
import org.kie.workbench.common.forms.jbpm.server.service.impl.TaskFormModelHandler;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.builder.cache.ModuleCache;
import org.kie.workbench.common.services.shared.project.KieModuleService;

public class TestFormModelHandlerManager implements FormModelHandlerManager {

    private KieModuleService moduleService;

    private ModuleCache moduleCache;

    private FieldManager fieldManager;

    private DataObjectFinderService finderService;

    public TestFormModelHandlerManager(KieModuleService projectService,
                                       ModuleCache moduleCache,
                                       FieldManager fieldManager,
                                       DataObjectFinderService finderService) {
        this.moduleService = projectService;
        this.moduleCache = moduleCache;
        this.fieldManager = fieldManager;
        this.finderService = finderService;
    }

    public TestFormModelHandlerManager(FieldManager fieldManager,
                                       DataObjectFinderService finderService) {
        this.fieldManager = fieldManager;
        this.finderService = finderService;
    }

    @Override
    public FormModelHandler getFormModelHandler(Class<? extends FormModel> clazz) {
        if (BusinessProcessFormModel.class.equals(clazz)) {
            return new BusinessProcessFormModelHandler(moduleService,
                                                       moduleCache,
                                                       fieldManager,
                                                       null);
        }
        if (TaskFormModel.class.equals(clazz)) {
            return new TaskFormModelHandler(moduleService,
                                            moduleCache,
                                            fieldManager,
                                            null);
        }
        return new DataObjectFormModelHandler(moduleService,
                                              moduleCache,
                                              finderService,
                                              fieldManager);
    }
}
