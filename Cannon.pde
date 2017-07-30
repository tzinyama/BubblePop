class Cannon{
  float x;
  float y;
  float radius = 90;
  
  // end points of the cannon's barrel
  float barrelX;
  float barrelY;
  
  // target marker
  float targX;
  float targY;
  
  Cannon(float tempX, float tempY){
    x = tempX;
    y = tempY;
    
    // target marker coordinates
    targX = x;
    targY = y - 200;
  }
  
  void display(){
    // update target location
    targX = mouseX;
    targY = mouseY;
    
    // set angle to the position of the mouse
    float angle = atan2(mouseY-y, mouseX - x);
    
    barrelX = (radius * cos(angle)) + x;
    barrelY = (radius * sin(angle)) + y; // calculate end position of the barrel
    
    // draw base of cannon and barrel
    ellipseMode(CENTER);
    noStroke();
    fill(#0ACF00);
    ellipse(x, y, 80, 80);
    noFill();
    stroke(#0ACF00);
    strokeWeight(20);
    line(x, y, barrelX, barrelY);
    
    // draw superstructure of cannon and barrel
    fill(#006D4C);
    noStroke();
    ellipse(x, y, 70, 70);
    noFill();
    stroke(#006D4C);
    strokeWeight(10);
    line(x, y, barrelX, barrelY);
    
    // draw the target marker
    noFill();
    strokeWeight(2);
    stroke(255, 0, 0);
    ellipseMode(CENTER);
    ellipse(targX, targY, 20, 20);
    line(targX - 30, targY, targX + 30, targY);
    line(targX, targY - 30, targX, targY + 30);
  }
  
  void shoot(){
    // play effects
    gunEffect.rewind();
    gunEffect.play();
    
    bulletManager.addBullet();
  }
  
  //********************************************
  // LEGACY CODE
  // Keyboard control scheme was used during project exhibition
  
  // KEYBOARD CONTROL SCHEME. Move the crosshairs
  void target(){
    // used with keyboard control scheme only
    
    if (keyPressed){ 
      if (key == CODED){
        
        if(keyCode == LEFT){
          targX -= 30;
          if (targX < x - 200) targX = x - 200;
        }
        else if (keyCode == RIGHT){
          targX += 30;
          if (targX > x + 200) targX = x + 200;
        }
        
        if (keyCode == UP){
          targY -= 30;
          if (targY < y - 200) targY = y - 200;
        }
        else if (keyCode == DOWN){
          targY += 30;
          if (targY > y + 200) targY = y + 200;
        }
      }
    }
  }
  
  // end LEGACY CODE
  //********************************************
}