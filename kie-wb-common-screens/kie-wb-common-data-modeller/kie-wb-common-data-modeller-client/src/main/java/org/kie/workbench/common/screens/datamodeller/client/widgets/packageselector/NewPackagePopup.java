/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.Command;

public class NewPackagePopup extends BaseModal {

    private TextBox newPackageName = new TextBox();

    private Button newPackageButton = new Button( Constants.INSTANCE.packageSelector_popup_add() );

    private String packageName;

    private HTMLPanel newPackageHelpHtml = new HTMLPanel( "P", Constants.INSTANCE.validPackageHelp( "<br>" ) );

    private HelpBlock errorMessages = new HelpBlock();

    private FormGroup newPackageControlGroup = new FormGroup();

    private FormGroup errorMessagesGroup = new FormGroup();

    private VerticalPanel mainPanel = new VerticalPanel();

    private HorizontalPanel dataPanel = new HorizontalPanel();

    private Command afterAddCommand;

    @Inject
    ValidatorService validatorService;

    public NewPackagePopup() {
        newPackageButton.setType( ButtonType.PRIMARY );
        newPackageButton.setIcon( IconType.PLUS );

        newPackageName.setPlaceholder( Constants.INSTANCE.package_id_placeholder() );
        newPackageControlGroup.add( newPackageName );
        errorMessagesGroup.add( errorMessages );
        dataPanel.add( newPackageControlGroup );
        dataPanel.add( newPackageButton );
        mainPanel.add( dataPanel );
        mainPanel.add( newPackageHelpHtml );
        mainPanel.add( errorMessagesGroup );
        add( new ModalBody(){{
            add( mainPanel );
        }} );

        setTitle( Constants.INSTANCE.new_dataobject_popup_new_package() );
        setClosable( true );

        newPackageButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {

                setPackageName( null );
                final String packgeName = newPackageName.getText() != null ? newPackageName.getText().trim().toLowerCase() : "";
                validatorService.isValidPackageIdentifier( packgeName, new ValidatorCallback() {
                    @Override
                    public void onFailure() {
                        setErrorMessage( Constants.INSTANCE.validation_error_invalid_package_identifier( packgeName ) );
                    }

                    @Override
                    public void onSuccess() {
                        setPackageName( packgeName );
                        clean();
                        hide();
                        if ( afterAddCommand != null ) {
                            afterAddCommand.execute();
                        }
                    }
                } );
            }
        } );
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName( String packageName ) {
        this.packageName = packageName;
    }

    public Command getAfterAddCommand() {
        return afterAddCommand;
    }

    public void setAfterAddCommand( Command afterAddCommand ) {
        this.afterAddCommand = afterAddCommand;
    }

    @Override
    public String getTitle() {
        return Constants.INSTANCE.packageSelector_popup_title();
    }

    @Override
    public void hide() {
        super.hide();
        clean();
    }

    public void clean() {
        newPackageName.setText( "" );
        cleanErrors();
    }

    public void cleanErrors() {
        errorMessages.setText( "" );
        newPackageControlGroup.setValidationState( ValidationState.NONE );
        errorMessagesGroup.setValidationState( ValidationState.NONE );
    }

    public void setErrorMessage( String errorMessage ) {
        newPackageControlGroup.setValidationState( ValidationState.ERROR );
        errorMessages.setText( errorMessage );
        errorMessagesGroup.setValidationState( ValidationState.ERROR );
    }
}