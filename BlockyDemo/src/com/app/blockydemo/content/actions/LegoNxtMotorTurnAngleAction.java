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
package com.app.blockydemo.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import com.app.blockydemo.content.Sprite;
import com.app.blockydemo.content.bricks.LegoNxtMotorTurnAngleBrick.Motor;
import com.app.blockydemo.formulaeditor.Formula;

public class LegoNxtMotorTurnAngleAction extends TemporalAction {

	private static final int NO_DELAY = 0;
	private Motor motorEnum;
	private Formula degrees;
	private Sprite sprite;

	@Override
	protected void update(float percent) {

		int degreesValue = degrees.interpretInteger(sprite);

		int tmpAngle = degreesValue;
		int direction = 1;
		if (degreesValue < 0) {
			direction = -1;
			tmpAngle = degreesValue + (-2 * degreesValue);
		}


		/*
		 * if (inverse == false) {
		 * LegoNXT.sendBTCMotorMessage(NO_DELAY, motor, 30, angle);
		 * } else {
		 * LegoNXT.sendBTCMotorMessage(NO_DELAY, motor, -30, angle);
		 * }
		 */
	}

	public void setMotorEnum(Motor motorEnum) {
		this.motorEnum = motorEnum;
	}

	public void setDegrees(Formula degrees) {
		this.degrees = degrees;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}