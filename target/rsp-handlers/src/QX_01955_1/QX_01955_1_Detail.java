package QX_01955_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

/**
 * 
 * @author User
 *
 */
public class QX_01955_1_Detail extends TxtReqRspHandler {
		private static RspState rsp = RspState.Login;

		@Override
		public IwResponse sendIwRequest(IwRequest iwReq) {
			IwResponse iwRsp=null;
			String host=iwReq.getHost();
			String path=iwReq.getRequestPath();
			path="http://"+host+path;
//			System.out.println(path);
			String result=null;
			try {
				result=getContent(path);
				iwRsp=new DefaultIwResponse(null, result.getBytes("GBK"), null, 0, "ok");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return iwRsp;
		}
		
		
		public static String getContent(String url) {
			String str = "";
			CloseableHttpClient client = HttpClients.createDefault();
			HttpResponse response;
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
			httpGet.addHeader("Content-type", "text/html; charset=GBK");
			httpGet.addHeader("Accept", "text/html");
			
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(10 * 1000).setSocketTimeout(180 * 1000).build();
			httpGet.setConfig(requestConfig);
			
			try {
				response = client.execute(httpGet);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					HttpEntity resEntity = response.getEntity();
					InputStream ins = resEntity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "GBK"));
					StringBuilder sb = new StringBuilder();
					while ((str = reader.readLine()) != null) {
						sb.append(str);
					}
					str = sb.toString();
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return str;
		}
		
		public class Response extends TxtBaseResponse {
			String content;
		}

		@Override
		protected RspState checkTxtRspContentState(String originTxtRspContent) {
			return rsp;
		}

		@Override
		protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
			Response response = new Response();
			if (rspState == rsp) {
				try {
					Document document = Jsoup.parse(originTxtRspContent);
					document.outputSettings().prettyPrint(true);
					//titleAndContent
					Element titleAndContent=document;
					//clone
					Element titleAndContent_clone=titleAndContent.clone();
					titleAndContent_clone.children().remove();
					
					Elements scripts=titleAndContent.select("script");
					if(scripts!=null){
						for(Element script:scripts){
							script.remove();
						}
					}
					Elements styles=titleAndContent.select("type[text/css]");
					if(styles!=null){
						for(Element style:styles){
							style.remove();
						}
					}
					Element title_html=titleAndContent.select("td[class=info_title]").first();
					String title_str=null;
					if(title_html!=null){
						title_str=title_html.outerHtml();
					}
					Element content_html=titleAndContent.select("td[class=info_content]").first();
					
//					System.out.println("asdfasdflasldflas"+content_html);
					
					Elements attachments=content_html.select("a[href*=.doc]");
					if(attachments!=null){
						for(Element attachment:attachments){
							String href=attachment.attr("attr");
							if(!href.startsWith("http")){
								href="http://www.dlzh.gov.cn"+href;
							}
							attachment.attr("href",href);
						}
					}
					String content_str=null;
					if(content_html!=null){
						content_str=content_html.outerHtml();
					}
					
					titleAndContent_clone.prepend(content_str);
					titleAndContent_clone.prepend(title_str);
					
					//.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "")
					response.content="<div>"+titleAndContent_clone.outerHtml().trim()+"</div>";
//System.out.println(response.content);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}
	public static void main(String[] args) {
		QX_01955_1_Detail handler = new QX_01955_1_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01955_1/apis/QX_01955_1_Detail\\SampleResponse",
					"utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}





