package com.example.notesplugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Intent;

public class NotesPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("openNotesList")) {
            Intent intent = new Intent(cordova.getActivity(), NotesListActivity.class);
            cordova.getActivity().startActivity(intent);
            callbackContext.success();
            return true;
        }
        return false;
    }
}

