#!/bin/bash
# prevent restarting XBMC at shutdown. This is only used for OpenElec before V5
LOCKDIR="/var/lock/"
LOCKFILE="xbmc.disabled"
# this is the GPIO pin receiving the shut-down signal
GPIOpin1=14
# functions
add_omit_pids() {
omit_pids="$omit_pids -o $1" 
}
safe_shutdown () {
# for OpenElec before V5
touch "$LOCKDIR/$LOCKFILE"
# for OpenElec V5 and later
systemctl stop kodi
add_omit_pids $(pidof connmand)
add_omit_pids $(pidof dbus-daemon)
killall5 -15 $omit_pids
for seq in `seq 1 10` ; do
usleep 500000
clear > /dev/tty1
killall5 -18 $omit_pids || break
done
sync
umount -a >/dev/null 2>&1
poweroff -f
}

echo "$GPIOpin1" > /sys/class/gpio/export
echo "in" > /sys/class/gpio/gpio$GPIOpin1/direction
while true; do
  sleep 1
  power=$(cat /sys/class/gpio/gpio$GPIOpin1/value)
  if [ $power != 0 ]; then
    echo "out" > /sys/class/gpio/gpio$GPIOpin1/direction
    echo "1" > /sys/class/gpio/gpio$GPIOpin1/value
    sleep 3
    safe_shutdown
  fi
done