package hayase.AIODraynorLumJack.src;

/**
 * Created by Kyle on 2016/12/11.
 */

import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@ScriptManifest(author = "Hayase", info = "Chops trees in Draynor/Lumbridge and finally banks in Draynor", name = "AIO Draynor Lumberjack", version = 1.0, logo = "http://i.imgur.com/la9gPez.png")
public class Main extends Script {

    /**
     * Initialize variables
     */
    private int logsChopped = 0, logPrice = 0;
    private long startTime = 0;
    private ArrayList<Task> tasks = new ArrayList<>();
    private GUI gui;

    @Override
    public void onStart() {

        /*
         * Create a new GUI object and lock it
         * Wait for the object to be ready for operation
         */
        gui = new GUI();
        synchronized (gui.lock) {
            try {
                gui.lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log("Error locking GUI " + e.getMessage());
            }
        }

        /*
         * Store the script start time
         */
        startTime = System.currentTimeMillis();

        /*
         * Start the experience tracker for woodcutting
         */
        experienceTracker.start(Skill.WOODCUTTING);

        /*
         * Get exchange information from RSBuddy for the given tree type
         */
        HashMap<String, Integer> exchangeInfo = getExchangeInfo(Constants.treeLogId());

        /*
         * Making sure the result is not going to break our script
         */
        if (exchangeInfo.get("overall") != null) {
            logPrice = exchangeInfo.get("overall");
        }

        /*
         * Debug information that the script has started
         */
        log("Started script");

        /*
         * Add the AxeTask to the script
         * Detects if the player needs to go to the bank for an axe
         */
        tasks.add(new AxeTask(this));

        /*
         * Adds the WalkTask to the script
         * Detects if we need to walk to trees to chop them
         */
        tasks.add(new WalkTask(this));

        /*
         * Conditional for Power Chopping mode
         */
        if (Constants.powerChopping()) {
            log("Power Chopping enabled");

            /*
             * Add the DropTask to the script
             */
            tasks.add(new DropTask(this));
        } else {

            /*
             * Power chopping is disabled--add the BankTask
             * Responsible for banking all chopped logs once the players inventory is full
             */
            tasks.add(new BankTask(this));
        }

        /*
         * If progressive mode is enabled and we can sell logs at the GE, add the ExchangeTask to the script
         */
        if (Constants.sellLogsAtGE()) {
            log("Grand Exchange enabled");

            /*
             * ExchangeTask is responsible for walking to the GE and selling logs to purchase axes
             */
            tasks.add(new ExchangeTask(this));
        }

        /*
         * Adds the ChopTask to the script
         * Responsible for detecting all local trees and chopping them with respect to their type
         */
        tasks.add(new ChopTask(this));

        /*
         * If progressive mode is enabled add ProgressiveTask to our script
         */
        if (Constants.progressiveMode()) {
            log("Progressive Mode enabled");

            /*
             * ProgressiveTask is responsible for changing what type of tree the player will be chopping with respect
             * to the players woodcut level
             */
            tasks.add(new ProgressiveTask(this));
        }

        /*
         * If the player is using progressive mode and has selected, "Skip Willow trees" this constant will ensure that
         * ProgressiveTask skips willow trees (it will chop oak trees until it can chop yew trees)
         */
        if (Constants.skipWillowTrees()) {
            log("Progressive mode will skip willow trees.");
        }

        /*
         * If anti-ban was selected, add the RandomTask methods to our script
         */
        if (Constants.useAntiban()) {
            log("Anti-ban enabled");

            /*
             * RandomTask is a set of methods that try to artificially replicate real player behavior
             */
            tasks.add(new RandomTask(this));
        }

        /*
         * World hopping support enabled if the world hop constraints do not equal zero
         */
        if (Constants.getHopPlayerLim() != 0 || Constants.getHopTimeMin() != 0) {
            log("World hopping enabled. We will hop after " + Constants.getHopPlayerLim() + " or more players enters our spot. The hop interval is every " + Constants.getHopTimeMin() + " minutes.");
        }
    }

