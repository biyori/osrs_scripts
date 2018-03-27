package bot;

/**
 * Created by Kyle on 2016/12/11.
 */

import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.Model;
import org.osbot.rs07.api.model.RS2Object;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.ceil;

@ScriptManifest(author = "Hayase", info = "Mines clay and iron in the Dwarven Mines and banks at Edgeville", name = "ClaymOre", version = 1.15, logo = "http://i.imgur.com/D5oXDem.png")
public class Main extends Script {
    private int oreMined = 0;
    private int orePrice = 0;
    private long startTime = 0;
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private newGUI gui;
    private ExecutorService executor = Executors.newCachedThreadPool();
    //private newGUI des;

    @Override
    public void onStart() {
        //GUI
      /*  String parameters = getParameters();
        if(parameters !=null) {
            parseArgs(parameters);
        } else {*/
        gui = new newGUI();
        synchronized (gui.lock) {
            try {
                gui.lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log("Error with GUI " + e.getMessage());
            }
        }
        //   }
        log("Started script");
        startTime = System.currentTimeMillis();
        experienceTracker.start(Skill.MINING);

        HashMap<String, Integer> exchangeInfo = getExchangeInfo(rock_id(Global.guiSelectOre()));//clay id
        if (exchangeInfo.get("overall") != null)
            orePrice = exchangeInfo.get("overall");

        tasks.add(new WalkTask(this));
        tasks.add(new BankTask(this));
        tasks.add(new MineTask(this));
        if (Global.shouldMule()) {
            log("Muling enabled");
            log("Will be trading resources to " + Global.getMuleUsername() + " after collecting " + Global.getMuleOreLim() + " rocks.");
            tasks.add(new MuleTask(this));
        }
        if (Global.shouldAntiban()) {
            log("Anti-ban enabled");
            tasks.add(new RandomTask(this));
        }
        if (Global.getHopPlayerLim() != 0 && Global.getHopTimeMin() != 0) {
            log("World hopping enabled. We will hop after " + Global.getHopPlayerLim() + " or more players comes to our spot. The hop interval is every " + Global.getHopTimeMin() + " minutes.");
        }
    }

    @Override
    public int onLoop() throws InterruptedException {
        tasks.forEach(tasks -> tasks.run());
        return random(200, 300);
    }

    @Override
    public void onMessage(Message c) {
        String m = c.getMessage().toLowerCase();
        if (c.getType().equals(Message.MessageType.GAME) && (m.contains("you manage to mine some") && (m.endsWith("clay.") || m.endsWith("iron.")))) {
            oreMined++;
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
        log("Script end. Ran for " + formatTime(System.currentTimeMillis() - startTime) + " and mined " + oreMined + " ores. We have banked [" + Global.getTotalOres() + "] total ores.");

        if (gui != null) {
            synchronized (gui.lock) {
                gui.lock.notify();
            }
            gui.setVisible(false);
            gui.dispose();
        }
    }

    @Override
    public void onPaint(Graphics2D g) {
        if (getClient().getLoginStateValue() == 40) //40 = connection lost
            TakeScreenshot();

        Point mP = getMouse().getPosition();

        g.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
        g.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);

        if (getClient().getMyPlayerIndex() > -1) {
            Color tBlack = new Color(0, 0, 0, 128);
            g.setFont(new Font("Arial", Font.PLAIN, 12));

            g.setColor(tBlack);
            g.fillRect(20, 235, 180, 95);
            g.setColor(Color.WHITE);
            g.drawRect(20, 235, 180, 95);
            int profit = Global.getTotalOres() * orePrice;
            String suffix = "";
            if (profit > 1000) {
                profit = profit / 1000;
                suffix = "k";
            }

            String lvlsGained = experienceTracker.getGainedLevels(Skill.MINING) > 0 ? " (+" + experienceTracker.getGainedLevels(Skill.MINING) + ")" : "";
            g.drawString("Level: " + skills.getStatic(Skill.MINING) + lvlsGained + " (TTL: " + formatTime(experienceTracker.getTimeToLevel(Skill.MINING)) + ")", 25, 250);
            g.drawString("XP Gained: " + experienceTracker.getGainedXP(Skill.MINING) + " [" + experienceTracker.getGainedXPPerHour(Skill.MINING) + "/hr]", 25, 265);
            g.drawString(whatOre() + " mined: " + oreMined + " [" + ceil(getPerHour(oreMined)) + "/hr]", 25, 280);
            g.drawString(whatOre() + " in bank: " + Global.getTotalOres() + " (" + profit + suffix + " gp)", 25, 295);
            g.drawString("Profit: " + oreMined * orePrice + "gp [" + String.format("%.2f", (getPerHour(oreMined)) * orePrice) + " gp/hr]", 25, 310);
            g.drawString("Time Ran: " + formatTime(System.currentTimeMillis() - startTime), 25, 325);

            /*new Thread(() -> {
                if (Global.getHighlightRocks() != null) {
                    for (RS2Object aRock : Global.getHighlightRocks()) drawModel(g, aRock);
                }
            }).start();
*/
            Runnable r = () -> {
                if (Global.getHighlightRocks() != null) {
                    for (RS2Object aRock : Global.getHighlightRocks()) {
                        if (aRock.isVisible() && getClient().isLoggedIn())
                            drawModel(g, aRock);
                    }
                }
            };
            executor.submit(r);
        }
    }

