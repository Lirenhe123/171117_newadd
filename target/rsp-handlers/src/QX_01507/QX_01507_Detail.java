package QX_01507;


import java.io.IOException;

import org.jgroups.protocols.pbcast.STABLE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class QX_01507_Detail extends TxtRspHandler {

	private static RspState rsp = RspState.Login;

	public class Response extends TxtBaseResponse {
		String content;
		String title;
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
				Element titleAndContent=null;
				Element parentOfStake=null;
				Element title_html=null;
				String title_str=null;
				
				Element table=document.select("table").get(1);
				if(table!=null){
					titleAndContent=table;
				}else{
					Elements stakesOfListAndPage = document.select("p[class=MsoNormal]");
					if(stakesOfListAndPage==null||stakesOfListAndPage.outerHtml().equals("")){
						stakesOfListAndPage=document.select("p[class=p]");
						title_html=stakesOfListAndPage.select("p[class=p]").first();
						if(title_html!=null){
							title_str=title_html.text();
							Element attachment=title_html.select("a[href]").first();
							String href=attachment.attr("href");
							if(href.startsWith("href")){
								href="http://www.caigou.bjshy.gov.cn"+href;
							}
							attachment.attr("href",href);
						}
						response.title=title_str.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");;
					}else{
						title_html=stakesOfListAndPage.select("p[class=MsoNormal]").first();
						System.out.println("==========="+title_html);
						if(title_html!=null){
							title_str=title_html.text();
						}
						response.title=title_str.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");;
					}
					for(Element oneStake:stakesOfListAndPage){
						if(oneStake.parent()!=null){
							parentOfStake=oneStake.parent();
							while(!parentOfStake.tagName().equals("tbody")){
								parentOfStake=parentOfStake.parent();
							}
							if(parentOfStake.tagName().equals("tbody")){
								break;
							}
						}
					}
					titleAndContent=parentOfStake;
				}
				
				if(titleAndContent==null){
					System.out.println("titleAndContent is null");
				}
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
				Element removeOne=titleAndContent.select("div[style=width:100%; height:30px; line-height:30px; text-align:center; font-size:12px]").first();
				
				if(removeOne!=null){
					removeOne.remove();
				}
				
				
				//附件url补全
				Elements attachments=titleAndContent.select("a[href]");
				for (Element oneAttachment : attachments) {
					String href = oneAttachment.attr("href");
					if (!href.startsWith("http")) {
						href = "http://www.caigou.bjshy.gov.cn" + href;
					}
					oneAttachment.attr("href", href);
				}
				/*for (Element srcElement : detailDivElement.select("*[src]")) {
					String src = srcElement.attr("src");
					if (!src.startsWith("http")) {
						src = "" + src;
					}
					srcElement.attr("src", src);
				}*/
				
				
				//.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "")
				response.content="<div>"+titleAndContent.outerHtml().trim()+"</div>";
//				System.out.println(response.content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	public static void main(String[] args) {
		QX_01507_Detail handler=new QX_01507_Detail();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01507/apis/QX_01507_Detail\\SampleResponse","utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

