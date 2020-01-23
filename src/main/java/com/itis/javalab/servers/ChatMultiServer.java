package com.itis.javalab.servers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itis.javalab.context.interfaces.AnotherApplicationContext;
import com.itis.javalab.dispatchers.RequestDispatcher;
import com.itis.javalab.dto.interfaces.Dto;
import com.itis.javalab.dto.system.ServiceDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatMultiServer {
    // список клиентов
    public List<ClientHandler> clients;
    private AnotherApplicationContext context;

    public ChatMultiServer(AnotherApplicationContext context) {
        this.context = context;
        clients = new CopyOnWriteArrayList<>();
    }

    public void start(int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        while (true) {
            try {
                // запускаем обработчик сообщений для каждого подключаемого клиента
                new ClientHandler(serverSocket.accept()).start();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public class ClientHandler extends Thread {
        // связь с одним клиентом
        public Socket clientSocket;
        private BufferedReader in;
        private RequestDispatcher dispatcher;
        private ObjectMapper objectMapper;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.dispatcher = new RequestDispatcher(context);
            this.objectMapper = new ObjectMapper();
        }

        public void run() {
            try {
                System.out.println("New user connection");
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
                String inputLine;
                Request request = new Request();
                while ((inputLine = in.readLine()) != null) {
                    request.loadMessage(inputLine);
                    Response response = this.getResponse(request);
                    switch (response.getChatId()) {
                        case 0:
                            sendToCurrent(out, response.getJsonToSend(objectMapper));
                            if (response.header.get("typ").equals("200L")) {
                                clients.add(this);
                            }
                            if (response.header.get("typ").equals("logout")) {
                                this.stopClientConnection();
                            }
                            break;
                        case 1:
                            sendToAll(response.getJsonToSend(objectMapper));
                            break;
                    }
                }
                remove();
                this.stopConnection();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        private void sendToAll(String jsonToSend) {
            for (ClientHandler client : clients) {
                try {
                    PrintWriter out = new PrintWriter(client.clientSocket.getOutputStream(), true);
                    out.println(jsonToSend);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        private void sendToCurrent(PrintWriter out, String jsonToSend) {
            out.println(jsonToSend);
        }

        private Response getResponse(Request request) {
            return prepareResponse(dispatcher.doDispatch(request));
        }

        private Response prepareResponse(Dto result) {
            ServiceDto dto = (ServiceDto) result;
            Response response = new Response();
            response.setChatId(((ServiceDto) result).getChatId());
            switch (dto.getService()) {
                case 1:
                    response.header.put("typ", (String) dto.getParametr("status"));
                    response.header.put("bearer", (String) dto.getParametr("token"));
                    response.payload.put("message", dto.getParametr("message"));
                    break;
                case 2:
                    response.header.put("typ", (String) dto.getParametr("typ"));
                    response.payload.put("message", dto.getParametr("message"));
                    break;
                case 3:
                    response.header.put("typ", (String) dto.getParametr("typ"));
                    response.payload.put("data", dto.getParametr("data"));
                    break;
                case 4:
                    response.header.put("typ", (String) dto.getParametr("typ"));
                    response.payload.put("product", dto.getParametr("product"));
                    response.payload.put("message", dto.getParametr("message"));
            }
            return response;
        }

        private void stopClientConnection() {
            remove();
            try {
                this.stopConnection();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private void remove() {
            if (clients.contains(this)) {
                clients.remove(this);
                System.out.println("Авторизованный клиент завершает подключение");
            }
        }

        private void stopConnection() throws IOException {
            System.out.println("Подключение завершено");
            this.clientSocket.close();
            in.close();
            this.stop();
        }
    }
}
