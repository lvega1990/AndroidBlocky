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
package com.app.blockydemo.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;


import com.app.blockydemo.R;
import com.app.blockydemo.content.Script;
import com.app.blockydemo.content.Sprite;
import com.app.blockydemo.formulaeditor.Formula;
import com.app.blockydemo.ui.fragment.FormulaEditorFragment;

public class ExecuteActionBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;
	private Formula mFeature;

	private transient View prototypeView;

	public ExecuteActionBrick(Sprite sprite, int xFeatureValue) {
		this.sprite = sprite;
		mFeature = new Formula(xFeatureValue);
	}

	public ExecuteActionBrick(Sprite sprite, Formula xFeature) {
		this.sprite = sprite;
		this.mFeature = xFeature;
	}

	public ExecuteActionBrick() {

	}

	@Override
	public Formula getFormula() {
		return mFeature;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		ExecuteActionBrick copyBrick = (ExecuteActionBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_execute_action, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_execute_action_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});
		TextView textX = (TextView) view.findViewById(R.id.brick_execute_action_prototype_text_view);
		TextView editX = (TextView) view.findViewById(R.id.brick_execute_action_edit_text);

		mFeature.setTextFieldId(R.id.brick_execute_action_edit_text);
		mFeature.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_execute_action_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textX = (TextView) view.findViewById(R.id.brick_execute_action_text_view);
			TextView editX = (TextView) view.findViewById(R.id.brick_execute_action_edit_text);
			textX.setTextColor(textX.getTextColors().withAlpha(alphaValue));
			editX.setTextColor(editX.getTextColors().withAlpha(alphaValue));
			editX.getBackground().setAlpha(alphaValue);

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_execute_action, null);
		TextView textXPosition = (TextView) prototypeView.findViewById(R.id.brick_execute_action_prototype_text_view);
		textXPosition.setText(String.valueOf(mFeature.interpretInteger(sprite)));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new ExecuteActionBrick(getSprite(), mFeature.clone());
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, mFeature);
	}
}
