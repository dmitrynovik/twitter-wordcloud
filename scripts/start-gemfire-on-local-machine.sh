dir="$HOME/gemfire-store"
locator=Tweets
server1=server1
server1_port=40411
server2=server2
server2_port=40412
# enable_pdx=true

mkdir -p $dir
echo "Data is written to: $dir"
echo "Configuration: { locator = $locator, server.0 = $server1, server.1 = $server2 }"

gfsh start locator --name=$locator --dir="$dir/$locator"
# gfsh connect
# gfsh configure pdx --read-serialized=$enable_pdx
gfsh start server  --name=server1 --server-port=$server1_port --dir="$dir/$server1" --locators="localhost[10334]"
gfsh start server  --name=server2 --server-port=$server2_port --dir="$dir/$server2" --locators="localhost[10334]"