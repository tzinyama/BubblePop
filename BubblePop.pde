/*
Bubble Pop
By Tino Zinyama
*/

//audio library import
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

//audio variables
Minim soundManager;
AudioPlayer backgroundSong;
AudioPlayer gunEffect;
AudioPlayer popEffect;

//time varibles
float time;
float timeDelta;
float maxTime = 20.0;

//score variables
int score;
int highScore;
int highScoreShooter = 0;
int highScoreInflator = 0;

//Game object variables
Cannon player;
BulletManager bulletManager;
BubbleManager bubbleManager;
BubbleManager inflatableManager;

//Particla managers for different game stages
ParticleManager introParticles1;
ParticleManager introParticles2;
ParticleManager introParticles3;

ParticleManager backParticles1;
ParticleManager backParticles2;
ParticleManager backParticles3;
ParticleManager backParticles4;
ParticleManager backParticles5;

//color palete
//range of blue colors for bubbles
color[] colors = {#3AAACF, #6E84D6, #35C0CD, #5EC4CD, #4284D3, #6899D3};

//game variables
int gameState;  //0 = pre game, 1 = shooter, 2 = inflator, 3 = paused, 4 = game Over, 5 = level select
boolean isShooter; //flag to determine what mode game was in before being paused


void setup(){
  size (1280, 720);  //full screen hd
  smooth();
  frameRate(60);
  noCursor();
  
  //audio variables
  soundManager = new Minim(this);
  backgroundSong = soundManager.loadFile("nightrave.mp3");
  gunEffect = soundManager.loadFile("shoot.mp3");
  popEffect = soundManager.loadFile("pop.mp3");
  backgroundSong.loop();
  
  //place player on a random location on screen
  player = new Cannon(random(450, 1450), random(300, 600));
  
  bulletManager = new BulletManager(player);
  
  introParticles1 =  new ParticleManager(width/2, 50, 15);
  introParticles2 =  new ParticleManager(width/2 - 300, 400, 12);
  introParticles3 =  new ParticleManager(width/2 + 300, 400, 12);
  
  backParticles1 = new ParticleManager(0.1 * width, -100, 10);
  backParticles2 = new ParticleManager(0.3 * width, -100, 10);
  backParticles3 = new ParticleManager(0.5 * width, -100, 10);
  backParticles4 = new ParticleManager(0.7 * width, -100, 10);
  backParticles5 = new ParticleManager(0.9 * width, -100, 10);
  
  gameState = 0;
  score = 0;
  highScore = 0;
  
  time = 0;
  timeDelta = 0.01666;  // approx time for each frame
  
  setupLevels();
}

//*************************************************************
//Draw
void draw(){
  background(0);
  gameManager();
}

//*************************************************************
//Manage gameplay
void gameManager(){
  //Pre Game
  if (gameState == 0){
    introParticles1.run();
    introParticles2.run();
    introParticles3.run();
    
    noStroke();
    ellipseMode(CENTER);
    fill(#0B61A4);
    ellipse(width/2, 400, 600, 300);
    fill(#3F92D2);
    ellipse(width/2, 400, 570, 270);
    
    fill(255);
    textAlign(CENTER, CENTER);
    textSize(40);
    text("Bubble Wars", width/2, 350);
    textSize(10);
    fill(0);
    text("By Tino Zinyama", width/2 + 75, 375);
    fill(255);
    textSize(25);
    text("Press ENTER to Begin", width/2, 420);
      
    bubbleManager.run();
    
  }
  //Game Mode Select Screen
  else if (gameState == 5){
    introParticles1.run();
    introParticles2.run();
    introParticles3.run();
    
    noStroke();
    ellipseMode(CENTER);
    fill(#0B61A4);
    ellipse(width/2, 400, 600, 300);
    fill(#3F92D2);
    ellipse(width/2, 400, 570, 270);
    
    fill(255);
    textAlign(CENTER, CENTER);
    textSize(40);
    text("Bubble Wars", width/2, 350);
    textSize(10);
    text("By Tino Zinyama", width/2 + 75, 375);
    
    //gun select options
    fill(0);
    textSize(25);
    text("Select Game Mode", width/2, 420);
    fill(255);
    textSize(18);
    text("1 : Bubble Shooter", width/2, 460);
    text("2 : Bubble Inflator", width/2, 490);
      
    bubbleManager.run();
    
  }
  //In Game
  else if (gameState == 1 || gameState == 2){
    //calculate time
    time += timeDelta;
    
    if (time > maxTime + 0.25)
      gameState = 4; //game over
  
    backParticles1.run();
    backParticles2.run();
    backParticles3.run();
    backParticles4.run();
    backParticles5.run();
        
    bulletManager.run();
    bubbleManager.run();
    player.display();
    
    scoreManager();
    
  }
  //Game Paused
  else if (gameState == 3){
    backParticles1.run();
    backParticles2.run();
    backParticles3.run();
    backParticles4.run();
    backParticles5.run();
    
    introParticles2.run();
    introParticles3.run();
    
    scoreManager();
    
    noStroke();
    ellipseMode(CENTER);
    fill(#0B61A4);
    ellipse(width/2, 400, 600, 300);
    fill(#3F92D2);
    ellipse(width/2, 400, 570, 270);
    
    fill(255);
    textAlign(CENTER, CENTER);
    textSize(40);
    text("Game Paused", width/2, 350);
    textSize(25);
    text("Press ENTER To Continue", width/2, 420);
  }
  //Game Over
  else if (gameState == 4){
    backParticles1.run();
    backParticles2.run();
    backParticles3.run();
    backParticles4.run();
    backParticles5.run();
    
    introParticles2.run();
    introParticles3.run();
    
    noStroke();
    ellipseMode(CENTER);
    fill(#0B61A4);
    ellipse(width/2, 400, 600, 300);
    fill(#3F92D2);
    ellipse(width/2, 400, 570, 270);
    
    fill(255);
    textAlign(CENTER, CENTER);
    textSize(40);
    //choose which highScore to display
    text( (score >= highScore ? "Congratulations!" : "Game Over"), width/2, 350);
    textSize(25);
    text((score >= highScore ? "New High Score " : "Your Score ") + score, width/2, 410);
    fill(0);
    textSize(20);
    text("Press ENTER To Continue", width/2, 460);
  }
}

//*************************************************************
//Setup the different levels 
void setupLevels(){
  
  switch(gameState){
    
    case 0:  //INTRO SCREEN
      bubbleManager = new BubbleManager(false);
      break;
    
    case 1:  //SHOOTER
      isShooter = true;
      bubbleManager = new BubbleManager(false);
      highScore = highScoreShooter;  //set high score to game mode high score
      break;
      
    case 2: //POPPER
      isShooter = false;
      bubbleManager = new BubbleManager(true);  //create inflatable bubbles
      highScore = highScoreInflator;  //set high score to game mode high score
      break;

    default:
      break;
  }
}

//*************************************************************
// Manage Game Scores
void scoreManager(){
  //score background
  noStroke();
  fill(#0B61A4);
  ellipseMode(CENTER);
  ellipse(0, 0, 300, 150);
  fill(#3F92D2);
  ellipse(0, 0, 280, 130);
  
  //determine which high score to display
  switch(gameState){
    case 1: //Bubble shooter mode
      if (score > highScoreShooter){
        highScoreShooter = score;
        highScore = highScoreShooter;
      }
      break;
    case 2: //Bubble Inflator Mode
      if (score > highScoreInflator){
        highScoreInflator = score;
        highScore = highScoreInflator;
      }
      break;
   default:
     break;
  }
  
  //score text
  fill(255);
  textAlign(CENTER, CENTER);
  textSize(20);
  text("Score: " + score, 50, 15);
  //select which high score to display
  textSize(13);
  text("High Score: " + highScore, 50, 37);
  
  //time background
  noStroke();
  fill(#0B61A4);
  ellipseMode(CENTER);
  ellipse(width, 0, 300, 150);
  fill(#3F92D2);
  ellipse(width, 0, 280, 130);
  
  //time text
  fill(255);
  textAlign(CENTER, CENTER);
  textSize(20);
  text("Time: " + int(maxTime - time), width - 50, 25);
  
}

//*************************************************************
// Manage Mouse Presses
void mouseReleased(){
  if(gameState == 1 || gameState == 2){
     player.shoot(); 
  }
}

//*************************************************************
// Manage Key Presses
void keyPressed(){
  
  player.target();
  
  //Use mouse buttons instead
  /*if ((gameState == 1 || gameState == 2) && (key == TAB || key == 'w')){ //w for controller
    player.shoot();
  }*/
  
  if (key == ENTER || key ==  'g'){  //g for controller
    switch(gameState){
      case 0:
        //start shooter mode
        gameState = 5;  //game select screen
        break;
      case 1:
        //pause the game
        gameState = 3;
        break;
        
      case 2:
        //pause the game
        gameState = 3;
        break;
        
      case 3:
        //continue paused game
        gameState = (isShooter ? 1 : 2);  //return game to correct statew
        break;
      case 4:
        //restart the game
        backgroundSong.close();
        setup();
      default:
        break;
    }
  }
  
  if (gameState == 5 && (key == '1' || key == 'w')){ //w for controller
    //start bubble shooter mode
    gameState = 1;
    setupLevels();
  }
  else if (gameState == 5 && (key == '2' || key == 'd')){ //d for controller
    //start bubble inflator mode
    gameState = 2;
    setupLevels();
  }
}

//*************************************************************
//Close the sound channels
void stop(){
  backgroundSong.close();
  gunEffect.close();
  popEffect.close();
  soundManager.stop();
  super.stop();
}