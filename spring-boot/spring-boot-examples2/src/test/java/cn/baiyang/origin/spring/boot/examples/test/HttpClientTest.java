package cn.baiyang.origin.spring.boot.examples.test;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2018-04-11 14:37
 */
public class HttpClientTest {

    public static void main(String[] args) {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        HttpGet httpget = new HttpGet("http://httpbin.org/");
        httpclient.execute(httpget, null);

    }

}