    private void drawModel(Graphics2D g, Entity entity) {
        Color colors[] = {Color.BLACK, Color.BLUE, Color.CYAN, Color.RED, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.WHITE, Color.YELLOW};
        Random random = new Random();
        int index = random.nextInt(colors.length);
        g.setColor(colors[index]);
        Model model = entity.getModel();

        if (model == null)
            return;

        g.drawPolygon(entity.getPosition().getPolygon(getBot()));
        //GraphicUtilities.drawWireframe(getBot(), g, entity);
        //GraphicUtilities.drawModel(getBot(), g, entity.getGridX(), entity.getGridY(), entity.getZ(), model);
    }

    private String formatTime(final long ms) {
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60;
        m %= 60;
        h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

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

   /* private void parseArgs(String parameters) {
        String[] args = parameters.split("-");//location-antiban-mule
        return args[i].replace("_", " ");
    }*/

    private void TakeScreenshot() {
        log("Attempting to take a screenshot!");
        BufferedImage img = bot.getCanvas().getGameBuffer();
        String fileName = String.valueOf(System.currentTimeMillis() / 1000).substring(5, 10);//last 5 digits

        try {
            //make hayase folder
            if (!new File(getDirectoryData() + "Claym0re/Screenshots").exists()) {
                new File(getDirectoryData() + "Claym0re/Screenshots").mkdirs();
            }
            ImageIO.write(
                    img,
                    "png",
                    new File(getDirectoryData() + "Claym0re/Screenshots/" + myPlayer().getName().replaceAll("\\u00a0", "_") + "-" + fileName + ".png"));
        } catch (Exception e) {
            log("Error! " + e.getMessage());
        }
    }

    private int rock_id(String rock) {
        switch (rock) {
            case "Clay (west)":
                return 434;
            case "Clay (east)":
                return 434;
            case "Iron (west)":
                return 440;
            case "Iron (east)":
                return 440;
            default:
                return -1;
        }
    }

    private String whatOre() {
        switch (Global.guiSelectOre()) {
            case "Clay (west)":
                return "Clay";
            case "Clay (east)":
                return "Clay";
            case "Iron (west)":
                return "Iron";
            case "Iron (east)":
                return "Iron";
            default:
                return "Clay";
        }
    }

    private double getPerHour(double value) {
        if ((System.currentTimeMillis() - startTime) > 0) {
            return value * 3600000d / (System.currentTimeMillis() - startTime);
        } else {
            return 0;
        }
    }
}