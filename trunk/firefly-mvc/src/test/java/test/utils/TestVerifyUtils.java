package test.utils;

import org.junit.Assert;
import org.junit.Test;
import com.firefly.utils.VerifyUtils;
import static org.hamcrest.Matchers.*;

public class TestVerifyUtils {

	@Test
	public void testIsNumeric() {
		Assert.assertThat(VerifyUtils.isNumeric("13422224343"), is(true));
		Assert.assertThat(VerifyUtils.isNumeric(""), is(false));
		Assert.assertThat(VerifyUtils.isNumeric("134"), is(true));
		Assert.assertThat(VerifyUtils.isNumeric("134dfdfsfdf"), is(false));
	}

	@Test
	public void tesstSimpleWildcardMatch() {
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("*", "toto"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("toto", "toto"));
		Assert.assertFalse(VerifyUtils.simpleWildcardMatch("toto.java",
				"tutu.java"));
		Assert.assertFalse(VerifyUtils.simpleWildcardMatch("12345", "1234"));
		Assert.assertFalse(VerifyUtils.simpleWildcardMatch("*f", ""));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("***", "toto"));

		Assert.assertFalse(VerifyUtils.simpleWildcardMatch("*.java", "toto."));
		Assert.assertFalse(VerifyUtils
				.simpleWildcardMatch("*.java", "toto.jav"));
		Assert.assertTrue(VerifyUtils
				.simpleWildcardMatch("*.java", "toto.java"));
		Assert.assertFalse(VerifyUtils.simpleWildcardMatch("abc*", ""));
		Assert
				.assertTrue(VerifyUtils.simpleWildcardMatch("a*c",
						"abbbbbccccc"));

		Assert.assertTrue(VerifyUtils
				.simpleWildcardMatch("abc*xyz", "abcxxxyz"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("*xyz", "abcxxxyz"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("abc**xyz",
				"abcxxxyz"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("abc**x", "abcxxx"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("*a*b*c**x",
				"aaabcxxx"));

		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("abc*x*yz",
				"abcxxxyz"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("a*b*c*x*yf*z*",
				"aabbccxxxeeyffz"));
		Assert.assertFalse(VerifyUtils.simpleWildcardMatch("a*b*c*x*yf*zze",
				"aabbccxxxeeyffz"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("a*b*c*x*yf*z",
				"aabbccxxxeeyffz"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("a*b*c*x*yf*ze",
				"aabbccxxxeeyfze"));

		Assert.assertTrue(VerifyUtils.simpleWildcardMatch(
				"*LogServerInterface*.java", "_LogServerInterfaceImpl.java"));
		Assert.assertTrue(VerifyUtils.simpleWildcardMatch("*Log*Impl.java",
				"_LogServerInterfaceImpl.java"));
		Assert.assertTrue(VerifyUtils
				.simpleWildcardMatch("abc*xyz", "abcxyxyz"));
	}
}
