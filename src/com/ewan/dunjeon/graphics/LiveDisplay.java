package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Floor;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class LiveDisplay {
    private final int size = 20;
    private static final int furniture_padding = 1;
    private JFrame frame;
    private JPanel panel;
    private static List<BasicCell> DEBUG_CELLS = new ArrayList<>();
    private static List<Point2D[]> DEBUG_LINES = new ArrayList<>();
    private static final boolean SHOW_ALL_TILES = true;

    public static void setDebugCells(List<BasicCell> cells) {
        DEBUG_CELLS = cells;
    }

    public static void setDebugLines(List<Point2D[]> lines) {
        DEBUG_LINES = lines;
    }

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
                    Floor lev = w.getPlayer().getLevel();
                    for (BasicCell cell : lev.getCellsAsList()) {
                        if(DEBUG_CELLS.contains(cell)){
                            graphics.setColor(Color.YELLOW);
                        }
                        else {
                            //Color block based on whether it is visible (full color), remembered (faded color), or unknown (black)
                            if(World.getInstance().getPlayer().getVisibleCells().contains(cell) || SHOW_ALL_TILES) {
                                graphics.setColor(cell.getColor());
                                graphics.fillRect(cell.getX() * size, cell.getY() * size, size, size);
                                Furniture f = cell.getFurniture();
                                if(f != null && f.getColor() != null) {
                                    graphics.setColor(f.getColor());
                                    graphics.fillRect(cell.getX() * size + furniture_padding, cell.getY() * size + furniture_padding, size - furniture_padding*2, size - furniture_padding*2);
                                }
                            }else if(World.getInstance().getPlayer().getRememberedCells().contains(cell)){
                                int r = cell.getColor().getRed();
                                int g = cell.getColor().getGreen();
                                int b = cell.getColor().getBlue();
                                graphics.setColor(new Color(r/4, g/4, b/4));
                                graphics.fillRect(cell.getX() * size, cell.getY() * size, size, size);

                            }else{
                                graphics.setColor(Color.BLACK);
                                graphics.fillRect(cell.getX() * size, cell.getY() * size, size, size);
                            }
                        }
//                        graphics.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size+size, cell.getY()*size);
//                        graphics.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size, cell.getY()*size+size);
                    }

                    for (Entity e : lev.getEntities()) {
                        if(SHOW_ALL_TILES || e == w.getPlayer() || w.getPlayer().getVisibleCells().contains(e.getContainingCell())){
                            graphics.setColor(e.getColor());
                            graphics.fillRect((int)((e.getX() - e.getSize()/2) * size), (int)((e.getY() - e.getSize()/2) * size), (int)(e.getSize() * size) , (int)(e.getSize() * size));
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
