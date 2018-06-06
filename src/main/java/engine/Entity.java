package engine;

import org.joml.Vector3f;

abstract class Entity {
    protected Vector3f position = new Vector3f(0, 0, 0);
    protected Vector3f rotation = new Vector3f(0, 0, 0);
    
    void translate(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }
    
    void rotate(float dx, float dy, float dz) {
        rotation.x += dx;
        rotation.y += dy;
        rotation.z += dz;
        rotation.x %= 360;
        rotation.y %= 360;
        rotation.z %= 360;
    }
}
