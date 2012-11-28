package org.dramatech.atq.gesture;

import processing.core.PApplet;
import processing.core.PVector;

public class Disgust extends Gesture{
    boolean usingLeftArm = false;

    public Disgust(){
        super();
        super.name="Disgust";
        prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
    }



    //Checks is birth is happening,
    //In this case hand is below elbow which is below shoulder at first
    public boolean birthChecker(){
        if(state==-1){

            //If leftHand is below leftElbow which is below leftShoulder (or right)

            if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]
                    && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]
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
            if(prevJoints[GestureInfo.LEFT_HAND]==null){

            }


            if( ( (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) &&
                    (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT]) && !GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ABOVE_TORSO])
                    ||
                    ( (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&&
                            (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT]) && !GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ABOVE_TORSO])


                            && !GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]){
                //println("Got one of them rising");
                duration++;

                if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING] && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING] ) ){
                    PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm=true;
                    tempo+=diffHand.mag();

                    PApplet.println("Using left arm");
                }
                else if((!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING] ) ){
                    //println("Setting to usingRightHand");
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
        //println("Life checker got");

        //If hand isn't going up anymore, that's a prerequisite
        if(state==0 || state==1){
            if(usingLeftArm){

                if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT])
                        && GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_EXTENDED] && GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ABOVE_TORSO]){
                    PApplet.println("Life of left");
                    PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm=true;
                    tempo+=diffHand.mag();
                    duration++;
                    return true;
                }

            }
            else{
                //println("Using right arm");
                if((GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT])
                        && GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ABOVE_TORSO]){
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
        //println("Ever getting to death in disgust?");
        if(state==1 || state==2){

            /**
             println("Left hand above head: "+(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_ABOVE_HEAD]));
             println("Left hand still: "+ GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]);
             println("Right hand down: "+(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]));
             println("Left hand not down: "+(!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] ));
             */
            //If hand is below shoulder
            if(usingLeftArm){
                if(((!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_ABOVE_HEAD] && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]))
                        && (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN])
                        && (!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN]) && !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD]){

                    PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
                    usingLeftArm=true;
                    tempo+=diffHand.mag();
                    duration++;
                    return true;
                }

            }
            else{
                //println("Death with right");
                if(((!GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_ABOVE_HEAD] &&GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]))
                        && (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] )
                        && (!GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN])&& !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD]){
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
