
# 特性 #
  * firefly是一个追求高性能的一站式应用程序框架(含IOC、MVC框架，模板引擎，应用服务器，常用工具类)，性能大幅超越主流的(spring3、struts2) + tomcat架构
  * 精简、轻量、无侵入
  * 使用简单，基于约定优于配置的思想，可完全使用Annotation方式 或 Annotation和XML混合方式 进行配置

# IOC #
[IOC](http://baike.baidu.com/view/1486379.htm)为firefly的核心模块，支持Annotation或XML配置方式，组件生命周期为单例。
## xml声明组件 ##
  1. 把firefly.jar放入classpath下
  1. 在classpath下创建firefly.xml文件
firefly.xml文件例子：
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://code.google.com/p/firefly/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://code.google.com/p/firefly/beans 
http://firefly.googlecode.com/files/beans.xsd">

	<import resource="fruit.xml"/>
	<component-scan base-package="test.mixed"/>

	<bean id="foodRepository" class="test.mixed.impl.FoodRepositoryImpl">
		<property name="food">
			<list>
				<ref bean="apple"/>
				<ref bean="orange"/>
				<ref bean="strawberry"/>
			</list>
		</property>
	</bean>

</beans>
```
firefly.xml配置说明：
  1. ` <import/> ` 节点用来引入其他配置文件，可以声明多个引入多个配置xml文件
  1. ` <component-scan/> ` 节点用来配置Annotation组件声明，表示annotation包扫描路径，可以配置多个component-scan节点，表示对多个包进行扫描。
  1. ` <bean/> ` 节点把 test.mixed.impl.FoodRepositoryImpl 声明成一个组件，id属性是可选的，不填写的时候只能按接口类型获取bean。
  1. ` <property/> ` 节点通过name属性指定javabean中的set方法给组件进行依赖注入。
  1. ` <property/> ` 节点下可选节点有`<value/><ref/><list/><array/><map/>`
    * ` <value/> ` 直接注入字面值，并且会根据其字面值自动判断类型，或者通过type属性声明value的类型
    * ` <ref/> ` 注入其他声明过的组件
    * ` <list/> ` 注入Collection接口的各种实现类，会自动匹配set方法参数类型，或者通过type指定特定类型的Collection实现类
    * ` <array/> ` 注入数组类型，会自动匹配set方法参数类型
    * ` <map/> ` 注入Map接口的各种实现类，可以通过type指定特定的map接口实现类，没有指定type时自动匹配set方法参数的类型
具体的xml配置参数可以参看 [schema定义](http://firefly.googlecode.com/files/beans.xsd)

## annotation声明组件 ##
例子：
```
@Component("foodService")
public class FoodServiceImpl implements FoodService {

	@Inject
	private FoodRepository foodRepository;

	@Override
	public Food getFood(String name) {
		for (Food f : foodRepository.getFood()) {
			if (f.getName().equals(name))
				return f;
		}
		return null;
	}

}
```
@Component("foodService") 其中foodService对应xml声明中的bean id，这是可选项，不填写这一项的时候无法通过id获取bean，但可以按类型自动获取bean。其中FoodRepository组件是先前在firefly.xml中声明的，xml和annotation声明组件是等效的，在同一个上下文中，能互相注入。当这两种声明方式发生冲突时，ApplicationContext启动过程当中抛出BeanDefinitionParsingException异常。

  * 方法参数注入
例子：
```
@Component("methodInject")
public class MethodInjectImpl implements MethodInject {
	private AddService addService;

	@Inject
	public void init(AddService addService) {
		this.addService = addService;
	}
}
```
方法参数只支持按类型自动注入

  * 属性注入
例子：
```
@Component("fieldInject")
public class FieldInjectImpl implements FieldInject {

	@Inject
	private AddService addService;
	@Inject("addService")
	private AddService addService2;

	......
}
```
属性支持按照属性类型自动注入或者按组件id的方式注入

## 创建IOC容器 ##
例子：
```
	public static ApplicationContext applicationContext = new XmlApplicationContext(
			"firefly.xml");

	@Test
	public void testInject() {
		FoodService foodService = applicationContext.getBean("foodService");
		Food food = foodService.getFood("apple");
		log.debug(food.getName());
		Assert.assertThat(food.getPrice(), is(5.3));
		
		foodService = applicationContext.getBean(FoodService.class);
		food = foodService.getFood("strawberry");
		log.debug(food.getName());
		Assert.assertThat(food.getPrice(), is(10.00));
	}
```
支持根据Bean Id获取bean，或者通过接口的类型自动获取bean：
  1. applicationContext.getBean("foodService");使用id获取bean实例对象
  1. applicationContext.getBean(FoodService.class);根据类型自动找到实例对象

# Web框架 #
使用[MVC](http://baike.baidu.com/view/31.htm)模式，依赖firefly的IOC模块，使用Annotation配置方式
## 前端控制器 ##
  * firefly在Java EE环境下使用，需要添加web.xml文件。在web.xml里面添加前端控制器，默认读取的配置文件为firefly.xml，也可以通过init-param自定义配置文件名。
  * 如果使用firefly web server则不需要添加web.xml，firefly内嵌的web server已经和mvc框架无缝集成，详情请参看[应用服务器文档](http://code.google.com/p/firefly/wiki/server_guide)

在web.xml里面添加前端控制器，默认读取的配置文件为firefly.xml，也可以通过init-param自定义配置文件名
```
<servlet>
	<servlet-name>fireflyMVC</servlet-name>
	<servlet-class>com.firefly.mvc.web.servlet.DispatcherServlet</servlet-class>
	<!-- <init-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>
			firefly_mvc.xml
		</param-value>
	</init-param> -->
	<load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
	<servlet-name>fireflyMVC</servlet-name>
	<url-pattern>/app/*</url-pattern>
</servlet-mapping>
```

firefly.xml配置文件
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://code.google.com/p/firefly/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://code.google.com/p/firefly/beans 
http://firefly.googlecode.com/files/beans.xsd">

	<component-scan base-package="com.test.sample"/>
	<mvc view-path="/WEB-INF/page" view-encoding="UTF-8"/>

</beans>
```
`<mvc/>`节点有两个属性，view-path表示jsp视图存放的路径，view-encoding为返回视图的字符集

## Controller使用 ##
例子：
```
@Controller
public class HelloController {
	@RequestMapping(value = "/hello")
	public View index(HttpServletRequest request) {
		request.setAttribute("hello", "你好 firefly!");
		return new JspView("/index.jsp");
	}

	@RequestMapping(value = "/hello/text")
	public View text(HttpServletRequest request) {
		log.info("into text output >>>>>>>>>>>>>>>>>");
		return new TextView("文本输出");
	}
	
	@RequestMapping(value = "/hello/text-?/?-?")
	public View text2(HttpServletRequest request, @PathVariable String[] args) {
		return new TextView("text-" + args[0] + "-" + args[1] + "-" + args[2]);
	}

        @RequestMapping(value = "/hello/redirect")
	public View hello5(HttpServletRequest request,
			HttpServletResponse response) {
		return new RedirectView("/hello");
	}

        @RequestMapping(value = "/book/json", method = HttpMethod.POST)
	public View getBook(@HttpParam("book") Book book) {
		return new JsonView(book);
	}

        @RequestMapping(value = "/document/?/?")
	public View document(HttpServletRequest request, @PathVariable String[] args) {
		request.setAttribute("info", args);
		return new TemplateView("/index.html");
	}
}
```
  * @RequestMapping中value定义了请求URI，可以使用"?"作为通配符，并把匹配的字符串注入到@PathVariable参数中
  * 返回视图分为TemplateView、JsonView、TextView、RedirectView、JspView。如果返回null则不进行视图渲染，需要使用response对象输出数据。

## 自动封装request参数到javabean ##
例子：
```
	@RequestMapping(value = "/book/add")
	public View gotoCreateBook() {
		return new TemplateView("/book_create.html");
	}

	@RequestMapping(value = "/book/create", method = HttpMethod.POST)
	public View createBook(@HttpParam("book") Book book) {
		book.setSell(true);
		book.setText("测试当前book");
		book.setId(90);
		return new TemplateView("/book.html");
	}
```
页面book\_create.jsp
```
......
<form action="create" method="POST">
	书名：<input type="text" name="title"></input><br/>
	价格：<input type="text" name="price"></input><br/>
	<input type="submit" value="提交"></input>
</form>
......
```
页面book.html
```
......
<body>
| ${book.id} | ${book.title} | ${book.text} | ${book.price} | ${book.sell} |
</body>
......
```
在页面book\_create.jsp输入 书名：book1和 价格：123.2，提交后book.jsp页面显示结果为：| 90 | book1 | 测试当前book | 123.2 | true |
  * @RequestMapping中method默认值为get，表示此方法用来处理get请求，可以通过设置method属性来控制处理不同的http请求
  * @HttpParam自动封装请求参数注入到javabean，，@HttpParam.value()是可选值，当不为空的时候会自动把Book对象setAttribute里面

## 拦截器的使用 ##
例子：
```
@Interceptor(uri = "/hello*", order = 0)
public class HelloInterceptor {
	public View dispose(HandlerChain chain, HttpServletRequest request, HttpServletResponse response) {
		Food food = new Food();
		food.setName("apple");
		food.setPrice(8.0);
		request.setAttribute("fruit0", food);
		
		food = foodService.getFood("strawberry");
		request.setAttribute("strawberry", food);
		log.info("food interceptor 0 : {}", food);
		
		return chain.doNext(request, response, chain);
	}
}
```
  * 拦截器约定执行dispose方法
  * 拦截的uri支持使用“`*`”通配符进行URL匹配
  * order定义拦截链的顺序
  * chain.doNext()执行拦截链的下一个对象

## 自定义错误页面 ##
例子：
```
SystemHtmlPage.addErrorPage(404, "/error/err404.html");
SystemHtmlPage.addErrorPage(500, "/error/err500.html");
```

  * 可以通过SystemHtmlPage增加自定义错误页面，当发生错误时，会输出对应状态码的自定义的错误页面

```
<!DOCTYPE html>
<html>
<head>
</head>
<body>
<h1>${#systemErrorMessage}</h1>
<p>测试自定义错误页面</p>
</body>
</html>
```

  * 页面中可以通过#systemErrorMessage属性获得系统错误信息

```
<bean class="com.firefly.server.http.Config">
	<property name="serverHome" value="/app/page"></property>
	<property name="host" value="localhost"></property>
	<property name="port" value="7676"></property>
	<property name="keepAlive" value="true"></property>
	<property name="pipeline" value="false"></property>
	<property name="errorPage">
		<map>
			<entry key="404" value="/error/err404.html"></entry>
			<entry key="500" value="/error/err500.html"></entry>
		</map>
	</property>
</bean>
```

  * 如果通过firefly.xml启动自带的HTTP Server，还可以通过errorPage属性配置自定义错误页面