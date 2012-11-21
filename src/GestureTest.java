import processing.core.*; 
import processing.xml.*; 

import SimpleOpenNI.*; 
import oscP5.*; 
import netP5.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class GestureTest extends PApplet {

/* --------------------------------------------------------------------------
 * SimpleOpenNI User Test
 * --------------------------------------------------------------------------
 * Processing Wrapper for the OpenNI/Kinect library
 * http://code.google.com/p/simple-openni
 * --------------------------------------------------------------------------
 * prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
 * date:  02/16/2011 (m/d/y)
 * ----------------------------------------------------------------------------
 */






OscP5 oscP5;
NetAddress myRemoteLocation;
SimpleOpenNI  context;
boolean       autoCalib=true;
GestureController controller;
int checkGestures=0;
Gesture currGesture;

PFrame gestureInfoFrame;

public void setup()
{
  context = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);
   
  // enable depthMap generation 
  if(context.enableDepth() == false)
  {
     println("Can't open the depthMap, maybe the camera is not connected!"); 
     exit();
     return;
  }
  
  // enable skeleton generation for all joints
  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
 
  background(200,0,0);

  stroke(0,0,255);
  strokeWeight(3);
  smooth();
  
  
  gestureInfoFrame = new PFrame("Current Gesture");
  //wave = new Wave(0);
  PFont font;
  font = loadFont("Serif-30.vlw"); 
  gestureInfoFrame.s.textFont(font); 
  controller = new GestureController();
  GestureInfo.init();
  
  // start oscP5, telling it to listen for incoming messages at port 5001 */
  oscP5 = new OscP5(this,50001);
 
  // set the remote location to be the localhost on port 5001
  myRemoteLocation = new NetAddress("127.0.0.1",57131);
  
  size(context.depthWidth(), context.depthHeight()); 
}

public void draw()
{
  // update the cam
  context.update();
  
  // draw depthImageMap
  image(context.depthImage(),0,0);
  
  //draw center of mass
  //TRYING: Don't really need this
  PVector pos = new PVector();
  pushStyle();
  strokeWeight(15);
  for (int userId=1;userId <= 10;userId++)
  {
    if(context.isTrackingSkeleton(userId)){
      context.getCoM(userId, pos);
      PVector displayPos = new PVector();
      context.convertRealWorldToProjective(pos, displayPos);
      stroke(0,255,0);
      point(displayPos.x, displayPos.y);
    }
  }  
  popStyle();
  
  // draw the skeleton if it's available
  for(int i = 0; i<10; i++){
    if(context.isTrackingSkeleton(i)){
      fill(0,0,255);
      drawSkeleton(i);
      
      if(checkGestures==3){
        checkGestures=0;
        PVector[] joints = new PVector[GestureInfo.JOINTS_LENGTH];  
        
        PVector jointPos = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_LEFT_ELBOW,jointPos);
        joints[GestureInfo.LEFT_ELBOW] = jointPos;
        
        PVector jointPos1 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_LEFT_HAND,jointPos1);
        joints[GestureInfo.LEFT_HAND] = jointPos1;
        
        PVector jointPos2 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_LEFT_SHOULDER,jointPos2);
        joints[GestureInfo.LEFT_SHOULDER] = jointPos2;
        
        PVector jointPos3 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_TORSO,jointPos3);
        joints[GestureInfo.TORSO] = jointPos3;
        
        PVector jointPos4 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_RIGHT_ELBOW,jointPos4);
        joints[GestureInfo.RIGHT_ELBOW] = jointPos4;
        
        PVector jointPos5 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_RIGHT_HAND,jointPos5);
        joints[GestureInfo.RIGHT_HAND] = jointPos5;
        
        PVector jointPos6 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_RIGHT_SHOULDER,jointPos6);
        joints[GestureInfo.RIGHT_SHOULDER] = jointPos6;
        
        PVector jointPos7 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_HEAD,jointPos7);
        joints[GestureInfo.HEAD] = jointPos7;
        
        PVector jointPos8 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_NECK,jointPos8);
        joints[GestureInfo.NECK] = jointPos8;
        
        PVector jointPos9 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_LEFT_KNEE,jointPos9);
        joints[GestureInfo.LEFT_KNEE] = jointPos9;
        
        PVector jointPos10 = new PVector();
        context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_RIGHT_KNEE,jointPos10);
        joints[GestureInfo.RIGHT_KNEE] = jointPos10;
        
        
        Gesture response = controller.updateGestures(joints);
        
        if(response!=null){
          currGesture = response;
          
        }
      }
      else{
        checkGestures++;
      }
    }
    
  }
  gestureInfoFrame.s.fill(0);
  gestureInfoFrame.s.rect(0,0,gestureInfoFrame.w,gestureInfoFrame.h);
  if(currGesture!=null){
    if(currGesture.confidence>0.1f){
      //Erase previous
      
      
      if(currGesture.confidence>0){
        gestureInfoFrame.s.fill(255,255,255);
        gestureInfoFrame.s.text(currGesture.name+". Con: "+currGesture.confidence +". Dur: "+currGesture.duration +". Tempo: "+currGesture.tempo,0,50);
      }  
    }  
  }
}

