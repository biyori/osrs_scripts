package bot;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Kyle on 2016/12/20.
 */
public class MineTask extends Task {

    private Area dwarvenMines = new Area(3025, 9807, 3033, 9831);//dwarven mine area with iron + clay prospects
    private Area DM_westClay = new Area(3027, 9807, 3031, 9811);
    private Area DM_eastClay = new Area(3052, 9817, 3054, 9820);
    private Area DM_westIron = new Area(3031, 9825, 3033, 9827);
    private Area DM_eastIron = new Area(3052, 9821, 3054, 9828);
    private Area safeSpot = new Area(3016, 9845, 3023, 9850);
    private long nextHop = System.currentTimeMillis() / 1000;
    private int worldHopCount = 0, changeOreCount = 5;

    MineTask(MethodProvider api) {
        super(api);
    }

    @Override
    public boolean canProcess() {
        return getArea(Global.guiSelectOre()).contains(api.myPlayer()) && !api.inventory.isFull();
    }

    @Override
    public void process() {
        if (worldHopCount == changeOreCount) {
            String prospects[] = {"Clay (west)", "Clay (east)", "Iron (west)", "Iron (east)"};
            Random random = new Random();
            int index = random.nextInt(prospects.length);
            if (api.skills.getStatic(Skill.MINING) < 15) {
                while (prospects[index].startsWith("Iron"))
                    index = random.nextInt(prospects.length);
            }
            Global.guiSetOre(prospects[index]);
            worldHopCount = 0;
        }
        switch (Global.guiSelectOre()) {
            case "Clay (west)":
                mineRock(findRock(Rock.CLAY), DM_westClay, Rock.CLAY);
                //hop if there is more than 3 people around, every 1-5 minutes
                //shouldWeHop(playersInArea(DM_westClay), Global.getHopPlayerLim(), Global.getHopTimeMin());
                break;
            case "Clay (east)":
                mineRock(findRock(Rock.CLAY), DM_eastClay, Rock.CLAY);
                //hop if there is more than 3 people around, every 1-5 minutes
                //shouldWeHop(playersInArea(DM_eastClay), Global.getHopPlayerLim(), Global.getHopTimeMin());
                break;
            case "Iron (west)":
                mineRock(findRock(Rock.IRON), DM_westIron, Rock.IRON);

                //hop if there is more than 3 people around, every 1-5 minutes
                //shouldWeHop(playersInArea(DM_westIron), Global.getHopPlayerLim(), Global.getHopTimeMin());
                break;
            case "Iron (east)":
                mineRock(findRock(Rock.IRON), DM_eastIron, Rock.IRON);

                //hop if there is more than 3 people around, every 1-5 minutes
                //shouldWeHop(playersInArea(DM_eastIron), Global.getHopPlayerLim(), Global.getHopTimeMin());
                break;
        }
    }


    public enum Rock {
        CLAY(6705),
        COPPER(4645),
        TIN(53),
        IRON(2576),
        SILVER(74),
        COAL(10508),
        GOLD(8885),
        MITHRIL(-22239),
        ADAMANTITE(21662),
        RUNITE(-31437);

        private final short COLOUR;

        Rock(final int COLOUR) {
            this.COLOUR = (short) COLOUR;
        }
    }

    private RS2Object getRockWithOre(Rock rock) {

        return api.getObjects().closest(obj -> {

            short[] colours = obj.getDefinition().getModifiedModelColors();

            /*
             * Prefer rocks with no people
             */
            if (colours != null && peopleAroundEntity(obj, 1) <= 1) {

                for (short c : colours) {
                    if (c == rock.COLOUR) return true;
                }

                /*
                 * If no rocks with people can be found, settle for less
                 */
            } else if (colours != null) {

                for (short c : colours) {
                    if (c == rock.COLOUR) return true;
                }
            }
            return false;
        });
    }

    private RS2Object hoverRock(Rock rock, int x, int y) {

        return api.getObjects().closest(obj -> {

            short[] colours = obj.getDefinition().getModifiedModelColors();

            if (colours != null && obj.getX() != x && obj.getY() != y) {

                for (short c : colours) {

                    if (c == rock.COLOUR) return true;
                }
            }
            return false;
        });
    }

    private boolean isRockDead(Rock type, int x, int y) {
        List<RS2Object> obj = api.getObjects().get(x, y);
        RS2Object obj2 = obj.get(0);
        short[] colours = obj2.getDefinition().getModifiedModelColors();

        if (colours != null) {//in which cases is the colors[] more than one? Why not aways use x.getDefinition().getModifiedModelColors()[0];?
            for (short c : colours) {
                if (c == type.COLOUR)//add in portability to other rocks
                    return false;
            }
        }
        return true;
    }

