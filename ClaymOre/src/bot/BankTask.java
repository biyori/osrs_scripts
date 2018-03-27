package bot;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class BankTask extends Task {

    private Area edge_bank_range = new Area(3093, 3489, 3094, 3492);
    private final BankClass bonk;

    BankTask(MethodProvider api) {
        super(api);
        bonk = new BankClass(api);
    }

    @Override
    public boolean canProcess() {
        return api.getInventory().isFull();
    }

    @Override
    public void process() {
        //pause the painter
        Global.setHighlightRocks(null);
      /*  new Thread(() -> {
            if (Banks.EDGEVILLE.contains(api.myPlayer()))
                try {
                    api.bank.open();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }).start();*/
        WebWalkEvent webEvent = new WebWalkEvent(edge_bank_range);
        webEvent.setEnergyThreshold(5);
        webEvent.useSimplePath();
        webEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return Banks.EDGEVILLE.contains(api.myPlayer());
            }
        });

        api.log("Inventory full. Walking to bank!");
        api.execute(webEvent);


        RS2Object bankBooth = api.objects.closest(Banks.EDGEVILLE, "Bank booth");
        NPC banker = api.npcs.closest(Banks.EDGEVILLE, "Banker");
        if (bankBooth != null) {
            if (bankBooth.isVisible()) {
                if (!api.bank.isOpen()) {
                    /*
                     * Instead of using the same bank styles--spice it up
                     *
                     * After opening the bank, grab the best axe we can use
                     */
                    switch (random(1, 3)) {
                        case 1:
                            api.log("Banking case 1");
                            bonk.bankWithBanker(banker, "Bank");
                            bonk.depositOre();
                            break;
                        case 2:
                            api.log("Banking case 2");
                            bonk.bankWithBooth(bankBooth, "Bank");
                            bonk.depositOre();
                            break;
                        case 3:
                            api.log("Banking case 3");
                            bonk.bankWithBanker(banker, "Talk-to");
                            bonk.depositOre();
                            break;

                        //There is no need for a default statement since the switch range is defined (1,3)
                        //default:
                    }
                } else if (api.bank.isOpen())
                    bonk.depositOre();
            } else {
                api.camera.toEntity(bankBooth);
            }
        }
    }

    private void isTimeToMule() {
        if (Global.shouldMule()) {
            if (Global.getTotalOres() >= Global.getMuleOreLim()) {
                Global.timeToTradeMule(true);
                tellMuleLogin(api.worlds.getCurrentWorld());
            }
        }
    }

    private int tellMuleLogin(int world) {
        int muleStatus = -1;
        try {
            URL url = new URL("//mule.php?set_status=1&world=" + world);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            con.setUseCaches(true);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json = br.readLine();
            br.close();
            muleStatus = Integer.parseInt(json);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return muleStatus;
    }
}