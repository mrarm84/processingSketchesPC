package processing2js;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jonathan
 */
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.HeadlessException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;

public class JDWebbServer {

    private static JDWebbServer instance = null;

    private static int port = 8005;
    private HttpServer server = null;
    ExecutorService httpThreadPool;

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        JDWebbServer.port = port;
    }

    public void changePortAndRestart(int portNr) {
        port = portNr;
        stop();
        start();
    }

    public static JDWebbServer getInstance() {
        if (instance == null) {
            instance = new JDWebbServer();
        }
        return instance;
    }

    String sketchDir;

    public static void main(String[] args) {
        JDWebbServer jdws = JDWebbServer.getInstance();
        jdws.setSketchDir("C:\\googledrive\\processing\\ass\\repslumplinjerb");
    }

    public void setSketchDir(String sketchDir) {
        this.sketchDir = sketchDir;
    }

    private JDWebbServer() {
        start();
    }

    public void start() throws HeadlessException {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new MyHandler());
            server.setExecutor(null); // creates a default executor
            httpThreadPool = Executors.newFixedThreadPool(5);
            server.setExecutor(this.httpThreadPool);

            server.start();
        } catch (IOException iOException) {
            JOptionPane.showMessageDialog(null, "Error when trying to start webserver:\n" + iOException.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(1);
            httpThreadPool.shutdownNow();
        }
    }

    class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            //String localAdress = t.getLocalAddress().toString();
            //System.out.println("handle localAdress = " + localAdress);
            //System.out.println("handle getRemoteAddress = " + t.getRemoteAddress());
            //System.out.println("handle getRequestURI = " + t.getRequestURI());

            String relPath = t.getRequestURI().toString();
            if (relPath.equals("/")) {
                relPath += "index.html";
            }

            final String path = sketchDir + relPath;
            //System.out.println("handle path = " + path);
            //byte[] response = "This is the response..".getBytes();
            byte[] response = Files.readAllBytes(Paths.get(path));
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }

}
