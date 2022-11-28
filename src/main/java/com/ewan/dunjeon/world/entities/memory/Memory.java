package com.ewan.dunjeon.world.entities.memory;

import com.ewan.dunjeon.world.World;

public class Memory {

    public Memory(){
        isOldData = false;
        timeStamp = World.getInstance().getTime();
    }
    private float timeStamp;

    protected boolean isOldData;

    /**
     * Read as "Is this thing currently visible/audible, or is this purely a memory"
     * @return
     */
    public boolean isOldData(){return isOldData;}

    public void setOldData(){isOldData = true;}

    public float getTimeStamp() {
        return timeStamp;
    }
}
