#!/bin/bash -e

DIR=$1

read -s -p "Password: " SECRET && echo
echo $SECRET

find $DIR -name "*.crypt" | while read k; do
echo "decrypt: $k"
echo $SECRET | gpg --batch --yes --passphrase-fd 0 --output "$(echo "$k" | sed "s/\.crypt$//g")" --decrypt "$k"
done
