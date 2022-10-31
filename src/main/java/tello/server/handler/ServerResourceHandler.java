package tello.server.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import tello.server.constant.ServerConstant;
import tello.server.enums.HttpMethod;
import tello.server.utils.ServerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ServerResourceHandler implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(ServerResourceHandler.class.getName());

    private final String pathToRoot;
    private final boolean gzippable;
    private final boolean cacheable;
    private final Map<String, Resource> resources = new HashMap<>();
    private Handler404 handler404;
    private HandlerManager handlerManager;

    public ServerResourceHandler(String pathToRoot, boolean gzippable, boolean cacheable) throws IOException {
        this.pathToRoot = pathToRoot.endsWith(ServerConstant.FORWARD_SINGLE_SLASH) ? pathToRoot
                : pathToRoot + ServerConstant.FORWARD_SINGLE_SLASH;

        this.gzippable = gzippable;
        this.cacheable = cacheable;

        File[] files = new File(pathToRoot).listFiles();
        if (files == null) {
            throw new IOException("Couldn't find webroot: " + pathToRoot);
        }

        for (File f : files) {
            processFile("", f, gzippable);
        }

        handler404 = new Handler404();

        handlerManager = HandlerManager.getInstance(handler404);

        handlerManager.addHandler(
            "/getModes", 
            new Handler(
                new GetModes(), 
                Arrays.asList(HttpMethod.POST.getName())
            )
        );

        handlerManager.addHandler(
            "/runMode",
            new Handler(
                new RunMode(),
                Arrays.asList(HttpMethod.POST.getName())
            )
        );
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String request = httpExchange.getRequestURI().getPath();

        LOGGER.info("Requested Path: " + request);

        serveResource(httpExchange, request);
    }

    private void processFile(String path, File file, boolean gzippable) throws IOException {
        if (!file.isDirectory()) {
            resources.put(path + file.getName(), new Resource(readResource(new FileInputStream(file), gzippable)));
        }

        if (file.isDirectory()) {
            for (File sub : file.listFiles()) {
                processFile(path + file.getName() + ServerConstant.FORWARD_SINGLE_SLASH, sub, gzippable);
            }
        }
    }

    private byte[] readResource(final InputStream in, final boolean gzip) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        OutputStream gout = gzip ? new GZIPOutputStream(bout) : new DataOutputStream(bout);

        byte[] bs = new byte[4096];
        int r;

        while ((r = in.read(bs)) >= 0) {
            gout.write(bs, 0, r);
        }

        gout.flush();
        gout.close();
        in.close();

        return bout.toByteArray();
    }

    private void serveResource(HttpExchange httpExchange, String requestPath) throws IOException {
        requestPath = requestPath.substring(1);

        requestPath = requestPath.replaceAll(ServerConstant.FORWARD_DOUBLE_SLASH, ServerConstant.FORWARD_SINGLE_SLASH);

        if (requestPath.length() == 0) {
            requestPath = ServerConstant.HOME_FILE_NAME;
            System.out.println(pathToRoot + requestPath);
        }

        serveFile(httpExchange, pathToRoot + requestPath);
    }

    private void serveFile(HttpExchange httpExchange, String resourcePath) throws IOException {
        if (
                !HttpMethod.HEAD.getName().equals(httpExchange.getRequestMethod())
                && 
                !HttpMethod.GET.getName().equals(httpExchange.getRequestMethod())
            ) 
        {
            handlerManager.handle(httpExchange);
        } else {
            File file = new File(resourcePath);

            if (file.exists()) {
                InputStream in = new FileInputStream(resourcePath);

                Resource re = null;

                if (cacheable) {
                    if (resources.get(resourcePath) == null) {
                        re = new Resource(readResource(in, gzippable));
                    } else {
                        re = resources.get(resourcePath);
                    }
                } else {
                    re = new Resource(readResource(in, gzippable));
                }

                if (gzippable) {
                    httpExchange.getResponseHeaders().set(ServerConstant.CONTENT_ENCODING,
                            ServerConstant.ENCODING_GZIP);
                }

                String mimeType = ServerUtil.getFileMime(resourcePath);

                writeOutput(httpExchange, re.content.length, re.content, mimeType);
            } else {
                // Send a 404 response if possible... If not do default 404 message
                handler404.server404(httpExchange, ServerConstant.ERROR_404_FILE);
            }
        }
    }

    private void writeOutput(HttpExchange httpExchange, int contentLength, byte[] content, String contentType)
            throws IOException {
        if (HttpMethod.HEAD.getName().equals(httpExchange.getRequestMethod())) {
            Set<Map.Entry<String, List<String>>> entries = httpExchange.getRequestHeaders().entrySet();

            String response = "";

            for (Map.Entry<String, List<String>> entry : entries) {
                response += entry.toString() + "\n";
            }

            httpExchange.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, ServerConstant.TEXT_PLAIN);
            httpExchange.sendResponseHeaders(200, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.getResponseBody().close();
        } else {
            httpExchange.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, contentType);
            httpExchange.sendResponseHeaders(200, contentLength);
            httpExchange.getResponseBody().write(content);
            httpExchange.getResponseBody().close();
        }
    }

    public class Handler404 {
        public void server404(HttpExchange httpExchange, String resourcePath) throws IOException {
            File file = new File(resourcePath);

            if (file.exists()) {
                InputStream in = new FileInputStream(resourcePath);

                Resource re = null;

                if (cacheable) {
                    if (resources.get(resourcePath) == null) {
                        re = new Resource(readResource(in, gzippable));
                    } else {
                        re = resources.get(resourcePath);
                    }
                } else {
                    re = new Resource(readResource(in, gzippable));
                }

                if (gzippable) {
                    httpExchange.getResponseHeaders().set(ServerConstant.CONTENT_ENCODING,
                            ServerConstant.ENCODING_GZIP);
                }

                String mimeType = ServerUtil.getFileMime(resourcePath);

                writeOutput(httpExchange, re.content.length, re.content, mimeType);
            } else {
                LOGGER.severe("Couldn't find error 404 file: " + ServerConstant.ERROR_404_FILE);
                showError(httpExchange, 404, ServerConstant.ERROR_404_FILE_MESSAGE);
            }
        }

        private void showError(HttpExchange httpExchange, int respCode, String errDesc) throws IOException {
            String message = "HTTP error " + respCode + ": " + errDesc;
            byte[] messageBytes = message.getBytes(ServerConstant.ENCODING_UTF8);

            httpExchange.getResponseHeaders().set(ServerConstant.CONTENT_TYPE, ServerConstant.TEXT_PLAIN);
            httpExchange.sendResponseHeaders(respCode, messageBytes.length);

            OutputStream os = httpExchange.getResponseBody();
            os.write(messageBytes);
            os.close();
        }
    }
}
