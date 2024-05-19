package com.ewan.dunjeon.server.world.entities.ai;

import com.ewan.dunjeon.server.generation.PathFinding;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.meworking.data.server.data.CellPosition;
import com.ewan.meworking.data.server.data.Datas;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.dunjeon.server.world.entities.creatures.TestSubject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TestSubjectAIController extends CreatureController<TestSubject, TestSubject.TestSubjectControls>{
    public TestSubjectAIController(TestSubject connectedCreature) {
        super(connectedCreature);
    }

    List<Point> currentPath = new ArrayList<>();

    @Override
    public void update(double stepSize) {

        var selfQuery = getBasicMemoryBank().querySinglePackage(controls.getUUID(), Datas.EntityData.class, List.of(Datas.EntityPositionalData.class));
        if(selfQuery.isEmpty()){
            //TODO Is this really a sensible path? I think the only case this will occur is during server loading, in which case the server should hold off updating AI until every creature's had a chance to gather data...
            return;
        }
        BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData> selfAccessor = selfQuery.get();

        long floorID = selfAccessor.getKnowledge(Datas.EntityPositionalData.class).getInfo().getFloorUUID();

        Function<Vector2, Double> weightProvider = vector2 -> {
            var val = getBasicMemoryBank().querySinglePackage(new CellPosition(vector2, floorID), Datas.CellData.class, List.of(Datas.CellEnterableData.class));
            if(val.isEmpty()){
                return Double.POSITIVE_INFINITY;
            }else{
                var val2 = val.get().getKnowledge(Datas.CellEnterableData.class).getInfo().getEnterableStatus();
                if(val2 == Datas.CellEnterableData.EnterableStatus.ENTERABLE){
                    return 1.0;
                }else{
                    return Double.POSITIVE_INFINITY;
                }
            }
        };

        Integer lowX = null, lowY = null, highX = null, highY = null;

        for (var entry : getBasicMemoryBank().queryMultiPackage(Datas.CellData.class, List.of(Datas.CellEnterableData.class)).getIndividualAccessors().entrySet()) {
            CellPosition position = (CellPosition) entry.getKey();
            Datas.CellEnterableData data = entry.getValue().getKnowledge(Datas.CellEnterableData.class).getInfo();

            int x = (int)position.getPosition().x;
            int y = (int)position.getPosition().y;

            lowX = lowX == null ? x : Math.min(x, lowX);
            lowY = lowY == null ? y : Math.min(y, lowY);

            highX = highX == null ? x : Math.max(x, highX);
            highY = highY == null ? y : Math.max(y, highY);
        }

        val foreignEntityCheck = getBasicMemoryBank().queryMultiPackage(Datas.EntityData.class, List.of(Datas.EntityPositionalData.class)).getIndividualAccessors();

        for (BasicMemoryBank.SingleQueryAccessor<Object, Datas.EntityData> value : foreignEntityCheck.values()) {
            val entityData = value.attemptGetKnowledge(Datas.EntityPositionalData.class);
            if((Long)value.getIdentifier() != getControls().getUUID()){
                n 
            }
//            entityData.get().getTime() == Dunjeon.get
        }

        Vector2 startPosVector = selfAccessor.getKnowledge(Datas.EntityPositionalData.class).getInfo().getPosition();
        Point startPosPoint = new Point((int)startPosVector.x, (int)startPosVector.y);

        var list = PathFinding.getAStarPath(weightProvider, startPosPoint, new Point(startPosPoint.x, startPosPoint.y+1), new Point(lowX, lowY), new Point(highX, highY), false, PathFinding.CornerInclusionRule.NO_CORNERS, 0, true);

        System.out.println(list.size());
//        PathFinding.getAStarPath(weightProvider)

        /*
        * Step 1 identify goal
        *  - Investigate a foreign entity
        *  - Investigate unexplored rooms
        *  - Investigate unexplored tiles
        *  - Investigate unknown sounds
        *  - Reinvestigate old tiles/rooms/entities
         */

        var otherEntities = getBasicMemoryBank().queryMultiPackage(Datas.EntityData.class, List.of(Datas.EntityPositionalData.class));

//        for (var otherEntity : otherEntities.getIndividualAccessors().values()) {
//            var posData = otherEntity.getKnowledge(Datas.EntityPositionalData.class);
//            var kinData = otherEntity.attemptGetKnowledge(Datas.EntityKineticData.class);
//
//
//        }
    }

    private HashMap<Vector2, TileMetaData> tileMap = new HashMap<>();

    private class TileMetaData {
        public TileMetaData(Vector2 pos){
            position = pos;
        }

        @Getter
        final Vector2 position;

        @Setter
        private int lastTickSeen = 0;

        @Setter
        private int lastTickVisited = 0;

        public Optional<Integer> getLastTickSeen(){
            if(lastTickSeen == 0){
                return Optional.empty();
            }else{
                return Optional.of(lastTickSeen);
            }
        }

        public Optional<Integer> getLastTickVisited(){
            if(lastTickVisited == 0){
                return Optional.empty();
            }else{
                return Optional.of(lastTickVisited);
            }
        }

    }

}
