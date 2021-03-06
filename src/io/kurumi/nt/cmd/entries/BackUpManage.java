package io.kurumi.nt.cmd.entries;

import io.kurumi.nt.*;
import io.kurumi.nt.cmd.*;
import cn.hutool.core.codec.*;
import cn.hutool.json.*;
import cn.hutool.core.util.*;
import cn.hutool.core.io.*;

public class BackUpManage extends NTBaseCmd {

    public static void apply(final NTUser user, final NTMenu menu) {

        final NTMenu backupMenuMain = menu.subMenu("配置备份");

        backupMenuMain.init = new Runnable() {

            @Override
            public void run() {

                backupMenuMain.item(new NTMenu.Item("备份配置") {

                        @Override
                        public boolean run() {

                            println("请复制配置 :");

                            println(user.getConfigObject().toString());

                            pause();
                            
                            return false;

                        }

                    });

                backupMenuMain.item(new NTMenu.Item("还原配置") {

                        @Override
                        public boolean run() {

                            String config = input("配置文本");

                            try {

                                
                                new JSONObject(config);

                                if (confirm("覆盖配置？这将丢失现有配置！")) {

                                    FileUtil.writeUtf8String(config, user.getConfigFile());
                                    user.refresh();

                                    menu.msg("配置还原成功！");

                                    return true;

                                }

                            } catch (JSONException ex) {

                                ex.printStackTrace();
                                backupMenuMain.msg("配置文本无效");

                            }

                            return false;

                        }

                    });

            }

        };

    }

}
