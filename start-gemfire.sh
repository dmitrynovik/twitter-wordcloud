dir="$HOME/gemfire-store"
locator=Tweets
server1=server1
server2=server2
# enable_pdx=true

mkdir -p $dir
echo "Data is written to: $dir"
echo "Configuration: { locator = $locator, server.0 = $server1, server.1 = $server2 }"

gfsh start locator --name=$locator --dir="$dir/$locator"
# gfsh connect
# gfsh configure pdx --read-serialized=$enable_pdx
gfsh start server  --name=server1 --server-port=40411 --dir="$dir/$server1" --locators="localhost[10334]"
gfsh start server  --name=server2 --server-port=40412 --dir="$dir/$server2" --locators="localhost[10334]"