package org.dramatech.atq.gesture;

import processing.core.PApplet;
import processing.core.PVector;

public class Confusion extends Gesture{
    //And therefore right leg
    boolean usingLeftArm=false;



    public Confusion(){
        super();
        super.name="Confusion";
        prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
    }

    //Checks is birth is happening,
    //In this case hand is below elbow which is below shoulder at first
    public boolean birthChecker(){
        if(state==-1){

            //If leftHand is below leftElbow which is below leftShoulder (or right)

            if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]){
                //birth is totes happening
                //print("Got the starter");
                state=0;
                confidence=0.0f;
                return true;
            }
        }
        else if(state==0){
            //If hand is higher than it was previously
            if(prevJoints[GestureInfo.LEFT_HAND]==null){

            }


            if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) &&
                    (!GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_MED])||
                    (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&&
                            (!GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_MED])){
                //println("Got one of them rising");
                duration++;

                if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) ){
                    PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm=true;
                    tempo+=diffHand.mag();
                }
                else{
                    PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
                    usingLeftArm=false;
                    PApplet.println("Confusion: Not using 'left'");
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
                //If is moving back and forth
                if((GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_DECREASING])
                        && !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD]){
                    //println("Life of confusion");
                    duration++;
                    PVector handDiff = new PVector();
                    handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
                    tempo+=handDiff.mag();
                    return true;
                }
            }
            else{
                if((GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_DECREASING]) && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD]){
                    duration++;
                    PVector handDiff = new PVector();
                    handDiff = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
                    tempo+=handDiff.mag();
                    return true;
                }
            }
        }


        return false;
    }


    public boolean deathChecker(){
        //println("Death of confusion");
        if(state==1 || state==2){

            //If hand is below shoulder
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
}
