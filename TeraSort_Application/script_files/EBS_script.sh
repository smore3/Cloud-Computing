#!/bin/bash

echo "Mounting EBS....\n"
sudo mkfs -t ext4 /dev/xvdf
sudo mkdir -p /mnt/raid
sudo mount /dev/xvdf /mnt/raid
sudo chown -R ec2-user /mnt
echo "Mounting EBS completed\n"
