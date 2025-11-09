package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Main {


    public static void main(String[] args) throws IOException {
        System.out.println("Http Server Started!");

        var serverSocket = new ServerSocket(8080);

        while (true) {
            Socket connection = serverSocket.accept(); // Accept incoming connections
            var httpRequest = readRequest(connection);

            System.out.println("We have a request at " + java.time.LocalDateTime.now() + "... " + httpRequest);

            try (var os = connection.getOutputStream()) {
                var body = """
                        {
                            "id": "1",
                            "msg": "Hello World"
                        }
                        """;
                var response = """
                        HTTP/1.1 200 OK
                        Content-Type:application/json
                        Content-Length: %d
                        
                        %s""".formatted(body.getBytes(StandardCharsets.UTF_8).length, body);
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }


        }

    }

    private static HttpReq readRequest(Socket conn) throws IOException {

        var r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        var line = r.readLine();
//        System.out.println("Read line = " + line);
        if (line == null) {
            return null;
        }
        var methodUrl = line.split(" ");
        var method = methodUrl[0];
        String url = methodUrl[1];
        System.out.println("Read line = " + line);
        return new HttpReq(method, url, Map.of(), null);
    }

    private record HttpReq(String method,
                           String url,
                           Map<String, List<String>> headers,
                           byte[] body) {

    }
}