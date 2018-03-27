package bot;

/**
 * Created by Kyle on 2016/12/11.
 */

import org.osbot.rs07.script.MethodProvider;

public abstract class Task extends Main {

    MethodProvider api;

    Task(MethodProvider api) {
        this.api = api;
    }

    public abstract boolean canProcess();

    public abstract void process();

    void run() {
        if (canProcess())
            process();
    }
}