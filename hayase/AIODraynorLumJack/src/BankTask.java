package hayase.AIODraynorLumJack.src;

/**
 * Created by Kyle on 2016/12/11.
 */

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

public class BankTask extends Task {

    private Area draynorBankRange = new Area(3092, 3242, 3090, 3245);
    private final BankClass bonk;

    BankTask(MethodProvider api) {
        super(api);
        bonk = new BankClass(api);
    }

    /**
     * The BankTask will run only when the inventory is full
     *
     * @return true if the inventory is full
     */
    @Override
    public boolean canProcess() {
        return api.getInventory().isFull();
    }

    @Override
    public void process() {
        api.log("Inventory full. Walking to bank!");

        WebWalkEvent webEvent = new WebWalkEvent(draynorBankRange);
        webEvent.setEnergyThreshold(5);
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
                if (!api.bank.isOpen() && !api.myPlayer().isUnderAttack()) {

                    /*
                     * If the script detected our axe purchase offer went through--open the collection window and
                     * collect our axes to bank
                     */
                    if (Constants.collectAxeAtBank()) {
                        bonk.openCollectWindow(banker);
                        bonk.collectAxes();
                    }
                    /*
                     * Instead of using the same bank styles--spice it up
                     *
                     * After opening the bank, grab the best axe we can use
                     */
                    switch (random(1, 3)) {
                        case 1:
                            api.log("Banking case 1");
                            bonk.bankWithBanker(banker, "Bank");
                            bonk.depositLogs();
                            break;
                        case 2:
                            api.log("Banking case 2");
                            bonk.bankWithBooth(bankBooth, "Bank");
                            bonk.depositLogs();
                            break;
                        case 3:
                            api.log("Banking case 3");
                            bonk.bankWithBanker(banker, "Talk-to");
                            bonk.depositLogs();
                            break;

                        //There is no need for a default statement since the switch range is defined (1,3)
                        //default:
                    }

                    /*
                     * If the bank is already open--depositLogs();
                     */
                } else if (api.bank.isOpen()) {
                    bonk.depositLogs();
                }
            } else {

                /*
                 * The bank booth is not visible--move camera until we can see it
                 */
                api.camera.toEntity(bankBooth);
            }
        }
    }
}