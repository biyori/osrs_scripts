package bot;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.Widgets;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Kyle on 2016/12/30.
 */
class BankClass {
    MethodProvider api;

    BankClass(MethodProvider ex) {
        this.api = ex;
    }

    /**
     * A simple function to interact with a bank booth
     *
     * @param booth       the booth to interact with
     * @param interaction the action to the booth
     */
    void bankWithBooth(RS2Object booth, String interaction) {
        if (booth.interact(interaction)) {
            //Force a delay between actions (this afk might be unnecessary)
            afk(777, 1111);

            //If the interaction was successful, call the openBank function
            openBank();
        }
    }

    /**
     * A simple function to interact with a bank booth
     *
     * @param banker      the banker to interact with
     * @param interaction the action to the banker
     */
    void bankWithBanker(NPC banker, String interaction) {
        if (banker.interact(interaction)) {
            //Force a delay between actions (same as above this afk might be unnecessary)
            afk(777, 1111);

            //If the interaction was successful, call the openBank function
            openBank();
        }
    }

    /**
     * Private function to handle opening the bank
     * <p>
     * Detects bank dialogues, bank pin windows, and handles collecting completed purchases from the ExchangeTask
     */
    private void openBank() {

        /*
         * Create one widget object
         */
        Widgets widget = api.getWidgets();

        api.log("Attempting to bank");

        /*
         * NPC banker dialogue is visible
         */
        if (widget.isVisible(231)) {
            api.log("Bank chat is visible!");
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.dialogues.inDialogue();
                }
            }.sleep();

            /*
             * Handle the banker dialogue
             */
            bankerTalk();
        }

        /*
         * Bank PIN widget is visible
         */
        if (widget.isVisible(213)) {
            api.log("Bank PIN window open. Waiting for RandomSolver...");

            /*
             * If the widget is visible, sleep until the RandomSolver enters the PIN
             */
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.bank.isOpen();
                }
            }.sleep();
        }

        /*
         * Bank PIN widget is not visible, so just sleep until the bank is open
         */
        new ConditionalSleep(5_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return api.bank.isOpen() || isStuckAtEdgeBank();
            }
        }.sleep();
    }

    /**
     * A banker interaction function to open the collection window
     *
     * @param banker the banker to interact with
     */
    void openCollectWindow(NPC banker) {

        /*
         * Interact with the banker and open the collect widget
         */
        if (banker.interact("Collect")) {

            /*
             * Sleep until the collection widget is visible
             */
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.getWidgets().isVisible(402);
                }
            }.sleep();
        }
    }

    /**
     * The function tries to deposit logs, and then check the bank for a better axe to use
     */
    void depositOre() {
        if (api.getBank() != null) {
            if (api.bank.isOpen()) {
                afk(200, 500);
                api.log("Depositing our logs");
                depositItems();
                //Tell ExchangeTask to go buy some axes
           /*     if (Constants.progressiveMode() && Constants.sellLogsAtGE())
                    needToBuyAxes();
                upgradeAxe();*/
            } else {
                api.log("Failed to bank");
            }
        }
    }

    /**
     * The function will set the bank mode to withdraw notes, and proceed to take out logs, and oak logs if there is enough
     * quest points to trade them
     */
    void getLogs() {
        if (api.getBank() != null) {
            if (api.bank.isOpen()) {
                afk(200, 500);
                api.bank.enableMode(Bank.BankMode.WITHDRAW_NOTE);
                api.log("Searching logs...");
                if (api.bank.withdrawAll("Logs")) {
                    api.log("Found logs");
                    new ConditionalSleep(5_000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return api.inventory.contains("Logs");
                        }
                    }.sleep();
                }
               /* if (api.quests.getQuestPoints() > 6 && api.bank.withdrawAll("Oak logs")) {
                    api.log("Found oak logs");
                    new ConditionalSleep(5_000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return api.inventory.contains("Oak logs");
                        }
                    }.sleep();
                }*/
                api.bank.close();
            } else {
                api.log("Failed to bank.");
            }
        }
    }


    /**
     * Deposit all items except our woodcutting axes
     * <p>
     * This function also counts the total logs in the bank to display on the paint
     */
    private void depositItems() {
        api.bank.depositAllExcept(Global.pickaxes);
        new ConditionalSleep(5_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return api.inventory.isEmptyExcept(Global.pickaxes);
            }
        }.sleep();
        Global.setTotalOres(api.bank.getItem(whatOre()).getAmount());
    }

    private String whatOre() {
        switch (Global.guiSelectOre()) {
            case "Clay (west)":
                return "Clay";
            case "Clay (east)":
                return "Clay";
            case "Iron (west)":
                return "Iron ore";
            case "Iron (east)":
                return "Iron ore";
            default:
                return "Clay";
        }
    }

    private boolean isStuckAtEdgeBank() {
        return api.myPlayer().getX() == 3094 && api.myPlayer().getY() == 3492;
    }

    /**
     * This function detects bank dialogues and if dialog is open--process the options
     */
    private void bankerTalk() {
        if (api.dialogues.isPendingContinuation()) {
            api.log("Dialog open! Clicking continue...");
            afk(300, 1000);
            api.dialogues.clickContinue();
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.dialogues.isPendingOption();
                }
            }.sleep();
        }
        if (api.dialogues.isPendingOption()) {
            api.log("Dialog open! Selecting option one to open bank.");
            afk(300, 1000);
            api.dialogues.selectOption(1); //First choice "...access bank account please..."
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.bank.isOpen();
                }
            }.sleep();
        }
    }

    /**
     * A function to simulate AFK with some constraints
     *
     * @param min minimum about of time to afk in milliseconds
     * @param max maximum about of time to afk in milliseconds
     */
    private void afk(int min, int max) {
        try {
            MethodProvider.sleep(MethodProvider.random(min, max));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to deposit everything including equipment
     */
    void depositAll() {
        api.bank.depositAll();
        new ConditionalSleep(5_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return api.inventory.isEmpty();
            }
        }.sleep();
        api.bank.depositWornItems();
        new ConditionalSleep(5_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return api.equipment.isEmpty();
            }
        }.sleep();
    }
}