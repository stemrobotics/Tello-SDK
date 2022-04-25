package tello.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class AbstractPostHandler implements HttpHandler {

    /**
     * Uses the HttpExchange to get the parameters from the request.
     * @param he - The http exchange to get the parameters from.
     * @return - A map of the parameters.
     * @throws IOException
     */
    public Map<String, Object> getParamaters(HttpExchange he) {
        // Parse request parameter
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");

            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            
            parseQuery(query, params);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }

    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");

                String key = null;
                String value = null;

                if (param.length > 0) {
                    key =  URLDecoder.decode(
                        param[0], 
                        System.getProperty("file.encoding")
                    );
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);

                    if (obj instanceof List<?>) {
                        List<String> values =  (List<String>) obj;
                        values.add(value);
                    }else if (obj instanceof String) {
                        List<String> values =  new ArrayList<String>();
                        values.add( (String) obj );  
                        values.add(value);
                        parameters.put(key, values);
                    }
                }else {
                    parameters.put(key, value);
                }
            }
        }
    }
}