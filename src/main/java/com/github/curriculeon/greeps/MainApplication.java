package com.github.curriculeon.greeps;


import com.github.git_leon.jfoot.system.JFootApplication;
import greenfoot.Greenfoot;
import greenfoot.core.WorldHandler;


/**
 * @author leonhunter
 * @created 04/03/2020 - 3:59 PM
 */
public class MainApplication {
    public static void main(String[] args) {
        new JFootApplication().run();
        Greenfoot.setWorld(new Earth());
        Greenfoot.setSpeed(50);
        WorldHandler.getInstance().getWorld();
    }
}
