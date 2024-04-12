package com.github.curriculeon.greeps;

import com.github.git_leon.RandomUtils;
import greenfoot.Actor;
import greenfoot.Greenfoot;

import java.util.List;

/**
 * A Creature is the base class for all alien beings in this scenario. It
 * provides the basic abilities of creatures in this world.
 *
 * @author Michael Kölling
 * @version 2.0
 */
public abstract class Creature extends Actor {
    private static final double WALKING_SPEED = 5.0;
    private static final int TIME_TO_SPIT = 10;
    private boolean carryingTomato = false; // Indicate whether we have a tomato with us
    private Spaceship ship; // The creature's home ship
    private boolean moved = false;
    private int timeToSpit = 0;
    private int memory; // General purpose memory
    private boolean[] states; //General purpose memory

    /**
     * Create a creature at its ship.
     */
    public Creature(Spaceship ship) {
        this.ship = ship;
        this.states = new boolean[2];
        setRotation(RandomUtils.createInteger(0, 360));
    }

    /**
     * @return string-representation of the current image of this `Creature` instance
     */
    abstract public String getCurrentImage();

    /**
     * Defines what a `Creature` will do
     */
    abstract protected void behave();

    /**
     * @param imageName path of image to set this `Creature` instance to
     */
    @Override
    public final void setImage(String imageName) {
        super.setImage(new StringBuilder()
                .append(System.getProperty("user.dir"))
                .append("/src/main/resources/images/")
                .append(imageName)
                .toString());
    }

    /**
     * Act - must be called as part of subclass act. This ensures single
     * movement in each act round.
     */
    @Override
    public final void act() {
        moved = false;
        behave();
    }


    /**
     * To avoid confusion/cheating, calling the built-in Greenfoot
     * move(int amount) does the same as the Creature move() function.
     */
    @Deprecated
    @Override
    public final void move(int amount) {
        move();
    }


    /**
     * Turn in the direction of the specified `actor`
     *
     * @param actor - Actor to turn towards
     */
    public void turnTowards(Actor actor) {
        if (actor != null) {
            super.turnTowards(actor.getX(), actor.getY());
        }
    }


    /**
     * @param actor            the actor to turn towards
     * @param likelihoodOfTurn the likelihood of successfully turning toward the specified actor
     */
    public void turnTowards(Actor actor, float likelihoodOfTurn) {
        if (RandomUtils.createBoolean(likelihoodOfTurn)) {
            turnTowards(actor);
        }
    }

    /**
     * turn towards the ship
     */
    public void turnTowardsHome() {
        turnTowardsHome(100);
    }

    /**
     * turn towards the ship with some likelihood
     *
     * @param likelihoodOfTurn the likelihood of successfully turning toward the ship
     */
    public void turnTowardsHome(float likelihoodOfTurn) {
        turnTowards(ship, likelihoodOfTurn);
    }

    /**
     * @param angle the angle to turn away from
     */
    public void turnOppositeDirection(int angle) {
        setRotation(angle);
        turn(180);
    }

    /**
     * @param actor the actor to turn away from
     */
    public void turnAwayFrom(Actor actor) {
        turnAwayFrom(actor, 100);
    }

    /**
     * @param actor                     the actor to turn away from
     * @param likelihoodOfExecutingTurn the likelihood of executing the turn
     */
    public void turnAwayFrom(Actor actor, int likelihoodOfExecutingTurn) {
        if (RandomUtils.createBoolean(likelihoodOfExecutingTurn)) {
            turnTowards(actor);
            turnOppositeDirection(getRotation());
        }
    }

    /**
     * @return true if we are at our space ship.
     */
    public final boolean isAtShip() {
        return getOneIntersectingObject(Spaceship.class) != null;
    }

    /**
     * @return true if we have found food at our current location.
     */
    public final boolean isAtTomatoes() {
        return getSurroundingTomatoPile() != null;
    }


    /**
     * @return the instance of the tomato pile that this `Creature` instance is currently intersecting; return null if no tomato pile can be found
     */
    public TomatoPile getSurroundingTomatoPile() {
        return (TomatoPile) getOneIntersectingObject(TomatoPile.class);
    }


    /**
     * @return true if we have just seen water in front of us.
     */
    public final boolean isAtWater() {
        return ((Earth) getWorld()).isWater(getNextXCoordinate(), getNextYCoordinate());
    }

    /**
     * @return true if we are within 3 cells of the world's edge
     */
    public boolean isAtWorldEdge() {
        if (getX() < 3 || getX() > getWorld().getWidth() - 3)
            return true;
        if (getY() < 3 || getY() > getWorld().getHeight() - 3)
            return true;
        else
            return false;
    }

