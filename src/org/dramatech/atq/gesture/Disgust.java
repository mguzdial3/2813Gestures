package org.dramatech.atq.gesture;

import processing.core.PVector;

public class Disgust extends Gesture {
    boolean usingLeftArm;

    public Disgust() {
        super();
        super.name = "Disgust";
        prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
    }


    // Checks is birth is happening,
    // In this case hand is below elbow which is below shoulder at first
    public boolean birthChecker() {
        switch (state) {
            case NONE:
                // If leftHand is below leftElbow which is below leftShoulder (or right)
                if (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN]
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]
                        && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]
                        && GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_VERTICAL]
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_VERTICAL]) {

                    state = GestureState.BIRTH;
                    confidence = 0.0f;
                    return true;
                }
                break;
            case BIRTH:
                if (((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING])
                        && (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT])
                        && !GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ABOVE_TORSO])
                        || ((GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])
                        && (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT])
                        && !GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ABOVE_TORSO])
                        && !GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]) {

                    duration++;

                    if ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]
                            && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])) {
                        final PVector diffHand =
                                PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                        usingLeftArm = true;
                        tempo += diffHand.mag();

                    } else if ((!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]
                            && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])) {

                        final PVector diffHand =
                                PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
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
                if ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT])
                        && GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_EXTENDED]
                        && GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ABOVE_TORSO]) {

                    final PVector diffHand =
                            PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm = true;
                    tempo += diffHand.mag();
                    duration++;
                    return true;
                }

            } else {
                if ((GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT])
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ABOVE_TORSO]) {
                    final PVector diffHand =
                            PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                    usingLeftArm = false;
                    tempo += diffHand.mag();
                    duration++;
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
                if (((!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_ABOVE_HEAD]
                        && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]))
                        && (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN])
                        && (!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN])
                        && !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD]) {

                    final PVector diffHand =
                            PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm = true;
                    tempo += diffHand.mag();
                    duration++;
                    return true;
                }

            } else {
                if (((!GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_ABOVE_HEAD]
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]))
                        && (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN])
                        && (!GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN])
                        && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD]) {
                    final PVector diffHand =
                            PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
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
