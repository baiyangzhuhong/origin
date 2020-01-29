package cn.baiyang.origin.examples.crypto.invitecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generate Random Invite Code<br/>
 * Required global unique and random<br/>
 * <strong>
 *     对于惟一性，用时间戳来保证 - 取JavaVirtualMachine的时间毫秒数字(从1970年1月1日开始)；
 *     <br/>
 *     变换为随机字符的方法为：
 *     <li>首先，把此惟一数字换算成37进制的一串表示数字；正好对应于37个字符列表</li>
 *     <li>其次，对此37进制列表数字做障眼法处理-进行一定规则的乱序排列</li>
 *     <li>最后，再加上一位随机字符</li>
 * </strong>
 * 注意: 在37进制表示的当前时间戳数字，至少是8位的，所以随机字符也至少位8位。此方法为线程安全且能保证毫秒级别没有重复(<br/>
 * 同一毫秒并发:不超过37个时创建可能会重复; 超过37个一定会重复)，永远惟一。
 * @author hongzhu
 * @version V1.0
 * @since 2020-01-29 11:00
 */
public class RandomInviteCodeUtil {

    private static final char[] FACTORS = {'a', '!', 'b','c', '@', 'd','e', '#', 'f','2','g','h','3','j','4','k','5','m','n','6', 'p','7','q','r','8','s','t','9','u', '%', 'v','w', '&', 'x','y', '*', 'z'};
    private static final Pattern DIGITS = Pattern.compile("^[0-9]+$");
    private static final Pattern ALPHABET = Pattern.compile("^[a-z]+$");

    private static final int MIN_LENGTH = 8;
    private static final long PADDING_INDEX = 19;

    /**
     * 随机生成9位邀请码
     * @return String
     */
    public static String getRandomInviteCode(){
        String randomInviteCode = "";
        while (true) {
            randomInviteCode = generateInviteCode();
            Matcher digitsMatcher = DIGITS.matcher(randomInviteCode);
            Matcher alphabetMatcher = ALPHABET.matcher(randomInviteCode);
            if (!digitsMatcher.matches() && ! alphabetMatcher.matches()) {
                break;
            }
        }
        return randomInviteCode;
    }

    private static String generateInviteCode() {
        /**
         * 这里不用System.nanoTime()的原因:
         * <li>产生的随机数字太长，37进制至少14个字符</li>
         * <li>官方API说明nanoTime API主要是为了计算时间偏差，不适合确切时间数字表示场景</li>
         */
        Long[] notion = getNotion(System.currentTimeMillis());
        byte[] maskArr = mask(notion);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < maskArr.length; i++) {
            builder.append(FACTORS[maskArr[i]]);
        }

        Random random = new Random(System.nanoTime());
        int index = random.nextInt(FACTORS.length);
        builder.append(FACTORS[index]);
        return builder.toString();
    }

    private static byte[] mask(Long[] notion) {
        int size = notion.length;
        if (size < MIN_LENGTH) {
            for (; size < MIN_LENGTH; size++) {
                notion[size] = PADDING_INDEX;
            }
        }

        byte[] arr = new byte[size];
        for (int i = 0; i < size; i++) {
            arr[i] = notion[i].byteValue();
        }

        int mid_index = size / 2;
        byte first = arr[0];
        byte midPlusOne = arr[mid_index];
        arr[mid_index] = first;
        arr[0] = midPlusOne;
        byte mid = arr[mid_index - 1];
        byte last = arr[size - 1];
        arr[mid_index - 1] = last;
        arr[size - 1] = mid;

        return arr;
    }

    private static Long[] getNotion(long n) {
        List<Long> list = new ArrayList<Long>();
        while (true) {
            list.add(n % FACTORS.length);
            long result = n / FACTORS.length;
            if (result < FACTORS.length) {
                list.add(result);
                break;
            }
            n = result;
        }
        return list.toArray(new Long[0]);
    }

    public static void main(String[] args) {
        System.out.println(getRandomInviteCode());
    }

}