    /**
     * Check whether we can see paint of a given color where we are sitting.
     */
    public boolean isSeeingPaint(String color) {
        List paintDrops = getIntersectingObjects(GreepSpit.class);
        for (Object obj : paintDrops) {
            if (((GreepSpit) obj).getColor().equals(color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve the value of a flag. 'flagNo' can be 1 or 2.
     */
    public boolean isFlaggedForState(int state) {
        if (state < 1 || state > 2)
            throw new IllegalArgumentException("flag number must be either 1 or 2");
        else
            return states[state - 1];
    }


    /**
     * Check whether we are carrying a tomato.
     */
    public final boolean isCarryingTomato() {
        return carryingTomato;
    }

    /**
     * Remove the tomato currently carried (and return true). Return
     * false if we were not carrying one.
     */
    public final boolean removeTomato() {
        if (carryingTomato) {
            carryingTomato = false;
            return true;
        } else
            return false;
    }


    /**
     * Receive a tomato and carry it.
     */
    private void carryTomato() {
        carryingTomato = true;
        setImage(getCurrentImage());
    }


    /**
     * Drop the tomato we are carrying. If we are at the ship, it is counted.
     * If not, it's just gone...
     */
    protected final void dropTomato() {
        if (!carryingTomato)
            return;

        if (isAtShip()) {
            ship.storeTomato(this);
        }
        carryingTomato = false;
        setImage(getCurrentImage());
    }


    /**
     * Load a tomato onto *another* creature. This works only if there is another creature
     * and a tomato pile present, otherwise this method does nothing.
     */
    public final void loadTomato() {
        // check whether there's a tomato pile here
        TomatoPile tomatoes = (TomatoPile) getOneIntersectingObject(TomatoPile.class);
        // check whether there's another creature here
        Creature greep = (Creature) getOneIntersectingObject(Creature.class);

        if (greep != null && tomatoes != null) {
            if (!greep.isCarryingTomato()) {
                tomatoes.takeOne();
                greep.carryTomato();
            }
        }
    }


    /**
     * Spit a drop of paint onto the ground. We can spit in three colors: "red", "orange",
     * and "purple". (All other strings will be mapped to one of these.)
     */
    public void spit(String color) {
        if (timeToSpit == 0) {
            GreepSpit paint = new GreepSpit(color);
            getWorld().addObject(paint, getX(), getY());
            timeToSpit = TIME_TO_SPIT + Greenfoot.getRandomNumber(10);
        }
    }


    /**
     * Store a user defined value. Attention: even though the parameter type is int,
     * only byte size values (0 <= val <= 255) are accepted.
     */
    public void setMemory(int val) {
        if (val < 0 || val > 255)
            throw new IllegalArgumentException("memory value must be in range [0..255]");
        else
            memory = val;
    }


    /**
     * Retrieve a previously stored value.
     */
    public int getMemory() {
        return memory;
    }


    /**
     * Store a user defined boolean value (a "flag"). Two flags are available,
     * i.e. 'flagNo' may be 1 or 2.
     */
    public void setFlag(int flagNo, boolean val) {
        if (flagNo < 1 || flagNo > 2)
            throw new IllegalArgumentException("flag number must be either 1 or 2");
        else
            states[flagNo - 1] = val;
    }

    /**
     * @return the Spaceship to return tomato piles to
     */
    public Spaceship getShip() {
        return ship;
    }

    /**
     * Move forward roughly in the current direction. Sometimes we get a
     * little off course.
     */
    public void move() {
        if (moved)   // can move only once per 'act' round
            return;

        // there's a 3% chance that we randomly turn a little off course
        if (RandomUtils.createBoolean(3)) {
            turn((Greenfoot.getRandomNumber(3) - 1) * 10);
        }

        if (!isAtWater()) {
            setLocation(getNextXCoordinate(), getNextYCoordinate());
        }

        if (timeToSpit > 0)
            timeToSpit--;

        moved = true;
    }

    /**
     * @return true if the next coordinate is not equal to the current coordinate
     */
    public Boolean canMove() {
        boolean xCoordinateWillBeTheSame = getNextXCoordinate() == getX();
        boolean yCoordinateWillBeTheSame = getNextYCoordinate() == getY();
        boolean positionWillBeTheSame = xCoordinateWillBeTheSame && yCoordinateWillBeTheSame;
        return !positionWillBeTheSame;
    }

    /**
     * @return return the `Y` coordinates of the   location to move into
     */
    public int getNextXCoordinate() {
        double angle = Math.toRadians(getRotation());
        int literalX = (int) Math.round(getX() + Math.cos(angle) * WALKING_SPEED);
        int projectedX = (int) Math.round(getX() + Math.cos(angle) * WALKING_SPEED + 2);

        if (projectedX >= getWorld().getWidth()) {
            literalX = getWorld().getWidth() - 1;
        }
        if (projectedX < 0) {
            literalX = 0;
        }
        return literalX;
    }

    /**
     * @return return the `X` coordinates of the next location to move into
     */
    public int getNextYCoordinate() {
        double angle = Math.toRadians(getRotation());
        int literalY = (int) Math.round(getY() + Math.sin(angle) * WALKING_SPEED);
        int projectedY = (int) Math.round(getY() + Math.sin(angle) * WALKING_SPEED + 2);

        if (projectedY >= getWorld().getHeight()) {
            literalY = getWorld().getHeight() - 1;
        }
        if (projectedY < 0) {
            literalY = 0;
        }
        return literalY;
    }
}
