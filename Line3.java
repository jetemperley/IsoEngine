

public class Line3{

    Vec3 dir;
    Vec3 point;

    Line3(Vec3 point1, Vec3 point2){
        dir = new Vec3(point1.x - point2.x, point1.y - point2.y, point1.z - point2.z);
        point = point1;
    }

    Line3(float x1, float y1, float z1, float x2, float y2, float z2){
        this(new Vec3(x1, y1, z1), new Vec3(x2, y2, z2));
    }

    // the Vec3 parameter a is the vector direction of a line
    Vec3 getNormal(Vec3 a){
        Vec3 normal = new Vec3(
            a.y*dir.z - a.z*dir.y,
            a.z*dir.x - a.x*dir.z,
            a.x*dir.y - a.y*dir.x
        );
        return normal;
    }

    static Vec3 getNormal(Vec3 a, Vec3 b){
        Vec3 normal = new Vec3(
            a.y*b.z - a.z*b.y,
            a.z*b.x - a.x*b.z,
            a.x*b.y - a.y*b.x
        );
        return normal;
    }
    
} 