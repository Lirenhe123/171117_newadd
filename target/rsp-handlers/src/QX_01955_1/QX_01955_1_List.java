package QX_01955_1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
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
public class QX_01955_1_List extends TxtRspHandler {
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
		System.out.println("aaa");
		Response response = new Response();
		if (rspState == rsp) {
			try {
				Document document = Jsoup.parse(originTxtRspContent);
				
				System.out.println("adlkfald"+originTxtRspContent);
				
				// listAndPage
				Element parentOfStake=null;
				Elements stakesOfListAndPage = document.select("a[href*=http://www.dlzh.gov.cn]");
				for(Element oneStake:stakesOfListAndPage){
					if(oneStake.attr("target").equals("_blank")){
						parentOfStake=oneStake.parent();
						while(!parentOfStake.tagName().equals("tbody")){
							parentOfStake=parentOfStake.parent();
						}
						if(parentOfStake.tagName().equals("tbody")){
							break;
						}
					}
				}
				// 解析List
				Element divWithRows = parentOfStake;
				
				if (divWithRows == null) {
					System.out.println("divWithUl is null");
				}
				Elements rows = divWithRows.select("tr");
				for (Element row : rows) {
					Element aTag = row.select("a").first();
					String id = aTag.attr("href");
					if(id.startsWith("http")){
						id=id.substring("http://www.dlzh.gov.cn".length(), id.length());
					}
					String url = "";
					url = super.getNewPathPrefix() + "/?" + super.getAdditionalLinkParamStr()
					+ "&iw-cmd=QX_01955_1_Detail&" + id;
					
					String title = aTag.attr("title");
					if (title == null || title.equals("null") || title.equals("")) {
						title = aTag.text().trim();
					}
					// matchesTitle()
					Element date = row.select("td").last();
					String date_str = null;
					if (date != null) {
						date_str = date.text().trim();
						date_str=date_str.replaceAll("(.*)(\\d{4}-\\d{2}-\\d{2})(.*)", "$2");
					}
					BranchNew bn = new BranchNew();
					bn.date = date_str;
					bn.id = id;
					bn.title = title;
					bn.url = url;
					//-----------------------------------------------------
//					System.out.println(bn.date);
//					System.out.println(bn.id);
//					System.out.println(bn.url);
//					System.out.println(bn.title);
					response.list.add(bn);
				}

				// 解析page
				Element allFatherTagOfPage = document.select("select[name=page]").first();
				if(allFatherTagOfPage!=null){
					allFatherTagOfPage=allFatherTagOfPage.parent();
				}
				String page=null;
				if (allFatherTagOfPage == null) {
					response.pageCount = "1";
				} else {
					page = allFatherTagOfPage.text().replaceAll("\\s*", "").replaceAll("(.*)/(\\d+)(.*)", "$2");
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
		QX_01955_1_List handler = new QX_01955_1_List();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01955_1/apis/QX_01955_1_List\\SampleResponse",
					"utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	@Override
//	public IwResponse sendIwRequest(IwRequest arg0) {
//		System.out.println("sssss");
//		return null;
//	}
}





