package org.dramatech.atq.gesture;

class GestureInfo {

    // JOINT ARRAY
    public static final int LEFT_HAND = 0;
    public static final int LEFT_ELBOW = 1;
    public static final int LEFT_SHOULDER = 2;
    public static final int RIGHT_HAND = 3;
    public static final int RIGHT_ELBOW = 4;
    public static final int RIGHT_SHOULDER = 5;
    public static final int TORSO = 6;
    public static final int HEAD = 7;
    public static final int NECK = 8;
    public static final int LEFT_KNEE = 9;
    public static final int RIGHT_KNEE = 10;
    public static final int JOINTS_LENGTH = 11;

    // Boolean Pieces (These relate to positions in values of a boolean array)

    public static final int LEFT_HAND_RISING = 0;
    public static final int RIGHT_HAND_RISING = 1;
    public static final int LEFT_HAND_FALLING = 2;
    public static final int RIGHT_HAND_FALLING = 3;
    // Left hand is below shoulder and elbow
    public static final int LEFT_HAND_DOWN = 4;
    // Right hand is below shoulder and elbow
    public static final int RIGHT_HAND_DOWN = 5;
    public static final int LEFT_HAND_MOVING_LEFT = 6;
    public static final int RIGHT_HAND_MOVING_LEFT = 7;
    public static final int LEFT_HAND_MOVING_RIGHT = 8;
    public static final int RIGHT_HAND_MOVING_RIGHT = 9;

    // Left hand has not moved further than half neck length
    public static final int LEFT_HAND_STILL = 10;
    public static final int RIGHT_HAND_STILL = 11;

    // Less than half a neck length away from head
    public static final int LEFT_HAND_NEAR_HEAD = 12;
    public static final int RIGHT_HAND_NEAR_HEAD = 13;
    public static final int RIGHT_KNEE_GOING_BACK = 14;
    public static final int LEFT_KNEE_GOING_BACK = 15;

    // Below shoulder, but above elbow
    public static final int LEFT_HAND_BELOW_SHOULDER = 16;
    public static final int RIGHT_HAND_BELOW_SHOULDER = 17;

    public static final int LEFT_ARM_ANGLE_DECREASING = 18;
    public static final int RIGHT_ARM_ANGLE_DECREASING = 19;

    public static final int LEFT_ARM_ANGLE_MED = 20;
    public static final int RIGHT_ARM_ANGLE_MED = 21;

    // Left hand is above shoulder and elbow
    public static final int LEFT_ARM_UP = 22;
    // Right hand is below shoulder and elbow
    public static final int RIGHT_ARM_UP = 23;

    public static final int HANDS_TOGETHER = 24;
    public static final int HANDS_ABOVE_HEAD = 25;

    // Arms on sides of body
    public static final int LEFT_HAND_LEFT_OF_BODY = 26;
    public static final int RIGHT_HAND_LEFT_OF_BODY = 27;
    public static final int LEFT_HAND_RIGHT_OF_BODY = 28;
    public static final int RIGHT_HAND_RIGHT_OF_BODY = 29;

    // Hands in between shoulders
    public static final int LEFT_HAND_BETWEEN_SHOULDERS = 30;
    public static final int RIGHT_HAND_BETWEEN_SHOULDERS = 31;

    public static final int LEFT_HAND_ABOVE_HEAD = 32;
    public static final int RIGHT_HAND_ABOVE_HEAD = 33;

    public static final int LEFT_ARM_VERTICAL = 34;
    public static final int RIGHT_ARM_VERTICAL = 35;

    public static final int LEFT_ARM_HORIZONTAL = 36;
    public static final int RIGHT_ARM_HORIZONTAL = 37;

    public static final int LEFT_ARM_EXTENDED = 38;
    public static final int RIGHT_ARM_EXTENDED = 39;

    public static final int LEFT_ARM_ABOVE_TORSO = 40;
    public static final int RIGHT_ARM_ABOVE_TORSO = 41;

    // Length of array, should always be one more than previous entry
    public static final int NUMBER_OF_PIECES = 42;

    public static boolean[] gesturePieces;

    public static void init() {
        gesturePieces = new boolean[NUMBER_OF_PIECES];
    }
}
