package com.tituspeterson.CordovaPluginCloverAndroidSDK;

import com.clover.sdk.util.Platform2
import com.clover.sdk.v3.scanner.BarcodeScanner

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * This class echoes a string called from JavaScript.
 */
public class CloverAndroidSDK extends CordovaPlugin {

    public static final String BARCODE_BROADCAST = "com.clover.BarcodeBroadcast";
    private final BarcodeReceiver barcodeReceiver = new BarcodeReceiver();
    private CallbackContext callbackContext;

    @Override
    public void onStart() {
        super.onStart();
        registerBarcodeScanner();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterBarcodeScanner();
    }

    private void registerBarcodeScanner() {
        registerReceiver(barcodeReceiver, new IntentFilter(BARCODE_BROADCAST));
    }

    private void unregisterBarcodeScanner() {
        unregisterReceiver(barcodeReceiver);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("isClover") {
            checkCloverDevice(callbackContext);
            return true;
        } else if (action.equals("startScan")) {
            this.callbackContext = callbackContext;
            startBarcodeScanner();
            return true;
        }
        return false;
    }

    private boolean checkCloverDevice(CallbackContext callbackContext) {
        Platform2 platform = new Platform2();
        boolean isClover = platform.isClover();
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, isClover);
        callbackContext.sendPluginResult(pluginResult);
        return isClover;
    }

    private static Bundle getBarcodeSetting(final boolean enabled) {
        final Bundle extras = new Bundle();
        extras.putBoolean(Intents.EXTRA_START_SCAN, enabled);
        extras.putBoolean(Intents.EXTRA_SHOW_PREVIEW, enabled);
        extras.putBoolean(Intents.EXTRA_SHOW_MERCHANT_PREVIEW, enabled);
        extras.putBoolean(Intents.EXTRA_LED_ON, false);
        return extras;
    }

    public boolean startBarcodeScanner() {
        return new BarcodeScanner(this).startScan(getBarcodeSetting(true));
    }

    public boolean stopBarcodeScanner() {
        return new BarcodeScanner(this).stopScan(getBarcodeSetting(false));
    }

    private class BarcodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BARCODE_BROADCAST)) {
                stopBarcodeScanner();
                String barcode = intent.getStringExtra("Barcode");
                if (barcode != null) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, barcode);
                    callbackContext.sendPluginResult(pluginResult);
                }
            }
        }
    }
}