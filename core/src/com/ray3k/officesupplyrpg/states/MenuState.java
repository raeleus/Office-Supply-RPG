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
package com.ray3k.officesupplyrpg.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.officesupplyrpg.Core;
import com.ray3k.officesupplyrpg.ScrollingTiledDrawable;
import com.ray3k.officesupplyrpg.State;

public class MenuState extends State {
    private Stage stage;
    private Skin skin;
    private Table root;
    private static final float SCROLL_H_SPEED = 10.0f;
    private static final float SCROLL_V_SPEED = 10.0f;

    public MenuState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/Office Supply RPG.json", Skin.class);
        stage = new Stage(new ScreenViewport());
        
        Gdx.input.setInputProcessor(stage);
        
        createMenu();
    }
    
    private void createMenu() {
        root = new Table();
        root.setBackground(skin.getDrawable("bg-tile-tiled"));
        root.setFillParent(true);
        stage.addActor(root);
        
        root.defaults().space(10.0f);
        Image image = new Image(skin, "logo");
        image.setScaling(Scaling.fit);
        root.add(image);
        
        root.defaults().minWidth(150.0f).minHeight(75.0f);
        root.row();
        TextButton textButton = new TextButton("Play", skin);
        root.add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.assetManager.get(Core.DATA_PATH + "/sfx/confirm.wav", Sound.class).play(1.0f);
                showDialog();
            }
        });
        
        root.row();
        textButton = new TextButton("Quit", skin);
        root.add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }
    
    private void showDialog() {
        Dialog dialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                if (object.equals(Boolean.TRUE)) {
                    showIntroduction();
                }
            }
            
        };
        Table table = dialog.getContentTable();
        Label label = new Label("What is your name?", skin);
        table.add(label);
        
        table.row();
        TextField textField = new TextField(GameState.name, skin);
        textField.setName("firstText");
        table.add(textField).spaceBottom(35.0f);
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextField textField = (TextField) actor;
                GameState.name = textField.getText();
            }
        });
        
        table.row();
        table = dialog.getContentTable();
        label = new Label("What is your favorite hobby?", skin);
        table.add(label);
        
        table.row();
        textField = new TextField(GameState.hobby, skin);
        table.add(textField).spaceBottom(35.0f);
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextField textField = (TextField) actor;
                GameState.hobby = textField.getText();
            }
        });
        
        table.row();
        table = dialog.getContentTable();
        label = new Label("What is the name of an office supply?", skin);
        table.add(label);
        
        table.row();
        textField = new TextField(GameState.officeSupply, skin);
        table.add(textField).spaceBottom(35.0f);
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextField textField = (TextField) actor;
                GameState.officeSupply = textField.getText();
            }
        });
        
        table.row();
        table = dialog.getContentTable();
        label = new Label("What is the name of your archnemesis?", skin);
        table.add(label);
        
        table.row();
        textField = new TextField(GameState.nemesis, skin);
        table.add(textField).spaceBottom(35.0f);
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextField textField = (TextField) actor;
                GameState.nemesis = textField.getText();
            }
        });
        
        dialog.getButtonTable().defaults().minWidth(125.0f);
        dialog.getButtonTable().pad(10.0f);
        dialog.button("OK", true).button("Cancel", false);
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        
        dialog.show(stage);
        
        textField = dialog.findActor("firstText");
        
        stage.setKeyboardFocus(textField);
        textField.selectAll();
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void act(float delta) {
        ScrollingTiledDrawable tile = skin.get("bg-tile-tiled", ScrollingTiledDrawable.class);
        tile.setOffsetX(tile.getOffsetX() + SCROLL_H_SPEED * delta);
        tile.setOffsetY(tile.getOffsetY() + SCROLL_V_SPEED * delta);
        stage.act(delta);
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    private void showIntroduction() {
        final Dialog dialog = new Dialog("", skin, "credits") {
            @Override
            protected void result(Object object) {
                Core.stateManager.loadState("game");
            }
        };
        
        dialog.setFillParent(true);
        
        String text = Gdx.files.local(Core.DATA_PATH + "/data/intro.txt").readString();
        text = text.replaceAll("<player>", GameState.name);
        
        Label label = new Label(text, skin, "story");
        label.setWrap(true);
        dialog.getContentTable().add(label).grow().pad(25.0f);
        
        label.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        label.addAction(Actions.sequence(Actions.fadeIn(5.0f), Actions.delay(5.0f), Actions.fadeOut(5.0f)));
        
        Image image = new Image(skin, "logo");
        dialog.addActor(image);
        
        image.setPosition(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, Align.center);
        image.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        image.addAction(Actions.sequence(Actions.delay(15.0f), Actions.fadeIn(3.0f), Actions.delay(2.0f), Actions.fadeOut(3.0f), new Action() {
            @Override
            public boolean act(float f) {
                Core.stateManager.loadState("game");
                return true;
            }
        }));
        
        dialog.key(Keys.SPACE, null);
        dialog.show(stage);
    }
}