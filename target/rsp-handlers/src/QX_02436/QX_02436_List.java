package QX_02436;

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
public class QX_02436_List extends TxtRspHandler {
	private static RspState rsp = RspState.Login;

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;

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
		Response response = new Response();
		if (rspState == rsp) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				// listAndPage
				Element listAndPage = null;
				// 解析List
				listAndPage = document.select("div[class=class_listcnt]").first();
				Element divWithRows = listAndPage.select("div[class=class_list1 lm_class_bg]").first();

				if (divWithRows == null) {
					System.out.println("divWithUl is null");
				}
				Elements rows = divWithRows.select("li");
				for (Element row : rows) {
					if (row.attr("class").equals("list_pages")) {
						continue;
					}
					Element aTag = row.select("a").first();
					String id = aTag.attr("href");
					if (id.startsWith("http")) {
						id = id.substring("http://www.ggzyzx.com".length(), id.length());
					}
					StringBuffer id_buffer=new StringBuffer(id);
					id_buffer=id_buffer.insert(id.indexOf("?")+1, "id=");
					id="/"+id_buffer.toString();
					String title = aTag.attr("title");
					if (title == null || title.equals("null") || title.equals("")) {
						title = aTag.text().trim();
					}
					// matchesTitle()
					BranchNew bn = new BranchNew();
					bn.id = id;
					bn.title = title;
					// -----------------------------------------------------
//					 System.out.println(bn.id);
//					 System.out.println(bn.title);
					response.list.add(bn);
				}

				// 解析page
				Element allFatherTagOfPage = document.select("li[class=list_pages]").first();
				String page = null;
				if (allFatherTagOfPage == null) {
					response.pageCount = "1";
				} else {
					page = allFatherTagOfPage.text().replaceAll("\\s*", "").replaceAll("(.*)/(\\d+)(.*)", "$2");
				}
				response.pageCount = page;
//				 System.out.println(response.pageCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_02436_List handler = new QX_02436_List();
		try {
			String originTxtRspContent = IO
					.deserializeString("internetware/QX_02436/apis/QX_02436_List\\SampleResponse", "utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
