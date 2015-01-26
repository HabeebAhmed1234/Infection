package com.cromiumapps.gravwar.exceptions;

import android.util.Log;

public class InvalidMoveException extends Exception{
    private String what;
    public static String TAG = "InvalidMoveException";

    public InvalidMoveException(String what){
        this.what = what;
    }

    public void printWhat(){
        Log.d(TAG, what);
    }

}