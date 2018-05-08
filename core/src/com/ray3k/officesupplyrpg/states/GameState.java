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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.officesupplyrpg.Core;
import com.ray3k.officesupplyrpg.EntityManager;
import com.ray3k.officesupplyrpg.InputManager;
import com.ray3k.officesupplyrpg.ScrollingTiledDrawable;
import com.ray3k.officesupplyrpg.State;
import com.ray3k.officesupplyrpg.entities.BattleDialog;
import com.ray3k.officesupplyrpg.entities.CreditsDialog;
import com.ray3k.officesupplyrpg.entities.IntroEntity;
import com.ray3k.officesupplyrpg.entities.OutroEntity;
import com.ray3k.officesupplyrpg.entities.PaperClipEntity;
import com.ray3k.officesupplyrpg.entities.PlayerEntity;
import com.ray3k.officesupplyrpg.entities.RubberBandEntity;
import com.ray3k.officesupplyrpg.entities.ScissorsEntity;
import com.ray3k.officesupplyrpg.entities.SecretaryEntity;
import com.ray3k.officesupplyrpg.entities.StairsEntity;
import com.ray3k.officesupplyrpg.entities.StaplerEntity;
import com.ray3k.officesupplyrpg.entities.StaticEntity;
import com.ray3k.officesupplyrpg.entities.TapeEntity;
import com.ray3k.officesupplyrpg.entities.VillainEntity;

public class GameState extends State {
    private static GameState instance;
    private int score;
    private static int highscore = 0;
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;
    private InputManager inputManager;
    private Skin skin;
    private Stage stage;
    public static EntityManager entityManager;
    public static TextureAtlas spineAtlas;
    public static TwoColorPolygonBatch twoColorPolygonBatch;
    public static final float GAME_WIDTH = 800;
    public static final float GAME_HEIGHT = 600;
    public static String name = "Nesh", officeSupply = "Vorpal Pen", hobby = "Rockin'", nemesis = "Lumbergh";
    public static int xp;
    public static int nextLevelXP;
    public static int xpLevel;
    public ScrollingTiledDrawable scrollingTiledDrawable;
    public Array<Rectangle> rectangles;
    private Polygon tempPolygon;
    private int level;
    
    public static GameState inst() {
        return instance;
    }
    
    public GameState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        instance = this;
        
        spineAtlas = Core.assetManager.get(Core.DATA_PATH + "/spine/Office Supply RPG.atlas", TextureAtlas.class);
        
        score = 0;
        
        inputManager = new InputManager();
        
