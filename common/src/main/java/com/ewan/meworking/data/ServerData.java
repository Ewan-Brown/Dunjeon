package com.ewan.meworking.data;

import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import lombok.*;

public final class ServerData {
    @Getter
    @Setter
    private BasicMemoryBank basicMemoryBank;

    public ServerData(BasicMemoryBank basicMemoryBank) {
        this.basicMemoryBank = basicMemoryBank;
    }
    public ServerData(){
        System.out.println("No Arg ServerData constructor called! DELETE ME!");
        this.basicMemoryBank = new BasicMemoryBank(0);}


}