package QX_01959_1;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;

import cn.internetware.phone.extension.reqrsp.IwRequest;
import cn.internetware.phone.extension.reqrsp.IwResponse;
import cn.internetware.phone.extension.reqrsp.impl.DefaultIwResponse;
import cn.internetware.phone.extension.reqrsp.impl.TxtReqRspHandler;
import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.utils.IO;
import cn.internetware.utils.Utils;


/**
 * 
 * @author User
 *
 */
public class QX_01959_1_ListCombine extends TxtReqRspHandler {
	private static RspState rsp = RspState.Login;
	
	@Override
	public IwResponse sendIwRequest(IwRequest iwReq) {
		IwResponse iwRsp=null;
		try {
			String ProType=iwReq.getRequestPathParam("ProType");
			String AfficheType=iwReq.getRequestPathParam("AfficheType");
			String Class=iwReq.getRequestPathParam("Class");
			String ModuleID=iwReq.getRequestPathParam("ModuleID");
			String ViewID=iwReq.getRequestPathParam("ViewID");
System.out.println("class:"+Class);
System.out.println("viewid:"+ViewID);

			String urltoCallParams=null;
			if(ViewID==null||ViewID.equals("null")||ViewID.equals("")){
				if(Class.equals("null")||Class==null||Class.equals("")){
					urltoCallParams=getNewPathPrefix()+"/?"+getAdditionalLinkParamStr()
					+"&iw-cmd=QX_01959_1_Params"
					+"&ProType="+ProType
					+"&AfficheType="+AfficheType
					+"&ModuleID="+ModuleID
					;
				}else{
					urltoCallParams=getNewPathPrefix()+"/?"+getAdditionalLinkParamStr()
					+"&iw-cmd=QX_01959_1_Params"
					+"&ProType="+ProType
					+"&AfficheType="+AfficheType
					+"&ModuleID="+ModuleID
					+"&Class="+Class
					;
				}
			}else{
				if(Class.equals("null")||Class==null||Class.equals("")){
					urltoCallParams=getNewPathPrefix()+"/?"+getAdditionalLinkParamStr()
					+"&iw-cmd=QX_01959_1_Params"
					+"&ProType="+ProType
					+"&AfficheType="+AfficheType
					+"&ModuleID="+ModuleID
					+"&ViewID="+ViewID
					;
				}else{
					urltoCallParams=getNewPathPrefix()+"/?"+getAdditionalLinkParamStr()
					+"&iw-cmd=QX_01959_1_Params"
					+"&ProType="+ProType
					+"&AfficheType="+AfficheType
					+"&ModuleID="+ModuleID
					+"&Class="+Class
					+"&ViewID="+ViewID
					;
				}
			}
System.out.println(urltoCallParams);
			
			String paramsList=Utils.callApi(urltoCallParams);
			while(paramsList.length()<100||paramsList==null){
				int i=0;
				paramsList=Utils.callApi(urltoCallParams);
				i++;
				if(i==100){
					System.out.println("paramsList is null");
					break;
				}
			}
//System.out.println(paramsList);
			JSONObject paramJson=new JSONObject(paramsList);
			JSONObject params_obj=paramJson.getJSONObject("params");
//System.out.println(params_obj);



			String __VIEWSTATE=params_obj.getString("__VIEWSTATE");
			String __VIEWSTATEGENERATOR=params_obj.getString("__VIEWSTATEGENERATOR");
			String __EVENTVALIDATION=params_obj.getString("__EVENTVALIDATION");
			String grdBulletin$ctl18$NumGoto=iwReq.getRequestContentParam("grdBulletin$ctl18$NumGoto");
//System.out.println("LALDLFALSD："+grdBulletin$ctl18$NumGoto);
			
			String urltoCallList=getNewPathPrefix()+"/?"+getAdditionalLinkParamStr()
			+"&iw-cmd=QX_01959_1_List"
			+"&__VIEWSTATE="+__VIEWSTATE
			+"&__VIEWSTATEGENERATOR="+__VIEWSTATEGENERATOR
			+"&__EVENTVALIDATION="+__EVENTVALIDATION
			+"&grdBulletin$ctl18$NumGoto="+grdBulletin$ctl18$NumGoto
			;
System.out.println("LKASDLKFALKSDFL:"+urltoCallList);
			String result=Utils.callApi(urltoCallList);
			iwRsp = new DefaultIwResponse(null, result.getBytes("UTF-8"), null, 0, "ok");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return iwRsp;
	}

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		
		@Override
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", date=" + date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}
	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response = new Response();
		if (rspState == rsp) {
			try {
				Gson gson = new Gson();
				Response result = gson.fromJson(originTxtRspContent, Response.class);
				response.list = result.list;
				response.pageCount = result.pageCount;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_01959_1_ListCombine handler = new QX_01959_1_ListCombine();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01959_1/apis/QX_01959_1_ListCombine\\SampleResponse",
					"utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}