// draw the skeleton with the selected joints
public void drawSkeleton(int userId)
{
  // to get the 3d joint data
  /*
  PVector jointPos = new PVector();
  context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_NECK,jointPos);
  println(jointPos);
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


public void sendJointPosition(int userId)
{
  PVector centerOfMass = new PVector();
 
  //////////////////
  //HEAD AND TORSO
  //////////////////  
  
  // get the joint position of the right hand
  context.getCoM(userId,centerOfMass);
  
   // create an osc message
  OscMessage gestureMessage = new OscMessage("/"+currGesture.name);
 
 
 // send joint position of all axises by OSC
  gestureMessage.add(centerOfMass.x);
  gestureMessage.add(centerOfMass.y); 
  gestureMessage.add(centerOfMass.z);
  
  
  gestureMessage.add(currGesture.confidence);
  gestureMessage.add(currGesture.duration);
  gestureMessage.add(currGesture.tempo);
  
  oscP5.send(gestureMessage, myRemoteLocation); 
  
  
}


// -----------------------------------------------------------------
// SimpleOpenNI events

public void onNewUser(int userId)
{
  println("onNewUser - userId: " + userId);
  println("  start pose detection");
  
  if(autoCalib)
    context.requestCalibrationSkeleton(userId,true);
  else    
    context.startPoseDetection("Psi",userId);
}

public void onLostUser(int userId)
{
  println("onLostUser - userId: " + userId);
}

public void onStartCalibration(int userId)
{
  println("onStartCalibration - userId: " + userId);
}

public void onEndCalibration(int userId, boolean successfull)
{
  println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);
  
  if (successfull) 
  { 
    println("  User calibrated !!!");
    context.startTrackingSkeleton(userId); 
  } 
  else 
  { 
    println("  Failed to calibrate user !!!");
    println("  Start pose detection");
    context.startPoseDetection("Psi",userId);
  }
}

public void onStartPose(String pose,int userId)
{
  println("onStartPose - userId: " + userId + ", pose: " + pose);
  println(" stop pose detection");
  
  context.stopPoseDetection(userId); 
  context.requestCalibrationSkeleton(userId, true);
 
}

public void onEndPose(String pose,int userId)
{
  println("onEndPose - userId: " + userId + ", pose: " + pose);
}
public class Gesture{
  public float tempo, duration, confidence;
  public String name;
  //Definitions for types of gestures
  public final int UPPERBODY=0;
  public final int LOWERBODY=1;
  public final int ARMS=2;
  public final int FULLBODY=3;
  
  //ACTUAL TYPE OF THIS GESTURE, NOT CURRENTLY SET, AS THIS IS JUST THE BASE CLASS
  //Also allows us to switch which joints we're grabbing based on what part of the gesture we're in
  public int type;
  
  //0 is birth, 1 is life, 2 is death
  public int state;
  public PVector[] joints, prevJoints;
    
  public Gesture(){
    this.type=UPPERBODY;  
  }
  
  public Gesture(int type){
    this.type=type;
    
    state=-1;
  }
  
  //Returns confidence at this moment
  public float update(PVector[] joints){
    this.joints=joints;
    
    if(prevJoints[GestureInfo.LEFT_HAND]!=null){
      if(state==-1){
          confidence=0;
          if(birthChecker()){
            state=0;
          }
      }
      else if(state==0){
        boolean birthing = birthChecker();
        
        if(!birthing){
          if(lifeChecker()){
            //println("0.5");
            state=1;
            confidence=0.5f;
          }
          else{
            state=-1;
            duration=0;
            tempo=0;
            confidence=0;
          }
        }
        else{
          if(confidence<0.3f){
            confidence+=0.001f;
          }
          else{
            confidence-=0.02f;
          }
        }
        
      }
      else if(state==1){
        
        boolean living =lifeChecker();
        
        if(!living){
          
          if(deathChecker()){
            state=2;
            confidence =1.0f;
          }
          else{
            
            state=-1;
            duration=0;
            tempo=0;
            confidence=0;
          }
        }
        else{
          if(confidence<0.6f){
            confidence+=0.001f;
          }
          else{
            confidence-=0.02f;
          }
        }
        
      }
      else if(state==2){
        
        boolean dying = deathChecker();
        
        //If no longer dying, reset
        if(!dying){
         state=-1;
         duration=0;
         tempo=0; 
        }
        else{
          if(confidence<1){
            confidence+=0.001f;
          }
          else{
            confidence-=0.02f;
          }
        }
        
      }
    }
    
    prevJoints = joints;
    return confidence;
  }
  
  public boolean birthChecker(){
    if(state==-1){
      return false;
    }
    else if(state==0){
      //Previously has been birth
      return true;
    }
    return false;
  }
  
  public boolean lifeChecker(){
    if(state==0){
      return false;
    }
    else if(state==1){
      return true;
    }
    return false;
  }
  
  public boolean deathChecker(){
    return false;
  }
}

/**
----------------------------------------------------------------------------------------------------------------
*/
public class Wave extends Gesture{
  boolean usingLeft=false;
  public final int waveAmount=10;

  
  
  public Wave(){
    super();
    super.name="Wave";
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
          confidence=0.3f;
          return true;
       }
    }
    else if(state==0){
      //If hand is higher than it was previously
      if(prevJoints[GestureInfo.LEFT_HAND]==null){
        
      }
      
      
      if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) ||
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])){
        //println("Got one of them rising");          
        duration++;
        
        if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) ){
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
          usingLeft=true;
          tempo+=diffHand.mag();
        }
        else{
          PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          usingLeft=false;
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
      if(usingLeft){
        
        if(((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT] || GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_RIGHT])) ){
          print("Ever happening?");
          duration++;
          PVector handDiff = new PVector();
          handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          tempo+=handDiff.mag();
          return true;
        }
      }
      else{
         if(((GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_LEFT] || GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT])) ){
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
    if(state==1 || state==2){
      
      //If hand is below shoulder
      if(usingLeft){
        if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING]){
          
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          usingLeft=true;
          tempo+=diffHand.mag();
          duration++;
          return true;
        }
        
      }
      else{
        if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING]){
          PVector diffHand = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          usingLeft=false;
          tempo+=diffHand.mag();
          duration++;
          return true;
        }
      }
    }
    
    
    return false;
    
  }
}


/**
----------------------------------------------------------------------------------------------------------------
*/
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
          println("Confusion: Not using 'left'");
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


