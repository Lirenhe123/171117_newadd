package QX_01952;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class QX_01952_List extends TxtRspHandler {
	private static RspState rsp = RspState.Login;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		
		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		System.out.println(originTxtRspContent);
		
		
		Response response = new Response();
		if (rspState == rsp) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				// listAndPage
				Element listAndPage=null;
				listAndPage=document.select("div[class=content_right]").first();
				// 解析List
				Element divWithRows = listAndPage.select("ul[class=news_list]").first();
				
				
				if (divWithRows == null) {
					System.out.println("divWithUl is null");
				}
				Elements rows = divWithRows.select("li");
				
				for (Element row : rows) {
					Element aTag = row.select("a").last();
					String id = aTag.attr("href");
					if(id.startsWith("http")){
						id=id.substring("http://lzzc.liaozhong.gov.cn".length(), id.length());
					}
					
					String title = aTag.attr("title");
					if (title == null || title.equals("null") || title.equals("")) {
						title = aTag.text().trim();
					}
					// matchesTitle()
					Element date = row.select("span").first();
					String date_str = null;
					if (date != null) {
						date_str = date.text().trim();
						date_str=date_str.replaceAll("(.*)(\\d{4}-\\d{2}-\\d{2})(.*)", "$2");
					}
					BranchNew bn = new BranchNew();
					bn.date = date_str;
					bn.id = id;
					bn.title = title;
					//-----------------------------------------------------
//					System.out.println(bn.date);
//					System.out.println(bn.id);
//					System.out.println(bn.title);
					response.list.add(bn);
				}

				// 解析page
				Element allFatherTagOfPage = document.select("span[class=pageinfo]").first();
				if(allFatherTagOfPage!=null){
					allFatherTagOfPage=allFatherTagOfPage.parent();
				}
				String page=null;
				if (allFatherTagOfPage == null) {
					response.pageCount = "1";
				} else {
					page = allFatherTagOfPage.select("strong").first().text();
				}
				response.pageCount=page;
//System.out.println(response.pageCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_01952_List handler = new QX_01952_List();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01952/apis/QX_01952_List\\SampleResponse",
					"utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}





