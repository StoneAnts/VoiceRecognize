package com.demo.voice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sina.CreAmazing.voice.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.NoCopySpan.Concrete;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.iflytek.ui.SynthesizerDialog;
import com.iflytek.ui.SynthesizerDialogListener;

public class VoiceRecognize extends Activity {

    // 声明控件
    private EditText et;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private ToggleButton tb;
    private PackageManager pm;
    // 全局只设一个String，因为String为final类型，这样做节省内存
    String text = "";
    private static final String APPID = "appid=5cad6355";//定义一个常量APPPID，我自己的不便公开需要修改为你自己的；

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5cad6355");//APPID是你自己注册的APPID，可以自己去查看

        //这是后台朗读，实例化一个SynthesizerPlayer
        SynthesizerPlayer player = SynthesizerPlayer.createSynthesizerPlayer(VoiceRecognize.this, APPID);
        //设置语音朗读者，可以根据需要设置男女朗读，具体请看api文档和官方论坛
        player.setVoiceName("vivixiaoyan");//在此设置语音播报的人选例如：vivixiaoyan、vivixiaomei、vivixiaoqi
        player.playText("hello world", "ent=vivi21,bft=5",null);


        bt1 = (Button) findViewById(R.id.bt_recognize);
        bt2 = (Button) findViewById(R.id.bt_speek);
        bt3 = (Button) findViewById(R.id.bt_speek_bg);
        et = (EditText) findViewById(R.id.et);
        tb = (ToggleButton) findViewById(R.id.tb);
        // 初始化监听器
        initListener();

    }

    private void initListener() {
        bt1.setOnClickListener(myListener);
        bt2.setOnClickListener(myListener);
        bt3.setOnClickListener(myListener);

    }

    OnClickListener myListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // 根据不同View的id调用不同方法
            switch (v.getId()) {
                case R.id.bt_recognize:
                    // 这是语言识别部分，最重要的实例化一个
                    // RecognizerDialog并把在官方网站申请的appid填入进去，非法id不能进行识别
                    RecognizerDialog isrDialog = new RecognizerDialog(VoiceRecognize.this, APPID);

                    /*
                     * 设置引擎目前支持五种 ”sms”：普通文本转写 “poi”：地名搜索 ”vsearch”：热词搜索
                     * ”video”：视频音乐搜索 ”asr”：命令词识别
                     */
                    isrDialog.setEngine("sms", null, null);
                    isrDialog.setListener(recoListener);
                    isrDialog.show();
                    break;

                case R.id.bt_speek:
                    // 这是语言合成部分 同样需要实例化一个SynthesizerDialog ，并输入appid
                    SynthesizerDialog syn = new SynthesizerDialog(VoiceRecognize.this, APPID);
                    syn.setListener(new SynthesizerDialogListener() {

                        @Override
                        public void onEnd(SpeechError arg0) {

                        }
                    });
                    // 根据EditText里的内容实现语音合成
                    syn.setText(et.getText().toString(), null);
                    syn.show();
                    break;


                case R.id.bt_speek_bg:
                    //这是后台朗读，实例化一个SynthesizerPlayer
                    SynthesizerPlayer player = SynthesizerPlayer.createSynthesizerPlayer(VoiceRecognize.this, APPID);
                    //设置语音朗读者，可以根据需要设置男女朗读，具体请看api文档和官方论坛
                    player.setVoiceName("vivixiaoyan");//在此设置语音播报的人选例如：vivixiaoyan、vivixiaomei、vivixiaoqi
                    player.playText(et.getText().toString(), "ent=vivi21,bft=5",null);
                    break;
                default:
                    break;
            }

        }
    };
    public  void openApp(String str){
        //应用过滤条件，intent启动是应用的顶层
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager mPackageManager =getPackageManager();
        //返回给定条件的所有ResolveInfo对象（本质为activity）
        List<ResolveInfo> mAllApps = mPackageManager.queryIntentActivities(mainIntent, 0);
        //按包名排序
        Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));
        for(ResolveInfo res : mAllApps){
            //该应用的包名和主Activity
            String pkg = res.activityInfo.packageName;
            String cls = res.activityInfo.name;
            System.out.println("pkg~~~~~~" +pkg);
            // 打开QQ pkg中包含"qq"，打开微信，pkg中包含"mm"
            if(pkg.contains(str)){
                ComponentName componet = new ComponentName(pkg, cls);

                Intent intent = new Intent();
                intent.setComponent(componet);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                VoiceRecognize.this.startActivity(intent);
            }
        }
    }

    // 语言识别监听器，有两个方法
    RecognizerDialogListener recoListener = new RecognizerDialogListener() {

        @Override
        public void onResults(ArrayList<RecognizerResult> results,
                              boolean isLast) {
            // 新增加了一个ToggleButton tb，首先检查tb是否被按下，如果被按下才进行语言控制，没被按下就进行文字识别
            if (tb.isChecked()) {
                // doVoice方法就是进行识别
                doVoice(results);
            } else {
                // 服务器识别完成后会返回集合，我们这里就只得到最匹配的那一项
                text += results.get(0).text;
                System.out.println(text);
            }

        }

        // 首先迭代结果，然后获取每个结果，并进行对比，如果包含有特定字符串，那么就执行相应Intent跳转。
        // 注意 凡是Intent能办到的（发邮件，跳到已安装应用，拨号，发短信，发彩信，浏览网页，播放多媒体。。。。），它就都能办到。
        private void doVoice(ArrayList<RecognizerResult> results) {
            Intent i = new Intent();
            for (RecognizerResult result : results) {
                if (result.text.contains("天气")) {
                    // 天气界面的跳转
                    i.setClass(VoiceRecognize.this, Weather.class);
                    startActivity(i);
                } else if (result.text.contains("QQ")) {
                    // 跳转到QQ
                    openApp("com.tencent.mobileqq");//QQ的包名
                } else if (result.text.contains("短信")) {
                    // 短信界面的跳转
                    i.setAction(Intent.ACTION_VIEW);
                    i.setType("vnd.android-dir/mms-sms");
                    startActivity(i);
                } else {
                    // 如果没有相应指令就用Toast提示用户
                    Toast.makeText(VoiceRecognize.this, "无法识别",Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public void onEnd(SpeechError error) {
            if (error == null) {
                // 完成后就把结果显示在EditText上
                et.setText(text);
            }
        }
    };

}

