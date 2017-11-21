package QX_02448;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class QX_02448_List extends TxtRspHandler {

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
			return "BranchNew [title=" + title + ", id=" + id + ", date="
					+ date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
		
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				NodeList divs = xmlDoc.getElementsByTagName("div");
				if(divs != null){
					for(int i = 0 ; i < divs.getLength();i++){
						Element dive = (Element) divs.item(i);
						if("List1".equals(dive.getAttribute("class"))){
							Element ule = (Element) dive.getElementsByTagName("ul").item(0);
							NodeList lis = ule.getElementsByTagName("li");
							if(lis != null){
								for(int j = 0 ; j < lis.getLength();j++){
									Element lie = (Element) lis.item(j);
									BranchNew bn = new BranchNew();
									Element ae = (Element) lie.getElementsByTagName("a").item(0);
									String id = ae.getAttribute("href");
									id = id.substring(id.indexOf("?")+1,id.length());
									String title = ae.getAttribute("title").trim();
									String str = lie.getElementsByTagName("span").item(0).getTextContent().trim();
									Date da = sdf.parse(str);
									String date = sdf.format(da);
									bn.date = date;
									bn.title = title;
									bn.id = id;
									response.list.add(bn);
//									System.out.println("title="+title+" id="+id+" date="+date);
								}
							}
						}
						if("TxtCenter".equals(dive.getAttribute("class"))){
							String page = dive.getElementsByTagName("span").item(1).getTextContent().trim();
							page = page.substring(page.indexOf("/")+1,page.length()).trim();
							response.pageCount = page;
						}
					}
				}
//				System.out.println(response.pageCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_02448_List handler = new QX_02448_List();
		try {
			String originContent = IO.deserializeString("internetware/QX02448/apis/QX02448_List\\SampleResponse","GB2312");
			handler.processRspContent(handler.checkTxtRspContentState(originContent),originContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
