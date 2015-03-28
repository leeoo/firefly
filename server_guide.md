
# 特性 #
  * 基于nio的高性能嵌入式应用服务器
  * 使用简单，与firefly IOC、MVC框架无缝融合
  * 轻量化，启动时间平均1秒左右

# Getting Started #
```
firefly-system=INFO,/data/logs
firefly-access=INFO,/data/logs
```
  * 在classpath下添加firefly-log.properties文件，firefly-access打印的是服务器访问日志。

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://code.google.com/p/firefly/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://code.google.com/p/firefly/beans http://firefly.googlecode.com/files/beans.xsd">

	<component-scan base-package="com.firefly.benchmark"/>
	<mvc view-path="/template" view-encoding="UTF-8"/>

<!-- <bean class="com.firefly.server.http.Config">
		<property name="serverHome" value="/usr/app/myApp"></property>
		<property name="host" value="localhost"></property>
		<property name="port" value="7676"></property>
		<property name="keepAlive" value="false"></property>
	</bean> -->

</beans>
```
  * 在classpath下添加firefly.xml文件，里面配置了模版引擎路径、字符集、以及annotation包扫描路径。
  * 还可以通过添加com.firefly.server.http.Config的bean来配置HTTP Server，启动的时候调用ServerBootstrap.start("firefly.xml")启动服务器。

```
public static void main(String[] args) throws Throwable {
	String serverHome = new File(new File(Bootstrap.class.getResource("/")
				.toURI()).getParent(), "/page").getAbsolutePath();
	ServerBootstrap.start(serverHome, "localhost", 6655);
}
```
  * 编写main函数，启动firefly应用服务器，serverHome是模版页面以及其他静态资源存放的根目录。

# 参数详细说明 #
  * firefly应用服务器绝大多数情况下只需要提供: serverHome, host, port 这三个参数运行即可，另外也提供了更详细的性能参数使用ServerBootstrap.start(Config config)来启动。
| **参数** | **描述** |
|:-----------|:-----------|
| configFileName | firefly MVC、IOC框架配置文件名，默认值为firefly.xml |
| encoding | 字符编码，默认与firefly.xml中配置的字符编码一致 |
| maxRequestLineLength | http request line 最大长度，默认8k |
| maxRequestHeadLength | http 请求头最大长度，默认16k |
| maxRangeNum | http multipart range 请求最大数量，默认为8 |
| writeBufferSize | 服务器write缓冲区大小，默认8k |
| maxSessionInactiveInterval | http session发呆时间，默认为10分钟 |
| maxUploadLength | post请求最大长度，默认50MB |
| keepAlive | 是否启用http长连接，默认true，最终服务器是否工作在长连接模式，还取决于Connection请求头以及http协议的版本 |
| sessionIdName | sessionId的名称，默认jsessionid |
| httpSessionManager | http session管理器接口，默认为本地session管理 |
| httpSessionAttributeListener | http session属性监听，当操作session属性时触发 |
| httpSessionListener | http session监听，当session创建和销毁时触发 |
| httpConnectionListener | http 连接监听，当建立和关闭http连接时触发 |
| fileAccessFilter | 静态文件访问过滤器，可以拦截服务器访问静态资源并做编程处理 |
| pipeline | 启用HTTP管线化请求，默认关闭 |
| maxConnectionTimeout | 连接超时，默认5秒 |
| maxConnections | 最大连接数，默认2000 |
| errorPage | 自定义错误页面 |
| secure | 启用TLS/SSL加密 |
| credentialPath | 服务端证书路径 |
| keystorePassword | 密钥仓库密码 |
| keyPassword | 密钥 |