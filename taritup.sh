#/bin/sh
readonly tarball=firewall_punch.tar.gz
tar -cvzf "$tarball" *.jar *.bat *.sh src doc
chmod 664 "$tarball"
