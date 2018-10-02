
# Configuration
IP="54.77.152.118"

#Test request
for (( ; ; ))
do
    curl "http://$IP/r.html?f=test01.txt&sc=100&sr=100&wc=100&wr=100&coff=0&roff=0" &
    sleep 2
    curl "http://$IP/r.html?f=test01.txt&sc=100&sr=100&wc=50&wr=50&coff=50&roff=50" &
    sleep 1
    curl "http://$IP/r.html?f=test01.txt&sc=200&sr=200&wc=200&wr=200&coff=0&roff=0" &
    sleep 5
    curl "http://$IP/r.html?f=test01.txt&sc=250&sr=100&wc=250&wr=50&coff=0&roff=50" &
    sleep 3
    curl "http://$IP/r.html?f=test01.txt&sc=500&sr=500&wc=250&wr=250&coff=0&roff=0" &
    sleep 4
    curl "http://$IP/r.html?f=test01.txt&sc=500&sr=500&wc=300&wr=300&coff=100&roff=100" &
    sleep 7
    curl "http://$IP/r.html?f=test01.txt&sc=500&sr=500&wc=500&wr=500&coff=0&roff=0" &
    sleep 8
    curl "http://$IP/r.html?f=test01.txt&sc=1000&sr=1000&wc=750&wr=750&coff=0&roff=0" &
    sleep 10
done
