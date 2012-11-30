package org.dramatech.atq.graphics;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;

public class SimpleOpenNIUser extends PApplet {

    public SimpleOpenNI context;

    public void setup() {
        context = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);

        // Enable depthMap generation
        context.enableDepth();

        // Enable skeleton generation for all joints
        context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

        background(200, 0, 0);

        stroke(0, 0, 255);
        strokeWeight(3);
        smooth();

        final int depthWidth = context.depthWidth();
        final int depthHeight = context.depthHeight();
        size(depthWidth, depthHeight);
    }

    public void draw() {
        // Update the cam
        context.update();

        // Draw depthImageMap
        final PImage depthImage = context.depthImage();
        image(depthImage, 0, 0);

        // Draw the skeleton if it's available
        if (context.isTrackingSkeleton(1)) {
            drawSkeleton(1);
        }
    }

    // Draw the skeleton with the selected joints
    public void drawSkeleton(final int userId) {
        // To get the 3d joint data:
        /*
        PVector jointPos = new PVector();
        context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_NECK,jointPos);
        */

        context.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

        context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

        context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

        context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

        context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);
    }

    // SimpleOpenNI events

    public void onNewUser(final int userId) {
        println("onNewUser - userId: " + userId);
        println("  start pose detection");

        context.startPoseDetection("Psi", userId);
    }

    public void onLostUser(final int userId) {
        println("onLostUser - userId: " + userId);
    }

    public void onStartCalibration(final int userId) {
        println("onStartCalibration - userId: " + userId);
    }

    public void onEndCalibration(final int userId, final boolean successful) {
        println("onEndCalibration - userId: " + userId + ", successful: " + successful);

        if (successful) {
            println("  User calibrated !!!");
            context.startTrackingSkeleton(userId);
        } else {
            println("  Failed to calibrate user !!!");
            println("  Start pose detection");
            context.startPoseDetection("Psi", userId);
        }
    }

    public void onStartPose(final String pose, final int userId) {
        println("onStartPose - userId: " + userId + ", pose: " + pose);
        println(" stop pose detection");

        context.stopPoseDetection(userId);
        context.requestCalibrationSkeleton(userId, true);

    }

    public void onEndPose(final String pose, final int userId) {
        println("onEndPose - userId: " + userId + ", pose: " + pose);
    }
}
