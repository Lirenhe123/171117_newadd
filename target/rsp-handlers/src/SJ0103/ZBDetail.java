package SJ0103;



import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class ZBDetail extends TxtRspHandler {

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
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				Element  td=xmlDoc.getElementById("TDContent");
				
					String TDContent=Utils.getNodeHtml(td).trim();
					response.content=TDContent;
					//System.out.println(TDContent);
				
				


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		ZBDetail handler = new ZBDetail();

		try {
			String originRspContent = IO
					.deserializeString("internetware/SJ0103/apis/0103_Detail\\SampleResponse","GB2312");
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

