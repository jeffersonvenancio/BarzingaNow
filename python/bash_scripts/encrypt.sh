#!/bin/bash -e

FILE=$1

if [ "x$FILE" == "x" ]; then
echo "You must provide what FILE to encrypt."
exit 1
fi

read -s -p "Password: " SECRET && echo

echo $SECRET | gpg --batch --yes --passphrase-fd 0 --symmetric --cipher-algo AES256 --output "$FILE.crypt" "$FILE"
