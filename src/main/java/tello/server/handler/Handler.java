package tello.server.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import tello.server.enums.HttpMethod;

public class Handler {
    private List<String> methods = new ArrayList<String>();
    private HttpHandler handler;

    public Handler(Object handler, List<String> method) {
        this.handler = (HttpHandler) handler;

        for (String met : method) this.methods.add(met);
    }

    /**
     * @param method -  The HTTP method to check.
     * @return if thehandler can handle the request method type
     */
    public boolean isMethod(String method) {
        return methods.contains(method);
    }
    
    public boolean isGet() {
        return methods.contains(HttpMethod.GET.getName());
    }

    public boolean isPost() {
        return methods.contains(HttpMethod.POST.getName());
    }

    public boolean isPut() {
        return methods.contains(HttpMethod.PUT.getName());
    }

    public boolean isDelete() {
        return methods.contains(HttpMethod.DELETE.getName());
    }

    /**
     * This method is called when the server receives a request.
     * @param he - The HTTP exchange to handle.
     * @throws IOException
     */
    public void handle(HttpExchange he) throws IOException {
        handler.handle(he);
    }
}
