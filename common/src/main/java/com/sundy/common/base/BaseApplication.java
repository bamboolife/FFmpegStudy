package com.sundy.common.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.haohaohu.cachemanage.ACache;
import com.haohaohu.cachemanage.CacheUtil;
import com.haohaohu.cachemanage.CacheUtilConfig;
import com.haohaohu.cachemanage.strategy.Des3EncryptStrategy;
import com.sundy.common.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;

import butterknife.ButterKnife;

/**
 * 项目名称：CommonLibrary
 *
 * @Author bamboolife
 * 邮箱：core_it@163.com
 * 创建时间：2019-11-15 21:58
 * 描述：Application基类
 * 注意：第三库好多开了多进程  最好判断进程初始化 要不然会重复加载多次
 */
public class BaseApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRouter();
    }

    private void initRouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this);
        ButterKnife.setDebug(true);
        initCacheConfigDefault();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }

    private void initCacheConfigDefault() {
        CacheUtilConfig cc =
                CacheUtilConfig.builder(this).allowMemoryCache(true)//是否允许保存到内存
                        .allowEncrypt(false)//是否允许加密
                        .build();
        CacheUtil.init(cc);//初始化，必须调用
    }

//    private void initCacheConfig1() {
//        CacheUtilConfig cc =
//                CacheUtilConfig.builder(this).allowMemoryCache(true)//是否允许保存到内存
//                        .allowEncrypt(false)//是否允许加密
//                        .allowKeyEncrypt(true)//是否允许Key加密
//                        //.preventPowerDelete(true)//强力防止删除，将缓存数据存储在app数据库目录下的cachemanage文件夹下
//                        //                .setACache(ACache.get(file1))//自定义ACache，file1为缓存自定义存储文件夹,设置该项，preventPowerDelete失效
//                        .setAlias("")//默认KeyStore加密算法私钥，建议设置.自定义加密算法，该功能失效
//                        .setIEncryptStrategy(new Des3EncryptStrategy(this,
//                                "WLIJkjdsfIlI789sd87dnu==", "haohaoha"))//自定义des3加密
//                        .build();
//        CacheUtil.init(cc);//初始化，必须调用
//    }
//
//    private void initCacheConfig2() {
//        CacheUtilConfig cc = CacheUtilConfig.builder(this)
//                .setIEncryptStrategy(
//                        new Des3EncryptStrategy(this, "WLIJkjdsfIlI789sd87dnu==",
//                                "haohaoha"))
//                .allowMemoryCache(true)//是否允许保存到内存
//                .allowEncrypt(true)//是否允许加密
//                .build();
//        CacheUtil.init(cc);//初始化，必须调用
//        KeyGenerator xd;
//        try {
//            xd = KeyGenerator.getInstance("DES");
//            String str = xd.generateKey().toString();
//            Log.e("str", str);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
//            keyStore.load(null);
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void initCacheConfig3() {
//        File file = new File(ACache.getDiskCacheDir(this));
//        File file1 = new File(file.getParent(), "cachemanage");
//        CacheUtilConfig cc =
//                CacheUtilConfig.builder(this).allowMemoryCache(true)//是否允许保存到内存
//                        .allowEncrypt(false)//是否允许加密
//                        .setACache(ACache.get(file1))//自定义ACache
//                        .build();
//        CacheUtil.init(cc);//初始化，必须调用
//    }
//
//    private void initCacheConfig4() {
//        File file1 = new File(Environment.getExternalStorageDirectory(), "cache");
//        CacheUtilConfig cc =
//                CacheUtilConfig.builder(this).allowMemoryCache(true)//是否允许保存到内存
//                        .allowEncrypt(false)//是否允许加密
//                        //                        .preventPowerDelete(true)//强力防止删除，将缓存数据存储在app数据库目录下的cachemanage文件夹下
//                        .setACache(ACache.get(file1))//自定义ACache
//                        .build();
//        CacheUtil.init(cc);//初始化，必须调用
//    }
}