        gameCamera = new OrthographicCamera();
        gameCamera.position.set(0.0f, 0.0f, 0.0f);
        gameViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, gameCamera);
        gameViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        gameViewport.apply();
        
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/Office Supply RPG.json", Skin.class);
        stage = new Stage(new ScreenViewport());
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputManager);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        entityManager = new EntityManager();
        
        twoColorPolygonBatch = new TwoColorPolygonBatch(3100);
        
        createStageElements();
        
        tempPolygon = new Polygon();
        
        rectangles = new Array<Rectangle>();
        
        scrollingTiledDrawable = new ScrollingTiledDrawable(spineAtlas.findRegion("tile-1"));
        
        xp = 0;
        xpLevel = 1;
        nextLevelXP = 100;
        
        level = 0;
        
        loadNextLevel();
    }
    
    private void createStageElements() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Label label = new Label("XP:" + xp + " Next:" + nextLevelXP, skin);
        label.setName("xp");
        root.add(label).expandY().top().padTop(25.0f);
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(71 / 255.0f, 71 / 255.0f, 71 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        spriteBatch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        spriteBatch.begin();
        spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        scrollingTiledDrawable.draw(spriteBatch, 0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        
        gameCamera.update();
        
        twoColorPolygonBatch.setProjectionMatrix(gameCamera.combined);
        twoColorPolygonBatch.begin();
        twoColorPolygonBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        entityManager.draw(spriteBatch, delta);
        twoColorPolygonBatch.end();
        
        stage.draw();
    }

    @Override
    public void act(float delta) {      
        entityManager.act(delta);
        
        stage.act(delta);
        
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Core.stateManager.loadState("menu");
        }
    }

    @Override
    public void dispose() {
        if (twoColorPolygonBatch != null) {
            twoColorPolygonBatch.dispose();
        }
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, false);
        stage.getViewport().update(width, height, true);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        if (score > highscore) {
            highscore = score;
        }
        
        Label label = stage.getRoot().findActor("score");
        label.setText("Score: " + Integer.toString(this.score));
    }
    
    public void addScore(int score) {
        this.score += score;
        if (this.score > highscore) {
            highscore = this.score;
        }
        
        Label label = stage.getRoot().findActor("score");
        label.setText("Score: " + Integer.toString(this.score));
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public void setGameCamera(OrthographicCamera gameCamera) {
        this.gameCamera = gameCamera;
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getStage() {
        return stage;
    }
    
    public void playSound(String name) {
        playSound(name, 1.0f, 1.0f);
    }
    
    public void playSound (String name, float volume) {
        playSound(name, volume, 1.0f);
    }
    
    /**
     * 
     * @param name
     * @param volume
     * @param pitch .5 to 2. 1 is default
     */
    public void playSound(String name, float volume, float pitch) {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/" + name + ".wav", Sound.class).play(volume, pitch, 0.0f);
    }
    
    public void showStoryDialog(FileHandle file) {
        String output = file.readString();
        output = output.replaceAll("<player>", name);
        output = output.replaceAll("<office supply>", officeSupply);
        output = output.replaceAll("<enemy>", nemesis);
        output = output.replaceAll("<hobby>", hobby);
        showStoryDialog(new Array<String>(output.split("\\n")));
    }
    
    public void showStoryDialog(final Array<String> lines) {
        Dialog dialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                lines.removeIndex(0);
                if (lines.size > 0) {
                    if (lines.first().equals("<battle>")) {
                        playSound("boss");
                        GameState.inst().showBattle(VillainEntity.class);
                    } else if (lines.first().equals("<bonus>")) {
                        playSound("bonus");
                        PlayerEntity player = entityManager.get(PlayerEntity.class);
                        player.setAllowInput(true);
                    } else if (lines.first().equals("<gameover>")) {
                        CreditsDialog creditsDialog = new CreditsDialog();
                        creditsDialog.show(stage);
                    } else {
                        if (lines.first().startsWith("<laugh>")) {
                            lines.set(0, lines.first().replaceAll("<laugh>", ""));
                            playSound("laughter");
                        } else if (lines.first().startsWith("<kill>")) {
                            lines.set(0, lines.first().replaceAll("<kill>", ""));
                            VillainEntity villain = entityManager.get(VillainEntity.class);
                            villain.dispose();
                        }
                        showStoryDialog(lines);
                    }
                } else {
                    PlayerEntity player = entityManager.get(PlayerEntity.class);
                    player.setAllowInput(true);
                }
            }
        };
        
        int align;
        if (lines.first().startsWith("<right>")) {
            lines.set(0, lines.first().replaceAll("<right>", ""));
            align = Align.topRight;
        } else {
            lines.set(0, lines.first().replaceAll("<left>", ""));
            align = Align.bottomLeft;
        }
        
        Label label = new Label(lines.first(), skin);
        label.setWrap(true);
        dialog.getContentTable().add(label).growX();
        
        dialog.getContentTable().row();
        label = new Label("Press Space...", skin);
        dialog.getContentTable().add(label).expand().right().bottom();
        
        dialog.show(stage);
        dialog.setSize(500, 200);
        if (align == Align.topRight) {
            dialog.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), align);
        } else {
            dialog.setPosition(0.0f, 0.0f, align);
        }
        dialog.key(Keys.SPACE, null);
    }
    
    public void showBattle(Class clazz) {
        BattleDialog battleDialog = new BattleDialog(clazz);
        battleDialog.show(stage);
    }
    
    public void loadNextLevel() {
        level++;
        
        loadLevel("Level " + Integer.toString(level));
    }
    
    public void loadLevel(String name) {
        entityManager.clear();
        rectangles.clear();
        
        boolean stationary = false;
        
        if (name.equals("Level 1")) {
            scrollingTiledDrawable = new ScrollingTiledDrawable(spineAtlas.findRegion("tile-1"));
        } else if (name.equals("Level 2")) {
            scrollingTiledDrawable = new ScrollingTiledDrawable(spineAtlas.findRegion("tile-3"));
        } else if (name.equals("Level 3")) {
            scrollingTiledDrawable = new ScrollingTiledDrawable(spineAtlas.findRegion("tile-2"));
        } else if (name.equals("Level 4")) {
            scrollingTiledDrawable = new ScrollingTiledDrawable(spineAtlas.findRegion("tile-1"));
            stationary = true;
        }
        
        SkeletonData skeletonData = Core.assetManager.get(Core.DATA_PATH + "/spine/" + name + ".json", SkeletonData.class);
        Skeleton skeleton = new Skeleton(skeletonData);
        for (Slot slot : skeleton.getSlots()) {
            Attachment attachment = slot.getAttachment();
            if (attachment instanceof RegionAttachment) {
                if (attachment.getName().equals("player")) {
                    PlayerEntity player = new PlayerEntity();
                    player.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entityManager.addEntity(player);
                } else if (attachment.getName().equals("villain")) {
                    VillainEntity entity = new VillainEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("secretary")) {
                    SecretaryEntity entity = new SecretaryEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("paper-clip")) {
                    PaperClipEntity entity = new PaperClipEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entity.setStationary(stationary);
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("rubber-band")) {
                    RubberBandEntity entity = new RubberBandEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entity.setStationary(stationary);
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("scissors")) {
                    ScissorsEntity entity = new ScissorsEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entity.setStationary(stationary);
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("stapler")) {
                    StaplerEntity entity = new StaplerEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entity.setStationary(stationary);
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("tape")) {
                    TapeEntity entity = new TapeEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entity.setStationary(stationary);
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("stairs-exit")) {
                    StairsEntity entity = new StairsEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("intro")) {
                    IntroEntity entity = new IntroEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entityManager.addEntity(entity);
                } else if (attachment.getName().equals("outro")) {
                    OutroEntity entity = new OutroEntity();
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entityManager.addEntity(entity);
                } else {
                    StaticEntity entity = new StaticEntity(attachment.getName());
                    entity.setPosition(((RegionAttachment) attachment).getX(), ((RegionAttachment) attachment).getY());
                    entity.getSkeleton().getRootBone().setRotation(((RegionAttachment) attachment).getRotation());
                    entity.getSkeleton().getRootBone().setScaleX(((RegionAttachment) attachment).getScaleX());
                    entity.getSkeleton().getRootBone().setScaleY(((RegionAttachment) attachment).getScaleY());
                    entityManager.addEntity(entity);
                }
            } else if (attachment instanceof BoundingBoxAttachment) {
                for (int i = 0; i + 1 < ((BoundingBoxAttachment) attachment).getVertices().length; i += 2) {
                    tempPolygon.setVertices(((BoundingBoxAttachment) attachment).getVertices());
                    rectangles.add(new Rectangle(tempPolygon.getBoundingRectangle()));
                }
            }
        }
    }
    
    public void updateXP() {
        Label label = stage.getRoot().findActor("xp");
        label.setText("XP:" + xp + " Next:" + nextLevelXP);
    }
}