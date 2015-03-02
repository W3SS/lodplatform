/*******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2012 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.ucuenca.pentaho.plugin.step.rdf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;

/**
 * This class is part of the demo step plug-in implementation. It demonstrates
 * the basics of developing a plug-in step for PDI.
 * 
 * The demo step adds a new string field to the row stream and sets its value to
 * "Hello World!". The user may select the name of the new field.
 * 
 * This class is the implementation of StepDialogInterface. Classes implementing
 * this interface need to:
 * 
 * - build and open a SWT dialog displaying the step's settings (stored in the
 * step's meta object) - write back any changes the user makes to the step's
 * meta object - report whether the user changed any settings when confirming
 * the dialog
 * 
 */
public class RDFGenerationDialog extends BaseStepDialog implements
		StepDialogInterface {

	/**
	 * The PKG member is used when looking up internationalized strings. The
	 * properties file with localized keys is expected to reside in {the package
	 * of the class specified}/messages/messages_{locale}.properties
	 */
	private static Class<?> PKG = RDFGenerationMeta.class; // for i18n purposes

	// this is the object the stores the step's settings
	// the dialog reads the settings from it when opening
	// the dialog writes the settings to it when confirmed
	private RDFGenerationMeta meta;

	
	private Text txtdatabaseUrl;
	private Text txtdatabaseSchema;
	private Text txtuserName;
	private Text txtbaseUri;
	private Text txtoutputFileRDF;
	private Text txtpassword;

	private Button btnloadFile;
	private Button btnTest;
	private Button btnreuseConection;
	private Button btnloadDirectory;

	private FormData fdloadFile;

	private Label lbsqlvendor;
	private Label lbdatabaseUrl;
	private Label lbdatabaseSchema;
	private Label lbuserName;
	private Label lbpassword;
	private Label lboutputFileRDF;
	private Label lboutputFormat;
	private Label lbbaseUri;

	private FormData fdsqlvendor;
	private FormData fddatabaseUrl;
	private FormData fduserName;
	private FormData fdpassword;
	private FormData fdlbbaseUri;
	private FormData fdlboutputFormat;
	private FormData fddatabaseSchema;

	private CCombo cbmsqlvendor;
	private CCombo cbmoutputFormat;
	private FormData fbcbmsqlvendor;
	private FormData fdloadDirectory;
	private FormData fbcbmoutputFormat;

	// text field holding the name of the field to add to the row stream
	private Text txtR2rmlfile;

	/**
	 * The constructor should simply invoke super() and save the incoming meta
	 * object to a local variable, so it can conveniently read and write
	 * settings from/to it.
	 * 
	 * @param parent
	 *            the SWT shell to open the dialog in
	 * @param in
	 *            the meta object holding the step's settings
	 * @param transMeta
	 *            transformation description
	 * @param sname
	 *            the step name
	 */
	public RDFGenerationDialog(Shell parent, Object in, TransMeta transMeta,
			String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		meta = (RDFGenerationMeta) in;
	}

	/**
	 * This method is called by Spoon when the user opens the settings dialog of
	 * the step. It should open the dialog and return only once the dialog has
	 * been closed by the user.
	 * 
	 * If the user confirms the dialog, the meta object (passed in the
	 * constructor) must be updated to reflect the new step settings. The
	 * changed flag of the meta object must reflect whether the step
	 * configuration was changed by the dialog.
	 * 
	 * If the user cancels the dialog, the meta object must not be updated, and
	 * its changed flag must remain unaltered.
	 * 
	 * The open() method must return the name of the step after the user has
	 * confirmed the dialog, or null if the user cancelled the dialog.
	 */
	public String open() {

		// store some convenient SWT variables
		Shell parent = getParent();
		Display display = parent.getDisplay();

		// SWT code for preparing the dialog
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN
				| SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, meta);

		// Save the value of the changed flag on the meta object. If the user
		// cancels
		// the dialog, it will be restored to this saved value.
		// The "changed" variable is inherited from BaseStepDialog
		changed = meta.hasChanged();

		// The ModifyListener used on all controls. It will update the meta
		// object to
		// indicate that changes are being made.
		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				meta.setChanged();
			}
		};

		// ------------------------------------------------------- //
		// SWT code for building the actual settings dialog //
		// ------------------------------------------------------- //
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "RDFGeneration.Shell.Title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname
				.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(80, 0);
		wStepname.setLayoutData(fdStepname);

		// output field value
		Label lbR2rmlFile = new Label(shell, SWT.RIGHT);
		lbR2rmlFile.setText(BaseMessages.getString(PKG,
				"RDFGeneration.FieldName.Label"));
		props.setLook(lbR2rmlFile);
		FormData fdlbR2rmlFile = new FormData();
		fdlbR2rmlFile.left = new FormAttachment(0, 0);
		fdlbR2rmlFile.right = new FormAttachment(middle, -margin);
		fdlbR2rmlFile.top = new FormAttachment(wStepname, margin);
		lbR2rmlFile.setLayoutData(fdlbR2rmlFile);

		txtR2rmlfile = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(txtR2rmlfile);
		txtR2rmlfile.addModifyListener(lsMod);
		FormData fdtxtR2rmlfile = new FormData();
		fdtxtR2rmlfile.left = new FormAttachment(middle, 0);
		fdtxtR2rmlfile.right = new FormAttachment(80, 0);
		fdtxtR2rmlfile.top = new FormAttachment(wStepname, margin);
		txtR2rmlfile.setLayoutData(fdtxtR2rmlfile);

		btnloadFile = new Button(shell, SWT.PUSH );
		props.setLook(btnloadFile);
		btnloadFile.setText(BaseMessages.getString(PKG,
				"RDFGeneration.button.Filer2rml"));
		btnloadFile.setToolTipText(BaseMessages.getString(PKG,
				"System.Tooltip.BrowseForFileOrDirAndAdd"));
		btnloadFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				LoadFile();
			}			
		});
		fdloadFile = new FormData();
		fdloadFile.left = new FormAttachment(txtR2rmlfile, 0);
		fdloadFile.top = new FormAttachment(wStepname, margin);
		btnloadFile.setLayoutData(fdloadFile);
		
		
		  lbsqlvendor = new Label(shell, SWT.RIGHT);
	        lbsqlvendor.setText(BaseMessages.getString(PKG, "RDFGeneration.label.sqlvendor"));
	        props.setLook(lbsqlvendor);
	        fdsqlvendor = new FormData();
	        fdsqlvendor.left = new FormAttachment(0, 0);
	        fdsqlvendor.right = new FormAttachment(middle, -margin);
	        fdsqlvendor.top = new FormAttachment(txtR2rmlfile, margin);
	        lbsqlvendor.setLayoutData(fdsqlvendor);
	        
	        cbmsqlvendor = new CCombo(shell,  SWT.SINGLE | SWT.LEFT |  SWT.BORDER);
	        cbmsqlvendor.setEditable(true);
	        props.setLook(cbmsqlvendor);
	        cbmsqlvendor.addModifyListener(lsMod);
	        fbcbmsqlvendor = new FormData();
	        fbcbmsqlvendor.left = new FormAttachment(middle, 0);
	        fbcbmsqlvendor.top = new FormAttachment(btnloadFile, margin);
	        fbcbmsqlvendor.right = new FormAttachment(80, -margin);
	        cbmsqlvendor.setLayoutData(fbcbmsqlvendor);
	        cbmsqlvendor.setEnabled(false);
	        cbmsqlvendor.addSelectionListener(new SelectionListener() {

	            public void widgetSelected(SelectionEvent selectionevent)
	            {
	            }

	            public void widgetDefaultSelected(SelectionEvent selectionevent)
	            {
	            }	          
	        });
	        
	        
	        lbdatabaseUrl = new Label(shell, SWT.RIGHT);
	        lbdatabaseUrl.setText(BaseMessages.getString(PKG, "RDFGeneration.label.dataBaseUri"));
	        props.setLook(lbdatabaseUrl);
	        fddatabaseUrl = new FormData();
	        fddatabaseUrl.left = new FormAttachment(0, 0);
	        fddatabaseUrl.right = new FormAttachment(middle, -margin);
	        fddatabaseUrl.top = new FormAttachment(cbmsqlvendor, margin);
	        lbdatabaseUrl.setLayoutData(fddatabaseUrl);
	        
	        txtdatabaseUrl = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	        props.setLook(txtdatabaseUrl);
	        txtdatabaseUrl.addModifyListener(lsMod);         
	        FormData fdtxtdatabaseUrl = new FormData();
	        fdtxtdatabaseUrl.left = new FormAttachment(middle, 0);
	        fdtxtdatabaseUrl.right = new FormAttachment(80, 0);
	        fdtxtdatabaseUrl.top = new FormAttachment(cbmsqlvendor, margin);
	        txtdatabaseUrl.setLayoutData(fdtxtdatabaseUrl);
	        
	        
	        
	        lbdatabaseSchema = new Label(shell, SWT.RIGHT);
	        lbdatabaseSchema.setText(BaseMessages.getString(PKG, "RDFGeneration.label.dataBaseSchema"));
	        props.setLook(lbdatabaseSchema);
	        this.fddatabaseSchema = new FormData();
	        this.fddatabaseSchema.left = new FormAttachment(0, 0);
	        this.fddatabaseSchema.right = new FormAttachment(middle, -margin);
	        this.fddatabaseSchema.top = new FormAttachment(txtdatabaseUrl, margin);
	        lbdatabaseSchema.setLayoutData(this.fddatabaseSchema);
	        
	        txtdatabaseSchema = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	        props.setLook(txtdatabaseSchema);
	        txtdatabaseSchema.addModifyListener(lsMod);
	        FormData fddatabaseSchema = new FormData();
	        fddatabaseSchema.left = new FormAttachment(middle, 0);
	        fddatabaseSchema.right = new FormAttachment(80, 0);
	        fddatabaseSchema.top = new FormAttachment(txtdatabaseUrl, margin);
	        txtdatabaseSchema.setLayoutData(fddatabaseSchema);
	        
	        
	        lbuserName = new Label(shell,  SWT.RIGHT);
	        lbuserName.setText(BaseMessages.getString(PKG, "RDFGeneration.label.userName"));
	        props.setLook(lbuserName);
	        fduserName = new FormData();
	        fduserName.left = new FormAttachment(0, 0);
	        fduserName.right = new FormAttachment(middle, -margin);
	        fduserName.top = new FormAttachment(txtdatabaseSchema, margin);
	        lbuserName.setLayoutData(fduserName);
	        
	        txtuserName = new Text(shell,  SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	        props.setLook(txtuserName);
	        txtuserName.addModifyListener(lsMod);
	        FormData fbuserName = new FormData();
	        fbuserName.left = new FormAttachment(middle, 0);
	        fbuserName.right = new FormAttachment(80, 0);
	        fbuserName.top = new FormAttachment(txtdatabaseSchema, margin);
	        txtuserName.setLayoutData(fbuserName);
	        
	        lbpassword = new Label(shell, SWT.RIGHT);
	        lbpassword.setText(BaseMessages.getString(PKG, "RDFGeneration.label.Password"));
	        props.setLook(lbpassword);
	        fdpassword = new FormData();
	        fdpassword.left = new FormAttachment(0, 0);
	        fdpassword.right = new FormAttachment(middle, -margin);
	        fdpassword.top = new FormAttachment(txtuserName, margin);
	        lbpassword.setLayoutData(fdpassword);
	        
	        txtpassword = new Text(shell,SWT.SINGLE | SWT.LEFT | SWT.BORDER  | SWT.PASSWORD);
	        props.setLook(txtpassword);
	        txtpassword.addModifyListener(lsMod);
	        FormData fdtxtpassword = new FormData();
	        fdtxtpassword.left = new FormAttachment(middle, 0);
	        fdtxtpassword.right = new FormAttachment(80, 0);
	        fdtxtpassword.top = new FormAttachment(txtuserName, margin);
	        txtpassword.setLayoutData(fdtxtpassword);
	        
	        lbbaseUri = new Label(shell, SWT.RIGHT);
	        lbbaseUri.setText(BaseMessages.getString(PKG, "RDFGeneration.label.BaseUri"));
	        props.setLook(lbbaseUri);
	        fdlbbaseUri = new FormData();
	        fdlbbaseUri.left = new FormAttachment(0, 0);
	        fdlbbaseUri.right = new FormAttachment(middle, -margin);
	        fdlbbaseUri.top = new FormAttachment(txtpassword, margin);
	        lbbaseUri.setLayoutData(fdlbbaseUri);
	        
	        txtbaseUri = new Text(shell,  SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	        props.setLook(txtbaseUri);
	        txtbaseUri.addModifyListener(lsMod);
	        FormData fdbaseUri = new FormData();
	        fdbaseUri.left = new FormAttachment(middle, 0);
	        fdbaseUri.right = new FormAttachment(80, 0);
	        fdbaseUri.top = new FormAttachment(txtpassword, margin);
	        txtbaseUri.setLayoutData(fdbaseUri);
	        
	        lboutputFileRDF = new Label(shell, SWT.RIGHT);
	        lboutputFileRDF.setText(BaseMessages.getString(PKG, "RDFGeneration.label.outputRDFfiel"));
	        props.setLook(lboutputFileRDF);
	        FormData fdloutputrdf = new FormData();
	        fdloutputrdf.left = new FormAttachment(0, 0);
	        fdloutputrdf.right = new FormAttachment(middle, -margin);
	        fdloutputrdf.top = new FormAttachment(txtbaseUri, margin);
	        lboutputFileRDF.setLayoutData(fdloutputrdf);
	        
	        txtoutputFileRDF = new Text(shell,  SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	        props.setLook(txtoutputFileRDF);
	        txtoutputFileRDF.addModifyListener(lsMod);
	        FormData fdtxtoutputrdf = new FormData();
	        fdtxtoutputrdf.left = new FormAttachment(middle, 0);
	        fdtxtoutputrdf.right = new FormAttachment(80, 0);
	        fdtxtoutputrdf.top = new FormAttachment(txtbaseUri, margin);
	        txtoutputFileRDF.setLayoutData(fdtxtoutputrdf);
	        
	        btnloadDirectory = new Button(shell, SWT.PUSH);
	        props.setLook(btnloadDirectory);
	        btnloadDirectory.setText(BaseMessages.getString(PKG, "RDFGeneration.button.OutputFileRDF"));
	        btnloadDirectory.setToolTipText(BaseMessages.getString(PKG, "System.Tooltip.BrowseForFileOrDirAndAdd", new String[0]));
	        btnloadDirectory.addSelectionListener(new SelectionAdapter() {

	            public void widgetSelected(SelectionEvent e)
	            {
	                Directory();
	            }
	           
	        }
	);
	        fdloadDirectory = new FormData();
	        fdloadDirectory.left = new FormAttachment(txtoutputFileRDF, 0);
	        fdloadDirectory.top = new FormAttachment(txtbaseUri, margin);
	        btnloadDirectory.setLayoutData(fdloadDirectory);
	        
	        lboutputFormat = new Label(shell, SWT.RIGHT);
	        lboutputFormat.setText(BaseMessages.getString(PKG, "RDFGeneration.label.Formats", new String[0]));
	        props.setLook(lboutputFormat);
	        fdlboutputFormat = new FormData();
	        fdlboutputFormat.left = new FormAttachment(0, 0);
	        fdlboutputFormat.right = new FormAttachment(middle, -margin);
	        fdlboutputFormat.top = new FormAttachment(txtoutputFileRDF, margin);
	        lboutputFormat.setLayoutData(fdlboutputFormat);
	        
	        cbmoutputFormat = new CCombo(shell,  SWT.SINGLE | SWT.LEFT | SWT.BORDER);
	        cbmoutputFormat.setEditable(true);
	        props.setLook(cbmoutputFormat);
	        cbmoutputFormat.addModifyListener(lsMod);
	        fbcbmoutputFormat = new FormData();
	        fbcbmoutputFormat.left = new FormAttachment(middle, 0);
	        fbcbmoutputFormat.top = new FormAttachment(btnloadDirectory, margin);
	        fbcbmoutputFormat.right = new FormAttachment(80, -margin);
	        cbmoutputFormat.setLayoutData(fbcbmoutputFormat);
	        cbmoutputFormat.setEnabled(false);
	        cbmoutputFormat.addSelectionListener(new SelectionListener() {

	            public void widgetSelected(SelectionEvent selectionevent)
	            {
	            }

	            public void widgetDefaultSelected(SelectionEvent selectionevent)
	            {
	            }

	            
	        }
	);
	        btnTest = new Button(shell, 8);
	        btnTest.setText(BaseMessages.getString(PKG, "RDFGeneration.button.Test", new String[0]));
	        btnreuseConection = new Button(shell, 8);
	        btnreuseConection.setText(BaseMessages.getString(PKG, "RDFGeneration.button.ReuseConection", new String[0]));
	        BaseStepDialog.positionBottomButtons(shell, new Button[] {
	            btnTest, btnreuseConection
	        }, margin, cbmoutputFormat);
		
		
		

		// OK and cancel buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		BaseStepDialog.positionBottomButtons(shell,
				new Button[] { wOK, wCancel }, margin, btnreuseConection);

		// Add listeners for cancel and OK
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);

		// default listener (for hitting "enter")
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};
		wStepname.addSelectionListener(lsDef);
		txtR2rmlfile.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window and cancel the
		// dialog properly
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Set/Restore the dialog size based on last position on screen
		// The setSize() method is inherited from BaseStepDialog
		setSize();

		// populate the dialog with the values from the meta object
		populateDialog();

		// restore the changed flag to original value, as the modify listeners
		// fire during dialog population
		meta.setChanged(changed);

		// open dialog and enter event loop
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		// at this point the dialog has closed, so either ok() or cancel() have
		// been executed
		// The "stepname" variable is inherited from BaseStepDialog
		return stepname;
	}

	/**
	 * This helper method puts the step configuration stored in the meta object
	 * and puts it into the dialog controls.
	 */
	private void populateDialog() {
		wStepname.selectAll();
		txtR2rmlfile.setText(meta.getInputFieldr2rml());
	}

	/**
	 * Called when the user cancels the dialog.
	 */
	private void cancel() {
		// The "stepname" variable will be the return value for the open()
		// method.
		// Setting to null to indicate that dialog was cancelled.
		stepname = null;
		// Restoring original "changed" flag on the met aobject
		meta.setChanged(changed);
		// close the SWT dialog window
		dispose();
	}

	/**
	 * Called when the user confirms the dialog
	 */
	private void ok() {
		// The "stepname" variable will be the return value for the open()
		// method.
		// Setting to step name from the dialog control
		stepname = wStepname.getText();
		// Setting the settings to the meta object
		meta.setInputFieldr2rml(txtR2rmlfile.getText());
		// close the SWT dialog window
		dispose();
	}
	
	private void LoadFile()
    {
        try
        {
            FileDialog dialog = new FileDialog(shell, 4096);
            dialog.setText(BaseMessages.getString(PKG, "GetPropertiesOWL.FieldName.Choose"));
            String result = dialog.open();
            txtR2rmlfile.setText(result);
        }
        catch(Exception e) { }
    }

    private void Directory()
    {
        try
        {
            DirectoryDialog directorio = new DirectoryDialog(shell, 4096);
            directorio.setText(BaseMessages.getString(PKG, "GetPropertiesOWL.FieldName.Choose"));
            String result = directorio.open();
            txtoutputFileRDF.setText(result);
        }
        catch(Exception e) { }
    }
}
