package hayase.AIODraynorLumJack.src;

/**
 * Created by Kyle on 2016/12/11.
 */

import org.osbot.rs07.script.MethodProvider;

public abstract class Task extends Main {

    MethodProvider api;

    Task(MethodProvider api) {
        this.api = api;
    }

    /**
     * Setting up conditions if the given task can process
     *
     * @return true if the conditions are met
     */
    public abstract boolean canProcess();

    /**
     * Execute the task process() function
     */
    public abstract void process();

    /**
     * Run the task code
     */
    void run() {

        /*
         * If the task meets the condition, run the task
         */
        if (canProcess())
            process();
    }
}