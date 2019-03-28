package bham.bioshock.server;

import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.interfaces.ServerService;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DevServer extends NanoHTTPD {

  private Store store;
  private ArrayList<ServerService> connecting;
  private ConcurrentHashMap<UUID, ServerService> connected;

  public DevServer() throws IOException {
    super(8000);
    start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    System.out.println("\nDev server running on: http://localhost:8080/ \n");
  }

  @Override
  public Response serve(IHTTPSession session) {
    StringBuilder data = new StringBuilder("{ \"connecting\": [");
    Json j = new Json();
    j.setOutputType(OutputType.json);

    for (ServerService s : connecting) {
      data.append("{" + "\"service\": \"" + j.toJson(s) + "\"},");
    }
    if (connecting.size() > 0) {
      data.deleteCharAt(data.length() - 1);
    }
    data.append("], \"connected\": [");

    for (ServerService s : connected.values()) {
      Player p = store.getPlayer(s.Id().get());
      data.append("{" + "\"player\": " + j.toJson(p) + ",");
      data.append("\"queueSize\":" + s.getSenderQueueSize() + ",");
      data.append("\"num\":" + s.getSenderCounter());
      data.append("},");
      s.resetSenderCounter();
    }
    if (connected.values().size() > 0) {
      data.deleteCharAt(data.length() - 1);
    }
    MinigameStore localStore = store.getMinigameStore();
    String minigameDesc = null;
    if (localStore != null) {
      StringBuilder s = new StringBuilder("{");
      s.append("\"entities\":" + localStore.getEntities().size() + ",");
      s.append("\"staticEntities\":" + localStore.getStaticEntities().size() + ",");
      s.append("\"lastMessages\": {");
      int i = 0;
      for (Entry<UUID, Long> entry : localStore.getLastMessages().entrySet()) {
        String username = store.getPlayer(entry.getKey()).getUsername();
        s.append("\"" + username + "\":" + entry.getValue() + ",");
        i++;
      }
      if (i > 0) {
        s.deleteCharAt(s.length() - 1);
      }
      s.append("}}");
      minigameDesc = s.toString();
    }

    data.append("], \"minigame\":" + minigameDesc + "}");

    Response response = newFixedLengthResponse(data.toString());
    response.addHeader("Access-Control-Allow-Methods", "DELETE, GET, POST, PUT");
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Headers", "X-Requested-With");
    return response;
  }

  public void addServices(
      Store store,
      ArrayList<ServerService> connecting,
      ConcurrentHashMap<UUID, ServerService> connected) {
    this.store = store;
    this.connecting = connecting;
    this.connected = connected;
  }
}
