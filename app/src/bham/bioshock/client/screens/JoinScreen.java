package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;


public class JoinScreen extends ScreenMaster {
    private Store store;

    /* Container elements */
    private Holder holder;

    /* Animation Elements */
    private float stateTime = 0;
    private Texture[] loadTextures;
    private Texture[] connectedTextures;


    public JoinScreen(Router router, Store store) {
        super(router);
        this.store = store;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        loadTextures = new Texture[store.MAX_PLAYERS];
        loadTextures[0] = new Texture(Gdx.files.internal("app/assets/animations/loading1.png"));
        loadTextures[1] = new Texture(Gdx.files.internal("app/assets/animations/loading2.png"));
        loadTextures[2] = new Texture(Gdx.files.internal("app/assets/animations/loading3.png"));
        loadTextures[3] = new Texture(Gdx.files.internal("app/assets/animations/loading4.png"));

        connectedTextures = new Texture[store.MAX_PLAYERS];
        connectedTextures[0] = new Texture(Gdx.files.internal("app/assets/animations/connectedAnimationSheet1.png"));
        connectedTextures[1] = new Texture(Gdx.files.internal("app/assets/animations/connectedAnimationSheet2.png"));
        connectedTextures[2] = new Texture(Gdx.files.internal("app/assets/animations/connectedAnimationSheet3.png"));
        connectedTextures[3] = new Texture(Gdx.files.internal("app/assets/animations/connectedAnimationSheet4.png"));


        setUpHolder();
        setUpPlayerContainers();
        addStartGameButton();

    }



    private void setUpHolder() {
        holder = new Holder();
        stage.addActor(holder);
    }

    private void setUpPlayerContainers() {

        for(int i=0; i<store.MAX_PLAYERS; i++) {

            holder.addPlayerContainer(new PlayerContainer("Player"+(i+1), WaitText.WAITING, loadTextures[i]));

            if(i%2 != 0){
                holder.row();
            }
        }
    }


    /* CREATE */
    @Override
    public void show() {
        super.show();
    }

    /* RENDER */
    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        ArrayList<Player> players = store.getPlayers();

        for(int i=0; i<store.MAX_PLAYERS; i++) {
            PlayerContainer pc = holder.getPlayerContainer(i);
            if(players.size() > i) {
                Player p = players.get(i);
                pc.setName(p.getUsername());
                pc.setWaitText(WaitText.CONNECTED);
                pc.changeAnimation(connectedTextures[i], 7, 1, 1.2f);
            }

        }


    }

    public enum WaitText {
        WAITING("Waiting..."), CONNECTED("Connected");
        final String text;
        WaitText(String text) {
            this.text = text;
        }
    }


    public class Holder extends Table {
        private ArrayList<PlayerContainer> playerContainers;
        private int padding = 100;

        public Holder() {
            //this.setDebug(true);
            this.setFillParent(true);

            this.pad(padding);

            playerContainers = new ArrayList<>();
        }

        public void addPlayerContainer(PlayerContainer pc) {
            playerContainers.add(pc);
            this.add(pc);
        }
        public PlayerContainer getPlayerContainer(int i) {
            if(playerContainers.size() >= i) {
                return playerContainers.get(i);
            }
            else {
                return null;
            }
        }
    }

    public class PlayerContainer extends Table {

        private Label name;
        private Label waitText;
        private Anim animation;
        private int padding = 20;

        public PlayerContainer(String n, WaitText status, Texture sheet) {
            //this.setDebug(true);
            name = new Label(n, skin);
            waitText = new Label(status.toString(), skin);
            animation = new Anim(sheet, 26, 1, 100,100, 0.8f);

            this.pad(padding);

            this.add(name);
            this.row();
            this.add(animation).height(animation.getHeight()).width(animation.getWidth());
            this.row();
            this.add(waitText);


        }

        public void setName(String n) {
            name.setText(n);
        }
        public void setWaitText(WaitText text) {
            waitText.setText(text.toString());
        }
        public void changeAnimation(Texture sheet, int cols, int rows, float frameDuration) {
            Anim newAnimation = new Anim(sheet, cols, rows, 100, 120, frameDuration);
            this.animation = newAnimation;
        }

        public Anim getAnimation() {
            return animation;
        }

        @Override
        public void act(float delta) {
            clearChildren();
            this.add(name);
            this.row();
            this.add(animation).height(animation.getHeight()).width(animation.getWidth());
            this.row();
            this.add(waitText);

            super.act(delta);
        }
    }

    public class Anim extends Image {

        private float frameDuration = 0.8f;
        private int cols;
        private int rows;

        private int width = 100;
        private int height = 100;

        private Animation<TextureRegion> animation;

        private TextureRegion[] textureRegion;

        public Anim(Texture sheet, int cols, int rows, int width, int height, float frameDuration) {

            this.cols = cols;
            this.rows = rows;

            this.width = width;
            this.height = height;

            this.frameDuration = frameDuration;

            textureRegion = new TextureRegion[cols*rows];
            TextureRegion[][] tmp = TextureRegion.split(sheet,sheet.getWidth()/cols, sheet.getHeight()/rows);

            int index = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    textureRegion[index++] = tmp[i][j];
                }
            }

            animation = new Animation<>(frameDuration, textureRegion);

            setWidth(width);
            setHeight(height);
        }


        public void act(float delta) {
            stateTime += delta;
            TextureRegion currentFrame = animation.getKeyFrame(stateTime+=delta, true);
            TextureRegionDrawable drawable = new TextureRegionDrawable(currentFrame);
            this.setDrawable(drawable);
            super.act(delta);
        }


    }

    public class ButtonsContainer extends Table {
        ButtonsContainer() {
            bottom();
            right();
            setWidth(stage.getWidth());
            pad(20);

            TextButton startButton = new TextButton("Start Game", skin);
            TextButton miniGameButton = new TextButton("TEST Mini Game", skin);
            add(startButton);
            row();
            add(miniGameButton);

            startButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    router.call(Route.STOP_MAIN_MUSIC);
                    router.call(Route.START_GAME);
                }
            });

            miniGameButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    router.call(Route.SEND_MINIGAME_START);
                }
            });
        }

        @Override
        public void act(float delta) {
            setWidth(stage.getWidth());
            super.act(delta);
        }


    }

    public void addStartGameButton() {
        ButtonsContainer buttons = new ButtonsContainer();
        stage.addActor(buttons);
    }

    @Override
    protected void setPrevious() {
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                router.call(Route.DISCONNECT_PLAYER);
                store.removeAllPlayers();
                router.back();
            }
        });
    }

    public static Vector2 getStageLocation(Actor actor) {
        return actor.localToStageCoordinates(new Vector2(0, 0));
    }

    @Override
    public void dispose() {
        batch.dispose();
        for(int i = 0; i <loadTextures.length; i++)
        loadTextures[i].dispose();
    }
}
