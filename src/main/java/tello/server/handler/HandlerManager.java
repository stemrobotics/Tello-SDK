package tello.server.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import tello.server.constant.ServerConstant;
import tello.server.handler.ServerResourceHandler.Handler404;

public class HandlerManager implements HttpHandler {
    private Map<String, Object> handlers = new HashMap<String, Object>();

    private Handler404 handler404;

    public HandlerManager(Handler404 handler404) {
        this.handler404 = handler404;
    }

    public void addHandler(String path, List<Object> methods, Object handler) {
        Map<String, Object> handlerMap = new HashMap<String, Object>();

        handlerMap.put("methods", methods);
        handlerMap.put("handler", handler);

        handlers.put(path, handlerMap);
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        if (handlers.containsKey( getPath(he) ) ) {
            Map<String, Object> handlerMap = (Map<String, Object>) handlers.get( getPath(he) );

            if (handlerMap.containsKey("methods")) {
                List<Object> methods = (List<Object>) handlerMap.get("methods");

                if (methods.contains( getMethod(he) )) {
                    Object handler = handlerMap.get("handler");

                    if (handler instanceof HttpHandler) {
                        ((HttpHandler) handler).server404(he, ServerConstant.Error404File);
                    } else if (handler instanceof Handler404) {
                        ((Handler404) handler).server404(he, ServerConstant.Error404File);
                    }
                } else {
                    handler404.server404(he, ServerConstant.Error404File);
                }
            } else {
                handler404.server404(he, ServerConstant.Error404File);
            }
        } else {
            handler404.server404(he, ServerConstant.Error404File);
        }
    }

    public String getPath(HttpExchange he) {
        return he.getRequestURI().getPath();
    }

    public String getMethod(HttpExchange he) {
        return he.getRequestMethod();
    }
}
