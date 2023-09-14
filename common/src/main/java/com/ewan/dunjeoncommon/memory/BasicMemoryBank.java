package com.ewan.dunjeoncommon.memory;

import com.esotericsoftware.kryo.kryo5.serializers.FieldSerializer;
import com.ewan.dunjeon.server.world.CellPosition;
//import com.ewan.dunjeon.server.world.entities.memory.celldata.CellKnowledge;
import com.ewan.dunjeoncommon.data.Data;
import com.ewan.dunjeoncommon.data.DataWrapper;
import com.ewan.dunjeoncommon.data.Datas;
import com.ewan.dunjeoncommon.memory.creaturedata.CreatureKnowledge;
import com.ewan.dunjeoncommon.data.CellKnowledge;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class BasicMemoryBank extends DataSink {

    private final ConcurrentHashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<CellPosition, CellKnowledge> cellKnowledgeHashMap = new ConcurrentHashMap<>();

    private record Pairing<I, D extends Data, K extends KnowledgePackage<I,? extends D>>
            (ConcurrentHashMap<I, K> knowledgeMap, Class<D> relatedBaseDataClass, Function<I,K> knowledgeProducer){}

    @FieldSerializer.Optional("")
    final List<Pairing<?, ? extends Data, ? extends KnowledgePackage<? , ?>>> knowledgeDataPairings = new ArrayList<>();

    {
        knowledgeDataPairings.add(new Pairing<>(creatureKnowledgeHashMap, Datas.EntityData.class, CreatureKnowledge::new));
        knowledgeDataPairings.add(new Pairing<>(floorKnowledgeHashMap, Datas.FloorData.class, FloorKnowledge::new));
        knowledgeDataPairings.add(new Pairing<>(cellKnowledgeHashMap, Datas.CellData.class, CellKnowledge::new));
    }

    //Unwrap data to figure out its context, and place it in the appropriate knowledge object
    @SuppressWarnings("unchecked")
    public <T extends Data, I, P extends KnowledgePackage<I,T>> void processWrappedData(DataWrapper<T, I> wrappedData){

        for (Pairing<?, ?, ?> knowledgeDataPairing : knowledgeDataPairings) {
            if(wrappedData.getBaseClass() == knowledgeDataPairing.relatedBaseDataClass){
                ConcurrentHashMap<I, P> hashMap = (ConcurrentHashMap<I, P>) knowledgeDataPairing.knowledgeMap;
                P relevantPackage = hashMap.get(wrappedData.getIdentifier());

                if(relevantPackage == null){
                    Function<I, P> knowledgePackageProducer = (Function<I, P>) knowledgeDataPairing.knowledgeProducer;
                    relevantPackage = knowledgePackageProducer.apply(wrappedData.getIdentifier());
                    hashMap.put(wrappedData.getIdentifier(), relevantPackage);
                }
                for (T datum : wrappedData.getData()) {
                    KnowledgeFragment<T> fragment = new KnowledgeFragment<>(datum, null, wrappedData.getTimestamp());
                    relevantPackage.register(fragment);
                }

                break;
            }
        }
    }

    //in order to keep access to this tree of data clean and safe accessors are the only way to read data.
    //************** ACCESSORS ***************//


    @SuppressWarnings("unchecked")
    public <I, D extends Data, K extends KnowledgePackage<I,D>> QueryResult<SingleQueryAccessor<I, D>, Boolean> querySinglePackage(I identifier, Class<D> baseClazz, List<Class<? extends D>> requiredClasses){
        Pairing<I, D, K> pairing = (Pairing<I, D, K>) knowledgeDataPairings.stream().filter(p -> p.relatedBaseDataClass() == baseClazz).findAny().get();
        ConcurrentHashMap<I, K> knowledgeMap =  pairing.knowledgeMap();
        K knowledgePackage = knowledgeMap.get(identifier);
        if(knowledgePackage == null){
            return new QueryResult<>(null, false);
        }
        for (Class<? extends D> requiredClass : requiredClasses) {
            if(knowledgePackage.get(requiredClass) == null){
                return new QueryResult<>(null, false);
            }
        }

        return new QueryResult<>(new SingleQueryAccessor<>(knowledgePackage, requiredClasses), true);

    }

    @SuppressWarnings("unchecked")
    public <I, D extends Data, K extends KnowledgePackage<I,D>> MultiQueryAccessor<I, D> queryMultiPackage(Class<D> baseClazz, List<Class<? extends D>> requiredClasses){
        Pairing<I, D, K> pairing = (Pairing<I, D, K>) knowledgeDataPairings.stream().filter(p -> p.relatedBaseDataClass == baseClazz).findFirst().orElseThrow();
        ConcurrentHashMap<I, K> hashMap = pairing.knowledgeMap();
        HashMap<I, SingleQueryAccessor<I, D>> individualAccessors = new HashMap<>();
        packages:
        for (K k : hashMap.values()) {
            for (Class<? extends D> requestedClass : requiredClasses) {
                if (k.get(requestedClass) == null){
                    continue packages;
                }
            }
            //All requestedClasses are valid! Add this package to the collection.
            SingleQueryAccessor<I, D> accessor = new SingleQueryAccessor<>(k, requiredClasses);
            individualAccessors.put(k.getIdentifier(), accessor);
        }

        return new MultiQueryAccessor<>(requiredClasses, individualAccessors);
    }

    public final class SingleQueryAccessor<I, D extends Data>{

        private final List<Class<? extends D>> requestedClasses;
        @Getter
        I identifier;
        KnowledgePackage<I,D> relatedPackage;

        private SingleQueryAccessor(KnowledgePackage<I,D> knowledgePackage, List<Class<? extends D>> requestedClasses) {
            this.requestedClasses = requestedClasses;
            relatedPackage = knowledgePackage;
            identifier = knowledgePackage.getIdentifier();
        }

        public <T extends D> KnowledgeFragment<T> getKnowledge(Class<T> clazz){
            if(!requestedClasses.contains(clazz)){
                throw new IllegalArgumentException("Attempted to retrieve knowledge who's type was not part of requested classes: " + clazz.getName());
            }else{
                return relatedPackage.get(clazz);
            }
        }


    }

    public final class MultiQueryAccessor<I, D extends Data> {
        private final List<Class<? extends D>> requestedClasses;
        @Getter
        private final HashMap<I, SingleQueryAccessor<I, D>> individualAccessors;

        MultiQueryAccessor(List<Class<? extends D>> requestedClasses, HashMap<I, SingleQueryAccessor<I, D>> accessors){
            this.requestedClasses = requestedClasses;
            individualAccessors = accessors;
        }

    }

    public record QueryResult<A, S>(A result, S status){

    }
}
