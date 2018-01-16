package com.manju23reddy.wifip2p;

import android.os.IInterface;
import android.os.StrictMode;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by MReddy3 on 1/16/2018.
 */

public class ServerSocketHelper extends WebSocketServer {
    public final int PORT = 8009;

    ArrayList<WebSocket>mClients;

    interface ServerSocketCallBacks{
        public void onClientDataReceived(String message);
        public void onClientOpened();
        public void onClientClosed();
    };

    ServerSocketCallBacks mCallBacks = null;

    public ServerSocketHelper(ServerSocketCallBacks callBacks){
        this( new Draft_6455(), callBacks);
    }

    public ServerSocketHelper( Draft draft, ServerSocketCallBacks callBacks){
        super(new InetSocketAddress(8009), Collections.singletonList(draft));
        mCallBacks = callBacks;
        mClients = new ArrayList<>();
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        mCallBacks.onClientDataReceived(s);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        mClients.add(webSocket);
        mCallBacks.onClientOpened();
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        mClients.remove(webSocket);
        mCallBacks.onClientClosed();
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }

    public void sendMessageToClient(String message){
        for(WebSocket client : mClients){
            client.send(message);
        }
    }
}
