package org.dramatech.atq.gesture;

import processing.core.PVector;

public abstract class Gesture {
    public float tempo, duration, confidence;
    public String name;

    // Also allows us to switch which joints we're grabbing based on what part of the gesture we're in
    public GestureType type;

    public GestureState state;
    public PVector[] joints, prevJoints;

    public Gesture() {
        this(GestureType.FULLBODY);
    }

    public Gesture(final GestureType type) {
        this.type = type;

        state = GestureState.NONE;
    }

    // Returns confidence at this moment
    public float update(final PVector[] joints) {
        this.joints = joints;

        if (prevJoints[GestureInfo.LEFT_HAND] != null) {
            if (state == GestureState.NONE) {
                confidence = 0;
                if (birthChecker()) {
                    state = GestureState.BIRTH;
                }
            } else {
                switch (state) {
                    case BIRTH:
                        final boolean birthing = birthChecker();

                        if (!birthing) {
                            if (lifeChecker()) {
                                state = GestureState.LIFE;
                                confidence = 0.5f;
                            } else {
                                state = GestureState.NONE;
                                duration = 0;
                                tempo = 0;
                                confidence = 0;
                            }
                        } else {
                            if (confidence < 0.3f) {
                                confidence += 0.001f;
                            } else {
                                confidence -= 0.02f;
                            }
                        }
                        break;
                    case LIFE:
                        final boolean living = lifeChecker();

                        if (!living) {
                            if (deathChecker()) {
                                state = GestureState.DEATH;
                                confidence = 1.0f;
                            } else {
                                state = GestureState.NONE;
                                duration = 0;
                                tempo = 0;
                                confidence = 0;
                            }
                        } else {
                            if (confidence < 0.6f) {
                                confidence += 0.001f;
                            } else {
                                confidence -= 0.02f;
                            }
                        }
                        break;
                    case DEATH:
                        final boolean dying = deathChecker();

                        // If no longer dying, reset
                        if (!dying) {
                            state = GestureState.NONE;
                            duration = 0;
                            tempo = 0;
                        } else {
                            if (confidence < 1) {
                                confidence += 0.001f;
                            } else {
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
        return state == GestureState.BIRTH;
    }

    public boolean lifeChecker() {
        return state == GestureState.LIFE;
    }

    public boolean deathChecker() {
        return state == GestureState.DEATH;
    }
}
