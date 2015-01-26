package com.cromiumapps.gravwar.exceptions;

import android.util.Log;

/**
 * Created by Habeeb Ahmed on 1/24/2015.
 */
public class InvalidMissileException extends Exception{
    public static final String TAG = "InvalidMissileException";
    private String what;
    public InvalidMissileException(String what){
        this.what = what;
    }

    public void printWhat(){
        Log.d(TAG, what);
    }
}
