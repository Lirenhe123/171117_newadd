package QX_02441;

import java.text.SimpleDateFormat;
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

public class QX_02441_List extends TxtRspHandler {

	public class Response extends TxtBaseResponse {
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		//String currentPage;
	}

	public class BranchNew {
		String title;
		String id;
		String date;
		String content;


		public String toString() {
			return "BranchNew[ title=" + title + ";id=" + id + ";date=" + date+ ";]";
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
				JSONObject jsObject = new JSONObject(originTxtRspContent);
				JSONArray jsArray = jsObject.getJSONArray("rows");
				for(int i = 0;i<jsArray.length();i++)
				{
					JSONObject jsonObject = jsArray.getJSONObject(i);
					int  idString = jsonObject.getInt("Id");
					String  titleString = jsonObject.getString("Title");
					JSONObject jsonObject2 = jsonObject.getJSONObject("RecordInfo");
					String milisecond = jsonObject2.getString("CreatedAt");
					milisecond = milisecond.substring(milisecond.indexOf("(")+1,milisecond.indexOf(")"));
					long second = Long.parseLong(milisecond);
					Date date = new Date(second);
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String dateString = sDateFormat.format(date);
					String content = jsonObject.getString("Descr");
					BranchNew branchNew = new BranchNew();
					branchNew.date = dateString;
					branchNew.id = idString + "";
					branchNew.title = titleString;
					branchNew.content = content;
					response.list.add(branchNew);
//					System.out.println(branchNew);
//					System.out.println(content);
				}
				
			
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}


	public static void main(String[] args) {
		QX_02441_List handler = new QX_02441_List();

		try {
			String originRspContent = IO
					.deserializeString("./internetware/QX_02441/apis/QX_02441_List/SampleResponse","UTF-8");
			handler.processRspContent(
					handler.checkTxtRspContentState(originRspContent),
					originRspContent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
