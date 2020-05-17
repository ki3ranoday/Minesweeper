# Minesweeper

Left click to open a cell, right click to flag it. Right click on an open cell with the correct number of nearby flags to open all the cells around it. If your right click doesn't work or you don't like to use the right click, you can use the spacebar to flag and quick-open cells.

You can either play the game by yourself, or set the built-in AI off to try and solve the puzzle. Try different difficulties Easy, Medium and Hard which have different numbers of bombs and different sized boards.

Because building the Java project takes a little bit of time, I included a demo video called MinesweeperExample. The example shows me playing the game (I didn't try to win, just showed basic functionality) and then shows the AI taking a shot at a hard, easy and medium board. In the video the AI solves the hard and easy boards, but guesses wrong in the end of the Medium game. My AI is not perfect, because when it doesn't have a definite choice it opens a cell with the least likelihood of being a bomb based on some calculations, but solves the hard board very regularly.

Read more about the game and the AI in the comments of the code
