package org.dramatech.atq.gesture;

import processing.core.PVector;

public class Weakness extends Gesture{
    boolean usingLeftArm;

    public Weakness(){
        super();
        super.name="Weakness";
        prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
    }



    //Checks is birth is happening,
    //In this case hand is below elbow which is below shoulder at first
    public boolean birthChecker(){
        if(state==-1){

            //If leftHand is below leftElbow which is below leftShoulder (or right)

            if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]
                    && GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_VERTICAL] && GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_VERTICAL]){
                //birth is totes happening
                //print("Got the starter");
                state=0;
                confidence=0.0f;
                return true;
            }
        }
        else if(state==0){
            //If hand is higher than it was previously

            if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) &&(GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]) &&
                    (GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_DECREASING])||
                    (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&&(GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK])&&
                            (GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_DECREASING])){
                //println("Got one of them rising");
                duration++;

                if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) ){
                    PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm=true;
                    tempo+=diffHand.mag();
                }
                if((GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING]) ){
                    PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
                    usingLeftArm=false;
                    tempo+=diffHand.mag();
                }
                return true;
            }
        }

        return false;
        //return true;
    }


    public boolean lifeChecker(){
        //If hand isn't going up anymore, that's a prerequisite
        if(state==0 || state==1){
            if(usingLeftArm){
                if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD] &&GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]){

                    PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm=true;
                    tempo+=diffHand.mag();
                    duration++;
                    return true;
                }

            }
            else{
                if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD] &&GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]){
                    PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
                    usingLeftArm=false;
                    tempo+=diffHand.mag();
                    duration++;
                    return true;
                }
            }
        }


        return false;
    }


    public boolean deathChecker(){
        if(state==1 || state==2){

            //If hand is below shoulder
            if(usingLeftArm){
                if((GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_DECREASING]) ||
                        (!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD] &&GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL])){

                    PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm=true;
                    tempo+=diffHand.mag();
                    duration++;
                    return true;
                }

            }
            else{
                if((GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_DECREASING]) ||
                        (!GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD] &&GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL])){
                    PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
                    usingLeftArm=false;
                    tempo+=diffHand.mag();
                    duration++;
                    return true;
                }
            }
        }


        return false;

    }
}
