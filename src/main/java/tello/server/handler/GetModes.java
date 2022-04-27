package tello.server.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class GetModes extends AbstractPostHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        sendJSONResponse(he, HandlerManager.getModeManger().getModeList());
    }
}
