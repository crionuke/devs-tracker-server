#!/bin/bash
apt update -y
# nginx
apt install -y nginx
mkdir -p /var/www/devstracker.crionuke.com/html
chmod -R 755 /var/www/devstracker.crionuke.com
cat > /etc/nginx/sites-available/devstracker.crionuke.com << EOL
upstream devstracker1 {
    server 10.110.0.4:10000;
}
server {
    listen 80;
    listen [::]:80;
    index index.html index.htm index.nginx-debian.html;
    server_name devstracker.crionuke.com www.devstracker.crionuke.com;
    location /.well-known {
        root /var/www/html;
    }
    location /devstracker/v1 {
        proxy_pass http://devstracker1;
    }
}
EOL
# Test
mkdir -p /var/www/html/.well-known/acme-challenge
echo Success > /var/www/html/.well-known/acme-challenge/example.html
ln -s /etc/nginx/sites-available/devstracker.crionuke.com /etc/nginx/sites-enabled/
service nginx restart
# Setup firewall
ufw allow 80
ufw allow 443
ufw status verbose
# certbot
apt install -y certbot python3-certbot-nginx
certbot --nginx -d devstracker.crionuke.com -d www.devstracker.crionuke.com
