package QX_01861;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class QX_01861_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
	
		
		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date="+date+ ";]";
		}

	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				 ////System.out.println(originTxtRspContent);
				//response.currentPage=originTxtRspContent;
//				String content=originTxtRspContent.replace("</body>", "");
//				content=content.replace("</html>", "");
//				//System.out.println(content);
				Document xmlDoc=Utils.getDocByContent(originTxtRspContent);
				NodeList div_list=xmlDoc.getElementsByTagName("DIV");
				for(int j=0;j<div_list.getLength();j++){
				Element div=(Element) div_list.item(j);
				if("List1".equals(div.getAttribute("class"))){
					NodeList tr_list=div.getElementsByTagName("LI");
						for(int i=0;i<tr_list.getLength();i++){
						Element ul_ele=(Element)tr_list.item(i);
							BranchNew branchNew=new BranchNew();
							Element a_ele=(Element) ul_ele.getElementsByTagName("A").item(0);
//							if(a_ele==null){
//								continue;
//							}
							String title=a_ele.getAttribute("title").trim();
							
							if(title.substring(title.length()-1, title.length()).equals(".")){
								title=title.substring(0,title.length()-3);
							}
//							//System.out.println(title);
//							title=title.replaceAll("\\s*", "");
//							title=title.replaceAll(" ", "");
							
							branchNew.title=title;
//							//System.out.println(title);
							String href=a_ele.getAttribute("href");
//							//System.out.println(href);
							String InfoID=href.substring(href.indexOf("=")+1);
							////System.out.println(InfoID);
							//String CategoryNum=href.substring(href.lastIndexOf("=")+1);
							branchNew.id=InfoID;
							String date="";
							Element SUP=(Element) ul_ele.getElementsByTagName("SPAN").item(0);
//							Element div1=(Element) div_list.item(j+1);
//							date=ul_ele.getElementsByTagName("SPAN").item(0);
							date=SUP.getTextContent();
							if(date.substring(date.indexOf("-")+1,date.lastIndexOf("-")).length()<2){
								date=date.substring(0,4)+"-"+"0"+date.substring(date.indexOf("-")+1,date.lastIndexOf("-"))+"-"+date.substring(date.lastIndexOf("-")+1);
							}
							if(date.substring(date.lastIndexOf("-")+1).length()<2){
								date=date.substring(0,date.lastIndexOf("-")+1)+"0"+date.substring(date.lastIndexOf("-")+1);
							}
							
							branchNew.date=date;
							response.list.add(branchNew);
							//System.out.println(branchNew.toString());
							
						}
					}
				}
				
				String page="1";
				NodeList td_List=xmlDoc.getElementsByTagName("SPAN");
				for(int i=0;i<td_List.getLength();i++){
					Element td_ele=(Element) td_List.item(i);
					if(" color:#0033FF".equals(td_ele.getAttribute("style"))){
//						page=td_ele.getTextContent();
//						page=page.substring(page.indexOf("=")+1);
//						page=page.substring(page.indexOf("=")+1);
//						page=page.substring(page.indexOf("=")+1);
//						page=page.substring(page.indexOf("=")+1);
//						page=page.substring(0,page.indexOf("/")).trim();
//						//System.out.println(page);
//						NodeList td_list1=td_ele.getElementsByTagName("A");
//						Element td_ele1=(Element) td_ele.getElementsByTagName("A").item(td_list1.getLength()-1);
//						page=td_ele1.getTextContent().trim();
//						page=td_ele1.getAttribute("href");	
//						page=page.substring(page.lastIndexOf("_")+1,page.lastIndexOf(".")).trim();
//						if(!td_ele1.getTextContent().equals("...")){
							page=td_ele.getTextContent().trim();
							page=page.substring(page.indexOf("/")+1).trim();
//						}
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

	final static int TIMEOUT_IN_MS = 100000;

	private static String callApi(String path) {
		String result = "";
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);
			result = new String(retBytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args) {
		QX_01861_List handler = new QX_01861_List();

		try {
			String originRspContent = IO
					.deserializeString("./internetware/HB01861/apis/HB01861List/SampleResponse","gb2312");
			handler.processRspContent(
					handler.checkTxtRspContentState(originRspContent),
					originRspContent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
