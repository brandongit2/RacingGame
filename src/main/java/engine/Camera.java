package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends Entity {
    private static       Matrix4f projectionMatrix;
    private static       Matrix4f viewMatrix = new Matrix4f();
    private              Vector3f position   = new Vector3f(0, 0, 0);
    private Vector3f orientation = new Vector3f(0, 0, 0);
    private              float    fov;
    private static final float    Z_NEAR     = 0.01f;
    private static final float    Z_FAR      = 1000.0f;
    
    public Camera(Vector3f cameraPos, Vector3f cameraRot, float fov, String name) {
        Game.createCamera(name, this);
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
        
        transform();
    }
    
    public void rotate(float yaw, float pitch, float roll) {
        orientation.x += yaw;
        orientation.y += pitch;
        orientation.z += roll;
        orientation.x %= 360;
        orientation.y %= 360;
        orientation.z %= 360;
        if (orientation.y < -90) {
            orientation.y = -90;
        } else if (orientation.y > 90) {
            orientation.y = 90;
        }
        
        transform();
    }
    
    private void transform() {
        viewMatrix.rotation(0f, 0f, 1f, 0f);
        viewMatrix.rotate((float) Math.toRadians(orientation.z), new Vector3f(0f, 0f, 1f));
        viewMatrix.rotate((float) Math.toRadians(orientation.y), new Vector3f(1f, 0f, 0f));
        viewMatrix.rotate((float) Math.toRadians(orientation.x), new Vector3f(0f, 1f, 0f));
    
        viewMatrix.translate(position);
    }
    
    public Vector3f getOrientation() {
        return orientation;
    }
    
    Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