/**
----------------------------------------------------------------------------------------------------------------
*/
public class Terror extends Gesture{
  //And therefore right leg

  public Terror(){
    super();
    super.name="Terror";
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
      (GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_DECREASING])&&
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
      (GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_DECREASING])){
        //println("Got one of them rising");          
        duration++;
        PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
        tempo+=diffHand.mag();
        PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
       
          //Average of two vectors
        tempo+=(diffHand.mag()+diffHandR.mag())/2;
        
        return true;
      }
    }
    
    return false;
    //return true;
  }
  
  
  public boolean lifeChecker(){
    //If hand isn't going up anymore, that's a prerequisite
    if(state==0 || state==1){
        //If is moving back and forth
        if((GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_MED]) && !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN]
        && (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])
    && (GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_MED]) && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]
    && !(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BETWEEN_SHOULDERS]) && !(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BETWEEN_SHOULDERS])){
          
          duration++;
          PVector handDiff = new PVector();
          
          //Average
          handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector handDiffR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          tempo+=(handDiff.mag()+handDiffR.mag());
          return true;
        }
     }
      
    
    
    
    return false;
  }
  
  
  public boolean deathChecker(){
    if(state==1 || state==2){
      
      if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]
      && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]){
          
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          
          //Average
          tempo+=(diffHand.mag()+diffHandR.mag())/2;
          duration++;
          return true;
        }
        
      }
      
    
    
    
    return false;
    
  }
}

/**
----------------------------------------------------------------------------------------------------------------
*/
public class Sadness extends Gesture{
  //And therefore right leg

  
  
  public Sadness(){
    super();
    super.name="Sadness";
    prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
  }
  
  
  
  //Checks is birth is happening,
  //In this case hand is below elbow which is below shoulder at first 
  public boolean birthChecker(){
    if(state==-1){
      
      //If leftHand is below leftElbow which is below leftShoulder (or right)
     // println("Ever got hands together?: "+GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]);
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
      if(prevJoints[GestureInfo.LEFT_HAND]==null){
        
      }
      
      if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
        GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER] && 
        !GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD]){
        //println("Got one of them rising");          
        duration++;
        PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
        tempo+=diffHand.mag();
        PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
       
          //Average of two vectors
        tempo+=(diffHand.mag()+diffHandR.mag())/2;
        
        return true;
      }
    }
    
    return false;
    //return true;
  }
  
  
  public boolean lifeChecker(){
    //print("Getting to life of sadness");
    //If hand isn't going up anymore, that's a prerequisite
    if(state==0 || state==1){
        //If is moving back and forth
        if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
        GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER] && 
        GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD]){
          
          duration++;
          PVector handDiff = new PVector();
          
          //Average
          handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector handDiffR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          tempo+=(handDiff.mag()+handDiffR.mag());
          return true;
        }
     }
      
    
    
    
    return false;
  }
  
  
  public boolean deathChecker(){
    //print("Getting to death of sadness");
    if(state==1 || state==2){
      
      if(GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD] && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]
      && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]){
          
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          
          //Average
          tempo+=(diffHand.mag()+diffHandR.mag())/2;
          duration++;
          return true;
        }
        
      }
      
    
    
    
    return false;
    
  }
}


/**
----------------------------------------------------------------------------------------------------------------
*/
public class Exhaustion extends Gesture{
  
  
  public Exhaustion(){
    super();
    super.name="Exhaustion";
    prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
  }
  
  
  
  //Checks is birth is happening,
  //In this case hand is below elbow which is below shoulder at first 
  public boolean birthChecker(){
    if(state==-1){
      
      //If leftHand is below leftElbow which is below leftShoulder (or right)
     // println("Ever got hands together?: "+GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]);
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
      if(prevJoints[GestureInfo.LEFT_HAND]==null){
        
      }
      
      if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
        !GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD]){
         
        //println("Got one of them rising");          
        duration++;
        PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
        tempo+=diffHand.mag();
        PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
       
          //Average of two vectors
        tempo+=(diffHand.mag()+diffHandR.mag())/2;
        
        return true;
      }
    }
    
    return false;
    //return true;
  }
  
  
  public boolean lifeChecker(){
    //If hand isn't going up anymore, that's a prerequisite
    if(state==0 || state==1){
        //If is moving back and forth
        if( GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD]
        && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]
        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]){
          
          duration++;
          PVector handDiff = new PVector();
          
          //Average
          handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector handDiffR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          tempo+=(handDiff.mag()+handDiffR.mag());
          return true;
        }
     }
      
    
    
    
    return false;
  }
  
  
  public boolean deathChecker(){
    if(state==1 || state==2){
      
      if(((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING])&& 
      (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING]))){
          
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          
          //Average
          tempo+=(diffHand.mag()+diffHandR.mag())/2;
          duration++;
          return true;
        }
        
      }
      
    
    
    
    return false;
    
  }
}

/**
----------------------------------------------------------------------------------------------------------------
*/
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


/**
----------------------------------------------------------------------------------------------------------------
*/
public class Pain extends Gesture{
  
  
  public Pain(){
    super();
    super.name="Pain";
    prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
  }
  
  
  
