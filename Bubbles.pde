class Bubble{
 
  float x;
  float y;
  float xspeed;
  float yspeed;
  
  int size;
  boolean isDead;
  color clr;
  
  //constructor for bubbles that move across the screen i.e in x only
  Bubble(boolean fromLeft){
    x = fromLeft ? -225 : width + 225;
    y = random (0, height);
    
    xspeed =  fromLeft ? random(2) : random(-2);
    yspeed = random(-0.5, 0.25);
    
    size = int(random(40, 90));
    isDead = false;
    clr = colors[int(random(colors.length))];
  }
  
  //constructor for bubbles that move in both x and y
  Bubble(float tx, float ty){
    x = tx;
    y = ty;
    yspeed = random(1, 3);
    xspeed = random(-2, 3);
    
    size = int(random(40, 90));
    isDead = false;
    clr = colors[int(random(colors.length))];
  }
  
  
  void display(){
    // draw main body of the bubble
    fill(clr);
    noStroke();
    ellipseMode(CENTER);
    ellipse(x, y, size, size);
    
    // draw the highlight
    fill(#61B4CF);
    ellipseMode(CORNER);
    ellipse(x + 5, y - 5, size/4, size/5);
    
    ellipseMode(CENTER);
  }
  
  // move bubbles up and down the screen
  void fall(){
    y += yspeed;
    x += xspeed;
    
    if (y > height - size/2){
      y = height - size/2;  // stay at the bottom
      
      // bounce off the bottom
      xspeed *= -1; 
      yspeed *= -1;
    }
    
    // kill offscreen items
    if (y < -200)
      isDead = true;
      
    if (x < -150 || x > width + 150)
      isDead = true;
  }
    
  // move across the screen
  void drift(){
    x += xspeed;
    y += yspeed;
    
    //kill offscreen bubbles
    if (x < -250 || x > width + 250)
      isDead = true;
  }
  
  // collide with bullets
  void collide(ArrayList<Bullet> bullets){
    
    for (int i = 0; i < bullets.size(); i++){
       Bullet b = bullets.get(i);
       float distance = dist(b.loc.x, b.loc.y, x, y); 
       
       if (distance < size / 2){
         //play sound effect
         popEffect.rewind();
         popEffect.play();
         
         size += 30;
         b.isDead = true;      // kill the bullet
         this.isDead = true;   // kill the bubble
         score++; // incerement score
       }
     }     
  }
  
  // collide with inflator bullets
  void inflate(ArrayList<Bullet> inflators){
    
    for (int i = 0; i < inflators.size(); i++){
      Bullet bull = inflators.get(i);
      float distance = dist(bull.loc.x, bull.loc.y, x, y);
      
      if (distance < size/2 && bull.hits == 0){
        bull.hits++;
        bull.isDead = true;  // kill the bullet
        this.size += 10;     // inflate the bubble
      }
    }
    
    if (size > 150){
      // play sound effect
      popEffect.rewind();
      popEffect.play();
      
      size = 180;  // pop animation, i.e. instant increase in size before destruction
      isDead = true;
      score++; // increment score
    }
  }
  
  // collide with other bubbles
  void collideBubbles(Bubble bub){
    float distance = dist(x, y, bub.x, bub.y);
    
    if (distance <= (size/2 + bub.size/2) ){
      //change direction
      xspeed *= -1;
      yspeed *= -1;
      
    }
  }
}

//*************************************************************

class BubbleManager{
  
  ArrayList<Bullet> buls = bulletManager.bullets;
  
  ArrayList<Bubble> bubbles;
  ArrayList<Bubble> inflatables;
  
  int max = 10;
  int delay = 10;  // frames till generation of next bubble
  int waited = 0;  // frames since the last bubble generation
  boolean inflatable;
  
  
  BubbleManager(boolean inflate){
    inflatable = inflate;
    
    // create infalatble bubbles
    if (inflate){
      inflatables = new ArrayList<Bubble>();
    }
    // create standard bubbles
    else{
      bubbles = new ArrayList<Bubble>();
    }
  }
  
  void create(){
    // create inflatable bubbles
    if (inflatable){
      if (inflatables.size() < max){
        
        if (waited % delay == 0){
          float direction = random(-1, 1);
          inflatables.add(new Bubble(direction < 0));  // chose random direction for each
          delay = int(random(60, 100)); // reset the delay
        }
      }
      
    }
    // create standard bubbles
    else{
       if (bubbles.size() < max){
         
         if (waited % delay == 0){
           bubbles.add(new Bubble (random(75, width - 75), -150));
           delay = int(random(60, 150));  // reset the delay
         }
       }
    }
  }
  
  void run(){
    // run inflatable bubbles
    if (inflatable){
      for (int i = 0; i < inflatables.size(); i++){
        Bubble b = inflatables.get(i);
        b.drift();
        b.inflate(buls);
        b.display();
        
        for(Bubble bbl : inflatables){
          if (int(bbl.x) != int(b.x) || int(bbl.y) != int(b.y))
            b.collideBubbles(bbl);
        }
        
        if (b.isDead)
          inflatables.remove(i);
      }
    }
    // run standard bubbles
    else{
      for (int i = 0; i < bubbles.size(); i++){
        Bubble b = bubbles.get(i);
        b.fall();
        b.collide(buls);
        b.display();
        
        for(Bubble bbl : bubbles){
          if (int(bbl.x) != int(b.x) || int(bbl.y) != int(b.y))
            b.collideBubbles(bbl);
        }
        
        if (b.isDead)
          bubbles.remove(i);
      }
    }
    
    create();  // attemp to create a new bubble
    waited++; // increment waited frames
  }
}