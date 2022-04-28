package tello.server.handler;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONObject;

public class RunMode extends AbstractPostHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        String status = "success";
        try {
            JSONObject params = getParamaters( he );

            String modeID = new JSONObject(
                params.get(
                    "data"
                ).toString()
            ).getString(
                "modeId"
            );

            HandlerManager.getModeManger().runMode( modeID );

        }catch (Exception e) {
            status = "failure";
            e.printStackTrace();
        }

        sendJSONResponse( he, new JSONObject().put( "status", status ));
    }
}


