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

import android.net.wifi.WifiConfiguration;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.ScatteringByteChannel;

/**
 * Created by tch on 12/10/14.
 */
public class ProxyHelper {

    static private String TAG = "ProxyHelper";
    static private String TAG_METHOD = "setProxy";

    static private String PROPERTIES_FIELD = "linkProperties";
    static private String PROPERTIES_METHOD = "setHttpProxy";

    static  private String PROXY_CLASS = "android.net.ProxyProperties";
    static private String PROXY_FIELD = "proxySettings";
    static private String PROXY_TYPE = "STATIC";

    static Boolean setProxy(WifiConfiguration config, String host, Integer port) {
        Log.e(TAG, TAG_METHOD);
        try {
            Object linkProperties = WifiConfiguration.class.getField(PROPERTIES_FIELD).get(config);
            Class<?> ProxyProperties = Class.forName(PROXY_CLASS);
            Method setHttpProxy = linkProperties.getClass().getDeclaredMethod(PROPERTIES_METHOD, ProxyProperties);

            setHttpProxy.setAccessible(true);
            // TODO research what other properties are supported
            Object proxyProperties = ProxyProperties.getConstructor(String.class, int.class, String.class).newInstance(host, port, null);
            setHttpProxy.invoke(linkProperties, proxyProperties);

            Field proxySettings = WifiConfiguration.class.getField(PROXY_FIELD);
            proxySettings.set(config,  Enum.valueOf((Class<Enum>) proxySettings.getType(), PROXY_TYPE));

            return true;
        } catch (NoSuchFieldException e) {
            Log.e(TAG, TAG_METHOD, e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, TAG_METHOD, e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, TAG_METHOD, e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, TAG_METHOD, e);
        } catch (InstantiationException e) {
            Log.e(TAG, TAG_METHOD, e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, TAG_METHOD, e);
        }

        return false;
    }
}