  //Checks is birth is happening,
  //In this case hand is below elbow which is below shoulder at first 
  public boolean birthChecker(){
    if(state==-1){
      
      //If leftHand is below leftElbow which is below leftShoulder (or right)
     // println("Ever got hands together?: "+GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]);
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
      if(prevJoints[GestureInfo.LEFT_HAND]==null){
        
      }
      
      if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] 
        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]&& 
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_RIGHT] 
        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_LEFT]){
        //println("Got one of them rising");          
        duration++;
        PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
        tempo+=diffHand.mag();
        PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
       
          //Average of two vectors
        tempo+=(diffHand.mag()+diffHandR.mag())/2;
        
        return true;
      }
    }
    
    return false;
    //return true;
  }
  
  
  public boolean lifeChecker(){
    //If hand isn't going up anymore, that's a prerequisite
    if(state==0 || state==1){
        //If is moving back and forth
        if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BELOW_SHOULDER] 
        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BELOW_SHOULDER]&& 
        GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_RIGHT] 
        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_LEFT]){
          
          duration++;
          PVector handDiff = new PVector();
          
          //Average
          handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector handDiffR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          tempo+=(handDiff.mag()+handDiffR.mag());
          return true;
        }
     }
      
    
    
    
    return false;
  }
  
  
  public boolean deathChecker(){
    if(state==1 || state==2){
      
      if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]&&
      GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL])){
          
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          
          //Average
          tempo+=(diffHand.mag()+diffHandR.mag())/2;
          duration++;
          return true;
        }
        
      }
      
    
    
    
    return false;
    
  }
}

/**
----------------------------------------------------------------------------------------------------------------
*/

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
          
          println("Using left arm");
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
          println("Life of left");
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


/**
----------------------------------------------------------------------------------------------------------------
*/
public class Relief extends Gesture{
  
  
  public Relief(){
    super();
    super.name="Relief";
    prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
  }
  
  
  
  //Checks is birth is happening,
  //In this case hand is below elbow which is below shoulder at first 
  public boolean birthChecker(){
    if(state==-1){
      
      //If leftHand is below leftElbow which is below leftShoulder (or right)
     // println("Ever got hands together?: "+GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]);
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
      if(prevJoints[GestureInfo.LEFT_HAND]==null){
        
      }
      
      if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])){
        //println("Got one of them rising");          
        duration++;
        PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
        tempo+=diffHand.mag();
        PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
       
          //Average of two vectors
        tempo+=(diffHand.mag()+diffHandR.mag())/2;
        
        return true;
      }
    }
    
    return false;
    //return true;
  }
  
  
  public boolean lifeChecker(){
    //println("Life");
    //If hand isn't going up anymore, that's a prerequisite
    if(state==0 || state==1){
        //If is moving back and forth
        if(((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BETWEEN_SHOULDERS] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BETWEEN_SHOULDERS]) 
        && (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BELOW_SHOULDER] 
      && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BELOW_SHOULDER]))
      && (!GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] && !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN])){
          
          duration++;
          PVector handDiff = new PVector();
          
          //Average
          handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector handDiffR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          tempo+=(handDiff.mag()+handDiffR.mag());
          return true;
        }
     }
      
    
    
    
    return false;
  }
  
  
  public boolean deathChecker(){
    //println("Death");
    if(state==1 || state==2){
      
      
      //Either still with both hands out, or each hand moving out
      if( ( (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]&&
      GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]) && (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_LEFT_OF_BODY] 
      && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RIGHT_OF_BODY]) )
    
      
      || (GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT] 
      && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT])){
          
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          
          //Average
          tempo+=(diffHand.mag()+diffHandR.mag())/2;
          duration++;
          return true;
        }
        
      }
      
    
    
    
    return false;
    
  }
}

/**
----------------------------------------------------------------------------------------------------------------
*/
public class Loneliness extends Gesture{
  
  
  public Loneliness(){
    super();
    super.name="Loneliness";
    prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
  }
  
  
  
  //Checks is birth is happening,
  //In this case hand is below elbow which is below shoulder at first 
  public boolean birthChecker(){
    if(state==-1){
      
      //If leftHand is below leftElbow which is below leftShoulder (or right)
     // println("Ever got hands together?: "+GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]);
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
      if(prevJoints[GestureInfo.LEFT_HAND]==null){
        
      }
      
      if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
        !GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD]){
        //println("Got one of them rising");          
        duration++;
        PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
        tempo+=diffHand.mag();
        PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
       
          //Average of two vectors
        tempo+=(diffHand.mag()+diffHandR.mag())/2;
        
        return true;
      }
    }
    
    return false;
    //return true;
  }
  
  
  public boolean lifeChecker(){
    //print("Lonely life");
    //If hand isn't going up anymore, that's a prerequisite
    if(state==0 || state==1){
        //If is moving back and forth
        if( GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD]
        && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]
        && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]){
          
          duration++;
          PVector handDiff = new PVector();
          
          //Average
          handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector handDiffR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          tempo+=(handDiff.mag()+handDiffR.mag());
          return true;
        }
     }
      
    
    
    
    return false;
  }
  
  
  public boolean deathChecker(){
    //println("Lonely death");
    if(state==1 || state==2){
      
      if(((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT])||
      (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT]))
      || 
      
      ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL])&&
      (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]))
      ){
          
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          
          //Average
          tempo+=(diffHand.mag()+diffHandR.mag())/2;
          duration++;
          return true;
        }
        
      }
    
    return false;
    
  }
}

/**
----------------------------------------------------------------------------------------------------------------
*/
public class Excitement extends Gesture{
  
  
  public Excitement(){
    super();
    super.name="Excitement";
    prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
  }
  
  
  
