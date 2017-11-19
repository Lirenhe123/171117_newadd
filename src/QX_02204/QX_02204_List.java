package QX_02204;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class QX_02204_List extends TxtRspHandler {
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
				// listAndPage
				Element listAndPage=null;
				/*
				Element parentOfStake=null;
				Element stakeOfListAndPage=null;
				Elements stakesOfListAndPage = document.select("span[class=h12]");
				if(stakesOfListAndPage!=null){
					stakeOfListAndPage=stakesOfListAndPage.first();
					if(stakeOfListAndPage.getElementsByTag("img")!=null){
						parentOfStake=stakeOfListAndPage.parent();
						while(!parentOfStake.tagName().equals("tbody")){
							parentOfStake=parentOfStake.parent();
						}
					}
				}*/
				listAndPage=document.select("td[class=sy_bk3]").first();
				// 解析List
				Element outTagOfOnlyList = listAndPage.select("table").first();
				if(outTagOfOnlyList!=null){
					Element table=outTagOfOnlyList;
					Elements trs = table.select("tr:has(a)");
					for (Element tr : trs) {
						Element aTag = tr.select("a[href]").first();
						String id = aTag.attr("href");
						String title = aTag.attr("title");
						if(title==null||title.equals("null")||title.equals("")){
							title=aTag.text().trim();
						}
						// matchesTitle()
						Pattern pattern = Pattern.compile("(20\\d{2})[-/](\\d{1,2})[-/](\\d{1,2})");
						Matcher matcher ;
						Element date_html = tr.select("td[class=zi_wz1]").last();
						String date_str=null;
System.out.println(date_html);
						if (date_html != null) {
							date_str = date_html.text().trim();
							date_str.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
						}
						matcher = pattern.matcher(date_str);
						if(matcher.find()){
							String year = matcher.group(1);
							String month = matcher.group(2);
							String day = matcher.group(3);
							if(month.length() == 1)
								month = "0" + month;
							if(day.length() == 1)
								day = "0" + day;
							date_str = year + "-" + month + "-" + day;
						}
						
						BranchNew bn = new BranchNew();
						bn.date = date_str;
						bn.id = id;
						bn.title = title;
						
						
//						System.out.println(bn.date);
//						System.out.println(bn.id);
//						System.out.println(bn.title);
						response.list.add(bn);
					}
				}
				/*
				// 解析page
				Element stakeOfPage=document.select("input[id=nowPage]").first();
				Element parentOfPageTag=stakeOfPage.parent().parent();
				Element allFatherTagOfPage = parentOfPageTag;
				System.out.println(allFatherTagOfPage);
				String page = null;
				if (allFatherTagOfPage == null) {
					response.pageCount = "1";
				} else {
					page = allFatherTagOfPage.text().replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "")
							.replaceAll("(.*)/(\\d+)(.*)", "$2");
				}
				response.pageCount = page;
System.out.println("asdalkdsflk"+response.pageCount);*/

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_02204_List handler = new QX_02204_List();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_02204/apis/QX_02204_List\\SampleResponse", "utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
