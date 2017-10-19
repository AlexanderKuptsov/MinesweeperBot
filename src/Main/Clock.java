package Main;

import org.lwjgl.Sys;

public enum Clock {
    INSTANCE;

    private boolean paused = false;
    private long lastFrame, totalTime;
    private float deltaTime = 0;

    private long getTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }

    public float getDelta() {
        long currentTime = getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = getTime();
        float maxDelay = 0.05f;
        if (delta * 0.001f > maxDelay) return maxDelay;
        return delta * 0.001f;
    }

    public float delta() {
        if (paused) return 0;
        else return deltaTime;
    }

    public float totalTime() {
        return totalTime;
    }

    public void update() {
        deltaTime = getDelta();
        totalTime += deltaTime;
    }

    private void pause() {
        paused = !paused;
    }
}
