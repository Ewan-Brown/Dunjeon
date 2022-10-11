package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.entities.Creature;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import com.ewan.dunjeon.world.level.Floor;

import javax.swing.*;
import java.awt.*;

public class LiveDisplay {
    private final int scale = 20;
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
                    FloorMemory memoryFloor = World.getInstance().getPlayer().getFloorMemory(lev);
                    if(memoryFloor == null){
                        return;
                    }
                    synchronized (memoryFloor) {
                        for (int x = 0; x < lev.getWidth(); x++) {
                            for (int y = 0; y < lev.getHeight(); y++) {
                                CellMemory data = World.getInstance().getPlayer().getFloorMemory(lev).getDataAt(x, y);
                                Color processedCellColor = null;
                                Color processedFurnitureColor = null;
                                if (data == null) {
                                    graphics.setColor(Color.BLACK);
                                } else {
                                    //Color block based on whether it is visible (full color), remembered (faded color), or unknown (black)
                                    if (!data.isOldData()) {
                                        processedCellColor = data.cellRenderData.getColor();
                                    } else {
                                        Color rawColor = data.cellRenderData.getColor();
                                        processedCellColor = new Color(rawColor.getRed() / 3, rawColor.getGreen() / 3, rawColor.getBlue() / 3);
                                    }


                                    //Draw Furniture if it exists
                                    CellMemory.FurnitureData furnitureData = data.furnitureData;
                                    if (furnitureData != null && furnitureData.isVisible()) {
                                        if (!data.isOldData()) {
                                            processedFurnitureColor = furnitureData.furnitureRenderData.getColor();
                                        } else {
                                            Color rawColor = furnitureData.furnitureRenderData.getColor();
                                            processedFurnitureColor = new Color(rawColor.getRed() / 3, rawColor.getGreen() / 3, rawColor.getBlue() / 3);
                                        }

                                    }
                                }
                                graphics.setColor(processedCellColor);
                                graphics.fillRect(x * scale, y * scale, scale, scale);
                                if (processedFurnitureColor != null) {
                                    CellMemory.FurnitureData fData = data.furnitureData;
                                    int fX = (int) ((fData.getCenterX() - fData.getSize() / 2f) * scale);
                                    int fY = (int) ((fData.getCenterY() - fData.getSize() / 2f) * scale);
                                    int fSize = (int) (fData.getSize() * scale);
                                    graphics.setColor(processedFurnitureColor);
                                    graphics.fillRect(fX, fY, fSize, fSize);
                                    if (fData.isInteractable()) {
                                        graphics.setColor(Color.BLACK);
                                        graphics.drawRect(fX, fY, fSize - 1, fSize - 1);
                                    }
                                }

                            }
                        }
                        //Draw Player
                        graphics.setColor(Color.BLUE);
                        Creature p = w.getPlayer();
                        graphics.fillRect((int)((p.getCenterX() - p.getSize()/2) * scale), (int)((p.getCenterY() - p.getSize()/2) * scale), (int)(p.getSize() * scale) , (int)(p.getSize() * scale));

                        for (EntityMemory memory : World.getInstance().getPlayer().getFloorMemory(lev).getEntityMemory()) {
                            float size = memory.getSize();
                            float x = memory.getX();
                            float y = memory.getY();
                            int x1 = (int)((x - size/2f) * scale);
                            int y1 = (int)((y - size/2f) * scale);
                            Color c = memory.getRenderData().getColor();
                            if(memory.isOldData()){
                                c = new Color(c.getRed()/3, c.getGreen()/3, c.getBlue()/3);
                            }
                            graphics.setColor(c);
                            graphics.fillRect(x1, y1, (int)(size * scale), (int)(size * scale));
                        }
                        
                    }


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
