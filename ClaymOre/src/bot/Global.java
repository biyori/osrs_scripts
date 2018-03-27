package bot;

import org.osbot.rs07.api.model.RS2Object;

import java.util.List;

/**
 * Created by Kyle on 2016/12/15.
 */
public class Global {
    private static int total_logs = 0;
    private static int limit = 0;
    private static String mule = "";
    private static String guiPickedOre = "";
    private static boolean shouldMule = false;
    private static boolean isItTimeToTradeMule = false;
    private static boolean shouldAntiban = false;
    private static long lastHop = 0;
    private static long nextHop = 0;
    private static int playerLimits = 0;
    private static int minutesPerHop = 0;
    static String pickaxes[] = {"Bronze pickaxe", "Iron pickaxe", "Steel pickaxe", "Black pickaxe", "Mithril pickaxe", "Adamant pickaxe", "Rune pickaxe"};
    private static List<RS2Object> highlightRocks = null;

    static void setHighlightRocks(List<RS2Object> rocks) {
        Global.highlightRocks = rocks;
    }

    static List<RS2Object> getHighlightRocks() {
        return Global.highlightRocks;
    }

    static void setTotalOres(int logs) {
        Global.total_logs = logs;
    }

    static int getTotalOres() {
        return Global.total_logs;
    }

    static String getMuleUsername() {
        return Global.mule;
    }

    static void setMuleUsername(String username) {
        Global.mule = username;
    }


    static boolean shouldMule() {
        return Global.shouldMule;
    }

    static void shouldMule(boolean yes) {
        Global.shouldMule = yes;
    }

    static void setShouldAntiban(boolean yes) {
        Global.shouldAntiban = yes;
    }

    static boolean shouldAntiban() {
        return Global.shouldAntiban;
    }

    static void setMuleResourceLim(int resources) {
        Global.limit = resources;
    }

    static int getMuleOreLim() {
        return Global.limit;
    }

    static String guiSelectOre() {
        return Global.guiPickedOre;
    }

    static void guiSetOre(String ore) {
        Global.guiPickedOre = ore;
    }

    static void lastHopTime(long time) {
        Global.lastHop = time;
    }

    static long lastHopTime() {
        return Global.lastHop;
    }

    static void nextHopTimer(int timeToHop) {
        Global.nextHop = timeToHop;
    }

    static long getNextHop() {
        return Global.nextHop;
    }

    static boolean timeToTradeMule() {
        return Global.isItTimeToTradeMule;
    }

    static void timeToTradeMule(boolean yes) {
        Global.isItTimeToTradeMule = yes;
    }

    static void setHopValues(int playerLim, int minutesPerHop) {
        Global.playerLimits = playerLim;
        Global.minutesPerHop = minutesPerHop;
    }

    static int getHopPlayerLim() {
        return Global.playerLimits;
    }

    static int getHopTimeMin() {
        return Global.minutesPerHop;
    }


}
