package AIODraynorLumJack.src;

import org.osbot.rs07.api.map.Area;

/**
 * Created by Kyle on 2016/12/15.
 */

/**
 * The constants class is for defining constant values and exchanging values between Tasks
 */
public class Constants {
    private static int total_logs = 0, nextHop = 0, playerLimits = 0, minutesPerHop = 0, logValue = 31;
    private static String statusReport = "", tree = "";
    private static boolean powerChop = false, enableAntiBan = true, enableProgressiveMode = false, sellLogsAtGE = false, skipWillowTrees = false, pauseProgressiveMode = false, needToBuyAxes = false, needToCollectAxes = false;
    private static long lastHop;

    static boolean powerChopping() {
        return Constants.powerChop;
    }

    static void powerChopping(boolean yes) {
        Constants.powerChop = yes;
    }

    public static String statusReport() {
        return Constants.statusReport;
    }

    public static void setStatusReport(int var) {
        Constants.statusReport = statusReport;
    }

    static void setTotalLogs(int logs) {
        Constants.total_logs = logs;
    }

    static void setLogPrice(int gp_value) {
        Constants.logValue = gp_value;
    }

    static int getLogPrice() {
        return Constants.logValue;
    }

    static int getTotalLogs() {
        return Constants.total_logs;
    }

    static Area getAreaForTree() {
        switch (Constants.getSelectedTree()) {
            case "Tree":
                //  return new Area(3123, 3207, 3144, 3227);//big cluster
                return draynorWorkingArea;
            case "Oak":
                //return new Area(3101,3241,3103,3245);//outside draynor bank
                return draynorWorkingArea;
            case "Willow":
                return new Area(3088, 3226, 3090, 3235);//outside draynor bank
            case "Yew":
                Area[] allYews = { //https://biyori.org/i/1483006075.png
                        new Area(3145, 3254, 3148, 3257), //top left
                        new Area(3150, 3229, 3154, 3233), //bottom left
                        new Area(3164, 3218, 3168, 3222), //bottom middle
                        new Area(3183, 3225, 3187, 3229) //farm patch
                };
                return allYews[Main.random(0, allYews.length - 1)];
            case "default":
                return new Area(3102, 3216, 3122, 3224);//near wizard tower
        }
        return new Area(3123, 3207, 3144, 3227);
    }

    static Area draynorWorkingArea = new Area( //https://biyori.org/i/1482989492.png
            new int[][]{
                    {3103, 3245},
                    {3101, 3245},
                    {3101, 3241},
                    {3097, 3237},
                    {3088, 3235},
                    {3086, 3231},
                    {3088, 3226},
                    {3093, 3214},
                    {3095, 3213},
                    {3101, 3209},
                    {3110, 3212},
                    {3117, 3210},
                    {3125, 3208},
                    {3134, 3206},
                    {3135, 3201},
                    {3136, 3196},
                    {3145, 3196},
                    {3144, 3214},
                    {3148, 3214},
                    {3200, 3214},
                    {3200, 3254},
                    {3144, 3259},
                    {3142, 3241},
                    {3140, 3234},
                    {3132, 3226},
                    {3120, 3224},
                    {3115, 3225},
                    {3111, 3227}
            }
    );

    static String woodAxes[] = {
            "Bronze axe",
            "Iron axe",
            "Steel axe",
            "Black axe",
            "Mithril axe",
            "Adamant axe",
            "Rune axe"
    };

    static boolean canUseAxe(String axe, int wcLevel) {
        switch (axe) {
            case "Bronze axe":
                return wcLevel > 0;
            case "Steel axe":
                return wcLevel > 4;
            case "Black axe":
                return wcLevel > 10;
            case "Mithril axe":
                return wcLevel > 20;
            case "Adamant axe":
                return wcLevel > 30;
            case "Rune axe":
                return wcLevel > 40;
        }
        return false;
    }

    static String bestAxeToUse(int level) {
        if (level < 5) {
            return "Bronze axe";
        } else if (level < 11) {
            return "Steel axe";
        } else if (level < 21) {
            return "Black axe";
        } else if (level < 31) {
            return "Mithril axe";
        } else if (level < 41) {
            return "Adamant axe";
        } else {
            return "Rune axe";
        }
    }

    static int axeLevelReq(String axe) {
        switch (axe) {
            case "Bronze axe":
                return 1;
            case "Steel axe":
                return 5;
            case "Black axe":
                return 11;
            case "Mithril axe":
                return 21;
            case "Adamant axe":
                return 31;
            case "Rune axe":
                return 41;
        }
        return 1;
    }

    static String treeLogType() {
        switch (Constants.getSelectedTree()) {
            case "Tree":
                return "Logs";
            case "Oak":
                return "Oak logs";
            case "Willow":
                return "Willow logs";
            case "Yew":
                return "Yew logs";
            default:
                return "Logs";
        }
    }

    static int treeLogId() {
        switch (Constants.getSelectedTree()) {
            case "Tree":
                return 1511;
            case "Oak":
                return 1521;
            case "Willow":
                return 1519;
            case "Yew":
                return 1515;
            default:
                return 1511;
        }
    }


    static void setSelectedTree(String selectedTree) {
        Constants.tree = selectedTree;
    }

    static String getSelectedTree() {
        return Constants.tree;
    }

    static void setEnableAntiBan(boolean useAntiban) {
        Constants.enableAntiBan = useAntiban;
    }

    static boolean useAntiban() {
        return Constants.enableAntiBan;
    }

    static void setHopValues(int playerLim, int minutesPerHop) {
        Constants.playerLimits = playerLim;
        Constants.minutesPerHop = minutesPerHop;
    }

    static int getHopPlayerLim() {
        return Constants.playerLimits;
    }

    static int getHopTimeMin() {
        return Constants.minutesPerHop;
    }

    static void lastHopTime(long time) {
        Constants.lastHop = time;
    }

    static long lastHopTime() {
        return Constants.lastHop;
    }

    static void nextHopTimer(int timeToHop) {
        Constants.nextHop = timeToHop;
    }

    static long getNextHop() {
        return Constants.nextHop;
    }

    static void enableProgressiveMode(boolean yes) {
        Constants.enableProgressiveMode = yes;
    }

    static boolean progressiveMode() {
        return Constants.enableProgressiveMode;
    }

    static void sellLogsAtGE(boolean yes) {
        Constants.sellLogsAtGE = yes;
    }

    static boolean sellLogsAtGE() {
        return Constants.sellLogsAtGE;
    }

    static void needToBuyAxes(boolean yes) {
        Constants.needToBuyAxes = yes;
    }

    static boolean needToBuyAxes() {
        return Constants.needToBuyAxes;
    }

    static boolean progressiveModePaused() {
        return Constants.pauseProgressiveMode;
    }

    static void pauseProgressiveMode(boolean yes) {
        Constants.pauseProgressiveMode = yes;
    }

    static void progressiveSkipWillows(boolean yes) {
        Constants.skipWillowTrees = yes;
    }

    static boolean skipWillowTrees() {
        return Constants.skipWillowTrees;
    }

    static void collectAxeAtBank(boolean yes) {
        Constants.needToCollectAxes = yes;
    }

    static boolean collectAxeAtBank() {
        return Constants.needToCollectAxes;
    }
}
