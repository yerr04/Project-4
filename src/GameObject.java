import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameObject {
    public AffineTransform transform; //the location/scale/rotation of our object
    public Shape shape; //the collider/rendered shape of this object
    public Material material; //data about the fill color, border color, and border thickness
    public ArrayList<ScriptableBehavior> scripts = new ArrayList<>(); //all scripts attached to the object
    public boolean active = true; //whether this gets Updated() and Draw()n

    //TODO: create the default GameObject use a default AffineTransform, default Material, and a 10x10 pix rectangle Shape at 0,0
    public GameObject(){
        this.transform = new AffineTransform();
        this.material = new Material();
        this.shape = new Rectangle2D.Float(0,0,10,10);
    }

    //TODO: create the default GameObject, but with its AffineTransform translated to the coordinate x,y
    public GameObject(int x, int y){
        this();
        this.transform.translate(x,y);
    }

    //TODO: 1) save the pen's old transform, 2) transform it based on this object's transform, 3) draw either the styled shape, or the image scaled to the bounds of the shape.
    public void Draw(Graphics2D pen){
        // update the transform of the pen to match the transform of the object
        AffineTransform oldTransform = pen.getTransform();
        pen.transform(this.transform);

        // draw the shape or image
        if (this.material.isShape){
            pen.setColor(this.material.fill);
            pen.fill(this.shape);
            pen.setColor(this.material.border);
            pen.setStroke(new BasicStroke(this.material.borderWidth));
            pen.draw(this.shape);
        } else {
            pen.drawImage(this.material.img, 0, 0, (int) this.shape.getBounds2D().getWidth(), (int) this.shape.getBounds2D().getHeight(), null);
        }

        // reset the transform of the pen
        pen.setTransform(oldTransform);
    }

    //TODO: start all scripts on the object
    public void Start(){
        for (ScriptableBehavior s : this.scripts){
            s.Start();
        }
    }

    //TODO: update all scripts on the object
    public void Update(){
        for (ScriptableBehavior s : this.scripts){
            s.Update();
        }
    }

    //TODO: move the GameObject's transform
    public void Translate(float dX, float dY){
        this.transform.translate(dX, dY);
    }

    //TODO: scale the GameObject's transform around the CENTER of its shape
    public void Scale(float sX, float sY){
        Rectangle2D bounds = this.shape.getBounds2D();
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        this.transform.translate(centerX, centerY);
        this.transform.scale(sX, sY);
        this.transform.translate(-centerX, -centerY);
    }

    //TODO: should return true if the two objects are touching (i.e., the intersection of their areas is not empty)
    public boolean CollidesWith(GameObject other){
        Area areaThis = new Area(this.shape);
        Area areaOther = new Area(other.shape);
        
        areaThis.transform(this.transform);
        areaOther.transform(other.transform);
    
        areaThis.intersect(areaOther);
        return !areaThis.isEmpty();
    }

    //TODO: should return true of the shape on screen contains the point
    public boolean Contains(Point2D point){
        try {
            Point2D transformedPoint = this.transform.inverseTransform(point, null);
            return this.shape.contains(transformedPoint);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
