package Test;

import java.math.BigDecimal;
import java.text.MessageFormat;

import org.junit.Test;

public class dome {
	public static void main(String[] args) {
		String s="{0}��{1}����";
		s=MessageFormat.format(s, "�û���","����");
		System.out.println(s);
	}
	

}
