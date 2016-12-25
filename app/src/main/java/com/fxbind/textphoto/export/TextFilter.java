package com.fxbind.textphoto.export;

import com.fxbind.textphoto.text.FloatText;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/23/2016.
 */

public class TextFilter {
    public static String getFilter(String input, String output,
                                   ArrayList<FloatText> listText){
        String filter="";
        for (int i=0; i<listText.size(); i++){
            TextHolder text = listText.get(i).textHolder;
            String in = i==0? input:"[out"+i+"]";
            String out = i==listText.size()-1?output:"[out"+(i+1)+"];";
            filter += "color=black@0:"+text.width+"x"+text.height+",fps=30[bgr];" +
                    "[bgr]format=rgba,drawtext=fontfile="+text.fontPath+":textfile="+text.textPath +
                    ":fontsize="+text.size+":fontcolor="+text.fontColor+
                    ":box=1:boxcolor="+text.boxColor+":boxborderw=10:x="+ text.padding+":y="+text.padding
                    +",colorkey=000000:0.01:1[textPath];"+
                    "[textPath]rotate="+text.rotate+":c=none:ow=rotw("+text.rotate+")"+
                    ":oh=roth("+text.rotate+")[ov];"+ in+"[ov]overlay=x="+text.x+":y="
                    +text.y+":shortest=1"+out;
        }
        return filter;
    }
}
