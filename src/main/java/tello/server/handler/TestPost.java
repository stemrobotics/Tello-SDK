package tello.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class TestPost extends AbstractPostHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {
        Map<String, Object> params = getParamaters(he);

        String response = "";
        // Send response to server
        for (String key : params.keySet()) 
            response += key + " = " + params.get(key) + "\n";

        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
