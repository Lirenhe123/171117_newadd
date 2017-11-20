package QX_01508;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class QX_01508_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
	
		
		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id +  ";date=" + date+ "]";
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
				originTxtRspContent=originTxtRspContent.substring(0,originTxtRspContent.length()-1);
				originTxtRspContent=originTxtRspContent.substring(originTxtRspContent.indexOf(":")+1,originTxtRspContent.lastIndexOf("}")+1);
				JSONObject data=new JSONObject(originTxtRspContent);
				String currentPage= data.getString("currentPage").toString();
				currentPage = currentPage.substring(currentPage.lastIndexOf("共")+1,currentPage.lastIndexOf("页"));
				response.pageCount=currentPage;
				originTxtRspContent = originTxtRspContent.substring(originTxtRspContent.indexOf("source")+8,originTxtRspContent.lastIndexOf("}]")+2);
				JSONArray array=new JSONArray(originTxtRspContent);
				for(int i = 0;i < array.length();i++){
					JSONObject object = array.getJSONObject(i);
					
					BranchNew branchNew = new BranchNew();
					
					String title = object.getString("main_title").toString();
					branchNew.title=title;
					
					String date = object.getString("create_date").toString();
					branchNew.date=date;
					
					String id = object.getString("content_path").toString();
					branchNew.id=id;
					
					response.list.add(branchNew);
					////System.out.println(branchNew);
				}
				////System.out.println(response.pageCount);
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
		QX_01508_List handler = new QX_01508_List();

		try {
			String originRspContent = IO
					.deserializeString("./internetware/QX_01508/apis/QX_01508_List/SampleResponse");
			handler.processRspContent(
					handler.checkTxtRspContentState(originRspContent),
					originRspContent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
