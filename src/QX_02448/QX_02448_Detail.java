package QX_02448;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class QX_02448_Detail extends TxtRspHandler{
	public class Response extends TxtBaseResponse{
		String content;
		String title;
		String date;
	}
	
	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if(rspState == RspState.Login){
			originTxtRspContent = "<div>" + originTxtRspContent + "</div>";
			try {
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList divs = xmlDoc.getElementsByTagName("div");
				String content = null;
				if(divs != null && divs.getLength() > 0){
					for(int i = 0 ; i < divs.getLength();i++){
						Element dive = (Element) divs.item(i);
						if("BorderCCCDot Padding10 TxtCenter".equals(dive.getAttribute("class"))){
							String title = dive.getElementsByTagName("font").item(0).getTextContent().trim();
							response.title = title;
							content = Utils.getNodeHtml(dive);
						}
						if("Section0".equals(dive.getAttribute("class"))){
							content = content + Utils.getNodeHtml(dive);
						}
					}
				}
				//remove something
				org.jsoup.nodes.Document document=Jsoup.parse(content);
				Elements removes=document.select("p[class=MsoNormal]");
				for(org.jsoup.nodes.Element removeOne:removes){
					if(removeOne.text().contains("发布时间")){
						removeOne.remove();
					}
				}
				response.content = "<div>"+document.outerHtml()+"</div>";
//				System.out.println(response.title);
//				System.out.println(response.content);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return response;
	}

	public static void main(String[] args) {
		QX_02448_Detail handler = new QX_02448_Detail();
		try {
			String originContent = IO.deserializeString("internetware/QX02448/apis/QX02448_Detail\\SampleResponse","GB2312");
			handler.processRspContent(handler.checkRspContentState(originContent), originContent);
			handler.transformTableToBeImage(originContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
