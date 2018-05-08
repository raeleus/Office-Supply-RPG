/*
 * The MIT License
 *
 * Copyright 2018 .
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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.ray3k.officesupplyrpg.Core;
import com.ray3k.officesupplyrpg.states.GameState;

/**
 *
 * @author Raymond
 */
public class CreditsDialog extends Dialog {
    private Skin skin;

    public CreditsDialog() {
        super("", GameState.inst().getSkin(), "credits");
        
        skin = GameState.inst().getSkin();
        setFillParent(true);
        
        Image image = new Image(skin, "continued");
        addActor(image);
        image.setPosition(Gdx.graphics.getWidth() / 2.0f, 0.0f, Align.top);
        image.addAction(Actions.moveBy(0.0f, Gdx.graphics.getHeight() + 150.0f, 15.0f));
        
        Label label = new Label(GameState.name + " will return in...", skin, "story");
        addActor(label);
        label.setPosition(Gdx.graphics.getWidth() / 2.0f, 0.0f, Align.top);
        label.addAction(Actions.delay(3.0f, Actions.moveBy(0.0f, Gdx.graphics.getHeight() + 150.0f, 15.0f)));
        
        image = new Image(skin, "logo2");
        addActor(image);
        image.setPosition(Gdx.graphics.getWidth() / 2.0f, 0.0f, Align.top);
        image.addAction(Actions.sequence(Actions.delay(7.0f), Actions.moveBy(0.0f, Gdx.graphics.getHeight() / 2.0f, 7.5f), Actions.fadeOut(5.0f), new Action() {
            @Override
            public boolean act(float f) {
                Core.stateManager.loadState("menu");
                return true;
            }
        }));
    }
}
