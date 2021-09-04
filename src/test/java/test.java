import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 柴柴快乐每一天
 * @create 2021-09-04  11:01 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
public class test {
    private final static String IP = "192.168.31";

    @Test
    public void test() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");
        String format = simpleDateFormat.format(new Date());
        String concatIp = IP + format;
        System.out.println(concatIp);
        String s= DigestUtils.md5Hex(concatIp);
        System.out.println(s);
    }
}
