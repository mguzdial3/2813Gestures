package org.dramatech.atq.gesture;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class GestureController {
    ArrayList<Gesture> gestures = new ArrayList<Gesture>();
    PVector[] prevJoints;
    int[] counters;
    float prevRAngle, prevLAngle, rAngle, lAngle;

    public GestureController() {
        // NOT CHECK
        //final Gesture disgust = new Disgust();
        //gestures.add(disgust);

        // NOT CHECK
        final Gesture exhaustion = new Exhaustion();
        gestures.add(exhaustion);

        // CHECK
        final Gesture pain = new Pain();
        gestures.add(pain);

        // final Gesture confusion = new Confusion();
        // gestures.add(confusion);

        // CHECK, NOT MIX UP
        final Gesture terror = new Terror();
        gestures.add(terror);

        // CHECK
        final Gesture relief = new Relief();
        gestures.add(relief);

        // LONELINESS AND EXHAUSTION CHECK
        final Gesture loneliness = new Loneliness();
        gestures.add(loneliness);

        // TERROR MIX UP
        final Gesture excitement = new Excitement();
        gestures.add(excitement);

        // Gesture sadness = new Sadness();
        // gestures.add(sadness);

        final Weakness weakness = new Weakness();
        gestures.add(weakness);

        counters = new int[GestureInfo.NUMBER_OF_PIECES];
    }

    public Gesture updateGestures(final PVector[] joints) {
        if (prevJoints != null) {
            setGesturePieces(joints);
        }

        prevJoints = joints;
        prevRAngle = rAngle;
        prevLAngle = lAngle;
        return checkGestures(joints);
    }

    public void setGesturePieces(final PVector[] joints) {
        // Left hand rising (Might want to change these first four to be based on the normalized vector
    	GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING] = joints[GestureInfo.RIGHT_HAND].y<prevJoints[GestureInfo.RIGHT_HAND].y;
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING] = joints[GestureInfo.LEFT_HAND].y<prevJoints[GestureInfo.LEFT_HAND].y;
        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING] = joints[GestureInfo.RIGHT_HAND].y>prevJoints[GestureInfo.RIGHT_HAND].y;
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING] = joints[GestureInfo.LEFT_HAND].y>prevJoints[GestureInfo.LEFT_HAND].y;

        // Left hand down
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] =
                (joints[GestureInfo.LEFT_HAND].y < joints[GestureInfo.LEFT_ELBOW].y)
                        && joints[GestureInfo.LEFT_ELBOW].y < joints[GestureInfo.LEFT_SHOULDER].y;

        // Right hand down
        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN] =
                joints[GestureInfo.RIGHT_HAND].y < joints[GestureInfo.RIGHT_ELBOW].y
                        && joints[GestureInfo.RIGHT_ELBOW].y < joints[GestureInfo.RIGHT_SHOULDER].y;

        // Left hand moving left
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT] =
                joints[GestureInfo.LEFT_HAND].x < prevJoints[GestureInfo.LEFT_HAND].x;

        // Right hand moving left
        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_LEFT] =
                joints[GestureInfo.RIGHT_HAND].x < prevJoints[GestureInfo.RIGHT_HAND].x;

        // Left hand moving right
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_RIGHT]
                = joints[GestureInfo.LEFT_HAND].x > prevJoints[GestureInfo.LEFT_HAND].x;

        // Right hand moving right
        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT]
                = joints[GestureInfo.RIGHT_HAND].x > prevJoints[GestureInfo.RIGHT_HAND].x;

        final float distanceMeter = PVector.sub(joints[GestureInfo.HEAD], joints[GestureInfo.NECK]).mag() / 2;

        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL] =
                PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]).mag() < distanceMeter;
        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL] =
                PVector.sub(joints[GestureInfo.RIGHT_HAND], prevJoints[GestureInfo.RIGHT_HAND]).mag() < distanceMeter;


        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD] =
                PVector.sub(joints[GestureInfo.LEFT_HAND], joints[GestureInfo.HEAD]).mag() < distanceMeter * 2;
        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD] =
                PVector.sub(joints[GestureInfo.RIGHT_HAND], joints[GestureInfo.HEAD]).mag() < distanceMeter * 2;

        // Left knee going back
        if (GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK]) {
            GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK] =
                    joints[GestureInfo.LEFT_KNEE].z > prevJoints[GestureInfo.LEFT_KNEE].z;
            if (GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK]) {
                counters[GestureInfo.LEFT_KNEE_GOING_BACK] = 3;
            } else {
                if (counters[GestureInfo.LEFT_KNEE_GOING_BACK] != 0) {
                    counters[GestureInfo.LEFT_KNEE_GOING_BACK] -= 1;
                    GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK] = true;
                }
            }
        } else {
            GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK] =
                    joints[GestureInfo.LEFT_KNEE].z > prevJoints[GestureInfo.LEFT_KNEE].z;
            if (GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK]) {
                counters[GestureInfo.LEFT_KNEE_GOING_BACK] = 3;
            }
        }

        // Right knee going back
        if (GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]) {
            GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK] =
                    joints[GestureInfo.RIGHT_KNEE].z > prevJoints[GestureInfo.RIGHT_KNEE].z;
            if (GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]) {
                counters[GestureInfo.RIGHT_KNEE_GOING_BACK] = 3;
            } else {
                if (counters[GestureInfo.RIGHT_KNEE_GOING_BACK] != 0) {
                    counters[GestureInfo.RIGHT_KNEE_GOING_BACK] -= 1;
                    GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK] = true;
                }
            }
        } else {
            GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]
                    = joints[GestureInfo.RIGHT_KNEE].z > prevJoints[GestureInfo.RIGHT_KNEE].z;
            if (GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]) {
                counters[GestureInfo.RIGHT_KNEE_GOING_BACK] = 3;
            }
        }

        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BELOW_SHOULDER] =
                joints[GestureInfo.LEFT_HAND].y < joints[GestureInfo.LEFT_SHOULDER].y
                        && joints[GestureInfo.LEFT_ELBOW].y < joints[GestureInfo.LEFT_HAND].y;

        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BELOW_SHOULDER] =
                joints[GestureInfo.RIGHT_HAND].y < joints[GestureInfo.RIGHT_SHOULDER].y
                        && joints[GestureInfo.RIGHT_ELBOW].y < joints[GestureInfo.RIGHT_HAND].y;


        final PVector rUpperArm = PVector.sub(joints[GestureInfo.RIGHT_SHOULDER], joints[GestureInfo.RIGHT_ELBOW]);
        final PVector rLowerArm = PVector.sub(joints[GestureInfo.RIGHT_HAND], joints[GestureInfo.RIGHT_ELBOW]);

        final PVector lUpperArm = PVector.sub(joints[GestureInfo.LEFT_SHOULDER], joints[GestureInfo.LEFT_ELBOW]);
        final PVector lLowerArm = PVector.sub(joints[GestureInfo.LEFT_HAND], joints[GestureInfo.LEFT_ELBOW]);

        rAngle = PVector.angleBetween(rUpperArm, rLowerArm);
        lAngle = PVector.angleBetween(lUpperArm, lLowerArm);

        rAngle = (float) (180*(rAngle)/3.14f);
        lAngle = (float) (180*(lAngle)/3.14f);
        
        GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_DECREASING] = lAngle < prevLAngle;
        GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_DECREASING] = rAngle < prevRAngle;

        GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_MED] = lAngle < 100;
        GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_MED] = rAngle < 100;

        // Left hand down
        GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_UP] =
                (joints[GestureInfo.LEFT_HAND].y > joints[GestureInfo.LEFT_ELBOW].y)
                        && joints[GestureInfo.LEFT_ELBOW].y > joints[GestureInfo.LEFT_SHOULDER].y;

        // Right hand down
        GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_UP] =
                joints[GestureInfo.RIGHT_HAND].y > joints[GestureInfo.RIGHT_ELBOW].y
                        && joints[GestureInfo.RIGHT_ELBOW].y > joints[GestureInfo.RIGHT_SHOULDER].y;

        GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER] =
                PVector.sub(joints[GestureInfo.LEFT_HAND], joints[GestureInfo.RIGHT_HAND]).mag() < distanceMeter *2;

        // Hands above head
        GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_UP] =
                (joints[GestureInfo.RIGHT_HAND].y > joints[GestureInfo.RIGHT_ELBOW].y)
                        && joints[GestureInfo.RIGHT_ELBOW].y > joints[GestureInfo.RIGHT_SHOULDER].y;

        GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD] =
                joints[GestureInfo.RIGHT_HAND].y > joints[GestureInfo.HEAD].y
                        && joints[GestureInfo.LEFT_HAND].y > joints[GestureInfo.HEAD].y;

        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_ABOVE_HEAD] =
                joints[GestureInfo.LEFT_HAND].y > joints[GestureInfo.HEAD].y;

        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_ABOVE_HEAD] =
                joints[GestureInfo.RIGHT_HAND].y > joints[GestureInfo.HEAD].y;

        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_LEFT_OF_BODY] =
                joints[GestureInfo.LEFT_HAND].x < joints[GestureInfo.LEFT_SHOULDER].x;

        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_LEFT_OF_BODY] =
                joints[GestureInfo.RIGHT_HAND].x < joints[GestureInfo.LEFT_SHOULDER].x;

        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RIGHT_OF_BODY] =
                joints[GestureInfo.LEFT_HAND].x > joints[GestureInfo.RIGHT_SHOULDER].x;

        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RIGHT_OF_BODY] =
                joints[GestureInfo.RIGHT_HAND].x > joints[GestureInfo.RIGHT_SHOULDER].x;

        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BETWEEN_SHOULDERS] =
                !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_LEFT_OF_BODY]
                        && !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RIGHT_OF_BODY];

        GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BETWEEN_SHOULDERS] =
                !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_LEFT_OF_BODY]
                        && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RIGHT_OF_BODY];


        final float leftLowerArmDiff =
                PApplet.abs(joints[GestureInfo.LEFT_HAND].x - joints[GestureInfo.LEFT_ELBOW].x);
        final float leftUpperArmDiff =
                PApplet.abs(joints[GestureInfo.LEFT_SHOULDER].x - joints[GestureInfo.LEFT_ELBOW].x);

        GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_VERTICAL] =
                leftLowerArmDiff < distanceMeter && leftUpperArmDiff < distanceMeter;

        final float rightLowerArmDiff =
                PApplet.abs(joints[GestureInfo.RIGHT_HAND].x - joints[GestureInfo.RIGHT_ELBOW].x);
        final float rightUpperArmDiff =
                PApplet.abs(joints[GestureInfo.RIGHT_SHOULDER].x - joints[GestureInfo.RIGHT_ELBOW].x);

        GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_VERTICAL] =
                rightLowerArmDiff < distanceMeter && rightUpperArmDiff < distanceMeter;

        final float leftLowerArmDiffY =
                PApplet.abs(joints[GestureInfo.LEFT_HAND].y - joints[GestureInfo.LEFT_ELBOW].y);
        final float leftUpperArmDiffY =
                PApplet.abs(joints[GestureInfo.LEFT_SHOULDER].y - joints[GestureInfo.LEFT_ELBOW].y);

        GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_HORIZONTAL] =
                leftLowerArmDiffY < distanceMeter && leftUpperArmDiffY < distanceMeter;

        final float rightLowerArmDiffY =
                PApplet.abs(joints[GestureInfo.RIGHT_HAND].y - joints[GestureInfo.RIGHT_ELBOW].y);
        final float rightUpperArmDiffY =
                PApplet.abs(joints[GestureInfo.RIGHT_SHOULDER].y - joints[GestureInfo.RIGHT_ELBOW].y);

        GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_HORIZONTAL] =
                rightLowerArmDiffY < distanceMeter && rightUpperArmDiffY < distanceMeter;

        GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_EXTENDED] =
                joints[GestureInfo.RIGHT_HAND].x > joints[GestureInfo.RIGHT_ELBOW].x
                        && joints[GestureInfo.RIGHT_ELBOW].x > joints[GestureInfo.RIGHT_SHOULDER].x;

        GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_EXTENDED] =
                joints[GestureInfo.LEFT_HAND].x < joints[GestureInfo.LEFT_ELBOW].x
                        && joints[GestureInfo.LEFT_ELBOW].x < joints[GestureInfo.LEFT_SHOULDER].x;

        GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ABOVE_TORSO] =
                joints[GestureInfo.LEFT_HAND].y > joints[GestureInfo.TORSO].y;
        GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ABOVE_TORSO] =
                joints[GestureInfo.RIGHT_HAND].y > joints[GestureInfo.TORSO].y;
    }

    public Gesture checkGestures(final PVector[] joints) {
        float maxValue = -1.0f;
        Gesture maxGesture = null;

        // Go through and call each Gesture's update; see what it returns and return the max.
        for (final Gesture gesture : gestures) {
            final float confidenceValue = gesture.update(joints);
            if (confidenceValue > maxValue) {
                maxValue = confidenceValue;
                maxGesture = gesture;
            }
        }

        return maxGesture;
    }

    public void setAllElseToZero(final Gesture winner) {
        for (final Gesture gesture : gestures) {
            if (gesture != winner) {
                gesture.confidence = 0;
            }
        }
    }
}
