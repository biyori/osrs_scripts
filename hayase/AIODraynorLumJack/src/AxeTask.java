package hayase.AIODraynorLumJack.src;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Kyle on 2016/12/29.
 */
public class AxeTask extends Task {
    private Area draynorBankRange = new Area(3092, 3242, 3090, 3245);
    private final BankClass bonk;

    AxeTask(MethodProvider api) {
        super(api);
        bonk = new BankClass(api);
    }

    /**
     * The AxeTask runs only when we have no axe in our inventory or equipment
     * <p>
     * Is the bot wielding an axe, have an axe in the inventory, or perhaps both?
     *
     * @return Since it returns true, we need to flip that statement and just check !true to process that we have no axe
     */
    @Override
    public boolean canProcess() {
        return !((!api.equipment.contains(Constants.woodAxes) && api.inventory.contains(Constants.woodAxes)) ||
                (!api.inventory.contains(Constants.woodAxes) && api.equipment.contains(Constants.woodAxes)) ||
                (api.inventory.contains(Constants.woodAxes) && api.equipment.contains(Constants.woodAxes)));
    }

    @Override
    public void process() {

        /*
        * Occasionally after hopping worlds, we lose information about what is inside our inventory and equipment
        *
        * Check out inventory & equipment before determining if we really need to find an axe in our bank
        */
        if (!api.tabs.getOpen().equals(Tab.EQUIPMENT)) {
            api.tabs.open(Tab.EQUIPMENT);
        }
        try {
            MethodProvider.sleep(random(1000, 2000));
        } catch (Exception e) {
            api.log("Error: " + e);
        }
        api.tabs.open(Tab.INVENTORY);

        /*
         * We live in Draynor, so lets use the Draynor bank once again
         */
        api.log("Where is our axe? Walking to bank!");
        WebWalkEvent webEvent = new WebWalkEvent(draynorBankRange);
        webEvent.setEnergyThreshold(5);
        webEvent.useSimplePath();
        webEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return Banks.DRAYNOR.contains(api.myPlayer());
            }
        });
        webEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return api.myPlayer().isUnderAttack();
            }
        });
        api.execute(webEvent);

        /*
         * People like to lure dark wizards inside Draynor bank--run away from those mages
         */
        if (api.myPlayer().isUnderAttack()) {
            api.log("Under attack! Running to a safe spot...");

            //Safe spot near abby witch
            Area area = new Area(3090, 3263, 3094, 3257);

            //Walk to the safe area
            api.walking.walk(area);

            //Sleep until we are out of combat
            new ConditionalSleep(10_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return !api.myPlayer().isUnderAttack();
                }
            }.sleep();

            //Lets hop worlds to get away from the bank mages
            if (api.worlds.hopToF2PWorld()) {
                api.log("Hopping worlds...");
            }
        }

        /*
         * Select the closest booths and bankers in the bank
         */
        RS2Object bankBooth = api.objects.closest(draynorBankRange, "Bank booth");
        NPC banker = api.npcs.closest(draynorBankRange, "Banker");

        /*
         * If the booth exists, so must the bankers behind those booths
         */
        if (bankBooth != null) {
            if (bankBooth.isVisible()) {

                /*
                 * If the bank is not open, start trying to open the bank
                 */
                if (!api.bank.isOpen()) {

                    /*
                     * Instead of using the same bank styles--spice it up
                     *
                     * After opening the bank, grab the best axe we can use
                     */
                    switch (random(1, 3)) {
                        case 1:
                            api.log("Banking with banker");
                            bonk.bankWithBanker(banker, "Bank");
                            bonk.getAxe();
                            break;
                        case 2:
                            api.log("Banking with booth");
                            bonk.bankWithBooth(bankBooth, "Bank");
                            bonk.getAxe();
                            break;
                        case 3:
                            api.log("Talking to banker");
                            bonk.bankWithBanker(banker, "Talk-to");
                            bonk.getAxe();
                            break;

                        //There is no need for a default statement since the switch range is defined (1,3)
                        //default:
                    }
                } else {
                    api.log("Bank is open");
                }
            } else {
                api.camera.toEntity(bankBooth);
            }
        }
    }
}