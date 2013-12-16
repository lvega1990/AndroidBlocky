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

import android.content.Context;

import com.app.blockydemo.R;
import com.app.blockydemo.common.BrickValues;
import com.app.blockydemo.content.Script;
import com.app.blockydemo.content.Sprite;
import com.app.blockydemo.content.StartScript;
import com.app.blockydemo.content.bricks.Brick;
import com.app.blockydemo.content.bricks.ChangeVariableBrick;
import com.app.blockydemo.content.bricks.ExecuteActionBrick;
import com.app.blockydemo.content.bricks.ForeverBrick;
import com.app.blockydemo.content.bricks.IfLogicBeginBrick;
import com.app.blockydemo.content.bricks.IfLogicElseBrick;
import com.app.blockydemo.content.bricks.IfLogicEndBrick;
import com.app.blockydemo.content.bricks.RepeatBrick;
import com.app.blockydemo.content.bricks.SetVariableBrick;
import com.app.blockydemo.content.bricks.WhenStartedBrick;
import com.app.blockydemo.formulaeditor.Formula;
import com.app.blockydemo.formulaeditor.FormulaElement;
import com.app.blockydemo.formulaeditor.UserVariable;
import com.app.blockydemo.formulaeditor.FormulaElement.ElementType;

import java.util.ArrayList;
import java.util.List;

public class CategoryBricksFactory {

	public List<Brick> getBricks(String category, Sprite sprite, Context context) {
		if (category.equals(context.getString(R.string.category_control))) {
			return setupControlCategoryList(sprite, context);
		} else if (category.equals(context.getString(R.string.category_variables))) {
			return setupVariablesCategoryList(sprite);
		}else if (category.equals(context.getString(R.string.category_marketplace))) {
			return setupMathCategoryList(sprite);
		}

		return new ArrayList<Brick>();
	}

	private List<Brick> setupControlCategoryList(Sprite sprite, Context context) {
		List<Brick> controlBrickList = new ArrayList<Brick>();
		controlBrickList.add(new WhenStartedBrick(sprite, null));
		//controlBrickList.add(new WhenBrick(sprite, null));
		controlBrickList.add(new ForeverBrick(sprite));
		controlBrickList.add(new IfLogicBeginBrick(sprite, 0));
		controlBrickList.add(new RepeatBrick(sprite, BrickValues.REPEAT));

		controlBrickList.add(new ExecuteActionBrick(sprite, 0));
		
		return controlBrickList;
	}

	private List<Brick> setupMathCategoryList(Sprite sprite) 	{
		List<Brick> scriptBrickList = new ArrayList<Brick>();
		Script script = new StartScript(sprite);
		script.setName("Upload to picasa");
		script.addBrick(new ExecuteActionBrick(sprite, new Formula(new FormulaElement(ElementType.USER_VARIABLE, "Feature(Take Picture)", null))));
		IfLogicBeginBrick begin = new IfLogicBeginBrick(sprite, new Formula(new FormulaElement(ElementType.USER_VARIABLE, "WifiOn", null)));
		IfLogicElseBrick iflogicelse =new IfLogicElseBrick(sprite, begin);
		SetVariableBrick var = new SetVariableBrick(sprite,  new Formula(new FormulaElement(ElementType.USER_VARIABLE, "Feature(Is Wifi on)", null)), new UserVariable("WifiOn"));
		script.addBrick(var);
		script.addBrick(begin);
		script.addBrick(new ExecuteActionBrick(sprite, new Formula(new FormulaElement(ElementType.USER_VARIABLE, "Feature(Upload to picasa)", null))));
		script.addBrick(iflogicelse);
		script.addBrick(new ExecuteActionBrick(sprite, new Formula(new FormulaElement(ElementType.USER_VARIABLE, "Feature(Save in SDCard)", null))));
		script.addBrick(new IfLogicEndBrick(sprite, iflogicelse,begin));
		scriptBrickList.add(new WhenStartedBrick(sprite, script));
		
		Script script2  = new StartScript(sprite);
		script2.setName("Take a picture");
		script2.addBrick(new ExecuteActionBrick(sprite, new Formula(new FormulaElement(ElementType.USER_VARIABLE, "Feature(Take Picture)", null))));
		scriptBrickList.add(new WhenStartedBrick(sprite, script2));
		
		Script script3  = new StartScript(sprite);
		script3.setName("Call number");
		script3.addBrick(new ExecuteActionBrick(sprite, new Formula(new FormulaElement(ElementType.USER_VARIABLE, "Feature(Call Number)", null))));
		scriptBrickList.add(new WhenStartedBrick(sprite, script3));
		return scriptBrickList;
	}


	private List<Brick> setupVariablesCategoryList(Sprite sprite) {
		List<Brick> userVariablesBrickList = new ArrayList<Brick>();
		userVariablesBrickList.add(new SetVariableBrick(sprite, 0));
		userVariablesBrickList.add(new ChangeVariableBrick(sprite, 0));
		return userVariablesBrickList;
	}

}
