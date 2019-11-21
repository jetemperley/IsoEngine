import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.texture.*;

import java.util.*;
import java.io.*;
import java.nio.*;

public class GLGraphics {

    GL4 g;

    private int[] vao;
    private int[] vbo;

    // main rendering location and uniforms
    int rendering_program, ui_rend_prog;
    // rendering program locs
    int colorLoc, gridLoc, pvLoc, sunDirLoc, lightCamLoc, ambLightLoc;
    // ui rend prog locs
    int transLoc, altColorLoc;

    // shadoow shader location and uniforms
    int shadow_shader, temp_sShader;
    int shLightCamLoc, shGridLoc;
    private int[] customBuffers, customTextures;

    int currentGridLoc, currentColorLoc;

    Camera cam;
    World newWorld;
    Matrix4 temp;
    VBOManager vm;
    Assets assets;
    // matrixes for calulating shadows
    // mats to translate between camera space and 0,1 screen pixel space
    Matrix4 sunV, sunPV, transOffset, lightCam2;
    Vec3 sun;
    int iter;
    VBO vt;

    public GLGraphics(GL4 g) {

        this.g = g;

        assets = new Assets();
        temp = new Matrix4();

        vao = new int[3];
        vbo = new int[2];

        customBuffers = new int[3];
        customTextures = new int[3];

        g.glGenFramebuffers(customBuffers.length, customBuffers, 0);
        g.glGenTextures(customTextures.length, customTextures, 0);

        cam = new Camera();
        rendering_program = createShaderPrograms(g, "vert_shader.txt", "frag_shader.txt");
        ui_rend_prog = createShaderPrograms(g, "ui_vert_shader.txt", "ui_frag_shader.txt");
        createShadowShader(g);

        g.glGenVertexArrays(vao.length, vao, 0);
        g.glGenBuffers(vbo.length, vbo, 0);

        newWorld = new World();
        vm = new VBOManager(vbo[0]);
        // set up binds for vao[0], regular render

        pvLoc = g.glGetUniformLocation(rendering_program, "pv");
        colorLoc = g.glGetUniformLocation(rendering_program, "set_color");
        gridLoc = g.glGetUniformLocation(rendering_program, "grid_loc");
        sunDirLoc = g.glGetUniformLocation(rendering_program, "sun_dir");
        lightCamLoc = g.glGetUniformLocation(rendering_program, "light_cam");
        ambLightLoc = g.glGetUniformLocation(rendering_program, "ambientLight");

        currentGridLoc = gridLoc;

        shLightCamLoc = g.glGetUniformLocation(shadow_shader, "light_cam");
        shGridLoc = g.glGetUniformLocation(shadow_shader, "grid_loc");
        // Mat4Utl.writeMat4(cam.projection);

        initEnvProg(g);
        initMenuProg(g);

        // initShadowProg(g);

        sunV = new Matrix4();
        sunV.loadIdentity();
        sunV.rotate((float) (3 * Math.PI / 8), 1, 0, 0);
        sunV.rotate((float) (Math.PI), 0, 1, 0);
        sunV.rotate((float) (3 * Math.PI / 4), 0, 0, 1);
        sunV.translate(1, 2, 3);
        sunPV = new Matrix4();
        sunPV.loadIdentity();

        sun = new Vec3(-1f, -2f, 5f);
        sun.normalise();

        transOffset = new Matrix4();
        transOffset.loadIdentity();
        float[] f = { 0.5f, 0, 0, 0.5f, 0, 0.5f, 0, 0.5f, 0, 0, 0.5f, 0.5f, 0, 0, 0, 1 };
        float[] b = { 1 / (Camera.CLIP_EDGE * 2), 0, 0, 0.5f, 0, 1 / (Camera.CLIP_EDGE * 2), 0, 0.5f, 0, 0,
                1 / (-Camera.CLIP_DEPTH + Camera.CLIP_DEPTH), 0.5f, 0, 0, 0, 1 };
        transOffset.multMatrix(f);
        lightCam2 = new Matrix4();

    }

