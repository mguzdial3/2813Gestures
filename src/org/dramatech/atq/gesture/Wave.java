package org.dramatech.atq.gesture;

import processing.core.PVector;

public class Wave extends Gesture {
    boolean usingLeft = false;
    public final int waveAmount = 10;

    public Wave() {
        super();
        super.name = "Wave";
        prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
    }

    // Checks if birth is happening,
    // In this case hand is below elbow which is below shoulder at first
    public boolean birthChecker() {
        switch (state) {
            case -1:
                // If leftHand is below leftElbow which is below leftShoulder (or right)
                if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] &&
                        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]) {
                    state = 0;
                    confidence = 0.3f;
                    return true;
                }
                break;
            case 0:
                if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) ||
                        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])) {
                    duration++;

                    if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) ) {
                        PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],
                                prevJoints[GestureInfo.LEFT_HAND]);
                        usingLeft = true;
                        tempo += diffHand.mag();
                    }
                    else {
                        PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND],
                                prevJoints[GestureInfo.RIGHT_HAND]);
                        usingLeft = false;
                        tempo += diffHand.mag();
                    }
                    return true;
                }
                break;
        }
        return false;
    }


    public boolean lifeChecker(){
        // If hand isn't going up anymore, that's a prerequisite
        if(state==0 || state==1) {
            if(usingLeft) {
                if(((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT] ||
                        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_RIGHT])) ){
                    duration++;
                    PVector handDiff = new PVector();
                    handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    tempo += handDiff.mag();
                    return true;
                }
            }
            else {
                if(((GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_LEFT] ||
                        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT])) ) {
                    duration++;
                    PVector handDiff = new PVector();
                    handDiff = PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                    tempo += handDiff.mag();
                    return true;
                }
            }
        }
        return false;
    }


    public boolean deathChecker() {
        if(state == 1 || state == 2) {
            // If hand is below shoulder
            if(usingLeft){
                if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING]) {
                    PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeft = true;
                    tempo += diffHand.mag();
                    duration++;
                    return true;
                }
            } else {
                if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING]) {
                    PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]);
                    usingLeft = false;
                    tempo += diffHand.mag();
                    duration++;
                    return true;
                }
            }
        }
        return false;
    }
}
