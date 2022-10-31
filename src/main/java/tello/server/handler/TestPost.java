package tello.server.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONObject;


public class TestPost extends AbstractPostHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        JSONObject params = getParamaters(he);

        sendJSONResponse(he, params);
    }
}
