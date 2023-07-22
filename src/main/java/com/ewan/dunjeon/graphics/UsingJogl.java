/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ewan.dunjeon.graphics;

import com.ewan.dunjeon.data.Datas;
import com.ewan.dunjeon.world.Dunjeon;
import com.ewan.dunjeon.world.WorldUtils;
import com.ewan.dunjeon.world.cells.BasicCell;
import com.ewan.dunjeon.world.entities.Entity;
import com.ewan.dunjeon.world.entities.creatures.BasicMemoryBank;
import com.ewan.dunjeon.world.entities.creatures.Creature;
import com.ewan.dunjeon.world.entities.memory.FloorKnowledge;
import com.ewan.dunjeon.world.entities.memory.celldata.CellKnowledge;
import com.ewan.dunjeon.world.entities.memory.creaturedata.CreatureKnowledge;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.*;
import org.dyn4j.world.World;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class UsingJogl extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 5663760293144882635L;

	protected GLCanvas canvas;

	public UsingJogl() {
		super("JOGL Dunjeon");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension size = new Dimension(800, 800);

		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);

		this.canvas = new GLCanvas(caps);
		this.canvas.setPreferredSize(size);
		this.canvas.setMinimumSize(size);
		this.canvas.setMaximumSize(size);
		this.canvas.setIgnoreRepaint(true);
		this.canvas.addGLEventListener(this);

		this.setLayout(new BorderLayout());

		this.add(this.canvas);
		this.setResizable(false);

		this.pack();
	}

	public void start() {
		Animator animator = new Animator(this.canvas);
		animator.setRunAsFastAsPossible(true);
		animator.start();
	}

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-1, 1, -1, 1, 0, 1);
		gl.glViewport(0, 0, this.getWidth(), this.getHeight());

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		gl.setSwapInterval(0);
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		// get the OpenGL context
		GL2 gl = glDrawable.getGL().getGL2();

		// clear the screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix (0,0) is in the center of the window
		gl.glLoadIdentity();

		this.render(gl);

	}

	@Override
	public void dispose(GLAutoDrawable glDrawable) {
	}

	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {
	}

	/**
	 * Renders the example.
	 * @param gl the OpenGL context
	 */
	protected void render(GL2 gl) {

		gl.glPushMatrix();
		gl.glColor3d(1,0,0);

		// Draw things with true sight

		gl.glScaled(0.02, 0.02, 1.0);

		renderPerspective(gl, Dunjeon.getInstance().getPlayer());
		gl.glPopMatrix();
	}

	public static void renderPerspective(GL2 gl, Creature<BasicMemoryBank> c){

		HashMap<WorldUtils.CellPosition, CellKnowledge> map = c.getMemoryProcessor().getCellKnowledgeHashMap();

		for (CellKnowledge cellKnowledge : map.values()) {
			Datas.CellEnterableData enterableData = cellKnowledge.get(Datas.CellEnterableData.class);
			if(enterableData != null && enterableData.getEnterableStatus() == Datas.CellEnterableData.EnterableStatus.ENTERABLE){
				gl.glColor3d(1, 0,1);
			}else{
				gl.glColor3d(0, 0,1);
			}
			gl.glBegin(GL2.GL_POLYGON);
			final double SIZE = 1;

			Vector2 centerPos = new Vector2(cellKnowledge.getIdentifier().getX(), cellKnowledge.getIdentifier().getY());
			Vector2 relativePos = new Vector2(c.getWorldCenter().to(centerPos));

			double centerX = relativePos.x;
			double centerY = relativePos.y;

			gl.glVertex2d(centerX, centerY);
			gl.glVertex2d(centerX+SIZE, centerY);
			gl.glVertex2d(centerX+SIZE, centerY+SIZE);
			gl.glVertex2d(centerX, centerY+SIZE);
			gl.glEnd();
		}

		var creatureKnowledgeHashMap = c.getMemoryProcessor().getCreatureKnowledgeHashMap();

		for (CreatureKnowledge value : creatureKnowledgeHashMap.values()) {
			gl.glColor3d(0, 1,0);
			gl.glBegin(GL2.GL_POLYGON);
			final double SIZE = 1;
			final double HALF_SIZE = SIZE/2;

			Datas.EntityPositionalData posData = value.get(Datas.EntityPositionalData.class);

			if(posData != null) {
				Vector2 centerPos = posData.getPosition();
				Vector2 relativePos = new Vector2(c.getWorldCenter().to(centerPos));


				double centerX = relativePos.x;
				double centerY = relativePos.y;

				gl.glVertex2d(centerX - HALF_SIZE, centerY - HALF_SIZE);
				gl.glVertex2d(centerX + HALF_SIZE, centerY - HALF_SIZE);
				gl.glVertex2d(centerX + HALF_SIZE, centerY + HALF_SIZE);
				gl.glVertex2d(centerX - HALF_SIZE, centerY + HALF_SIZE);
				gl.glEnd();
			}
		}


	}

	public static void renderAll(GL2 gl){
		//		for (BasicCell basicCell : Dunjeon.getInstance().getPlayer().getFloor().getCellsAsList()) {
//			Color c = basicCell.color;
//			gl.glColor3d(c.getRed()/255.0,c.getGreen()/255.0,c.getBlue()/255.0);
//
//			gl.glBegin(GL2.GL_POLYGON);
//
//			double lowX = basicCell.getX();
//			double lowY = basicCell.getY();
//			double highX = lowX	+ 1;
//			double highY = lowY	+ 1;
//
//			gl.glVertex2d(lowX, lowY);
//			gl.glVertex2d(highX, lowY);
//			gl.glVertex2d(highX, highY);
//			gl.glVertex2d(lowX, highY);
//
//			gl.glEnd();
//
//		}

//		for (Entity e : Dunjeon.getInstance().getPlayer().getFloor().getEntities()){
//			gl.glColor3d(0, 1,0);
//			gl.glBegin(GL2.GL_POLYGON);
//
//			final double SIZE = 1;
//			final double HALF_SIZE = SIZE/2;
//
//			double centerX = e.getWorldCenter().x;
//			double centerY = e.getWorldCenter().y;
//
//			gl.glVertex2d(centerX-HALF_SIZE, centerY-HALF_SIZE);
//			gl.glVertex2d(centerX+HALF_SIZE, centerY-HALF_SIZE);
//			gl.glVertex2d(centerX+HALF_SIZE, centerY+HALF_SIZE);
//			gl.glVertex2d(centerX-HALF_SIZE, centerY+HALF_SIZE);
//
//		}
	}
}
