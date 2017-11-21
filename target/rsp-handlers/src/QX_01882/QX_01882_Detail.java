package QX_01882;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class QX_01882_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;

	}

	private static final String Element = null;

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
				originTxtRspContent = "<div>" + originTxtRspContent + "</div>";
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);

				NodeList div_list = xmlDoc.getElementsByTagName("div");
				String content1 = "";
				for (int i = 0; i < div_list.getLength(); i++) {
					Element div_ele = (Element) div_list.item(i);
					if (div_ele.getAttribute("id").equals("Infor_Content")) {
						content1 = Utils.getNodeHtml(div_ele);
//						//System.out.println(content1);
						response.content = content1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_01882_Detail handler = new QX_01882_Detail();

		try {
			String originRspContent = IO
					.deserializeString(
							"./internetware/QX_01882/apis/QX_01882_Detail/SampleResponse");
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
