# Minesweeper
This was the final project for my Programming Languges and Techniques: I class written in Java using the Swing GUI framework.

It features the classic game of Minesweeper, with a few extra features.

## Interesting Features

### Animations
This game utilizes a few animation sequences. As Swing runs the GUI concurrently by default for performance, the animations were achieved using a linked-list of timers, which each activated to produce the desired animation frame, then upon completion, triggered the next timer. This allowed for finer control over the timings, as well as the ability to arbitrarily remove or reorder frames.

### Saving Game State
The state of the game in progress can be saved at any time. This was accomplished using buffered readers and writers, which buffered chunks of data together before reading or writing to allow for fewer read/write calls overall and thus increase performance.

## Game Mechanics

On each new game, the board is initally blank, and then filled in after the user's first click, which is always guarenteed to be a space touching no mines (denoted by an empty square). At this point the timer begins.

Right clicking on a square will flag it, and the user will not be able to open that tile until they unflag it. The user is told how many bombs are on the map by the number of flags they are given.

When the user clicks on a square, it reveals how many bombs are adjacent to that tile. If there are 0 bombs adjacent to that tile, the game will automatically click all adjacent tiles recursively.

If the user clicks on a square with a bomb, that tile will reveal and red square, and an animation will begin to reveal all bomb tiles one by one, starting at the bottom right corner working up (skipping whichever bomb the user already revealed).

When the user has revealed all non-bomb tiles, the timer stops, and a similar animation plays, except this time the bomb squares are not opened, but instead turn blue.

## Saved State

Whenever the user wants, they can click on the 'Save Game' button to save the current state of their game, including the timer, flags, and opened tiles, onto the disk. This game state can be replayed at any time, even after the applicaiton has been closed and reopened, but clicking on the 'Load Game' button.

The user can only save the game state once a game has begun, as the game doesn't have any state before a user clicks on the board.
