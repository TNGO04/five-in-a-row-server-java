<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->



<h1 align="center">Five In A Row Server</h1>
<div align="center">  
  Visit web app <a href="http://five-in-a-row-react-client.s3-website-us-east-1.amazonaws.com/">here</a>
  
  Front-end client [here](https://github.com/TNGO04/five-in-a-row-client-react)
</div>

<br/>


<!-- ABOUT THE PROJECT -->
## About The Project

![React App ScreenShot](https://user-images.githubusercontent.com/87917284/214201506-f633c279-228c-4a10-9a26-13b3ac8ca487.png)

Five In A Row, also known as Caro, is a popular Vietnamese logic game. The game rule is similar to Tic-Tac-Toe; however, a player needs 5 move in a row to win instead of 3. The board size is 20-by-20, much larger than the Tic-Tac-Toe board. Players take turn placing moves on available spaces on a grid, and whoever reach 5 moves in a row first win the game. 

In this project, I developed a web-based version of the game. Users can sign up, and then log into the web app to play games against other players or against an AI. 

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Usage

Project deployed [here](http://five-in-a-row-react-client.s3-website-us-east-1.amazonaws.com/).

User can sign up for an account, then use that account to log-in and play a game! If you don't want to sign up, you can use this credentials to log in:
```
Username: guest
Password: 12345
````

After logging in, you can start a Player-versus-Player game and wait for another player to join with the provided game Id. Alternatively, you can play a game against the game AI. 
<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Back-End Features
I initially built a console-based version of the game in Java to practice Object-Oriented Design and to have a try at implementing a game AI. Once I decided to deploy the game onto the web, Spring Boot is a logical solution to building the back-end using my existing code.

### Sequence Diagram:
![Sequence diagram](https://user-images.githubusercontent.com/87917284/214379684-4f468415-fb10-47a1-b391-9e5510ee2e32.png)

### REST APIs and WebSocket
#### Game API
Game API is implemented at endpoint `/game `:
* POST to `/start`: start a game given the first player PlayerX
* POST to `/connect`: connect to a game given the game ID and second player PlayerO
* POST to `/connect/random`: connect to a random game as second player PlayerO
* POST to `/connect/computer`: connect to an AI game as second player PlayerO
* POST to  `/gameplay`: add a move to game with given ID
* GET to `/gameplay/computer`: get game state after a move is made by game AI

#### User API
User API is implemented at endpoint `/users`:
* POST to `/register`: register user with given credentials
* POST to `/login`: verify user with given credentials

#### WebSocket
A WebSocket server is set up at STOMP endpoint `/gameplay` to allow for two-way communication between the client and server for real-time game updates. Every time a new game is started by the client, it automatically established WebSocket connection to the server and subscribe to listen at `/topic/game-progress/<GAMEID>`.

Whenever a request is made to `/connect`, `/connect/computer`,  `/gameplay` and `/gameplay/computer`, the REST controller automatically sends an updated version of the game object to `/topic/game-progress/<GAMEID>`. The subscribers (clients) can obtain this and display an updated version of the game. Using WebSocket, playerX can get updated whenever playerO makes a move, and vice versa. 

### MongoDB
MongoDB is chosen for this project due to its ease in storing complex Java objects. 
#### There are 2 collections within the database:
#### 1) users: 
* Each document within this collection is composed of a `username`, `password` and unique `_id`. 
* Mongo Repository implements CRUD functionality, as well as querying by username and by credentials. 
#### 2) games: 
* Mongo Repository implements CRUD functionality. 
* Each document composes of:
  * `status` represents game status as 'NEW', 'IN-PROGRESS', or 'FINISHED'
  * `playerX` stores a User object representing player X
  * `playerO` stores a User object representing player O
  * `winner` stores a User object representing winner (initialized to NULL)
  * `board` stores a GameBoard object representing current board state

### Java Model Objects
* `Game` class represents an instance of a game. 

* `user` package
  * `User` class represents a user of the web app.
  * `AIPlayer` implements the Singleton design pattern to prepresent one single AI player across all games
  
* `GamePlay` represents a move (with row and column coordinates, as well as gameID the move belongs to)
* `Symbol` enum represents X, Y, or EMPTY (valid contents of each cell on the board)

* `board` package
  * `GameBoard` represents the game board as a 2D matrix of Symbol objects and have methods to extract certain information from current board state, such as checking for consecutive streaks

* `streak` package
  * `Streak` class stores count of unblocked and blocked-on-one-side streaks of a certain length. It does not store count of streaks that are blocked on both sides, since that streak cannot be added to on either side and is therefore useless in terms of gameplay
  * `StreakList` stores a list of Streak object with different lengths (from 1 to 5)
* `MinimaxAI` classe represents the brain of the AI, helping to find the optimal move for the AI to make given a board state

### Game AI Implementation

Five In A Row is essentially a more complex version of Tic Tac Toe, since more moves are needed to win and the board is much larger. Minimax algorithm is commonly used to create AI player for two player games, including Tic Tac Toe.
The minimax algorithm works backwards from the end of the game, and at each step assuming either that the opponent will make a move to minimize the probability of winning for the current player or that the current player will make a move to maximize their chances of winning.

![image](https://user-images.githubusercontent.com/87917284/214413889-b9e32247-9774-4b5c-87fe-1958af306d5b.png)

As seen from the image above, this algorithm considers all possible moves until game is terminated. This is manageable for Tic Tac Toe, but for games with larger dimension, it is evident to be very computationally intensive to consider all possible legal moves on the board. I made two modifications to adapt the algorithm for this game.

#### Reducing action set

At each step along the algorithm, original minimax considers all possible legal moves. However, in Five In A Row, an observation can be made that you should only make a move that is adjacent to another existing move, either to block an opponent or add to s streak of your own. If we reduce the action set to only moves that are adjacent to at least another existing move on the board, we can reduce computation effort significantly. 

`GameBoard` class offers method `isDisconnected` to check if a move is adjacent to any other moves. `MinimaxAI` can use this public method to only add a potential move to action set if it is not disconnected. 

<div align="center">
<img height=200 src="https://user-images.githubusercontent.com/87917284/214418256-9f4b045a-8e51-4d66-aa23-67b5fbbfc316.png"/>
<img height=200 src="https://user-images.githubusercontent.com/87917284/214419095-07280a44-e44f-4349-8b06-76e981611c18.png"/>
<p>Left: disconnected move, Right: connected move</p>
</div>

### Designing Utility Function for Depth Limited Minimax

Original Minimax algorithm would explore moves until the game is terminated. This can be exponentially intensive in Five In A Row, as it is strategically more complex and more moves are made before the game is terminated. It is not possible to consider all moves until termination, so I implemented a depth-limited algorithm that use a utility function to score how optimal a move is. 

The utility scoring/estimating function utilizes heuristic/knowledge of the game to minimize the depth needed to make a quality decision. 
Utility scores range from 1 (AI player is winning) to -1 (opponent player is winning). 
A list of streaks of varying length (1-5) is compiled from a board state, and each streak is scored differently based on how favorable they are. 
Since the objective is to make 5-in-a-row, it is favorable to make streaks as long as possible, so streaks with longer length are scored higher than shorter lengths. 
Since an unblocked streak offers more potential for expanding, unblocked streak are scored higher than blocked ones with the same length. 
  
<p align="center">
  <img src="https://i.ibb.co/MSQ2BLV/Utility.png" />
</p>
    
  Certain board states are win-adjacent, in which no winning streak can be detected but streaks that will result in a win condition is detected. One such example is an unblocked streak-of-4, it is not 5-in-a-row,
  but there is no way an opponent can block both side to prevent this from expanding to 5-in-a-row. These win-adjacent streak are scored at 1.0 in the utility function. 

<div align="center" font-size="0.5rem">
  <img src="https://user-images.githubusercontent.com/87917284/214426351-c0bb37fb-a7a2-402d-b1f6-ba7beaa27629.png" />
  
Opponent cannot prevents current player from winning with win-adjacent moves: unblocked 4-in-a-row, two connected 3-in-a-row, or connected 4-in-a-row and 3-in-a-row
</div>
  
  I incorporated scoring of both the AI moves and opponent moves. By using knowledge of the game, I was able to created a game AI needing to only consider all moves within a depth of 2. 
  
  ```Utility of a board state =  Utility of all streaks by AI - Utility of all streaks by opponent```
  
## Limitations and Future Ideas
* The server programs the AI to be the starting player of the game in a game against computer. As with most 2-player game of similar character (chess, Tic Tac Toe), first player has a game advantage. In the future, I want to work on adapting the back-end to support AI player playing second.
* I also want to explore implementing other AI algorithm, such as Q-learning, etc.
* Configure the project for HTTPS requests and re-deploy the web app over HTTPS protocol for better security. 


## Acknowledgements
1. Back-end REST API based upon [this tutorial](https://www.youtube.com/watch?v=XwQJRfv9Mfg) on implementing Server and WebSocket in SpringBoot
2. [Web Development Course](https://www.youtube.com/watch?v=lyPmK9cO_ZU&list=PL_GGiAMracOUjPUjIbmrD6WIPeOJQgozq) by Professor Jose Annunziato
3. [README template](https://github.com/othneildrew/Best-README-Template) 
4. [Artificial Intelligence course](https://cs50.harvard.edu/ai/2020/) by Harvard University



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[JavaScript]: https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black
[Spring]: https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[Spring-url]: https://spring.io/projects/spring-boot
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[Redux]: https://img.shields.io/badge/Redux-593D88?style=for-the-badge&logo=redux&logoColor=white
[Mongo-DB]: https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white
[AWS]: https://img.shields.io/badge/Amazon_AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white
[Figma]: https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white
