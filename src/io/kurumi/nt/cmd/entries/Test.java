package io.kurumi.nt.cmd.entries;

import io.kurumi.nt.*;
import io.kurumi.nt.cmd.*;
import twitter4j.*;
import cn.xsshome.taip.nlp.*;
import io.kurumi.nt.taip.*;
import cn.hutool.core.util.*;
import cn.hutool.json.*;

import cn.hutool.json.JSONObject;

public class Test extends NTBaseCmd {

    public static void apply(final NTUser user, NTMenu menu) {

        menu.item(new NTMenu.Item("测试1") {

                @Override
                public boolean run() {

                    try {

                        TwiAccount acc = UserManage.chooseAccount(user);

                        Twitter api =  acc.createApi();


                        UserList zm = UserListManage.chooseOwnUserList(acc);
                        UserList fm = UserListManage.chooseOwnUserList(acc);

                        Status s = api.showStatus(Long.parseLong("1086050926937423872"));

                        int index = 0;

                        long cursor = -1;
                        IDs ids;

                        do {

                            ids = api.getFollowersIDs("HiedaNaKan", cursor);
                            cursor = ids.getNextCursor();

                            StringBuilder sc = new StringBuilder();

                            for (long id : ids.getIDs()) {

                                try {

                                    index ++;

                                    float r = an(api, id);

                                    User u = api.showUser(id);

                                    sc.append(NTApi.formatUsernName(u));
                                    sc.append(" : ");


                                    sc.append("\n");

                                    if (r > 0) {

                                        sc.append("偏正面 ");




                                    } else if (r < 0) {

                                        sc.append("偏负面 ");



                                    }



                                    sc.append(r);

                                    sc.append("%");

                                    sc.append("\n\n");

                                    if (index >= 5) {

                                        s = NTApi.reply(api, s, sc.toString());

                                        sc = new StringBuilder();

                                        println(u.getScreenName() + " successed");

                                        index = 0;

                                    }

                                } catch (Exception e ) {
                                    index --;
                                    continue; }


                            }

                        } while(ids.hasNext());

                    } catch (Exception ex) { ex.printStackTrace(); }

                    return false;
                }

            });

    }

    public static float an(Twitter api, long target) throws TwitterException {

        println("正在分析 : " + target);

        ResponseList<Status> tl = api.getUserTimeline(target, new Paging().count(100));

        float count = 0;
        float polar = 0;

        for (Status s : tl) {

            if (!s.isRetweet()) {

                String json = AIUtil.nlpTextpolar(s.getText());

                count ++;

                polar += new JSONObject(json).getJSONObject("data").getInt("polar");



            }

        }

        println("分析完成 :");

        float r = (polar / count);

        if (r > 0) {

            print("偏正面 :");

        } else {

            print("偏负面 :");

            //  r = -r;

        }

        r =  r * 100;

        println(r + "%");

        return r;

    }

}
