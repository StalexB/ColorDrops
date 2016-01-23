package me.stalexgaming.colordrops.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright by Bankras, created on 22-1-2016.
 * Stealing and/or copying this plugin has severe consequences.
 */
public enum Area {

    RED_SPAWN, BLUE_SPAWN, BLOCKSPAWN_1, BLOCKSPAWN_2, BLOCKSPAWN_3, BLOCKSPAWN_4, BLOCKSPAWN_5, BLOCKSPAWN_6, BLOCKSPAWN_7, BLOCKSPAWN_8;

    public static List<Area> getAreas(){
        List<Area> areas = new ArrayList<>();

        areas.add(RED_SPAWN);
        areas.add(BLUE_SPAWN);
        areas.add(BLOCKSPAWN_1);
        areas.add(BLOCKSPAWN_2);
        areas.add(BLOCKSPAWN_3);
        areas.add(BLOCKSPAWN_4);
        areas.add(BLOCKSPAWN_5);
        areas.add(BLOCKSPAWN_6);
        areas.add(BLOCKSPAWN_7);
        areas.add(BLOCKSPAWN_8);

        return areas;
    }

    public static List<Area> getBlockSpawns(){
        List<Area> areas = new ArrayList<>();

        areas.add(BLOCKSPAWN_1);
        areas.add(BLOCKSPAWN_2);
        areas.add(BLOCKSPAWN_3);
        areas.add(BLOCKSPAWN_4);
        areas.add(BLOCKSPAWN_5);
        areas.add(BLOCKSPAWN_6);
        areas.add(BLOCKSPAWN_7);
        areas.add(BLOCKSPAWN_8);

        return areas;
    }

}
