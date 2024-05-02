package com.ewan.dunjeon.data.datastreams;

import com.ewan.dunjeon.data.DataStreamParameters;
import com.ewan.dunjeon.data.Datastream;
import com.ewan.dunjeon.data.Sensor;
import com.ewan.dunjeon.server.world.Dunjeon;
import com.ewan.dunjeon.server.world.WorldUtils;
import com.ewan.dunjeon.server.world.floor.Floor;
import com.ewan.meworking.data.server.event.HeardSoundEvent;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.MathUtils;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.ewan.dunjeon.server.game.StartServer.rand;

public class HearingDataStream extends Datastream<HearingDataStream.HearingDataStreamParameters> {

    private List<SoundRequest> amalgamatedSoundEvents = new ArrayList<>();

    private static Vector2 randomizeVector2(Vector2 v, double intensity){
        return new Vector2(v.x += (rand.nextDouble() - 0.5) * intensity, v.y += (rand.nextDouble() - 0.5) * intensity);
    }

    public void appendSoundEvent(SoundRequest e){
        amalgamatedSoundEvents.add(e);
    }

    @Override
    public void update(Dunjeon d) {
        for (Sensor<HearingDataStreamParameters> subscriber : getSubscribers()) {
            HearingDataStreamParameters params = subscriber.getParameters();

            List<HeardSoundEvent> events = new ArrayList<>();
            for (SoundRequest sound : amalgamatedSoundEvents) {
                if(sound.sourceUUID == subscriber.getParameters().sensorHostUUID){
                    //TODO If player then they should probably be able to hear themselves for audio feedback
                }else{
                    List<Vector2> tiles = WorldUtils.getMatchingTilesBetweenPoints(params.hearingSourceLocation, sound.sourceLocation,
                            vector2 -> params.sensorFloor.getCellAt(vector2).orElseThrow(() -> new RuntimeException("what the heck!" + vector2)).isFilled(),
                            vector2 -> vector2.x >= params.sensorFloor.getWidth() || vector2.y >= params.sensorFloor.getHeight() || vector2.x < 0 || vector2.y < 0);
                    double dist = params.hearingSourceLocation.distance(sound.sourceLocation);
                    double muffled = Math.min(Math.max(1.0,tiles.size()/2.0), 3.0);
                    float perceivedIntensity = (float)(sound.intensity / (dist*dist));
                    float inaccuracy = (float)muffled ;
                    HeardSoundEvent event = new HeardSoundEvent(sound.category, randomizeVector2(sound.sourceLocation, inaccuracy/10), perceivedIntensity, sound.sourceUUID == subscriber.getParameters().sensorHostUUID, d.getTimestamp());
                    events.add(event);
                }

            }

            //Transform each of the existing soundrequests to heardSoundEvents for this subscriber
            subscriber.passOnEvents(events);
        }
        amalgamatedSoundEvents.clear();
    }

    @AllArgsConstructor
    public static class HearingDataStreamParameters extends DataStreamParameters{
        private final Vector2 hearingSourceLocation;
        private final long sensorHostUUID;
        private final Floor sensorFloor; //TODO REMOVEME?
    }

    @AllArgsConstructor
    public static class SoundRequest{
        private final Vector2 sourceLocation;
        private final float intensity;
        private final long sourceUUID;
        private final HeardSoundEvent.SoundSourceCategory category;
    }
}
