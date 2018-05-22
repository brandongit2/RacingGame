package engine;

import org.joml.Vector3f;

public abstract class Entity {
    protected Vector3f position = new Vector3f(0, 0, 0);
    protected Vector3f rotation = new Vector3f(0, 0, 0);
}
