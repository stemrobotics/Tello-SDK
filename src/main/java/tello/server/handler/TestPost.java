package tello.server.handler;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONObject;

import tello.server.constant.ServerConstant;

public class TestPost extends AbstractPostHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        JSONObject params = getParamaters(he);

        sendJSONResponse(he, params);
    }
}
