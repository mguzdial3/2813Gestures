package org.dramatech.atq.gesture;

import processing.core.PVector;

public class Confusion extends Gesture {
    // And therefore right leg
    boolean usingLeftArm;

    public Confusion() {
        super();
        super.name = "Confusion";
        prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
    }

    // Checks is birth is happening,
    // In this case hand is below elbow which is below shoulder at first
    public boolean birthChecker() {
        switch (state) {
            case NONE:
                // If leftHand is below leftElbow which is below leftShoulder (or right)
                if (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN]
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]) {
                    state = GestureState.BIRTH;
                    confidence = 0.0f;
                    return true;
                }
                break;
            case BIRTH:
                if (((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING])
                        && (!GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_MED]))
                        || ((GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])
                        && (!GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_MED]))) {
                    duration++;

                    if ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING])) {
                        final PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],
                                prevJoints[GestureInfo.LEFT_HAND]);
                        usingLeftArm = true;
                        tempo += diffHand.mag();
                    } else {
                        final PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND],
                                prevJoints[GestureInfo.RIGHT_HAND]);
                        usingLeftArm = false;
                        tempo += diffHand.mag();
                    }
                    return true;
                }
                break;
        }

        return false;
    }

    public boolean lifeChecker() {
        // If hand isn't going up anymore, that's a prerequisite
        if (state == GestureState.BIRTH || state == GestureState.LIFE) {
            if (usingLeftArm) {
                // If is moving back and forth
                if ((GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_DECREASING])
                        && !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD]) {
                    duration++;

                    final PVector handDiff =
                            PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    tempo += handDiff.mag();
                    return true;
                }
            } else {
                if ((GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_DECREASING])
                        && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD]) {
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
            if (usingLeftArm) {
                if (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD]
                        && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]) {

                    final PVector diffHand
                            = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm = true;
                    tempo += diffHand.mag();
                    duration++;
                    return true;
                }
            } else {
                if (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD]
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]) {
                    final PVector diffHand
                            = PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                    usingLeftArm = false;
                    tempo += diffHand.mag();
                    duration++;
                    return true;
                }
            }
        }

        return false;
    }
}
