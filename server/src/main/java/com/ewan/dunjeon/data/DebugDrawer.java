package com.ewan.dunjeon.data;

import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class DebugDrawer {

    BufferedImage bufferedImage;
    Vector2 playerPos;
    int scale = 200;
    int size = 10000;

    HashMap<Integer, List<Drawable>> shapeTierList = new HashMap<>();

    private record Drawable(Shape shape, Color color, boolean fill){};

    public DebugDrawer(Vector2 p){
        bufferedImage = new BufferedImage(size,size,TYPE_INT_ARGB);
        bufferedImage.getGraphics().setColor(Color.BLACK);
        bufferedImage.getGraphics().clearRect(0,0,size,size);
        playerPos = p;
    }

    public void addLine(Vector2 v1, Vector2 v2, Color c, int tier){
        v1 = transformVector(v1);
        v2 = transformVector(v2);
        addToList(new Drawable(new Line2D.Double(v1.x, v1.y, v2.x, v2.y), c, false), tier);
    }

    private Vector2 transformVector(Vector2 v){
        return v.difference(playerPos).multiply(scale).add(new Vector2((int)(size/2.0), (int)(size/2.0)));
    }

    public void addCircle(Vector2 v, double radius, Color c, int tier){
        throw new RuntimeException("addCircle not implemented yet");
    }


    public void addSquare(Vector2 v1, Color c, boolean filled, int tier){
        v1 = transformVector(v1);
        addToList(new Drawable(new Rectangle2D.Double(v1.x, v1.y, scale,scale), c, filled), tier);
//        Graphics g = bufferedImage.getGraphics();
//        g.setColor(c);
//        if(filled){
//            g.fillRect((int)v1.x, (int)v1.y, scale, scale);
//        }else{
//            g.drawRect((int)v1.x, (int)v1.y, scale, scale);
//        }
    }

    private void addToList(Drawable d, int tier){
        if(!shapeTierList.containsKey(tier)){
            shapeTierList.put(tier, new ArrayList<>());
        }
        shapeTierList.get(tier).add(d);
    }

    public void generateImage(){

        Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
        shapeTierList.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).forEachOrdered(new Consumer<>() {
            @Override
            public void accept(Map.Entry<Integer, List<Drawable>> integerListEntry) {
                integerListEntry.getValue().forEach(drawable -> {
                    g.setColor(drawable.color());
                    if (drawable.fill()) {
                        g.fill(drawable.shape());
                    } else {
                        g.draw(drawable.shape());
                    }
                });
            }
        });


        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -bufferedImage.getHeight()));
        bufferedImage = createTransformed(bufferedImage, at);

        try {
            ImageIO.write(bufferedImage, "png",new File("C:\\Users\\Ewan\\Pictures\\image.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //https://stackoverflow.com/questions/23457754/how-to-flip-bufferedimage-in-java
    private static BufferedImage createTransformed(
            BufferedImage image, AffineTransform at)    {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }


}
