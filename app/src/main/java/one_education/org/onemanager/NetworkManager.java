/*
 * Copyright (C) 2014 Martin Abente Lahaye - martin.abente.lahaye@gmail.com.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package one_education.org.onemanager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import one_education.org.onemanager.ProxyHelper;


public class NetworkManager {

    private String TAG = "NetworkManager";

    private WifiManager manager;
    private WifiConfiguration config;

    public NetworkManager(Context context){
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public List<String> getNetworks(){
        List<String> networksList = new ArrayList<String>();

        List<ScanResult> networks = manager.getScanResults();
        for (ScanResult network : networks) {
            networksList.add(network.SSID);
        }

        return networksList;
    }

    public Boolean createAndConnect(String ssid, String key){
        // TODO detect network type and create configuration accordingly
        config = createNetworkWPA(ssid, key);

        Integer networkId = manager.addNetwork(config);
        if (networkId < 0) {
            Log.e(TAG, "createAndConnect: could not add network");
            return false;
        }

        Boolean result = manager.enableNetwork(networkId, true);
        if (result == false) {
            Log.e(TAG, "createAndConnect: could not enable network");
            return false;
        }

        config.networkId = networkId;

        return true;
    }

    public Boolean setAndEnable(String host, Integer port) {
        Boolean result = ProxyHelper.setProxy(config, host, port);

        if (result == false) {
            Log.e(TAG, "could not activate proxy");
            return false;
        }

        if (manager.updateNetwork(config) < 0) {
            Log.e(TAG, "could not update config");
            return false;
        }

        manager.saveConfiguration();
        manager.disconnect();
        manager.reconnect();

        return true;
    }

    private WifiConfiguration createNetworkWPA(String ssid, String key){
        WifiConfiguration newConfig = new WifiConfiguration();

        newConfig.SSID = String.format("\"%s\"", ssid);
        newConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        newConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        newConfig.preSharedKey = String.format("\"%s\"", key);

        return newConfig;
    }
}
