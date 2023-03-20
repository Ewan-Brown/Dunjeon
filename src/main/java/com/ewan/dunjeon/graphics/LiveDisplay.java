package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.world.World;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.entities.EntityStateData;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import com.ewan.dunjeon.world.entities.memory.CellMemory;
import com.ewan.dunjeon.world.entities.memory.EntityMemory;
import com.ewan.dunjeon.world.entities.memory.FloorMemory;
import com.ewan.dunjeon.world.level.Floor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class LiveDisplay {
    private final int scale = 8;
    private static final int furniture_padding = 1;
    private JFrame frame;
    private JPanel panel;

    public static HashMap<Point, Color> debugCells = new HashMap<>();
    public static HashMap<Line2D.Float, Color> debugLines = new HashMap<>();
    public static boolean RENDER_GRID = false;
    public static boolean RENDER_OLD_ENTITIES = false;


    public void startDrawing(World w){
        if(frame == null) {
            frame = new JFrame();
            frame.addKeyListener(w);
            frame.setFocusable(true);
            frame.setSize(600, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            panel = new JPanel() {
                public void paint(Graphics g) {
                    super.paint(g);
                    Graphics2D graphics = (Graphics2D) g;
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
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

                                    int wallThickness = (int) Math.ceil(scale / 2.0);

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
                        for (RenderableObject drawable : p.getRawDrawables()) {
                            g.setColor(drawable.getColor());
                            AffineTransform af = new AffineTransform();
                            af.scale(scale,scale);
                            af.translate(p.getPosX(),p.getPosY());
                            af.rotate(p.getRotation());
                            Shape s = af.createTransformedShape(drawable.getShape());
                            ((Graphics2D) g).fill( s);
                        }


                        for (EntityMemory entityMemory : p.getCurrentFloorMemory().getEntityMemory()) {
                            EntityStateData state = entityMemory.getStateData();
                            AffineTransform af = new AffineTransform();
                            af.scale(scale,scale);
                            af.translate(state.getX(),state.getY()) ;
                            af.rotate(state.getRotation());
                            for (RenderableObject renderableObject : entityMemory.getRenderableObjects()) {
                                Color c = renderableObject.getColor();
                                if(entityMemory.isOldData()){
                                    c = new Color(c.getRed() / 3,c.getGreen() / 3, c.getBlue() / 3);
                                }
                                Shape s = af.createTransformedShape(renderableObject.getShape());
                                g.setColor(c);
                                ((Graphics2D) g).fill(s);
                            }
                        }

                    }

                    if(RENDER_GRID) {

                        graphics.setColor(Color.BLACK);
                        for (int x = 0; x < lev.getWidth(); x++) {
                            graphics.drawLine(x * scale, 0, x * scale, lev.getHeight() * scale);
                        }

                        for (int y = 0; y < lev.getHeight(); y++) {
                            graphics.drawLine(0, y * scale, lev.getWidth() * scale, y * scale);
                        }

                    }

                    debugLines.forEach((line2D, color) -> {
                        AffineTransform af = new AffineTransform();
                        af.scale(scale,scale);
                        graphics.setColor(color);
                        graphics.draw(af.createTransformedShape(line2D));
                    });

                    debugCells.forEach((point, color) -> {
                        AffineTransform af = new AffineTransform();
                        af.scale(scale,scale);
                        graphics.setColor(color);
                        graphics.draw(af.createTransformedShape(new Rectangle(point.x,point.y,1,1)));
                    });

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
