package QX_01861;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

public class QX_01861_Detail extends TxtRspHandler {
	
	public class Response extends TxtBaseResponse {
		String content;
		String date;
	}
	final static int TIMEOUT_IN_MS = 100000;

	private static String callApi(String path) {
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);

			return new String(retBytes, "UTF-8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
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
				originTxtRspContent = "<div>" + originTxtRspContent
						+ "</div>";
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList a_List=xmlDoc.getElementsByTagName("A");//改附件
				if(a_List.getLength()>0){
					for(int j=0;j<a_List.getLength();j++){
						Element a_ele=(Element) a_List.item(j);
						String href=a_ele.getAttribute("href");
						if(!href.equals("")){
						String a=href.substring(0, 1);
						if(a.equals("/")){
							href="http://www.xhqggzy.com"+href;
							a_ele.setAttribute("href", href);
						}
						}
					}
				}
				NodeList a_List1=xmlDoc.getElementsByTagName("IMG");//改附件
				if(a_List1.getLength()>0){
					for(int j=0;j<a_List1.getLength();j++){
						Element a_ele=(Element) a_List1.item(j);
						String href=a_ele.getAttribute("src");
						if(!href.equals("")){
						String a=href.substring(0, 1);
						if(a.equals("/")){
							href="http://www.xhqggzy.com"+href;
							a_ele.setAttribute("src", href);
						}
						}
					}
				}
//				String content2="";
//				NodeList td_List1=xmlDoc.getElementsByTagName("h2");
//				for(int i=0;i<td_List1.getLength();i++){
//					Element td_ele=(Element) td_List1.item(i);
//					if("title".equals(td_ele.getAttribute("class"))){
//						content2=Utils.getNodeHtml(td_ele).trim();
//					}
//				}
				String content1="";
				NodeList td_List=xmlDoc.getElementsByTagName("DIV");
				for(int i=0;i<td_List.getLength();i++){
					Element td_ele=(Element) td_List.item(i);
					if("Section0".equals(td_ele.getAttribute("class"))){
//						//System.out.println(Utils.getNodeHtml(td_ele).trim());
//						td_ele.getElementsByTagName("TR").item(0).setTextContent("");
//						content1=Utils.getNodeHtml(td_ele).trim();
//						Element td_ele1=(Element) td_List.item(i+1);
						content1=Utils.getNodeHtml(td_ele).trim();
//						break;
					}
				}
				
				response.content =content1;
				//System.out.println(response.content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_01861_Detail handler = new QX_01861_Detail();

		try {
			String originRspContent = IO
					.deserializeString("./internetware/HB01861/apis/HB01861Detail/SampleResponse","gb2312");
			handler.processRspContent(
					handler.checkRspContentState(originRspContent),
					originRspContent);
			handler.transformTableToBeImage(originRspContent);
			// //System.out.println(originRspContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

