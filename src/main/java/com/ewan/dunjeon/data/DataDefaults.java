package com.ewan.dunjeon.data;

import com.ewan.dunjeon.data.Datas.QueryResult;
import com.ewan.dunjeon.data.Datas.QueryResult.QueryStatus;

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
    public static <D extends Data> QueryResult<D> getDefault(Class<D> clazz){
        var result = pairingMap.get(clazz);
        if(result == null){
            return new QueryResult<>(null, QueryStatus.MISSING);
        }else{
            return (QueryResult<D>) new QueryResult<>(result.get(), QueryStatus.SUCCESS);
        }
    }



}