    /**
     * Loop through each task in the given start order
     *
     * @return the loop interval between tasks is (200,300) milliseconds
     * @throws InterruptedException if the script has been stopped mid execution
     */
    @Override
    public int onLoop() throws InterruptedException {
        tasks.forEach(tasks -> tasks.run());
        return random(200, 300);
    }

    /**
     * Gather all of the in game chat messages
     *
     * @param c the message from the in game chat box
     */
    @Override
    public void onMessage(Message c) {
        String m = c.getMessage().toLowerCase();

        /*
         * Detect if we have gotten some logs from a tree
         */
        if (m.contains("you get some") && m.endsWith("logs.")) {
            logsChopped++;
        }

        /*
         * Detect if our previously set GE offer returns completed
         */
        if (m.contains("finished buying 1 x") && m.contains("axe.")) {

            /*
             * Tell the BankTask to collect our axes at the next bank session
             */
            Constants.collectAxeAtBank(true);
        }
    }

    @Override
    public void pause() {
        tasks.forEach(tasks -> tasks.pause());
    }

    @Override
    public void resume() {
        tasks.forEach(tasks -> tasks.resume());
    }

    @Override
    public void onExit() {
        if (gui != null) {
            synchronized (gui.lock) {
                gui.lock.notify();
            }
            gui.setVisible(false);
            gui.dispose();
        }

        log("Script end. Ran for " + formatTime(System.currentTimeMillis() - startTime) + " and cut " + logsChopped + " logs. We have banked [" + Constants.getTotalLogs() + "] " + Constants.treeLogType());
    }

    @Override
    public void onPaint(Graphics2D g) {

        /*
         * If the bot loses connection (got banned?)--take a screenshot
         * The reason for this is to screenshot the last bit of life the user might have
         *
         * Note: I noticed there was a Utilities.takeScreenshot() function but I couldn't change the file name to the players username for easy organization
         */
        if (getClient().getLoginStateValue() == 40) {//40 = connection lost
            log("Client connection lost!");
            TakeScreenshot();//possibly add in paint as well
        }

        /*
         * Change the mouse pointer to an X
         */
        Point mP = getMouse().getPosition();
        g.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
        g.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);

        /*
         * This is unnecessary but to keep the paint aesthetically pretty
         * we display paint after the bot has logged in
         */
        if (getClient().getMyPlayerIndex() > 0) {
            int width;

            Color tBlack = new Color(0, 0, 0, 128);
            g.setFont(new Font("Arial", Font.PLAIN, 12));

            /*
             * To handle the width of our paint box, store the strings in some objects to compare later
             */
            String chopStats = Constants.treeLogType() + " chopped: " + logsChopped + " [" + Math.ceil(getPerHour(logsChopped)) + "/hr]";
            String profitStats = "Profit: " + logsChopped * logPrice + "gp [" + String.format("%.2f", (getPerHour(logsChopped)) * logPrice) + " gp/hr]";

            /*
             * Calculate which string is longer and make that our width
             */
            if (chopStats.length() < profitStats.length()) {
                width = profitStats.length();
            } else {
                width = chopStats.length();
            }

            /*
             * Create the paint box which will hold our stats
             */
            g.setColor(tBlack);
            g.fillRect(20, 235, (width * 6), 95);//[width * 6] seems to work the best with the spaces between characters
            g.setColor(Color.WHITE);
            g.drawRect(20, 235, (width * 6), 95);
            int profit = Constants.getTotalLogs() * logPrice;
            String suffix = "";
            if (profit > 1000) {
                profit = profit / 1000;
                suffix = "k";
            }

            /*
             * Fill in the paint box with  strings
             */
            g.drawString(Constants.treeLogType() + " in bank: " + Constants.getTotalLogs() + " (" + profit + suffix + " gp)", 25, 250);//TODO AVERAGE BANK TIMER
            g.drawString(profitStats, 25, 265);
            g.drawString(chopStats, 25, 280);
            g.drawString("Time Ran: " + formatTime(System.currentTimeMillis() - startTime), 25, 295);
            g.drawString("XP Gained: " + experienceTracker.getGainedXP(Skill.WOODCUTTING) + " [" + experienceTracker.getGainedXPPerHour(Skill.WOODCUTTING) + "/hr]", 25, 310);
            g.drawString("Next level: " + formatTime(experienceTracker.getTimeToLevel(Skill.WOODCUTTING)), 25, 325);
        }

