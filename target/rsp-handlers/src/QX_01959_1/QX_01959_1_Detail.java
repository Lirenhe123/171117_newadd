package QX_01959_1;

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
import net.sourceforge.htmlunit.corejs.javascript.regexp.SubString;

public class QX_01959_1_Detail extends TxtRspHandler {

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
				Element titleAndContent=document.select("div[class=Content]").first();
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
				
				/*Element removeOne=titleAndContent.select("div[class=tit-attr]").first();
				if(removeOne!=null){
					removeOne.remove();
				}*/
				
				titleAndContent_clone.prepend(titleAndContent.outerHtml());
				
				
				//附件url补全
				Elements attachments=titleAndContent_clone.select("a:contains(.doc)");
				for (Element oneAttachment : attachments) {
					String href = oneAttachment.attr("href");
					if (!href.startsWith("http")) {
						href = "http://www.hcsggzy.com" + href;
					}
					oneAttachment.attr("href", href);
				}
				Elements imgs=titleAndContent_clone.select("a[href]");
				for (Element img :imgs) {
					String href = img.attr("href");
					if(href.startsWith("../")){
						href=href.substring(href.lastIndexOf("../")+2, href.length());
					}
					if (!href.startsWith("http")) {
						href = "http://www.hcsggzy.com" + href;
					}
					img.attr("href", href);
//					System.out.println(srcElement.attr("src"));
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
		QX_01959_1_Detail handler=new QX_01959_1_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01959_1/apis/QX_01959_1_Detail\\SampleResponse","utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

