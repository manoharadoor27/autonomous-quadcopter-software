#include <Servo.h>

Servo rollServo, pitchServo, throttleServo, yawServo;
int rollPin = 6, pitchPin = 7, throttlePin = 8, yawPin = 9;

int led = 13;

int servoHigh = 130;
int servoLow = 50;

void setup() {
  pinMode(rollPin, OUTPUT);
  pinMode(pitchPin, OUTPUT);
  pinMode(throttlePin, OUTPUT);
  pinMode(yawPin, OUTPUT);

  pinMode(led, OUTPUT);
  
  rollServo.attach(rollPin);
  pitchServo.attach(pitchPin);
  throttleServo.attach(throttlePin);
  yawServo.attach(yawPin);
  
  Serial.begin(9600);
  
  rollServo.write(90);
  pitchServo.write(90);
  throttleServo.write(90);
  yawServo.write(90);
}

void loop(){
  rollServo.write(servoHigh);
  pitchServo.write(servoHigh);
  throttleServo.write(servoHigh);
  yawServo.write(servoHigh);
  delay(2000);
  rollServo.write(servoLow);
  pitchServo.write(servoLow);
  throttleServo.write(servoLow);
  yawServo.write(servoLow);
  delay(2000);
}

