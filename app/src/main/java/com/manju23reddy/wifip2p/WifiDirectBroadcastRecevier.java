package com.manju23reddy.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.util.Iterator;

/**
 * Created by MReddy3 on 1/16/2018.
 */

public class WifiDirectBroadcastRecevier extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChnnel;
    private MainActivity mActivity;

    public WifiDirectBroadcastRecevier(WifiP2pManager p2pManager, WifiP2pManager.Channel channel,
                                       MainActivity activity){

        super();
        mActivity = activity;
        mChnnel = channel;
        mManager = p2pManager;


    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        String action = intent.getAction();
        switch (action){
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                    //enabled
                }
                else{
                    //not enabled
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if (null != mManager){
                    mManager.requestPeers(mChnnel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            Iterator<WifiP2pDevice> iterator = peers.getDeviceList().iterator();
                            WifiP2pDevice device;
                            while(iterator.hasNext()){
                                device = iterator.next();
                                final WifiP2pConfig config = new WifiP2pConfig();
                                config.deviceAddress = device.deviceAddress;
                                mManager.connect(mChnnel, config, new WifiP2pManager.ActionListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(mActivity, "Connected to WifiP2P Device", Toast.LENGTH_LONG).show();
                                        mActivity.setConnectionStatus("Connected to "+config.deviceAddress);
                                        mActivity.setPeerAddress(config.deviceAddress);
                                    }

                                    @Override
                                    public void onFailure(int reason) {
                                        Toast.makeText(mActivity, "Filed to connect to WifiP2P Device", Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            }
                        }
                    });
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                break;
        }
    }
}
