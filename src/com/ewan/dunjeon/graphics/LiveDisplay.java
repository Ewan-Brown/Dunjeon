package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.level.Level;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.furniture.Furniture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class LiveDisplay {
    private int size = 8;
    private JFrame frame;
    private JPanel panel;
    private Level lev;
    private static List<BasicCell> selectedCells = new ArrayList<>();

    public static void setSelectedCells(List<BasicCell> cells) {
        selectedCells = cells;
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
                        if(selectedCells.contains(cell)) g.setColor(Color.YELLOW);
                        else g.setColor(cell.getColor());
                        g.fillRect(cell.getX()*size, cell.getY()*size, size, size);
                        g.setColor(Color.BLACK);
                        g.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size+size, cell.getY()*size);
                        g.drawLine(cell.getX()*size, cell.getY()*size, cell.getX()*size, cell.getY()*size+size);
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
                        g.fillRect(e.getX() * size + 1, e.getY() * size + 1, size - 1, size - 1);
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
