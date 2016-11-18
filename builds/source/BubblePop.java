import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 
import ddf.minim.signals.*; 
import ddf.minim.spi.*; 
import ddf.minim.ugens.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BubblePop extends PApplet {

/*
Bubble Pop
By Tino Zinyama
*/

//audio library import







//game state enum
enum GameState{
  PREGAME,
  SHOOTER,
  INFLATOR,
  PAUSED,
  MODESELECT,
  GAMEOVER
}

//audio variables
Minim soundManager;
AudioPlayer backgroundSong;
AudioPlayer gunEffect;
AudioPlayer popEffect;

//time varibles
float time;
float timeDelta;
static final float MAXTIME = 30.0f;

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
int[] colors = {0xff3AAACF, 0xff6E84D6, 0xff35C0CD, 0xff5EC4CD, 0xff4284D3, 0xff6899D3};

//game variables
//int gameState;  //0 = pre game, 1 = shooter, 2 = inflator, 3 = paused, 4 = game Over, 5 = level select
GameState gameState;
boolean isShooter; //flag to determine what mode game was in before being paused


public void setup(){
    //full screen hd
  
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
  
  backParticles1 = new ParticleManager(0.1f * width, -100, 10);
  backParticles2 = new ParticleManager(0.3f * width, -100, 10);
  backParticles3 = new ParticleManager(0.5f * width, -100, 10);
  backParticles4 = new ParticleManager(0.7f * width, -100, 10);
  backParticles5 = new ParticleManager(0.9f * width, -100, 10);
  
  gameState = GameState.PREGAME;
  score = 0;
  highScore = 0;
  
  time = 0;
  timeDelta = 0.01666f;  // approx time for each frame
  
  setupLevels();
}

//*************************************************************
//Draw
public void draw(){
  background(0);
  gameManager();
}

//*************************************************************
//Manage gameplay
public void gameManager(){
  //Pre Game
  if (gameState == GameState.PREGAME){
    introParticles1.run();
    introParticles2.run();
    introParticles3.run();
    
    noStroke();
    ellipseMode(CENTER);
    fill(0xff0B61A4);
    ellipse(width/2, 400, 600, 300);
    fill(0xff3F92D2);
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
  else if (gameState == GameState.MODESELECT){
    introParticles1.run();
    introParticles2.run();
    introParticles3.run();
    
    noStroke();
    ellipseMode(CENTER);
    fill(0xff0B61A4);
    ellipse(width/2, 400, 600, 300);
    fill(0xff3F92D2);
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
  else if (gameState == GameState.SHOOTER || gameState == GameState.INFLATOR){
    //calculate time
    time += timeDelta;
    
    if (time > MAXTIME + 0.25f)
      gameState = GameState.GAMEOVER; //game over
  
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
  else if (gameState == GameState.PAUSED){
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
    fill(0xff0B61A4);
    ellipse(width/2, 400, 600, 300);
    fill(0xff3F92D2);
    ellipse(width/2, 400, 570, 270);
    
    fill(255);
    textAlign(CENTER, CENTER);
    textSize(40);
    text("Game Paused", width/2, 350);
    textSize(25);
    text("Press ENTER To Continue", width/2, 420);
  }
  //Game Over
  else if (gameState == GameState.GAMEOVER){
    backParticles1.run();
    backParticles2.run();
    backParticles3.run();
    backParticles4.run();
    backParticles5.run();
    
    introParticles2.run();
    introParticles3.run();
    
    noStroke();
    ellipseMode(CENTER);
    fill(0xff0B61A4);
    ellipse(width/2, 400, 600, 300);
    fill(0xff3F92D2);
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
public void setupLevels(){
  
  switch(gameState){
    
    case PREGAME:  //INTRO SCREEN
      bubbleManager = new BubbleManager(false);
      break;
   
    case SHOOTER:  //SHOOTER
      isShooter = true;
      bubbleManager = new BubbleManager(false);
      highScore = highScoreShooter;  //set high score to game mode high score
      break;
      
    case INFLATOR: //INFLATOR
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
public void scoreManager(){
  //score background
  noStroke();
  fill(0xff0B61A4);
  ellipseMode(CENTER);
  ellipse(0, 0, 300, 150);
  fill(0xff3F92D2);
  ellipse(0, 0, 280, 130);
  
  //determine which high score to display
  switch(gameState){
    case SHOOTER: //Bubble shooter mode
      if (score > highScoreShooter){
        highScoreShooter = score;
        highScore = highScoreShooter;
      }
      break;
    case INFLATOR: //Bubble Inflator Mode
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
  fill(0xff0B61A4);
  ellipseMode(CENTER);
  ellipse(width, 0, 300, 150);
  fill(0xff3F92D2);
  ellipse(width, 0, 280, 130);
  
  //time text
  fill(255);
  textAlign(CENTER, CENTER);
  textSize(20);
  text("Time: " + PApplet.parseInt(MAXTIME - time), width - 50, 25);
  
}

//*************************************************************
// Manage Mouse Presses
public void mouseReleased(){
  if(gameState == GameState.SHOOTER || gameState == GameState.INFLATOR){
     player.shoot(); 
  }
}

//*************************************************************
// Manage Key Presses
public void keyPressed(){
  
  //Use mouse buttons instead
  //player.target();
  /*if ((gameState == 1 || gameState == 2) && (key == TAB || key == 'w')){ //w for controller
    player.shoot();
  }*/
  
  if (key == ENTER || key ==  'g'){  //g for controller
    switch(gameState){
      case PREGAME:
        //start shooter mode
        gameState = GameState.MODESELECT;  //game mode select screen
        break;
      case SHOOTER:
        //pause the game
        gameState = GameState.PAUSED;
        break;
      case INFLATOR:
        //pause the game
        gameState = GameState.PAUSED;
        break;
      case PAUSED:
        //continue paused game
        gameState = (isShooter ? GameState.SHOOTER : GameState.INFLATOR);  //return game to correct statew
        break;
      case GAMEOVER:
        //restart the game
        backgroundSong.close();
        setup();
      default:
        break;
    }
  }
  
  if (gameState == GameState.MODESELECT && (key == '1' || key == 'w')){ //w for controller
    //start bubble shooter mode
    gameState = GameState.SHOOTER;
    setupLevels();
  }
  else if (gameState == GameState.MODESELECT && (key == '2' || key == 'd')){ //d for controller
    //start bubble inflator mode
    gameState = GameState.INFLATOR;
    setupLevels();
  }
}

//*************************************************************
//Close the sound channels
public void stop(){
  backgroundSong.close();
  gunEffect.close();
  popEffect.close();
  soundManager.stop();
  super.stop();
}
/*
Background Song
Night Rave by Audionautix is licensed under a Creative Commons Attribution license (https://creativecommons.org/licenses/by/4.0/)
Artist: http://audionautix.com/

"Cannon" by Jashaszun, licensed under Creative Commons Attribution-Share Alike 3.0 and GNU GPL license.
Work: http://openprocessing.org/visuals/?visualID= 4660  
License: 
http://creativecommons.org/licenses/by-sa/3.0/
http://creativecommons.org/licenses/GPL/2.0/

*/
//*************************************************************
//Bubble Class
class Bubble{
 
  float x;
  float y;
  float xspeed;
  float yspeed;
  
  int size;
  boolean isDead;
  int clr;
  
  //constructor for bubbles that move across the screen i.e in x only
  Bubble(boolean fromLeft){
    x = fromLeft ? -225 : width + 225;
    y = random (0, height);
    
    xspeed =  fromLeft ? random(2) : random(-2);
    yspeed = random(-0.5f, 0.25f);
    
    size = PApplet.parseInt(random(40, 90));
    isDead = false;
    clr = colors[PApplet.parseInt(random(colors.length))];
  }
  
  //constructor for bubbles that move in both x and y
  Bubble(float tx, float ty){
    x = tx;
    y = ty;
    yspeed = random(1, 3);
    xspeed = random(-2, 3);
    
    size = PApplet.parseInt(random(40, 90));
    isDead = false;
    clr = colors[PApplet.parseInt(random(colors.length))];
  }
  
  
  public void display(){
    //Draw main body of the bubble
    fill(clr);
    noStroke();
    ellipseMode(CENTER);
    ellipse(x, y, size, size);
    
    //darw the highlight
    fill(0xff61B4CF);
    ellipseMode(CORNER);
    ellipse(x + 5, y - 5, size/4, size/5);
    
    ellipseMode(CENTER);
  }
  
  
  //move bubbles up and down the screen
  public void fall(){
    y += yspeed;
    x += xspeed;
    
    if (y > height - size/2){
      y = height - size/2;  //stay at the bottom
      
      //bounce off the bottom
      xspeed *= -1; 
      yspeed *= -1;
    }
    
    //kill offscreen items
    if (y < -200)
      isDead = true;
      
    if (x < -150 || x > width + 150)
      isDead = true;  //is out of screen hence dead
  }
    
  //move across the screen
  public void drift(){
    x += xspeed;
    y += yspeed;
    
    //kill offscreen bubbles
    if (x < -250 || x > width + 250)
      isDead = true;
  }
  
  
  //collide with bullets
  public void collide(ArrayList<Bullet> bullets){
    
    for (int i = 0; i < bullets.size(); i++){
       Bullet b = bullets.get(i);
       float distance = dist(b.loc.x, b.loc.y, x, y); 
       
       if (distance < size / 2){
         //play sound effect
         popEffect.rewind();
         popEffect.play();
         
         size += 30;
         b.isDead = true;  //kill the bullet
         this.isDead = true;  //kill the bubble
         score++; //incerement score
       }
     }     
  }
  
  //collide with inflator bullets
  public void inflate(ArrayList<Bullet> inflators){
    
    for (int i = 0; i < inflators.size(); i++){
      Bullet bull = inflators.get(i);
      float distance = dist(bull.loc.x, bull.loc.y, x, y);
      
      if (distance < size/2 && bull.hits == 0){
        bull.hits++;
        bull.isDead = true;  //kill the bullet
        this.size += 10; // inflate the bubble
      }
    }
    
    if (size > 150){
      //play sound effect
      popEffect.rewind();
      popEffect.play();
      
      size = 180;  //pop animation, ine instant increase in size before destruction
      isDead = true;
      score++; //increment score
    }
  }
  
  //collide with other bubbles
  public void collideBubbles(Bubble bub){
    float distance = dist(x, y, bub.x, bub.y);
    
    if (distance <= (size/2 + bub.size/2) ){
      //change direction
      xspeed *= -1;
      yspeed *= -1;
      
    }
  }
}

//*************************************************************
//Bubble Manager Class
//For Bubbles that move up and down
class BubbleManager{
  
  ArrayList<Bullet> buls = bulletManager.bullets;
  
  ArrayList<Bubble> bubbles;
  ArrayList<Bubble> inflatables;
  
  int max = 10;
  int delay = 10;  //frames till generation of next bubble
  int waited = 0;  //frames since the last bubble generation
  boolean inflatable;
  
  
  BubbleManager(boolean inflate){
    inflatable = inflate;
    
    //create infalatble bubbles
    if (inflate){
      inflatables = new ArrayList<Bubble>();
    }
    //create standard bubbles
    else{
      bubbles = new ArrayList<Bubble>();
    }
  }
  
  
  public void create(){
    //create inflatable bubbles
    if (inflatable){
      if (inflatables.size() < max){
        
        if (waited % delay == 0){
          float direction = random(-1, 1);
          inflatables.add(new Bubble(direction < 0));  //chose random direction for each
          delay = PApplet.parseInt(random(60, 100)); //reset the delay
        }
      }
      
    }
    //create standard bubbles
    else{
       if (bubbles.size() < max){
         
         if (waited % delay == 0){
           bubbles.add(new Bubble (random(75, width - 75), -150));
           delay = PApplet.parseInt(random(60, 150));  //reset the delay
         }
       }
    }
  }
  
  public void run(){
    //run inflatable bubbles
    if (inflatable){
      for (int i = 0; i < inflatables.size(); i++){
        Bubble b = inflatables.get(i);
        b.drift();
        b.inflate(buls);
        b.display();
        
        for(Bubble bbl : inflatables){
          if (PApplet.parseInt(bbl.x) != PApplet.parseInt(b.x) || PApplet.parseInt(bbl.y) != PApplet.parseInt(b.y))
            b.collideBubbles(bbl);
        }
        
        if (b.isDead)
          inflatables.remove(i);
      }
    }
    //run standard bubbles
    else{
      for (int i = 0; i < bubbles.size(); i++){
        Bubble b = bubbles.get(i);
        b.fall();
        b.collide(buls);
        b.display();
        
        for(Bubble bbl : bubbles){
          if (PApplet.parseInt(bbl.x) != PApplet.parseInt(b.x) || PApplet.parseInt(bbl.y) != PApplet.parseInt(b.y))
            b.collideBubbles(bbl);
        }
        
        if (b.isDead)
          bubbles.remove(i);
      }
    }
    
    create();  //attemp to create a new bubble
    waited++; //increment waited frames
  }
}
//*************************************************************
//Bullets Class
class Bullet{
  
  Cannon cannon = player;
  PVector loc = new PVector();
  float angle;
  int age = 0;
  PVector center;
  PVector turrent;
  boolean isDead;
  
  //to fix bug where bubbles continually inflates
  int hits = 0;
  
  Bullet(float x, float y){
    loc.x = x;
    loc.y = y;
    center = new PVector(cannon.x, cannon.y);
    turrent  =  new PVector(cannon.cX, cannon.cY);
  }
  
  public void run(){
    move();
    display();
  }
  
  private void display(){
    noStroke();
    fill(0xffFF8D40);
    ellipse(loc.x, loc.y, 10, 10);
  }
  
  private void move(){
    PVector vel = PVector.sub(turrent, center);  //get direction of motion
    loc.add(new PVector(vel.x/8, vel.y/8));
    age++;
  }
  
  //Check if the bullet is still useful
  public boolean isDead(){
    return age > 120;
  }
}


//*************************************************************
//Bullets Manager Class
class BulletManager{
  
  ArrayList<Bullet> bullets;
  PVector loc;
  
  BulletManager(Cannon can){
    bullets = new ArrayList<Bullet>();
    loc = new PVector(can.x, can.y);
  }
  
  public void addBullet(){ 
    bullets.add(new Bullet(loc.x, loc.y));
  }
  
  public void run(){
    for (int i = 0; i < bullets.size(); i++){
      Bullet b = bullets.get(i);
      b.run();
      
      if (b.isDead()){
        bullets.remove(i);
      }
    }
  }
}
class Cannon{
  //Inspired by "Cannon" by Jashaszun. See Attributions
  float x;
  float y;
  float r = 90;  //radius
  
  //End points of the cannon's barrel
  float cX;
  float cY;
  
  //set the target marker
  float targX;
  float targY;
  
  Cannon(float tempX, float tempY){
    //Cannon coordinates
    x = tempX;
    y = tempY;
    
    //Target Marker coordinates
    targX = x;
    targY = y - 200;
  }
  
  public void display(){
    //update crosshair location
    targX = mouseX;
    targY = mouseY;
    
    //Set angle to the position of the mouse
    float angle = atan2(mouseY-y, mouseX - x);
    
    //Set angle to position of crosshairs. Used with keyboard controls
    //float angle = atan2(targY - y, targX - x);
    
    cX = (r * cos(angle)) + x;
    cY = (r * sin(angle)) + y; //Calculate end position of the barrel
    
    //Draw base of cannon and barrel
    ellipseMode(CENTER);
    noStroke();
    fill(0xff0ACF00);
    ellipse(x, y, 80, 80);
    noFill();
    stroke(0xff0ACF00);
    strokeWeight(20);
    line(x, y, cX, cY);
    
    //Draw superstructure of cannon and barrel
    fill(0xff006D4C);
    noStroke();
    ellipse(x, y, 70, 70);
    noFill();
    stroke(0xff006D4C);
    strokeWeight(10);
    line(x, y, cX, cY);
    
    //Draw the target marker
    noFill();
    strokeWeight(2);
    stroke(255, 0, 0);
    ellipseMode(CENTER);
    ellipse(targX, targY, 20, 20);
    line(targX - 30, targY, targX + 30, targY);
    line(targX, targY - 30, targX, targY + 30);
   
  }
  
  //KEYBOARD CONTROL SCHEME. Move the crosshairs
  public void target(){
    //Used with keyboard control scheme only
    
    if (keyPressed){ 
      if (key == CODED){
        
        //Move target along the x-axis
        if(keyCode == LEFT){
          targX -= 30;
          //set boundary
          if (targX < x - 200) targX = x - 200;
        }
        else if (keyCode == RIGHT){
          targX += 30;
          if (targX > x + 200) targX = x + 200;
        }
        
        //Move Target Along the y-axis
        if (keyCode == UP){
          targY -= 30;
          //set boundary
          if (targY < y - 200) targY = y - 200;
        }
        else if (keyCode == DOWN){
          targY += 30;
          //set boundary
          if (targY > y + 200) targY = y + 200;
        }
      }
    }
  }
  
  //shoot a bullet
  public void shoot(){
    gunEffect.rewind(); //set it at the beginning of the sound
    gunEffect.play();
    bulletManager.addBullet();
  }
  
}
//*************************************************************
//Particles Class
class Particle{
  
  float x;
  float y;
  float xvel;
  float yvel;
  float acceleration;
  float lifespan;
  int clr;
  int size;
  
  Particle(float tx, float ty, int tsize){
    x = tx;
    y = ty;
    acceleration = 0.1f;
    xvel = random(-2, 2);
    yvel = random(-2, 0);
    
    clr = colors[PApplet.parseInt(random(colors.length))];
    size = tsize;
    lifespan = 255.0f;
  }
  
  public void run(){
    update();
    display();
  }
  
  //Update location;
  public void update(){
    yvel += acceleration;  //gradually gain speed downwards
    
    //move
    x += xvel;
    y += yvel;
    lifespan -= 1.0f;
  }
  
  //Method to display
  public void display(){
    noStroke();
    fill(clr, lifespan);  //gradually become transparent
    ellipse(x, y, size, size);
  }
  
  //Is the particle still useful?
  public boolean isDead(){
    if (lifespan < 0.0f){
      return true;
    }else{
      return false;
    }
  }
}

//*************************************************************
//Particles Manager Class
class ParticleManager{
  
  ArrayList<Particle> particles;
  PVector origin;
  int size;
  
  ParticleManager(float x, float y, int tsize){
    origin = new PVector(x, y);
    size = tsize;
    particles = new ArrayList<Particle>();
  }
  
  public void addParticle(){
    particles.add(new Particle(origin.x, origin.y, size));
  }
  
  public void run(){
    addParticle();
    
    for (int i = particles.size() - 1; i >= 0; i--){
      Particle p = particles.get(i);
      p.run();
      if(p.isDead()){
        particles.remove(i);
      }
    }
  }
}
  public void settings() {  size (1280, 720);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BubblePop" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
