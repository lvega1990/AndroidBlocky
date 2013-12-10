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
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;

import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.R;
import com.app.blockydemo.content.Sprite;
import com.app.blockydemo.formulaeditor.SensorHandler;
import com.app.blockydemo.ui.adapter.BrickAdapter;
import com.app.blockydemo.ui.adapter.ScriptActivityAdapterInterface;
import com.app.blockydemo.ui.dragndrop.DragAndDropListView;
import com.app.blockydemo.ui.fragment.FormulaEditorFragment;
import com.app.blockydemo.ui.fragment.FormulaEditorListFragment;
import com.app.blockydemo.ui.fragment.FormulaEditorVariableListFragment;
import com.app.blockydemo.ui.fragment.ScriptActivityFragment;
import com.app.blockydemo.ui.fragment.ScriptFragment;

import java.io.IOException;
import java.util.concurrent.locks.Lock;


public class ScriptActivity extends BaseActivity {
	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_LOOKS = 1;
	public static final int FRAGMENT_SOUNDS = 2;

	public static final String EXTRA_FRAGMENT_POSITION = "com.app.blockydemo.ui.fragmentPosition";

	public static final String ACTION_SPRITE_RENAMED = "com.app.blockydemo.SPRITE_RENAMED";
	public static final String ACTION_SPRITES_LIST_INIT = "com.app.blockydemo.SPRITES_LIST_INIT";
	public static final String ACTION_SPRITES_LIST_CHANGED = "com.app.blockydemo.SPRITES_LIST_CHANGED";
	public static final String ACTION_BRICK_LIST_CHANGED = "com.app.blockydemo.BRICK_LIST_CHANGED";
	public static final String ACTION_LOOK_DELETED = "com.app.blockydemo.LOOK_DELETED";
	public static final String ACTION_LOOK_RENAMED = "com.app.blockydemo.LOOK_RENAMED";
	public static final String ACTION_LOOKS_LIST_INIT = "com.app.blockydemo.LOOKS_LIST_INIT";
	public static final String ACTION_SOUND_DELETED = "com.app.blockydemo.SOUND_DELETED";
	public static final String ACTION_SOUND_COPIED = "com.app.blockydemo.SOUND_COPIED";
	public static final String ACTION_SOUND_RENAMED = "com.app.blockydemo.SOUND_RENAMED";
	public static final String ACTION_SOUNDS_LIST_INIT = "com.app.blockydemo.SOUNDS_LIST_INIT";
	public static final String ACTION_VARIABLE_DELETED = "com.app.blockydemo.VARIABLE_DELETED";

	private FragmentManager fragmentManager = getSupportFragmentManager();

	private ScriptFragment scriptFragment = null;

	private ScriptActivityFragment currentFragment = null;

	private static int currentFragmentPosition;
	private String currentFragmentTag;

	private Lock viewSwitchLock = new ViewSwitchLock();

	private boolean isSoundFragmentFromPlaySoundBrickNew = false;
	private boolean isSoundFragmentHandleAddButtonHandled = false;
	private boolean isLookFragmentFromSetLookBrickNew = false;
	private boolean isLookFragmentHandleAddButtonHandled = false;

