package com.ewan.meworking.data.server.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Unused for now, but will probably be useful
 */
public class DataDefaults {

    static Logger logger = LogManager.getLogger();
    public record DefaultPairing<D extends Data>(Class<D> dataClass, Supplier<D> defaultSupplier){}
    private static final HashMap<Class<? extends Data>, Supplier<? extends Data>> pairingMap = new HashMap<>();

    private static <D extends Data> void registerPairing(DefaultPairing<D> pairing){
        pairingMap.put(pairing.dataClass, pairing.defaultSupplier);
    }

    @SuppressWarnings("unchecked")
    public static <D extends Data> Optional<D> getDefault(Class<D> clazz){
        var result = pairingMap.get(clazz);
        if(result == null){
            return Optional.empty();
        }else{
            return Optional.of((D)result.get());
        }
    }



}
