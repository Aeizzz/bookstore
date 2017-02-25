package Test;

import java.math.BigDecimal;
import java.text.MessageFormat;

import org.junit.Test;

public class dome {
	public static void main(String[] args) {
		String s="{0}或{1}错误";
		s=MessageFormat.format(s, "用户名","密码");
		System.out.println(s);
	}
	

}
