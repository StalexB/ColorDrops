package me.stalexgaming.colordrops.enums;

/**
 * Copyright by Bankras, created on 21-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public enum GameState {

    LOBBY, INGAME, ENDING;

    private static GameState gameState;

    public static GameState getState(){
        if(gameState != null){
            return gameState;
        }
        return LOBBY;
    }

    public static void setState(GameState state){
        gameState = state;
    }

}
