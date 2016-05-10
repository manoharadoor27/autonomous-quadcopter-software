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

int pwm = 1950;

void loop(){
  if(Serial.available())
  { pwm = Serial.parseInt();  }

  if(pwm==1950)
  {
    rollServo.write(90);
    pitchServo.write(90);
    throttleServo.write(servoHigh);
    yawServo.write(90);
    delay(200);
  }

  if(pwm==1050)
  {
    rollServo.write(90);
    pitchServo.write(90);
    throttleServo.write(servoLow);
    yawServo.write(90);
    delay(200);
  }
  Serial.println(pwm);
}

