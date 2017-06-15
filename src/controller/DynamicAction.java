package controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.struts2.interceptor.SessionAware;

import service.DynamicService;
import util.CheckUtil;
import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;

import domain.BusinessService;
import domain.Dynamic;

public class DynamicAction extends ActionSupport implements SessionAware {

    /**
     *
     */
    private static final long serialVersionUID = -674453320386630893L;

    private Map<String, Object> session;
    private InputStream inputStream;

    private DynamicService dynamicService;

    private Dynamic dynamic;// 动态
    private int userId;
    private int page;
    private int dynamicId;

    public String save() {
        int status = -1;
        System.out.println("saving dynamic...");
        System.out.println("user's id:" + dynamic.getUser().getUsername());
        System.out.println("content:" + dynamic.getContent());

        // checkIllegalwords
        boolean hasIllegalWords = CheckUtil.hasIllegalWords(dynamic.getContent());
        if (hasIllegalWords) {
            status = -3;
            System.out.println("save-status:" + status);
            inputStream = MsgUtil.sendString(status + "");
            return SUCCESS;
        }

        boolean flag = dynamicService.save(dynamic);
        if (flag) {
            status = 1;
        } else {
            status = -1;
        }
        System.out.println("save-status:" + status);

        inputStream = MsgUtil.sendString(status + "");

        return SUCCESS;
    }

    public String delete() {
        int status = -1;
        System.out.println("delete-dynamicId:" + dynamicId);
        System.out.println("delete-userId:" + userId);
        try {
            Dynamic d = dynamicService.getDynamicById(dynamicId);
            if (d == null) {
                status = -4;// 无此动态
                System.out.println("delete-status:" + status);
                inputStream = MsgUtil.sendString(status + "");
                return SUCCESS;
            }
            if (d.getUser().getId() == userId) {
                boolean flag = dynamicService.delete(d);
                if (flag) {
                    status = 1;
                } else {
                    status = -1;
                }
            } else {
                status = -3;// 删除别人的动态
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = -2;
        }

        System.out.println("delete-status:" + status);
        inputStream = MsgUtil.sendString(status + "");

        return SUCCESS;
    }

    /**
     * 已精简
     *
     * @return
     */
    public String getById() {
        System.out.println("getById-dynamicId:" + dynamicId);
        String result = "-1";
        JSONObject jsResp;
        try {
            Dynamic dynamic = dynamicService.getDynamicById(dynamicId);

            BusinessService bs = dynamic.getBusinessService();
            if (bs != null) {
                bs.setUser(null);
            }
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
            jsonConfig.setExcludes(new String[]{"canServiceDay", "category", "detail", "exchange", "favoriteNumber",
                    "favoritePeople", "finishedPeople", "great", "greatPeople", "images", "reward_money",
                    "reward_thing", "reward_unit", "serviceCity", "serviceTime", "serviceType", "status", "time",
                    "age", "address", "concernedPeople", "favoriteServices", "hobby", "homePageBackgroundImage", "job",
                    "latestLocation_x", "latestLocation_y", "password", "photos", "rank", "rank_credit",
                    "receivedRequest", "registCheckCode", "registTime", "services", "sign", "todoRequest",
                    "userComment", "userRequest", "verifyStatus", "userId", "img1", "img2", "img3", "birthday"});

            jsResp = JSONObject.fromObject(dynamic, jsonConfig);
            result = jsResp.toString();
        } catch (Exception e) {
            e.printStackTrace();
            result = "-2";
        }

        System.out.println("getById-result:" + result);
        inputStream = MsgUtil.sendString(result);

        return SUCCESS;
    }

    public String addSupport() {
        String result = "-1";
        System.out.println("addSupport");
        boolean flag = dynamicService.addSupport(dynamicId);
        if (flag) {
            result = "1";
        } else {
            result = "-1";
        }

        inputStream = MsgUtil.sendString(result);

        return SUCCESS;
    }

    public String addTrample() {
        System.out.println("addTrample");
        String result = "-1";
        boolean flag = dynamicService.addTrample(dynamicId);
        if (flag) {
            result = "1";
        } else {
            result = "-1";
        }

        inputStream = MsgUtil.sendString(result);

        return SUCCESS;
    }

    public String getByUserIdAndPage() {
        String result = "-1";

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        List<Dynamic> list = dynamicService.getDynamicsByIdAndPage(userId, page);
        JSONArray jsonArray = JSONArray.fromObject(list, jsonConfig);
        JSONObject jsResp = new JSONObject();
        jsResp.put("list", jsonArray);
        result = jsResp.toString();

        result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

        inputStream = MsgUtil.sendString(result);

        return SUCCESS;

    }

    public String getBriefByUserId() {
        String result = "-1";
        System.out.println("getBriefByUserId-userId:" + userId);
        List<Dynamic> list = dynamicService.getBriefByUserId(page, userId);

        JSONObject jsResp = new JSONObject();
        try {
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
            jsonConfig.setExcludes(new String[]{"businessService", "location_x", "location_y", "status", "support",
                    "trample", "user", ""});
            JSONArray ja = JSONArray.fromObject(list, jsonConfig);
            jsResp.put("list", ja);
            result = jsResp.toString();
        } catch (Exception e) {
            result = "-2";
            e.printStackTrace();
        }

        result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图

        System.out.println("getBriefByPage-result:" + result);
        inputStream = MsgUtil.sendString(result);

        return SUCCESS;

    }

    public String getBriefByPage() {
        System.out.println("getBriefByPage-page:" + page);
        List<Dynamic> list = dynamicService.getBriefByPage(page, 15);
        sendBriefByPage(list);
        return SUCCESS;
    }

    public String getBriefByPage1() {
        System.out.println("getBriefByPage-page1:" + page);
        List<Dynamic> list = dynamicService.getBriefByPage(page, 15);
        if (list.size() >= 12)
            for (int i = 11; i >= 0; i -= 2)
                list.remove(i);
        sendBriefByPage(list);
        return SUCCESS;
    }

    public String getBriefByPage2() {
        System.out.println("getBriefByPage-page2:" + page);
        List<Dynamic> list = dynamicService.getBriefByPage(page, 15);
        if (list.size() >= 12)
            for (int i = 10; i >= 0; i -= 2)
                list.remove(i);
        sendBriefByPage(list);
        return SUCCESS;
    }

    private void sendBriefByPage(List<Dynamic> list) {
        String result = "-1";
        JSONObject jsResp = new JSONObject();
        try {
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
            jsonConfig.setExcludes(new String[]{"businessService", "location_x", "location_y", "pic2", "pic3",
                    "publishTime", "status", "support", "trample", "user", "img1", "img2", "img3", "username",
                    "nickName"});
            JSONArray ja = JSONArray.fromObject(list, jsonConfig);
            jsResp.put("list", ja);
            result = jsResp.toString();
        } catch (Exception e) {
            result = "-2";
            e.printStackTrace();
        }
        result = result.replaceAll("/big/", "/small/");// 将原图转化为缩略图
        System.out.println("getBriefByPage-result:" + result);
        inputStream = MsgUtil.sendString(result);
    }

    /*******************************/

    public Dynamic getDynamic() {
        return dynamic;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public DynamicService getDynamicService() {
        return dynamicService;
    }

    public void setDynamicService(DynamicService dynamicService) {
        this.dynamicService = dynamicService;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(int dynamicId) {
        this.dynamicId = dynamicId;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
