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

import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.R;
import com.app.blockydemo.common.BrickValues;
import com.app.blockydemo.common.MessageContainer;
import com.app.blockydemo.content.Sprite;
import com.app.blockydemo.content.bricks.Brick;
import com.app.blockydemo.content.bricks.BroadcastBrick;
import com.app.blockydemo.content.bricks.BroadcastReceiverBrick;
import com.app.blockydemo.content.bricks.BroadcastWaitBrick;
import com.app.blockydemo.content.bricks.ChangeBrightnessByNBrick;
import com.app.blockydemo.content.bricks.ChangeGhostEffectByNBrick;
import com.app.blockydemo.content.bricks.ChangeSizeByNBrick;
import com.app.blockydemo.content.bricks.ChangeVariableBrick;
import com.app.blockydemo.content.bricks.ChangeVolumeByNBrick;
import com.app.blockydemo.content.bricks.ChangeXByNBrick;
import com.app.blockydemo.content.bricks.ChangeYByNBrick;
import com.app.blockydemo.content.bricks.ClearGraphicEffectBrick;
import com.app.blockydemo.content.bricks.ComeToFrontBrick;
import com.app.blockydemo.content.bricks.ForeverBrick;
import com.app.blockydemo.content.bricks.GlideToBrick;
import com.app.blockydemo.content.bricks.GoNStepsBackBrick;
import com.app.blockydemo.content.bricks.HideBrick;
import com.app.blockydemo.content.bricks.IfLogicBeginBrick;
import com.app.blockydemo.content.bricks.IfOnEdgeBounceBrick;
import com.app.blockydemo.content.bricks.LegoNxtMotorActionBrick;
import com.app.blockydemo.content.bricks.LegoNxtMotorStopBrick;
import com.app.blockydemo.content.bricks.LegoNxtMotorTurnAngleBrick;
import com.app.blockydemo.content.bricks.LegoNxtPlayToneBrick;
import com.app.blockydemo.content.bricks.MoveNStepsBrick;
import com.app.blockydemo.content.bricks.NextLookBrick;
import com.app.blockydemo.content.bricks.NoteBrick;
import com.app.blockydemo.content.bricks.PlaceAtBrick;
import com.app.blockydemo.content.bricks.PointInDirectionBrick;
import com.app.blockydemo.content.bricks.PointInDirectionBrick.Direction;
import com.app.blockydemo.content.bricks.PointToBrick;
import com.app.blockydemo.content.bricks.RepeatBrick;
import com.app.blockydemo.content.bricks.SetBrightnessBrick;
import com.app.blockydemo.content.bricks.SetGhostEffectBrick;
import com.app.blockydemo.content.bricks.SetSizeToBrick;
import com.app.blockydemo.content.bricks.SetVariableBrick;
import com.app.blockydemo.content.bricks.SetVolumeToBrick;
import com.app.blockydemo.content.bricks.SetXBrick;
import com.app.blockydemo.content.bricks.SetYBrick;
import com.app.blockydemo.content.bricks.ShowBrick;
import com.app.blockydemo.content.bricks.SpeakBrick;
import com.app.blockydemo.content.bricks.StopAllSoundsBrick;
import com.app.blockydemo.content.bricks.TurnLeftBrick;
import com.app.blockydemo.content.bricks.TurnRightBrick;
import com.app.blockydemo.content.bricks.WaitBrick;
import com.app.blockydemo.content.bricks.WhenBrick;
import com.app.blockydemo.content.bricks.WhenStartedBrick;
import com.app.blockydemo.formulaeditor.Formula;
import com.app.blockydemo.formulaeditor.FormulaElement;
import com.app.blockydemo.formulaeditor.FormulaElement.ElementType;
import com.app.blockydemo.formulaeditor.Operators;

import java.util.ArrayList;
import java.util.List;

public class CategoryBricksFactory {

