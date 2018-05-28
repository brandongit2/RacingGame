package engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends Entity {
    private static       Matrix4f projectionMatrix;
    private static       Matrix4f viewMatrix = new Matrix4f();
    private              Vector3f position   = new Vector3f(0, 0, 0);
    private Vector3f orientation = new Vector3f(0, 0, -1);
    private              float    fov;
    private static final float    Z_NEAR     = 0.01f;
    private static final float    Z_FAR      = 1000.0f;
    
    public Camera(Vector3f cameraPos, Vector3f cameraRot, float fov, String name) {
        Game.cameras.put(name, this);
        Game.setCurrentCamera(name);
        
        viewMatrix.translation(cameraPos);
        rotate(cameraRot.x, cameraRot.y, cameraRot.z);
        
        this.fov = (float) Math.toRadians(fov);
        changeProjectionMatrix(Game.getCurrentWindow().getWidth(), Game.getCurrentWindow().getHeight());
    }
    
    void changeProjectionMatrix(int width, int height) {
        float aspectRatio = (float) width / height;
        projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, Z_NEAR, Z_FAR);
    }
    
    Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
    
    public void translate(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
        
        viewMatrix.translate(dx, dy, dz);
    }
    
    public void rotate(float yaw, float pitch, float roll) {
        Quaternionf quaternion = new Quaternionf(orientation.x, orientation.y, orientation.z, (float) Math.toRadians(roll));
        Vector3f v = orientation.cross(new Vector3f(0, 1, 0)).normalize();
        quaternion.add(v.x, v.y, v.z, (float) Math.cos(Math.toRadians(pitch) / 2));
        quaternion.add(0, 1, 0, (float) Math.toRadians(yaw));
        quaternion.normalize();
        
        System.out.println(quaternion);
        viewMatrix.rotate(quaternion);
        
        orientation.x += yaw;
        orientation.y += pitch;
        orientation.z += roll;
        orientation.normalize();
    }
    
    Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
