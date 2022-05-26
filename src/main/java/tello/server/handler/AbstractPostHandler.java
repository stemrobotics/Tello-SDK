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
     *
     * @param he - The HTTP exchange to get the parameters from.
     * @return - A map of the parameters.
     * @throws IOException
     */
    public JSONObject getParamaters(HttpExchange he) throws IOException {
        try {
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");

            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();

            return parseQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONObject("data: {}");
    }

    /**
     * Turns a post request JSON-String query into a JSONObject
     *
     * @param query - The query you would like to turn in a JSONObject
     * @return - The query in JSON format
     * @throws UnsupportedEncodingException
     */
    public static JSONObject parseQuery(String query) throws UnsupportedEncodingException {

        JSONObject json;

        if (query != null) {
            json = new JSONObject(" { data : " + query + " }");
        } else {
            json = new JSONObject("{ data: {} }");
        }

        return new JSONObject(
            json.get("data").toString()
        );
    }

    /**
     * Sends back a response to client in JSON format.
     * @param he - The Client HttpExchange
     * @param json - The JSONObject response
     * @throws IOException 
     */
    public void sendJSONResponse(HttpExchange he, JSONObject json) throws IOException {
        he.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, ServerConstant.APPLICATION_JSON);
        he.sendResponseHeaders(200, json.toString().length());

        OutputStream os = he.getResponseBody();
        os.write(json.toString().getBytes());
        os.close();
    }

    /**
     * Sends back a response to client in String format
     * @param he - The Client HttpExchange
     * @param json - The JSONObject response being sent as string
     * @throws IOException 
     */
    public void sendStringResponse(HttpExchange he, JSONObject json) throws IOException {
        he.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, ServerConstant.TEXT_PLAIN);
        he.sendResponseHeaders(200, json.toString().length());

        OutputStream os = he.getResponseBody();
        os.write(json.toString().getBytes());
        os.close();
    }

    /**
     * Sends back a response to client in String format
     * @param he - The Client HttpExchange
     * @param response - The String response being sent
     * @throws IOException 
     */
    public void sendStringResponse(HttpExchange he, String response) throws IOException {
        he.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, ServerConstant.TEXT_PLAIN);
        he.sendResponseHeaders(200, response.length());

        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
