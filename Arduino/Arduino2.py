import serial
import random
from time import sleep

port = "COM7"
ser = serial.Serial(port, 9600)
while True: 
	num = random.randint(20,50)
	num2 = random.randint(30,99)
	ser.write(str(num)+":"+str(num2)+"\n")
	print "x es",num
	sleep(2)
ser.close()
