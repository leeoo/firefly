package test;

import static org.hamcrest.Matchers.is;
import static com.firefly.template.support.RPNUtils.*;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.template.support.RPNUtils;
import com.firefly.template.support.RPNUtils.Fragment;



public class TestRPN {
	
	@Test
	public void test() {
		Assert.assertThat(getReversePolishNotation("(i += +-3 + + + + -i++ - -+i --) >= 2").toString(), is("[i, -3, -i++, +, -i --, -, +=, 2, >=]"));
		Assert.assertThat(getReversePolishNotation("${login}").toString(), is("[${login}]"));
		Assert.assertThat(getReversePolishNotation("(- ${user.age} += (-3 + -  2) * 4) > 22").toString(), is("[-${user.age}, -3, -2, +, 4, *, +=, 22, >]"));
		Assert.assertThat(getReversePolishNotation("(${user.age} += 3 + 2 * 4) > 22").toString(), is("[${user.age}, 3, 2, 4, *, +, +=, 22, >]"));
		Assert.assertThat(getReversePolishNotation("1*2+3").toString(), is("[1, 2, *, 3, +]"));
		Assert.assertThat(getReversePolishNotation("1*2+3>>2+1").toString(), is("[1, 2, *, 3, +, 2, 1, +, >>]"));
		Assert.assertThat(getReversePolishNotation("1 + ((2 + 3) * 3) * 5").toString(), is("[1, 2, 3, +, 3, *, 5, *, +]"));
		Assert.assertThat(getReversePolishNotation("${user.age} > 1 + (2 + 3) * 5").toString(), is("[${user.age}, 1, 2, 3, +, 5, *, +, >]"));
		Assert.assertThat(getReversePolishNotation("${user.age} + 3 > 1 + (2 + 3) * 5").toString(), is("[${user.age}, 3, +, 1, 2, 3, +, 5, *, +, >]"));
		Assert.assertThat(getReversePolishNotation("${user.age} + 3 == ${user1.age} + (2 + 3) * 5").toString(), is("[${user.age}, 3, +, ${user1.age}, 2, 3, +, 5, *, +, ==]"));
		
		List<Fragment> list = getReversePolishNotation("!${login} != !false ");
		Assert.assertThat(list.toString(), is("[!${login}, !false, !=]"));
		Assert.assertThat(list.get(0).type, is(RPNUtils.Type.VARIABLE));
		Assert.assertThat(list.get(1).type, is(RPNUtils.Type.BOOLEAN));
		Assert.assertThat(list.get(2).type, is(RPNUtils.Type.CONDITIONAL_OPERATOR));
		
		list = getReversePolishNotation("${name} != \"Pengtao Qiu\"");
		Assert.assertThat(list.get(1).type, is(RPNUtils.Type.STRING));
		
		list = getReversePolishNotation("${user.age} > 18");
		Assert.assertThat(list.get(1).type, is(RPNUtils.Type.INTEGER));
		
		list = getReversePolishNotation("${user.id} > 18L");
		Assert.assertThat(list.get(1).type, is(RPNUtils.Type.LONG));
		
		list = getReversePolishNotation("${food.price} > 3.3f");
		Assert.assertThat(list.get(1).type, is(RPNUtils.Type.FLOAT));
		
		list = getReversePolishNotation("${food.price} > 3.3");
		Assert.assertThat(list.get(1).type, is(RPNUtils.Type.DOUBLE));
	}
	
	public static void main(String[] args) {
		float e = -.3F;
		
		System.out.println(Boolean.parseBoolean("!false"));
//		System.out.println(Long.parseLong("-3L"));
		int i = 0;
		if(!  ((-i + + - -+i) >= 2)) {
			
		}
		
		List<Fragment> list = getReversePolishNotation("! ${login} != ! false");
		System.out.println(list.toString());
		for(Fragment f : list) {
			System.out.print(f.type + ", ");
		}
		System.out.println();
		
		list = getReversePolishNotation("${name} != \"Pengtao Qiu\"");
		System.out.println(list.toString());
		for(Fragment f : list) {
			System.out.print(f.type + ", ");
		}
		System.out.println();
		
		System.out.println(getReversePolishNotation("\"Pengtao Qiu\" == ${user.name}"));
		System.out.println(getReversePolishNotation("(- ${user.age} += (-3 + -  2) * 4) > 22"));
		System.out.println(getReversePolishNotation("(i += +-3 + + + + -i++ - -+i --) >= 2"));
		System.out.println(getReversePolishNotation("1*2+3>>2+1"));
	}
}
