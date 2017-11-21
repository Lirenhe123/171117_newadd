package QX_01873_1;

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

public class QX_01873_1_Detail extends TxtRspHandler {

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
				// Ttile+Content
				Element titleAndContent = document.select("div[class=detail]").first();
				Element titleAndContent_clone = titleAndContent.clone();
				titleAndContent_clone.children().remove();
				// clean titleAndContent
				Elements scripts = titleAndContent.select("script");
				if (scripts != null) {
					for (Element script : scripts) {
						script.remove();
					}
				}
				Elements styles = titleAndContent.select("style[type=text/css]");
				if (styles != null) {
					for (Element style : styles) {
						style.remove();
					}
				}
				
				Element content_html=titleAndContent.select("div[class=detail]").first();
				Elements attachments=content_html.select("a[href]");
				for(Element attachment:attachments){
					String href=attachment.attr("href");
					if(!href.startsWith("http")){
						href="http://lp.ggzyjyw.com"+href; 
					}
					attachment.attr("href", href);
				}
				String content_html_str=null;
				if(content_html!=null){
					content_html_str=content_html.outerHtml().trim();
				}
				
				// 拼接title和content
				titleAndContent_clone.prepend(content_html_str);
				// prepend 会加入换行符/n
				response.content = "<div>" + titleAndContent_clone.outerHtml().trim().replaceAll("[\n]", "") + "</div>";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_01873_1_Detail handler = new QX_01873_1_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01873_1/apis/QX_01873_1_Detail\\SampleResponse", "utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
