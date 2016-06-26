import xbmcaddon
import xbmcgui
import sys
sys.path.append('/storage/.kodi/addons/python.RPi.GPIO/lib')
import RPi.GPIO as GPIO
import time
import subprocess
  
addon = xbmcaddon.Addon()
addonname = addon.getAddonInfo('name')
icon = addon.getAddonInfo('icon')

GPIO.setmode(GPIO.BOARD)

max = 82
pulseCounts = [0] * max

GPIO.setup(18, GPIO.OUT)
GPIO.output(18, True)
time.sleep(.5)

GPIO.output(18, False)
time.sleep(.02)

GPIO.setup(18, GPIO.IN)

for i in xrange(0,25):
	i=i

count = 0
while GPIO.input(18):
	if(count > 32000):
		print("Timeout-Error")
		exit
	else:
		count = count +1

for i in xrange(0,max,2):
	while(not(GPIO.input(18))):
		if(pulseCounts[i] > 32000):
			print("Timeout-Error")
			exit
		else:
			pulseCounts[i] = pulseCounts[i] +1

	while(GPIO.input(18)):
		if(pulseCounts[i+1] > 32000):
			printf("Timeout-Error")
		else:
			pulseCounts[i+1] = pulseCounts[i+1] +1

treshold=0
for i in xrange(2,max,2):
	treshold = treshold + pulseCounts[i]

treshold=treshold/40

data=[0]*5
for i in xrange(3,max,2):
	index = (i-3)/16
	data[index] = data[index] << 1
	if(pulseCounts[i] >= treshold):
		data[index] = data[index] | 1

if(data[4] == ((data[0] + data[1] + data[2] + data[3]) & 0xFF)):
	humidity = (data[0] * 256 + data[1]) / 10.0
	temp = (((data[2] & 0x7F) * 256 + data[3])/10.0)

	if(data[2] & 0x80):
		temp = temp * -1.0

	exit
else:
	print("Checksum failed")
	exit

line1 = "Temperatur: " + str(temp) + " C Luftfeuchtigkeit: " + str(humidity) + " %"
time = 5000 #in miliseconds
 
# xbmcgui.Dialog().ok(addonname, line1, line2, line3)
xbmc.executebuiltin('Notification(%s, %s, %d, %s)'%(addonname, line1, time, icon))
