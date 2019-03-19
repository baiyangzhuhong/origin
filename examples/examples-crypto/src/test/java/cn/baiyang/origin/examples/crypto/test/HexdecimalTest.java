package cn.baiyang.origin.examples.crypto.test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.testng.annotations.Test;

/**
 *
 * @author hongzhu
 * @version V1.0
 * @since 2018-09-30 15:49
 */
public class HexdecimalTest {

    @Test
    public void testSha1BytesToHex() {
        String accessToken = "!Pq5C0Uc1B1Xx0U0oJ7wgSohUOWl9K2_aH-lTxpFILGO-KIEiUPebJ1aCyS2WjZNitd2iia-GaF2iZTRyvoLWTjb-zot93xWOEjfgKnI7K3Q1N16N8IY_LxPm3U8N32DNWC5XLnOUUdxhHm_TwG6__VpnLM1O6Uf1cqKk76tvZEro8";
        String clientId = "61ebd7efef8f4ad4a6ff0a53a85f91f7";
        String tsr = "123456";
        String clientSecret = "ed7c96a52f2843c69c9206ef254e7959";
        String params = accessToken + clientId + tsr;
        String signInitStr = clientSecret + params + clientSecret;
        try {
            byte[] inputBytes = signInitStr.getBytes("UTF-8");
            System.out.println(Arrays.toString(inputBytes));

            System.out.println();
            String hexText = DigestUtils.sha1Hex(inputBytes);
            System.out.println(hexText);

            System.out.println();
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(inputBytes);
            byte[] outputBytes = digest.digest();
            System.out.println(Arrays.toString(outputBytes));
            for(byte b : outputBytes) {
                int i = (int) (b);
                i = i < 0 ? 256 + i : i;
                String s = Integer.toString(i, 16);
                s = s.length() == 1 ? "0" + s : s;
                System.out.print(s);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
