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
            frame.setSize(400, 420);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            panel = new JPanel() {
                public void paint(Graphics g) {
                    super.paint(g);
                    for (BasicCell cell : lev.getCellsAsList()) {
                        if(DEBUG_CELLS.contains(cell)){
                            g.setColor(Color.YELLOW);
                        }
                        else {
                            if(World.getInstance().getPlayer().getViewRange().contains(cell)) {
                                g.setColor(cell.getColor());
                            }else{
                                g.setColor(Color.BLACK);
                            }
                            g.fillRect(cell.getX() * size, cell.getY() * size, size, size);
                        }
//                        g.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size+size, cell.getY()*size);
//                        g.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size, cell.getY()*size+size);
                    }
                    for (Furniture f : lev.getFurniture()) {
                        BasicCell c  = f.containingCell;
                        if(f.getColor() != null) {
                            g.setColor(f.getColor());
                            g.fillRect(c.getX() * size + 2, c.getY() * size + 2, size - 3, size - 3);
                        }
                    }
                    for (Entity e : lev.getEntities()) {
                        g.setColor(e.getColor());
                        g.fillRect(e.getX() * size, e.getY() * size, size, size);
                    }
                    for (Point2D[] line : DEBUG_LINES){
                        g.setColor(Color.BLACK);
                        int x1 = (int)(line[0].getX() * size);
                        int y1 = (int)(line[0].getY() * size);
                        int x2 = (int)(line[1].getX() * size);
                        int y2 = (int)(line[1].getY() * size);
                        g.drawLine(x1, y1, x2, y2 );
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
