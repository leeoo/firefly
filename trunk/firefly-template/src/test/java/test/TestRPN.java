package test;

import static org.hamcrest.Matchers.is;
import static com.firefly.template.support.RPNUtils.*;

import org.junit.Assert;
import org.junit.Test;



public class TestRPN {
	
	@Test
	public void test() {
		Assert.assertThat(getReversePolishNotation("(i += +-3 + + + + -i++ - -+i --) >= 2").toString(), is("[i, +-3, + + + -i++ - -+i --, +, +=, 2, >=]"));
		Assert.assertThat(getReversePolishNotation("${login}").toString(), is("[${login}]"));
		Assert.assertThat(getReversePolishNotation("(- ${user.age} += (-3 + -  2) * 4) > 22").toString(), is("[- ${user.age}, -3, -  2, +, 4, *, +=, 22, >]"));
		Assert.assertThat(getReversePolishNotation("(${user.age} += 3 + 2 * 4) > 22").toString(), is("[${user.age}, 3, 2, 4, *, +, +=, 22, >]"));
		Assert.assertThat(getReversePolishNotation("1*2+3").toString(), is("[1, 2, *, 3, +]"));
		Assert.assertThat(getReversePolishNotation("1 + ((2 + 3) * 3) * 5").toString(), is("[1, 2, 3, +, 3, *, 5, *, +]"));
		Assert.assertThat(getReversePolishNotation("${user.age} > 1 + (2 + 3) * 5").toString(), is("[${user.age}, 1, 2, 3, +, 5, *, +, >]"));
		Assert.assertThat(getReversePolishNotation("${user.age} + 3 > 1 + (2 + 3) * 5").toString(), is("[${user.age}, 3, +, 1, 2, 3, +, 5, *, +, >]"));
		Assert.assertThat(getReversePolishNotation("!${login} != !false ").toString(), is("[!${login}, !false, !=]"));
		Assert.assertThat(getReversePolishNotation("${user.age} + 3 == ${user1.age} + (2 + 3) * 5").toString(), is("[${user.age}, 3, +, ${user1.age}, 2, 3, +, 5, *, +, ==]"));
	}
	
	public static void main(String[] args) {
		System.out.println(getReversePolishNotation("(i += +-3 + + + + -i++ - -+i --) >= 2"));
		System.out.println(getReversePolishNotation("${login}"));
		System.out.println(getReversePolishNotation("(- ${user.age} += (-3 + -  2) * 4) > 22"));
		System.out.println(getReversePolishNotation("(${user.age} += 3 + 2 * 4) > 22"));
		System.out.println(getReversePolishNotation("1*2+3"));
		System.out.println(getReversePolishNotation("1 + ((2 + 3) * 3) * 5"));
		System.out.println(getReversePolishNotation("${user.age} > 1 + (2 + 3) * 5"));
		System.out.println(getReversePolishNotation("${user.age} + 3 > 1 + (2 + 3) * 5"));
		System.out.println(getReversePolishNotation("!${login} != !false "));
		System.out.println(getReversePolishNotation("${user.age} + 3 == ${user1.age} + (2 + 3) * 5"));
	}
}
