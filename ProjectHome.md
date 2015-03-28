# 特性 #
  * firefly是一个高性能web开发框架，包含Template engine，IOC、MVC framework，HTTP Server，Common tools等模块，提供了web开发的主要技术栈。
  * 使用简单，精简、轻量、无侵入。
  * Non-blocking I/O，TLS/SSL支持。
  * 原生RESTful支持


# 最近更新 #
firefly-3.0\_01发布
  1. 修复json tools静态公共域序列化问题
  1. 修复request getParameter方法角标越界错误
  1. 修复模版渲染发生异常时没有响应500错误码
  1. 修复json parser读取空数组时类型转换错误
  1. 修复firefly运行在JavaEE环境classloader问题
firefly-3.0发布
  1. 增加HTTPS，TLS/SSL支持
  1. Servlet3.0 API 支持
  1. HTTP Server管道流优化
  1. TimeWheel增加Future返回，用于取消任务
  1. Json Parser增加BigDecimal, BigInteger类型支持
  1. 修复服务端错误没有响应500的bug
  1. 修复模版语言函数调用字符串参数bug
  1. 修复模版语言注释之后的字符截断bug
  1. 修复HTTP pipeline解码器bug
  1. 修复模版注释被消除的问题
  1. 修复HTTP Server输出流未设置字符集问题
  1. 修复Json工具特殊字符逃逸问题
firefly-2.0\_08发布
  1. 修复模版语言for变量冲突的bug
  1. 修复模版语言map嵌套导航的bug
  1. 修复模版语言set引号为过滤的bug
  1. 修复模版语言自定义函数调用bool变量参数bug
  1. 重写反射api，提高反射性能，json解析和模版语言整体性能提升20%
  1. 模版新增自增变量Function，用于循环中获取自增变量
firefly-2.0\_07发布
  1. 增加自定义系统错误页面功能，详情可参看[firefly文档](https://code.google.com/p/firefly/wiki/guide#自定义错误页面)中的自定义错误页面章节
  1. 模版压缩影响模版中js单行注释问题修复
firefly-2.0\_06发布
  1. 修复demo启动时因不同系统造成的问题
  1. 修正Json序列化，造型阶段时读取父类public get方法的私有字段打印异常信息
  1. POST请求时ContentType判断bug修正
  1. 模版语言编译生成文件大小优化
firefly-2.0\_05发布
  1. 修复HTTP服务器内部错误返回404的bug
  1. 修复HTTP服务器字节错乱的bug
firefly-2.0\_04发布
  1. netTool session.write方法参数修改
  1. Http Server增加最大连接数限制参数
  1. Http Server增加TCP连接超时参数
  1. 判断业务在response对象主动设置Connection: close头时主动关闭连接
firefly-2.0\_03发布
  1. ConcurrentLRUHashMap增加参数和listener泛型支持
  1. 修复任意文件访问漏洞
  1. 修正资源访问模糊匹配bug
  1. nettool修改时间轮为静态成员
  1. HTTP Server新增pipeline参数，默认关闭
firefly-2.0\_02发布
  1. 新增HTTP Server启动接口，可以通过firefly.xml配置HTTP Server的参数，具体参考 [应用服务器文档](http://code.google.com/p/firefly/wiki/server_guide) 。
  1. 解决URL模糊匹配时NPE异常问题
  1. 模版数组导航NPE异常问题
firefly-2.0\_01发布
  1. 解决Controller多个HTTP Method匹配问题
  1. HTTP Server性能优化
  1. access日志修改
firefly-2.0\_rc发布，文档已经更新
  1. 代码架构优化，简化了框架使用
  1. Restful URL风格支持
  1. common增加了Pattern类，性能大约为正则匹配的10倍左右
  1. http server非等幂方法使用独立线程处理
  1. bug修复
firefly-1.2\_02发布
  1. common增加了json parser，性能大约为gson-2.1的4倍
  1. common增加了ConcurrentLRUHashMap
  1. http server处理队列增加过载保护
  1. bug修复

# 主页 #
http://www.fireflysource.com

# 文档 #
| [模板引擎](http://code.google.com/p/firefly/wiki/template_guide) |
|:---------------------------------------------------------------------|
| [MVC、IOC框架](http://code.google.com/p/firefly/wiki/guide) |
| [应用服务器](http://code.google.com/p/firefly/wiki/server_guide) |
| nettool |
| common工具类 |
| [性能测试](http://code.google.com/p/firefly/wiki/firefly_performance_test1) |

# Hello World #
## 启动服务 ##
```
public class Bootstrap {

	public static void main(String[] args) throws Throwable {
		String serverHome = new File(new File(Bootstrap.class.getResource("/")
				.toURI()).getParent(), "/page").getAbsolutePath();
		ServerBootstrap.start(serverHome, "localhost", 6655);
	}

}
```
## Controller ##
```
@Controller
public class IndexController {
	@RequestMapping(value = "/index")
	public View index(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("hello", "hello firefly");
		return new TemplateView("/index.html");
	}
}
```
## 页面模版 ##
```
<!DOCTYPE html>
<html>
<head>
<title>${hello} </title>
</head>
<body>
${hello}
</body>
</html>
```
## 配置文件 ##
  * firefly.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://code.google.com/p/firefly/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://code.google.com/p/firefly/beans http://firefly.googlecode.com/files/beans.xsd">

	<component-scan base-package="com.firefly.benchmark"/>
	<mvc view-path="/template" view-encoding="UTF-8"/>

</beans>
```
  * firefly-log.properties
```
firefly-system=INFO,/Users/username/app/logs
firefly-access=INFO,/Users/username/app/logs
```