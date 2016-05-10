#include <Servo.h>

Servo rollServo, pitchServo, throttleServo, yawServo;
int rollPin = 6, pitchPin = 7, throttlePin = 8, yawPin = 9;

int trigger = 4, echo = 2;

int led = 13;

int i;

void setup() {
  pinMode(trigger, OUTPUT);
  pinMode(echo, INPUT);

  pinMode(rollPin, OUTPUT);
  pinMode(pitchPin, OUTPUT);
  pinMode(throttlePin, OUTPUT);
  pinMode(yawPin, OUTPUT);

  pinMode(led, OUTPUT);

  for(i=0;i<600;i++)
  {
    digitalWrite(throttlePin, HIGH);
    digitalWrite(yawPin, HIGH);
    digitalWrite(rollPin, HIGH);
    digitalWrite(pitchPin, HIGH);
    delayMicroseconds(950);
    digitalWrite(throttlePin, LOW);
    delayMicroseconds(550);
    digitalWrite(rollPin, LOW);
    digitalWrite(pitchPin, LOW);
    delayMicroseconds(480);
    digitalWrite(yawPin, LOW);
    delay(18);
    delayMicroseconds(20);
  }
  
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

unsigned long timeBegin, timeEnd;
unsigned long distanceCount = 0;

int valRoll, valPitch, valThrottle, valYaw;
int pwmRoll, pwmPitch, pwmThrottle, pwmYaw;

char choice;
int triggerAPM = 0;

unsigned long echoDetectBegin = 0, echoDetectEnd = 0, echoSafetyTime = 0;

int pwmMid = 1500, pwmHigh = 1750, pwmLow = 1250;

unsigned int serialBeginTime, serialEndTime, serialDifferenceTime;

unsigned long distanceCountTime;

int distanceCM;

void loop() {

  //ULTRASONICS http://www.micropik.com/PDF/HCSR04.pdf 60ms and 4 metre accuracy

  //Unlike newping library and ultrasonics library which typically generate a maximum range of 50 cm, this code works for a range of over 2.5 metres!
  digitalWrite(trigger, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigger, LOW);

  echoDetectBegin = millis();
  echoSafetyTime = 0;
  
  while( (digitalRead(echo)==LOW) & (echoSafetyTime<60) )
  { 
    timeBegin = millis();
    echoDetectEnd = millis();
    echoSafetyTime = echoDetectEnd - echoDetectBegin;
  }
  
  //Serial.print(echoSafetyTime);
  //Serial.print(" ");
  
  distanceCount = 0;
  
  while(  (digitalRead(echo)==HIGH) & (distanceCount<60) & (distanceCountTime<60) )
  { 
    timeEnd = millis();
    distanceCountTime = timeEnd - timeBegin;
    delay(1);
    distanceCount = distanceCount + 1;
  }
  //ULTRASONICS

  //Serial.println(distanceCount);

  distanceCM = distanceCount*17;
  
  if(Serial.available())
  { 
    choice = Serial.read();
    triggerAPM = 0;
    serialBeginTime = millis();
  }
  else
  {
    serialEndTime = millis();
    serialDifferenceTime = serialEndTime - serialBeginTime;
  }

  if(serialDifferenceTime > 6000) //6 seconds
  { triggerAPM = 1; }

  if(distanceCount<23) //23.5*17 = 400 cm
  { 
    if(choice=='f')//Move forward
    { choice = 's'; }
    if((choice=='l')|(choice=='r'))//Roll
    { choice = 's'; }
  }

  if(triggerAPM==0)  
  {
    switch(choice)
    {
      case 'u': pwmThrottle = pwmHigh; digitalWrite(led, HIGH); break;
      case 'l': pwmRoll = pwmLow; digitalWrite(led, HIGH); break;
      case 'd': pwmThrottle = pwmLow; digitalWrite(led, HIGH); break;
      case 'r': pwmRoll = pwmHigh; digitalWrite(led, HIGH); break;
      case 'f': pwmPitch = pwmHigh; digitalWrite(led, HIGH); break;
      case 'b': pwmPitch = pwmLow; digitalWrite(led, HIGH); break;
      case 'c': pwmYaw = pwmHigh; digitalWrite(led, HIGH); break;
      case 'a': pwmYaw = pwmLow; digitalWrite(led, HIGH); break;
      case 'x': digitalWrite(led, HIGH); break;
      case 's': pwmThrottle = pwmMid, pwmRoll = pwmMid, pwmPitch = pwmMid, pwmYaw = pwmMid;
                digitalWrite(led, LOW); break;    
    }
  
    valRoll = 45 + (pwmRoll - 1000)/11;
    valPitch = 45 + (pwmPitch - 1000)/11;
    valThrottle = 45 + (pwmThrottle - 1000)/11;
    valYaw = 45 + (pwmYaw - 1000)/11;
  
    rollServo.write(valRoll);
    pitchServo.write(valPitch);
    throttleServo.write(valThrottle);
    yawServo.write(valYaw);
    delay(20);//Pulse duration of 50Hz PWM
  }

  if(triggerAPM==1)
  {
    //Disable all signals
    rollServo.write(0);
    pitchServo.write(0);
    throttleServo.write(0);
    yawServo.write(0);
  }
  
}
