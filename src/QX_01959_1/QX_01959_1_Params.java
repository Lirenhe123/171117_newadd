package QX_01959_1;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class QX_01959_1_Params extends TxtRspHandler{
	public static RspState rsp=RspState.Login;
	
	public class Response extends TxtBaseResponse{
		Params params=new Params();
	}
	public class Params{
		String __VIEWSTATE;
		String __VIEWSTATEGENERATOR;
		String __EVENTVALIDATION;
		String grdBulletin$ctl18$NumGoto;
		
		@Override
		public String toString() {
			return "__VIEWSTATE="+__VIEWSTATE+"__VIEWSTATEGENERATOR="+__VIEWSTATEGENERATOR+"__EVENTVALIDATION="+__EVENTVALIDATION;
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return rsp;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState, String originTxtRspContent) {
		Response response=new Response();
		if(rspState==rsp){
			try{
				Document document=Jsoup.parse(originTxtRspContent);
				Element __VIEWSTATE_html=document.select("input[name=__VIEWSTATE]").first();
				Element __VIEWSTATEGENERATOR_html=document.select("input[name=__VIEWSTATEGENERATOR]").first();
				Element __EVENTVALIDATION_html=document.select("input[name=__EVENTVALIDATION]").first();
				Element grdBulletin$ctl18$NumGoto_html=document.select("input[name=grdBulletin$ctl18$NumGoto]").first();
				String __VIEWSTATE_str=null;
				String __VIEWSTATEGENERATOR_str=null;
				String __EVENTVALIDATION_str=null;
				String grdBulletin$ctl18$NumGoto_str=null;
				if(__VIEWSTATE_html!=null){
					__VIEWSTATE_str=__VIEWSTATE_html.attr("value");
				}
				if(__VIEWSTATEGENERATOR_html!=null){
					__VIEWSTATEGENERATOR_str=__VIEWSTATEGENERATOR_html.attr("value");
				}
				if(__EVENTVALIDATION_html!=null){
					__EVENTVALIDATION_str=__EVENTVALIDATION_html.attr("value");
				}
				if(grdBulletin$ctl18$NumGoto_html!=null){
					grdBulletin$ctl18$NumGoto_str=grdBulletin$ctl18$NumGoto_html.attr("value");
				}
				response.params.__VIEWSTATE=__VIEWSTATE_str;
				response.params.__VIEWSTATEGENERATOR=__VIEWSTATEGENERATOR_str;
				response.params.__EVENTVALIDATION=__EVENTVALIDATION_str;
				response.params.grdBulletin$ctl18$NumGoto=grdBulletin$ctl18$NumGoto_str;
				
				
//				System.out.println(response.params.__VIEWSTATE);
//				System.out.println(response.params.__VIEWSTATEGENERATOR);
//				System.out.println(response.params.__EVENTVALIDATION);
//				System.out.println(response.params.grdBulletin$ctl18$NumGoto);
				
				
			}catch(Exception e){
				System.out.println(e);
			}
		}
		return response;
	}
	
	public static void main(String[] args) {
		QX_01959_1_Params handler = new QX_01959_1_Params();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_01959_1/apis/QX_01959_1_Params\\SampleResponse",
					"utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
