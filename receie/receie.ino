void setup() {
  Serial.begin(9600); /* Define baud rate for serial communication */
  pinMode(2, OUTPUT);
  pinMode(14, OUTPUT);
}

void loop() {
  
    if (Serial.available())  /* If data is available on serial port */
    {
      char data_received; 
      data_received = Serial.read();  /* Data received from bluetooth */
      if (data_received == '1')
      {
        digitalWrite(2, HIGH);
          Serial.write("LED turned ON\n");
        delay(2000);
        digitalWrite(2, LOW);     
      }
      else if (data_received == '2')
      {
        digitalWrite(14, HIGH);
        Serial.write("LED turned OFF\n");
        delay(2000);
        digitalWrite(14, LOW); 
      }
      else
      {
        Serial.write("Select either 1 or 2\n");
      }
    }
}
