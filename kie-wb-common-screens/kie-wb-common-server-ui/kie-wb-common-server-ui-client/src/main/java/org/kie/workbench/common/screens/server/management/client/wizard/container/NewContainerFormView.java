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
package org.kie.workbench.common.screens.server.management.client.wizard.container;

import java.util.ArrayList;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.ContentChangeHandler;
import org.kie.workbench.common.screens.server.management.client.util.StyleHelper;
import org.kie.workbench.common.screens.server.management.client.widget.Div;
import org.kie.workbench.common.screens.server.management.client.widget.artifact.ArtifactListWidgetPresenter;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
@Templated
public class NewContainerFormView extends Composite
        implements NewContainerFormPresenter.View {

    private TranslationService translationService;

    @DataField("new-container-name-form")
    Element containerNameGroup = DOM.createDiv();

    @Inject
    @DataField("new-container-name")
    TextBox containerName;

    @DataField("new-container-name-help")
    Element containerNameHelp = DOM.createSpan();

    @DataField("new-group-id-form")
    Element groupIdGroup = DOM.createDiv();

    @Inject
    @DataField("new-group-id")
    TextBox groupId;

    @DataField("new-artifact-id-form")
    Element artifactIdGroup = DOM.createDiv();

    @Inject
    @DataField("new-artifact-id")
    TextBox artifactId;

    @DataField("new-version-form")
    Element versionGroup = DOM.createDiv();

    @Inject
    @DataField("new-version")
    TextBox version;

    @Inject
    @DataField("content-area")
    Div content;

    private final ArrayList<ContentChangeHandler> changeHandlers = new ArrayList<ContentChangeHandler>();

    @Inject
    public NewContainerFormView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final NewContainerFormPresenter presenter ) {
        containerName.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( presenter.isContainerNameValid() ) {
                    noErrorOnContainerName();
                } else {
                    errorOnContainerName();
                }
                fireChangeHandlers();
            }
        } );

        groupId.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( presenter.isGroupIdValid() ) {
                    noErrorOnGroupId();
                } else {
                    errorOnGroupId();
                }
                fireChangeHandlers();
            }
        } );

        artifactId.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( presenter.isArtifactIdValid() ) {
                    noErrorOnArtifactId();
                } else {
                    errorOnArtifactId();
                }
                fireChangeHandlers();
            }
        } );

        version.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( presenter.isVersionValid() ) {
                    noErrorOnVersion();
                } else {
                    errorOnVersion();
                }
                fireChangeHandlers();
            }
        } );
    }

    private void fireChangeHandlers() {
        for ( final ContentChangeHandler changeHandler : changeHandlers ) {
            changeHandler.onContentChange();
        }
    }

    @Override
    public String getTitle() {
        return getTitleText();
    }

    @Override
    public void setArtifactListWidgetView( final ArtifactListWidgetPresenter.View view ) {
        content.add( view );
    }

    @Override
    public void clear() {
        containerName.setText( "" );
        groupId.setText( "" );
        artifactId.setText( "" );
        version.setText( "" );

        noErrors();
    }

    @Override
    public void noErrors() {
        noErrorOnContainerName();
        noErrorOnGroupId();
        noErrorOnArtifactId();
        noErrorOnVersion();
    }

    @Override
    public void noErrorOnContainerName() {
        containerNameHelp.getStyle().setVisibility( Style.Visibility.HIDDEN );
        StyleHelper.addUniqueEnumStyleName( containerNameGroup, ValidationState.class, ValidationState.NONE );
    }

    @Override
    public void noErrorOnGroupId() {
        StyleHelper.addUniqueEnumStyleName( groupIdGroup, ValidationState.class, ValidationState.NONE );
    }

    @Override
    public void noErrorOnArtifactId() {
        StyleHelper.addUniqueEnumStyleName( artifactIdGroup, ValidationState.class, ValidationState.NONE );
    }

    @Override
    public void noErrorOnVersion() {
        StyleHelper.addUniqueEnumStyleName( versionGroup, ValidationState.class, ValidationState.NONE );
    }

    @Override
    public void addContentChangeHandler( final ContentChangeHandler contentChangeHandler ) {
        changeHandlers.add( checkNotNull( "contentChangeHandler", contentChangeHandler ) );
    }

    @Override
    public void setGroupId( final String value ) {
        groupId.setText( value );
    }

    @Override
    public void setArtifactId( final String value ) {
        artifactId.setText( value );
    }

    @Override
    public void setVersion( final String value ) {
        version.setText( value );
    }

    @Override
    public String getContainerName() {
        return containerName.getText();
    }

    @Override
    public String getGroupId() {
        return groupId.getText();
    }

    @Override
    public String getArtifactId() {
        return artifactId.getText();
    }

    @Override
    public String getVersion() {
        return version.getText();
    }

    @Override
    public void errorOnContainerName() {
        StyleHelper.addUniqueEnumStyleName( containerNameGroup, ValidationState.class, ValidationState.ERROR );
    }

    @Override
    public void errorOnContainerName( final String message ) {
        errorOnContainerName();
        containerNameHelp.getStyle().setVisibility( Style.Visibility.VISIBLE );
        containerNameHelp.setInnerText( message );
    }

    @Override
    public void errorOnGroupId() {
        StyleHelper.addUniqueEnumStyleName( groupIdGroup, ValidationState.class, ValidationState.ERROR );
    }

    @Override
    public void errorOnArtifactId() {
        StyleHelper.addUniqueEnumStyleName( artifactIdGroup, ValidationState.class, ValidationState.ERROR );
    }

    @Override
    public void errorOnVersion() {
        StyleHelper.addUniqueEnumStyleName( versionGroup, ValidationState.class, ValidationState.ERROR );
    }

    @Override
    public String getInvalidErrorMessage() {
        return translationService.format( Constants.NewContainerFormView_InvalidErrorMessage );
    }

    @Override
    public String getNewContainerWizardTitle() {
        return translationService.format( Constants.NewContainerFormView_NewContainerWizardTitle );
    }

    @Override
    public String getNewContainerWizardSaveSuccess() {
        return translationService.format( Constants.NewContainerFormView_NewContainerWizardSaveSuccess );
    }

    @Override
    public String getNewContainerWizardSaveError() {
        return translationService.format( Constants.NewContainerFormView_NewContainerWizardSaveError );
    }

    private String getTitleText() {
        return translationService.format( Constants.NewContainerFormView_TitleText );
    }
}
