package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import com.ewan.dunjeon.world.level.Floor;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class LiveDisplay {
    private final int scale = 10;
    private static final int furniture_padding = 1;
    private JFrame frame;
    private JPanel panel;

//    public static ArrayList<Point> debugCells;
    public static HashMap<Point, Color> debugCells = new HashMap<>();
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
                    Floor lev = w.getPlayer().getFloor();
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

                                    Point p = new Point(x, y);
                                    if (debugCells != null && debugCells.containsKey(p)) {
                                        processedCellColor = debugCells.get(p);
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

                                    graphics.setColor(processedCellColor);

                                    //***************************
                                    // RENDER WALLS IF NECESSARY
                                    //***************************

                                    int wallThickness = (int) Math.ceil(scale / 3.0);

                                    int x1 = x * scale;
                                    int x2 = x * scale + scale;
                                    int y1 = y * scale;
                                    int y2 = y * scale + scale;

                                    if (data.cellRenderData.shouldRenderWalls()) {
                                        for (WorldUtils.Side cellSide : data.cellRenderData.getSides().keySet()) {
                                            Color c = switch (data.cellRenderData.getSides().get(cellSide)) {
                                                case SEE_PRESENT -> data.cellRenderData.getColor();
                                                case SEEN_PREVIOUSLY -> processedCellColor;
                                                default -> null;
                                            };
                                            if (c != null) {
                                                switch (cellSide) {
                                                    case EAST -> graphics.fillRect(x2 - wallThickness, y1, wallThickness, scale);
                                                    case WEST -> graphics.fillRect(x1, y1, wallThickness, scale);
                                                    case NORTH -> graphics.fillRect(x1, y1, scale, wallThickness);
                                                    case SOUTH -> graphics.fillRect(x1, y2 - wallThickness, scale, wallThickness);

                                                }
                                            }
                                        }
                                    } else {
                                        graphics.fillRect(x * scale, y * scale, scale, scale);
                                        if (processedFurnitureColor != null) {
                                            CellMemory.FurnitureData fData = data.furnitureData;
                                            int fX = (int) ((fData.getPosX() - fData.getSize() / 2f) * scale);
                                            int fY = (int) ((fData.getPosY() - fData.getSize() / 2f) * scale);
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

                            }
                        }
                        //Draw Player
                        graphics.setColor(Color.BLUE);
                        Creature p = w.getPlayer();
                        graphics.fillRect((int)((p.getPosX() - p.getSize()/2) * scale), (int)((p.getPosY() - p.getSize()/2) * scale), (int)(p.getSize() * scale) , (int)(p.getSize() * scale));

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

                            if (memory.isInteractable()) {
                                graphics.setColor(Color.BLACK);
                                graphics.drawRect(x1, y1, (int)(scale*size - 1), (int)(scale*size - 1));
                            }
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