    private RS2Object findRock(Rock type) {
        Predicate<RS2Object> suitableRock = rock ->
                rock != null &&
                        rock.hasAction("Mine") &&
                        rock.getDefinition().getModifiedModelColors() != null && //needed a null check before using it to find rocks
                        rock.getDefinition().getModifiedModelColors()[0] == type.COLOUR &&//TODO: Add specific rock checking such as iron/etc
                        peopleAroundEntity(rock, 1) < 1 &&//adjust accordingly the players around an entity
                        getArea(Global.guiSelectOre()).contains(rock);

        Predicate<RS2Object> secondBestRock = rock ->
                rock != null &&
                        rock.hasAction("Mine") &&
                        rock.getDefinition().getModifiedModelColors() != null &&
                        rock.getDefinition().getModifiedModelColors()[0] == type.COLOUR &&
                        getArea(Global.guiSelectOre()).contains(rock);

        List<RS2Object> rock = api.getObjects().getAll().stream().filter(suitableRock).collect(Collectors.toList());

        //paint rocks
        Global.setHighlightRocks(rock);

        if (rock != null && api.getObjects().getAll().stream().anyMatch(suitableRock)) {
            rock.sort(Comparator.<RS2Object>comparingInt(a -> api.getMap().realDistance(a))
                    .thenComparingInt(b -> api.getMap().realDistance(b)));
            api.log("Amount of players around rock: " + peopleAroundEntity(rock.get(0), 1) + " stream matches: " + rock.size());
            return rock.get(0);
        } else {
            api.log("No suitable rocks available, need to hop worlds! Downgrading rock selection...");
            if (nextHop < System.currentTimeMillis() / 1000) {
                api.worlds.hopToF2PWorld();
                nextHop = System.currentTimeMillis() / 1000 + random(600);
                api.log("Next hop in " + (nextHop - System.currentTimeMillis() / 1000) + " seconds");
                worldHopCount++;
            }

            rock = api.getObjects().getAll().stream().filter(secondBestRock).collect(Collectors.toList());

            //paint rocks
            Global.setHighlightRocks(rock);
            if (rock != null && api.getObjects().getAll().stream().anyMatch(secondBestRock)) {
                if (rock.size() > 1) {
                    rock.sort(Comparator.<RS2Object>comparingInt(a -> api.getMap().realDistance(a))
                            .thenComparingInt(b -> api.getMap().realDistance(b)));
                }
                api.log("Second best rock has " + peopleAroundEntity(rock.get(0), 1) + " people around it");
                return rock.get(0);
            }
            return null;
        }
    }


    private void mineRock(RS2Object rock, Area area, Rock type) {
        if (rock != null && !isRockDead(type, rock.getX(), rock.getY())) {
            afk(268, 290);
            if (!api.myPlayer().isAnimating() && rock.interact("Mine")) {
                api.log("Attempting to mine " + type.name().toLowerCase() + " id: " + rock.getId() + " players: " + peopleAroundEntity(rock, 1));

                if (type == Rock.IRON) {
                    RS2Object hover = hoverRock(type, rock.getX(), rock.getY());
                    if (hover != null)
                        hover.hover();
                }
                new ConditionalSleep(10_000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return isRockDead(type, rock.getX(), rock.getY());
                    }
                }.sleep();
            } else if (!rock.getArea(5).contains(api.myPlayer())) {
                api.log("Walking closer to rock...");
                api.walking.walk(rock);
            } else {
                api.log("Waiting for rocks...");
                afk(100, 200);
            }
        }

        /*
         * Only sleep on clay mining to stay in one spot
         */
        if (type == Rock.CLAY) {
            if (rock != null && isRockDead(type, rock.getX(), rock.getY())) {
                api.log("Rock ded...");
                afk(1000, 1200);
            }
        }

        if (api.myPlayer().isUnderAttack()) {
            api.log("We are under attack! Running to safe spot...");

            WalkingEvent webEvent = new WalkingEvent(safeSpot);
            webEvent.setEnergyThreshold(3);
            webEvent.setBreakCondition(new Condition() {
                @Override
                public boolean evaluate() {
                    if (!api.myPlayer().isUnderAttack()) {
                        api.log("MineTask: No longer under attack [condition met]");
                        walkToRock(area, type);
                        return true;
                    }
                    return false;
                }
            });
            api.execute(webEvent);
        }
    }

    private void walkToRock(Area rockArea, Rock RockType) {
        RS2Object rock = getRockWithOre(RockType);
        WalkingEvent webEvent = new WalkingEvent(rockArea);
        webEvent.setEnergyThreshold(5);
        webEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                if (rock.isVisible()) {
                    api.log("MineTask: Ore is visible [condition met]");
                    return true;
                } else if (api.camera.toEntity(rock)) {
                    if (rock.isVisible()) {
                        api.log("MineTask: Moving camera to ore [condition met]");
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
        api.execute(webEvent);
    }

    private int peopleAroundEntity(Entity e, int radius) {
        if (e.getArea(radius).contains(api.myPlayer()))
            return Math.toIntExact(api.players.getAll().stream().filter(x -> e.getArea(radius).contains(x)).count()) - 1;
        return Math.toIntExact(api.players.getAll().stream().filter(x -> e.getArea(radius).contains(x)).count());

    }


    private Area getArea(String area) {
        switch (area) {
            case "Clay (west)":
                return DM_westClay;
            case "Clay (east)":
                return DM_eastClay;
            case "Iron (west)":
                return DM_westIron;
            case "Iron (east)":
                return DM_eastIron;
            default:
                return dwarvenMines;
        }
    }

    private void afk(int min, int max) {
        try {
            sleep(random(min, max));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}