/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.widgets.client.popups.validation;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

/**
 * A popup that lists BuildMessages
 */
public class ValidationPopup extends BaseModal {

    interface ValidationPopupWidgetBinder
            extends
            UiBinder<Widget, ValidationPopup> {

    }

    private static ValidationPopupWidgetBinder uiBinder = GWT.create( ValidationPopupWidgetBinder.class );

    private static ValidationPopup instance = new ValidationPopup();

    @UiField
    protected CellTable<ValidationMessage> table;

    private ValidationPopup() {
        setTitle( CommonConstants.INSTANCE.ValidationErrors() );
        setHideOtherModals( false );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( ValidationPopup.this ) );
        }} );

        add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );

        final ValidationMessageLevelColumn validationMessageLevelColumn = new ValidationMessageLevelColumn() {

            @Override
            public Level getValue( final ValidationMessage msg ) {
                return msg.getLevel();
            }
        };
        table.addColumn( validationMessageLevelColumn );
        table.setColumnWidth( validationMessageLevelColumn,
                              "32px" );
        table.addColumn( new TextColumn<ValidationMessage>() {

            @Override
            public String getValue( final ValidationMessage msg ) {
                return msg.getText();
            }
        } );
    }

    private void setMessages( final List<ValidationMessage> messages ) {
        this.table.setRowData( messages );
    }

    public static void showMessages( final List<ValidationMessage> messages ) {
        instance.setMessages( messages );
        instance.show();
    }

}
