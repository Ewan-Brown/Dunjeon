package com.ewan.meworking.data.server.memory;

import com.ewan.meworking.data.server.data.Data;
import com.ewan.meworking.data.server.data.DataWrapper;
import com.ewan.meworking.data.server.event.ObservedEvent;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BasicMemoryBank extends DataSink {

    private static long MAX_EVENTS_CACHED = 100;

    private final Long ownerUUID;
    static Logger logger = LogManager.getLogger();
    @Getter
    private final List<MemoryBankListener> listeners = new ArrayList<>();

    public BasicMemoryBank(Long uuid){
        this(uuid, new ArrayList<>());
    }

    public BasicMemoryBank(){
        this(null, new ArrayList<>());
    }

    public BasicMemoryBank(Long uuid, List<Pairing<?, ? extends Data, ? extends KnowledgePackage<? , ?>>> pairings){
        this.ownerUUID = uuid;
        this.knowledgeDataPairings = pairings;
    }

    public void addListener(MemoryBankListener lis){
        listeners.add(lis);
    }

    public record Pairing<I, D extends Data, P extends KnowledgePackage<I,? extends D>>
            (ConcurrentHashMap<I, P> knowledgeMap, Class<D> relatedBaseDataClass){}

    //TODO Does it really make sense to store events in the memory bank
    private List<ObservedEvent> unprocessedEvents = new ArrayList<>();
    //Each pairing in this list is a for specific 'category' of knowledge - defined by the base Data class. For example EntityData/FloorData/CellData are 3 existing categories.
    private final List<Pairing<?, ? extends Data, ? extends KnowledgePackage<? , ?>>> knowledgeDataPairings;

    public void addEvent(ObservedEvent e){
        for (MemoryBankListener listener : listeners) {
            listener.processEvent(e);
        }
        if(unprocessedEvents.size() < MAX_EVENTS_CACHED){
            unprocessedEvents.add(e);
        }else{
            logger.warn("cache size reached, skipped adding event : " + e);
        }
    }

    public void clearEvents(){
        if(!unprocessedEvents.isEmpty()){
            logger.warn("Basic Memory Bank left with unprocessed events! You should probably clear those up at parent update");
        }
    }

    //Unwrap data to understand its context, and place it in the appropriate knowledge object
    @SuppressWarnings("unchecked")
    public <D extends Data, I, P extends KnowledgePackage<I, D>> void processWrappedData(DataWrapper<D, I> wrappedData){

        for (MemoryBankListener listener : listeners) {
            listener.processWrappedData(wrappedData);
        }
        Pairing<?, ?, ?> matchingPairing = knowledgeDataPairings.stream()
                .filter(pairing -> pairing.relatedBaseDataClass() == wrappedData.getBaseClass())
                .findFirst().orElse(null);

        if(matchingPairing == null){
            matchingPairing = new Pairing<>(new ConcurrentHashMap<>(), wrappedData.getBaseClass());
            knowledgeDataPairings.add(matchingPairing);
        }

        ConcurrentHashMap<I, P> hashMap = (ConcurrentHashMap<I, P>) matchingPairing.knowledgeMap;
        P relevantPackage = hashMap.get(wrappedData.getIdentifier());

        if(relevantPackage == null){
            relevantPackage = (P) new KnowledgePackage<I, D>(wrappedData.getIdentifier());
            hashMap.put(wrappedData.getIdentifier(), relevantPackage);
        }
        for (D datum : wrappedData.getData()) {
            KnowledgeFragment<D> fragment = new KnowledgeFragment<>(datum, null, wrappedData.getTimestamp());
            relevantPackage.register(fragment);
        }

    }

    //in order to keep access to this tree of data clean and safe accessors are the only way to read data.
    //************** ACCESSORS ***************//

    @SuppressWarnings("unchecked")
    public <I, D extends Data, P extends KnowledgePackage<I,D>> Optional<SingleQueryAccessor<I, D>> querySinglePackage(I identifier, Class<D> baseClazz, List<Class<? extends D>> requiredClasses){
        Optional<?> pairingOptional = knowledgeDataPairings.stream().filter(p -> p.relatedBaseDataClass() == baseClazz).findAny();
        if(pairingOptional.isPresent()) {
            Pairing<I, D, P> pairing = (Pairing<I, D, P>) pairingOptional.get();
            ConcurrentHashMap<I, P> knowledgeMap = pairing.knowledgeMap();
            P knowledgePackage = knowledgeMap.get(identifier);
            if (knowledgePackage == null) {
                return Optional.empty();
            }
            for (Class<? extends D> requiredClass : requiredClasses) {
                if (knowledgePackage.get(requiredClass) == null) {
                    return Optional.empty();
                }
            }
            return Optional.of(new SingleQueryAccessor<>(knowledgePackage, requiredClasses));
        }else{
            return Optional.empty();
        }

    }

    @SuppressWarnings("unchecked")
    public <I, D extends Data, P extends KnowledgePackage<I,D>> MultiQueryAccessor<I, D> queryMultiPackage(Class<D> baseClazz, List<Class<? extends D>> requiredClasses){
        Pairing<I, D, P> pairing = (Pairing<I, D, P>) knowledgeDataPairings.stream().filter(p -> p.relatedBaseDataClass == baseClazz).findFirst().orElseThrow();
        ConcurrentHashMap<I, P> hashMap = pairing.knowledgeMap();
        HashMap<I, SingleQueryAccessor<I, D>> individualAccessors = new HashMap<>();
        packages:
        for (P p : hashMap.values()) {
            for (Class<? extends D> requestedClass : requiredClasses) {
                if (p.get(requestedClass) == null){
                    continue packages;
                }
            }
            //All requestedClasses are valid! Add this package to the collection.
            SingleQueryAccessor<I, D> accessor = new SingleQueryAccessor<>(p, requiredClasses);
            individualAccessors.put(p.getIdentifier(), accessor);
        }

        return new MultiQueryAccessor<>(requiredClasses, individualAccessors);
    }

    public final class SingleQueryAccessor<I, D extends Data>{

        private final List<Class<? extends D>> requestedClasses;
        @Getter
        private final I identifier;
        private final KnowledgePackage<I,D> relatedPackage;

        private SingleQueryAccessor(KnowledgePackage<I,D> knowledgePackage, List<Class<? extends D>> requestedClasses) {
            this.requestedClasses = requestedClasses;
            relatedPackage = knowledgePackage;
            identifier = knowledgePackage.getIdentifier();
        }

        public <T extends D> Optional<KnowledgeFragment<T>> attemptGetKnowledge(Class<T> clazz){
            if(!requestedClasses.contains(clazz)){
                return Optional.empty();
            }else{
                return Optional.of(relatedPackage.get(clazz));
            }
        }

        public <T extends D> KnowledgeFragment<T> getKnowledge(Class<T> clazz){
            if(!requestedClasses.contains(clazz)){
                throw new IllegalArgumentException("Attempted to retrieve knowledge who's type was not part of requested classes: " + clazz.getName()+" requestedClasses: " + requestedClasses);
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

    public long getOwnerUUID(){
        if(ownerUUID == null){
            throw new RuntimeException("Client side Basic Memory Bank doesn't store owner UUID yet - use the FrameInfo packet instead");
        }else{
            return ownerUUID;
        }
    }

    public BasicMemoryBank getShallowClone(){
        BasicMemoryBank clone = new BasicMemoryBank();
        for (var knowledgeDataPairing : knowledgeDataPairings) {
            var newPairing = getShallowClone(knowledgeDataPairing);
            clone.knowledgeDataPairings.add(newPairing);
        }

        return clone;
    }


    //TODO Clean this up...
    @SuppressWarnings("unchecked") //trust
    private <I, D extends Data, P extends KnowledgePackage<I,D>> Pairing<?, ?, ?> getShallowClone(Pairing<?, ?, ?> pairing){
        Pairing<I, D, P> p2 = (Pairing<I, D, P>) pairing;
        final Class<D> baseClazz = p2.relatedBaseDataClass();
        ConcurrentHashMap<I, P> newMap = new ConcurrentHashMap<>();
        for (var entry : p2.knowledgeMap().entrySet()) {
            I identifier = entry.getKey();
            P newPackage = getShallowClone(entry.getValue());
            newMap.put(identifier, newPackage);
        }

        return new Pairing<>(newMap, baseClazz);
    }

    @SuppressWarnings("unchecked") //trust
    public <I, D extends Data, K extends KnowledgePackage<I, D>> K getShallowClone(K oldPackage){
        return (K) new KnowledgePackage<>(oldPackage.getIdentifier(),
                (HashMap<Class<? extends D>, KnowledgeFragment<? extends D>>) oldPackage.getDataMap().clone());
    }



}
