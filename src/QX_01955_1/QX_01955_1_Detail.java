package QX_01955_1;

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

/**
 * 
 * @author User
 *
 */
public class QX_01955_1_Detail extends TxtRspHandler {

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





