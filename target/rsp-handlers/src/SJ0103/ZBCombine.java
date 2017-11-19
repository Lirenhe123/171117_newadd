package SJ0103;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.utils.Utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class ZBCombine extends TxtReqRspHandler {

	public class Response extends TxtBaseResponse {

		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;

		//String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String url;


		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date+ ";url=" + url+ "]";
		}

	}


	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {

		return RspState.Login;
	}



	protected TxtRspObject processTxtRspContent(RspState rspState,
			String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			Gson gson = new Gson();
			Response result = gson.fromJson(originTxtRspContent,
					new TypeToken<Response>() {
			}.getType());

			response.list = result.list;
			response.pageCount=result.pageCount;
			
			//response.currentPage = result.currentPage;
		}

		return response;
	}

	final static int TIMEOUT_IN_MS = 100000;

	private static String callApi(String path) {
		byte[] retBytes = new byte[0];
		try {
			URLConnection conn = new URL(path).openConnection();
			conn.setConnectTimeout(TIMEOUT_IN_MS);
			conn.setReadTimeout(TIMEOUT_IN_MS);
			retBytes = IOUtils.toByteArray(conn);

			return new String(retBytes, "GB2312");
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
	public IwResponse sendIwRequest(IwRequest iwRequest) {
		IwResponse iwRsp = null;
		try {
			String CategoryNum = iwRequest.getRequestPathParam("CategoryNum").trim();
			//			String id=iwRequest.getRequestPathParam("id").trim();
			//			String FromUrl=iwRequest.getRequestPathParam("FromUrl").trim();
//			String lei = CategoryNum.substring(0, 6);
			String iw_ir_4=CategoryNum;
			//			////System.out.println(iw_ir_2);
			String iw_ir_2 = CategoryNum.substring(0,6);
			//					.trim();
			//			String leiString = lei + "/" + iw_ir_4;
			String __VIEWSTATE;
			//			String __CSRFTOKEN;
//						String __VIEWSTATEGENERATOR;
			String __EVENTVALIDATION;
			//String __VIEWSTATEGENERATOR=iwRequest.getRequestContentParam("__VIEWSTATEGENERATOR").trim();
			//String iw_ir_2=iwRequest.getRequestContentParam("iw_ir_2").trim();
			//			String ctl00_myTreeView_ExpandState=iwRequest.getRequestContentParam("ctl00_myTreeView_ExpandState").trim();
			//			String ctl00$ContentPlaceHolder1$BestNewsListALL$myGV$ctl13$inPageNum=iwRequest.getRequestContentParam("ctl00$ContentPlaceHolder1$BestNewsListALL$myGV$ctl13$inPageNum").trim();
			String __EVENTTARGET=iwRequest.getRequestContentParam("__EVENTTARGET").trim();
			String __EVENTARGUMENT=iwRequest.getRequestContentParam("__EVENTARGUMENT").trim();
			String sdParamsUrl = getNewPathPrefix() + "/?"
					+ getAdditionalLinkParamStr()  + "&iw-cmd=0103_Params"
					+"&iw_ir_2="+iw_ir_2
					+"&iw_ir_4="+iw_ir_4
					+"&CategoryNum="+CategoryNum;
			//System.out.println(sdParamsUrl);
			
			String sdParamsList = Utils.callApi(sdParamsUrl);
			////System.out.println(sdParamsList + "*******************");
			JSONObject jsonObject = new JSONObject(sdParamsList);
			JSONObject params = (JSONObject) jsonObject.get("params");
			// //System.out.println(params.get("__CSRFTOKEN"));
			sdParamsList = getJson(sdParamsList, "params");
			__VIEWSTATE = params.getString("__VIEWSTATE");
			//System.out.println("1111111");
            			__EVENTVALIDATION=params.getString("__EVENTVALIDATION");
			//			__CSRFTOKEN=params.getString("__CSRFTOKEN");
//			__VIEWSTATEGENERATOR = params.getString("__VIEWSTATEGENERATOR");
			//		 __EVENTTARGET = params.getString("__EVENTTARGET");
			//		__EVENTARGUMENT = params.getString("__EVENTARGUMENT");
			// pageToken = params.getString("__CSRFTOKEN");
			String ChangZhouListCombine = getNewPathPrefix() + "/?"
					+ getAdditionalLinkParamStr()
					+ "&iw-cmd=0103_List"
					//+"&__CSRFTOKEN="+__CSRFTOKEN
					+"&__VIEWSTATE=" + __VIEWSTATE
					+ "&__EVENTVALIDATION="+__EVENTVALIDATION
//					+"&__VIEWSTATEGENERATOR="+__VIEWSTATEGENERATOR
//					+ "&ctl00_myTreeView_ExpandState="+ctl00_myTreeView_ExpandState
					+"&__EVENTARGUMENT="+__EVENTARGUMENT
                    +"&__EVENTTARGET"+__EVENTTARGET
                    +"&iw_ir_2="+iw_ir_2
					+"&iw_ir_4="+iw_ir_4
					+"&CategoryNum="+CategoryNum;
//					+"&ctl00$ContentPlaceHolder1$BestNewsListALL$myGV$ctl13$inPageNum="+ctl00$ContentPlaceHolder1$BestNewsListALL$myGV$ctl13$inPageNum;
			////System.out.println(ChangZhouListCombine);
			String result = Utils.callApi(ChangZhouListCombine);
			////System.out.println(result);
			iwRsp = new DefaultIwResponse(null, result.getBytes("GB2312"), null,
					0, "ok");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwRsp;

	}
	public String getJson(String Content, String ID) {
		String resultStr = "";
		try {
			if (Content.substring(0, 1) != "[") {
				Content = "[" + Content + "]";
			}
			JSONArray jsonArray = new JSONArray(Content);
			resultStr = jsonArray.getJSONObject(0).toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultStr;
	}
	// public static void main(String[] args) {
	// CarInfoResult handler = new CarInfoResult();
	// try {
	// String originRspContent = IO
	// .deserializeString("E:\\test\\DriverInfo.html");
	// handler.processRspContent(handler.checkRspContentState(originRspContent),
	// originRspContent);
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
