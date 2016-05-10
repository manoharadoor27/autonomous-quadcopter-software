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
  throttleServo.write(servoLow);
  yawServo.write(servoHigh);
  delay(6000);
}

int i;

void loop(){

  rollServo.write(0);
  pitchServo.write(0);
  throttleServo.write(0);
  yawServo.write(0);
  delay(200);
  
}

