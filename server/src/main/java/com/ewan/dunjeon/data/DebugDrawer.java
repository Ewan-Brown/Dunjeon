package com.ewan.dunjeon.data;

import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;

public class DebugDrawer {

    BufferedImage bufferedImage;
    Vector2 playerPos;
    double scale = 100;

    public DebugDrawer(Vector2 p){
        bufferedImage = new BufferedImage(5000,5000,TYPE_3BYTE_BGR);
        playerPos = p;
    }

    public void addLine(Vector2 v1, Vector2 v2, Color c){
        v1 = v1.difference(playerPos);
        v2 = v2.difference(playerPos);
        bufferedImage.getGraphics().setColor(c);
        bufferedImage.getGraphics().drawLine((int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y);
    }

    private Vector2 transformVector(Vector2 v){
        return v.difference(playerPos).multiply(scale);
    }

    public void addCircle(Vector2 v, double radius, Color c){

    }


    public void addSquare(Vector2 v1, Color c){

    }

    public void generateImage(){

    }


}
