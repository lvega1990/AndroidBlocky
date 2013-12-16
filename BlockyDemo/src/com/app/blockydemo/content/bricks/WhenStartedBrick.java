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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.blockydemo.R;
import com.app.blockydemo.content.Script;
import com.app.blockydemo.content.Sprite;
import com.app.blockydemo.content.StartScript;

import java.util.ArrayList;
import java.util.List;

public class WhenStartedBrick extends ScriptBrick {
	private static final long serialVersionUID = 1L;

	private Script script;


	public WhenStartedBrick(Sprite sprite, Script script) {
		this.script = script;
		this.sprite = sprite;


	}

	public WhenStartedBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		WhenStartedBrick copyBrick = (WhenStartedBrick) clone();
		copyBrick.sprite = sprite;
		copyBrick.script = script;
		return copyBrick;
	}

	@Override
	public View getView(Context context, int brickId, final BaseAdapter baseAdapter) {
		if (animationState) {
			return view;

		}
		view = View.inflate(context, R.layout.brick_when_started, null);

		setCheckboxView(R.id.brick_when_started_checkbox);

		if (this.script != null){
			((TextView) view.findViewById(R.id.brick_when_started_name)).setText(script.getName());	
		}
		//method moved to to DragAndDropListView since it is not working on 2.x
		/*
		 * checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		 * 
		 * @Override
		 * public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		 * checked = isChecked;
		 * if (!checked) {
		 * for (Brick currentBrick : adapter.getCheckedBricks()) {
		 * currentBrick.setCheckedBoolean(false);
		 * }
		 * }
		 * adapter.handleCheck(brickInstance, checked);
		 * 
		 * }
		 * });
		 */

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		if (script != null){
			view = View.inflate(context, R.layout.brick_when_started, null);
			if (script.getName() != null && !script.getName().equals(""))
				((TextView)view.findViewById(R.id.brick_when_started_name)).setText(script.getName());
			return view;
		}else{
			return View.inflate(context, R.layout.brick_when_started, null);	
		}
	}

	@Override
	public Brick clone() {
		if (script != null)
			script.setNextName();
		return new WhenStartedBrick(getSprite(), script);
	}

	@Override
	public Script initScript(Sprite sprite) {
		if (script == null) {
			script = new StartScript(sprite);
		}

		return script;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_when_started_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			this.alphaValue = (alphaValue);

		}

		return view;
	}

	@Override
	public boolean isInitialized() {
		return script != null;
	}

	@Override
	public List<Brick> getAllNestingBrickParts(boolean sorted) {
		List<Brick> briks = new ArrayList<Brick>();
		briks.add(this);
		if (isInitialized()){
			briks.addAll(script.getBrickList());
		}
		return briks;
	}
}
