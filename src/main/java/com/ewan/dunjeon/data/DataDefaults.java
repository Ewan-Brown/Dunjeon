package com.ewan.dunjeon.data;

import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank.QueryResult;

import java.util.HashMap;
import java.util.function.Supplier;

public class DataDefaults {

    public record DefaultPairing<D extends Data>(Class<D> dataClass, Supplier<D> defaultSupplier){};
    private static final HashMap<Class<? extends Data>, Supplier<? extends Data>> pairingMap = new HashMap<>();

    private static <D extends Data> void registerPairing(DefaultPairing<D> pairing){
        pairingMap.put(pairing.dataClass, pairing.defaultSupplier);
    }

    {
//        registerPairing(new DefaultPairing<Data>(Entity));
    }

    @SuppressWarnings("unchecked")
    public static <D extends Data> QueryResult<D, Boolean> getDefault(Class<D> clazz){
        var result = pairingMap.get(clazz);
        if(result == null){
            return new QueryResult<>(null, false);
        }else{
            return new QueryResult<>((D)result.get(), true);
        }
    }



}