  //Checks is birth is happening,
  //In this case hand is below elbow which is below shoulder at first 
  public boolean birthChecker(){
    if(state==-1){
      
      //If leftHand is below leftElbow which is below leftShoulder (or right)
     // println("Ever got hands together?: "+GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]);
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
      if(prevJoints[GestureInfo.LEFT_HAND]==null){
        
      }
      
      if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
        !GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD] &&
        !GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]){
        //println("Got one of them rising");          
        duration++;
        PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
        tempo+=diffHand.mag();
        PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
       
          //Average of two vectors
        tempo+=(diffHand.mag()+diffHandR.mag())/2;
        
        return true;
      }
    }
    
    return false;
    //return true;
  }
  
  
  public boolean lifeChecker(){
    //If hand isn't going up anymore, that's a prerequisite
    if(state==0 || state==1){
        //If is moving back and forth
        if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) && 
        (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&& 
        GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD]&&
        !GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]){
          
          duration++;
          PVector handDiff = new PVector();
          
          //Average
          handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector handDiffR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          tempo+=(handDiff.mag()+handDiffR.mag());
          return true;
        }
     }
      
    
    
    
    return false;
  }
  
  
  public boolean deathChecker(){
    if(state==1 || state==2){
      
      if((GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD])
      && 
      
      ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL])&&
      (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]) 
      
      && ( ((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_LEFT_OF_BODY])||
      (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RIGHT_OF_BODY])) )
      )){
          
          PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
          PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
          
          //Average
          tempo+=(diffHand.mag()+diffHandR.mag())/2;
          duration++;
          return true;
        }
        
      }
    
    return false;
    
  }
}
public class GestureController{
  ArrayList<Gesture> gestures = new ArrayList<Gesture>();  
  PVector[] prevJoints;
  int[] counters;
  float prevRAngle, prevLAngle, rAngle, lAngle;
  
  public GestureController(){
    
    //NOT CHECK
    Gesture disgust = new Disgust();
    gestures.add(disgust);
    
    //NOT CHECK
    Gesture exhaustion = new Exhaustion();
    gestures.add(exhaustion);
    
    //CHECK
    Gesture pain = new Pain();
    gestures.add(pain);
    
    //Gesture confusion = new Confusion();
    //gestures.add(confusion);
    
    //CHECK, NOT MIX UP
    Gesture terror = new Terror();
    gestures.add(terror);
  
    //CHECK    
    Gesture relief = new Relief();
    gestures.add(relief);
    
    //LONELINESS AND EXHAUSTION CHECK
    Gesture loneliness = new Loneliness();
    gestures.add(loneliness);
    
    //TERROR MIX UP
    Gesture excitement = new Excitement();
    gestures.add(excitement);
    
    //Gesture sadness = new Sadness();
    //gestures.add(sadness);
    
    Weakness weakness = new Weakness();
    gestures.add(weakness);
    
    //print("Gestures: "+gestures.toString());
    counters = new int[GestureInfo.NUMBER_OF_PIECES];
  }
  
  public Gesture updateGestures(PVector[] joints){
    if(prevJoints!=null){
      setGesturePieces(joints);
    }
    
    prevJoints=joints;
    prevRAngle = rAngle;
    prevLAngle= lAngle; 
    return checkGestures(joints);
  }
  
