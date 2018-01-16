package com.manju23reddy.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.URI;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mWifiFilter;

    @BindView(R.id.statusTxt)
    TextView mStatus;

    @BindView((R.id.serverBtn))
    Button mServer;

    @BindView(R.id.clientBtn)
    Button mClient;

    @BindView(R.id.roleTxt)
    TextView mRole;

    ServerSocketHelper mServerSoc;

    ClientSocket mClientSoc;

    String mPeerAddr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastRecevier(mManager, mChannel, this);

        mWifiFilter = new IntentFilter();
        mWifiFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mWifiFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mWifiFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mWifiFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager.discoverPeers(mChannel, listener);

        ButterKnife.bind(this);

        mServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClient.setEnabled(false);
                mRole.setText("Server");

                mServerSoc = new ServerSocketHelper(serverCallBacks);

            }
        });

        mClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServer.setEnabled(false);
                mRole.setText("Client");
                try {
                    mClientSoc = new ClientSocket(URI.create(mPeerAddr), clientCallBacks);
                }
                catch (Exception ee){

                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mWifiFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    WifiP2pManager.ActionListener listener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailure(int reason) {

        }
    };

    public void setConnectionStatus(String text){
        mStatus.setText(text);
    }

    public void setPeerAddress(String address){
        mPeerAddr = address;
    }

    ServerSocketHelper.ServerSocketCallBacks serverCallBacks = new ServerSocketHelper.ServerSocketCallBacks() {
        @Override
        public void onClientDataReceived(String message) {
            mStatus.setText(message);
        }

        @Override
        public void onClientOpened() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mServerSoc.sendMessageToClient("Hello from Server ");
                }
            });


        }

        @Override
        public void onClientClosed() {
            mStatus.setText("Connection Closed");
        }
    };

    ClientSocket.ClientSocketCallbacks clientCallBacks = new ClientSocket.ClientSocketCallbacks() {
        @Override
        public void onMessageFromServer(String message) {
            mStatus.setText(message);
        }

        @Override
        public void onConnectionOpened() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mServerSoc.sendMessageToClient("Hello from Server ");
                }
            });
        }

        @Override
        public void onConnectionClosed() {
            mStatus.setText("Connection Closed");
        }
    };
}
