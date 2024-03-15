package com.ewan.dunjeonclient;

import com.ewan.meworking.data.server.data.CellPosition;
import com.ewan.meworking.data.server.data.Datas;
import com.ewan.meworking.data.server.memory.BasicMemoryBank;
import com.ewan.meworking.data.server.memory.BasicMemoryBank.MultiQueryAccessor;
import com.ewan.meworking.data.server.memory.KnowledgeFragment;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dyn4j.geometry.Vector2;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class UsingJogl implements GLEventListener {
	private static final long serialVersionUID = 5663760293144882635L;
	static Logger logger = LogManager.getLogger();

	@Getter
	protected GLCanvas canvas;
	private JFrame frame;
	private final ClientChannelHandler clientChannelHandler;
	final static Vector2 lastCameraPos = new Vector2();

	public UsingJogl(ClientChannelHandler clientChannelHandler) {
		logger.info("Creating UI");
		frame = new JFrame("Dungeon Client");

		this.clientChannelHandler = clientChannelHandler;

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

		frame.setLayout(new BorderLayout());

		frame.add(this.canvas);
		frame.setResizable(false);
		frame.setVisible(true);

		SimpleControls controls = new SimpleControls(clientChannelHandler);
		canvas.addKeyListener(controls);
		frame.pack();
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
		gl.glViewport(0, 0, frame.getWidth(), frame.getHeight());

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		gl.setSwapInterval(0);
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		logger.trace("display() called");
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
	public void dispose(GLAutoDrawable glDrawable) {}

	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {}

	//Only reason to change this is if packets are being occasionally dropped and causing flickering. This is LAST resort.
	private boolean isMemoryDataPresent(KnowledgeFragment<?> d){
        return (clientChannelHandler.getMostRecentTimestampReceived() - d.getTickStamp()) == 0;
	}
	
	protected void render(GL2 gl) {

		gl.glPushMatrix();
		gl.glColor3d(1,0,0);
		gl.glScaled(0.06, 0.06, 1.0);

		BasicMemoryBank basicMemoryBank = clientChannelHandler.getClientMemoryBank();
		if(basicMemoryBank != null){
			renderMemoryBank(gl, basicMemoryBank);
		}

		gl.glPopMatrix();

//		gl.glBegin(GL2.GL_POLYGON);
//		gl.glColor3d(1,0,0);
//		gl.glVertex2d(0,0);
//		gl.glVertex2d(1,0);
//		gl.glVertex2d(1,1);
//		gl.glVertex2d(0,1);
//		gl.glEnd();
	}

	boolean debug_hasReceivedAnyValidPackets = false;
	boolean debug_hasReceivedAnyNullPackets = false;

	public void renderMemoryBank(GL2 gl, BasicMemoryBank basicMemoryBank){
		final double SIZE = 1;
		final double HALF_SIZE = SIZE / 2;
		if(clientChannelHandler.getMostRecentFrameInfoPacket() == null){
			if(!debug_hasReceivedAnyValidPackets && debug_hasReceivedAnyNullPackets){
				logger.debug("Frame packet null");
			}else if (debug_hasReceivedAnyValidPackets){
				logger.warn("Most recent frame packet null - but we've previously received valid frame packets!");
			}
			return;
		}else{
			if(debug_hasReceivedAnyNullPackets || !debug_hasReceivedAnyValidPackets){
				logger.debug("First valid packet received!!");
			}
			debug_hasReceivedAnyValidPackets = true;
		}
		long ownerUUID = clientChannelHandler.getMostRecentFrameInfoPacket().clientUUID();
		Optional<BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData>> hostPos = basicMemoryBank.querySinglePackage(ownerUUID, Datas.EntityData.class, List.of(Datas.EntityPositionalData.class));

		//Check for query success
		if(hostPos.isPresent()){
			Vector2 hostEntityPosition = hostPos.get().getKnowledge(Datas.EntityPositionalData.class).getInfo().getPosition();
			Vector2 cameraDiff =  lastCameraPos.difference(hostEntityPosition);
			lastCameraPos.subtract(cameraDiff.multiply(0.003));
		}else{
			throw new IllegalStateException("The client's entity is unaware of its own position! Aaaaaa");
		}

		gl.glTranslated(-lastCameraPos.x, -lastCameraPos.y, 0);
		MultiQueryAccessor<CellPosition, Datas.CellData> cellQueryResults = basicMemoryBank.queryMultiPackage(Datas.CellData.class, List.of(Datas.CellEnterableData.class));

		logger.debug("# of known cells: " + cellQueryResults.getIndividualAccessors().size());
		for (var singleQueryAccessor : cellQueryResults.getIndividualAccessors().values()) {
			var enterableFragment = singleQueryAccessor.getKnowledge(Datas.CellEnterableData.class);
			CellPosition position = singleQueryAccessor.getIdentifier();
			gl.glPushMatrix();

			double alpha = isMemoryDataPresent(enterableFragment) ? 1 : 0.5;

			if (enterableFragment.getInfo().getEnterableStatus() == Datas.CellEnterableData.EnterableStatus.ENTERABLE) {
				gl.glColor3d(0, 0, alpha);
			} else {
				gl.glColor3d(alpha, 0, 0);
			}



			Vector2 centerPos = new Vector2(position.getPosition());
			gl.glTranslated(centerPos.x, centerPos.y, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2d(-HALF_SIZE, -HALF_SIZE);
			gl.glVertex2d(HALF_SIZE, -HALF_SIZE);
			gl.glVertex2d(HALF_SIZE, HALF_SIZE);
			gl.glVertex2d(-HALF_SIZE, HALF_SIZE);
			gl.glEnd();
			gl.glPopMatrix();
		}

		MultiQueryAccessor<Long, Datas.EntityData> entityQueryResults = basicMemoryBank.queryMultiPackage(Datas.EntityData.class, List.of(Datas.EntityPositionalData.class, Datas.EntityKineticData.class));
		logger.debug("# of known entities: " + entityQueryResults.getIndividualAccessors().size());
		for (BasicMemoryBank.SingleQueryAccessor<Long, Datas.EntityData> singleQueryAccessor : entityQueryResults.getIndividualAccessors().values()) {
			gl.glPushMatrix();

			var posFrag = singleQueryAccessor.getKnowledge(Datas.EntityPositionalData.class);
			var posData = posFrag.getInfo();
			var kinData = singleQueryAccessor.getKnowledge(Datas.EntityKineticData.class).getInfo();
			double alpha = isMemoryDataPresent(posFrag) ? 1 : 0.5;

			if (posData != null) {
				Vector2 centerPos = posData.getPosition();
				gl.glColor3d(0, alpha, 0);
				gl.glTranslated(centerPos.x, centerPos.y, 0);

				if(kinData != null){
					gl.glRotated(kinData.getRotation() * 180/Math.PI,0,0,1);
				}

				gl.glBegin(GL2.GL_POLYGON);
				gl.glVertex2d(-HALF_SIZE/2, -HALF_SIZE/2);
				gl.glVertex2d(HALF_SIZE/2, -HALF_SIZE/2);
				gl.glVertex2d(HALF_SIZE/2, HALF_SIZE/2);
				gl.glVertex2d(-HALF_SIZE/2, HALF_SIZE/2);
				gl.glEnd();
			}
			gl.glPopMatrix();
		}


	}
}
