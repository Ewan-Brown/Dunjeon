package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.entities.Creature;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.Player;
import com.ewan.dunjeon.world.entities.memory.CellData;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class LiveDisplay {
    private final int size = 12;
    private static final int furniture_padding = 1;
    private JFrame frame;
    private JPanel panel;
//    private static List<BasicCell> DEBUG_CELLS = new ArrayList<>();
//    private static List<Point2D[]> DEBUG_LINES = new ArrayList<>();
    public static boolean SHOW_ALL_TILES = false;

//    public static void setDebugCells(List<BasicCell> cells) {
//        DEBUG_CELLS = cells;
//    }

//    public static void setDebugLines(List<Point2D[]> lines) {
//        DEBUG_LINES = lines;
//    }

    public void startDrawing(World w){
        if(frame == null) {
            frame = new JFrame();
            frame.addKeyListener(w);
            frame.setFocusable(true);
            frame.setSize(600, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            panel = new JPanel() {
                public void paint(Graphics graphics) {
                    super.paint(graphics);
                    graphics.setColor(Color.BLACK);
                    graphics.clearRect(0,0,getWidth(),getHeight());
                    Floor lev = w.getPlayer().getLevel();
                    Interactable nearestPlayerTouchable = w.getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType.TOUCH);
                    Interactable nearestPlayerChattable = w.getPlayersNearestAvailableInteractionOfType(Interactable.InteractionType.CHAT);
                    for (int x = 0; x < lev.getWidth(); x++) {
                        for (int y = 0; y < lev.getHeight(); y++) {
                            FloorMemory memoryFloor = World.getInstance().getPlayer().getFloorMemory(lev);
                            if(memoryFloor == null){
                                continue;
                            }
                            synchronized (memoryFloor) {
                                CellData data = World.getInstance().getPlayer().getFloorMemory(lev).getDataAt(x, y);
                                if (data == null) {
                                    graphics.setColor(Color.BLACK);
                                } else {
                                    //Color block based on whether it is visible (full color), remembered (faded color), or unknown (black)
                                    Color processedColor;
                                    if (!data.isOldData()) {
                                        processedColor = data.visual.getColor();
                                    } else {
                                        Color rawColor = data.visual.getColor();
                                        processedColor = new Color(rawColor.getRed() / 3, rawColor.getGreen() / 3, rawColor.getBlue() / 3);
                                    }
                                    graphics.setColor(processedColor);

                                }
                                graphics.fillRect(x * size, y * size, size, size);
                            }
                        }
                    }

                    //Draw Player
                    graphics.setColor(Color.BLUE);
                    Creature p = w.getPlayer();
                    graphics.fillRect((int)((p.getX() - p.getSize()/2) * size), (int)((p.getY() - p.getSize()/2) * size), (int)(p.getSize() * size) , (int)(p.getSize() * size));

                }
            };
            frame.add(panel);
            frame.setVisible(true);
            new Thread(() -> {
                while(true){
                    try {
                        Thread.sleep(15);
                        panel.repaint();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
}
