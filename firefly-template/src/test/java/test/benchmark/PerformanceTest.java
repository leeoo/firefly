package test.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import test.ModelMock;
import test.TestConfig;

import com.firefly.template.Function;
import com.firefly.template.FunctionRegistry;
import com.firefly.template.Model;
import com.firefly.template.TemplateFactory;
import com.firefly.template.View;
import com.firefly.utils.time.SafeSimpleDateFormat;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class PerformanceTest {

	public static void main(String[] args) throws Throwable {
		int size = 100;
        int times = 10000;
        Random random = new Random();
        Book[] books = new Book[size];
        for (int i = 0; i < size; i ++) {
            books[i] = new Book(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), new Date(), random.nextInt(100) + 10, random.nextInt(60) + 30);
        }
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("user", new User("liangfei", "admin"));
        context.put("books", books);
        
        Model model = new ModelMock();
        model.put("user", new User("liangfei", "admin"));
        model.put("books", books);
        
     // freemark
        StringWriter writer = new StringWriter();
        Configuration configuration = new Configuration();
        configuration.setTemplateLoader(new ClassTemplateLoader(PerformanceTest.class, "/"));
        Template template = configuration.getTemplate("books.ftl");
        template.process(context, writer);
//        byte[] ret = null;
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
        	writer = new StringWriter();
        	template.process(context, writer);
        	writer.toString().getBytes("UTF-8");
		}
        long end = System.currentTimeMillis();
        System.out.println("freemark: " + (end - start) + "ms\t" + (int)(times / (double)(end - start) * 1000) + "tps");
//        System.out.println(new String(ret, "UTF-8"));
        
        // firefly
        Function function = new Function(){
			@Override
			public void render(Model model, OutputStream out, Object... obj) {
				Date date = (Date)obj[0];
				try {
					out.write(SafeSimpleDateFormat.defaultDateFormat.format(date).getBytes("UTF-8"));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}};
		FunctionRegistry.add("date_format", function);
		Function function2 = new Function(){
			@Override
			public void render(Model model, OutputStream out, Object... obj) {
				Integer index = (Integer)model.get("book_index");
				if(index != null) {
					model.put("book_index", index + 1);
				} else {
					model.put("book_index", 1);
				}
			}};
		FunctionRegistry.add("book_index", function2);
		Function function3 = new Function() {

			@Override
			public void render(Model model, OutputStream out, Object... obj) {
				Book book = (Book)obj[0];
				try {
					out.write(String.valueOf(book.getPrice() * book.getDiscount() / 100).getBytes("UTF-8"));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
		};
		FunctionRegistry.add("book_count", function3);
        TemplateFactory t = new TemplateFactory(new File(TestConfig.class.getResource("/").toURI())).init();
        View view = t.getView("/books.html");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        view.render(model, out);
		out.close();
		start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
        	out = new ByteArrayOutputStream();
            view.render(model, out);
    		out.close();
        }
        end = System.currentTimeMillis();
        System.out.println("firefly-template: " + (end - start) + "ms\t" + (int)(times / (double)(end - start) * 1000) + "tps");
//        System.out.println(new String(out.toByteArray(), "UTF-8"));

	}

}
