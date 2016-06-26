Do not replace your config.txt, just add the following lines after you remounted your filesystem:

mount -o,remount,rw /flash
nano /flash/config.txt

To add:
