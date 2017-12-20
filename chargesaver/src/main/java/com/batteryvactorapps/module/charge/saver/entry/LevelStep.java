package com.batteryvactorapps.module.charge.saver.entry;

/**
 * Created by song on 16/4/19.
 */
public class LevelStep {
    private int currentLevel;
    private int lastLevelUpdateTime;

    public void start(int level) {
        this.currentLevel = level;
        lastLevelUpdateTime = (int) (System.currentTimeMillis() / 1000L);
    }

    public int update(int level) {
        if (currentLevel != level) {
            final int offset = Math.abs(level - currentLevel);
            int offsetTime = 0;
            if (offset == 1) {
                offsetTime = (int) (System.currentTimeMillis() / 1000L - lastLevelUpdateTime);
                start(level);
            }
            return offsetTime;
        } else {
            return 0;
        }
    }
}
