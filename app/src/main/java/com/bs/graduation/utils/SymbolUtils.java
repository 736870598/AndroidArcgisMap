package com.bs.graduation.utils;

import android.content.Context;
import android.graphics.Color;

import com.bs.graduation.R;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.SimpleFillSymbol;

/**
 * 显示各种色块symbol的工具类
 */
public class SymbolUtils {

    private static FillSymbol[] fillSymbols = new FillSymbol[12];
    private static int[] peopleCount;
    private static int[] areaCount;
    private static String[] colorArray;


    /**
     * 根据人口数得到不同人口显示的色块
     */
    public static FillSymbol getSymbolFromPopulation(Context context, double population){
        if(colorArray == null){
            colorArray = context.getApplicationContext().getResources().getStringArray(R.array.map_color);
        }
        if(peopleCount == null){
            peopleCount = context.getApplicationContext().getResources().getIntArray(R.array.people_count);
        }
        int index = getIndexFromCount(population, peopleCount);
        FillSymbol fillSymbol = fillSymbols[index];
        if(fillSymbol == null){
            fillSymbol = new SimpleFillSymbol(Color.parseColor(colorArray[index]));
            fillSymbols[index] = fillSymbol;
        }
        return fillSymbol;
    }

    /**
     * 根据面积大小得到不同面积显示的色块
     */
    public static FillSymbol getSymbolFromArea(Context context, double area){
        if(colorArray == null){
            colorArray = context.getApplicationContext().getResources().getStringArray(R.array.map_color);
        }
        if(areaCount == null){
            areaCount = context.getApplicationContext().getResources().getIntArray(R.array.area_count);
        }
        int index = getIndexFromCount(area, areaCount);
        FillSymbol fillSymbol = fillSymbols[index];
        if(fillSymbol == null){
            fillSymbol = new SimpleFillSymbol(Color.parseColor(colorArray[index]));
            fillSymbols[index] = fillSymbol;
        }
        return fillSymbol;
    }

    /**
     * 获取在数组中的位置
     * @param population
     * @param array
     * @return
     */
    private static int getIndexFromCount(double population, int[] array){
        for (int i = 0; i < 10; i++){
            if(population < array[i]){
                return i;
            }
        }
        return 0;
    }
}
