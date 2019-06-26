# LearningToEscape
A SWEN30006 2018 project

This is a Java project start from design, implement, integrate and test a car autocontroller that is able to successfully traverse the map and its traps. 

It must also be capable of safely: 

1. exploring the map and locating the keys 

2. retrieving the keys in order
3. making its way to the exit 

A key element is that this design is modular, clearly separating out elements of behaviour/strategy that the autocontroller deploys.

The main design of this project is focused on decision-making. This includes higher-level
decisions, such as what our current objective is or what area of the map to explore next, and
lower level decision making such as when to apply turning to achieve our next destination.
Given a variety of changing information, our design approach considered how to best
encapsulate information and delegate responsibilities.