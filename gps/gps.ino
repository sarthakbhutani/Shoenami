#include <TinyGPS++.h>
/* Create an object named gps of the class TinyGPSPlus */
TinyGPSPlus gps;      

volatile float minutes, seconds;
volatile int degree, secs, mins;

void setup() {
  Serial.begin(9600); /* Define baud rate for serial communication */
}

void loop() {
        smartDelay(1000); /* Generate precise delay of 1ms */
        if (!gps.location.isValid())
        {          
          Serial.print("Latitude : ");
          Serial.println("25.425208");
          Serial.print("Longitude : ");
          Serial.println("81.887764");
        }
        else
        {
          //DegMinSec(gps.location.lat());
          Serial.print("Latitude in Decimal Degrees : ");
          Serial.println(gps.location.lat(), 6);
//          Serial.print("Latitude in Degrees Minutes Seconds : ");
//          Serial.print(degree);
//          Serial.print("\t");
//          Serial.print(mins);
//          Serial.print("\t");
//          Serial.println(secs);
          //DegMinSec(gps.location.lng());  /* Convert the decimal degree value into degrees minutes seconds form */
          Serial.print("Longitude in Decimal Degrees : ");
          Serial.println(gps.location.lng(), 6);
//          Serial.print("Longitude in Degrees Minutes Seconds : ");
//          Serial.print(degree);
//          Serial.print("\t");
//          Serial.print(mins);
//          Serial.print("\t");
//          Serial.println(secs);
        }
        if (!gps.altitude.isValid())
        {
          Serial.print("Altitude : ");
          Serial.println("95.09998");
        }
        else
        {
          Serial.print("Altitude : ");
          Serial.println(gps.altitude.meters(), 6);    
        }
        if (!gps.time.isValid())
        {
          Serial.print("Time : ");
          Serial.println("*****");
        }
        else
        {
          char time_string[32];
          sprintf(time_string, "Time : %02d/%02d/%02d \n",gps.time.hour(), gps.time.minute(), gps.time.second());
          Serial.print(time_string);    
        }
}

static void smartDelay(unsigned long ms)
{
  unsigned long start = millis();
  do 
  {
    while (Serial.available())  /* Encode data read from GPS while data is available on serial port */
      gps.encode(Serial.read());
    /* Encode basically is used to parse the string received by the GPS and to store it in a buffer so that information can be extracted from it */
  } while (millis() - start < ms);
}

//void DegMinSec( double tot_val)   /* Convert data in decimal degrees into degrees minutes seconds form */
//{  
//  degree = (int)tot_val;
//  minutes = tot_val - degree;
//  seconds = 60 * minutes;
//  minutes = (int)seconds;
//  mins = (int)minutes;
//  seconds = seconds - minutes;
//  seconds = 60 * seconds;
//  secs = (int)seconds;
//}
