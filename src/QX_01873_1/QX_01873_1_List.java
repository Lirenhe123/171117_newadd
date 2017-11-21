package QX_01873_1;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//	String jsonUnescape=StringEscapeUtils.unescapeJava(originTxtRspContent);
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class QX_01873_1_List extends TxtRspHandler {

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
			return "BranchNew [title=" + title + ", id=" + id + ", date="
					+ date + "]";
		}
	}

	@Override
	protected RspState checkTxtRspContentState(String originTxtRspContent) {
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState rspState,String originTxtRspContent) {
		Response response = new Response();
		if (rspState == RspState.Login) {
			try {
				String rowsOfJson=null;
				if(!originTxtRspContent.startsWith("{")){
					System.out.println("the format of originTxtRspContent is not right");
					System.out.println(originTxtRspContent);
				}else{
					rowsOfJson=originTxtRspContent;
				}
				JSONObject rowsOfJsonObject=new JSONObject(rowsOfJson);
				String totalRows=rowsOfJsonObject.getString("total");
				if(Integer.parseInt(totalRows)<20){
					response.pageCount="1";
				}else{
					response.pageCount=(Integer.parseInt(totalRows)/20+1)+"";
				}
//				System.out.println(response.pageCount);
				
				JSONArray rowObjectsArray=(JSONArray) rowsOfJsonObject.getJSONArray("rows");
				for(int i=0;i<rowObjectsArray.length();i++){
					JSONObject rowObject=rowObjectsArray.getJSONObject(i);
					String title=(String)rowObject.get("itemname");
					String id=(String)rowObject.get("md5id");
					/*String date_str=(String)rowObject.get("sellstime");
					//resolve date
					DateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
					date_str=format1.format(new Date(Long.parseLong(date_str)));*/
//					System.out.println(title);
//					System.out.println(id);
					
					BranchNew bn=new BranchNew();
					bn.id=id;
					bn.title=title;
					response.list.add(bn);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		QX_01873_1_List handler = new QX_01873_1_List();
		try {
			String originContent = IO.deserializeString("internetware/QX_01873_1/apis/QX_01873_1_List\\SampleResponse","UTF-8");
			handler.processRspContent(handler.checkTxtRspContentState(originContent),originContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
