package cn.seu.dkpure;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * A threadsafe static http client
 * @author dkpure
 *
 */
public class DkHttpClient {
	private static HttpClient dk_http_client;
	
	/** A private constructor prevents instantiation */
	private DkHttpClient() { }
	
	/**
	 * A synchronized method to get a http client
	 * @return a http client
	 */
	public static synchronized HttpClient getHttpClient () {
		if (null == dk_http_client) {
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			
			ConnManagerParams.setTimeout(params, 30000); // 30s
			
			HttpConnectionParams.setConnectionTimeout(params, 30000); // 30s
			HttpConnectionParams.setSoTimeout(params, 30000); // 30s
			
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
			dk_http_client = new DefaultHttpClient(conMgr, params);			
		}
		
		return dk_http_client;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
