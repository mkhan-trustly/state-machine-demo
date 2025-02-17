## State machine demo

### State machine

A state machine is a concept where an entity can exist in different states, 
and transitions between these states occur based on predefined rules. 
Think of it like a traffic light: it transitions from 
```
Red → Green → Yellow → Red and never skips a state.
```

A finite state machine (FSM) is a specific type of state machine that has:
1.	A finite number of states.
2.	Defined transitions between states.
3.	A current state.
4.	A way to move from one state to another based on conditions.

For example, a washing machine has states like 
```
Idle → Washing → Rinsing → Spinning → Done.
```

### Solution
We will use Spring State Machine to enforce allowed state transitions for WorkOrderState. 
Any invalid transition will throw an exception.

### Key features of this implementation:

- Strict transitions – Only allowed transitions are possible; invalid transitions throw an exception.
- Service-based API – Calls applyEvent() to move states.
- Tested – JUnit tests ensure it works as expected.