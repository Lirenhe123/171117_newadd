package QX_02204;

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

public class QX_02183_Detail extends TxtRspHandler {

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
				//Ttile+Content
				Element titleAndContent=document;
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
				Element title_html=titleAndContent.select("span[class=STYLE12]").first();
				//remove something of title_html
				String title_html_str=null;
				if(title_html!=null){
					title_html_str="<div>"+title_html.outerHtml()+"</div>";
				}
				
				Element stakeOfContentTag=titleAndContent.select("p[style=WORD-BREAK: break-all]").first();
				Element content_html=stakeOfContentTag.parent();
				Element removeOne=content_html.select("p[strong]").last();
				if(removeOne!=null){
					removeOne.remove();
				}
				String content_html_str=null;
				if(content_html!=null){
					content_html_str=content_html.outerHtml();
				}
				//拼接title和content 
				titleAndContent_clone.prepend(content_html_str);
				titleAndContent_clone.prepend(title_html_str);
				//prepend 会加入换行符/n
				response.content="<div>"+titleAndContent_clone.outerHtml().trim().replaceAll("[\n]", "")+"</div>";
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		return response;
	}
	
	public static void main(String[] args) {
		QX_02183_Detail handler=new QX_02183_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_02183/apis/QX_02183_Detail\\SampleResponse","utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

