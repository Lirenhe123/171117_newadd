package QX_01959_1;
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
public class QX_01959_1_List extends TxtRspHandler {
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
//		System.out.println(originTxtRspContent);
		Response response = new Response();
		if (rspState == rsp) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				// listAndPage
				Element listAndPage=null;
				// 解析
				listAndPage=document.select("table[class=GridView]").first();
				Element divWithRows = listAndPage;
				
				if (divWithRows == null) {
					System.out.println("divWithUl is null");
				}
				Elements rows = divWithRows.select("tr[class=Row]");
				for (Element row : rows) {
					Element aTag = row.select("a").first();
					String id = aTag.attr("href");
					id=id.substring(id.indexOf("id"),id.length());
					
					String title = aTag.attr("title");
					if (title == null || title.equals("null") || title.equals("")) {
						title = aTag.text().trim();
					}
					Element title_html=row.select("td[class=DispLimitColumn]").last();
					if(title_html!=null){
						title=title_html.select("span[id]").attr("title");
					}
					// matchesTitle()
					Element date = row.select("td[class=DateColumn]").last();
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
				Element allFatherTagOfPage = document.select("td[class=RowPager]").first();
				String page=null;
				if (allFatherTagOfPage == null) {
					response.pageCount = "1";
				} else {
					page = allFatherTagOfPage.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "")
							.replaceAll("(.*)/(\\d+)(.*)", "$2");
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
		QX_01959_1_List handler = new QX_01959_1_List();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01959_1/apis/QX_01959_1_List\\SampleResponse",
					"utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}





