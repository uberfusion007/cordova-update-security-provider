package com.danielcwilson.plugins.analytics;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecurityProvider extends CordovaPlugin implements ProviderInstaller.ProviderInstallListener{
    AlertDialog alertDialog;
    private static final int ERROR_DIALOG_REQUEST_CODE = 1;
    public static final String INSTALL_IF_NEEDED = "installIfNeeded";
    protected static CallbackContext _callbackContext = null;
    private boolean mRetryProviderInstall;
    private ContentValues returnValues;
    private static final String TAG = "NotificationHub";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        _callbackContext = callbackContext;

        if (INSTALL_IF_NEEDED.equals(action)) {
            ProviderInstaller.installIfNeededAsync(this.cordova.getActivity().getApplicationContext(), this);
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
        this.x("onProviderInstalled");
        getCallbackContext().success("Security provider is successfully updated or is already up to date");

    }

    /**
     * This method is called if updating fails; the error code indicates
     * whether the error is recoverable.
     */
    @Override
    public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            getCallbackContext().error("Google Play services is not available, please install.");

            GooglePlayServicesUtil.showErrorDialogFragment(
                    errorCode,
                    this.cordova.getActivity(),
                    ERROR_DIALOG_REQUEST_CODE,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // The user chose not to take the recovery action
                            onProviderInstallerNotAvailable();
                        }
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GooglePlayServicesUtil.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            mRetryProviderInstall = true;
        }
    }

    /**
     * On resume, check to see if we flagged that we need to reinstall the
     * provider.
     */
    protected void onPostResume() {
        if (mRetryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this.cordova.getActivity(), this);
        }
        mRetryProviderInstall = false;
    }

    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.

        this.x("onProviderInstallerNotAvailable");
    }
    public void x(String xx){
        this.alertDialog = new AlertDialog.Builder(this.cordova.getActivity()).create();
        this.alertDialog.setTitle("Alert");
        this.alertDialog.setMessage(xx);

        this.alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        this.alertDialog.show();
    }

    /**
     * Returns plugin callback.
     */
    protected static CallbackContext getCallbackContext() {
        return _callbackContext;
    }

}