        /*
         * Hide player username on paint
         */
        if (getClient().isLoggedIn()) {
            Color tanColor = new Color(204, 187, 154);
            g.setColor(tanColor);
            g.fillRect(9, 459, 91, 15);
        }
    }

    /**
     * Function to format system time into a readable format
     *
     * @param ms millisecond input
     * @return readable time
     */
    private String formatTime(final long ms) {
        //1000 ms in one second, 60 seconds per minute, 60 minutes per hour
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60;
        m %= 60;
        //  h %= 24; //If the script runs for more than 24 hours don't reset
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    /**
     * Function to gather Grand Exchange values from RSBuddy
     *
     * @param id the id of the item
     * @return the values in a HashMap
     */
    private HashMap<String, Integer> getExchangeInfo(int id) {

        HashMap<String, Integer> exchangeInfo = new HashMap<>();

        try {
            URL url = new URL("http://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + id);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            con.setUseCaches(true);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json = br.readLine();
            br.close();
            json = json.replaceAll("[{}\"]", "");
            String[] items = json.split(",");
            for (String item : items) {
                String[] splitItem = item.split(":");
                exchangeInfo.put(splitItem[0], Integer.parseInt(splitItem[1]));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return exchangeInfo;
    }

    /**
     * Function to take a screenshot of the bot canvas and save it to a folder in the bot working directory
     * <p>
     * The purpose of this function is purely for analysis of when and where a player was possibly banned. This function
     * is only called when the player loses connection in-game. It does generate false positives such as the player really
     * did disconnect--but in the case of a real ban I think this will be helpful.
     */
    private void TakeScreenshot() {
        log("Attempting to take a screenshot");
        BufferedImage img = bot.getCanvas().getGameBuffer();

        //Get the current date to save in the screenshot folder
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue(), day = today.getDayOfMonth(), year = today.getYear();

        //Make the file name the last 5 digits of System.currentTimeMillis() converted to seconds
        //If I didn't use the LocalDate, 7 digits is enough time to handle 3.8 months of seconds before returning duplicates
        String fileName = String.valueOf(System.currentTimeMillis() / 1000).substring(5, 10);

        try {

            //Create a folder with my forum name to tell the user which script created this folder
            if (!new File(getDirectoryData() + "Hayase/Screenshots").exists()) {
                new File(getDirectoryData() + "Hayase/Screenshots").mkdirs();
            }

            //save the image to the folder and rename all player spaces with underscores
            if (ImageIO.write(
                    img,
                    "png",
                    new File(getDirectoryData() + "Hayase/Screenshots/" + year + "." + month + "." + day + "-" + myPlayer().getName().replaceAll("\\u00a0", "_") + "-" + fileName + ".png"))) {
                log("Saved " + getDirectoryData() + "Hayase/Screenshots/" + year + "." + month + "." + day + "-" + myPlayer().getName().replaceAll("\\u00a0", "_") + "-" + fileName + ".png");
            }
        } catch (Exception e) {
            log("Error! " + e.getMessage());
        }
    }

    /**
     * Function to calculate values per hour
     *
     * @param value the value to calculate
     * @return the expected value one hour from now
     */
    private double getPerHour(double value) {
        if ((System.currentTimeMillis() - startTime) > 0) {
            return value * 3600000d / (System.currentTimeMillis() - startTime);
        } else {
            return 0;
        }
    }
}