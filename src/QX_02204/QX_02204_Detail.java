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

public class QX_02204_Detail extends TxtRspHandler {

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
				Element titleAndContent=document.select("td[class=sy_bk3]").first();
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
				
				Element removeOne=titleAndContent.select("td[class=zi_top3]").first();
				if(removeOne!=null){
					removeOne.remove();
				}
				Element removeTwo=titleAndContent.select("td>img[src*=images]").first();
				if(removeTwo!=null){
					removeTwo.remove();
				}
				//拼接title和content 
				
				titleAndContent_clone.prepend(titleAndContent.outerHtml());
				//prepend 会加入换行符/n
				response.content="<div>"+titleAndContent_clone.outerHtml().trim()+"</div>";
//				System.out.println(response.content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		return response;
	}
	
	public static void main(String[] args) {
		QX_02204_Detail handler=new QX_02204_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_02204/apis/QX_02204_Detail\\SampleResponse","utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

