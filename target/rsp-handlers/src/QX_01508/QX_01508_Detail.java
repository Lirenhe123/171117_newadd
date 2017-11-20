package QX_01508;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class QX_01508_Detail extends TxtRspHandler {
	
	public class Response extends TxtBaseResponse {
		String content;
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
				NodeList div_list = xmlDoc.getElementsByTagName("div");
				String content="";
				if(div_list!=null){
					for(int i =0;i<div_list.getLength();i++){
						Element div_ele = (Element) div_list.item(i);
						if("cont_cen_ncon".equals(div_ele.getAttribute("class"))){
							NodeList a_list = div_ele.getElementsByTagName("a");
							if(a_list.getLength()!=0){
								for(int j =0;j<a_list.getLength();j++){
									Element a_ele = (Element) a_list.item(j);
									String href = a_ele.getAttribute("href");
									if(href.length()!=0&&"/".equals(href.substring(0,1))){
										href = "http://www.bjtzzfcg.gov.cn"+href;
										a_ele.setAttribute("href", href);
									}
								}
							}
							content += Utils.getNodeHtml(div_ele);
						}
					}
				}
				response.content=content;
				////System.out.println(response.content);
				} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_01508_Detail handler = new QX_01508_Detail();

		try {
			String originRspContent = IO
					.deserializeString("./internetware/QX_01508/apis/QX_01508_Detail/SampleResponse");
			handler.processRspContent(
					handler.checkRspContentState(originRspContent),
					originRspContent);
			handler.transformTableToBeImage(originRspContent);
			// ////System.out.println(originRspContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

