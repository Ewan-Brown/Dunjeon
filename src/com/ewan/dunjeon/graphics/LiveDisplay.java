package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Level;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class LiveDisplay {
    private int size = 6;
    private JFrame frame;
    private JPanel panel;
    private Level lev;
    private static List<BasicCell> DEBUG_CELLS = new ArrayList<>();
    private static List<Point2D[]> DEBUG_LINES = new ArrayList<>();
    private static boolean SHOW_ALL_TILES = false;

    public static void setDebugCells(List<BasicCell> cells) {
        DEBUG_CELLS = cells;
    }

    public static void setDebugLines(List<Point2D[]> lines) {
        DEBUG_LINES = lines;
    }

    public void startDrawing(Level l, KeyListener keyListener){
        lev = l;
        if(frame == null) {
            frame = new JFrame();
            frame.addKeyListener(keyListener);
            frame.setFocusable(true);
            frame.setSize(600, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            panel = new JPanel() {
                public void paint(Graphics graphics) {
                    super.paint(graphics);
                    for (BasicCell cell : lev.getCellsAsList()) {
                        if(DEBUG_CELLS.contains(cell)){
                            graphics.setColor(Color.YELLOW);
                        }
                        else {
                            if(World.getInstance().getPlayer().getVisibleCells().contains(cell) || SHOW_ALL_TILES) {
                                graphics.setColor(cell.getColor());
                                Furniture f = cell.getFurniture();
                                if(f != null && f.getColor() != null) {
                                    graphics.setColor(f.getColor());
                                    graphics.fillRect(cell.getX() * size + 2, cell.getY() * size + 2, size - 3, size - 3);
                                }
                            }else if(World.getInstance().getPlayer().getRememberedCells().contains(cell)){
                                int r = cell.getColor().getRed();
                                int g = cell.getColor().getGreen();
                                int b = cell.getColor().getBlue();
                                graphics.setColor(new Color(r/4, g/4, b/4));
                            }else{
                                graphics.setColor(Color.BLACK);
                            }
                            graphics.fillRect(cell.getX() * size, cell.getY() * size, size, size);
                        }
//                        graphics.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size+size, cell.getY()*size);
//                        graphics.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size, cell.getY()*size+size);
                    }

                    for (Entity e : lev.getEntities()) {
                        try {
                            graphics.setColor(e.getColor()); //FIXME Sometimes get a random nullpointerexcception here. What's up with that?
                            graphics.fillRect(e.getX() * size, e.getY() * size, size, size);
                        }catch (NullPointerException ex){
                            ex.printStackTrace();
                        }
                    }
                    for (Point2D[] line : DEBUG_LINES){
                        graphics.setColor(Color.BLACK);
                        int x1 = (int)(line[0].getX() * size);
                        int y1 = (int)(line[0].getY() * size);
                        int x2 = (int)(line[1].getX() * size);
                        int y2 = (int)(line[1].getY() * size);
                        graphics.drawLine(x1, y1, x2, y2 );
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
