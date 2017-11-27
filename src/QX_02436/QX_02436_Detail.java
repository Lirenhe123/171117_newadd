package QX_02436;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
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
import cn.internetware.utils.IO;

public class QX_02436_Detail extends TxtReqRspHandler {
	private static RspState rsp = RspState.Login;
	
	@Override
	public IwResponse sendIwRequest(IwRequest iwReq) {
		IwResponse iwRsp=null;
		
		String host=iwReq.getHost();
		String path=iwReq.getRequestPath();
		String id=iwReq.getRequestPathParam("id");
		
		path="http://"+host+path+"?"+id;
		
		System.out.println("path:"+path);
		final  int TIMEOUT_IN_MS = 100000;
		String result = "";
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);
			while(retBytes.length<1000){
				int i=1;
				retBytes = IOUtils.toByteArray(conn);
				if(i==10){
					break;
				}
			}
			result = new String(retBytes, "GBK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		System.out.println("result:"+result);
			
		/*try {
			String host = iwRequest.getHost();
			String path = iwRequest.getRequestPath();
			String Paging = iwRequest.getRequestPathParam("Paging");
			String pageing = iwRequest.getRequestPathParam("pageing");
			String url = "http://" + host + path;
			if(Paging != null && Paging.length() > 0 && Paging != "null"){
				url = url + "?Paging=" + Paging;
			} else {
				url = url + "?pageing=" + pageing;
			}
			System.out.println("url=" + url);
			String str = getContent(url);
			iwr = new DefaultIwResponse(null, str.getBytes("GBK"), null, 0, "ok");
		} catch (Exception e) {
			e.printStackTrace();
		}	*/
			
		new DefaultIwResponse(null, retBytes, null, TIMEOUT_IN_MS, result);
			
			try {
				iwRsp=new DefaultIwResponse(null, result.getBytes("UTF-8"), null, 0, "ok");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return iwRsp;
		
		
	}

	/*
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
	*/
	

	public class Response extends TxtBaseResponse {
		String content;
		String date;
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
				Element titleAndContent=document.select("div[class=WM_Content_Left]").first();
				if(titleAndContent==null){
					System.out.println("titleAndContent is null");
				}
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
				//resolve the date
				Element date_html=titleAndContent.select("div[class=art_info]").first();
				String date_html_str=null;
				if(date_html!=null){
					date_html_str=date_html.text().replaceAll("[\\s+]", "");
					
//					System.out.println(date_html_str);
					
					String regex="(.*)(20\\d{2})(.{1})(\\d+)(.{1})(\\d+)(.*)";
					Pattern pattern=Pattern.compile(regex);
					Matcher matcher=pattern.matcher(date_html_str);
					String year=null; 
					String month=null; 
					String day=null; 
					if(matcher.find()){
						year=matcher.group(2);
						month=matcher.group(4);
						day=matcher.group(6);
						if(month.length()<2){
							month="0"+month;
						}
						if(day.length()<2){
							day="0"+day;
						}
					}
					date_html_str=year+"-"+month+"-"+day;
					
				}
				response.date=date_html_str;
				
//				System.out.println(date_html_str);
				
				
				Element removeOne=titleAndContent.select("div[class=position]").first();
				if(removeOne!=null){
					removeOne.remove();
				}
				Element removeTwo=titleAndContent.select("div[class=art_info]").first();
				if(removeTwo!=null){
					removeTwo.remove();
				}
				
				titleAndContent_clone.prepend(titleAndContent.outerHtml());
				
				
				//附件url补全
				Elements attachments=titleAndContent_clone.select("a:contains(.doc)");
				for (Element oneAttachment : attachments) {
					String href = oneAttachment.attr("href");
					if (!href.startsWith("http")) {
						href = "http://www.ggzyzx.com" + href;
					}
					oneAttachment.attr("href", href);
				}
				
				//.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "")
				response.content="<div>"+titleAndContent_clone.outerHtml().trim()+"</div>";
//				System.out.println(response.content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	public static void main(String[] args) {
		QX_02436_Detail handler=new QX_02436_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_02436/apis/QX_02436_Detail\\SampleResponse","utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	

}

