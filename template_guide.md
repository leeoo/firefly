
# 特性 #
  * 无侵入，使用html注释语法，可以使用任何html编辑器。
  * 使用编译运行，比解释执行拥有更高的性能。
  * 可以单独使用，或集成到web环境中使用。

# Getting Started #
```
TemplateFactory t = new TemplateFactory(new File(TestConfig.class.getResource("/page").toURI())).init();

View view = t.getView("/index.html");
ByteArrayOutputStream out = new ByteArrayOutputStream();
Model model = new ModelMock();
view.render(model, out);
out.close();
System.out.println(out.toString());
```
  * 传入模板所在的根目录即可使用构造模板引擎

```
<!DOCTYPE html>
<html>
<body>
<div>
<div>${len(users)}</div>
<div>${u.name}</div>
</div>
</body>
</html>
```
  * ${u.name}打印model对象属性的值。
  * ${len(users)}是一个函数调用，表示打印users对象的长度

# 模版语法 #
关键字：if, else, elseif,  eval, for, include, set, switch, case<br />

## if, else, elseif ##
```
<!DOCTYPE html>
<html>
<body>
	<div>
	<!-- #if ${login} -->
	Welcome ${user.name}
	<!-- #else -->
	您无法访问
	<!-- #end -->
	</div>
	
	<!-- #if ${user.age} > 15 + 3 -->
	<div>年龄大于18</div>
	<!-- #end -->
	
	<!-- #if 15*2<=${user.age}  -->
	<div>年龄不小于30</div>
	<!-- #end -->
	
	<div>${testFunction(3, "hello", user.age)}</div>
	<div>${testFunction2()}</div>
	
	<div>
	<!-- #if "Pengtao Qiu" == ${user.name} -->
		城主来了
	<!-- #elseif ${user.name} == "Bob" -->
		厨师来了
	<!-- #elseif ${user.name} == "Jim" -->
		Jim来了
	<!-- #else -->
		小罗罗来了
	<!-- #end -->
	</div>
</body>
</html>
```
  * 条件表达式语法和java语法一样，支持多条件和括号等

## eval ##
```
<div><!-- #eval 3.0 + 3 * 5.0 / 2.0 --></div>
```
  * eval用来打印一个求值表达式

## for ##
```
<!DOCTYPE html>
<html>
<body>
<div>
<!-- #for i : ${intArr} -->
${i} &nbsp;&nbsp;
<!-- #end -->
</div>

<div>
<div>${len(users)}</div>
<table style="table-layout: fixed;">
	<thead style="text-align: center;">
	<tr><th>姓名</th><th>年龄</th></tr>
	</thead>
	<tbody>
	<!-- #for u : ${users} -->
	<tr><td>${u.name}|||${len(u.name)}</td><td>${u.age}</td></tr>
	<!-- #end -->
	</tbody>
</table>
</div>
</body>
</html>
```

## switch, case ##
```
<!DOCTYPE html>
<html>
<body>
<div>
<!-- #switch ${stage} -->
<!-- #case 1 -->
	stage1
<!-- #case 2 -->
	stage2
<!-- #default -->
	stage-default
<!-- #end -->
</div>

</body>
</html>
```

## include ##
```
<!DOCTYPE html>
<html>
<head>
<!-- #include /common/head.html?title=测试include -->
</head>
<body>
<!-- #include /common/top.html -->
<!-- #include /common/top.html?title=这是第一页 -->
</body>
</html>
```
  * include后面的URL要以/开头

## set ##
```
<!DOCTYPE html>
<html>
<body>
<!-- #set msg=welcom&price=4.5&testName=${name} -->
<div>
${msg}&nbsp;&nbsp;${testName}
</div>
<div>
苹果的价格是：${price}
</div>

</body>
</html>
```
  * set可以把一组键值对保存到model中

# 自定义函数 #
## 默认函数 ##
  * ${len(Object)} 求对象的长度，传入的对象可以是数组、集合类或者字符串。
  * ${dateFormat(Date)} 时间格式化打印，需要传入一个Date对象。
## 编写自定义函数 ##
```
Function function = new Function(){
	@Override
	public void render(Model model, OutputStream out, Object... obj) {
		Integer i = (Integer)obj[0];
		String str = (String)obj[1];
		String o = String.valueOf(obj[2]);
	
		try {
			out.write((i + "|" + str + "|" + o).getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
};

FunctionRegistry.add("testFunction", function);
```
  * 注册函数后就可以在模版页面中调用了，例如${testFunction(3, "hello", user.age)}
  * 注册自定义函数必须在TemplateFactory初始化之前

# 性能测试 #
含有if, else, for等简单逻辑的模版页面，循环渲染100000次
| **模板引擎** | **吞吐量** |
|:-----------------|:--------------|
| firefly-template\_1.0 | 2917tps |
| freemark\_2.3.18 | 1588tps |

测试用例：
```
<!DOCTYPE html>
<html>
<body>
<div>${user.name}/${user.role}</div>
<!-- #if ${user.role} == "admin" -->
<table>
	<tr>
	  <th>NO.</th>
	  <th>Title</th>
	  <th>Author</th>
	  <th>Publisher</th>
	  <th>PublicationDate</th>
	  <th>Price</th>
	  <th>DiscountPercent</th>
	  <th>DiscountPrice</th>
	</tr>
	<!-- #for book : ${books} -->
		<!-- #if ${book.price} > 0 -->
		<tr>
		  <td>${book_index}</td>
		  <td>${book.title}</td>
		  <td>${book.author}</td>
		  <td>${book.publisher}</td>
		  <td>${dateFormat(book.publication)}</td>
		  <td>${book.price}</td>
		  <td>${book.discount}%</td>
		  <td>${book_count(book)}</td>
		</tr>
		<!-- #end -->
	<!-- #end -->
</table>
<!-- #elseif ${user} != null -->
<table>
  <tr>
    <td>No privilege.</td>
  </tr>
</table>
<!-- #else -->
<table>
  <tr>
    <td>No login.</td>
  </tr>
</table>
<!-- #end -->
</body>
</html>
```