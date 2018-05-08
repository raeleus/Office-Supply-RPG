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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.officesupplyrpg.Core;
import com.ray3k.officesupplyrpg.states.GameState;

public class BattleDialog extends Dialog {
    private Skin skin;
    private Array<TextButton> buttons;
    private int playerHealth;
    private int playerMaxHealth;
    private int playerMagic;
    private int officeSupplyDamage;
    private int healAmount;
    private int hobbyDamage;
    private int enemyHealth;
    private int enemyMaxHealth;
    private int enemyXP;
    private int healCost;
    private int hobbyCost;
    private boolean showStory;

    public BattleDialog(final Class clazz) {
        super("", GameState.inst().getSkin(), "battle");
        skin = GameState.inst().getSkin();
        
        switch (GameState.xpLevel) {
            case 1:
                playerMaxHealth = 25;
                playerMagic = 10;
                officeSupplyDamage = 10;
                hobbyDamage = 0;
                healAmount = 0;
                break;
            case 2:
                playerMaxHealth = 35;
                playerMagic = 15;
                officeSupplyDamage = 12;
                hobbyDamage = 0;
                healAmount = 20;
                break;
            case 3:
                playerMaxHealth = 50;
                playerMagic = 25;
                officeSupplyDamage = 15;
                hobbyDamage = 25;
                healAmount = 30;
                break;
            case 4:
                playerMaxHealth = 80;
                playerMagic = 40;
                officeSupplyDamage = 18;
                hobbyDamage = 35;
                healAmount = 40;
                break;
            case 5:
                playerMaxHealth = 100;
                playerMagic = 60;
                officeSupplyDamage = 21;
                hobbyDamage = 50;
                healAmount = 50;
                break;
        }
        
        playerHealth = playerMaxHealth;
        healCost = 5;
        hobbyCost = 10;
        
        showStory = false;
        
        if (clazz.equals(PaperClipEntity.class)) {
            enemyMaxHealth = 25;
            enemyXP = 20;
        } else if (clazz.equals(RubberBandEntity.class)) {
            enemyMaxHealth = 55;
            enemyXP = 15;
        } else if (clazz.equals(ScissorsEntity.class)) {
            enemyMaxHealth = 40;
            enemyXP = 15;
        } else if (clazz.equals(StaplerEntity.class)) {
            enemyMaxHealth = 25;
            enemyXP = 20;
        } else if (clazz.equals(VillainEntity.class)) {
            enemyMaxHealth = 200;
            enemyXP = 100;
            showStory = true;
        } else {
            enemyMaxHealth = 35;
            enemyXP = 15;
        }
        
        enemyHealth = enemyMaxHealth;
        
        setFillParent(true);
        
        Table table = new Table();
        getContentTable().add(table).expand().top().right().colspan(3);
        
        Label label = new Label("HP: " + enemyHealth, skin, "health");
        label.setName("enemy-health");
        table.add(label).right();
        
        Image image;
        if (clazz.equals(PaperClipEntity.class)) {
            image = new Image(skin, "paper-clip-battle");
        } else if (clazz.equals(RubberBandEntity.class)) {
            image = new Image(skin, "rubber-band-battle");
        } else if (clazz.equals(ScissorsEntity.class)) {
            image = new Image(skin, "scissors-battle");
        } else if (clazz.equals(StaplerEntity.class)) {
            image = new Image(skin, "stapler-battle");
        } else if (clazz.equals(VillainEntity.class)) {
            image = new Image(skin, "villain-battle");
        } else {
            image = new Image(skin, "tape-battle");
        }
        image.setName("enemy");
        table.add(image);
        
        table = getContentTable();
        table.row();
        image = new Image(skin, "player-battle");
        image.setName("player");
        table.add(image).left();
        
        table = new Table();
        getContentTable().add(table).left().expandX();
        
        table.defaults().left();
        label = new Label("HP: " + playerHealth, skin, "health");
        label.setName("player-health");
        table.add(label);
        
        table.row();
        label = new Label("MP: " + playerMagic, skin, "magic");
        label.setName("player-magic");
        table.add(label);
        
        table = new Table();
        table.pad(15.0f);
        getContentTable().add(table).growX();
        
        buttons = new Array<TextButton>();
        table.defaults().minWidth(200.0f).growX().space(25.0f);
        TextButton textButton = new TextButton(GameState.officeSupply, skin);
        textButton.setDisabled(true);
        table.add(textButton);
        buttons.add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                for (TextButton textButton : buttons) {
                    textButton.setDisabled(true);
                }
                
                Image image = BattleDialog.this.findActor("player");
                image.addAction(Actions.sequence(Actions.moveBy(0.0f, 25.0f, .1f), Actions.moveBy(0.0f, -25.0f, .1f)));
                
                if (MathUtils.randomBoolean(.85f)) {
                    image = BattleDialog.this.findActor("enemy");
                    image.addAction(Actions.sequence(Actions.color(Color.RED, .1f), Actions.color(Color.WHITE, .1f)));

                    GameState.inst().playSound("punch");

                    damageEnemy(officeSupplyDamage);

                    GameState.inst().getStage().addAction(Actions.delay(.5f, new Action() {
                        @Override
                        public boolean act(float delta) {
                            showBattleMessage("You swing with " + GameState.officeSupply + " for " + officeSupplyDamage + " Damage.", new Action() {
                                @Override
                                public boolean act(float f) {
                                    generateEnemyAttack(clazz);
                                    return true;
                                }
                            });

                            return true;
                        }
                    }));
                } else {
                    GameState.inst().playSound("confirm");
                    
                    GameState.inst().getStage().addAction(Actions.delay(.5f, new Action() {
                        @Override
                        public boolean act(float delta) {
                            showBattleMessage("You swing with " + GameState.officeSupply + " and missed!", new Action() {
                                @Override
                                public boolean act(float f) {
                                    generateEnemyAttack(clazz);
                                    return true;
                                }
                            });

                            return true;
                        }
                    }));
                }
            }
        });
        
        table.row();
        textButton = new TextButton("Heal (MP Cost " + healCost + ")", skin);
        textButton.setName("heal-button");
        textButton.setDisabled(true);
        if (GameState.xpLevel < 2) textButton.setVisible(false);
        table.add(textButton);
        buttons.add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                for (TextButton textButton : buttons) {
                    textButton.setDisabled(true);
                }
                
                Image image = BattleDialog.this.findActor("player");
                image.addAction(Actions.sequence(Actions.color(Color.BLUE, .25f), Actions.color(Color.WHITE, .25f)));
                
                GameState.inst().playSound("heal");
                
                healPlayer(healAmount);
                
                useMagic(healCost);
                
                GameState.inst().getStage().addAction(Actions.delay(.5f, new Action() {
                    @Override
                    public boolean act(float delta) {
                        showBattleMessage("You heal yourself for " + healAmount + " health.", new Action() {
                            @Override
                            public boolean act(float f) {
                                generateEnemyAttack(clazz);
                                return true;
                            }
                        });

                        return true;
                    }
                }));
            }
        });
        
        table.row();
        textButton = new TextButton(GameState.hobby + " (MP Cost " + hobbyCost + ")", skin);
        textButton.setName("hobby-button");
        textButton.setDisabled(true);
        if (GameState.xpLevel < 3) textButton.setVisible(false);
        table.add(textButton);
        buttons.add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                for (TextButton textButton : buttons) {
                    textButton.setDisabled(true);
                }
                
                Image image = BattleDialog.this.findActor("player");
                image.addAction(Actions.sequence(Actions.moveBy(0.0f, 25.0f, .1f), Actions.moveBy(0.0f, -25.0f, .1f)));
                
                if (MathUtils.randomBoolean(.70f)) {
                    image = BattleDialog.this.findActor("enemy");
                    image.addAction(Actions.sequence(Actions.color(Color.YELLOW, .1f), Actions.color(Color.RED, .1f), Actions.color(Color.PURPLE, .1f), Actions.color(Color.WHITE, .1f)));

                    GameState.inst().playSound("critical");

                    damageEnemy(hobbyDamage);

                    useMagic(hobbyCost);

                    GameState.inst().getStage().addAction(Actions.delay(.5f, new Action() {
                        @Override
                        public boolean act(float delta) {
                            showBattleMessage("You cast " + GameState.hobby + " for " + hobbyDamage + " Damage!", new Action() {
                                @Override
                                public boolean act(float f) {
                                    generateEnemyAttack(clazz);
                                    return true;
                                }
                            });

                            return true;
                        }
                    }));
                } else {
                    GameState.inst().playSound("confirm");
                    
                    GameState.inst().getStage().addAction(Actions.delay(.5f, new Action() {
                        @Override
                        public boolean act(float delta) {
                            showBattleMessage("You cast " + GameState.hobby + " and missed!", new Action() {
                                @Override
                                public boolean act(float f) {
                                    generateEnemyAttack(clazz);
                                    return true;
                                }
                            });

                            return true;
                        }
                    }));
                }
            }
        });
        
        GameState.inst().getStage().addAction(Actions.delay(1.0f, new Action() {
            @Override
            public boolean act(float delta) {
                if (clazz.equals(PaperClipEntity.class)) {
                    showBattleMessage("A bodacious paper clip engages!");
                } else if (clazz.equals(RubberBandEntity.class)) {
                    showBattleMessage("A malicious rubber band attacks!");
                } else if (clazz.equals(ScissorsEntity.class)) {
                    showBattleMessage("A crazed pair of scissors beckons!");
                } else if (clazz.equals(StaplerEntity.class)) {
                    showBattleMessage("A wild stapler approaches!");
                } else if (clazz.equals(VillainEntity.class)) {
                    showBattleMessage(GameState.nemesis + " begins his assault!");
                } else {
                    showBattleMessage("A rabid tape dispenser motions toward you!");
                }
                
                return true;
            }
        }));
    }

    @Override
    protected void result(Object object) {
        super.result(object);
    }
    
    private void showBattleMessage(String message) {
        showBattleMessage(message, null);
    }
    
    private void showBattleMessage(String message, final Action action) {
        for (TextButton textButton : buttons) {
            textButton.setDisabled(true);
        }
        
        Dialog dialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                for (TextButton textButton : buttons) {
                    textButton.setDisabled(false);
                    
                    if (playerMagic < healCost) {
                        textButton = BattleDialog.this.findActor("heal-button");
                        textButton.setDisabled(true);
                    }
                    
                    if (playerMagic < hobbyCost) {
                        textButton = BattleDialog.this.findActor("hobby-button");
                        textButton.setDisabled(true);
                    }
                }
                
                if (action != null) {
                    GameState.inst().getStage().addAction(action);
                }
            }
        };
        
        Label label = new Label(message, skin);
        label.setWrap(true);
        label.setAlignment(Align.center);
        dialog.getContentTable().add(label).growX();
        
        dialog.getContentTable().row();
        label = new Label("Press space...", skin);
        dialog.getContentTable().add(label);
        
        dialog.show(GameState.inst().getStage());
        
        dialog.key(Keys.SPACE, null);
        
        dialog.setSize(400.0f, 200.0f);
        dialog.setPosition(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, Align.center);
    }
    
    private void generateEnemyAttack(Class clazz) {
        if (enemyHealth > 0) {
            int damage = 0;

            if (clazz.equals(PaperClipEntity.class)) {
                if (MathUtils.randomBoolean(.5f)) {
                    damage = 5;
                    showBattleMessage("The paper clip pokes at you for " + damage + " damage!");
                    GameState.inst().playSound("punch");
                } else {
                    showBattleMessage("The paper clip ponders the meaning of life...");
                    GameState.inst().playSound("confirm");
                }
            } else if (clazz.equals(RubberBandEntity.class)) {
                if (MathUtils.randomBoolean(.75f)) {
                    damage = 15;
                    showBattleMessage("The rubber band binds you for " + damage + " damage!");
                    GameState.inst().playSound("paper");
                } else {
                    damage = 25;
                    showBattleMessage("The rubber band snaps at you for " + damage + " damage!");
                    GameState.inst().playSound("rubberband");
                }
            } else if (clazz.equals(ScissorsEntity.class)) {
                if (MathUtils.randomBoolean(.8f)) {
                    damage = 10;
                    showBattleMessage("The scissors slice at you for " + damage + " damage!");
                    GameState.inst().playSound("scissors");
                } else {
                    showBattleMessage("The scissors jump up and down menacingly...");
                    GameState.inst().playSound("confirm");
                }
            } else if (clazz.equals(StaplerEntity.class)) {
                if (MathUtils.randomBoolean(.5f)) {
                    damage = 5;
                    showBattleMessage("The stapler shoots at you for " + damage + " damage!");
                    GameState.inst().playSound("staple");
                } else {
                    showBattleMessage("The stapler looks around confusedly...");
                    GameState.inst().playSound("confirm");
                }
            } else if (clazz.equals(VillainEntity.class)) {
                int choice = MathUtils.random(4);

                switch (choice) {
                    case 0:
                        showBattleMessage(GameState.nemesis + " cackles maniacly...");
                        GameState.inst().playSound("laughter");
                        break;
                    case 1:
                        damage = 30;
                        showBattleMessage(GameState.nemesis + " recites company policy for " + damage + " damage!");
                        GameState.inst().playSound("critical");
                        break;
                    case 2:
                        showBattleMessage(GameState.nemesis + " takes a coffee break!");
                        healEnemy(10);
                        GameState.inst().playSound("confirm");
                        break;
                    case 3:
                        damage = 40;
                        showBattleMessage(GameState.nemesis + " reviews an expense report for " + damage + " damage!");
                        GameState.inst().playSound("critical");
                        break;
                    case 4:
                        damage = 50;
                        showBattleMessage(GameState.nemesis + " lazily reads from a slide show for " + damage + " damage!");
                        GameState.inst().playSound("critical");
                        break;
                    case 5:
                        damage = 20;
                        showBattleMessage(GameState.nemesis + " shakes his newspaper at your for " + damage + " damage!");
                        GameState.inst().playSound("critical");
                        break;
                }
            } else {
                if (MathUtils.randomBoolean(.75f)) {
                    damage = 10;
                    showBattleMessage("The tape binds you for " + damage + " damage!");
                    GameState.inst().playSound("paper");
                } else {
                    damage = 15;
                    showBattleMessage("The tape creates a sticky mess for " + damage + " damage!");
                    GameState.inst().playSound("tape");
                }
            }

            if (!MathUtils.isZero(damage)) {
                Image image = BattleDialog.this.findActor("enemy");
                image.addAction(Actions.sequence(Actions.moveBy(0.0f, -25.0f, .2f), Actions.moveBy(0.0f, 25.0f, .2f)));

                image = BattleDialog.this.findActor("player");
                image.addAction(Actions.sequence(Actions.color(Color.RED, .25f), Actions.color(Color.WHITE, .25f)));
                
                damagePlayer(damage);
                
                if (playerHealth <= 0) {
                    GameState.inst().playSound("game over");
                    showBattleMessage(GameState.name + " has collapsed!", new Action() {
                        @Override
                        public boolean act(float f) {
                            Core.stateManager.loadState("game-over");
                            return true;
                        }
                    });
                }
            }
        } else {
            GameState.inst().playSound("cheer");
            
            final Action action = new Action() {
                @Override
                public boolean act(float f) {
                    BattleDialog.this.hide();
                    if (!showStory) {
                        PlayerEntity player = GameState.entityManager.get(PlayerEntity.class);
                        player.setAllowInput(true);
                    } else {
                        GameState.inst().showStoryDialog(Gdx.files.local(Core.DATA_PATH + "/data/dialog3.txt"));
                    }
                    return true;
                }
            };
            
            GameState.xp += enemyXP;
            GameState.inst().updateXP();
            
            if (GameState.xp >= GameState.nextLevelXP) {
                GameState.nextLevelXP += 100;
                GameState.xpLevel++;
                
                showBattleMessage("You defeated the enemy!\nYou earned " + enemyXP + " XP", new Action() {
                    @Override
                    public boolean act(float f) {
                        GameState.inst().playSound("bonus");
                        GameState.inst().updateXP();
                        
                        switch (GameState.xpLevel) {
                            case 2:
                                showBattleMessage("Level " + GameState.xpLevel + "!\nLearned Heal", action);
                                break;
                            case 3:
                                showBattleMessage("Level " + GameState.xpLevel + "!\nLearned " + GameState.hobby, action);
                                break;
                            default:
                                showBattleMessage("Level " + GameState.xpLevel + "!", action);
                                break;
                        }
                        return true;
                    }
                });
            } else {
                showBattleMessage("You defeated the enemy!\nYou earned " + enemyXP + " XP", action);
            }
        }
    }
    
    private void damageEnemy(int damage) {
        enemyHealth -= damage;
        
        Label label = findActor("enemy-health");
        label.setText("HP: " + enemyHealth);
    }
    
    private void healEnemy(int health) {
        enemyHealth += health;
        if (enemyHealth > enemyMaxHealth) enemyHealth = enemyMaxHealth;
        
        Label label = findActor("enemy-health");
        label.setText("HP: " + enemyHealth);
    }
    
    private void damagePlayer(int damage) {
        playerHealth -= damage;
        
        Label label = findActor("player-health");
        label.setText("HP: " + playerHealth);
    }
    
    private void healPlayer(int health) {
        playerHealth += health;
        if (playerHealth > playerMaxHealth) playerHealth = playerMaxHealth;
        
        Label label = findActor("player-health");
        label.setText("HP: " + playerHealth);
    }
    
    private void useMagic(int magic) {
        playerMagic -= magic;
        
        Label label = findActor("player-magic");
        label.setText("MP: " + playerMagic);
    }
}
