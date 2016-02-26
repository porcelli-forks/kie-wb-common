package org.kie.workbench.common.screens.server.management.client.navigation.template.copy;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
public class CopyPopupView extends BaseModal
        implements CopyPopupPresenter.View {

    //@Inject
    //private TranslationService translationService;

    private CopyPopupPresenter presenter;

    interface CopyPopupViewBinder
            extends
            UiBinder<Widget, CopyPopupView> {

    }

    private static CopyPopupViewBinder uiBinder = GWT.create( CopyPopupViewBinder.class );

    @UiField
    FormGroup templateNameGroup;

    @UiField
    FormLabel templateNameLabel;

    @UiField
    TextBox templateName;

    @UiField
    HelpBlock templateNameHelpInline;

    public CopyPopupView() {
        setTitle( getCopyServerTemplatePopupTitle() );

        setBody( uiBinder.createAndBindUi( CopyPopupView.this ) );
        templateNameLabel.setText( getTemplateNameLabelText() );
        add( new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                presenter.save();
            }
        }, new Command() {
            @Override
            public void execute() {
                hide();
            }
        } ) );
    }

    @Override
    public void init( final CopyPopupPresenter presenter ) {
        this.presenter = presenter;
        templateName.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !templateName.getText().trim().isEmpty() ) {
                    templateNameGroup.setValidationState( ValidationState.NONE );
                    templateNameHelpInline.setVisible( false );
                }
            }
        } );
    }

    @Override
    public void display() {
        this.show();
    }

    @Override
    public void clear() {
        templateName.setText( "" );
        templateNameGroup.setValidationState( ValidationState.NONE );
        templateNameHelpInline.setVisible( false );
    }

    @Override
    public String getNewTemplateName() {
        return templateName.getText();
    }

    @Override
    public void errorOnTemplateNameFromGroup() {
        templateNameGroup.setValidationState( ValidationState.ERROR );
        templateNameHelpInline.setText( getTemplateNameEmptyMessage() );
        templateNameHelpInline.setVisible( true );
    }

    @Override
    public void errorOnTemplateNameFromGroup( final String message ) {
        templateNameGroup.setValidationState( ValidationState.ERROR );
        templateNameHelpInline.setText( message );
        templateNameHelpInline.setVisible( true );
    }

    private String getTemplateNameLabelText() {
        return "Template Name";//translationService.format( Constants.CopyPopupView_TemplateNameLabelText );
    }

    private String getCopyServerTemplatePopupTitle() {
        return "Copy Server Template";//translationService.format( Constants.CopyPopupView_CopyServerTemplatePopupTitle );
    }

    private String getTemplateNameEmptyMessage() {
        return "Name can't be empty";//translationService.format( Constants.CopyPopupView_TemplateNameEmptyMessage );
    }
}
