package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.Dunjeon;

@Deprecated
public class Memory {

    public Memory(double timestamp){
        isOldData = false;
        timeStamp = Dunjeon.getInstance().getTime();
    }

    private double timeStamp;

    /**
     * True if this is data that is not representational of the 'present' from the hosts' point of view.
     */
    protected boolean isOldData;

    /**
     * Read as "Is this thing currently visible/audible, or is this purely a memory"
     * @return
     */
    public boolean isOldData(){return isOldData;}

    public void setOldData(){isOldData = true;}

    public double getTimeStamp() {
        return timeStamp;
    }
}
