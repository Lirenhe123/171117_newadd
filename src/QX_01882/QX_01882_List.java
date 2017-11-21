package QX_01882;

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

public class QX_01882_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		// String currentPage;
		String nextPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;

		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date
					+ ";]";
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
				Document xmlDoc = Utils.getDocByContent(originTxtRspContent);
				NodeList div_list = xmlDoc.getElementsByTagName("div");
				
				if (div_list != null) {
					for (int i = 0; i < div_list.getLength(); i++) {
						Element div_ele = (Element) div_list.item(i);
						if ("uc_lanmu_content".equals(div_ele
								.getAttribute("class"))) {
							NodeList li_list = div_ele
									.getElementsByTagName("li");
							if (li_list != null) {
								for (int j = 0; j < li_list.getLength(); j++) {
									Element li_ele = (Element) li_list.item(j);
									if ("article_style_1".equals(li_ele
											.getAttribute("class"))) {
										BranchNew branchNew = new BranchNew();
										Element aElement = (Element) li_ele
												.getElementsByTagName("a")
												.item(0);
										String title = aElement
												.getTextContent().trim();
										title = title.replace("...", "");
										// //System.out.println(title);

										Element bElement = (Element) li_ele
												.getElementsByTagName("span")
												.item(1);
										String date = bElement.getTextContent();

										branchNew.date = date;

										String href = aElement
												.getAttribute("href");
										String id = href.substring(
												href.indexOf("infoid=") + 7,
												href.lastIndexOf("&"));
//										 System.out.println(id);

										branchNew.title = title;

										branchNew.id = id;

										response.list.add(branchNew);
//										System.out
//												.println(branchNew.toString());
									}
								}
							}
						}
					}
				}

				NodeList div2_list = xmlDoc.getElementsByTagName("div");

				if (div2_list != null) {
					for (int i = 0; i < div2_list.getLength(); i++) {
						Element div2_ele = (Element) div2_list.item(i);
						if ("ctl01_ctl01_Page_Panel".equals(div2_ele
								.getAttribute("id"))) {
							NodeList a_list = div2_ele
									.getElementsByTagName("a");

							if (a_list != null) {
								for (int j = 0; j < a_list.getLength(); j++) {
									Element a_ele = (Element) a_list.item(j);
									if ("尾页".equals(a_ele.getTextContent())) {

										String page = a_ele
												.getAttribute("href");
										page = page.substring(
												page.indexOf("page=") + 5,
												page.lastIndexOf("&"));
										String pageCount = page;

										response.pageCount = pageCount;
//										//System.out.println(pageCount);
									}
								}
							}

						}

					}

				}

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
		QX_01882_List handler = new QX_01882_List();

		try {
			String originRspContent = IO
					.deserializeString(
							"./internetware/QX_01882/apis/QX_01882_List/SampleResponse");
			handler.processRspContent(
					handler.checkTxtRspContentState(originRspContent),
					originRspContent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}