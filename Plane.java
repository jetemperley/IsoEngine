

public class Plane{

    // normal: a vector with perpendicular direction t the plane
    // point: any point on the plane 
    Vec3 normal;
    Vec3 point;

    Plane(Vec3 normal, Vec3 point){
        this.normal = normal;
        this.point = point;
    }
    
    // creates a plane through the origin
    Plane(Vec3 norm){
        this(norm, new Vec3(0, 0, 0));
    }
    

    // uses d1 and d2 as the directions that define the plane, and any point the plane 
    Plane(Vec3 d1, Vec3 d2, Vec3 point){
        normal = getNormal(d1, d2);
        this.point = point;
    }   

    Vec3 getIntersect(Line3 line){
        float t = (normal.dot(point) - normal.dot(line.point)) / normal.dot(Vec3.normalise(line.dir));
        Vec3 out = new Vec3(line.point);
        out.add(Vec3.scale(Vec3.normalise(line.dir), t));
        return out;
    }

    // static Vec3 getIntersect(Plane plane, Line3 line){
    //     float d1 = plane.normal.dot(line.dir);
    //     float d2 = plane.normal.dot(line.point);
    //     if (d1 == 0)
    //         return null;
    //     float t = -(d2)/d1;
    //     Vec3 intersection = new Vec3(line.point);
    //     // scale instead of mult
    //     intersection.add(Vec3.mult(t, line.dir));
    //     return intersection;
    // }

    

    static Vec3 getNormal(Vec3 a, Vec3 b){
        Vec3 normal = new Vec3(
            a.y*b.z - a.z*b.y,
            a.z*b.x - a.x*b.z,
            a.x*b.y - a.y*b.x
        );
        return normal;
    }
}