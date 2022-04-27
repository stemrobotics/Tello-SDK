package tello.server.handler;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONObject;

public class RunMode extends AbstractPostHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        JSONObject params = getParameters( he );
        
    }
}

}
