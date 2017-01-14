package hayase.AIODraynorLumJack.src;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
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
         * Survival against dark mages and jail guards
         */
        if (api.myPlayer().isUnderAttack()) {

            /*
             * People like to lure dark wizards inside Draynor bank--run away from those mages
             */
            if (Banks.DRAYNOR.contains(api.myPlayer())) {
                api.log("Under attack! Running to a safe spot...");

                //Safe spot near abby witch
                WebWalkEvent runEvent = new WebWalkEvent(new Area(3090, 3263, 3094, 3257));
                runEvent.setEnergyThreshold(1);
                api.execute(runEvent);

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
            } else {

                /*
                 * If we are still under attack and not in the bank, it must be from the jail guard
                 * Run as fast as we can to the bank
                 */
                api.log("Under attack! Running to the bank");
                WebWalkEvent runEvent = new WebWalkEvent(Banks.DRAYNOR);
                runEvent.setEnergyThreshold(1);
                api.execute(runEvent);
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