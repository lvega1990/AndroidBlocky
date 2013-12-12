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
package com.app.blockydemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import  com.app.blockydemo.content.Project;
import  com.app.blockydemo.content.Script;
import  com.app.blockydemo.content.Sprite;
import java.io.File;
import java.io.IOException;

public final class ProjectManager {
	private static final ProjectManager INSTANCE = new ProjectManager();
	private static final String TAG = ProjectManager.class.getSimpleName();

	private Project project;
	private Script currentScript;
	private Sprite currentSprite;
	private boolean asynchronTask = true;


	private ProjectManager() {
	}

	public static ProjectManager getInstance() {
		return INSTANCE;
	}



	public void initializeNewProject(String projectName, Context context, boolean empty) throws IOException {
		project = new Project(context, projectName);
		currentSprite = null;
		currentScript = null;
	}

	public Project getCurrentProject() {
		return project;
	}

	public void setProject(Project project) {
		currentScript = null;
		currentSprite = null;

		this.project = project;
	}

	public Sprite getCurrentSprite() {
		return currentSprite;
	}

	public void setCurrentSprite(Sprite sprite) {
		currentSprite = sprite;
	}

	public Script getCurrentScript() {
		return currentScript;
	}

	public void setCurrentScript(Script script) {
		if (script == null) {
			currentScript = null;
		} else if (currentSprite.getScriptIndex(script) != -1) {
			currentScript = script;
		}
	}

	public void addSprite(Sprite sprite) {
		project.addSprite(sprite);
	}

	public void addScript(Script script) {
		currentSprite.addScript(script);
	}

	public boolean spriteExists(String spriteName) {
		for (Sprite sprite : project.getSpriteList()) {
			if (sprite.getName().equalsIgnoreCase(spriteName)) {
				return true;
			}
		}
		return false;
	}

	public boolean scriptExists(String scriptName) {
		
		for (Script script : currentSprite.getScriptList()) {
			if (script.getName().equalsIgnoreCase(scriptName)) {
				return true;
			}
		}
		return false;
	}
	
	public int getCurrentSpritePosition() {
		return project.getSpriteList().indexOf(currentSprite);
	}

	public int getCurrentScriptPosition() {
		int currentSpritePosition = this.getCurrentSpritePosition();
		if (currentSpritePosition == -1) {
			return -1;
		}

		return project.getSpriteList().get(currentSpritePosition).getScriptIndex(currentScript);
	}

	private String createTemporaryDirectoryName(String projectDirectoryName) {
		String temporaryDirectorySuffix = "_tmp";
		String temporaryDirectoryName = projectDirectoryName + temporaryDirectorySuffix;
		return temporaryDirectoryName;
	}


	private class SaveProjectAsynchronousTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	}


	private void showLoginRegisterDialog(FragmentActivity fragmentActivity) {
	}

}
