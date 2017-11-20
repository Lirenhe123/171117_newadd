package QX_01952;

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

public class QX_01952_Detail extends TxtRspHandler {

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
				Element titleAndContent=document.select("div[class=content_right]").first();
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
				Element title_html=titleAndContent.select("div[class=tlt_02]").first();
				String title_html_str=null;
				if(title_html!=null){
					title_html_str=title_html.outerHtml();
				}
				Element content_html=titleAndContent.select("div[class=txt_03]").first();
				Elements imgs=content_html.select("img[src]");
				for(Element img:imgs){
					String src=img.attr("src");
					if(!src.startsWith("http")){
						src="http://lzzc.liaozhong.gov.cn"+src;
					}
					img.attr("src",src);
				}
				String content_html_str=null;
				if(content_html!=null){
					content_html_str=content_html.outerHtml();
				}
				//拼接title和content 
				titleAndContent_clone.prepend(content_html_str);
				titleAndContent_clone.prepend(title_html_str);
				//prepend 会加入换行符/n
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
		QX_01952_Detail handler=new QX_01952_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01952/apis/QX_01952_Detail\\SampleResponse","utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

