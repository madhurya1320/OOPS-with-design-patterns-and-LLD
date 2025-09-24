abstract class Shape {
  abstract double calculateArea();
  
}

class Circle extends Shape {
 private
  double radius;
  @Override 
public double calculateArea() {
    return Math.PI * radius * radius;
  }
}

class Rectangle extends Shape {
 private
  double width;
 private
  double height;
  @Override 
public double calculateArea() {
    return width * height;
  }
}


class Triangle extends Shape {
 private
  double base;
 private
  double height;
  @Override 
public double calculateArea() {
    return 0.5 * base * height;
  }
}