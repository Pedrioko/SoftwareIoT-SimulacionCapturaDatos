import serial
import random
from time import sleep

port = "COM4"
ser = serial.Serial(port, 9600)
while True: 
	num=random.randint(20,50)
	ser.write(str(num)+"\n")
	print "x es",num
	sleep(2)
ser.close()
