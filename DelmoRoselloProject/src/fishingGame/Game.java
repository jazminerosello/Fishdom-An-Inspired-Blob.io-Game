/*************** * This class game is the starting class of the game, it contains different scenes that are needed
 * for the game application, it have the respective buttons adn when click a respective action
 * will be called.
 *
 * More description of the codes can be found and read below
 *
 * @author Jazmine Rosello and Arawela Delmo
* @created_date 2022-26-12 18:34
 *********/
package fishingGame;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Game {
	private Stage stage;
	private Scene menuScene; // scene where main menu is displayed
	private Scene gameScene; // scene where game is displayed
	private Scene instructionScene;
	private Scene aboutScene;
	private Group root;
	private Canvas canvas;
	private DropShadow shadow; //for the shadow effect when button is clicked

	// for sound effect
	private Media clicksound = new Media(new File("src/images/Menu Game Button Click Sound Effect (128 kbps) (1).mp3").toURI().toString());
	private Media sound = new Media(new File("src/images/Pirate Accordion Music - Pirates of the Coast (128 kbps).mp3").toURI().toString());
	private Media starto = new Media(new File("src/images/startTheGame.mp3").toURI().toString());


	public final static int MAP_WIDTH = 2400;
	public final static int MAP_HEIGHT = 2400;
	public final static int WINDOW_WIDTH = 800;
	public final static int WINDOW_HEIGHT = 800;
	public final static int BOUNDS1 = 0;
	public final static int BOUNDS2 = -(Game.MAP_WIDTH-Game.WINDOW_WIDTH);
	public final static int WINDOW_CENTER = Game.WINDOW_WIDTH/2 - Fish.FISH_INIT_DIAMETER/2;
	public final static Image background = new Image("images/gameBg.png", Game.MAP_WIDTH, Game.MAP_HEIGHT, false, false, false);

	public Game() {
		this.canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
		this.root = new Group();

		// our canvas is showing at the root
		this.root.getChildren().add(this.canvas);
		this.gameScene = new Scene(this.root);

		this.shadow =  new DropShadow();//initialize shadow as new dropShadow

	}



	// setting up the stage and running the app
	public void setStage(Stage stage) {
		// the stage that the Game is on is the stage created in start() of App
		this.stage = stage;

		stage.setTitle("Fishdom");

		//to play the sound effect
		MediaPlayer mediaPlayer = new MediaPlayer(sound);

		mediaPlayer.setOnEndOfMedia(new Runnable() { //to repeat the sound over and over again until the game is still on
		       public void run() {
		         mediaPlayer.seek(Duration.ZERO);
		       }
		   });
		 mediaPlayer.play();

		this.initMenu(stage);//call the initMenu as the initial na lalabas sa screen
		stage.setResizable(false);
		stage.show();

	}



	// display the main menu
	private void initMenu(Stage stage) {
		// create its own canvas and graphicsContext
		Canvas canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// set a bg image for the scene
		Image bg = new Image("images/menuBg.png", Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, false, false);
		gc.drawImage(bg, 0, 0);

		// display buttons: "new game", "instructions", "about"
		VBox menuButtons = this.createMenuButtons();

		// add bg image and menu buttons to the root of scene
		StackPane menuRoot = new StackPane();
		menuRoot.getChildren().addAll(canvas, menuButtons);
		this.menuScene = new Scene(menuRoot);

		stage.setScene(this.menuScene); //set the scene as menuScene
	}



	// returns buttons needed for the main menu
	private VBox createMenuButtons() {
		// NOTE: Vbox lays out its children in a single vertical column
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(5));
		vbox.setSpacing(10);

		Button b1 = new Button("New Game");
		Button b2 = new Button("Instructions");
		Button b3 = new Button("About");

		//for the font of the text inside the button
		Font font = Font.font("Times New Roman", FontWeight.BOLD, 35); // yung 30 here is yung size kaya malaki yung button sa display
		b1.setFont(font);
		b2.setFont(font);
		b3.setFont(font);

		//for the style property of the button, the color of button, the outline of the button, the text etc.
		b1.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #9F2B00; -fx-background-color: linear-gradient(#FF8B00, #F4D03F, #FF8B00); -fx-border-color:#9F2B00; -fx-border-radius: 15px; -fx-border-width: 5px" );
		b2.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #9F2B00; -fx-background-color: linear-gradient(#FF8B00, #F4D03F, #FF8B00); -fx-border-color:#9F2B00; -fx-border-radius: 15px; -fx-border-width: 5px" );
		b3.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #9F2B00; -fx-background-color: linear-gradient(#FF8B00, #F4D03F, #FF8B00); -fx-border-color:#9F2B00; -fx-border-radius: 15px; -fx-border-width: 5px" );

		vbox.getChildren().addAll(b1,b2,b3);//add the buttons to the vbox

		MediaPlayer clickPlayer = new MediaPlayer(clicksound); //initialize MediaPlayer as the clicksound


		// create actions when a specific button is pressed
		b1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

				clickPlayer.play(); //play the click sound whenever button 1 is clicked
				b1.setEffect(shadow); //for the shadow effect when button is clicked
				setGame(stage, "game");// allow player to play the game

			}
		});

		b2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

				clickPlayer.play(); //play the click sound whenever button 1 is clicked
				b2.setEffect(shadow); //for the shadow effect when button is clicked
				setGame(stage, "instruction");// displays the instruction scene
			}
		});

		b3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

				clickPlayer.play(); //play the click sound whenever button 1 is clicked
				b3.setEffect(shadow); //for the shadow effect when button is clicked
				setGame(stage, "about");// displays the about scene
			}
		});

		return vbox;
	}



	// change scenes (depending on parameter passed)
	private void setGame(Stage stage, String string) {
		GraphicsContext gc = this.canvas.getGraphicsContext2D(); // to be able to draw on canvas
		//Media sound = new Media(new File("images/Pirate Accordion Music - Pirates of the Coast (128 kbps).mp3").toURI().toString());

		if (string.equals("game")) {
			// setting game scene as the current scene of the stage
			stage.setScene(this.gameScene);

			MediaPlayer mediaPlayer = new MediaPlayer(starto); //initialize mediaPlayer containing the starto sound

			// game timer is used to animate movements etc
			GameTimer gameTimer = new GameTimer(gameScene, gc);
			gameTimer.start(); // internally calls the handle() method of GameTimer

			//to play the mediaPlayer with the set duration and rate of time
			mediaPlayer.seek(Duration.ONE);
			mediaPlayer.setRate(1);
			mediaPlayer.play();

		} else if(string.equals("instruction")) {
			initInstruct(stage); //call the method initInstruct to display the instruction scene

		} else if(string.equals("about")) {
			initAbout(stage); //call the method initInstruct to display the about scene
		}
	}

	/*
	 * FOR ABOUT SCENE
	 */

	public void initAbout(Stage stage) {

		//create its own canvas and gc
		 Canvas canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
		 GraphicsContext gc = canvas.getGraphicsContext2D();

		 Image bg = new Image("images/tabMenu.png", Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, false, false);
	     gc.drawImage(bg, 0, 0); //set the bg image

	     //for the image that will be displayed
	        Image developer1 = new Image("images/wela.png", 300, 300, false, false);
	        gc.drawImage(developer1, 0, 60);

	        Image developer2 = new Image("images/jaja.png", 300, 300, false, false);

	        gc.drawImage(developer2, 0, 280);


	    //for the text to be displayed
	        String text = "About us";
	        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 50)); //set font
			gc.setFill(Color.GHOSTWHITE); //set color of the text in the canvas
			gc.fillText(text, 300, 70); //draw or filltext the text in canvas in x and y position

			String name1 = "Arawela Delmo" ;
			gc.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
			gc.fillText(name1, 250, 150);

			String description = "\nShe is a 2nd Year BS Computer Science student in University of the \nPhilippines Los Banos. Cool movies about programmers \nenticed "
					+ "her to take this path, and now she's \ntrying to find her niche. She's taking it one step at a time \nand going through "
					+ "this journey together \nwith her coffee. " ;
			gc.setFont(Font.font("Georgia", 18));
			gc.fillText(description, 250, 160);

			String name2 = "Ma. Jazmine P. Rosello";
			gc.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
			gc.fillText(name2, 250, 367);

			String description2 = "\nShe is a 2nd Year BS Computer Science in University of the \nPhilippines Los Banos. She lives in Bulacan and her dream"
					+ " was to \nbecome  a web developer. She is not a professional in the field \nshe is in but she is trying to climb up just to achieve"
					+ "her goal in life. \nShe believe that if everyone can, you can too. " ;
			gc.setFont(Font.font("Georgia", 18));
			gc.fillText(description2, 250, 377);



		//to display the about scene in the screen
		StackPane root = new StackPane();
        root.getChildren().addAll(canvas, this.createbutton()); //add createCanvas() and createVbox() to the root of this scen
        this.aboutScene = new Scene(root); //then set the this.scene as new scene(root)
        stage.setScene(this.aboutScene); //set the stage scene as the aboutScene
	}

	//for the buttons needed in the aout scene
	private VBox createbutton() {

		//created button needed here in abut scen

		VBox vbox = new VBox();
        vbox.setAlignment(Pos.BOTTOM_LEFT); //alignment of the vbox
        vbox.setPadding(new Insets(40)); //the padding of the text inside the button
        vbox.setSpacing(8);

        Button b1 = new Button("Back"); //create button back
        b1.setAlignment(Pos.BOTTOM_LEFT); //alignment of button


        Button b2 = new Button("For References"); //button for reference that whenever it is clicked, txt file will open containing the references developers used in creating this program
        b1.setAlignment(Pos.BOTTOM_LEFT);

        //for the font of the text inside the button
        Font font = Font.font("Times New Roman", FontWeight.BOLD, 20);
        b1.setFont(font); //set fornt
        b2.setFont(font);

        //for the style of the buttons, the color etc.
        b1.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #0B74B0" );
        b2.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #0B74B0" );


        //add the buttons in vbox as its children
		vbox.getChildren().add(b2);
        vbox.getChildren().add(b1);

        MediaPlayer clickPlayer = new MediaPlayer(clicksound); //initialize a mediaplayer with the clicksound as the sound

        //for event handler
        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	clickPlayer.play(); //to play the sound
            	b1.setEffect(shadow); //for teh button to have a shadow effect when it is clicked
            	initMenu(stage);//kapag pinindot 'yung button back,babalik sa menu scene
            }

        });

        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e)  {

            	try {
            		clickPlayer.play(); //to play the sound
                	b1.setEffect(shadow); //for teh button to have a shadow effect when it is clicked
					Desktop.getDesktop().browse(new URI("https://docs.google.com/document/d/1oc-ihWhmLvc26FoJ_pIWlS2ccv3AP54CzLO7Ku2EJk4/edit?usp=sharing"));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}// //kapag pinindot 'yung button reference, pupunta sa reference.txt containing the references used in the progrma
            }



        });
		return vbox; //return vbox
	}

	/*
	 * FOR INSTRUCTION SCENE
	 */

	public void initInstruct(Stage stage) {
		// create its own canvas and gc
		Canvas canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
	    GraphicsContext gc3 = canvas.getGraphicsContext2D();	// we will pass this gc to be able to draw on this Game's canvas

		// display the bg
		Image bg = new Image("images/tabMenu.png", Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT, false, false);
		gc3.drawImage(bg, 0, 0);

		// for the text that will be displayed on the screen
		gc3.setFill(Color.GHOSTWHITE);
        String text = "The rules of the game are simple.";
        gc3.setFont(Font.font("Times New Roman",FontWeight.BOLD, 25)); //set font of the text in canvas
        gc3.fillText(text, 80, 150);

		String text2 = "Player must eat as many foods and enemies as he can while avoiding bigger enemies.";
		gc3.setFont(Font.font("Times New Roman",FontWeight.SEMI_BOLD, 20));
		gc3.fillText(text2, 80, 200);

		String text3 = "Remember, not only the player can eat food, enemies too!.";
		gc3.setFont(Font.font("Times New Roman",FontWeight.SEMI_BOLD, 20));
		gc3.fillText(text3, 80, 240);

		String text4 = "If the bigger enemy tries to eat the player, then it is";
		gc3.setFont(Font.font("Times New Roman",FontWeight.SEMI_BOLD, 20));
		gc3.fillText(text4, 80, 280);

		String text5 = "GAME OVER!";
		gc3.setFont(Font.font("Times New Roman",FontWeight.BOLD, 30));
		gc3.fillText(text5, 500, 280);


		String text6 = "2 Powerups a player can have: ";
		gc3.setFont(Font.font("Times New Roman",FontWeight.BOLD, 20));
		gc3.fillText(text6, 280, 320);

		String text7 = "Speedboost";
		gc3.setFont(Font.font("Times New Roman",FontWeight.SEMI_BOLD, 20));
		gc3.fillText(text7, 80, 360);

		String text8 = " -> it doubles the speed of the player fish for 5 seconds.";
		gc3.setFont(Font.font("Times New Roman",FontWeight.SEMI_BOLD, 20));
		gc3.fillText(text8, 180, 360);

		String text9 = "Shield";
		gc3.setFont(Font.font("Times New Roman",FontWeight.SEMI_BOLD, 20));
		gc3.fillText(text9, 80, 400);

		String text10 = " -> provides immunity to the player for 5 seconds.";
		gc3.setFont(Font.font("Times New Roman",FontWeight.SEMI_BOLD, 20));
		gc3.fillText(text10, 140, 400);

		String text11 = " That's all. The rules of the game.\n";
		gc3.setFont(Font.font("Times New Roman",FontWeight.BOLD, 25));
		gc3.fillText(text11, 80, 460);

		//to display the about scene in the screen
		StackPane root = new StackPane();
        root.getChildren().addAll(canvas, this.createVbox()); //add createCanvas() and createVbox() to the root of this scen
        this.instructionScene = new Scene(root); //then set the this.scene as new scene(root)
        stage.setScene(this.instructionScene);	//set the stage scene as the instruction scene
	}


	//for the buttons needed in the instruction scene
	private VBox createVbox() {
		//create vbox and buttons
		VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_LEFT); //alignemnt of the vbox
        vbox.setPadding(new Insets(40));
        vbox.setSpacing(8);

        //create button back
        Button b1 = new Button("Back");
        b1.setAlignment(Pos.TOP_LEFT); //alignment of button back

        //for the font of the text insidet the button
        Font font = Font.font("Times New Roman", FontWeight.BOLD, 20);
        b1.setFont(font); //set font of the text inside the button


        //set the style and color of the text inside the button
        b1.styleProperty().setValue("-fx-background-radius: 15px; -fx-text-fill: #0B74B0" );

        vbox.getChildren().add(b1); //add button in vbox

        MediaPlayer clickPlayer = new MediaPlayer(clicksound); //create a mediapplayer containing the clicksound

        //button event handle
        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	clickPlayer.play(); //to play the sound
            	b1.setEffect(shadow); //for the button to have a shadow effect when it is clicked
            	initMenu(stage);//kapag pinindot 'yung button back,babalik sa menu scene
            }
        });

		return vbox; //return vbox
	}

}
