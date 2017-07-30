class Bullet{
  Cannon cannon = player;
  
  PVector loc = new PVector();
  float angle;
  int age = 0;
  
  PVector center;
  PVector turrent;
  
  boolean isDead;
  
  // to fix bug where bubbles continually inflates
  int hits = 0;
  
  Bullet(float x, float y){
    loc.x = x;
    loc.y = y;
    center = new PVector(cannon.x, cannon.y);
    turrent  =  new PVector(cannon.cX, cannon.cY);
  }
  
  void run(){
    move();
    display();
  }
  
  private void display(){
    noStroke();
    fill(#FF8D40);
    ellipse(loc.x, loc.y, 10, 10);
  }
  
  private void move(){
    PVector vel = PVector.sub(turrent, center);  // get direction of motion
    loc.add(new PVector(vel.x/8, vel.y/8));
    age++;
  }
  
  // Check if the bullet is still useful
  boolean isDead(){
    return age > 120;
  }
}

//*************************************************************

class BulletManager{
  
  ArrayList<Bullet> bullets;
  PVector loc;
  
  BulletManager(Cannon can){
    bullets = new ArrayList<Bullet>();
    loc = new PVector(can.x, can.y);
  }
  
  void addBullet(){ 
    bullets.add(new Bullet(loc.x, loc.y));
  }
  
  void run(){
    for (int i = 0; i < bullets.size(); i++){
      Bullet b = bullets.get(i);
      b.run();
      
      if (b.isDead()){
        bullets.remove(i);
      }
    }
  }
}