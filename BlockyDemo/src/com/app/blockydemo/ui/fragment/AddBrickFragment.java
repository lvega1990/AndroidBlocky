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

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.R;
import com.app.blockydemo.content.Script;
import com.app.blockydemo.content.Sprite;
import com.app.blockydemo.content.bricks.Brick;
import com.app.blockydemo.content.bricks.ScriptBrick;
import com.app.blockydemo.ui.adapter.PrototypeBrickAdapter;

import java.util.List;

public class AddBrickFragment extends ListFragment {

	private static final String BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category";
	public static final String ADD_BRICK_FRAGMENT_TAG = "add_brick_fragment";
	private ScriptFragment scriptFragment;
	private CharSequence previousActionBarTitle;
	private PrototypeBrickAdapter adapter;
	private CategoryBricksFactory categoryBricksFactory = new CategoryBricksFactory();

	public static AddBrickFragment newInstance(String selectedCategory, ScriptFragment scriptFragment) {
		AddBrickFragment fragment = new AddBrickFragment();
		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY, selectedCategory);
		fragment.setArguments(arguments);
		fragment.scriptFragment = scriptFragment;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_brick_add, null);

		setUpActionBar();
		setupSelectedBrickCategory();

		return view;
	}

	private void setupSelectedBrickCategory() {
		Context context = getActivity();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		String selectedCategory = getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY);

		List<Brick> brickList = categoryBricksFactory.getBricks(selectedCategory, sprite, context);
		adapter = new PrototypeBrickAdapter(context, brickList);
		setListAdapter(adapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void setUpActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		previousActionBarTitle = actionBar.getTitle();
		actionBar.setTitle(this.getArguments().getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY));
	}

	private void resetActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(previousActionBarTitle);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onDestroy() {
		resetActionBar();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		setupSelectedBrickCategory();
	}

	@Override
	public void onStart() {
		super.onStart();

		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Brick brickToBeAdded = adapter.getItem(position).clone();
				scriptFragment.updateAdapterAfterAddNewBrick(brickToBeAdded);

				if (brickToBeAdded instanceof ScriptBrick) {
					Script script = ((ScriptBrick) brickToBeAdded).initScript(ProjectManager.getInstance()
							.getCurrentSprite());
					ProjectManager.getInstance().setCurrentScript(script);
				}

				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				Fragment categoryFragment = getFragmentManager().findFragmentByTag(
						BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);
				if (categoryFragment != null) {
					fragmentTransaction.remove(categoryFragment);
					getFragmentManager().popBackStack();
				}
				Fragment addBrickFragment = getFragmentManager().findFragmentByTag(
						AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);
				if (addBrickFragment != null) {
					fragmentTransaction.remove(addBrickFragment);
					getFragmentManager().popBackStack();
				}
				fragmentTransaction.commit();
			}

		});
	}
}