    void initEnvProg(GL4 g) {
        // vao0 is for the 3d environment renderer
        g.glBindVertexArray(vao[0]);
        // set up the custom depth buffer
        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[0]);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, customTextures[0], 0);
        g.glDrawBuffer(GL4.GL_FRONT);
        
        // set all the configurations for vao[0], render program
        g.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer verts = vm.genBuffer();
        g.glBufferData(GL4.GL_ARRAY_BUFFER, verts.limit() * 4, verts, GL4.GL_STATIC_DRAW);

        // configure pointer for position
        g.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 32, 0);
        g.glEnableVertexAttribArray(0);
        // config pointer for normals
        g.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 32, 12);
        g.glEnableVertexAttribArray(1);
        // pointer for tex coords
        g.glVertexAttribPointer(2, 2, GL4.GL_FLOAT, false, 32, 24);
        g.glEnableVertexAttribArray(2);
    }

    void initMenuProg(GL4 g) {
        // vertex array for the menues
        g.glBindVertexArray(vao[1]);

        // regular pointer for verts
        g.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
         // configure pointer for position
         g.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 32, 0);
         g.glEnableVertexAttribArray(0);
         // config pointer for normals
         g.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 32, 12);
         g.glEnableVertexAttribArray(1);
         // pointer for tex coords
         g.glVertexAttribPointer(2, 2, GL4.GL_FLOAT, false, 32, 24);
         g.glEnableVertexAttribArray(2);

        // pointer for texture coords
        g.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
        g.glActiveTexture(GL4.GL_TEXTURE0);
        g.glBindTexture(GL4.GL_TEXTURE_2D, Assets.joglTexLocs[0]);

        FloatBuffer buff = Buffers.newDirectFloatBuffer(GeoVerts.getFaceTexCoords());
        g.glBufferData(GL4.GL_ARRAY_BUFFER, buff.limit() * 4, buff, GL4.GL_STATIC_DRAW);

        transLoc = g.glGetUniformLocation(ui_rend_prog, "t");
        altColorLoc = g.glGetUniformLocation(ui_rend_prog, "alt_color");

        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[1]);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, customTextures[1], 0);
    }

    public void readyEnvProg() {

        this.g = g;

        g.glClear(GL4.GL_COLOR_BUFFER_BIT);
        enableEnvProg();        
        g.glClear(GL4.GL_DEPTH_BUFFER_BIT);

        // set uniforms
        g.glUniformMatrix4fv(pvLoc, 1, false, cam.getCamera(), 0);
        g.glUniform4f(colorLoc, 1, 1, 1, 1);
        g.glUniform3f(gridLoc, 0, 0, 0);
        g.glUniform3f(sunDirLoc, sun.x, sun.y, sun.z);
        g.glUniform3f(ambLightLoc, 0.4f, 0.4f, 0.4f);
        lightCam2.loadIdentity();
        lightCam2.multMatrix(transOffset);
        lightCam2.multMatrix(cam.projection);
        lightCam2.multMatrix(sunV);

        g.glUniformMatrix4fv(lightCamLoc, 1, false, sunPV.getMatrix(), 0);
        g.glUniform3f(gridLoc, 0, 0, 0);

        // sModel.loadIdentity();
        // sModel.scale(3, 3, 3);
        // sModel.multMatrix(cam.camera);
        // g.glUniformMatrix4fv(pvLoc, 1, false, sModel.getMatrix(), 0);

        // VBO v;
        // v = vm.getVBO(VBOManager.DIAMOND);
        // g.glDrawArrays(v.vertexPattern, v.start/6, v.length);

    }

    void enableEnvProg(){

        g.glBindVertexArray(vao[0]);
        g.glUseProgram(rendering_program);

        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
        // g.glDrawBuffer(GL4.GL_FRONT);

        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[0]);
        // g.glActiveTexture(GL4.GL_TEXTURE0);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, customTextures[0], 0);
        g.glDrawBuffer(GL4.GL_FRONT);

        g.glEnable(GL4.GL_DEPTH_TEST);
        g.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        g.glEnable(GL4.GL_BLEND);
        g.glDepthFunc(GL4.GL_LEQUAL);
        g.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_TRIANGLES);

        currentColorLoc = colorLoc;
    }

    void readyMenuProg() {

        g.glBindVertexArray(vao[1]);
        g.glUseProgram(ui_rend_prog);

        g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
        // g.glDrawBuffer(GL4.GL_FRONT);
        g.glDisable(GL4.GL_DEPTH_TEST);
        g.glDepthFunc(GL4.GL_LEQUAL);

        g.glUniform4f(altColorLoc, 0, 0, 0, 0);

        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
        
        // g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[1]);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, customTextures[1], 0);
        // g.glClear(GL4.GL_DEPTH_BUFFER_BIT);
        g.glDrawBuffer(GL4.GL_FRONT);

        // g.glDisable(GL4.GL_DEPTH_TEST);
        

        g.glActiveTexture(GL4.GL_TEXTURE0);
        g.glBindTexture(GL4.GL_TEXTURE_2D, Assets.joglTexLocs[0]);

        currentColorLoc = altColorLoc;

    }

    void drawMenuFace() {
        vt = vm.getVBO(VBOManager.CUBE_TOP);
        g.glDrawArrays(vt.vertexPattern, vt.start / vm.VERT_SIZE, vt.length);
    }

    // x, y, width and heiht are all 0-1 proportions of the screen space
    void drawMenuFace(float x, float y, float width, float height, int assetID) {

        temp.loadIdentity();

        // convert between 0-1 screen coords and opengl coords
        temp.translate((x * 2) - 1, (y * 2) + 1, 0);
        // scale from 0-1 proportion of the screen to opengl coords
        temp.scale(width * 2, height * 2, 1);
        
        g.glUniformMatrix4fv(transLoc, 1, false, temp.getMatrix(), 0);

        g.glActiveTexture(GL4.GL_TEXTURE0);
        g.glBindTexture(GL4.GL_TEXTURE_2D, Assets.joglTexLocs[assetID]);
        vt = vm.getVBO(VBOManager.CUBE_TOP);
        g.glDrawArrays(vt.vertexPattern, vt.start / vm.VERT_SIZE, vt.length);
    }


    void drawMenuFace(float x, float y, float width, float height, int assetID, float r, float g, float b, float a) {

        this.g.glUniform4f(altColorLoc, r, g, b, a);

        temp.loadIdentity();

        // convert between 0-1 screen coords and opengl coords
        temp.translate((x * 2) - 1, (y * 2) + 1, 0);
        // scale from 0-1 proportion of the screen to opengl coords
        temp.scale(width * 1.75f, height * 1.75f, 1);
        
        this.g.glUniformMatrix4fv(transLoc, 1, false, temp.getMatrix(), 0);
        this.g.glActiveTexture(GL4.GL_TEXTURE0);
        this.g.glBindTexture(GL4.GL_TEXTURE_2D, assetID);
        vt = vm.getVBO(VBOManager.CUBE_TOP);
        this.g.glDrawArrays(vt.vertexPattern, vt.start / vm.VERT_SIZE, vt.length);
        this.g.glUniform4f(altColorLoc, 0, 0, 0, 0);
    }

    void drawCube(boolean[] faces) {

        iter = 0;
        g.glActiveTexture(GL4.GL_TEXTURE1);
        g.glBindTexture(GL4.GL_TEXTURE_2D, Assets.joglTexLocs[Assets.DIRT]);
        while (iter < 6) {
            if (faces[iter]) {

                vt = vm.getVBO(iter);
                g.glDrawArrays(vt.vertexPattern, vt.start / vm.VERT_SIZE, vt.length);
            }
            iter++;
        }
    }

    void drawMesh(int i){
        vt = vm.getVBO(i);
        // g.glBindTexture(GL4.GL_TEXTURE_2D, Assets.joglTexLocs[vt.texID]);
        g.glDrawArrays(vt.vertexPattern, vt.start / vm.VERT_SIZE, vt.length);
    }

    void setDrawLoc(float x, float y, float z) {

        g.glUniform3f(currentGridLoc, x + cam.xoff, y + cam.yoff, z + cam.zoff);
    }

    void setCustDrawLoc(float x, float y, float z) {

        g.glUniform3f(currentGridLoc, x, y, z);
    }

    void setColorLoc(float red, float green, float blue, float alpha) {

        g.glUniform4f(currentColorLoc, red, green, blue, alpha);
    }

    float getDepthAt(int x, int y) {
        FloatBuffer buffer = FloatBuffer.allocate(4);
        g.glReadPixels(x, IsoEngine.HEIGHT-y, 1, 1, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, buffer);
        return buffer.get(0);
    }

    // buffer must have at least 4 bytes of memory allocated
    float getDepthAt(int x, int y, FloatBuffer buffer) {
        g.glReadPixels(x, IsoEngine.HEIGHT-y, 1, 1, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, buffer);
        return buffer.get(0);
    }

    private void shadowPass(GL4 g) {

        g.glUseProgram(shadow_shader);

        g.glBindVertexArray(vao[2]);

        g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[2]);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, customTextures[2], 0);
        // disable drawing colors, but enable the depth computation
        g.glDrawBuffer(GL4.GL_NONE);
        g.glEnable(GL4.GL_DEPTH_TEST);

        // pass in light POV mat4 as uniform
        sunPV.loadIdentity();
        sunPV.multMatrix(cam.projection);
        sunPV.multMatrix(sunV);

        g.glUniformMatrix4fv(shLightCamLoc, 1, false, sunPV.getMatrix(), 0);

        // draw calls almost as normal
        g.glEnable(GL4.GL_DEPTH_TEST);
        g.glDepthFunc(GL4.GL_LEQUAL);

        // Cell[][][] locs = newWorld.visiCells;
        // for (int y = 0; y < locs.length; y++) {
        // for (int x = 0; x < locs[y].length; x++) {
        // drawGridLocs(locs[y][x]);
        // }
        // }

    }

    private void drawPass(GL4 g) {
        g.glUseProgram(rendering_program);

    }

    void drawGridLocs(Cell[] locs) {
        for (int i = 0; i < locs.length; i++) {
            g.glUniform3f(currentGridLoc, locs[i].getX() + cam.xoff, locs[i].getY() + cam.yoff, locs[i].getZ() + cam.zoff);
            // System.out.println(locData[i] + " " + locData[i + 1] + " " + locData[i + 2]);

            drawCube();

        }
        g.glUniform3f(gridLoc, 0, 0, 0);
    }

    void raycastMouse(GL4 g, int mouseX, int mouseY) {

        float[] coords1 = cam.screenToWorld(mouseX, mouseY);

        float[] norm = { 0, 0, 3, 1 };
        float[] normout = new float[4];
        cam.view.multVec(norm, normout);
        Vec3 znorm = new Vec3(normout[0], normout[1], normout[2]);
        znorm.normalise();
        Plane plane = new Plane(znorm);

        Line3 mouseRay = new Line3(new Vec3(coords1[0], coords1[1], -Camera.CLIP_DEPTH),
                new Vec3(coords1[0], coords1[1], Camera.CLIP_DEPTH));

        Vec3 intersect = plane.getIntersect(mouseRay);

        g.glUniformMatrix4fv(pvLoc, 1, false, cam.getCamera(), 0);
        g.glUniform3f(gridLoc, intersect.x, intersect.y, intersect.z);

        Matrix4 camInv = new Matrix4();
        camInv.loadIdentity();
        camInv.multMatrix(cam.getCamera());
        camInv.invert();
        normout[0] = intersect.x;
        normout[1] = intersect.y;
        normout[2] = intersect.z;
        camInv.multVec(normout, norm);
        if (IsoEngine.printlog) {
            System.out.println("grid hovered " + norm[0] + " " + norm[1]);
        }
        norm[0] = (float) Math.floor(norm[0]);
        norm[1] = (float) Math.floor(norm[1]);
        // norm[2] = (float)Math.floor(norm[2]/20);
        norm[2] = 0;
        if (IsoEngine.printlog) {
            System.out.println("floored to " + norm[0] + " " + norm[1]);
        }
        g.glUniform3f(gridLoc, norm[0], norm[1], norm[2]);
        // drawSingleCube(g);

    }

    void drawCube() {
        VBO v;
        v = vm.getVBO(VBOManager.CUBE_TOP);
        g.glDrawArrays(v.vertexPattern, v.start / vm.VERT_SIZE, v.length);

        v = vm.getVBO(VBOManager.CUBE_BOT);
        g.glDrawArrays(v.vertexPattern, v.start / vm.VERT_SIZE, v.length);

        v = vm.getVBO(VBOManager.CUBE_LEFT);
        g.glDrawArrays(v.vertexPattern, v.start / vm.VERT_SIZE, v.length);

        v = vm.getVBO(VBOManager.CUBE_RIGHT);
        g.glDrawArrays(v.vertexPattern, v.start / vm.VERT_SIZE, v.length);

        v = vm.getVBO(VBOManager.CUBE_FRONT);
        g.glDrawArrays(v.vertexPattern, v.start / vm.VERT_SIZE, v.length);

        v = vm.getVBO(VBOManager.CUBE_BACK);
        g.glDrawArrays(v.vertexPattern, v.start / vm.VERT_SIZE, v.length);
    }

    private void createShadowShader(GL4 gl) {
        String[] vertShaderSource = readShaderSource("shadow_vert_shader.txt");
        int vShader = makeVShader(gl, vertShaderSource);
        // check for errors
        int[] compiled = new int[1];
        gl.glGetShaderiv(vShader, GL4.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 1) {
            System.out.println(". . . shadow vertex compilation success.");
        } else {
            System.out.println(". . . shadow vertex compilation failed.");
            printShaderLog(gl, vShader);
        }
        shadow_shader = gl.glCreateProgram();
        gl.glAttachShader(shadow_shader, vShader);
        gl.glLinkProgram(shadow_shader);
    }

    private int createShaderPrograms(GL4 gl, String vertSh, String fragSh) {

        String[] vertShaderSource = readShaderSource(vertSh);
        String[] fragShaderSource = readShaderSource(fragSh);

        int vShader = makeVShader(gl, vertShaderSource);
        int fShader = makeFShader(gl, fragShaderSource);

        // check for errors
        int[] compiled = new int[1];
        gl.glGetShaderiv(vShader, GL4.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 1) {
            System.out.println(". . . " + vertSh + " compilation success.");
        } else {
            System.out.println(". . . " + vertSh + " compilation failed.");
            printShaderLog(gl, vShader);
        }

        gl.glGetShaderiv(fShader, GL4.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 1) {
            System.out.println(". . . " + fragSh + " compilation success.");
        } else {
            System.out.println(". . . " + fragSh + " compilation failed.");
            printShaderLog(gl, fShader);
        }

        int rendering_program = makeProgram(gl, vShader, fShader);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return rendering_program;

    }

    private int makeVShader(GL4 gl, String[] source) {
        int id = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        gl.glShaderSource(id, source.length, source, null, 0);
        gl.glCompileShader(id);
        return id;
    }

    private int makeFShader(GL4 gl, String[] source) {
        int id = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        gl.glShaderSource(id, source.length, source, null, 0);
        gl.glCompileShader(id);
        return id;
    }

    private int makeProgram(GL4 gl, int vShader, int fShader) {
        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);
        return vfprogram;
    }

    private String[] readShaderSource(String filename) {
        Vector<String> lines = new Vector<String>();
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (IOException e) {
            System.out.println("IOException reading file: " + e);
            return null;
        }
        while (sc.hasNext()) {
            // System.out.println();
            lines.addElement(sc.nextLine());
        }
        String[] program = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            program[i] = (String) lines.elementAt(i) + "\n";
            // System.out.println(program[i]);
        }
        sc.close();
        return program;
    }

    private void printShaderLog(GL4 gl, int shader) {

        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;
        // determine the length of the shader compilation log
        gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0) {
            log = new byte[len[0]];
            gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
            System.out.println("Shader Info Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
        }
    }

    void initShadowProg(GL4 g) {
        // set configurations for vao[1], shadow texture program
        g.glBindVertexArray(vao[2]);

        g.glActiveTexture(GL4.GL_TEXTURE0);
        g.glBindTexture(GL4.GL_TEXTURE_2D, customTextures[2]);
        g.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT32, IsoEngine.WIDTH, IsoEngine.HEIGHT, 0,
                GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);

        g.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
        g.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
        g.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_COMPARE_MODE, GL4.GL_COMPARE_REF_TO_TEXTURE);
        g.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_COMPARE_FUNC, GL4.GL_LEQUAL);

        g.glBindFramebuffer(GL4.GL_FRAMEBUFFER, customBuffers[2]);
        g.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, customTextures[2], 0);
        g.glDrawBuffer(GL4.GL_NONE);
        g.glEnable(GL4.GL_DEPTH_TEST);

        g.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        // configure pointer for position
        g.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 24, 0);
        g.glEnableVertexAttribArray(0);
    }

}