import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.math.Matrix4;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.nio.*;
import java.io.*;
import java.util.*;

import java.util.ArrayList;

public class IsoEngine extends GLCanvas implements GLEventListener, ActionListener {

   JFrame frame;
   javax.swing.Timer timer;
   static GL4 g;
   static GLU glu;
   static GLGraphics graphics;
   static int WIDTH = 800, HEIGHT = 800;
   static int mouseX = 0, mouseY = 0, pressX = 0, pressY = 0;
   static boolean printlog = false;
   static IsoEngine game;
   GameState state;
   GraphicsDevice[] gds;
   // key press/release flags whos indexes are 0-127
   // keyed according to its VK_CONSTANT integer value
   // and 128-130 are mouse buttons
   boolean[] keyPressFlags, keyTypedFlags;
   final static int KEY_COUNT = 128, MOUSE_KEY_COUNT = 3, N_KEYS = KEY_COUNT+MOUSE_KEY_COUNT, MOUSE_KEY_OFFSET = KEY_COUNT-1;
   final static int FRAME_TIME = 25;
   int typeDelay = 0;

   public static void main(String[] args) {
      final GLProfile profile = GLProfile.get(GLProfile.GL2);
      GLCapabilities capabilities = new GLCapabilities(profile);
      capabilities.setDoubleBuffered(true);
      IsoEngine game = new IsoEngine(capabilities);

   }

   public IsoEngine(GLCapabilities capabilities) {

      // super(capabilities);

      IsoEngine.game = this;

      keyPressFlags = new boolean[N_KEYS];
      keyTypedFlags = new boolean[N_KEYS];
      for (int i = 0; i < keyPressFlags.length; i++) {
         keyPressFlags[i] = false;
         keyTypedFlags[i] = false;
      }

      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      gds = ge.getScreenDevices();

      timer = new javax.swing.Timer(FRAME_TIME, this);

      this.addGLEventListener(this);
      this.setSize(WIDTH, HEIGHT);
      this.setAutoSwapBufferMode(false);

      Input input = new Input();
      this.addMouseListener(input);
      this.addKeyListener(input);
      this.addMouseWheelListener(input);
      this.addMouseMotionListener(input);

      // creating frame
      frame = new JFrame("Test");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // configure layout of content d
      frame.getContentPane().setLayout(new OverlayLayout(frame.getContentPane()));

      // add canvas to frame
      frame.getContentPane().add(this);

      // frame.setSize(frame.getContentPane().getPreferredSize());
      frame.pack();
      frame.setVisible(true);
      this.requestFocus();

      // cubes.add(new Cube(1, 1, 1));
      // cubes.add(new Cube(2, 2, 2));

      state = new GameState();

      timer.start();
   }

   @Override
   public void init(GLAutoDrawable arg0) {

      // method body
      // g = (GL4) arg0.getGL().getGL2();
      g = (GL4) GLContext.getCurrentGL();
      glu = GLU.createGLU(g);

      graphics = new GLGraphics(g);

   }

   public void actionPerformed(ActionEvent e) {
      setBackground(Color.WHITE);
      // System.out.println("action performed");
      this.display();

   }

   @Override
   public void display(GLAutoDrawable drawable) {
      // System.out.println("dispayed");
      // g = (GL4) drawable.getGL().getGL2();
      // super.display(drawable);
      g = (GL4) GLContext.getCurrentGL();
      checkInput();

      state.draw(graphics);

      // graphics.raycastMouse(g, mouseX, mouseY);
      IsoEngine.printlog = false;

   }

   // @Override
   // public void update(Graphics g) {
   // System.out.println("updated");
   // paint(g);
   // // g.fillRect(100, 100, 100, 100);

   // }

   // @Override
   // public void paint(Graphics g) {

   // // super.paint(g);
   // System.out.println("painted");

   // // swapBuffers();

   // }

   @Override
   public void dispose(GLAutoDrawable arg0) {
      // method body
   }

   @Override
   public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
      // method body
   }

   public void checkInput() {
      for (int i = 0; i < keyPressFlags.length; i++) {

         if (keyTypedFlags[i]) {
            state.keyPress(i, graphics);
            // System.out.println("typed");
            keyTypedFlags[i] = false;
         } else if (keyPressFlags[i]) {

            if (typeDelay >= 50) {
               state.keyPress(i, graphics); // System.out.println("pressed");
            } else {
               typeDelay += timer.getDelay();
            }

         }

      }
   }

   private class Input implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {

      int lastx = 0, lasty = 0;
      int xdrag = 0, ydrag = 0;

      public void mouseClicked(MouseEvent e) {
         // System.out.println("mouse triggered " + e.getButton());
         state.mouseClick(e.getButton());

      }

      public void mouseEntered(MouseEvent e) {

      }

      public void mouseExited(MouseEvent e) {

      }

      public void mousePressed(MouseEvent e) {
         // System.out.println("press");
         lastx = e.getX();
         lasty = e.getY();
         pressX = e.getX();
         pressY = e.getY();
         addFlag(e.getButton()+MOUSE_KEY_OFFSET);
      }

      public void mouseReleased(MouseEvent e) {
         removeFlag(e.getButton()+MOUSE_KEY_OFFSET);
      }

      public void mouseDragged(MouseEvent e) {
         
         xdrag = lastx - e.getX();
         ydrag = lasty - e.getY();
         lastx = e.getX();
         lasty = e.getY();

         for (int i = KEY_COUNT; i < keyPressFlags.length; i++){
            if (keyPressFlags[i]){
               state.mouseDrag(i, xdrag, ydrag);
            }
         }
         

      }

      public void mouseMoved(MouseEvent e) {
         mouseX = e.getX();
         mouseY = e.getY();
      }

      public void keyPressed(KeyEvent e) {
         
         addFlag(e.getKeyCode());

      }

      public void keyReleased(KeyEvent e) {
         removeFlag(e.getKeyCode());

      }

      public void keyTyped(KeyEvent e) {
         /*
          * keyTypedFlags[e.getKeyCode()] = true; System.out.println("key typed");
          */
      }

      public void mouseWheelMoved(MouseWheelEvent e) {
         state.mouseWheel(e.getWheelRotation());
      }

      void addFlag(int key){
         // System.out.println("added key " + key);
         if (!keyPressFlags[key] && !keyTypedFlags[key]) {
            keyPressFlags[key] = true;
            keyTypedFlags[key] = true;
            typeDelay = 0;
         }
      }

      void removeFlag(int key){
         keyPressFlags[key] = false;
      }

   }

}