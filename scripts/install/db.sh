#!/bin/bash

DB_PASSWORD="<password>"
SERVER_01_IP="<server_01_ip>"
SERVER_02_IP="<server_02_ip>"

PRIVATE_INET=$(ip route | grep eth1 | awk '{print $1}')
PRIVATE_IP=$(ip route | grep eth1 | awk '{print $9}')

# Install software
apt update && apt install -y postgresql postgresql-contrib
pg_ctlcluster 12 main stop
if [ -d "/mnt/$(ls /mnt)/postgresql" ]; then
    # Reuse data from volume
    rm -r /var/lib/postgresql/12/main
    ln -s /mnt/$(ls /mnt)/postgresql/12/main /var/lib/postgresql/12/main
else
    # Move data to volume
    mkdir -p /mnt/$(ls /mnt)/postgresql/12
    mv /var/lib/postgresql/12/main /mnt/$(ls /mnt)/postgresql/12/main
    ln -s /mnt/$(ls /mnt)/postgresql/12/main /var/lib/postgresql/12/main
fi
# Configure postgres
cp /etc/postgresql/12/main/postgresql.conf /etc/postgresql/12/main/postgresql.conf.backup
sed -i "s/^#listen_addresses.*$/listen_addresses = '${PRIVATE_IP}'/" /etc/postgresql/12/main/postgresql.conf
echo -e "host\tdevstracker\tdevstracker\t${PRIVATE_INET}\t\tmd5" >> /etc/postgresql/12/main/pg_hba.conf
pg_ctlcluster 12 main start
# Setup firewall
ufw allow in on eth1 from ${SERVER_01_IP} to any port 5432
ufw allow in on eth1 from ${SERVER_02_IP} to any port 5432
ufw status verbose
# Create database
sudo -u postgres psql -c "ALTER DATABASE template1 is_template = false;"
sudo -u postgres psql -c "DROP DATABASE template1;"
sudo -u postgres psql -c "CREATE DATABASE template1 WITH OWNER = postgres ENCODING = 'UTF8' TABLESPACE = pg_default LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8' CONNECTION LIMIT = -1 TEMPLATE template0;"
sudo -u postgres psql -c "ALTER database template1 is_template = true;"
sudo -u postgres psql -c "CREATE DATABASE devstracker WITH OWNER = postgres;"
sudo -u postgres psql -c "GRANT CONNECT ON DATABASE devstracker TO postgres;"
sudo -u postgres psql -c "GRANT ALL ON DATABASE devstracker TO postgres;"
sudo -u postgres psql -c "CREATE USER devstracker WITH INHERIT NOSUPERUSER NOCREATEDB NOCREATEROLE PASSWORD '${DB_PASSWORD}';"