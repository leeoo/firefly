home=/usr/local/app/alvinqiu/perf
classpath=$home/classes`find $home/lib -iname *.jar | xargs -n 1 -i echo -n :{}`
mainclass=test.FileTransferTcpServer
jdk=/usr/local/jdk/bin/java
stderr=$home/log/stderr.log
stdout=$home/log/stdout.log
command="nohup $jdk -cp $classpath $mainclass $*"
echo 'started...'
echo $command
$command >> $stdout 2>> $stderr &
