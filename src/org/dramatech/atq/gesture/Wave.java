package org.dramatech.atq.gesture;

import processing.core.PVector;

public class Wave extends Gesture {
    boolean usingLeft;

    public Wave() {
        super();
        super.name = "Wave";
        prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
    }

    // Checks if birth is happening,
    // In this case hand is below elbow which is below shoulder at first
    public boolean birthChecker() {
        switch (state) {
            case NONE:
                // If leftHand is below leftElbow which is below leftShoulder (or right)
                if (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN]
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]) {
                    state = GestureState.BIRTH;
                    confidence = 0.3f;
                    return true;
                }
                break;
            case BIRTH:
                if ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING])
                        || (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])) {
                    duration++;

                    final PVector diffHand;
                    if ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING])) {
                        diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                        usingLeft = true;
                    } else {
                        diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                        usingLeft = false;
                    }
                    tempo += diffHand.mag();

                    return true;
                }
                break;
        }
        return false;
    }

    public boolean lifeChecker() {
        // If hand isn't going up anymore, that's a prerequisite
        if (state == GestureState.BIRTH || state == GestureState.LIFE) {
            if (usingLeft) {
                if (((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT]
                        || GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_RIGHT]))) {
                    duration++;
                    final PVector handDiff =
                            PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    tempo += handDiff.mag();
                    return true;
                }
            } else {
                if (((GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_LEFT]
                        || GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT]))) {
                    duration++;
                    final PVector handDiff =
                            PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                    tempo += handDiff.mag();
                    return true;
                }
            }
        }
        return false;
    }


    public boolean deathChecker() {
        if (state == GestureState.LIFE || state == GestureState.DEATH) {
            // If hand is below shoulder
            if (usingLeft) {
                if (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING]) {
                    final PVector diffHand =
                            PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeft = true;
                    tempo += diffHand.mag();
                    duration++;
                    return true;
                }
            } else if (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING]) {
                final PVector diffHand =
                        PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                usingLeft = false;
                tempo += diffHand.mag();
                duration++;
                return true;
            }
        }
        return false;
    }
}
