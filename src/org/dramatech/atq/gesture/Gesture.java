package org.dramatech.atq.gesture;

import processing.core.PVector;

public class Gesture {
  public float tempo, duration, confidence;
  public String name;

  // ACTUAL TYPE OF THIS GESTURE, NOT CURRENTLY SET, AS THIS IS JUST THE BASE CLASS
  // Also allows us to switch which joints we're grabbing based on what part of the gesture we're in
  public GestureType type;

  // 0 is birth, 1 is life, 2 is death
  public int state;
  public PVector[] joints, prevJoints;

  public Gesture(){
    this.type = GestureType.FULLBODY;
  }

  public Gesture(GestureType type){
    this.type = type;

    state = -1;
  }

  //Returns confidence at this moment
  public float update(PVector[] joints){
    this.joints = joints;

    if(prevJoints[GestureInfo.LEFT_HAND] != null){
      if(state == -1){
          confidence = 0;
          if(birthChecker()){
            state = 0;
          }
      }
      else {
          switch (state) {
              case 0:
                  boolean birthing = birthChecker();

                  if(!birthing) {
                      if(lifeChecker()) {
                          state = 1;
                          confidence = 0.5f;
                      }
                      else {
                          state = -1;
                          duration = 0;
                          tempo = 0;
                          confidence = 0;
                      }
                  }
                  else{
                      if(confidence < 0.3f) {
                          confidence += 0.001f;
                      }
                      else {
                          confidence -= 0.02f;
                      }
                  }
                  break;
              case 1:
                  boolean living = lifeChecker();

                  if(!living) {
                      if(deathChecker()) {
                          state = 2;
                          confidence = 1.0f;
                      }
                      else {
                          state = -1;
                          duration = 0;
                          tempo = 0;
                          confidence = 0;
                      }
                  }
                  else {
                      if(confidence < 0.6f) {
                          confidence += 0.001f;
                      }
                      else {
                          confidence -= 0.02f;
                      }
                  }
                  break;
              case 2:
                  boolean dying = deathChecker();

                  // If no longer dying, reset
                  if(!dying){
                      state = -1;
                      duration = 0;
                      tempo = 0;
                  }
                  else {
                      if(confidence < 1) {
                          confidence += 0.001f;
                      }
                      else {
                          confidence -= 0.02f;
                      }
                  }
                  break;
          }
      }
    }

    prevJoints = joints;
    return confidence;
  }

  public boolean birthChecker() {
      switch (state) {
          case -1:
              return false;
          case 0:
              return true;
          default:
              return false;
      }
  }

  public boolean lifeChecker() {
      switch (state) {
          case 0:
              return false;
          case 1:
              return true;
          default:
              return false;
      }
  }

  public boolean deathChecker() {
    return false;
  }
}
