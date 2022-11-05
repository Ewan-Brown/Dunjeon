package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.world.World;

public class Memory {

    public Memory(){
        isOldData = false;
    }

    private boolean isOldData;

    public boolean isOldData(){return isOldData;}

    public void setOldData(){isOldData = true;}

}
