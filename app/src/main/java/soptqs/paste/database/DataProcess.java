package soptqs.paste.database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import org.litepal.crud.DataSupport;
import soptqs.paste.utils.AppUtils;
import soptqs.paste.utils.PreferenceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by S0ptq on 2018/2/6.
 * version: 2.0
 */

public class DataProcess {
    private static String TAG = "DataProcess.class";
    public static void addToBoard(String addedText, String time, String pakagename, Boolean isShared, Boolean isImage) {
        List<ClipSaves> clipSaves;
        ClipSaves insert;
        clipSaves = DataSupport.where("content = ?", addedText).find(ClipSaves.class);
        if (clipSaves.size() == 0) {
            //第一次复制该内容
            if (isShared) {
                if (isImage) {
                    //是图片分享
                    insert = new ClipSaves();
                    insert.setContent(addedText);
                    insert.setTime(time);
                    insert.setShared(true);
                    insert.setImage(true);
                    insert.save();
                } else {
                    //是文字分享
                    insert = new ClipSaves();
                    insert.setContent(addedText);
                    insert.setTime(time);
                    insert.setShared(true);
                    insert.save();
                }
            } else {
                //是复制内容
                insert = new ClipSaves();
                insert.setContent(addedText);
                insert.setTime(time);
                insert.setPakageName(pakagename);
                insert.save();
            }
        }else {
            //第二次复制该内容
            insert = clipSaves.get(0);
            insert.setTime(time);
            insert.setPakageName(pakagename);
            insert.update(insert.getId());
        }
    }

    public static void moveSaves(String frontTime, String toTime) {
        List<ClipSaves> clipSaves;
        ClipSaves clipSaves1;
        ClipSaves clipSaves2;
        String time;
        clipSaves = DataSupport.where("time = ?", frontTime).find(ClipSaves.class);
        if (clipSaves.size() == 0) {
            return;
        }
        clipSaves1 = clipSaves.get(0);
        clipSaves.clear();
        clipSaves = DataSupport.where("time = ?", toTime).find(ClipSaves.class);
        if (clipSaves.size() == 0) {
            return;
        }
        clipSaves2 = clipSaves.get(0);
        clipSaves.clear();
        time = clipSaves1.getTime();
        clipSaves1.setTime(clipSaves2.getTime());
        clipSaves1.update(clipSaves1.getId());
        clipSaves2.setTime(time);
        clipSaves2.update(clipSaves2.getId());
    }

    public static void deleteFromBoard(String time, Context context) {
        List<ClipSaves> clipSaves;
        clipSaves = DataSupport.where("time = ?", time).find(ClipSaves.class);
        if (clipSaves.size() == 0) {
            return;
        }
        if (clipSaves.get(0).isImage() && clipSaves.get(0).getContent() != null) {
            if (clipSaves.get(0).getContent().contains(",")) {
                ArrayList<String> Stringlist = new ArrayList<String>(Arrays.asList(clipSaves.get(0).getContent().split(",")));
                for (String string : Stringlist) {
                    String name = string + ".png";
                    File imagePath = new File(context.getFilesDir(), name);
                    imagePath.delete();
                }
            } else {
                String name = clipSaves.get(0).getContent() + ".png";
                File imagePath = new File(context.getFilesDir(), name);
                Log.e("path", "deleteAllData: " + imagePath);
                imagePath.delete();
            }
        }
        DataSupport.delete(ClipSaves.class, clipSaves.get(0).getId());
        Intent intent1 = new Intent("soptq.intent.notification.UPDATE");
        intent1.putExtra("service", 0);
        context.sendBroadcast(intent1);
    }

