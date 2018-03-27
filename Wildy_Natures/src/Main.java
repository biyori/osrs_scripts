/**
 * Created by Kyle on 4/14/2017.
 */

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.input.mouse.MiniMapTileDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;


@ScriptManifest(author = "Hayase", info = "Telegrabs nature runes from deep wild", logo = "", name = "DeezNats", version = 1.0)

public class Main extends Script {
    private static Area finishArea = new Area(3272, 3526, 3277, 3531);
    private static Position pos1 = new Position(3307, 3859, 0);
    private static Position pos2 = new Position(3311, 3853, 0);
    static ArrayList<Long> times = new ArrayList<>();
    static ArrayList<Long> times2 = new ArrayList<>();
    private static Position[] nat_runez = {
            pos1,
            pos2
    };
    private static Position[] pathToNats = {
            new Position(3274, 3524, 0),
            new Position(3284, 3526, 0),
            new Position(3294, 3528, 0),
            new Position(3304, 3530, 0),
            new Position(3314, 3532, 0),
            new Position(3324, 3534, 0),
            new Position(3331, 3534, 0),
            new Position(3336, 3543, 0),
            new Position(3341, 3552, 0),
            new Position(3345, 3558, 0),
            new Position(3343, 3566, 0),
            new Position(3345, 3571, 0),
            new Position(3352, 3572, 0),
            new Position(3355, 3580, 0),
            new Position(3354, 3590, 0),
            new Position(3353, 3600, 0),
            new Position(3352, 3600, 0),
            new Position(3344, 3605, 0),
            new Position(3336, 3610, 0),
            new Position(3333, 3612, 0),
            new Position(3332, 3621, 0),
            new Position(3338, 3628, 0),
            new Position(3345, 3628, 0),
            new Position(3348, 3635, 0),
            new Position(3357, 3636, 0),
            new Position(3365, 3643, 0),
            new Position(3365, 3643, 0),
            new Position(3365, 3653, 0),
            new Position(3365, 3661, 0),
            new Position(3373, 3667, 0),
            new Position(3375, 3668, 0),
            new Position(3376, 3678, 0),
            new Position(3377, 3688, 0),
            new Position(3378, 3696, 0),
            new Position(3372, 3704, 0),
            new Position(3366, 3712, 0),
            new Position(3366, 3714, 0),
            new Position(3368, 3724, 0),
            new Position(3370, 3731, 0),
            new Position(3373, 3740, 0),
            new Position(3376, 3749, 0),
            new Position(3377, 3750, 0),
            new Position(3368, 3755, 0),
            new Position(3359, 3760, 0),
            new Position(3350, 3765, 0),
            new Position(3348, 3767, 0),
            new Position(3346, 3777, 0),
            new Position(3344, 3787, 0),
            new Position(3343, 3788, 0),
            new Position(3342, 3798, 0),
            new Position(3342, 3802, 0),
            new Position(3340, 3812, 0),
            new Position(3340, 3813, 0),
            new Position(3331, 3818, 0),
            new Position(3322, 3823, 0),
            new Position(3313, 3828, 0),
            new Position(3310, 3829, 0),
            new Position(3301, 3834, 0),
            new Position(3301, 3834, 0),
            new Position(3302, 3844, 0),
            new Position(3301, 3854, 0)
    };
    private static Position[] pathToHome = {
            new Position(3306, 3844, 0),
            new Position(3308, 3838, 0),
            new Position(3310, 3832, 0),
            new Position(3312, 3827, 0),
            new Position(3315, 3822, 0),
            new Position(3319, 3817, 0),
            new Position(3322, 3812, 0),
            new Position(3328, 3804, 0),
            new Position(3334, 3796, 0),
            new Position(3339, 3789, 0),
            new Position(3342, 3779, 0),
            new Position(3344, 3771, 0),
            new Position(3348, 3762, 0),
            new Position(3349, 3759, 0),
            new Position(3356, 3756, 0),
            new Position(3365, 3752, 0),
            new Position(3366, 3751, 0),
            new Position(3373, 3744, 0),
            new Position(3380, 3738, 0),
            new Position(3374, 3730, 0),
            new Position(3369, 3725, 0),
            new Position(3367, 3715, 0),
            new Position(3365, 3709, 0),
            new Position(3373, 3703, 0),
            new Position(3381, 3697, 0),
            new Position(3382, 3695, 0),
            new Position(3381, 3685, 0),
            new Position(3381, 3685, 0),
            new Position(3379, 3675, 0),
            new Position(3377, 3665, 0),
            new Position(3377, 3662, 0),
            new Position(3367, 3660, 0),
            new Position(3365, 3660, 0),
            new Position(3358, 3653, 0),
            new Position(3351, 3646, 0),
            new Position(3344, 3639, 0),
            new Position(3337, 3632, 0),
            new Position(3335, 3629, 0),
            new Position(3332, 3619, 0),
            new Position(3329, 3609, 0),
            new Position(3329, 3607, 0),
            new Position(3337, 3601, 0),
            new Position(3341, 3597, 0),
            new Position(3338, 3587, 0),
            new Position(3336, 3580, 0),
            new Position(3328, 3574, 0),
            new Position(3320, 3568, 0),
            new Position(3312, 3562, 0),
            new Position(3305, 3558, 0),
            new Position(3299, 3550, 0),
            new Position(3293, 3542, 0),
            new Position(3287, 3534, 0),
            new Position(3281, 3526, 0),
            new Position(3274, 3523, 0)
    };
    private static Position[] teleGrabTiles = {
            new Position(3303, 3859, 0),
            new Position(3302, 3858, 0),
            new Position(3302, 3856, 0),
            new Position(3302, 3855, 0),
            new Position(3302, 3854, 0),//works
            new Position(3303, 3853, 0),
            new Position(3304, 3852, 0)
    };
    private static String state;
    private Optional<Integer> price;
    private long startTime;
    private long itemCount = 0;
    private long currentItemCount = -1;
    private static int wildyLevel = 0;
    static boolean needToGetOutOfHere = false, hopWorlds = false;

