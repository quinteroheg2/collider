/*
 * Copyright 2013-2014 Matthew D. Michelotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.matthewmichelotti.collider.demos.comps;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.PosAndVel;
import com.matthewmichelotti.collider.demos.WaveUpdater;

/**
 * A sticky circle that resizes its diameter in a wave-like fashion.
 * Circle will stop moving when it encounters another CMorphSticky or CTarget.
 * @author Matthew Michelotti
 */
public class CMorphSticky extends Component {
	private double stuckTime = -1;
	
	public CMorphSticky(PosAndVel pos) {
		super(Game.engine.makeCircle());
		final HBCircle circ = circ();
		circ.setPos(pos.x, pos.y);
		circ.setVel(pos.vx, pos.vy);
		circ.commit(Double.POSITIVE_INFINITY);
		new WaveUpdater(CMorphBullet.DIAM, -34, 1) {
			@Override protected boolean isValid() {return !isDeleted() && stuckTime < 0.0;}
			@Override protected void update(double value, double vel, double endTime) {
				circ.setDiam(value);
				circ.setVelDiam(vel);
				circ.commit(endTime);
			}
		};
		if(!isInBounds()) throw new RuntimeException();
	}

	@Override public boolean canInteract(Component o) {
		return o instanceof CTarget || o instanceof CBounds || o instanceof CMorphSticky;
	}
	@Override public boolean interactsWithBullets() {return false;}

	@Override
	public void onCollide(Component other) {
		if(other instanceof CBounds) return;
		double time = Game.engine.getTime();
		if(stuckTime >= 0.0 && stuckTime < time) return;
		if(stuckTime < 0.0) stuckTime = time;
		if(stuckTime == time && hitBox().getOverlap(other.hitBox()) > .1) {
			delete();
		}
		else {
			hitBox().setVel(0.0, 0.0);
			circ().setVelDiam(0.0);
			hitBox().commit(Double.POSITIVE_INFINITY);
		}
		if(other instanceof CTarget) ((CTarget)other).hit();
	}

	@Override public void onSeparate(Component other) {
		if(other instanceof CBounds) delete();
	}

	@Override public Color getColor() {return CSticky.COLOR;}
}
