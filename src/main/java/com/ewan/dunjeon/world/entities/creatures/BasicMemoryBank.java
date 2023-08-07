package com.ewan.dunjeon.world.entities.creatures;

import com.ewan.dunjeon.data.*;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.memory.FloorKnowledge;
import com.ewan.dunjeon.world.entities.memory.KnowledgeFragment;
import com.ewan.dunjeon.world.entities.memory.KnowledgePackage;
import com.ewan.dunjeon.world.entities.memory.celldata.CellKnowledge;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import com.ewan.dunjeon.data.Datas.QueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class BasicMemoryBank extends AbstractMemoryBank{

    private final ConcurrentHashMap<Long, CreatureKnowledge> creatureKnowledgeHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, FloorKnowledge> floorKnowledgeHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<WorldUtils.CellPosition, CellKnowledge> cellKnowledgeHashMap = new ConcurrentHashMap<>();
    private final List<Event<?>> eventList = new ArrayList<>();

    private record Pairing<I, D extends Data, K extends KnowledgePackage<I,? extends D>>
            (ConcurrentHashMap<I, K> knowledgeMap, Class<D> relatedBaseDataClass, Function<I,K> knowledgeProducer){}

    final List<Pairing<?, ?, ?>> knowledgeDataPairings = new ArrayList<>();

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
                    KnowledgeFragment<T> fragment = new KnowledgeFragment<>(datum, wrappedData.getSourceSensor(), wrappedData.getTimestamp());
                    relevantPackage.register(fragment);
                }

                break;
            }
        }
    }

    public <D extends Data, T extends D, I> List<I> getIdentifiersForAllValid(Class<D> baseClass, List<Class<? extends D>> requiredNonNull){
        ConcurrentHashMap<I, KnowledgePackage<I, D>> dataPackageMap = getDataPackageMap(baseClass);
        List<I> identifiers = new ArrayList<>();
        dataPackageMap.values().stream().filter(itKnowledgePackage -> checkIfValid(itKnowledgePackage, requiredNonNull)).
                forEach(itKnowledgePackage -> identifiers.add(itKnowledgePackage.getIdentifier()));
        return identifiers;
    }
    
    public <D extends Data, I> boolean checkIfValid(Class<D> baseClass, I identifier, List<Class<? extends D>> requiredNonNull){
        if(knowledgeDataPairings.stream().noneMatch(pairing -> pairing.relatedBaseDataClass == baseClass)){
            return false;
        }else{
            for (Class<? extends D> clazz : requiredNonNull) {
                var frag = getDataFragment(clazz, identifier);
                if(frag == null){
                    return false;
                }
            }
        }
        return true;
    }

    public <D extends Data, I, T extends D> boolean checkIfValid(KnowledgePackage<I, T> knowledgePackage, List<Class<? extends D>> requiredNonNull){
        for (Class<? extends D> clazz : requiredNonNull) {
            var frag = getDataFragment(clazz, knowledgePackage.getIdentifier());
            if(frag == null){
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private <D extends Data, I> ConcurrentHashMap<I, KnowledgePackage<I, D>> getDataPackageMap(Class<D> baseClass){
        var result =  knowledgeDataPairings.stream().filter(pairing -> pairing.relatedBaseDataClass == baseClass).toList();
        if(result.isEmpty()){
            throw new RuntimeException("No knowledgeDataPairings found for base class: " + baseClass);
        }else{
            return (ConcurrentHashMap<I, KnowledgePackage<I,D>>) result.get(0).knowledgeMap();
        }
    }

//    @SuppressWarnings("unchecked")
//    public <D extends Data, T extends D, I> KnowledgePackage<I, D> getDataPackage(Class<D> baseClass, I identifier){
//        return (KnowledgePackage<I, D>) getDataPackageMap(baseClass).get(identifier);
//    }

    @SuppressWarnings("unchecked")
    private <D extends Data, T extends D, I> QueryResult<KnowledgeFragment<T>> getDataFragment(Class<T> fragmentType, I identifier){

        Class<D> baseClass = (Class<D>) fragmentType.getSuperclass();
        ConcurrentHashMap<I, KnowledgePackage<I,D>> knowledgeMap = getDataPackageMap(baseClass);
        KnowledgePackage<I,D> knowledgePackage = knowledgeMap.get(identifier);
        if(knowledgePackage == null){
            return new QueryResult<>(null, QueryResult.QueryStatus.MISSING);
        }else{
            return new QueryResult<>(knowledgePackage.get(fragmentType), QueryResult.QueryStatus.SUCCESS);
        }
    }

    @SuppressWarnings("unchecked")
    public <D extends Data, T extends D, I> QueryResult<KnowledgeFragment<T>> getDataFragmentWithDefault(Class<T> fragmentType, I identifier){

        Class<D> baseClass = (Class<D>) fragmentType.getSuperclass();
        ConcurrentHashMap<I, KnowledgePackage<I,D>> knowledgeMap = getDataPackageMap(baseClass);
        KnowledgePackage<I,D> knowledgePackage = knowledgeMap.get(identifier);
        if(knowledgePackage == null){
            var defaultValue = DataDefaults.getDefault(fragmentType);
            if(defaultValue.status() == QueryResult.QueryStatus.SUCCESS){
                return new QueryResult<>(new KnowledgeFragment<>(defaultValue.result(), null, null), QueryResult.QueryStatus.SUCCESS);
            }
            return new QueryResult<>(null, QueryResult.QueryStatus.MISSING);
        }else{
            return new QueryResult<>(knowledgePackage.get(fragmentType), QueryResult.QueryStatus.SUCCESS);
        }
    }

    public void processEventData(Event e){
        eventList.add(e);
    }



}