	public List<Brick> getBricks(String category, Sprite sprite, Context context) {
		if (category.equals(context.getString(R.string.category_control))) {
			return setupControlCategoryList(sprite, context);
		} else if (category.equals(context.getString(R.string.category_variables))) {
			return setupVariablesCategoryList(sprite);
		}else if (category.equals(context.getString(R.string.category_math))) {
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

		return controlBrickList;
	}

	private List<Brick> setupMathCategoryList(Sprite sprite) {
		List<Brick> motionBrickList = new ArrayList<Brick>();
		motionBrickList.add(new PlaceAtBrick(sprite, BrickValues.X_POSITION, BrickValues.Y_POSITION));
		motionBrickList.add(new SetXBrick(sprite, BrickValues.X_POSITION));
		motionBrickList.add(new SetYBrick(sprite, BrickValues.Y_POSITION));
		motionBrickList.add(new ChangeXByNBrick(sprite, BrickValues.CHANGE_X_BY));
		motionBrickList.add(new ChangeYByNBrick(sprite, BrickValues.CHANGE_Y_BY));

		if (!isBackground(sprite)) {
			motionBrickList.add(new IfOnEdgeBounceBrick(sprite));
		}

		motionBrickList.add(new MoveNStepsBrick(sprite, BrickValues.MOVE_STEPS));
		motionBrickList.add(new TurnLeftBrick(sprite, BrickValues.TURN_DEGREES));
		motionBrickList.add(new TurnRightBrick(sprite, BrickValues.TURN_DEGREES));
		motionBrickList.add(new PointInDirectionBrick(sprite, Direction.RIGHT));
		motionBrickList.add(new PointToBrick(sprite, null));
		motionBrickList.add(new GlideToBrick(sprite, BrickValues.X_POSITION, BrickValues.Y_POSITION,
				BrickValues.GLIDE_SECONDS));

		if (!isBackground(sprite)) {
			motionBrickList.add(new GoNStepsBackBrick(sprite, BrickValues.GO_BACK));
			motionBrickList.add(new ComeToFrontBrick(sprite));
		}

		return motionBrickList;
	}

	private List<Brick> setupSoundCategoryList(Sprite sprite, Context context) {
		List<Brick> soundBrickList = new ArrayList<Brick>();
		soundBrickList.add(new StopAllSoundsBrick(sprite));
		soundBrickList.add(new SetVolumeToBrick(sprite, BrickValues.SET_VOLUME_TO));

		// workaround to set a negative default value for a Brick
		float positiveDefaultValueChangeVolumeBy = Math.abs(BrickValues.CHANGE_VOLUME_BY);
		FormulaElement defaultValueChangeVolumeBy = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.name(),
				null, null, new FormulaElement(ElementType.NUMBER, String.valueOf(positiveDefaultValueChangeVolumeBy),
						null));
		soundBrickList.add(new ChangeVolumeByNBrick(sprite, new Formula(defaultValueChangeVolumeBy)));

		soundBrickList.add(new SpeakBrick(sprite, context.getString(R.string.brick_speak_default_value)));

		return soundBrickList;
	}

	private List<Brick> setupLooksCategoryList(Sprite sprite) {
		List<Brick> looksBrickList = new ArrayList<Brick>();
		looksBrickList.add(new NextLookBrick(sprite));
		looksBrickList.add(new SetSizeToBrick(sprite, BrickValues.SET_SIZE_TO));
		looksBrickList.add(new ChangeSizeByNBrick(sprite, BrickValues.CHANGE_SIZE_BY));
		looksBrickList.add(new HideBrick(sprite));
		looksBrickList.add(new ShowBrick(sprite));
		looksBrickList.add(new SetGhostEffectBrick(sprite, BrickValues.SET_GHOST_EFFECT));
		looksBrickList.add(new ChangeGhostEffectByNBrick(sprite, BrickValues.CHANGE_GHOST_EFFECT));
		looksBrickList.add(new SetBrightnessBrick(sprite, BrickValues.SET_BRIGHTNESS_TO));
		looksBrickList.add(new ChangeBrightnessByNBrick(sprite, BrickValues.CHANGE_BRITHNESS_BY));
		looksBrickList.add(new ClearGraphicEffectBrick(sprite));

		return looksBrickList;
	}

	private List<Brick> setupVariablesCategoryList(Sprite sprite) {
		List<Brick> userVariablesBrickList = new ArrayList<Brick>();
		userVariablesBrickList.add(new SetVariableBrick(sprite, 0));
		userVariablesBrickList.add(new ChangeVariableBrick(sprite, 0));
		return userVariablesBrickList;
	}

	private List<Brick> setupLegoNxtCategoryList(Sprite sprite) {
		List<Brick> legoNXTBrickList = new ArrayList<Brick>();
		legoNXTBrickList.add(new LegoNxtMotorTurnAngleBrick(sprite, LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A,
				BrickValues.LEGO_ANGLE));
		legoNXTBrickList.add(new LegoNxtMotorStopBrick(sprite, LegoNxtMotorStopBrick.Motor.MOTOR_A));
		legoNXTBrickList.add(new LegoNxtMotorActionBrick(sprite, LegoNxtMotorActionBrick.Motor.MOTOR_A,
				BrickValues.LEGO_SPEED));
		legoNXTBrickList.add(new LegoNxtPlayToneBrick(sprite, BrickValues.LEGO_FREQUENCY, BrickValues.LEGO_DURATION));

		return legoNXTBrickList;
	}

	private boolean isBackground(Sprite sprite) {
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(sprite) == 0) {
			return true;
		}
		return false;
	}
}
