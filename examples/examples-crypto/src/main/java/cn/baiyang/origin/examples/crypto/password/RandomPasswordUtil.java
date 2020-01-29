package cn.baiyang.origin.examples.crypto.password;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generate Random Password<br/>
 * Required not sequence, allow a little repeated
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-01-29 10:53
 */
public class RandomPasswordUtil {

    private static final char[] FACTORS = {'a','b','0','c','d','1','e','f','2','g','h','3','i','j','4','k','l','5','m','n','6','o','p','7','q','r','8','s','t','9','u','v','w','x','y','z'};
    private static final Pattern DIGITS = Pattern.compile("^[0-9]+$");
    private static final Pattern ALPHABET = Pattern.compile("^[a-z]+$");

    /**
     * 密码长度
     */
    private static final Integer LENGTH = 6;

    /**
     * 随机生成6位(数字+字母)密码
     * @return String
     */
    public static String getRandomPwd(){
        String randomPwd = "";
        while (true) {
            randomPwd = generatePwd();
            Matcher digitsMatcher = DIGITS.matcher(randomPwd);
            Matcher alphabetMatcher = ALPHABET.matcher(randomPwd);
            if (!digitsMatcher.matches() && ! alphabetMatcher.matches()) {
                break;
            }
        }
        return randomPwd;
    }

    private static String generatePwd() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(FACTORS.length);
            builder.append(FACTORS[index]);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(getRandomPwd());
    }

}
