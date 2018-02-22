package com.abhishekjoshi.updatesecurityprovider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecurityProvider extends CordovaPlugin implements ProviderInstaller.ProviderInstallListener{
    private static final int ERROR_DIALOG_REQUEST_CODE = 1;
    private static final String INSTALL_IF_NEEDED = "installIfNeededAsync";
    private static CallbackContext _callbackContext = null;
    private Activity _activity;
    private SecurityProvider _sp;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        _callbackContext = callbackContext;
        _sp = this;

        if (INSTALL_IF_NEEDED.equals(action)) {
            this._activity = this.cordova.getActivity();
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run(){
                    ProviderInstaller.installIfNeededAsync(_activity,_sp);
                }
            });
        }

        return true;
    }

    /**
     * This method is only called if the provider is successfully updated
     * (or is already up-to-date).
     */
    @Override
    public void onProviderInstalled(){
        // Provider is up-to-date, app can make secure network calls.
        // getCallbackContext().success("Security provider is successfully updated or is already up to date");
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, this.result("installed"));
        pluginResult.setKeepCallback(true);
        getCallbackContext().sendPluginResult(pluginResult);
    }

    /**
     * This method is called if updating fails; the error code indicates
     * whether the error is recoverable.
     */
    @Override
    public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();

        if (api.isUserResolvableError(errorCode)) {
            // Recoverable error. Send data to the app to indicate to
            // install/update/enable Google Play services.

            // getCallbackContext().error("Google Play services is not available, please install.");


            // TODO: Only for debugging so I can see what's going on. To be removed.
            api.showErrorDialogFragment(this.cordova.getActivity(),
                    errorCode,
                    ERROR_DIALOG_REQUEST_CODE,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // The user chose not to take the recovery action
                        }
                    });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, this.result("action_required"));
            pluginResult.setKeepCallback(true);
            getCallbackContext().sendPluginResult(pluginResult);

        } else {
            // Google Play services is not available.
            // This is reached if the provider cannot be updated for some reason.
            // App should consider all HTTP communication to be vulnerable, and take
            // appropriate action.

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, this.result("not_available"));
            pluginResult.setKeepCallback(true);
            getCallbackContext().sendPluginResult(pluginResult);
        }
    }

    /**
     * Build JSON to return to the plugin's callback
     */
    public JSONObject result(String result){
        JSONObject returnResult = new JSONObject();
        try{
            returnResult.put("providerInstalledUpdated", result);
        }catch(JSONException e){
            getCallbackContext().error("Fatal error" + e.getMessage());
        }
        return returnResult;
    }

    /**
     * Returns plugin callback.
     */
    protected static CallbackContext getCallbackContext() {
        return _callbackContext;
    }

}
