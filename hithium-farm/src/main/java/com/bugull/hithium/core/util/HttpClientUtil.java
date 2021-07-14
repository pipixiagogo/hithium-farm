package com.bugull.hithium.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class HttpClientUtil {
    private static HttpClientUtil httpClientUtilWithoutSSL = null;
    private static HttpClientUtil httpClientUtilWithSSL = null;
    static {
        httpClientUtilWithoutSSL = new HttpClientUtil( false );
        httpClientUtilWithSSL = new HttpClientUtil( true );
    }
    private CloseableHttpClient httpClient = null;
    private HttpClientUtil(boolean isSSL ){
        if( !isSSL ){
            httpClient = HttpClients.createDefault();
        }else {
            try{
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{
                        //证书信任管理器（用于https请求）
                        new X509TrustManager(){
                            @Override
                            public void checkClientTrusted(X509Certificate[] arg0,
                                                           String arg1) throws CertificateException {
                            }
                            @Override
                            public void checkServerTrusted(X509Certificate[] arg0,
                                                           String arg1) throws CertificateException {
                            }
                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                        }
                }, new SecureRandom());
                //获取注册建造者
                RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
                //注册http和https请求
                Registry<ConnectionSocketFactory> socketFactoryRegistry  = registryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(sslContext))
                        .build();
                //获取HttpClient池管理者
                PoolingHttpClientConnectionManager connManager  = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                //初始化httpClient
                httpClient = HttpClients.custom().setConnectionManager(connManager).build();
            }catch(KeyManagementException e){
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    public static HttpClientUtil getInstanceWithSSL(){
        return httpClientUtilWithSSL;
    }
    public static HttpClientUtil getInstanceWithoutSSL(){
        return httpClientUtilWithoutSSL;
    }

    public void get(String url, Map<String,String> params, Map<String,String> httpHeader,
                    RequestConfig config,
                    Function<HttpResponse,Void> success,
                    Function<Exception,Void> timeout) throws IOException {
        CloseableHttpResponse response = null;
        try{
            String _url = url;
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if( params != null && params.size() > 0 ){
                sb.append("?");
                Set<Map.Entry<String, String>> entries = params.entrySet();
                for( Map.Entry<String,String> entry : entries ){
                    sb.append( URLEncoder.encode(entry.getKey(),"utf-8") ).append("=")
                            .append( URLEncoder.encode(entry.getValue(),"utf-8") ).append("&");
                }
                _url = sb.substring(0,sb.length());
            }
            HttpGet get = new HttpGet(_url);
            if( httpHeader != null && httpHeader.size() > 0 ){
                Set<Map.Entry<String,String>> head = httpHeader.entrySet();
                for( Map.Entry<String,String> entry : head ){
                    get.setHeader( entry.getKey(),entry.getValue() );
                }
            }
            if( config != null ){
                get.setConfig( config );
            }
            response = httpClient.execute(get);
            success.apply( response );
        }catch (Exception e){
            timeout.apply(e);
        }finally {
            if( response != null ){
                response.close();
            }
        }
    }



    public JSONObject get(String url, Map<String,String> params, Map<String,String> httpHeader
            , RequestConfig config
    ) throws IOException {
        CloseableHttpResponse response = null;
        try{
            String _url = url;
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if( params != null && params.size() > 0 ){
                sb.append("?");
                Set<Map.Entry<String, String>> entries = params.entrySet();
                for( Map.Entry<String,String> entry : entries ){
                    sb.append( URLEncoder.encode(entry.getKey(),"utf-8") ).append("=")
                            .append( URLEncoder.encode(entry.getValue(),"utf-8") ).append("&");
                }
                _url = sb.toString().substring(0,sb.length());
            }
            HttpGet get = new HttpGet(_url);
            if( httpHeader != null && httpHeader.size() > 0 ){
                Set<Map.Entry<String,String>> head = httpHeader.entrySet();
                for( Map.Entry<String,String> entry : head ){
                    get.setHeader( entry.getKey(),entry.getValue() );
                }
            }
            if( config != null ){
                get.setConfig( config );
            }
            response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            String str = EntityUtils.toString(entity);
            JSONObject json = JSON.parseObject( str );
            return json;
        }catch (Exception e){
            return null;
        }finally {
            if( response != null ){
                response.close();
            }
        }
    }
}
