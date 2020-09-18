import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.*;

public class VBO{

    // start: the location first INDEX in the buffer
    // length: the number of VERTECIES SPECIFIED in the sequence  
    // vertSize: the number of float elements per vertex (e.g. pos + norm + tc = 8)
    // NOTE cuurently using dublets
    final int start, length, vertexPattern, vertSize;

    VBO(int s, int l, int size){
        this(s, l, GL4.GL_TRIANGLES, size);
    }

    VBO(int s, int l, int vertexPattern, int size){
        start = s;
        length = l;
        this.vertexPattern = vertexPattern;
        vertSize = size;
    }

}