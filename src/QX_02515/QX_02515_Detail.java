package QX_02515;

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

public class QX_02515_Detail extends TxtRspHandler {

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
				Element titleAndContent=document.select("div[class=editor]").first();
				if(titleAndContent==null){
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
				Element content_html=titleAndContent;
				String content_html_str=null;
				if(content_html!=null){
					content_html_str=content_html.outerHtml();
				}
				
				//拼接title和content 
				titleAndContent_clone.prepend(content_html_str);
				
				//prepend 会加入换行符/n
				//.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "")
				response.content="<div>"+titleAndContent_clone.outerHtml().trim()+"</div>";
				System.out.println(response.content);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	public static void main(String[] args) {
		QX_02515_Detail handler=new QX_02515_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_02515/apis/QX_02515_Detail\\SampleResponse","utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

