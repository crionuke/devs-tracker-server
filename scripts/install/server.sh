#!/bin/bash

APP_PORT=10000
GATEWAY_01_IP="<gateway_01_ip>"

# Create user
sudo addgroup docker
sudo adduser --shell /bin/bash --disabled-password --gecos "" devstracker
sudo adduser devstracker docker
# Setup ssh
mkdir /home/devstracker/.ssh
sudo chown devstracker:devstracker /home/devstracker/.ssh
chmod 700 /home/devstracker/.ssh
cp /root/.ssh/authorized_keys /home/devstracker/.ssh/
sudo chown devstracker:devstracker /home/devstracker/.ssh/authorized_keys
# Install docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io
systemctl start docker
systemctl enable docker
# Setup firewall
ufw allow in on eth1 from ${GATEWAY_01_IP} to any port ${APP_PORT}
ufw status verbose