RoboCodeGit
===========

A box for all my robocode projects.

The newest and most continuously updated project is the cepl package.
  This is still in the development and testing phase for movement and movement prediction/projection.
  - The goal is to have a stable base to work with from which I can add on more advanced features.
  
  The next step for the project is completing the self-focused movement based on state space search.
  The movement calculations are based primarily on a "wave surfing" style, where the origin of a bullet and its distance traveled are known, but its heading is not. The robot moves to minimize the ammount of time it spends at high probability locations (distances where bullets could be).
  Another set of factors that will be considered is variations on where the robot has been hit previously, in an attempt to predict where it was shot at. To its fullest extent, this will involve analyzing and predicting the other robot's prediction algorithm to try and predict where the enemy thinks that my robot will be moving.

  After that, the movement projection and analysis will comprise most of the work, because it is based on the most complete dataset available to the robot.
  Parts of this will include
  - Pattern matching
  - Pattern analysis (statistical analysis)
  - Control model simulation
