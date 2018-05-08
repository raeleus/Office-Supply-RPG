/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ray3k.officesupplyrpg.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.officesupplyrpg.SpineTwoColorEntity;
import com.ray3k.officesupplyrpg.states.GameState;

public abstract class EnemyEntity extends SpineTwoColorEntity {
    private static final float MOVE_SPEED = 220.0f;
    private static Vector2 temp1 = new Vector2();
    private static Vector2 temp2 = new Vector2();
    private static Polygon poly = new Polygon();
    private boolean stationary;

    public EnemyEntity(String skeletonDataPath, String animation,
            TwoColorPolygonBatch twoColorPolygonBatch) {
        super(skeletonDataPath, animation, twoColorPolygonBatch);
    }

    @Override
    public void actSub(float delta) {
        if (!stationary) {
            PlayerEntity player = GameState.entityManager.get(PlayerEntity.class);

            if (player != null && player.isAllowInput()) {
                boolean lineOfSight = true;

                temp1.x = getX();
                temp1.y = getY();
                temp2.x = player.getX();
                temp2.y = player.getY();

                for (Rectangle rect : GameState.inst().rectangles) {
                    poly.setVertices(new float[] {rect.x, rect.y, rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height, rect.x + rect.width, rect.y});
                    if (Intersector.intersectSegmentPolygon(temp1, temp2, poly)) {
                        lineOfSight = false;
                        break;
                    }
                }

                if (lineOfSight) {
                    if (moveTowards(player.getX(), player.getY(), MOVE_SPEED * delta)) {
                        GameState.inst().playSound("enemy");
                        player.setAllowInput(false);
                        player.setMotion(0.0f, 0.0f);
                        player.getAnimationState().setAnimation(0, "stand", false);
                        hitPlayer();
                        dispose();
                        
                        GameState.inst().showBattle(getClass());
                    }
                }
            }
        }
    }

    public abstract void hitPlayer();
    
    public boolean isStationary() {
        return stationary;
    }

    public void setStationary(boolean stationary) {
        this.stationary = stationary;
    }
}
