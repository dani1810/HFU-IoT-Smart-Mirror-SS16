#!/bin/bash
(/storage/.config/irswitch.sh)&
/storage/hyperion/bin/hyperiond.sh /storage/.config/hyperion.config.json > /dev/null 2>&1 &