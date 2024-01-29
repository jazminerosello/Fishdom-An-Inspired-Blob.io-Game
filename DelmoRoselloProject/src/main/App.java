/*************************************************************************************************************************
 *
 * CMSC 22 Object-Oriented Programming
 * FishDom
 * Problem Domain: Fishdom is a game where the goal is for the player to eat as many foods and enemies as he can while avoiding bigger enemies that can eat him.
 * When a guardian is created, it is given initially a score of 0 foods and enemies eaten. The guardian can move up, left, right, or down.
 * There are 50 foods in the map where both the player and enemy can consume to get bigger.
 * Enemies move at random directions and die when the bigger player or co-enemy eats him.
 * When the player eats a smaller enemy, the player's size increases,
 * However, when the player tries to eat a bigger enemy, the player dies.
 * Different power-ups can be collected by the guardian:
 * Item 			Effect
 * Energy drink 	doubles the speed of for 5 seconds
 * Shield 	 		provides immunity for 5 seconds
 *
 * @author Jazmine Rosello and Arawela Delmo
* @created_date 2022-26-12 18:34
 *************************************************************************************************************************/
package main;

import fishingGame.Game;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application
{


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		Game game = new Game();
		game.setStage(stage);
	}
}
