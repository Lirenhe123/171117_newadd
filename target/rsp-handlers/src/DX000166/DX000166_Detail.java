package DX000166;

import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;

public class DX000166_Detail extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		String content;
		String title;
	}

	Pattern p = Pattern.compile("(\\d{4})(年||-)(\\d{1,2})(月||-)(\\d{1,2})");

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
				// 获取整个dom树
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				// 获取所有td
				NodeList td_list = xmlDoc.getElementsByTagName("div");
				// 循环所有的td
				String content = "";
				for (int i = 0; i < td_list.getLength(); i++) {
					// 获取每个td
					Element td_point = (Element) td_list.item(i);
					// 判断目的td
					if ("topTlt".equals(td_point.getAttribute("class"))) {
						Element h1 = (Element) td_point.getElementsByTagName(
								"p").item(0);
						content = Utils.getNodeHtml(h1);
					}
						// 将整个td的内容存放到字符串
						if ("divAbs".equals(td_point
								.getAttribute("class"))) {

							NodeList IMG_list = td_point
									.getElementsByTagName("img");
							if (IMG_list != null) {
								for (int j = 0; j < IMG_list.getLength(); j++) {
									Element IMG_ele = (Element) IMG_list
											.item(j);
									String src = IMG_ele.getAttribute("src");
									if (src.indexOf("http") == -1) {
										src = "http://wz.shangh.95306.cn"+src;
									}
									IMG_ele.setAttribute("src", src);
								}
							}

							NodeList a_list = td_point
									.getElementsByTagName("a");
							if (a_list != null) {
								for (int j = 0; j < a_list.getLength(); j++) {
									Element a_ele = (Element) a_list.item(j);
									String href = a_ele.getAttribute("href");
									if (href.indexOf("www") == -1
											&& href.indexOf("@") == -1
											&& href.indexOf("http") == -1) {
										
										href = "http://wz.shangh.95306.cn"+href;
									}
									a_ele.setAttribute("href", href);
								}
							}
							content += Utils.getNodeHtml(td_point);

						}

					}
				if(content.contains("请登录查看附件"))
				{
					String con="<div>信息來源：<a href='http://wz.shangh.95306.cn'>上海铁路局物资采购商务平台</a>";
					content+=con;
				}
			//	System.out.println(content);
				response.content=content;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		DX000166_Detail handler = new DX000166_Detail();

		try {
			String originRspContent = IO
					.deserializeString(
							"internetware/DX000166/apis/DX000166_Detail/SampleResponse",
							"utf-8");
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
