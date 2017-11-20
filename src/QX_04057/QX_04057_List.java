package QX_04057;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;
import cn.internetware.utils.IO;

public class QX_04057_List extends TxtRspHandler{
	
	public class BranchNew{
		String title;
		String id;
		String url;
		String date;
		public String toString() {
			return "BranchNew [title=" + title + ", id=" + id + ", url=" + url
					+ ", date=" + date + "]";
		}
		
	}
	
	
	public class Response extends TxtBaseResponse{
		List<BranchNew> list = new ArrayList<BranchNew>();
		String pageCount;
		String nextPage;
	}
	

	protected RspState checkTxtRspContentState(String arg0) {
		// TODO Auto-generated method stub
		return RspState.Login;
	}

	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Pattern p = Pattern.compile("\\d+");
		Response response = new Response();
		if(arg0 == RspState.Login){
			try{
				Document doc = Jsoup.parse(arg1);
				Elements tr_list =doc.select("table[width=98%][align=center] > tbody > tr");
				for(int i=0;i<tr_list.size()-1;i++){
					Element tr =tr_list.get(i);
					BranchNew n = new BranchNew();
					String href=tr.select("td > a").attr("href");
					Matcher m =p.matcher(href);
					if(m.find()){
						String id=m.group();
						n.id=id;
					}
					String date=tr.select("td > div").html().trim().replace(".", "").replace("/", "-");
					n.date=date;
					
//					System.out.println(n.id);
//					System.out.println(n.date);
					response.list.add(n);
				}
				String pagecount=doc.select("select[name=sel_page] > option").last().html();
				if(pagecount!=null){
					response.pageCount=pagecount;
				}
//System.out.println(response.pageCount);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return response;
	}
	public static void main(String[] args) {
		QX_04057_List handler = new QX_04057_List();
		try {
			String originTxtRspContent = IO.deserializeString("internetware/QX_04057/apis/QX_04057_List\\SampleResponse",
					"utf8");
			handler.processTxtRspContent(handler.checkTxtRspContentState(originTxtRspContent), originTxtRspContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
