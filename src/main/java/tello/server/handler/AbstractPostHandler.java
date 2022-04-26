package tello.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import tello.server.constant.ServerConstant;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class AbstractPostHandler implements HttpHandler {

    /**
     * Uses the HttpExchange to get the parameters from the request.
     * @param he - The http exchange to get the parameters from.
     * @return - A map of the parameters.
     * @throws IOException
     */
    public JSONObject getParamaters(HttpExchange he) {
        try {
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");

            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            
            return parseQuery(query);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONObject("data: {}");
    }

    public static JSONObject parseQuery(String query) throws UnsupportedEncodingException {
        if (query != null) {
            return new JSONObject(" { data : " + query + " }");
        }else {
            return new JSONObject("{ data: {} }");
        }
    }

    public void sendJSONResponse(HttpExchange he, JSONObject json) throws IOException {
        he.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, ServerConstant.APPLICATION_JSON);
        he.sendResponseHeaders(200, json.toString().length());

        OutputStream os = he.getResponseBody();
        os.write(json.toString().getBytes());
        os.close();
    }

    public void sendStringResponse(HttpExchange he, JSONObject json) throws IOException {
        he.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, ServerConstant.TEXT_PLAIN);
        he.sendResponseHeaders(200, json.toString().length());

        OutputStream os = he.getResponseBody();
        os.write(json.toString().getBytes());
        os.close();
    }

    public void sendStringResponse(HttpExchange he, String response) throws IOException {
        he.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, ServerConstant.TEXT_PLAIN);
        he.sendResponseHeaders(200, response.length());

        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}