  public void setGesturePieces(PVector[] joints){
    
    //Left hand rising (Might want to change these first four to be based on the normalized vector
    
    if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]){
      GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING] = joints[GestureInfo.LEFT_HAND].y>prevJoints[GestureInfo.LEFT_HAND].y;
      if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]){
        counters[GestureInfo.LEFT_HAND_RISING]=3;
        
      }
      else{
        if(counters[GestureInfo.LEFT_HAND_RISING]==0){
          //Do nothing, it's already false
        }
        else{
          counters[GestureInfo.LEFT_HAND_RISING]-=1;
          GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]=true;
        }
      }
    }
    else{
      GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING] = joints[GestureInfo.LEFT_HAND].y>prevJoints[GestureInfo.LEFT_HAND].y;
      if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]){
        counters[GestureInfo.LEFT_HAND_RISING]=3;
      }
    }
    
    
    
    
    ////////////////////
    //Right hand rising
    if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING]){
      GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING] = joints[GestureInfo.RIGHT_HAND].y>prevJoints[GestureInfo.RIGHT_HAND].y;
      if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING]){
        counters[GestureInfo.RIGHT_HAND_RISING]=3;
        
      }
      else{
        if(counters[GestureInfo.RIGHT_HAND_RISING]==0){
          //Do nothing, it's already false
        }
        else{
          counters[GestureInfo.RIGHT_HAND_RISING]-=1;
          GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING]=true;
        }
      }
    }
    else{
      GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING] = joints[GestureInfo.RIGHT_HAND].y>prevJoints[GestureInfo.RIGHT_HAND].y;
      if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING]){
        counters[GestureInfo.RIGHT_HAND_RISING]=3;
      }
    }
    
    
    
    //Left hand faling
    if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING]){
      GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING] = joints[GestureInfo.LEFT_HAND].y<prevJoints[GestureInfo.LEFT_HAND].y;
      if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING]){
        counters[GestureInfo.LEFT_HAND_FALLING]=3;
        
      }
      else{
        if(counters[GestureInfo.LEFT_HAND_FALLING]==0){
          //Do nothing, it's already false
        }
        else{
          counters[GestureInfo.LEFT_HAND_FALLING]-=1;
          GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING]=true;
        }
      }
    }
    else{
      GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING] = joints[GestureInfo.LEFT_HAND].y<prevJoints[GestureInfo.LEFT_HAND].y;
      if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_FALLING]){
        counters[GestureInfo.LEFT_HAND_FALLING]=3;
      }
    }
    
    //Right hand falling
    if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING]){
      GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING] = joints[GestureInfo.RIGHT_HAND].y<prevJoints[GestureInfo.RIGHT_HAND].y;
      if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING]){
        counters[GestureInfo.RIGHT_HAND_FALLING]=3;
        
      }
      else{
        if(counters[GestureInfo.RIGHT_HAND_FALLING]==0){
          //Do nothing, it's already false
        }
        else{
          counters[GestureInfo.RIGHT_HAND_FALLING]-=1;
          GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING]=true;
        }
      }
    }
    else{
      GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING] = joints[GestureInfo.RIGHT_HAND].y<prevJoints[GestureInfo.RIGHT_HAND].y;
      if(GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_FALLING]){
        counters[GestureInfo.RIGHT_HAND_FALLING]=3;
      }
    }
    
    //Left hand down
    
    //println("Hand y: "+joints[GestureInfo.LEFT_HAND].y);
    //println("Elbow y: "+ joints[GestureInfo.LEFT_ELBOW].y);
    //println("Shoulder y: "+joints[GestureInfo.LEFT_SHOULDER].y);
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] = (joints[GestureInfo.LEFT_HAND].y<joints[GestureInfo.LEFT_ELBOW].y)&& joints[GestureInfo.LEFT_ELBOW].y<joints[GestureInfo.LEFT_SHOULDER].y;
    
    //Right hand down
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN] = joints[GestureInfo.RIGHT_HAND].y<joints[GestureInfo.RIGHT_ELBOW].y && joints[GestureInfo.RIGHT_ELBOW].y<joints[GestureInfo.RIGHT_SHOULDER].y;
    
    //PROBABLY GOING TO NEED A CHECKER SIMILAR TO THE ONES ABOVE, USING COUNTER FOR THIS
    
    //Left hand moving left
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_LEFT] = joints[GestureInfo.LEFT_HAND].x<prevJoints[GestureInfo.LEFT_HAND].x;
    
    //Right hand moving left
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_LEFT] = joints[GestureInfo.RIGHT_HAND].x<prevJoints[GestureInfo.RIGHT_HAND].x;
    
    //Left hand moving right
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_MOVING_RIGHT] = joints[GestureInfo.LEFT_HAND].x>prevJoints[GestureInfo.LEFT_HAND].x;
    
    //Right hand moving right
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_MOVING_RIGHT] = joints[GestureInfo.RIGHT_HAND].x>prevJoints[GestureInfo.RIGHT_HAND].x;
    
    float distanceMeter = PVector.sub(joints[GestureInfo.HEAD],joints[GestureInfo.NECK]).mag()/2;
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL] = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]).mag()<distanceMeter;
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL] = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]).mag()<distanceMeter;
    
    
    
  
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_NEAR_HEAD]=   PVector.sub(joints[GestureInfo.LEFT_HAND],joints[GestureInfo.HEAD]).mag()<distanceMeter*2;
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_NEAR_HEAD]=   PVector.sub(joints[GestureInfo.RIGHT_HAND],joints[GestureInfo.HEAD]).mag()<distanceMeter*2;
    
    //Left knee going back
    if(GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK]){
      GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK] = joints[GestureInfo.LEFT_KNEE].z>prevJoints[GestureInfo.LEFT_KNEE].z;
      if(GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK]){
        counters[GestureInfo.LEFT_KNEE_GOING_BACK]=3;
        
      }
      else{
        if(counters[GestureInfo.LEFT_KNEE_GOING_BACK]==0){
          //Do nothing, it's already false
        }
        else{
          counters[GestureInfo.LEFT_KNEE_GOING_BACK]-=1;
          GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK]=true;
        }
      }
    }
    else{
      GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK] = joints[GestureInfo.LEFT_KNEE].z>prevJoints[GestureInfo.LEFT_KNEE].z;
      if(GestureInfo.gesturePieces[GestureInfo.LEFT_KNEE_GOING_BACK]){
        counters[GestureInfo.LEFT_KNEE_GOING_BACK]=3;
      }
    }
    
    //RIGHT knee going back
    if(GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]){
      GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK] = joints[GestureInfo.RIGHT_KNEE].z>prevJoints[GestureInfo.RIGHT_KNEE].z;
      if(GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]){
        counters[GestureInfo.RIGHT_KNEE_GOING_BACK]=3;
        
      }
      else{
        if(counters[GestureInfo.RIGHT_KNEE_GOING_BACK]==0){
          //Do nothing, it's already false
        }
        else{
          counters[GestureInfo.RIGHT_KNEE_GOING_BACK]-=1;
          GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]=true;
        }
      }
    }
    else{
      GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK] = joints[GestureInfo.RIGHT_KNEE].z>prevJoints[GestureInfo.RIGHT_KNEE].z;
      if(GestureInfo.gesturePieces[GestureInfo.RIGHT_KNEE_GOING_BACK]){
        counters[GestureInfo.RIGHT_KNEE_GOING_BACK]=3;
      }
    }
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BELOW_SHOULDER] = joints[GestureInfo.LEFT_HAND].y<joints[GestureInfo.LEFT_SHOULDER].y && 
    joints[GestureInfo.LEFT_ELBOW].y<joints[GestureInfo.LEFT_HAND].y;
    
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BELOW_SHOULDER] = joints[GestureInfo.RIGHT_HAND].y<joints[GestureInfo.RIGHT_SHOULDER].y && 
    joints[GestureInfo.RIGHT_ELBOW].y<joints[GestureInfo.RIGHT_HAND].y;
  
    
    PVector rUpperArm=PVector.sub(joints[GestureInfo.RIGHT_SHOULDER], joints[GestureInfo.RIGHT_ELBOW]);
    PVector rLowerArm = PVector.sub(joints[GestureInfo.RIGHT_HAND], joints[GestureInfo.RIGHT_ELBOW]);
    
    PVector lUpperArm=PVector.sub(joints[GestureInfo.LEFT_SHOULDER], joints[GestureInfo.LEFT_ELBOW]);
    PVector lLowerArm = PVector.sub(joints[GestureInfo.LEFT_HAND], joints[GestureInfo.LEFT_ELBOW]);
    
    rAngle = PVector.angleBetween(rUpperArm, rLowerArm);
    lAngle = PVector.angleBetween(lUpperArm, lLowerArm);
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_DECREASING]= lAngle<prevLAngle;
    GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_DECREASING]= rAngle<prevRAngle;
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ANGLE_MED]= lAngle<100;
    GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ANGLE_MED]= rAngle<100;
    
    //Left hand down
    GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_UP] = (joints[GestureInfo.LEFT_HAND].y>joints[GestureInfo.LEFT_ELBOW].y)&& joints[GestureInfo.LEFT_ELBOW].y>joints[GestureInfo.LEFT_SHOULDER].y;
    
    //Right hand down
    GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_UP] = joints[GestureInfo.RIGHT_HAND].y>joints[GestureInfo.RIGHT_ELBOW].y && joints[GestureInfo.RIGHT_ELBOW].y>joints[GestureInfo.RIGHT_SHOULDER].y;
    
    GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]=   PVector.sub(joints[GestureInfo.LEFT_HAND],joints[GestureInfo.RIGHT_HAND]).mag()<distanceMeter*3;
    
    //println("Hands were together. "+GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]);
    
    //Hands above head
    GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_UP] = (joints[GestureInfo.RIGHT_HAND].y>joints[GestureInfo.RIGHT_ELBOW].y)&& joints[GestureInfo.RIGHT_ELBOW].y>joints[GestureInfo.RIGHT_SHOULDER].y;   

    GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD]=joints[GestureInfo.RIGHT_HAND].y>joints[GestureInfo.HEAD].y && joints[GestureInfo.LEFT_HAND].y>joints[GestureInfo.HEAD].y;
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_ABOVE_HEAD]=joints[GestureInfo.LEFT_HAND].y>joints[GestureInfo.HEAD].y;

    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_ABOVE_HEAD]=joints[GestureInfo.RIGHT_HAND].y>joints[GestureInfo.HEAD].y;
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_LEFT_OF_BODY] = joints[GestureInfo.LEFT_HAND].x<joints[GestureInfo.LEFT_SHOULDER].x;
    
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_LEFT_OF_BODY] = joints[GestureInfo.RIGHT_HAND].x<joints[GestureInfo.LEFT_SHOULDER].x;
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RIGHT_OF_BODY] = joints[GestureInfo.LEFT_HAND].x>joints[GestureInfo.RIGHT_SHOULDER].x;
    
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RIGHT_OF_BODY] = joints[GestureInfo.RIGHT_HAND].x>joints[GestureInfo.RIGHT_SHOULDER].x;
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_BETWEEN_SHOULDERS] = !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_LEFT_OF_BODY] &&
                                                                          !GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RIGHT_OF_BODY];
                                                                         
    GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_BETWEEN_SHOULDERS] = !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_LEFT_OF_BODY] &&
                                                                          !GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RIGHT_OF_BODY];                                                         
    
    
    float leftLowerArmDiff = joints[GestureInfo.LEFT_HAND].x-joints[GestureInfo.LEFT_ELBOW].x;
    float leftUpperArmDiff = joints[GestureInfo.LEFT_SHOULDER].x-joints[GestureInfo.LEFT_ELBOW].x;
    
    leftLowerArmDiff = abs(leftLowerArmDiff);
    leftUpperArmDiff = abs(leftUpperArmDiff);
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_VERTICAL] = leftLowerArmDiff<distanceMeter && leftUpperArmDiff<distanceMeter;
    
    float rightLowerArmDiff = joints[GestureInfo.RIGHT_HAND].x-joints[GestureInfo.RIGHT_ELBOW].x;
    float rightUpperArmDiff = joints[GestureInfo.RIGHT_SHOULDER].x-joints[GestureInfo.RIGHT_ELBOW].x;
    
    rightLowerArmDiff = abs(rightLowerArmDiff);
    rightUpperArmDiff = abs(rightUpperArmDiff);
    
    GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_VERTICAL] = rightLowerArmDiff<distanceMeter && rightUpperArmDiff<distanceMeter;
    
    float leftLowerArmDiffY = joints[GestureInfo.LEFT_HAND].y-joints[GestureInfo.LEFT_ELBOW].y;
    float leftUpperArmDiffY = joints[GestureInfo.LEFT_SHOULDER].y-joints[GestureInfo.LEFT_ELBOW].y;
    
    leftLowerArmDiffY = abs(leftLowerArmDiffY);
    leftUpperArmDiffY = abs(leftUpperArmDiffY);
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_HORIZONTAL] = leftLowerArmDiffY<distanceMeter && leftUpperArmDiffY<distanceMeter;
    
    //println("Left arm horizontal: "+GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_HORIZONTAL]);
    
    float rightLowerArmDiffY = joints[GestureInfo.RIGHT_HAND].y-joints[GestureInfo.RIGHT_ELBOW].y;
    float rightUpperArmDiffY = joints[GestureInfo.RIGHT_SHOULDER].y-joints[GestureInfo.RIGHT_ELBOW].y;
    
    rightLowerArmDiffY = abs(rightLowerArmDiffY);
    rightUpperArmDiffY = abs(rightUpperArmDiffY);
    
    GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_HORIZONTAL] = rightLowerArmDiffY<distanceMeter && rightUpperArmDiffY<distanceMeter;

    GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_EXTENDED] = joints[GestureInfo.RIGHT_HAND].x>joints[GestureInfo.RIGHT_ELBOW].x && 
                                                                joints[GestureInfo.RIGHT_ELBOW].x>joints[GestureInfo.RIGHT_SHOULDER].x;
    
    //println("Right arm extended: "+GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_EXTENDED]);
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_EXTENDED]= joints[GestureInfo.LEFT_HAND].x<joints[GestureInfo.LEFT_ELBOW].x && 
                                                                joints[GestureInfo.LEFT_ELBOW].x<joints[GestureInfo.LEFT_SHOULDER].x;
    //println("Left arm extended: "+ GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_EXTENDED]);
    
    GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_ABOVE_TORSO] = joints[GestureInfo.LEFT_HAND].y> joints[GestureInfo.TORSO].y;
    GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_ABOVE_TORSO] =joints[GestureInfo.RIGHT_HAND].y> joints[GestureInfo.TORSO].y;
}
  
  public Gesture checkGestures(PVector[] joints){
    
    float maxValue=-1.0f;
    Gesture maxGesture = null;
    
    //Go through and call each Gesture's update, see what it returns and return the max.
    for (int i = 0; i<gestures.size(); i++){
      
      float confidenceValue = gestures.get(i).update(joints);
      if(confidenceValue>maxValue){
        maxValue = confidenceValue;
        maxGesture = gestures.get(i);
        
      }
      
    }
    
    return maxGesture;
  }
  
  public void setAllElseToZero(Gesture winner){
    for(int i= 0; i<gestures.size(); i++){
      if(gestures.get(i)!=winner){
        gestures.get(i).confidence=0;
      }
    }
  }
  
  
  
}
//PURELY AN INFO HOLDER
static class GestureInfo{
 
