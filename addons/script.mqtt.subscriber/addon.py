import xbmcaddon
import xbmcgui
import sys
sys.path.append('/storage/python/usr/lib/python2.7/site-packages')
 
addon = xbmcaddon.Addon()
addonname = addon.getAddonInfo('name') 
mqtt_server = addon.getSetting('mqtt_server') # returns the mqtt_server 
mqtt_server_port = addon.getSetting('mqtt_server_port') # returns the mqtt_server_port 
mqtt_topic_subscribe = addon.getSetting('mqtt_topic_subscribe') # returns the mqtt_topic to which you subscribe
theValue = "error"
 
line1 = "Mosquitto Subscriber subscribes to " + mqtt_server + ":" + mqtt_server_port
line2 = "Topic: " + mqtt_topic_subscribe

import sys
sys.path.append('/storage/python/usr/lib/python2.7/site-packages')

mqtt_server = "192.168.2.128"
mqtt_server_port = 1883
mqtt_topic_subscribe = "a/#"
content = ""

try:
    import paho.mqtt.client as mqtt
except ImportError:
    import paho.mqtt.client as mqtt
def on_connect(mqttc, obj, flags, rc):
    print "Connected to %s:%s" % (mqttc._host, mqttc._port) 
def on_message(mqttc, obj, msg):
    content = "Topic: " + msg.topic + " Value: " + str(msg.payload)
    print(content)
    line3 = str(content)
     
    xbmcgui.Dialog().ok(addonname, line1, line2, line3)
    exit
def on_publish(mqttc, obj, mid):
    print("mid: "+str(mid))
def on_subscribe(mqttc, obj, mid, granted_qos):
    print("Subscribed: "+str(mid)+" "+str(granted_qos))
def on_log(mqttc, obj, level, string):
    print(string) 

mqttc = mqtt.Client()
mqttc.on_message = on_message
mqttc.on_connect = on_connect
mqttc.on_publish = on_publish
mqttc.on_subscribe = on_subscribe
mqttc.connect(mqtt_server, mqtt_server_port, 60)
mqttc.subscribe(mqtt_topic_subscribe, 0)
mqttc.loop_forever()
