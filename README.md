This files are for the "smart mirror university project" at the furtwangen university of applied science, computer science faculty

## Requirements & Preinstallations ##
Please make sure, that you use a Raspberry Pi2 Model B+ or Raspberry Pi3
Also please make sure, that you use the remotePi Board instead of any other IR-Receiver
These files are only for the XBox One Media Remote. Please correct them, if you want to use any other remote control

To use this files, you need to install OpenElec 6.0 on your Raspberry Pi: http://openelec.tv/get-openelec
Also please install and use the Nebula-Skin

Your Raspberry should also have hyperion installed and ssh should be enabled:
curl -L --output install_hyperion.sh --get https://raw.githubusercontent.com/tvdzwan/hyperion/master/bin/install_hyperion.sh
sh ./install_hyperion.sh

Please make sure, that you use OpenWeatherMap as WeatherForecast and install the xbmcmail-Addon
If you want to use the DHT22-Sensor, you should also install the GPIO-Addon
If you want to use the Mosquitto Subscriber or Broker, you should also install the Python Version of Mosquitto into the path "/storage/python" using the following commands:

wget https://pypi.python.org/packages/83/96/dacc2b78bc9c5cd83eed178e9ce35d7bceecf2dd38db079c0190423efd4a/paho-mqtt-1.1.tar.gz
tar -xvf paho-mqtt-1.1.tar.gz
python setup.py install --root=/storage/python

If you want to use our aweseome pc_config_tool, you should also enable ftp through an optional addon

## PATHS ##
Than copy the files to this directories
addons: /storage/.kodi/addons
config: /storage/.config/
userdata: /storage/.kodi/userdata/
flash: DON´T Copy it, you may loose your Decoder Licence. Please read the README file in the subfolder flash

## Be Careful ##
These Folders aren´t used at your Raspberry Pi
dht22_script_no_addon: whereever you want
pc_config_tool: Thats not for your Raspberry Pi. Make a local Version at your PC and you can test it!