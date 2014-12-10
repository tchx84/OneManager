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

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import one_education.org.onemanager.NetworkManager;


public class OneManager extends Activity {

    private String TAG = "OneManager";

    private NetworkManager manager;
    private String ssid;
    private List<String> networksList;

    private ListView networkView;
    private LinearLayout connectionView;
    private GridLayout proxyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new NetworkManager(this);
        ssid = null;
        networksList = null;

        setContentView(R.layout.activity_one_manager);

        // XXX keep views at hand
        networkView = (ListView) findViewById(R.id.networkView);
        connectionView = (LinearLayout) findViewById(R.id.connectionView);
        proxyView = (GridLayout) findViewById(R.id.proxyView);

        refreshNetworkView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_one_manager, menu);
        return true;
    }

    private void refreshNetworkView() {
        // TODO re-do from scratch all the interface this one is just terrible
        networksList = manager.getNetworks();
        ArrayAdapter<String> networkListAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, networksList);

        networkView.setVisibility(View.VISIBLE);
        networkView.setAdapter(networkListAdapter);
        networkView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // change to view
                networkView.setVisibility(View.GONE);
                connectionView.setVisibility(LinearLayout.VISIBLE);

                ssid = networksList.get(position);
                Log.i(TAG, String.format("onClickListener: %s", ssid));

                Button connectButton = (Button) findViewById(R.id.connectButton);
                connectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: connect");

                        connectionView.setVisibility(View.GONE);
                        proxyView.setVisibility(View.VISIBLE);

                        EditText passwordEntry = (EditText) findViewById(R.id.passwordEntry);
                        String password = passwordEntry.getText().toString();
                        manager.createAndConnect(ssid, password);

                        Button enableButton = (Button) findViewById(R.id.enableButton);
                        enableButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(TAG, "onClick: enable");

                                EditText hostEntry = (EditText) findViewById(R.id.hostEntry);
                                String host = hostEntry.getText().toString();

                                EditText portEntry = (EditText) findViewById(R.id.portEntry);
                                Integer port = Integer.parseInt(portEntry.getText().toString());

                                Log.i(TAG, String.format("%s:%d", host, port));

                                manager.setAndEnable(host, port);
                            }
                        });
                    }
                });

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
