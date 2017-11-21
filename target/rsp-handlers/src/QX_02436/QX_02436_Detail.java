package QX_02436;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class QX_02436_Detail extends TxtRspHandler {

	private static RspState rsp = RspState.Login;

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
				
				System.out.println(document);
				
				//titleAndContent
				Element titleAndContent=document.select("div[id=WM_Cnt_center]").first();
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
					
					System.out.println(date_html_str);
					
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
					}
					date_html_str=year+"-"+month+"-"+day;
				}
				response.date=date_html_str;
				
				System.out.println(date_html_str);
				
				
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

