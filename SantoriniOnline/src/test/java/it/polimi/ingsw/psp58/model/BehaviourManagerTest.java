package it.polimi.ingsw.psp58.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class BehaviourManagerTest {
    BehaviourManager behaviour = null;

    @org.junit.Before
    public void setUp() throws Exception {
        behaviour = new BehaviourManager();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        behaviour = null;
    }

    //    MovementRemaining
    @Test
    public void getMovementsRemaining() {
        //default 1
        assertEquals(behaviour.getMovementsRemaining(), 1);
    }

    @org.junit.Test
    public void setMovementsRemaining() {
        behaviour.setMovementsRemaining(5);
        assertEquals(behaviour.getMovementsRemaining(), 5);
    }

    //    Block to Place Left
    @Test
    public void getBlockPlacementLeft() {
        //default 1
        assertEquals(behaviour.getBlockPlacementLeft(), 1);
    }

    @org.junit.Test
    public void setBlockPlacementLeft() {
        behaviour.setBlockPlacementLeft(8);
        assertEquals(behaviour.getBlockPlacementLeft(), 8);
    }

    //    Build dome everywhere
    @Test
    public void isCanBuildDomeEverywhere() {
        //default false
        assertFalse(behaviour.isCanBuildDomeEverywhere());
    }

    @org.junit.Test
    public void setCanBuildDomeEverywhere_false() {
        behaviour.setCanBuildDomeEverywhere(false);
        assertFalse(behaviour.isCanBuildDomeEverywhere());
    }

    @org.junit.Test
    public void setCanBuildDomeEverywhere_true() {
        behaviour.setCanBuildDomeEverywhere(true);
        assertTrue(behaviour.isCanBuildDomeEverywhere());
    }

    // Can climb
    @Test
    public void isCanClimb() {
        //default true
        assertTrue(behaviour.isCanClimb());
    }

    @org.junit.Test
    public void setCanClimb_false() {
        behaviour.setCanClimb(false);
        assertFalse(behaviour.isCanClimb());
    }

    @org.junit.Test
    public void setCanClimb_True() {
        behaviour.setCanClimb(true);
        assertTrue(behaviour.isCanClimb());
    }


}