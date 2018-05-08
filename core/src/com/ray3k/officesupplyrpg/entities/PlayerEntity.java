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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ray3k.officesupplyrpg.Core;
import com.ray3k.officesupplyrpg.Entity;
import com.ray3k.officesupplyrpg.SpineTwoColorEntity;
import com.ray3k.officesupplyrpg.states.GameState;

public class PlayerEntity extends SpineTwoColorEntity {
    private static final float WALK_SPEED = 200.0f;
    private boolean allowInput;
    private static Vector2 temp = new Vector2();
    
    public PlayerEntity() {
        super(Core.DATA_PATH + "/spine/player.json", "stand", GameState.twoColorPolygonBatch);
        getSkeleton().setSkin("player");
        getSkeleton().getRootBone().setRotation(90.0f);
        
        allowInput = true;
    }

    @Override
    public void actSub(float delta) {
        if (allowInput) {
            actControls(delta);

            actPhysics(delta);

            actCamera(delta);

            actCollision(delta);
        }
    }

    public void actControls(float delta) {
        float direction = 0.0f;
        float speed = 0.0f;
        
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            speed = WALK_SPEED;
            direction = 180.0f;
            
            if (Gdx.input.isKeyPressed(Keys.UP)) {
                speed = WALK_SPEED;
                direction = 135.0f;
            } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                speed = WALK_SPEED;
                direction = 225.0f;
            }
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            speed = WALK_SPEED;
            direction = 0.0f;
            
            if (Gdx.input.isKeyPressed(Keys.UP)) {
                speed = WALK_SPEED;
                direction = 45.0f;
            } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                speed = WALK_SPEED;
                direction = 315.0f;
            }
        } else if (Gdx.input.isKeyPressed(Keys.UP)) {
            speed = WALK_SPEED;
            direction = 90.0f;
        } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            speed = WALK_SPEED;
            direction = 270.0f;
        }
        
        setMotion(speed, direction);
        if (!MathUtils.isZero(speed)) {
            if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("walk")) {
                getAnimationState().setAnimation(0, "walk", true);
            }
            getSkeleton().getRootBone().setRotation(direction);
        } else {
            if (!getAnimationState().getCurrent(0).getAnimation().getName().equals("stand")) {
                getAnimationState().setAnimation(0, "stand", true);
            }
        }
    }
    
    private void actPhysics(float delta) {
        for (Rectangle rectangle : GameState.inst().rectangles) {
            if (rectangle.contains(getX() + getXspeed() * delta, getY() + getYspeed() * delta)) {
                if (rectangle.contains(getX() + getXspeed() * delta, getY())) {
                    setXspeed(0.0f);
                } else if (rectangle.contains(getX(), getY() + getYspeed() * delta)) {
                    setYspeed(0.0f);
                } else {
                    setMotion(0.0f, 0.0f);
                }
                break;
            }
        }
    }
    
    private void actCamera(float delta) {
        GameState.inst().getGameCamera().position.x = getX();
        GameState.inst().getGameCamera().position.y = getY();
        GameState.inst().scrollingTiledDrawable.setOffsetX(GameState.inst().scrollingTiledDrawable.getOffsetX() - getXspeed() * delta);
        GameState.inst().scrollingTiledDrawable.setOffsetY(GameState.inst().scrollingTiledDrawable.getOffsetY() - getYspeed() * delta);
    }
    
    private void actCollision(float delta) {
        StairsEntity stairs = GameState.entityManager.get(StairsEntity.class);
        
        if (stairs != null && stairs.getSkeletonBounds().aabbContainsPoint(getX(), getY())) {
            GameState.inst().loadNextLevel();
        }
        
        VillainEntity villain = GameState.entityManager.get(VillainEntity.class);
        if (villain != null) {
            temp.x = villain.getX();
            temp.y = villain.getY();

            temp.sub(getX(), getY());
            if (temp.len() < 100.0f) {
                allowInput = false;
                setMotion(0.0f, 0.0f);
                getAnimationState().setAnimation(0, "stand", true);
                
                GameState.inst().showStoryDialog(Gdx.files.local(Core.DATA_PATH + "/data/dialog2.txt"));
            }
        }
        
        IntroEntity intro = GameState.entityManager.get(IntroEntity.class);
        if (intro != null) {
            temp.x = intro.getX();
            temp.y = intro.getY();

            temp.sub(getX(), getY());
            if (temp.len() < 100.0f) {
                allowInput = false;
                setMotion(0.0f, 0.0f);
                getAnimationState().setAnimation(0, "stand", true);
                intro.dispose();
                
                GameState.inst().showStoryDialog(Gdx.files.local(Core.DATA_PATH + "/data/dialog1.txt"));
            }
        }
        
        OutroEntity outro = GameState.entityManager.get(OutroEntity.class);
        if (outro != null) {
            temp.x = outro.getX();
            temp.y = outro.getY();

            temp.sub(getX(), getY());
            if (temp.len() < 100.0f) {
                allowInput = false;
                setMotion(0.0f, 0.0f);
                getAnimationState().setAnimation(0, "stand", true);
                outro.dispose();
                
                GameState.inst().showStoryDialog(Gdx.files.local(Core.DATA_PATH + "/data/dialog4.txt"));
            }
        }
    }
    
    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

    public boolean isAllowInput() {
        return allowInput;
    }

    public void setAllowInput(boolean allowInput) {
        this.allowInput = allowInput;
    }
}
