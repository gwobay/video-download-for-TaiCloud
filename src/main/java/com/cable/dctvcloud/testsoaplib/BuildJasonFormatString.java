package com.cable.dctvcloud.testsoaplib;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by erickou on 2015/4/26.
 */
public class BuildJasonFormatString {

    public final static int SPLIT_SIZE=200*1024;
    private String buildRequestJsonHead(String key, String usrId, String phoneSN)
    {
        String json="";
        json +="{\"Changingtec\":";
        json += "{\"UserInfo\":";
        json += "{\"Key\":";
        json += "\""+key+"\",";//"\"e0VS5s2nxxyXxRy7y5wFfGEWwTTZYLPWig2Ul5DJc/lKFcKZDBPibA==\",";
        json += "\"UserID\":";
        json += "\""+usrId+"\",";//"\"C6153873-13CD-4E1D-8B9A-47374DC8393F\",";
        json += "\"PhoneDevice\":";
        json += "\"GT-P3100\",";
        json += "\"PhoneSN\":";
        json += "\""+phoneSN+"\"";//"\"B3A4D01A-4270-4D0F-A710-1311DECFFE4D\"}}}";
        //json += "}}}";
        return json;
    }

    public String getDirectoryRequestJason(String key, String usrId, String phoneSN)
    {
        return buildRequestJsonHead(key, usrId, phoneSN)+"}}}";
    }

    static public String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=null;
        try{
            System.gc();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
            b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("OOOz..", "Out of memory error ");
        }
        return temp;
    }
    public String buildJsonToUpLoadRequest(String key, String usrId, String phoneSN,
                                            String fileName, long fileSize,String ext, int splitCount,
                                            String category, String targetDirectory)
    {
        /*
        {"Changingtec":
            {"UserInfo":
                {"Key":"[密鑰]",
                        "UserID":"Admin",
                        "PhoneDevice":"[手機裝置名稱]",
                        "PhoneSN":"[手機序號]"},
                "File":{"Name":"[檔案名稱]",
                    "Size":"[檔案尺寸(Byte)]",
                    "Extension":"[副檔名]",
                    "PackageNumber":"[切割檔案總數]",
                    "Type":{"@Class":"Videos|Files|Images","#text":"[自訂資料夾路徑]"}
            }
            }
        }
        */
        String json=buildRequestJsonHead(key, usrId, phoneSN);
        json += "},";
        json +="\"File\":";
        json += "{\"Name\":";
        json += "\""+fileName+"\",";
        json += "{\"Size\":";
        json += "\""+fileSize+"\",";
        json += "\"Extension\":";
        json += "\""+ext+"\",";
        json += "\"PackageNumber\":";
        json += "\""+splitCount+"\",";
        json += "\"Type\":{";
        json += "\"@Class\":";
        json += "\""+category+"\",";
        json += "\"#text\":";
        json += "\""+targetDirectory+"\"";
        json += "}}}}";
        return json;
    }

    public String buildJsonUpLoadingRequest(String fileID,  int splitSize,
                                            int splitIndex,
                                            Bitmap icon)
    {
      /*
    {"Changingtec":{"File":
        {"ID":"87b1f5d5-e342-4b1b-9fa2-a9aa85a94bbe","Size":"[切割檔案尺寸(Byte)]",
        "PackageIndex":"[切割檔案索引(從0開始)]","Serialize":"[Base64]"}}}*/
        String json = "\"Changingtec\":{";
        json +="\"File\":";
        json += "{\"ID\":";
        json += "\""+fileID+"\",";
        json += "{\"Size\":";
        json += "\""+splitSize+"\",";
         json += "\"PackageIndex\":";
        json += "\""+splitIndex+"\",";
        json += "\"Serialize\":";
        json += "\""+bitMapToString(icon)+"\"";
        json += "}}}";
        return json;
    }
}

