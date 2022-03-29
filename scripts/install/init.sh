#!/bin/bash

SSHD_PORT=1612

sed -i "s/#Port 22/Port ${SSHD_PORT}/" /etc/ssh/sshd_config
service ssh restart
ufw default deny incoming
ufw default allow outgoing
ufw limit ${SSHD_PORT}
ufw --force enable
ufw status verbose