#!/usr/bin/expect -f
spawn ssh pi@10.64.98.137
expect "assword:"
send "raspberry\r"
expect "Last login"
send "./py_chat\r"
interact