  //JOINT ARRAY
  public static final int LEFT_HAND = 0;
  public static final int LEFT_ELBOW = 1;
  public static final int LEFT_SHOULDER = 2;
  public static final int RIGHT_HAND = 3;
  public static final int RIGHT_ELBOW = 4;
  public static final int RIGHT_SHOULDER = 5;
  public static final int TORSO = 6;
  public static final int HEAD =7;
  public static final int NECK = 8;
  public static final int LEFT_KNEE =9;
  public static final int RIGHT_KNEE =10;
  public static final int JOINTS_LENGTH =11;
  
  //Boolean Pieces (These relate to poisitions in values of a boolean array) 
  //[Have it be held here, as that way the value won't change unless it specifically
  //Should or shot not be on  
  
  public static final int LEFT_HAND_RISING=0;
  public static final int RIGHT_HAND_RISING=1;
  public static final int LEFT_HAND_FALLING=2;
  public static final int RIGHT_HAND_FALLING=3;
  //Left hand is below shoulder and elbow
  public static final int LEFT_HAND_DOWN=4;
  //Right hand is below shouler and elbow
  public static final int RIGHT_HAND_DOWN=5;
  public static final int LEFT_HAND_MOVING_LEFT=6;
  public static final int RIGHT_HAND_MOVING_LEFT=7;
  public static final int LEFT_HAND_MOVING_RIGHT=8;
  public static final int RIGHT_HAND_MOVING_RIGHT=9;
  
