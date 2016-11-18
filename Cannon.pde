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
  
  void display(){
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
    fill(#0ACF00);
    ellipse(x, y, 80, 80);
    noFill();
    stroke(#0ACF00);
    strokeWeight(20);
    line(x, y, cX, cY);
    
    //Draw superstructure of cannon and barrel
    fill(#006D4C);
    noStroke();
    ellipse(x, y, 70, 70);
    noFill();
    stroke(#006D4C);
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
  void target(){
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
  void shoot(){
    gunEffect.rewind(); //set it at the beginning of the sound
    gunEffect.play();
    bulletManager.addBullet();
  }
  
}