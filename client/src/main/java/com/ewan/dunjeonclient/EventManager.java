package com.ewan.dunjeonclient;

import com.ewan.meworking.data.server.EventPacket;
import com.ewan.meworking.data.server.Timestamp;
import com.ewan.meworking.data.server.event.HeardSoundEvent;
import com.ewan.meworking.data.server.event.ObservedEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Handles the transformation of ObservedEvents into video/audio effects, and keeps track of those effects
public class EventManager {

    public EventManager(){
        addEventHandler(new EventHandler<>() {
            @Override
            public void handleEvent(HeardSoundEvent event) {
                managedEvents.add(new ManagedEvent() {
                    @Override
                    public void update(float timeDiff) {
                        super.update(timeDiff);
                    }

                    @Override
                    public void doSomeRendering(GL2 gl) {
                        gl.glPushMatrix();
                        gl.glTranslated(event.getApproxLocation().x, event.getApproxLocation().y, 0);
                        gl.glScaled(0.3f,0.3f,0.3f);
//                        gl.glRotated(this.tickCount / 100.0 * 360.0,0,0,1);
                        gl.glColor4d(1, 0, 1, (5.0 - tickCount)/10.0);
                        gl.glBegin(GL2.GL_POLYGON);
                        gl.glVertex2d(-1, 1);
                        gl.glVertex2d(-1, -1);
                        gl.glVertex2d(1, -1);
                        gl.glVertex2d(1, 1);
                        gl.glEnd();
                        gl.glPopMatrix();
                    }

                    @Override
                    public boolean isComplete() {
                        return this.tickCount > 10;
                    }
                });
            }
        }, HeardSoundEvent.class);
    }

    private HashMap<Class<? extends ObservedEvent>, EventHandler<?>> eventHandlerMap = new HashMap<>();
    private List<ManagedEvent> managedEvents = new ArrayList<>();

    private <T extends ObservedEvent> void addEventHandler(EventHandler<T> e, Class<T> clazz){
        eventHandlerMap.put(clazz, e);
    }

    @SuppressWarnings("unchecked") //Trust me bro
    public <T extends ObservedEvent> void processEvent(T e){
        EventHandler<T> eventHandler = (EventHandler<T>) eventHandlerMap.get(e.getClass());
        eventHandler.handleEvent(e);
    }

    public void drawEvents(GL2 gl){
        for (int i = 0; i < managedEvents.size(); i++) {
            if(managedEvents.get(i).isComplete()){
                managedEvents.remove(i);
            }else {
                managedEvents.get(i).doSomeRendering(gl);
            }
        }
    }

    public void updateEvents(float timeStep){
        for (ManagedEvent managedEvent : managedEvents) {
            managedEvent.update(timeStep);
        }
    }

    private interface EventHandler<T extends ObservedEvent>{
        void handleEvent(T event);
    }

    private abstract static class ManagedEvent{
        int tickCount = 0;
        float timePassed = 0;
        public void update(float timeDiff){
            tickCount++;
            timePassed += timeDiff;
        }
        //Optional, to be called at drawtime by ClientInterface
        public void doSomeRendering(GL2 gl){}

        public abstract boolean isComplete();
    }
}