  //Left hand has not moved further than half neck length
  public static final int LEFT_HAND_STILL=10;
  public static final int RIGHT_HAND_STILL=11;
  
  //Less than half a neck length away from head
  public static final int LEFT_HAND_NEAR_HEAD =12;
  public static final int RIGHT_HAND_NEAR_HEAD =13;
  public static final int RIGHT_KNEE_GOING_BACK=14;
  public static final int LEFT_KNEE_GOING_BACK=15;
 
  //Below shoulder, but above elbow
  public static final int LEFT_HAND_BELOW_SHOULDER=16;
  public static final int RIGHT_HAND_BELOW_SHOULDER=17;
  
  public static final int LEFT_ARM_ANGLE_DECREASING=18;
  public static final int RIGHT_ARM_ANGLE_DECREASING =19;
  
  public static final int LEFT_ARM_ANGLE_MED=20;
  public static final int RIGHT_ARM_ANGLE_MED=21;
  
  //Left hand is above shoulder and elbow
  public static final int LEFT_ARM_UP=22;
  //Right hand is below shouler and elbow
  public static final int RIGHT_ARM_UP=23;
  
  public static final int HANDS_TOGETHER=24;
  public static final int HANDS_ABOVE_HEAD=25;
  
  //Arms on sides of body
  public static final int LEFT_HAND_LEFT_OF_BODY = 26;
  public static final int RIGHT_HAND_LEFT_OF_BODY = 27;
  public static final int LEFT_HAND_RIGHT_OF_BODY = 28;
  public static final int RIGHT_HAND_RIGHT_OF_BODY = 29;
  
  //Hands in between shoulders
  public static final int LEFT_HAND_BETWEEN_SHOULDERS=30;
  public static final int RIGHT_HAND_BETWEEN_SHOULDERS=31;
  
  public static final int LEFT_HAND_ABOVE_HEAD=32;
  public static final int RIGHT_HAND_ABOVE_HEAD=33;
  
  public static final int LEFT_ARM_VERTICAL = 34;
  public static final int RIGHT_ARM_VERTICAL = 35;
  
  public static final int LEFT_ARM_HORIZONTAL = 36;
  public static final int RIGHT_ARM_HORIZONTAL = 37;
  
  public static final int LEFT_ARM_EXTENDED = 38;
  public static final int RIGHT_ARM_EXTENDED =39;
  
  public static final int LEFT_ARM_ABOVE_TORSO = 40;
  public static final int RIGHT_ARM_ABOVE_TORSO =41;
  
  //Length of array, should always be one more than previous entry
  public static final int NUMBER_OF_PIECES =42;

  
  public static boolean[] gesturePieces;
  
  public static void init(){
    gesturePieces = new boolean[NUMBER_OF_PIECES];
  }
  
  
}
public class PFrame extends Frame {
    public SecondaryApplet s;  
    public int w,h;
  
    public PFrame() {
        setBounds(0,0,800,200);
        w=800;
        h=200;
        
        s = new SecondaryApplet(800,200);
        add(s);
        s.init();
        
        show();
    }
    
    public PFrame(String name) {
        this();
        this.setTitle(name);
    }
   
}


public class SecondaryApplet extends PApplet {
    int w, h;
    
    public SecondaryApplet(){
      super();
    }
    
    public SecondaryApplet(int w, int h){
      super();
      this.w=w;
      this.h=h;
    }
    
    
    public void setup() {
        size(w,h);
        smooth();
        //noLoop();
    }

    public void draw() {
      
    }
} 

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "GestureTest" });
  }
}
