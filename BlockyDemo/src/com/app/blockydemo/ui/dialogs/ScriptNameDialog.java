/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.app.blockydemo.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.R;
import com.app.blockydemo.content.Script;
import com.app.blockydemo.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ScriptNameDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_script_name";
	Script script;
	public ScriptNameDialog() {
		super();
	}

	public ScriptNameDialog(Script script) {
		super();
		this.script = script;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View dialogView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_script_editor_name, null);

		final Dialog dialogNewVariable = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.formula_editor_script_name)
				.setNegativeButton(R.string.cancel_button, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleOkButton(dialogView);
					}
				}).create();

		dialogNewVariable.setCanceledOnTouchOutside(true);
		dialogNewVariable.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		dialogNewVariable.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				handleOnShow(dialogNewVariable);
			}
		});

		return dialogNewVariable;
	}

	private void handleOkButton(View dialogView) {
		EditText variableNameEditText = (EditText) dialogView
				.findViewById(R.id.dialog_editor_script_name_edit_text);

		String variableName = variableNameEditText.getText().toString();
		script.setName(variableName);
	}

	private void handleOnShow(final Dialog dialogEditName) {
		final Button positiveButton = ((AlertDialog) dialogEditName).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setEnabled(false);

		EditText dialogEditText = (EditText) dialogEditName
				.findViewById(R.id.dialog_editor_script_name_edit_text);

		InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(dialogEditText, InputMethodManager.SHOW_IMPLICIT);

		dialogEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

				String variableName = editable.toString();
				positiveButton.setEnabled(true);
				for (Script local_script:ProjectManager.getInstance().getCurrentSprite().getScriptList()){
					if (local_script.getName().equals(variableName) && !local_script.getName().equals(script.getName())){
						Toast.makeText(getActivity(), R.string.formula_editor_existing_variable, Toast.LENGTH_SHORT).show();
						positiveButton.setEnabled(false);
					}
				}

				if (editable.length() == 0) {
					positiveButton.setEnabled(false);
				}
				if (editable.toString().equals(script.getName())) {
					positiveButton.setEnabled(false);
				}
			}
		});
		dialogEditText.setText(script.getName());
		
	}

}
