package invasion;

//Luke Abell-Smith
//CST100

/*
 * Inspiration from Stargate.
 * 
 * There are some spoilers.
 * The show did end a decade ago..
 * 
 * If you have not watched it, you should check it out. 
 * Stargate SG-1 is the longest running Sci-Fi TV Series. (10 seasons)
 * 
 * https://www.imdb.com/title/tt0118480/
 * https://en.wikipedia.org/wiki/Stargate_SG-1 (Spoilers)
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Invasion extends Application
{
	
	private ArrayList<Image> obShips;
	private ArrayList<Sprite> obFleet = new ArrayList<>();
	private Sprite obCity;
	private Canvas obCanvas;
	private GraphicsContext gc;
	private Stage obPrimeStage;
	
	private int nWave = 1;
	private String sPlayer;
	private ArrayList<Score> aScores = new ArrayList<>();
	private Score playerScore;
	
	private BorderPane obGamePane;
	
	private Sprite obDrone;
	
	private Image imgLeft, imgLeftD, imgRight, imgRightD, 
					imgFire, imgFireD, imgFireOff;
	private boolean fireEnabled = true;
	private boolean firePressed = false;
	
	private boolean gameLost = false;
	private boolean gameWon = false;
	
	
	private final int SCREEN_WIDTH = 500;
	private final int SCREEN_HEIGHT = 750;
	
	private int WEAPON_COOLDOWN = 1000; //Milliseconds
	private final int WEAPON_SPEED = 3;
	private final int WEAPON_STEER_SPEED = 5;
	
	
	@Override
	public void start(Stage obStage)
	{
		obPrimeStage = obStage;
		obGamePane = new BorderPane();
		
		
		obGamePane.setBottom(setControls());
		obGamePane.setStyle("-fx-background-color: navy");
		
		this.obCanvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.gc = this.obCanvas.getGraphicsContext2D();
		obCanvas.setStyle("-fx-background-color: blue");
		
		obGamePane.setCenter(obCanvas);
		
		loadImages("images/ori");
		
		obCity = new Sprite(new Image("file:images/skyline500.png"), 0, SCREEN_HEIGHT -30);
		obCity.render(this.gc);
		
		obStage.setScene(new Scene(getSplash(), 350,250));
		obStage.setTitle("Stargate Command");
		obStage.show();
		
	} //End Start
	
	private GridPane getSplash()
	{
		GridPane obSplashPane = new GridPane();
		obSplashPane.setAlignment(Pos.CENTER);
		obSplashPane.setPadding(new Insets(5));
		
		obSplashPane.add(new Label("You must be here to help save the planet!\n"
									+ "Enter your name to get signed up,\n"
									+ "then we will get right to work!\n "), 0, 0, 2, 1);
		
		TextField txtPlayerName = new TextField();
		obSplashPane.add(new Label("Player Name:  "), 0, 1);
		obSplashPane.add(txtPlayerName, 1, 1);
		
		Button cmdHelp= new Button("Instructions");
		obSplashPane.add(cmdHelp, 0, 2);
		
		cmdHelp.setOnAction( e -> {
			
			this.obPrimeStage.setScene(new Scene(getIntro(), 500, 300));
		});
		
		Button cmdStart = new Button("Let's do this!");
		obSplashPane.add(cmdStart, 1, 2);
		cmdStart.setDisable(true);
		
		txtPlayerName.setOnKeyReleased(e -> {
			if(txtPlayerName.getLength() > 8)
			{
				txtPlayerName.setText(txtPlayerName.getText().substring(0,8));
			}
			if(txtPlayerName.getLength() < 4)
			{
				cmdStart.setDisable(true);
			}
			if(txtPlayerName.getLength() >=4 && txtPlayerName.getLength() <=8)
			{
				cmdStart.setDisable(false);
			}
		});
		
		cmdStart.setOnAction( e -> {
			sPlayer = txtPlayerName.getText();
			
			if(sPlayer.equals("O'Neill") || sPlayer.equals("Sheppard"))
			{
				WEAPON_COOLDOWN = 200;
			}
			
			//Center Window so the bottom isn't cut off
			obPrimeStage.setX(((Screen.getPrimary().getBounds().getWidth()) - SCREEN_WIDTH ) / 2);
			obPrimeStage.setY(((Screen.getPrimary().getBounds().getHeight()) - SCREEN_HEIGHT ) / 2);	
			this.obPrimeStage.setScene(new Scene(this.obGamePane));
			launchWave();
		});
		
		return obSplashPane;
	}
	
	
	private GridPane getIntro()
	{
		GridPane obIntroPane = new GridPane();
		obIntroPane.setAlignment(Pos.CENTER);
		obIntroPane.setPadding(new Insets(5));
		ImageView imgDrone = new ImageView(new Image("file:images/drones/drone.png"));
		imgDrone.setFitHeight(263);
		imgDrone.setFitWidth(70);
		obIntroPane.add(imgDrone, 1, 0);
		
		obIntroPane.add(new Label("Use the Ancients' Drone weapons to protect Earth from the Ori fleet!\n\n"
								+ "You can see an inactive one to your right.\n"
								+ "You can use the interface we built to control them.\n\n"
								+ "You can also control them directly with your mind.\n"
								+ "(Arrow keys to steer, and spacebar to fire)\n"
								+ "(You may need to click the fire button the first time\n"
								+ "to initialize the system)\n\n"
								+ "Unfortunately, our interface only allows us to launch one drone per second.\n"
								+ "...Unless you have the Ancient Gene...\n"), 0, 0);
		
		Button cmdBack = new Button("Back to Start Screen");
		obIntroPane.add(cmdBack, 0, 5);
		
		cmdBack.setOnAction(e -> {
			this.obPrimeStage.setScene(new Scene(getSplash(), 300, 300));
		});
		
		return obIntroPane;
	}
	
	
	private void launchWave()
	{
		int nShips = (1 + (nWave * 3));
				
		gameWon = false;
		gameLost = false;

		for(int i=0; i<nShips; i++) //Launch multiple ships
		{	
			new Thread( () -> launchShip()).start();
			try
			{
				//This prevents an occasional problem/bug...
				Thread.sleep(2); 
			}
			catch(InterruptedException e) {}
		}

		Thread thCheckWon = new Thread( () -> checkWon());
		thCheckWon.setDaemon(true);
		thCheckWon.start();
		
		Thread thCheckLost = new Thread( () -> checkLost());
		thCheckLost.setDaemon(true);
		thCheckLost.start();
	
	} //End launchWave
	
	private void launchShip()
	{
		int nHPos = ((int) ((Math.floor(Math.random() * 10))) * 45);
		Sprite obShip = new Sprite(obShips.get(0), nHPos, -75);
		obFleet.add(obShip);
		
		startTask(obShip);	
	}
	
	
	private void loadImages(String sPath)
	{
		File obDir = new File(sPath);
		
		if(!obDir.isDirectory())
		{
			System.out.printf("Alien Ship Images Not Found!\n");
			return;
		}
		
		obShips = new ArrayList<>();
		
		try
		{
			for(String sVal : obDir.list())
			{
				if(sVal.matches(".*png"))
				{
					File obFile = new File(obDir.getAbsolutePath() + "/" + sVal);
					obShips.add(new Image(new FileInputStream(obFile)));
				}
			}
		}
		catch(FileNotFoundException e) {}
		
	}
	
	
	public HBox setControls()
	{
		estImages();
		HBox obBox = new HBox(30);
		obBox.setAlignment(Pos.CENTER);
		
		ImageView obLeft = new ImageView();
		ImageView obRight = new ImageView();
		ImageView obFire = new ImageView();
		
		setImageCharacteristics(obLeft, this.imgLeft);
		setImageCharacteristics(obRight, this.imgRight);
		setImageCharacteristics(obFire, this.imgFire);
		
		obLeft.setOnMousePressed(e -> moveLeft(obLeft));
		obLeft.setOnMouseReleased(e -> obLeft.setImage(this.imgLeft));
		
		obRight.setOnMousePressed(e -> moveRight(obRight));
		obRight.setOnMouseReleased(e -> obRight.setImage(this.imgRight));
		
		
		obFire.setOnMousePressed(e -> {
			obFire.requestFocus();
			firePressed(obFire);
		});
		
		obFire.setOnMouseReleased(e -> {
			fireReleased(obFire);
		});
		
		obFire.requestFocus();
		obFire.setOnKeyPressed(e -> {
			
			switch(e.getCode())
			{
				case LEFT:
					moveLeft(obLeft);
					obLeft.setImage(this.imgLeft);
					break;
					
				case RIGHT:
					moveRight(obRight);
					obRight.setImage(this.imgRight);
					break;
				
				case SPACE:
					firePressed(obFire);
					fireReleased(obFire);
					
				default:
					break;
			} //End Switch
		}); //End KeyPressed
		
		obBox.setStyle("-fx-background-color: grey");
		obBox.getChildren().addAll(obLeft, obFire, obRight);
		return obBox;
	} //End setControls
	
	
	//If the button is enabled, update image, disable it, start cooldown thread, fire
	private void firePressed(ImageView obFire)
	{
		obFire.setImage(this.imgFireD); //Pressed Image
		firePressed = true;				//Mark Pressed
		if(fireEnabled)
		{
			fireEnabled = false;
			new Thread( () -> fireCooldown(obFire)).start();

			fireWeapon();
		}
	}
	
	
	//If cooled down already, update image to ready. Else, update to disabled.
	private void fireReleased(ImageView obFire)
	{
		firePressed = false;		//Mark released
		
		if(fireEnabled)
		{
			obFire.setImage(this.imgFire);
		}
		else
		{
			obFire.setImage(imgFireOff);	
		}
	}
	
	
	//Wait for cooldown. Enable button. Then if the button has already been released, update image.  
	private void fireCooldown(ImageView obFire)
	{
		try
		{	
			Thread.sleep(WEAPON_COOLDOWN);
		} catch (InterruptedException e1) { e1.printStackTrace(); }

		fireEnabled = true;
		
		if(!firePressed)
		{
			obFire.setImage(this.imgFire);			
		}
	}
	
	//Load images for buttons
	private void estImages()
	{
		this.imgLeft = new Image("file:images/controls/leftG.png");
		this.imgLeftD= new Image("file:images/controls/left.png");
		this.imgRight = new Image("file:images/controls/rightG.png");
		this.imgRightD= new Image("file:images/controls/right.png");
		
		this.imgFire= new Image("file:images/controls/fireR.png");
		this.imgFireD = new Image("file:images/controls/fire.png");
		this.imgFireOff = new Image("file:images/controls/fireY.png");
	}
	
	
	public void setImageCharacteristics(ImageView obView, Image obImage) 
	{
		obView.setImage(obImage);
		obView.setFitHeight(40);
		obView.setFitWidth(40);
	}
	
	private void fireWeapon()
	{
		this.obDrone = new Sprite(new Image("file:images/drones/droneSmallActive.png"), 250,750);
		obDrone.render(this.gc);
		
		Thread obThread = new Thread( () -> weaponThreadTask(obDrone));
		obThread.setDaemon(true);
		obThread.start();
	}
	
	private void weaponThreadTask(Sprite obSprite)
	{
		try
		{
			while(true)
			{
				if(obSprite.isDead())
				{
					obSprite.renderNull(this.gc);
					break;
				}
				Platform.runLater( () -> track(obSprite));
				Thread.sleep(20);
			}
		}
		catch(InterruptedException e) { e.printStackTrace(); }
	}
	
	private void gravity(Sprite obSprite)
	{
		obSprite.moveY(1, gc);
		
		if(!obSprite.isDead())
		{
			if(obSprite.getCoors().getX() > SCREEN_WIDTH -30)
			{
				obSprite.moveX(-2, gc);
			}
			if(obSprite.getCoors().getX() < 5)
			{
				obSprite.moveX(2, gc);
			}
		}
	}
	
	private void checkLost()
	{

		try
		{
			while(!gameLost & !gameWon)
			{
				Thread.sleep(500);
				
				for(Sprite obShip : obFleet)
				{
					if(!obShip.isDead()) 
					{				
						if(obCity.getBoundary().intersects(obShip.getBoundary()))
						{
							gameLost = true;
							Platform.runLater(() -> this.obPrimeStage.setScene(new Scene(getScoreboard(), 300,300)));
							break;
						}
					}
				}	//End For		
			}  //End While
		}
		catch(InterruptedException e) {}
	}
	
	private void checkWon()
	{
		try
		{
			while(!gameLost && !gameWon)
			{
				Thread.sleep(1000);
				boolean bDone = true;
				
				for(Sprite obShip : obFleet)
				{
					if(!obShip.isDead())
					{
						bDone = false;
					}
					else
					{
						if(obShip.getOpacity() > 0.9)
						{
							new Thread(() -> bustGhost(obShip)).start();
						}
					}
				} //End For
				
				if(bDone)
				{
					gameWon = true;
					Platform.runLater(() -> getAlertWon());
					break;
				}
			}
		}
		catch(InterruptedException e) {}	
	}
	
	
	//Get rid of occasional bugged ghost ships
	private void bustGhost(Sprite obShip)
	{
		try
		{
			Thread.sleep(2500);
			if(obShip.getOpacity() > .9)
			{
				Thread.sleep(2000);
				if(obShip.getOpacity() > .9)
				{	
					for(int i=100; i>0; i--)
					{
						obShip.setOpacity(i/100.0);
						Platform.runLater(() -> gravity(obShip));
						Thread.sleep(10);
					}
					obShip.renderNull(this.gc);
				}
			}
		}
		catch(InterruptedException e) {}
		Thread.currentThread().interrupt();
	}
	
	
	private void getAlertWon()
	{
		ButtonType btnContinue = new ButtonType("Yes, Sir!", ButtonBar.ButtonData.OK_DONE);
		ButtonType btnQuit = new ButtonType("No, I give up..", ButtonBar.ButtonData.CANCEL_CLOSE);
		ImageView imgDrone = new ImageView(new Image("file:images/SGC75.png"));
		
		Alert alrtWin = new Alert(AlertType.CONFIRMATION, "Placeholder text.", btnContinue, btnQuit);
		alrtWin.setTitle("Attack Thwarted!");
		String sHeader = String.format("You have thwarted the attack! (Wave %d)", nWave);
		alrtWin.setHeaderText(sHeader);
		alrtWin.setGraphic(imgDrone);
		alrtWin.setContentText("It looks like there is another wave incoming..\n"
								+ "Are you ready to continue defending the planet?");
		
		Optional<ButtonType> continueChoice = alrtWin.showAndWait();
		
		if(continueChoice.get() == btnContinue)
		{
			nWave++;
			launchWave();
		}
		if(continueChoice.get() == btnQuit)
		{
			this.obPrimeStage.setScene(new Scene(getScoreboard(), 300,300));
		}
	}
	
	
	private void track(Sprite obSprite)
	{
		//Prevent weapon from continuing past top of screen
		if(obSprite.getCoors().getY() <= -5)
		{
			obSprite.kill();
			Thread.currentThread().interrupt();
		}
		
		obSprite.moveY(-WEAPON_SPEED, this.gc);
		
		for(Sprite obShip : obFleet)
		{
			if(!obShip.isDead()) 
			{				
				obSprite.intersects(obShip);
			}
		}		
	}
	
	private void moveLeft(ImageView obImage)
	{
		obImage.setImage(this.imgLeftD);
		Platform.runLater( () -> moveWeapon(-WEAPON_STEER_SPEED));
	}
	
	private void moveRight(ImageView obImage)
	{
		obImage.setImage(this.imgRightD);
		Platform.runLater( () -> moveWeapon(WEAPON_STEER_SPEED));
	}
	
	private void moveWeapon(int nInc)
	{
		if(!obDrone.isDead())
		{
			obDrone.moveX(nInc, this.gc);
		}
	}
	

	
	private void startTask(Sprite obSprite)
	{
		Thread obThread = new Thread( () -> runTask(obSprite));
		obThread.setDaemon(true);
		obThread.start();
	}
	
	private void runTask(Sprite obSprite)
	{
		try
		{
			int nSleepTime = (int) (Math.random() * 5000 * (nWave * 0.55));
			Thread.sleep(nSleepTime);
			
			int nDrift = 0;
			int nPos = 0;
			
			while(true)
			{
				if(obSprite.isDead())
				{
					obSprite.setImage(new Image("file:images/boom.png"));
					Platform.runLater(() -> gravity(obSprite));
					for(int i=100; i>0; i--)
					{
						obSprite.setOpacity(i/100.0);
						Platform.runLater(() -> gravity(obSprite));
						Thread.sleep(10);
					}
					obSprite.renderNull(this.gc);
					break;
				}

				//If Alive...
				
				//Check for overlap, reduce if any.
				for(Sprite obOtherShip : obFleet)
				{
					if(obSprite.getBoundary().intersects(obOtherShip.getBoundary()) &&
						(!obOtherShip.isDead()) && 
							(obSprite.getCoors().getY() < obOtherShip.getCoors().getY()))
					{
						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e) {}							
					}
				}
				
				//Get Drift amount
				if(nDrift == 0)
				{
					nDrift = getDrift();
				}
				
				//Do Drift
				//This method works better, because instead of constantly jittering, and possibly drifting
				//	There is a chance of getting a significant drift (1/1000, 200x/Second) then doing it all
				if(!obSprite.isDead())
				{
					if(nDrift < 0 && obSprite.getCoors().getX() > 5)
					{
						obSprite.moveX(-1, gc);
						nDrift++;
					}
					if(nDrift > 0 && obSprite.getCoors().getX() < SCREEN_WIDTH -30)
					{
						obSprite.moveX(1, gc);
						nDrift--;
					}					
				}
				
				//Usual movement stuff
				obSprite.setImage(obShips.get(nPos % obShips.size()));
				nPos++;
				Platform.runLater(() -> gravity(obSprite));
				Thread.sleep(50);
			} //End While
		}
		catch(InterruptedException e) {}
	}
	
	private int getDrift()
	{
		int nDrift = 0;
		
		if((Math.random()*1000) <= 1)
		{
			nDrift = (int) (Math.random() * 800) - 400;
		}
		return nDrift;
	}
	
	private GridPane getScoreboard()
	{
		updateScores();
		
		GridPane obPane = new GridPane();
		obPane.setAlignment(Pos.CENTER);
		
		String sWinner = String.format("You %s\n ", gameWon? "Won!" : "Lost.");
		obPane.add(new Label(sWinner), 0, 0);
		
		String sScore = String.format("Your Score: %d\n ", playerScore.getScore());
		obPane.add(new Label(sScore), 0, 1);
		
		obPane.add(new Label("Top Scores:\n "), 0, 2);
		
		//I had a shorter way of doing this, but this looks better.
		obPane.add(new Label("PLAYER"), 0, 3);
		obPane.add(new Label("WAVE"), 1, 3);
		
		obPane.add(new Label(aScores.get(0).getName()), 0, 4);
		obPane.add(new Label(String.format("%d", aScores.get(0).getScore())), 1, 4);
		
		obPane.add(new Label(aScores.get(1).getName()), 0, 5);
		obPane.add(new Label(String.format("%d", aScores.get(1).getScore())), 1, 5);
		
		obPane.add(new Label(aScores.get(2).getName()), 0, 6);
		obPane.add(new Label(String.format("%d", aScores.get(2).getScore())), 1, 6);
		
		obPane.add(new Label(" "), 0, 7);

		Button cmdQuit = new Button("Quit");
		obPane.add(cmdQuit, 0, 8);
		
		cmdQuit.setOnAction(e -> {
			System.exit(0);
		});
		
		return obPane;
	}
	
	
	private void updateScores()
	{
		loadScores();
		
		if(gameLost)
		{
			nWave--;
		}
		
		playerScore = new Score(sPlayer, nWave);
		aScores.add(playerScore);  
		
		Collections.sort(aScores);
		
		if(aScores.size() > 3)
		{
			aScores.remove(3);			
		}
		saveScores();
	}
	
	
	private void loadScores()
	{
		File obSaveFile = new File("scores.csv");
		Scanner obIn = null;
		
		if(obSaveFile.exists())
		{
			try
			{
				obIn = new Scanner(obSaveFile);
				
				while(obIn.hasNextLine()) 
				{
					String[] sVals = RafUtils.parseCSVLine(obIn.nextLine());
					Score obScore = new Score(sVals[0], Integer.parseInt(sVals[1]));
					this.aScores.add(obScore);
				}
			}
			catch(FileNotFoundException e) {}
			finally
			{
				obIn.close();
			}
		}		
	}
	
	private void saveScores()
	{
		boolean bFirstLine = true;
		File obSaveFile = new File("scores.csv");
		PrintWriter obWriter = null;
		
		obSaveFile.delete();
		
		try
		{
			obWriter = new PrintWriter(obSaveFile);
			
			for(Score obScore : aScores)
			{
				if(bFirstLine)
				{
					bFirstLine = false;
				}
				else
				{
					obWriter.println();					
				}
				obScore.writeToCSVFile(obWriter);
			}
		}
		catch(FileNotFoundException e) {e.printStackTrace();}
		finally
		{
			obWriter.close();
		}
	}
	

	public static void main(String[] args)
	{
		Application.launch(args);
	}

}