    public static void deleteFromBoardbyContent(String content, Context context) {
        List<ClipSaves> clipSaves;
        clipSaves = DataSupport.where("content = ?", content).find(ClipSaves.class);
        if (clipSaves.size() == 0) {
            return;
        }
        if (clipSaves.get(0).isImage() && clipSaves.get(0).getContent() != null) {
            if (clipSaves.get(0).getContent().contains(",")) {
                ArrayList<String> Stringlist = new ArrayList<String>(Arrays.asList(clipSaves.get(0).getContent().split(",")));
                for (String string : Stringlist) {
                    String name = string + ".png";
                    File imagePath = new File(context.getFilesDir(), name);
                    imagePath.delete();
                }
            } else {
                String name = clipSaves.get(0).getContent() + ".png";
                File imagePath = new File(context.getFilesDir(), name);
                Log.e("path", "deleteAllData: " + imagePath);
                imagePath.delete();
            }
        }
        DataSupport.delete(ClipSaves.class, clipSaves.get(0).getId());
        Intent intent1 = new Intent("soptq.intent.notification.UPDATE");
        intent1.putExtra("service", 0);
        context.sendBroadcast(intent1);
    }

    public static void maintainDataBase(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String num = String.valueOf(prefs.getLong(PreferenceUtils.PREF_CARDNUM, 20));
        List<ClipSaves> clipSaves = DataSupport.findAll(ClipSaves.class);
        for (ClipSaves clipSaves1 : clipSaves) {
            if (clipSaves1.getPakageName() != null && checkBL(clipSaves1.getPakageName())) {
                DataSupport.delete(ClipSaves.class, clipSaves1.getId());
            }
        }
        List<ClipSaves> clipSavesCur = DataSupport.order("time desc").offset(Integer.parseInt(num)).find(ClipSaves.class);
        if (clipSavesCur.size() != 0) {
            for (ClipSaves clipSaves1 : clipSavesCur) {
                if (clipSaves1.isImage() && clipSaves1.getContent() != null) {
                    if (clipSaves1.getContent().contains(",")) {
                        ArrayList<String> Stringlist = new ArrayList<String>(Arrays.asList(clipSaves1.getContent().split(",")));
                        for (String string : Stringlist) {
                            String name = string + ".png";
                            File imagePath = new File(context.getFilesDir(), name);
                            imagePath.delete();
                        }
                    } else {
                        String name = clipSaves1.getContent() + ".png";
                        File imagePath = new File(context.getFilesDir(), name);
                        Log.e("path", "deleteAllData: " + imagePath);
                        imagePath.delete();
                    }
                }
                DataSupport.delete(ClipSaves.class, clipSaves1.getId());
            }
        }
    }

    public static void deleteAllData(Context context) {
        List<ClipSaves> clipSavesCur = DataSupport.findAll(ClipSaves.class);
        if (clipSavesCur.size() != 0) {
            for (ClipSaves clipSaves1 : clipSavesCur) {
                if (clipSaves1.isImage() && clipSaves1.getContent() != null) {
                    if (clipSaves1.getContent().contains(",")) {
                        ArrayList<String> Stringlist = new ArrayList<String>(Arrays.asList(clipSaves1.getContent().split(",")));
                        for (String string : Stringlist) {
                            String name = string + ".png";
                            File imagePath = new File(context.getFilesDir(), name);
                            imagePath.delete();
                        }
                    } else {
                        String name = clipSaves1.getContent() + ".png";
                        File imagePath = new File(context.getFilesDir(), name);
                        Log.e("path", "deleteAllData: " + imagePath);
                        imagePath.delete();
                    }
                }
            }
            DataSupport.deleteAll(ClipSaves.class);
        }
    }

    public static void autoClear(Context context) {
        List<ClipSaves> clipSavesCur = DataSupport.findAll(ClipSaves.class);
        if (clipSavesCur.size() != 0) {
            for (ClipSaves clipSaves : clipSavesCur) {
                if (AppUtils.isTimeOut(Long.parseLong(clipSaves.getTime()), context)) {
                    DataSupport.delete(ClipSaves.class, clipSaves.getId());
                }
            }
        }
        maintainDataBase(context);
    }

