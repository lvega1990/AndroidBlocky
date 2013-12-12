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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.blockydemo.R;
import com.app.blockydemo.ui.BottomBar;
import com.app.blockydemo.ui.ViewSwitchLock;
import com.app.blockydemo.ui.adapter.BrickCategoryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class BrickCategoryFragment extends ListFragment {

	public static final String BRICK_CATEGORY_FRAGMENT_TAG = "brick_category_fragment";

	private CharSequence previousActionBarTitle;
	private OnCategorySelectedListener onCategorySelectedListener;
	BrickCategoryAdapter adapter;

	private Lock viewSwitchLock = new ViewSwitchLock();

	public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
		onCategorySelectedListener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_brick_categories, null);

		setUpActionBar();
		BottomBar.hideBottomBar(getActivity());
		setupBrickCategories();

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!viewSwitchLock.tryLock()) {
					return;
				}

				if (onCategorySelectedListener != null) {
					onCategorySelectedListener.onCategorySelected(adapter.getItem(position));
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		setupBrickCategories();
	}

	@Override
	public void onDestroy() {
		resetActionBar();
		BottomBar.showBottomBar(getActivity());
		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	private void setUpActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);

		this.previousActionBarTitle = actionBar.getTitle();
		actionBar.setTitle(R.string.categories);
	}

	private void resetActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(this.previousActionBarTitle);
	}

	private void setupBrickCategories() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		List<View> categories = new ArrayList<View>();
		categories.add(inflater.inflate(R.layout.brick_category_control, null));
		categories.add(inflater.inflate(R.layout.brick_category_uservariables, null));
		categories.add(inflater.inflate(R.layout.brick_category_math, null));
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean("setting_mindstorm_bricks", false)) {
			categories.add(inflater.inflate(R.layout.brick_category_math, null));
		}

		adapter = new BrickCategoryAdapter(categories);
		this.setListAdapter(adapter);
	}

	public interface OnCategorySelectedListener {

		void onCategorySelected(String category);

	}
}
