home=/usr/local/app/alvinqiu/perf
classpath=$home/classes`find $home/lib -iname *.jar | xargs -n 1 -i echo -n :{}`
mainclass=test.StringLineTcpServer
jdk=/usr/local/jdk/bin/java
jvm_params="-Dbind_host=10.147.22.162 -Dbind_port=9900 -Xms1024m -Xmx1024m -Xss1m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:+UseCompressedOops -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:PermSize=64m -XX:MaxPermSize=64m"
stderr=$home/log/stderr.log
stdout=$home/log/stdout.log
command="nohup $jdk $jvm_params -cp $classpath $mainclass $*"
echo 'started...'
echo $command
$command >> $stdout 2>> $stderr &
