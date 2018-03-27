package bot;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Kyle on 2016/12/12.
 */
public class WalkTask extends Task {
    private Area faladorStatueArea = new Area(2971, 3415, 2983, 3412);
    private Area DwarvenMineWorkingArea = new Area(3025, 9807, 3033, 9830);

    private Area DM_westClay = new Area(3027, 9807, 3031, 9811);
    private Area DM_eastClay = new Area(3052, 9817, 3054, 9820);
    private Area DM_westIron = new Area(3031, 9825, 3033, 9827);
    private Area DM_eastIron = new Area(3052, 9821, 3054, 9828);

    WalkTask(MethodProvider api) {
        super(api);
    }

    @Override
    public boolean canProcess() {
        return !getArea(Global.guiSelectOre()).contains(api.myPlayer()) && !api.inventory.isFull(); //&& !Global.timeToTradeMule();
    }

    @Override
    public void process() {
        api.log("Walking to " + Global.guiSelectOre());
        WebWalkEvent webEvent = new WebWalkEvent(getArea(Global.guiSelectOre()));
        webEvent.setEnergyThreshold(5);
        webEvent.useSimplePath();
        webEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                if (new Area(3014, 3336, 3024, 3342).contains(api.myPlayer())) {
                    api.log("We cannot use the mining guild just yet! Redirecting path...");
                    WebWalkEvent webEvent2 = new WebWalkEvent(faladorStatueArea);
                    webEvent2.setEnergyThreshold(5);
                    webEvent2.useSimplePath();
                    api.execute(webEvent2);
                }
                if (getArea(Global.guiSelectOre()).contains(api.myPlayer())) {
                    api.log("WalkTask: We have arrived!");
                    return true;
                } else
                    return false;
            }
        });
        api.execute(webEvent);
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
                return DwarvenMineWorkingArea;
        }
    }
}