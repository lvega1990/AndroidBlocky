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
package com.app.blockydemo.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;


import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.R;
import com.app.blockydemo.common.Constants;
import com.app.blockydemo.content.Script;
import com.app.blockydemo.content.Sprite;
import com.app.blockydemo.content.bricks.AllowedAfterDeadEndBrick;
import com.app.blockydemo.content.bricks.Brick;
import com.app.blockydemo.content.bricks.DeadEndBrick;
import com.app.blockydemo.content.bricks.NestingBrick;
import com.app.blockydemo.content.bricks.ScriptBrick;
import com.app.blockydemo.ui.BottomBar;
import com.app.blockydemo.ui.ScriptActivity;
import com.app.blockydemo.ui.ViewSwitchLock;
import com.app.blockydemo.ui.adapter.BrickAdapter;
import com.app.blockydemo.ui.adapter.BrickAdapter.OnBrickCheckedListener;
import com.app.blockydemo.ui.dialogs.CustomAlertDialogBuilder;
import com.app.blockydemo.ui.dragndrop.DragAndDropListView;
import com.app.blockydemo.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener;
import com.app.blockydemo.utils.Utils;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class ScriptFragment extends ScriptActivityFragment implements OnCategorySelectedListener,
		OnBrickCheckedListener {

	public static final String TAG = ScriptFragment.class.getSimpleName();

	private static final int ACTION_MODE_COPY = 0;
	private static final int ACTION_MODE_DELETE = 1;

	private static int selectedBrickPosition = Constants.NO_POSITION;

	private ActionMode actionMode;
	private View selectAllActionModeButton;

	private BrickAdapter adapter;
	private DragAndDropListView listView;

	private Sprite sprite;
	private Script scriptToEdit;

	private BrickListChangedReceiver brickListChangedReceiver;

	private Lock viewSwitchLock = new ViewSwitchLock();

	private boolean deleteScriptFromContextMenu = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_script, null);

		listView = (DragAndDropListView) rootView.findViewById(android.R.id.list);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initListeners();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onStart() {
		super.onStart();
		initListeners();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (brickListChangedReceiver == null) {
			brickListChangedReceiver = new BrickListChangedReceiver();
		}

		IntentFilter filterBrickListChanged = new IntentFilter(ScriptActivity.ACTION_BRICK_LIST_CHANGED);
		getActivity().registerReceiver(brickListChangedReceiver, filterBrickListChanged);

		initListeners();
	}

	@Override
	public void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();

		if (brickListChangedReceiver != null) {
			getActivity().unregisterReceiver(brickListChangedReceiver);
		}
	}

	public BrickAdapter getAdapter() {
		return adapter;
	}

	@Override
	public DragAndDropListView getListView() {
		return listView;
	}

	@Override
	public void onCategorySelected(String category) {
		AddBrickFragment addBrickFragment = AddBrickFragment.newInstance(category, this);
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.script_fragment_container, addBrickFragment,
				AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		adapter.notifyDataSetChanged();
	}

	public void updateAdapterAfterAddNewBrick(Brick brickToBeAdded) {
		int firstVisibleBrick = listView.getFirstVisiblePosition();
		int lastVisibleBrick = listView.getLastVisiblePosition();
		int position = ((1 + lastVisibleBrick - firstVisibleBrick) / 2);
		position += firstVisibleBrick;
		adapter.addNewBrick(position, brickToBeAdded, true);
		adapter.notifyDataSetChanged();
	}

	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();
		if (sprite == null) {
			return;
		}

		getActivity().findViewById(R.id.button_add).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				handleAddButton();
			}
		});

		adapter = new BrickAdapter(getActivity(), sprite, listView);
		adapter.setOnBrickCheckedListener(this);

		if (ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts() > 0) {
			ProjectManager.getInstance().setCurrentScript(((ScriptBrick) adapter.getItem(0)).initScript(sprite));
		}

		listView.setOnCreateContextMenuListener(this);
		listView.setOnDragAndDropListener(adapter);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
	}

	private void showCategoryFragment() {
		BrickCategoryFragment brickCategoryFragment = new BrickCategoryFragment();
		brickCategoryFragment.setOnCategorySelectedListener(this);
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.add(R.id.script_fragment_container, brickCategoryFragment,
				BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);

		fragmentTransaction.addToBackStack(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
		fragmentTransaction.commit();

		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean getShowDetails() {
		//Currently no showDetails option
		return false;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		//Currently no showDetails option
	}

	@Override
	protected void showRenameDialog() {
		//Rename not supported
	}

	@Override
	public void startRenameActionMode() {
		//Rename not supported
	}

	@Override
	public void startCopyActionMode() {
		startActionMode(copyModeCallBack);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback) {
		actionMode = getActivity().startActionMode(actionModeCallback);

		for (int i = adapter.listItemCount; i < adapter.getBrickList().size(); i++) {
			adapter.getView(i, null, getListView());
		}

		unregisterForContextMenu(listView);
		BottomBar.hideBottomBar(getActivity());
		adapter.setCheckboxVisibility(View.VISIBLE);
		adapter.setActionMode(true);
		updateActionModeTitle();
	}

	@Override
	public void handleAddButton() {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		if (listView.isCurrentlyDragging()) {
			listView.animateHoveringBrick();
			return;
		}

		showCategoryFragment();
	}

	@Override
	public boolean getActionModeActive() {
		return actionModeActive;
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	@Override
	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void showDeleteDialog() {
	}

	private class BrickListChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_BRICK_LIST_CHANGED)) {
				adapter.updateProjectBrickList();
			}
		}
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getLayoutInflater(null), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				adapter.checkAllItems();
			}
		});
	}

	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTag(ACTION_MODE_DELETE);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedBricksAndEnableButtons();
			} else {
				showConfirmDeleteDialog(false);
			}
		}
	};

	private ActionMode.Callback copyModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTag(ACTION_MODE_COPY);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			List<Brick> checkedBricks = adapter.getCheckedBricks();

			for (Brick brick : checkedBricks) {
				copyBrick(brick);
				if (brick instanceof ScriptBrick) {
					break;
				}
			}

			clearCheckedBricksAndEnableButtons();
		}
	};

	private void copyBrick(Brick brick) {
		if (brick instanceof NestingBrick
				&& (brick instanceof AllowedAfterDeadEndBrick || brick instanceof DeadEndBrick)) {
			return;
		}

		if (brick instanceof ScriptBrick) {
			scriptToEdit = ((ScriptBrick) brick).initScript(ProjectManager.getInstance().getCurrentSprite());

			Script clonedScript = scriptToEdit.copyScriptForSprite(sprite);

			sprite.addScript(clonedScript);
			adapter.initBrickList();
			adapter.notifyDataSetChanged();

			return;
		}

		int brickId = adapter.getBrickList().indexOf(brick);
		if (brickId == -1) {
			return;
		}

		int newPosition = adapter.getCount();
		Brick copy = brick.clone();
		Script scriptList = ProjectManager.getInstance().getCurrentScript();

		if (brick instanceof NestingBrick) {
			NestingBrick nestingBrickCopy = (NestingBrick) copy;
			nestingBrickCopy.initialize();

			for (Brick nestingBrick : nestingBrickCopy.getAllNestingBrickParts(true)) {
				scriptList.addBrick(nestingBrick);
			}
		} else {
			scriptList.addBrick(copy);
		}

		adapter.addNewBrick(newPosition, copy, false);
		adapter.initBrickList();

		adapter.notifyDataSetChanged();
	}

	private void deleteBrick(Brick brick) {

		if (brick instanceof ScriptBrick) {
			scriptToEdit = ((ScriptBrick) brick).initScript(ProjectManager.getInstance().getCurrentSprite());
			adapter.handleScriptDelete(sprite, scriptToEdit);
			return;
		}
		int brickId = adapter.getBrickList().indexOf(brick);
		if (brickId == -1) {
			return;
		}
		adapter.removeFromBrickListAndProject(brickId, true);
	}

	private void deleteCheckedBricks() {
		List<Brick> checkedBricks = adapter.getReversedCheckedBrickList();

		for (Brick brick : checkedBricks) {
			deleteBrick(brick);
		}
	}

	private void showConfirmDeleteDialog(boolean fromContextMenu) {
		this.deleteScriptFromContextMenu = fromContextMenu;
		int titleId;
		if ((deleteScriptFromContextMenu && scriptToEdit.getBrickList().size() == 0)
				|| adapter.getAmountOfCheckedItems() == 1) {
			titleId = R.string.dialog_confirm_delete_brick_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_bricks_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_brick_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (deleteScriptFromContextMenu) {
					adapter.handleScriptDelete(sprite, scriptToEdit);
				} else {
					deleteCheckedBricks();
					clearCheckedBricksAndEnableButtons();
				}
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if (!deleteScriptFromContextMenu) {
					clearCheckedBricksAndEnableButtons();
				}
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void clearCheckedBricksAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.showBottomBar(getActivity());
		adapter.setActionMode(false);
	}

	@Override
	public void onBrickChecked() {
		updateActionModeTitle();
		Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
	}

	private void updateActionModeTitle() {
		int numberOfSelectedItems = adapter.getAmountOfCheckedItems();

		String completeTitle;
		switch ((Integer) actionMode.getTag()) {
			case ACTION_MODE_COPY:
				completeTitle = getResources().getQuantityString(R.plurals.number_of_bricks_to_copy,
						numberOfSelectedItems, numberOfSelectedItems);
				break;
			case ACTION_MODE_DELETE:
				completeTitle = getResources().getQuantityString(R.plurals.number_of_bricks_to_delete,
						numberOfSelectedItems, numberOfSelectedItems);
				break;
			default:
				throw new IllegalArgumentException("Wrong or unhandled tag in ActionMode.");
		}

		int indexOfNumber = completeTitle.indexOf(' ') + 1;
		Spannable completeSpannedTitle = new SpannableString(completeTitle);
		completeSpannedTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)),
				indexOfNumber, indexOfNumber + String.valueOf(numberOfSelectedItems).length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		actionMode.setTitle(completeSpannedTitle);
	}

	@Override
	public void startBackPackActionMode() {

	}

}
