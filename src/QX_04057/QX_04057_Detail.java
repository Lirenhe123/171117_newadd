package QX_04057;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.internetware.phone.extension.response.RspState;
import cn.internetware.phone.extension.response.TxtRspObject;
import cn.internetware.phone.extension.response.impl.TxtBaseResponse;
import cn.internetware.phone.extension.response.impl.TxtRspHandler;

public class QX_04057_Detail extends TxtRspHandler{
	
	
	
	public class Response extends TxtBaseResponse{
			String content;
			String title;
			
	}

	@Override
	protected RspState checkTxtRspContentState(String arg0) {
		// TODO Auto-generated method stub
		return RspState.Login;
	}

	@Override
	protected TxtRspObject processTxtRspContent(RspState arg0, String arg1) {
		Response response =  new Response();
		try{
			Document doc =Jsoup.parse(arg1);
			String title =doc.select("font#titlezoom").html().trim();
			String content =doc.select("td.hui14").html();
			Document html=Jsoup.parse(content);
			if(html.select("a[href]").size()>0){
				Elements a_list=html.select("a[href]");
				for(int i=0;i<a_list.size();i++){
					if(content.contains("doc")||
							content.contains("zip")||
							content.contains("rar")||
							content.contains("pdf")){
						String href=a_list.get(i).attr("href");
						href=href.substring(href.indexOf("docs"),href.length());
						html.select("a[href]").get(i).attr("href","http://czh.bjfsh.gov.cn/"+href);
				}
				}
				content=html.html();
			}
			response.title=title;
			response.content="<div>"+title+"</div>"+content;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return response;
	}
	
	
	

	

}
