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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;


public class JoinScreen extends ScreenMaster {
    private Store store;

    /* Container elements */
    Group playersGroup;
    private Holder holder;
    private PlayerContainer[] playerContainers;
    int animationWidth;

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

    public void addStartGameButton() {
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton miniGameButton = new TextButton("TEST Mini Game", skin);
        startButton.setPosition(screen_width - 150, 40);
        miniGameButton.setPosition(screen_width - 150, 0);
        stage.addActor(startButton);
        stage.addActor(miniGameButton);

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
            if(players.size() > i) {
                Player p = players.get(i);
                PlayerContainer pc = holder.getPlayerContainer(i);
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
        private int padding = 20;

        public Holder() {
            //this.setDebug(true);
            this.setFillParent(true);
            this.pad(padding);

            this.setWidth(stage.getWidth());
            this.setHeight(stage.getHeight());

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
        private LoadingAnimation animation;
        private int padding = 20;

        public PlayerContainer(String n, WaitText status) {
            //this.setDebug(true);
            name = new Label(n, skin);
            waitText = new Label(status.toString(), skin);
            animation = new LoadingAnimation(loadingSheet);

            this.pad(padding);

            this.add(name);
            this.row();
            this.add(animation);
            this.row();
            this.add(waitText);

            this.setWidth(stage.getWidth()/4);
            this.setHeight(stage.getHeight()/4);

        }

        public void setName(String n) {
            name.setText(n);
        }
        public void setWaitText(WaitText text) {
            waitText.setText(text.toString());
        }
        public void changeAnimation() {

        }
    }

    public class LoadingAnimation extends Actor {
        private float frameDuration = 0.35f;
        private int cols = 26;
        private int rows = 1;

        private float x = 0;
        private float y = 0;

        private Animation<TextureRegion> animation;

        private TextureRegion[] textureRegion;

        public LoadingAnimation(Texture sheet) {


            textureRegion = new TextureRegion[cols*rows];
            TextureRegion[][] tmp = TextureRegion.split(sheet,sheet.getWidth()/cols, sheet.getHeight()/rows);

            int index = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    textureRegion[index++] = tmp[i][j];
                }
            }

            animation = new Animation<>(frameDuration, textureRegion);
            this.setBounds(x,y, 100,100);
        }

        public void updatePosition() {
            Vector2 pos = getStageLocation(this);
            this.x = pos.x;
            this.y = pos.y;
        }

        public Vector2 getPosition() {
            return getStageLocation(this);
        }

        public void act(float delta) {
            stateTime += delta;
            batch.begin();
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            updatePosition();
            batch.draw(currentFrame, x, y, 100,100);
            batch.end();
        }

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
