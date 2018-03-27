import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by Kyle on 6/15/2017.
 */

@ScriptManifest(author = "Hayase", info = "Collects Hides and Bones from Lumbridge Cows", name = "Hidden Bone Collector", version = 1.0, logo = "")
public class Main extends Script {

    /**
     * Initialize variables
     */
    private int hidePrice = 0, hideCount = 0, boneCount = 0, bonePrice = 0;
    private String status;
    private long startTime;
    private CollectBoners bone;
    private Bank goToBank;
    private CollectHide hide;
    private WalkToCows cows;

    @Override
    public void onStart() {
        /*
         * Time the start of the script
         */
        startTime = System.currentTimeMillis();

        /*
         * Get exchange information from RSBuddy for hides and boners
         */
        int cowHideId = 1739;
        int boneId = 526;
        HashMap<String, Integer> cowHideInfo = getExchangeInfo(cowHideId);
        HashMap<String, Integer> boneInfo = getExchangeInfo(boneId);

        /*
         * Make sure the RSBuddy result is not going to break our script
         */
        if (cowHideInfo.get("overall") != null) {
            hidePrice = cowHideInfo.get("overall");
        }
        if (boneInfo.get("overall") != null) {
            bonePrice = boneInfo.get("overall");
        }

        /*
         * Debug information that the script has started
         */
        bone = new CollectBoners(this);
        goToBank = new Bank(this);
        hide = new CollectHide(this);
        cows = new WalkToCows(this);
        log("Started script");
    }

    /**
     * Just use onLoop() bro
     *
     * @return the loop interval between tasks is (200,300) milliseconds
     * @throws InterruptedException if something fucks up
     */
    @Override
    public int onLoop() throws InterruptedException {
        if (!inventory.isFull() && cows.cowsNearby()) {
            status = "At work--picking up bones and hides...";
            bone.pickupBones();
            hide.collectHides();
            hideCount = hide.getHideCount();
            boneCount = bone.getBonerCount();
        } else if (inventory.isFull()) {
            status = "Time to bank";
            goToBank.walkToBank();
        } else if (!cows.cowsNearby() && inventory.isEmpty()) {
            status = "Walking to cows for more loot";
            cows.walkToCows();
        }
        return random(200, 300);
    }

    @Override
    public void onExit() {
        log("Script end. Ran for " + formatTime(System.currentTimeMillis() - startTime) + " " +
                "and collected " + hideCount + " hides and " + boneCount + " bones. " +
                "Our total profit this session was " + (hideCount * hidePrice + boneCount * bonePrice) + "gp.");
    }

    @Override
    public void onPaint(Graphics2D g) {
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
            g.setFont(new Font("Arial", Font.PLAIN, 12));

            g.setColor(Color.WHITE);
            int profit1 = hideCount * hidePrice;
            int profit2 = boneCount * bonePrice;

            /*
             * Fill in the paint with strings
             */
            g.drawString("Status: " + status, 25, 280);
            g.drawString("Hide Profit: " + profit1 + "gp (" + String.format("%.2f", (getPerHour(hideCount) * hidePrice)) + "gp/hr)", 25, 295);
            g.drawString("Bone Profit: " + profit2 + "gp (" + String.format("%.2f", (getPerHour(boneCount) * bonePrice)) + "gp/hr)", 25, 310);
            g.drawString("Total Profit: " + (profit1 + profit2) + "gp (" + (String.format("%.2f", (getPerHour(boneCount) * bonePrice) + (getPerHour(hideCount) * hidePrice))) + "gp/hr)", 25, 325);
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
     * Function to format time
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