    @Override
    public void onStart() {
        price = getPrice(561); //nature rune
        log(price);
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.MAGIC);
    }

    /**
     * if (shouldIDrinkPotion()) {
     * drinkPotion();
     * }
     * <p>
     * final WebWalkEvent webWalkEvent = new WebWalkEvent(destination);
     * webWalkEvent.setBreakCondition(new Condition() {
     *
     * @Override public boolean evaluate() {
     * return shouldIDrinkPotion();
     * }
     * <p>
     * });
     * execute(webWalkEvent);
     */


    @Override
    public int onLoop() throws InterruptedException {

       /* if (needToGetOutOfHere)
            stop();
*/
        setWildernessLevel(getWildernessLevelWidget());

        if (getClient().isLoggedIn()) {
            new Thread(() -> {
                if (widgets.isVisible(90, 46) && getPlayers().getAll().size() > 1) {
                    needToGetOutOfHere = true;
                }
            }).start();
        }

        if (getWildernessLevel() > 10 && getPlayers().getAll().size() > 1) {
            log("Local Players: " + getPlayers().getAll().size());
            for (Player player : getPlayers().getAll()) {
                log("[" + player.getName() + "] level: [" + player.getCombatLevel() + "]");
            }
        }

        for (States state : States.values())
            if (state.canProcess(this))
                state.process(this);
        recountItems();

        return 33;
    }


    enum States {

        GRAB {
            @Override
            public boolean canProcess(MethodProvider mp) {
                for (Position posi : teleGrabTiles) {
                    if (mp.myPosition().equals(posi) && mp.getInventory().contains("Law rune") && !needToGetOutOfHere && mp.getEquipment().isWieldingWeapon("Staff of air"))
                        return true;
                }
                return false;
            }

            @Override
            public void process(MethodProvider mp) {
                GroundItem nat = mp.getGroundItems().closest("Nature rune");
                if (mp.getMagic().isSpellSelected()) {
                    if (nat != null && nat.isVisible()) {
                        if (nat.getPosition().equals(pos1))
                            times2.add(System.currentTimeMillis());
                        if (times2.size() > 1) {
                            float difference = times2.get(times2.size() - 1) - times2.get(times2.size() - 2);
                            mp.log("New spawn! The spawn time was approximately: " + difference + " ms (" + (difference / 1000) + " sec)");
                        }
                        try {
                            sleep(random(190, 280));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        nat.interact("Cast");
                        int index;
                        if (nat.getPosition().equals(pos1))
                            index = 0;
                        else
                            index = 1;
                        state = "Interacting";
                        new ConditionalSleep(5_000) {
                            @Override
                            public boolean condition() throws InterruptedException {
                                return !nat.exists();
                            }
                        }.sleep();
                        times.add(index, System.currentTimeMillis());
                    } else {
                        if (nat != null && !nat.isVisible()) {
                            state = "Moving camera to runes";
                            mp.camera.toEntity(nat);
                        }
                        if (nat == null) {
                            java.util.List<GroundItem> anyNatsAround = mp.groundItems.filter(item -> item != null && item.getName().equals("Nature rune"));
                            if (anyNatsAround.size() == 0) {
                                hopWorlds = true;
                            }
                            Rectangle rect = pos1.getPolygon(mp.getBot()).getBounds();
                            mp.getMouse().move(rect.x + (rect.width / 2), rect.y + (rect.height / 2));
                            state = "Moving mouse to center";
                        }
                    }
                } else {
                    state = "Casting";
                    mp.getMagic().castSpell(Spells.NormalSpells.TELEKINETIC_GRAB);
                }
            }
        },

        WALK {
            @Override
            public boolean canProcess(MethodProvider mp) {
                return !Banks.VARROCK_EAST.contains(mp.myPlayer()) && !mp.getInventory().contains("Law rune")
                        || mp.myPosition().distance(teleGrabTiles[1]) > 10 && mp.getInventory().contains("Law rune");
            }

            @Override
            public void process(MethodProvider mp) {
                if (!Banks.VARROCK_EAST.contains(mp.myPlayer()) && !mp.getInventory().contains("Law rune")) {
                    state = "Walking to bank";
                    if (pathToHome[0].distance(mp.myPlayer()) < 20)
                        mp.getWalking().walkPath(Arrays.asList(pathToHome));
                    mp.getWalking().webWalk(Banks.VARROCK_EAST);
                } else if (getWildernessLevel() == 0 && mp.getInventory().contains("Law rune")) {
                    state = "Walking to starting position";
                    WebWalkEvent startingEvent = new WebWalkEvent(finishArea.getRandomPosition());
                    startingEvent.setEnergyThreshold(5);
                    startingEvent.useSimplePath();
                    mp.execute(startingEvent);
                } else {
                    state = "Walking to natures";
                    mp.getWalking().walkPath(Arrays.asList(pathToNats));
                }
            }
        },

        CENTER_TILE {
            @Override
            public boolean canProcess(MethodProvider mp) {
                for (Position posi : teleGrabTiles) {
                    if (mp.myPosition().equals(posi) && mp.getInventory().contains("Law rune") && getWildernessLevel() > 40) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void process(MethodProvider mp) throws InterruptedException {
                state = "Moving to telegrab position";
                Position tiles = new Position(teleGrabTiles[random(0, teleGrabTiles.length - 1)]);
                if (tiles.distance(mp.myPosition()) < 10) {
                    mp.walking.walk(tiles);
                    new ConditionalSleep(10_000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return mp.myPosition().equals(tiles) || !mp.myPlayer().isMoving();
                        }
                    }.sleep();
                }
            }
        },

        UNDER_ATTACK {
            @Override
            public boolean canProcess(MethodProvider mp) {
                return mp.myPlayer().isUnderAttack();
            }

            @Override
            public void process(MethodProvider mp) throws InterruptedException {
                state = "Under attack! Running home...";
                mp.walking.settings.setRunning(true);
                mp.walking.walkPath(Arrays.asList(pathToHome));
            }
        },

        LOGOUT {
            @Override
            public boolean canProcess(MethodProvider mp) {
                return needToGetOutOfHere;
            }

            @Override
            public void process(MethodProvider mp) throws InterruptedException {
                state = "WE GOTTA GET OUT OF HERE";
                if (mp.getMagic().isSpellSelected())
                    mp.mouse.click(false);
                mp.getLogoutTab().logOut();
                int min = 60 * random(1, 5);
                state = "Sleeping for " + min + " seconds (" + (min / 60) + " mins)";
                // mp.log("Sleeping for " + min + " seconds (" + (min / 60) + " mins)");
                //  sleep(1000 * min);

                // needToGetOutOfHere = false;
                //TODO: Update paint with timer
            }
        },

        EQUIP_STAFF {
            @Override
            public boolean canProcess(MethodProvider mp) {
                return !mp.getEquipment().isWieldingWeapon("Staff of air");
            }

            @Override
            public void process(MethodProvider mp) throws InterruptedException {
                state = "Equipping staff";
                if (mp.inventory.contains("Staff of air")) {
                    mp.equipment.equip(EquipmentSlot.WEAPON, "Staff of air");
                    new ConditionalSleep(2_000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return mp.getEquipment().isWieldingWeapon("Staff of air");
                        }
                    }.sleep();
                }

                if (mp.inventory.contains("Black cape")) {
                    mp.equipment.equip(EquipmentSlot.CAPE, "Black cape");
                    new ConditionalSleep(2_000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return mp.getEquipment().isWearingItem(EquipmentSlot.CAPE, "Black cape");
                        }
                    }.sleep();
                }
            }
        },

     /*   HOP_WORLDS {
            @Override
            public boolean canProcess(MethodProvider mp) {
                return hopWorlds;
            }

            @Override
            public void process(MethodProvider mp) throws InterruptedException {
                state = "Hopping worlds...";
                if (mp.getMagic().isSpellSelected())
                    mp.mouse.click(false);
                mp.worlds.hopToF2PWorld();
                new ConditionalSleep(10_000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return mp.getClient().getLoginStateValue() == 30;
                    }
                }.sleep();
*/
                /*
                 * Force a sleep after hopping successfully to make sure the client variables have finished refreshing (Fix no axe found issues)
                 */
            /*    try {
                    sleep(random(2000, 3100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hopWorlds = false;
            }
        },*/

        BANK {
            @Override
            public boolean canProcess(MethodProvider mp) {
                return Banks.VARROCK_EAST.contains(mp.myPlayer()) && !mp.getInventory().contains("Law rune");
            }

            @Override
            public void process(MethodProvider mp) throws InterruptedException {
                if (mp.getBank().isOpen()) {
                    state = "Depositing";
                    mp.getBank().depositAll("Nature Rune");
                    new ConditionalSleep(5000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return !mp.getInventory().contains("Nature rune");
                        }
                    }.sleep();
                    mp.getBank().withdraw("Law rune", random(10, 15));
                    new ConditionalSleep(5000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return mp.getInventory().contains("Law rune");
                        }
                    }.sleep();
                } else {
                    state = "Opening bank";
                    mp.getBank().open();
                    new ConditionalSleep(5000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return mp.getBank().isOpen();
                        }
                    }.sleep();
                }
            }
        };

        public abstract boolean canProcess(MethodProvider mp) throws InterruptedException;

        public abstract void process(MethodProvider mp) throws InterruptedException;

    }

    private int getWildernessLevelWidget() {
        if (widgets.isVisible(90, 46)) {
            RS2Widget wildyLevel = getWidgets().getWidgetContainingText(90, "Level");
            String str = wildyLevel.getMessage();
            str = str.replaceAll("[^0-9]+", " ");
            return Integer.parseInt(str.trim());
        }
        return 0;
    }

    private void setWildernessLevel(int level) {
        wildyLevel = level;
    }

    private static int getWildernessLevel() {
        return wildyLevel;
    }

    public void onPaint(Graphics2D g) {
        Graphics2D cursor = (Graphics2D) g.create();
        Graphics2D paint = (Graphics2D) g.create();

        final long runTime = System.currentTimeMillis() - startTime;

        Point mP = getMouse().getPosition();
        Rectangle rect = pos1.getPolygon(getBot()).getBounds();
        Color tBlack = new Color(0, 0, 0, 128);
        paint.setFont(new Font("Arial", Font.PLAIN, 12));

        paint.setColor(tBlack);
        paint.fillRect(0, 255, 200, 80);
        paint.setColor(Color.WHITE);
        paint.drawRect(0, 255, 200, 80);
        paint.drawString("Local Players: " + getPlayers().getAll().size(), 5, 270);
        paint.drawString("Run Time: " + formatTime(runTime), 5, 285);
        paint.drawString("Magic XP gained: " + formatValue(getExperienceTracker().getGainedXP(Skill.MAGIC)), 5, 300);
        paint.drawString("Profit: " + formatValue(price.get() * itemCount) + " [" + String.format("%.2f", (getPerHour(itemCount)) * price.get()) + "/hr]", 5, 315);
        paint.drawString("State: " + state, 5, 330);

        cursor.setColor(Color.WHITE);
        cursor.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
        cursor.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);
        cursor.drawPolygon(pos1.getPolygon(getBot()));
        cursor.drawString("x", rect.x + (rect.width / 2), rect.y + (rect.height / 2));

        int index = -1, time;
        for (Position p : nat_runez) {
            index++;
            if (p != null) {
                if (times.get(index) > 1) {
                    time = 97 - (int) (System.currentTimeMillis() - (times.get(index))) / 1000;
                    if (time < 0) {
                        times.remove(index);
                        break;
                    }
                } else {
                    time = 0;
                }

                Point map = new MiniMapTileDestination(getBot(), p).getExactPoint();
                g.setColor(Color.ORANGE.brighter().brighter());
                g.draw(p.getPolygon(getBot()));
                g.setColor(new Color(0, 255, 255, 148));
                g.fill(p.getPolygon(getBot()));
                if (map != null) {
                    g.fill(new Rectangle(map.x, map.y, 5, 5));
                }
                g.setColor(Color.GREEN);

                if (map != null) {
                    g.drawString("~".concat(String.valueOf(time)), map.x, map.y);
                }
                g.drawString("~".concat(String.valueOf(time)),
                        p.getPolygon(getBot()).getBounds().x, p.getPolygon(getBot()).getBounds().y);

            }
        }
            /*
        for (Player p : getPlayers().getAll()) {
            if (p != null) {
                if (p.getAnimation() == 836) {
                    if (!positions.contains(p.getPosition())) {
                        positions.add(p.getPosition());
                        times.add(System.currentTimeMillis());
                    }
                }
            }
        }*/


    }


    private Optional<Integer> getPrice(int id) {
        Optional<Integer> price = Optional.empty();
        try {
            URL url = new URL("http://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + id);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            con.setUseCaches(true);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String[] data = br.readLine().replace("{", "").replace("}", "").split(",");
            br.close();
            price = Optional.of(Integer.parseInt(data[0].split(":")[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
    }

    private String formatValue(final long l) {
        return (l > 1_000_000) ? String.format("%.2fm", (double) (l / 1_000_000))
                : (l > 1000) ? String.format("%.1fk", (double) (l / 1000)) : l + "";
    }

    private String formatTime(final long ms) {
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60;
        m %= 60;
        h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private double getPerHour(double value) {
        if ((System.currentTimeMillis() - startTime) > 0) {
            return value * 3600000d / (System.currentTimeMillis() - startTime);
        } else {
            return 0;
        }
    }

    private void recountItems() {
        long amt = getInventory().getAmount("Nature rune");
        if (currentItemCount == -1)
            currentItemCount = amt;
        else if (amt < currentItemCount) {
            currentItemCount = amt;
        } else {
            itemCount += amt - currentItemCount;
            currentItemCount = amt;
        }
    }
}
