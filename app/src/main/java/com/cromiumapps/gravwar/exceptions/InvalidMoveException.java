package com.cromiumapps.gravwar.exceptions;

public class InvalidMoveException extends Exception{
    private String what;

    public InvalidMoveException(String what){
        this.what = what;
    }

    @Override
    public String getMessage(){
        return what;
    }

}