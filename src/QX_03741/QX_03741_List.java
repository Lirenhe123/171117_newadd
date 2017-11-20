package QX_03741;
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
public class QX_03741_List extends TxtRspHandler {
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
		Response response = new Response();
		if (rspState == rsp) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				System.out.println(document);
				// listAndPage
				Element listAndPage=null;
				Element parentOfStake=null;
				Elements stakesOfListAndPage = document.select("tr[onclick*=window.open]");
				for(Element oneStake:stakesOfListAndPage){
					if(oneStake.attr("onclick").contains("page=display")){
						parentOfStake=oneStake.parent();
						while(!parentOfStake.tagName().equals("tbody")){
							parentOfStake=parentOfStake.parent();
						}
						if(parentOfStake.tagName().equals("tbody")){
							break;
						}
					}
				}
System.out.println(stakesOfListAndPage);
				// 解析List
				Element divWithRows = parentOfStake;
//System.out.println(divWithRows);
				if (divWithRows == null) {
					System.out.println("divWithRows is null");
				}
				Elements rows = divWithRows.select("tr");
				for (Element row : rows) {
					if(row.attr("onclick")==null||row.attr("onclick").equals("")){
						row.remove();
						continue;
					}
					String id=row.attr("onclick");
					id.trim();
					if(id.startsWith("window.open")){
						id=id.substring(id.indexOf("?")+1, id.length()-3);
					}
					
					
					Element title_html = row.select("td").get(1);
					String title=null;
					if (title_html != null) {
						title = title_html.text().trim();
					}
					// matchesTitle()
					Element date = row.select("td").get(2);
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
					System.out.println(bn.date);
					System.out.println(bn.id);
					System.out.println(bn.title);
					response.list.add(bn);
				}

				// 解析page
				Element allFatherTagOfPage = document.select("select[onchange]").first();
				String page=null;
				if (allFatherTagOfPage == null) {
					response.pageCount = "1";
				} else {
					Elements options= allFatherTagOfPage.select("option");
					if(options!=null){
						page =options.last().text();
					}
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
		QX_03741_List handler = new QX_03741_List();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_03741/apis/QX_03741_List\\SampleResponse",
					"utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}