	private ImageButton buttonAdd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_script);
		ProjectManager projectManager = ProjectManager.getInstance();
		//create project
		try {
			projectManager.initializeNewProject("test", this, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sprite sprite = new Sprite("Back");
		projectManager.addSprite(sprite);
		projectManager.setCurrentSprite(sprite);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		currentFragmentPosition = FRAGMENT_SCRIPTS;

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		updateCurrentFragment(currentFragmentPosition, fragmentTransaction);
		fragmentTransaction.commit();

		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);

		buttonAdd = (ImageButton) findViewById(R.id.button_add);
		updateHandleAddButtonClickListener();
	}

	public void updateHandleAddButtonClickListener() {
		if (buttonAdd == null) {
			buttonAdd = (ImageButton) findViewById(R.id.button_add);
		}
		buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				handleAddButton(view);
			}
		});
	}

	private void updateCurrentFragment(int fragmentPosition, FragmentTransaction fragmentTransaction) {
		boolean fragmentExists = true;
		currentFragmentPosition = fragmentPosition;

		switch (currentFragmentPosition) {
			case FRAGMENT_SCRIPTS:
				if (scriptFragment == null) {
					scriptFragment = new ScriptFragment();
					fragmentExists = false;
					currentFragmentTag = ScriptFragment.TAG;
				}
				currentFragment = scriptFragment;
				break;
			case FRAGMENT_LOOKS:
				break;
			case FRAGMENT_SOUNDS:
				break;
		}

		updateHandleAddButtonClickListener();

		if (fragmentExists) {
			fragmentTransaction.show(currentFragment);
		} else {
			fragmentTransaction.add(R.id.script_fragment_container, currentFragment, currentFragmentTag);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setVolumeControlStream(AudioManager.STREAM_RING);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		for (String tag : FormulaEditorListFragment.TAGS) {
			FormulaEditorListFragment fragment = (FormulaEditorListFragment) fragmentManager.findFragmentByTag(tag);
			if (fragment != null) {
				if (fragment.isVisible()) {
					return fragment.onKey(null, keyCode, event);
				}
			}
		}

		FormulaEditorVariableListFragment formulaEditorVariableListFragment = (FormulaEditorVariableListFragment) getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		if (formulaEditorVariableListFragment != null) {
			if (formulaEditorVariableListFragment.isVisible()) {
				return formulaEditorVariableListFragment.onKey(null, keyCode, event);
			}
		}

		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getSupportFragmentManager().findFragmentByTag(
				FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditor != null) {
			if (formulaEditor.isVisible()) {
				scriptFragment.getAdapter().updateProjectBrickList();
				return formulaEditor.onKey(null, keyCode, event);
			}
		}

		int backStackEntryCount = fragmentManager.getBackStackEntryCount();
		for (int i = backStackEntryCount; i > 0; --i) {
			String backStackEntryName = fragmentManager.getBackStackEntryAt(i - 1).getName();
			if (backStackEntryName != null) {
				fragmentManager.popBackStack();
			} else {
				break;
			}
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentFragmentPosition == FRAGMENT_SCRIPTS) {
				DragAndDropListView listView = scriptFragment.getListView();
				if (listView.isCurrentlyDragging()) {
					listView.resetDraggingScreen();

					BrickAdapter adapter = scriptFragment.getAdapter();
					adapter.removeDraggedBrick();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

		}
	}

	public void handleAddButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		currentFragment.handleAddButton();
	}

	public void handlePlayButton(View view) {
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//Dismiss ActionMode without effecting checked items

		FormulaEditorVariableListFragment formulaEditorVariableListFragment = (FormulaEditorVariableListFragment) getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		if (formulaEditorVariableListFragment != null) {
			if (formulaEditorVariableListFragment.isVisible()) {
				ListAdapter adapter = formulaEditorVariableListFragment.getListAdapter();
				((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
				return super.dispatchKeyEvent(event);
			}
		}

		if (currentFragment != null && currentFragment.getActionModeActive()) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
				ListAdapter adapter = null;
				if (currentFragment instanceof ScriptFragment) {
					adapter = ((ScriptFragment) currentFragment).getAdapter();
				} else {
					adapter = currentFragment.getListAdapter();
				}
				((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
			}
		}

		return super.dispatchKeyEvent(event);
	}

	public boolean isHoveringActive() {
		if (currentFragmentPosition == FRAGMENT_SCRIPTS && scriptFragment.getListView().isCurrentlyDragging()) {
			return true;
		}
		return false;
	}


	public ScriptActivityFragment getFragment(int fragmentPosition) {
		ScriptActivityFragment fragment = null;

		switch (fragmentPosition) {
			case FRAGMENT_SCRIPTS:
				fragment = scriptFragment;
				break;
			case FRAGMENT_LOOKS:
				break;
			case FRAGMENT_SOUNDS:
				break;
		}
		return fragment;
	}

	public void setCurrentFragment(int fragmentPosition) {

		switch (fragmentPosition) {
			case FRAGMENT_SCRIPTS:
				currentFragment = scriptFragment;
				currentFragmentPosition = FRAGMENT_SCRIPTS;
				currentFragmentTag = ScriptFragment.TAG;
				break;
			case FRAGMENT_LOOKS:
				break;
			case FRAGMENT_SOUNDS:
				break;
		}
	}

	public boolean getIsSoundFragmentFromPlaySoundBrickNew() {
		return this.isSoundFragmentFromPlaySoundBrickNew;
	}

	public void setIsSoundFragmentFromPlaySoundBrickNewFalse() {
		this.isSoundFragmentFromPlaySoundBrickNew = false;
		// TODO quickfix for issue #521 - refactor design (activity and fragment interaction)
		updateHandleAddButtonClickListener();
	}

	public boolean getIsSoundFragmentHandleAddButtonHandled() {
		return this.isSoundFragmentHandleAddButtonHandled;
	}

	public void setIsSoundFragmentHandleAddButtonHandled(boolean isSoundFragmentHandleAddButtonHandled) {
		this.isSoundFragmentHandleAddButtonHandled = isSoundFragmentHandleAddButtonHandled;
	}

	public boolean getIsLookFragmentFromSetLookBrickNew() {
		return this.isLookFragmentFromSetLookBrickNew;
	}

	public void setIsLookFragmentFromSetLookBrickNewFalse() {
		this.isLookFragmentFromSetLookBrickNew = false;
		// TODO quickfix for issue #521 - refactor design (activity and fragment interaction)
		updateHandleAddButtonClickListener();
	}

	public boolean getIsLookFragmentHandleAddButtonHandled() {
		return this.isLookFragmentHandleAddButtonHandled;
	}

	public void setIsLookFragmentHandleAddButtonHandled(boolean isLookFragmentHandleAddButtonHandled) {
		this.isLookFragmentHandleAddButtonHandled = isLookFragmentHandleAddButtonHandled;
	}

	public void switchToFragmentFromScriptFragment(int fragmentPosition) {

		ScriptActivityFragment scriptFragment = getFragment(ScriptActivity.FRAGMENT_SCRIPTS);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (scriptFragment.isVisible()) {
			fragmentTransaction.hide(scriptFragment);
		}

		switch (fragmentPosition) {
			case FRAGMENT_LOOKS:
				break;

			case FRAGMENT_SOUNDS:
				break;
		}

		updateHandleAddButtonClickListener();
		fragmentTransaction.commit();
	}
}
