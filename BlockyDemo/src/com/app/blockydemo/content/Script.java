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


import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.content.bricks.Brick;
import com.app.blockydemo.content.bricks.IfLogicBeginBrick;
import com.app.blockydemo.content.bricks.IfLogicElseBrick;
import com.app.blockydemo.content.bricks.IfLogicEndBrick;
import com.app.blockydemo.content.bricks.LoopBeginBrick;
import com.app.blockydemo.content.bricks.LoopEndBrick;
import com.app.blockydemo.content.bricks.NestingBrick;
import com.app.blockydemo.content.bricks.ScriptBrick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Script implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Brick> brickList;

	protected transient ScriptBrick brick;

	private transient volatile boolean paused;
	protected Sprite object;
	protected String name;
	
	public Script() {
	}

	public abstract Script copyScriptForSprite(Sprite copySprite);

	protected Object readResolve() {
		init();
		return this;
	}

	public abstract ScriptBrick getScriptBrick();

	public Script(Sprite sprite) {
		brickList = new ArrayList<Brick>();
		this.object = sprite;
		this.name = getNextName();
		init();
	}

	private String getNextName() {
		String value = "Script";
		int pos = 0;
		boolean cont = true;
		while (cont){
			cont = false;
			for (Script script:ProjectManager.getInstance().getCurrentSprite().getScriptList()){
				if (script.getName().equals(value)){
					cont = true;
					++pos;
					value = "Script"+pos;
				}
			}
			
		}
		return value;
	}
	public void setNextName() {
		String value =String.copyValueOf(name.toCharArray());
		int pos = 0;
		boolean cont = true;
		while (cont){
			cont = false;
			for (Script script:ProjectManager.getInstance().getCurrentSprite().getScriptList()){
				if (script.getName().equals(value)){
					cont = true;
					++pos;
					value = String.copyValueOf(name.toCharArray())+pos;
				}
			}
			
		}
		name = value;
	}
	private void init() {
		paused = false;
	}

	public void addBrick(Brick brick) {
		if (brick != null) {
			brickList.add(brick);
		}
	}

	public void addBrick(int position, Brick brick) {
		if (brick != null) {
			brickList.add(position, brick);
		}
	}

	public void removeBrick(Brick brick) {
		brickList.remove(brick);
	}

	public ArrayList<Brick> getBrickList() {
		return brickList;
	}
	
	public JSONObject getBrickListJson () {
		JSONObject json = new JSONObject();
		
		for (int i = 0; i < brickList.size(); i++) {
			try
            {
				JSONObject brickjson = new JSONObject();
				brickjson.put("position", i);
				
				json.put(brickList.get(i).getClass().getName(), brickjson);
            }
            catch (JSONException e)
            {
	            e.printStackTrace();
            }
		}
		
		return json;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRequiredResources() {
		int ressources = Brick.NO_RESOURCES;

		for (Brick brick : brickList) {
			ressources |= brick.getRequiredResources();
		}
		return ressources;
	}

	public boolean containsBrickOfType(Class<?> type) {
		for (Brick brick : brickList) {
			//Log.i("bt", brick.REQUIRED_RESSOURCES + "");
			if (brick.getClass() == type) {
				return true;
			}
		}
		return false;
	}

	public int containsBrickOfTypeReturnsFirstIndex(Class<?> type) {
		int i = 0;
		for (Brick brick : brickList) {

			if (brick.getClass() == type) {
				return i;
			}
			i++;
		}
		return -1;
	}

	//
	//	public boolean containsBluetoothBrick() {
	//		for (Brick brick : brickList) {
	//			if ((brick instanceof NXTMotorActionBrick) || (brick instanceof NXTMotorTurnAngleBrick)
	//					|| (brick instanceof NXTMotorStopBrick) || (brick instanceof NXTPlayToneBrick)) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	public Brick getBrick(int index) {
		if (index < 0 || index >= brickList.size()) {
			return null;
		}

		return brickList.get(index);
	}

	protected void setIfBrickReferences(IfLogicEndBrick copiedIfEndBrick, IfLogicEndBrick originalIfEndBrick) {
		List<NestingBrick> ifBrickList = originalIfEndBrick.getAllNestingBrickParts(true);
		IfLogicBeginBrick copiedIfBeginBrick = ((IfLogicBeginBrick) ifBrickList.get(0)).getCopy();
		IfLogicElseBrick copiedIfElseBrick = ((IfLogicElseBrick) ifBrickList.get(1)).getCopy();

		copiedIfBeginBrick.setIfElseBrick(copiedIfElseBrick);
		copiedIfBeginBrick.setIfEndBrick(copiedIfEndBrick);
		copiedIfElseBrick.setIfBeginBrick(copiedIfBeginBrick);
		copiedIfElseBrick.setIfEndBrick(copiedIfEndBrick);
		copiedIfEndBrick.setIfBeginBrick(copiedIfBeginBrick);
		copiedIfEndBrick.setIfElseBrick(copiedIfElseBrick);
	}

	protected void setLoopBrickReferences(LoopEndBrick copiedBrick, LoopEndBrick originalBrick) {
		List<NestingBrick> loopBrickList = originalBrick.getAllNestingBrickParts(true);
		LoopBeginBrick copiedLoopBeginBrick = ((LoopBeginBrick) loopBrickList.get(0)).getCopy();

		copiedLoopBeginBrick.setLoopEndBrick(copiedBrick);
		copiedBrick.setLoopBeginBrick(copiedLoopBeginBrick);
	}
}
