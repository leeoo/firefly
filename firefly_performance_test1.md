
# 测试环境 #
  * MAC OS X 10.8.2
  * JDK参数：-Xmx1024m -Xms1024m -Xss1m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:PermSize=64m -XX:MaxPermSize=64m -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops -XX:+DisableExplicitGC
  * JDK版本: 1.6.0\_29
  * CPU: Intel Core i5 2.3GHz
  * 内存：8G
  * 测试工具：Apache ab

# 简单动态页面测试 #
测试参数：10W请求，100并发。<br />
```
 ./ab -k -n100000 -c100 $URL
```
| **框架** | **吞吐量** | **错误率** |
|:-----------|:--------------|:--------------|
| firefly-2.0\_03 | 31975.61/秒 | 0% |
| spring\_3.0.5 + tomcat\_6.0.26 | 12748.44/秒 | 0% |

## 测试报告详情 ##
### firefly-2.0\_03 ###
```
This is ApacheBench, Version 2.3 <$Revision: 655654 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)


Server Software:        firefly-server/1.0
Server Hostname:        127.0.0.1
Server Port:            6655

Document Path:          /index
Document Length:        2706 bytes

Concurrency Level:      100
Time taken for tests:   3.127 seconds
Complete requests:      100000
Failed requests:        0
Write errors:           0
Keep-Alive requests:    100000
Total transferred:      287520125 bytes
HTML transferred:       270618942 bytes
Requests per second:    31975.61 [#/sec] (mean)
Time per request:       3.127 [ms] (mean)
Time per request:       0.031 [ms] (mean, across all concurrent requests)
Transfer rate:          89781.55 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0       8
Processing:     0    3   4.4      2      43
Waiting:        0    3   4.4      2      43
Total:          0    3   4.4      2      43

Percentage of the requests served within a certain time (ms)
  50%      2
  66%      3
  75%      4
  80%      5
  90%      6
  95%      7
  98%     25
  99%     28
 100%     43 (longest request)

```

### spring\_3.0.5 + tomcat\_6.0.26 ###
```
Server Software:        Apache-Coyote/1.1
Server Hostname:        127.0.0.1
Server Port:            7777

Document Path:          /mvc-basic/account/index2
Document Length:        2756 bytes

Concurrency Level:      100
Time taken for tests:   7.844 seconds
Complete requests:      100000
Failed requests:        0
Write errors:           0
Keep-Alive requests:    100000
Total transferred:      302439058 bytes
HTML transferred:       275726776 bytes
Requests per second:    12748.44 [#/sec] (mean)
Time per request:       7.844 [ms] (mean)
Time per request:       0.078 [ms] (mean, across all concurrent requests)
Transfer rate:          37652.59 [Kbytes/sec] received
```

# 简单静态页面测试 #
测试参数：10W请求，100并发。<br />
```
 ./ab -k -n100000 -c100 $URL
```
| **框架** | **吞吐量** | **错误率** |
|:-----------|:--------------|:--------------|
| firefly-2.0\_03 | 16432.96/秒 | 0% |

## 测试报告详情 ##
### firefly-2.0\_03 ###
```
This is ApacheBench, Version 2.3 <$Revision: 655654 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)


Server Software:        firefly-server/1.0
Server Hostname:        127.0.0.1
Server Port:            6655

Document Path:          /index.html
Document Length:        90 bytes

Concurrency Level:      100
Time taken for tests:   6.085 seconds
Complete requests:      100000
Failed requests:        0
Write errors:           0
Keep-Alive requests:    100000
Total transferred:      25700514 bytes
HTML transferred:       9000180 bytes
Requests per second:    16432.96 [#/sec] (mean)
Time per request:       6.085 [ms] (mean)
Time per request:       0.061 [ms] (mean, across all concurrent requests)
Transfer rate:          4124.37 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.1      0       5
Processing:     0    6   4.9      4      46
Waiting:        0    6   4.8      4      46
Total:          0    6   4.9      4      46

Percentage of the requests served within a certain time (ms)
  50%      4
  66%      6
  75%      8
  80%      9
  90%     12
  95%     16
  98%     20
  99%     25
 100%     46 (longest request)

```