    public static void dataCollect(String time) {
        List<ClipSaves> clipSaves;
        try {
            clipSaves = DataSupport.where("time = ?", time).find(ClipSaves.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Boolean isCollect = clipSaves.get(0).isCollection();
        if (isCollect) {
            ClipSaves clipSaves1 = new ClipSaves();
            clipSaves1.setToDefault("isCollection");
            clipSaves1.updateAll("time = ?", time);
        } else {
            ClipSaves clipSaves1 = new ClipSaves();
            clipSaves1.setCollection(true);
            clipSaves1.updateAll("time = ?", time);
        }
    }

    public static Boolean dataIsCollect(String time) {
        List<ClipSaves> clipSaves;
        try {
            clipSaves = DataSupport.where("time = ?", time).find(ClipSaves.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Boolean isCollect = clipSaves.get(0).isCollection();
        return isCollect;
    }

    public static Boolean dataIsTranslated(String time) {
        List<ClipSaves> clipSaves;
        try {
            clipSaves = DataSupport.where("time = ?", time).find(ClipSaves.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Boolean isTranslated = clipSaves.get(0).isTranslated();
        return isTranslated;
    }

    public static void isTranslated(String time, String translation) {
        try {
            ClipSaves clipSaves = new ClipSaves();
            clipSaves.setTranslated(true);
            clipSaves.setTranslation(translation);
            clipSaves.updateAll("time = ?", time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void isNotTranslated(String time) {
        try {
            ClipSaves clipSaves = new ClipSaves();
            clipSaves.setToDefault("isTranslated");
            clipSaves.setToDefault("translation");
            clipSaves.updateAll("time = ?", time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processBL(Boolean b, String pakageName) {
        if (b) {
            BlackListSaves blackListSaves = new BlackListSaves();
            blackListSaves.setPakageName(pakageName);
            if (blackListSaves.save()) {
//                Log.e("saves", "processBL: ok");
            } else {
//                Log.e("saves", "processBL: false");
            }
        } else {
            DataSupport.deleteAll(BlackListSaves.class, "pakageName = ?", pakageName);
        }
    }

    public static Boolean checkBL(String pakageName) {
        if (pakageName != null) {
            List<BlackListSaves> blackListSaves = DataSupport.where("pakageName = ?", pakageName).find(BlackListSaves.class);
            return blackListSaves.size() != 0;
        } else return false;
    }

    public static void createExpressSaves(String time) {
        List<ExpressSaves> expressSavesList;
        expressSavesList = DataSupport.where("time = ?", time).find(ExpressSaves.class);
        if (expressSavesList.size() == 0) {
            ExpressSaves expressSaves = new ExpressSaves();
            expressSaves.setTime(time);
            expressSaves.setIsTracking(0);
            expressSaves.save();
        }
        expressSavesList.clear();
    }

    public static int isExpressTracking(String time) {
        List<ExpressSaves> expressSavesList;
        expressSavesList = DataSupport.where("time = ?", time).find(ExpressSaves.class);
        if (expressSavesList.size() != 0) {
            return expressSavesList.get(0).getIsTracking();
        }
        return 0;
    }

    public static void changeExpressStatus(String time, Boolean isTracking) {
        List<ExpressSaves> expressSavesList;
        expressSavesList = DataSupport.where("time = ?", time).find(ExpressSaves.class);
        if (expressSavesList.size() != 0) {
            ExpressSaves expressSaves = expressSavesList.get(0);
            if (isTracking) {
                expressSaves.setIsTracking(1);
            } else {
                expressSaves.setIsTracking(-1);
            }
            expressSaves.update(expressSaves.getId());
        }
        expressSavesList.clear();
    }

//    public static Boolean dataIsLock(String time) {
//        List<ClipSaves> clipSaves;
//        try {
//            clipSaves = DataSupport.where("time = ?", time).find(ClipSaves.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        Boolean isTranslated = clipSaves.get(0).isLock();
//        return isTranslated;
//    }
//
//    public static void isLocked(String time) {
//        try {
//            ClipSaves clipSaves = new ClipSaves();
//            clipSaves.setLock(true);
//            clipSaves.updateAll("time = ?", time);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void isNotLocked(String time) {
//        try {
//            ClipSaves clipSaves = new ClipSaves();
//            clipSaves.setToDefault("isLock");
//            clipSaves.updateAll("time = ?", time);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
