package org.dramatech.atq.gesture;

import processing.core.PVector;

public class Terror extends Gesture {

    public Terror() {
        super();
        super.name = "Terror";
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
                    //birth is totes happening
                    //print("Got the starter");
                    state = GestureState.BIRTH;
                    confidence = 0.0f;
                    return true;
                }
                break;
            case BIRTH:
                if ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING])
                        && (GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_DECREASING])
                        && (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])
                        && (GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_DECREASING])) {
                    duration++;
                    final PVector diffHand =
                            PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    tempo += diffHand.mag();
                    final PVector diffHandR =
                            PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);

                    tempo += (diffHand.mag() + diffHandR.mag()) / 2;

                    return true;
                }
                break;
        }
        return false;
    }


    public boolean lifeChecker() {
        // If hand isn't going up anymore, that's a prerequisite
        if (state == GestureState.BIRTH || state == GestureState.LIFE) {
            //If is moving back and forth
            if ((GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_MED])
                    && !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN]
                    && (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING])
                    && (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])
                    && (GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_MED])
                    && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]
                    && !(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BETWEEN_SHOULDERS])
                    && !(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BETWEEN_SHOULDERS])) {

                duration++;
                final PVector handDiff =
                        PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                final PVector handDiffR =
                        PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                tempo += (handDiff.mag() + handDiffR.mag());

                return true;
            }
        }
        return false;
    }


    public boolean deathChecker() {
        if (state == GestureState.LIFE || state == GestureState.DEATH) {
            if (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]
                    && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]) {
                final PVector diffHand =
                        PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                final PVector diffHandR =
                        PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                tempo += (diffHand.mag() + diffHandR.mag()) / 2;
                duration++;
                return true;
            }

        }
        return false;
    }
}
