/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mturing.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mturing.model.basics.Point;
import mturing.view.MainFrame;

/**
 *
 * @author Allan Leon
 */
public class TuringMachine {
    
    private Set<State> states;
    private List<TMTransition> transitions;
    //private List<Set<TMConfiguration>> configurations;
    private TMConfiguration configuration;
    private List<State> reachableStates;
    private State initialState;
    private int createdStatesQuantity;
    
    public TuringMachine() {
        states = new HashSet<>();
        reachableStates = new ArrayList<>();
        transitions = new ArrayList<>();
        initialState = null;
        //configurations = new ArrayList<>();
        createdStatesQuantity = 0;
    }

    /**
     * @return the states
     */
    public Set<State> getStates() {
        return states;
    }

    /**
     * @return the transitions
     */
    public List<TMTransition> getTransitions() {
        return transitions;
    }

    /**
     * @return the configurations
     */
    /*public List<Set<TMConfiguration>> getConfigurations() {
        return configurations;
    }*/
    
    public TMConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @return the reachableStates
     */
    public List<State> getReachableStates() {
        return reachableStates;
    }

    /**
     * @return the initialState
     */
    public State getInitialState() {
        return initialState;
    }

    /**
     * @return the createdStatesQuantity
     */
    public int getCreatedStatesQuantity() {
        return createdStatesQuantity;
    }

    /**
     * @param states the states to set
     */
    public void setStates(Set<State> states) {
        this.states = states;
    }

    /**
     * @param transitions the transitions to set
     */
    public void setTransitions(List<TMTransition> transitions) {
        this.transitions = transitions;
    }

    /**
     * @param configurations the configurations to set
     */
    /*public void setConfigurations(List<Set<TMConfiguration>> configurations) {
        this.configurations = configurations;
    }*/
    
    public void setConfiguration(TMConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * @param reachableStates the reachableStates to set
     */
    public void setReachableStates(List<State> reachableStates) {
        this.reachableStates = reachableStates;
    }

    /**
     * @param initialState the initialState to set
     */
    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    /**
     * @param createdStatesQuantity the createdStatesQuantity to set
     */
    public void setCreatedStatesQuantity(int createdStatesQuantity) {
        this.createdStatesQuantity = createdStatesQuantity;
    }
    
    public void addState(State state) {
        states.add(state);
        createdStatesQuantity++;
    }
    
    public void createState(Point clickedPoint) {
        addState(new State(String.format("q%d", createdStatesQuantity), false, clickedPoint));
    }
    
    public TMTransition createTransition(State initial, State next) {
        TMTransition newTransition = new TMTransition(initial, next);
        int i = 0;
        boolean found = false;
        while (i < transitions.size() && !found) {
            if (transitions.get(i).equals(newTransition)) {
                newTransition = transitions.get(i);
                found = true;
            }
            i++;
        }
        if (!found) {
            addTransition(newTransition);
        }
        return newTransition;
    }
    
    public void addTransition(TMTransition transition) {
        transitions.add(transition);
    }
    
    public void removeState(State state) {
        if (state == initialState) {
            initialState = null;
        }
        states.remove(state);
        int i = 0;
        while (i < transitions.size()) {
            if (transitions.get(i).getInitialState().equals(state) || transitions.get(i).getNextState().equals(state)) {
                transitions.remove(i);
            } else {
                i++;
            }
        }
    }
    
    public void removeTransition(TMTransition transition) {
        transitions.remove(transition);
    }
    
    /*
    public void start(String word) {
        configurations.clear();
        Set<Configuration> startList = new HashSet<>();
        startList.add(new Configuration(initialState, word));
        configurations.add(startList);
    }
    
    public boolean next() {
        Set<Configuration> nextList = new HashSet<>();
        boolean ok;
        for (Configuration current : configurations.get(0)) {
            if (!current.isDead() && !current.isValid()) {
                ok = false;
                for (Transition transition : transitions) {
                    try {
                        for (Configuration newConf : transition.execute(current)) {
                            nextList.add(newConf);
                            ok = true;
                        }
                    } catch (TransitionException ex) {
                        Logger.getLogger(Automaton.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (!ok) {
                    current.setDead(true);
                    nextList.add(current);
                }
            }
        }
        if (nextList.size() > 0) {
            configurations.add(nextList);
            configurations.remove(0);
            return true;
        }
        return false;
    }
    */
    
    public void start(String word) {
        configuration = new TMConfiguration(initialState, word.toCharArray(), 0);
        configuration.increaseLeftWord();
        configuration.increaseRightWord();
    }
    
    public boolean next() {
        if (!configuration.isDead()) {
            for (TMTransition transition : transitions) {
                try {
                    configuration = transition.execute(configuration);
                    return true;
                } catch (TransitionException ex) {
                    configuration.setDead(true);
                }
            }
        }
        return false;
    }
    
    private boolean checkAcceptedStates() {
        for (State state : states) {
            if (state.isAccepted()) {
                return true;
            }
        }
        return false;
    }
    
    public void validate() throws AutomatonException {
        if (initialState == null) {
            throw new AutomatonException("No initial state found.");
        } else if (!checkAcceptedStates()) {
            throw new AutomatonException("Must exist at least one accepted state.");
        }
    }
    
    public boolean checkReachableStates() {
        reachableStates.clear();
        if (states.isEmpty()) {
            return true;
        }
        reachableStates.add(initialState);
        for (int i = 0; i < reachableStates.size(); i++) {
            for (TMTransition transition : transitions) {
                if (transition.getInitialState().equals(reachableStates.get(i))) {
                    if (!reachableStates.contains(transition.getNextState())) {
                        reachableStates.add(transition.getNextState());
                    }
                }
            }
        }
        return reachableStates.size() == states.size();
   }
    
    public void removeUnreachableStates() {
        Set<State> unreachableStates = new HashSet<>();
        for (State current : states) {
            if (!reachableStates.contains(current)) {
                unreachableStates.add(current);
            }
        }
        for (State current : unreachableStates) {
            removeState(current);
        }
        MainFrame.drawingState = MainFrame.DrawingState.Drawing;
    }
    
    public void updateTransitions() {
        for (TMTransition current : transitions) {
            current.calculatePos();
        }
    }
}