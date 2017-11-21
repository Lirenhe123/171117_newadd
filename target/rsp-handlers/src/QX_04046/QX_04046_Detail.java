package QX_04046;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class QX_04046_Detail extends TxtRspHandler {

	private static RspState rsp = RspState.Login;

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
				if(document!=null){}
				//Ttile+Content
				Element titleAndContent=document;
				if(titleAndContent.equals("")){
					System.out.println("titleAndContent is null");
				}
				Element titleAndContent_clone=titleAndContent.clone();
				titleAndContent_clone.children().remove();
				//clean titleAndContent
				Elements scripts=titleAndContent.select("script");
				if(scripts!=null){
					for(Element script:scripts){
						script.remove();
					}
				}
				Elements styles=titleAndContent.select("style[type=text/css]");
				if(styles!=null){
					for(Element style:styles){
						style.remove();
					}
				}
				
				//select title_html
				Element title_html=titleAndContent.select("table[class=con_wzbt]").first();
				String title_html_str=null;
				if(title_html!=null){
					title_html_str=title_html.outerHtml();
				}
				Element content_html=titleAndContent.select("table[class=con_wznr]").first();
				String content_html_str=null;
				if(content_html!=null){
					content_html_str=content_html.outerHtml();
				}
				
				//拼接title和content 
				titleAndContent_clone.prepend(content_html_str);
				titleAndContent_clone.prepend(title_html_str);
				
				//附件url补全
				Elements attachments=titleAndContent_clone.select("a:contains(.doc)");
				for (Element oneAttachment : attachments) {
					String href = oneAttachment.attr("href");
					if (!href.startsWith("http")) {
						href = "http://www.wdx.gov.cn" + href;
					}
					oneAttachment.attr("href", href);
				}
				for (Element srcElement : titleAndContent_clone.select("*[src]")) {
					String src = srcElement.attr("src");
					if (!src.startsWith("http")) {
						src = "http://www.wdx.gov.cn" + src;
					}
					srcElement.attr("src", src);
				}
				
				//prepend 会加入换行符/n
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
		QX_04046_Detail handler=new QX_04046_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_04046/apis/QX_04046_Detail\\SampleResponse","utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

