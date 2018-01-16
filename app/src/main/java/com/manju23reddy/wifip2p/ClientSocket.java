package com.manju23reddy.wifip2p;

import android.provider.Telephony;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by MReddy3 on 1/16/2018.
 */

public class ClientSocket extends WebSocketClient{
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        mCallBacks.onConnectionOpened();
    }

    @Override
    public void onMessage(String s) {
        mCallBacks.onMessageFromServer(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        mCallBacks.onConnectionClosed();
    }

    @Override
    public void onError(Exception e) {

    }

    interface ClientSocketCallbacks{
        public void onMessageFromServer(String message);
        public void onConnectionOpened();
        public void onConnectionClosed();
    }

    ClientSocketCallbacks mCallBacks;

    public ClientSocket(URI serverUri, ClientSocketCallbacks callbacks){
        this(serverUri, new Draft_6455(), callbacks);
    }

    public ClientSocket(URI serverURI, Draft protoDraft, ClientSocketCallbacks callbacks){
        super(serverURI, protoDraft);
        mCallBacks = callbacks;
    }

    public void sendToServer(String message){
        if(isOpen()){
            send(message);
        }
    }
}
