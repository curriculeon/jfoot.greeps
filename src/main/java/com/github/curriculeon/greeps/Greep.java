package com.github.curriculeon.greeps;

import com.github.git_leon.RandomUtils;
import com.github.git_leon.jfoot.sprite.SpriteSensorDecorator;
import greenfoot.Actor;

/**
 * A Greep is an alien creature that likes to collect tomatoes.
 *
 * @author (your name here)
 * @version 0.1
 */
public class Greep extends Creature {
    /**
     * Create a creature at its ship.
     *
     * @param ship
     */
    public Greep(Spaceship ship) {
        super(ship);
        setImage(getCurrentImage());
    }

    @Override
    protected void behave() {
        if (isCarryingTomato()) {
            if (isAtShip()) {
                dropTomato();
            } else {
               //
                //turnRandomDegrees(1,1);
                turnTowardsHome();
            }
        }
        else if(!isCarryingTomato() && !isAtTomatoes()){
           //
            if(isAtWorldEdge())
                //this with 70, 180 below is best combo so far at 74
                //turnRandomDegrees(1,30);
                //this with below 70, 180 also did 74
                //turnRandomDegrees(5,30);
                //this with -218, -360 below gave 75
                //turnRandomDegrees(5,15);
                //this with -210, -360 below gave 78
                turnRandomDegrees(5,10);
                //turnRandomDegrees(1,5);
            seekTomatoPile();
        }
        else if(isWaitingForAssistance()) {
            waitForTomatoLoadingAssistance();
        }
        else if(isAtTomatoes() && isWaitingForAssistance()) {
            checkFood();
        }
        if(isAtWater()) {
            //turnRandomDegrees();

            //the best I've done on all three rounds, score 72
            //turnRandomDegrees(70, 180);
            //this with 5,30 above did 73
            //turnRandomDegrees(-220, -360);
            //this with 5, 15 above gave 77
            //turnRandomDegrees(-210, -360);
            //turnRandomDegrees(-210, -307);
            turnRandomDegrees(-210, -360);

            //negative turning works best for third pass
            //turnRandomDegrees(-70, -180);


        }

        move();
    }

    private Boolean isToLeft(Actor actor) {
        int currentRotation = getRotation();
        turnTowards(actor);
        int relativeAngle = getRotation();
        if (relativeAngle > 180)
            return true;
        setRotation(currentRotation);
        return false;
    }

    private GreepSpit getIntersectingSpit() {
        return (GreepSpit) getOneIntersectingObject(GreepSpit.class);
    }

    private boolean isAtSpit(String colorName) {
        GreepSpit potentialSpit = (GreepSpit) getOneIntersectingObject(GreepSpit.class);
        if (potentialSpit != null) {
            return potentialSpit.getColor().equalsIgnoreCase(colorName);
        }
        return false;
    }

    public Boolean isWaitingForAssistance() {
        return isAtTomatoes() && !isCarryingTomato();
    }


    public Boolean isWaitingToAssist() {
        if (isAtTomatoes()) {
            for (Greep greep : getSurroundingTomatoPile().getIntersectingObjects(Greep.class)) {
                if (!greep.isCarryingTomato()) {
                    return true;
                }
            }
        }
        return false;
    }


    public void waitForTomatoLoadingAssistance() {
        turnTowards(getSurroundingTomatoPile());
        move();
        loadTomato();
    }


    public Boolean isReturningToShip() {
        return isCarryingTomato();
    }


    public void returnToShip() {
        turnTowardsHome(3);
        move();
    }


    public Boolean shouldSeekTomatoPile() {
        return !isCarryingTomato();
    }


    public void seekTomatoPile() {
        move();
    }


    public void turnRandomDegrees() {
        turnRandomDegrees(15, 90);
    }


    public void turnRandomDegrees(int minimumTurn, int maximumTurn) {
        turn(RandomUtils.createInteger(minimumTurn, maximumTurn));
    }


    public void turnRandomly(int minimumTurn, int maximumTurn, float likelihoodOfTurn) {
        if (minimumTurn > maximumTurn) {
            Integer temp;
            temp = maximumTurn;
            maximumTurn = minimumTurn;
            minimumTurn = temp;
        }
        if (RandomUtils.createBoolean(likelihoodOfTurn)) {
            turnRandomDegrees(minimumTurn, maximumTurn);
        }
    }

    /**
     * Is there any food here where we are? If so, try to load some!
     */

    public void checkFood() {
        // check whether there's a tomato pile here
        if (isAtTomatoes()) {
            loadTomato();
            // Note: this attempts to load a tomato onto *another* Greep. It won't
            // do anything if we are alone here.
        }
    }


    /**
     * This method specifies the image we want displayed at any time. (No need
     * to change this for the competition.)
     */

    public String getCurrentImage() {
        if (isCarryingTomato())
            return "greep-with-food.png";
        else
            return "greep.png";
    }

    /**
     * Create a Greep with its home space ship.
     */

    public static String getAuthorName() {
        return "Anonymous";
    }
}