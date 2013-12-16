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
package com.app.blockydemo.content;

import android.content.Context;

import com.app.blockydemo.R;
import com.app.blockydemo.common.ScreenValues;
import com.app.blockydemo.formulaeditor.UserVariablesContainer;
import com.app.blockydemo.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Sprite> spriteList = new ArrayList<Sprite>();
	private UserVariablesContainer userVariables = null;
private String mName = null;
	public Project(Context context, String name) {

		ifLandscapeSwitchWidthAndHeight();
		if (ScreenValues.SCREEN_HEIGHT == 0 || ScreenValues.SCREEN_WIDTH == 0) {
			Utils.updateScreenWidthAndHeight(context);
		}
		userVariables = new UserVariablesContainer();
		mName = name;
		if (context == null) {
			return;
		}

		Sprite background = new Sprite(context.getString(R.string.background));
		addSprite(background);
	}

	private void ifLandscapeSwitchWidthAndHeight() {
		if (ScreenValues.SCREEN_WIDTH > ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		}

	}

	public synchronized void addSprite(Sprite sprite) {
		if (spriteList.contains(sprite)) {
			return;
		}
		spriteList.add(sprite);

	}

	public synchronized boolean removeSprite(Sprite sprite) {
		return spriteList.remove(sprite);

	}

	public List<Sprite> getSpriteList() {
		return spriteList;
	}


	// default constructor for XMLParser
	public Project() {
	}

	public UserVariablesContainer getUserVariables() {
		return userVariables;
	}

	public String getName() {
		return mName;
	}

}
