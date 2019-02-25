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
    private static Texture loadingSheet;


    public JoinScreen(Router router, Store store) {
        super(router);
        this.store = store;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        loadingSheet = new Texture(Gdx.files.internal("app/assets/animations/loading_spritesheet.png"));


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

            holder.addPlayerContainer(new PlayerContainer("Player"+(i+1), WaitText.WAITING));

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

        public PlayerContainer(String n, WaitText status) {
            //this.setDebug(true);
            name = new Label(n, skin);
            waitText = new Label(status.toString(), skin);
            animation = new Anim(loadingSheet);

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
        public void changeAnimation() {

        }


        public Anim getAnimation() {
            return animation;
        }
    }

    public class Anim extends Image {

        private float frameDuration = 0.8f;
        private int cols = 26;
        private int rows = 1;

        private int width = 100;
        private int height = 100;

        private Animation<TextureRegion> animation;

        private TextureRegion[] textureRegion;

        public Anim(Texture sheet) {


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
        loadingSheet.dispose();
    }
}
