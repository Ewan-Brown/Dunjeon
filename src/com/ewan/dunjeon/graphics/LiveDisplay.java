package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.world.Interactable;
import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.entities.Creature;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.Player;
import com.ewan.dunjeon.world.entities.memory.CellData;
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
                            CellData data = World.getInstance().getPlayer().getFloorMemory(lev).getDataAt(x,y);
                            if(data == null){
                                graphics.setColor(Color.BLACK);
                            }
                            else {
                                //Color block based on whether it is visible (full color), remembered (faded color), or unknown (black)
                                Color processedColor;
                                if(!data.isOldData()){
                                    processedColor = data.visual.getColor();
                                }else{
                                    Color rawColor = data.visual.getColor();
                                    processedColor = new Color(rawColor.getRed()/3, rawColor.getGreen()/3, rawColor.getBlue()/3);
                                }
                                graphics.setColor(processedColor);
//                                if(!data.isOldData()) {
//                                    graphics.setColor(cell.getColor());
//                                    graphics.fillRect(cell.getX() * size, cell.getY() * size, size, size);
//                                    Furniture f = cell.getFurniture();
//                                    //Draw furniture
//                                    if(f != null && f.getColor() != null) {
//                                        graphics.setColor(f.getColor());
//                                        graphics.fillRect(cell.getX() * size + furniture_padding, cell.getY() * size + furniture_padding, size - furniture_padding*2, size - furniture_padding*2);
//                                        //Highlight furniture if in range for interactino
//                                        if(nearestPlayerTouchable == f){
//                                            graphics.setColor(Color.YELLOW);
//                                            graphics.drawRect(cell.getX() * size + furniture_padding, cell.getY() * size + furniture_padding, size - furniture_padding*2, size - furniture_padding*2);
//                                        }
//                                    }
//                                }else if(World.getInstance().getPlayer().getRememberedCells().contains(cell)){
//                                    int r = cell.getColor().getRed();
//                                    int g = cell.getColor().getGreen();
//                                    int b = cell.getColor().getBlue();
//                                    graphics.setColor(new Color(r/4, g/4, b/4));
//                                    graphics.fillRect(cell.getX() * size, cell.getY() * size, size, size);
//
//                                }else{
//                                    graphics.setColor(Color.BLACK);
//                                    graphics.fillRect(cell.getX() * size, cell.getY() * size, size, size);
//                                }
                            }
                            graphics.fillRect(x * size, y * size, size, size);

                        }
                    }
//                    for (BasicCell cell : lev.getCellsAsList()) {
//
////                        graphics.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size+size, cell.getY()*size);
////                        graphics.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size, cell.getY()*size+size);
//                    }

                    //Draw Player
                    graphics.setColor(Color.BLUE);
                    Creature p = w.getPlayer();
                    graphics.fillRect((int)((p.getX() - p.getSize()/2) * size), (int)((p.getY() - p.getSize()/2) * size), (int)(p.getSize() * size) , (int)(p.getSize() * size));

                    
//                    for (Entity e : lev.getEntities()) {
//                        if(SHOW_ALL_TILES || e == w.getPlayer() || w.getPlayer()){
//                            graphics.setColor(e.getColor());
//                            graphics.fillRect((int)((e.getX() - e.getSize()/2) * size), (int)((e.getY() - e.getSize()/2) * size), (int)(e.getSize() * size) , (int)(e.getSize() * size));
//                            if(e == nearestPlayerChattable){
//                                graphics.setColor(Color.YELLOW);
//                                graphics.drawRect((int)((e.getX() - e.getSize()/2) * size), (int)((e.getY() - e.getSize()/2) * size), (int)(e.getSize() * size) , (int)(e.getSize() * size));
//
//                            }
//                        }
//                    }
//                    for (Point2D[] line : DEBUG_LINES){
//                        graphics.setColor(Color.BLACK);
//                        int x1 = (int)(line[0].getX() * size);
//                        int y1 = (int)(line[0].getY() * size);
//                        int x2 = (int)(line[1].getX() * size);
//                        int y2 = (int)(line[1].getY() * size);
//                        graphics.drawLine(x1, y1, x2, y2 );
//                    }

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
