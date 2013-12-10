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
package com.app.blockydemo.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.R;
import com.app.blockydemo.formulaeditor.SensorHandler;

import java.util.concurrent.locks.Lock;

public class ProgramMenuActivity extends BaseActivity {

	private static final String TAG = ProgramMenuActivity.class.getSimpleName();
	public static final String FORWARD_TO_SCRIPT_ACTIVITY = "forwardToScriptActivity";

	private Lock viewSwitchLock = new ViewSwitchLock();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(FORWARD_TO_SCRIPT_ACTIVITY)) {
			Intent intent = new Intent(this, ScriptActivity.class);
			intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, bundle.getInt(FORWARD_TO_SCRIPT_ACTIVITY));
			startActivity(intent);
		}

		setContentView(R.layout.activity_program_menu);

		BottomBar.hideAddButton(this);

		final ActionBar actionBar = getActionBar();

		//The try-catch block is a fix for this bug: https://github.com/Catrobat/Catroid/issues/618
		try {
			String title = ProjectManager.getInstance().getCurrentSprite().getName();
			actionBar.setTitle(title);
			actionBar.setHomeButtonEnabled(true);
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, "onCreate: NPE -> finishing", nullPointerException);
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			((Button) findViewById(R.id.program_menu_button_looks)).setText(R.string.backgrounds);
		} else {
			((Button) findViewById(R.id.program_menu_button_looks)).setText(R.string.looks);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	public void handleScriptsButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_SCRIPTS);
	}

	public void handleLooksButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_LOOKS);
	}

	public void handleSoundsButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_SOUNDS);
	}

	public void handlePlayButton(View view) {
		
	}

	private void startScriptActivity(int fragmentPosition) {
		Intent intent = new Intent(this, ScriptActivity.class);
		intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, fragmentPosition);
		startActivity(intent);
	}
}
