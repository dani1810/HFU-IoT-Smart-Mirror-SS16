#!/bin/bash
if [ "$1" != "halt" ]; then
  killall hyperiond
fi
if [ "$1" != "reboot" ]; then
  GPIOpin=15
  GPIOpin1=14
  echo "$GPIOpin" > /sys/class/gpio/export
  # execute shutdown sequence on pin
  echo "out" > /sys/class/gpio/gpio$GPIOpin/direction
  echo "1" > /sys/class/gpio/gpio$GPIOpin/value
  usleep 125000
  echo "0" > /sys/class/gpio/gpio$GPIOpin/value
  usleep 200000
  echo "1" > /sys/class/gpio/gpio$GPIOpin/value
  usleep 400000
  echo "0" > /sys/class/gpio/gpio$GPIOpin/value
  # set GPIO 14 high to feedback shutdown to RemotePi Board
  # because the irswitch.sh has already been terminated
  echo "$GPIOpin1" > /sys/class/gpio/export
  echo "out" > /sys/class/gpio/gpio$GPIOpin1/direction
  echo "1" > /sys/class/gpio/gpio$GPIOpin1/value
  usleep 4000000
fi
