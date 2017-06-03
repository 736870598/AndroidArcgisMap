package com.bs.graduation.view;

import android.content.Context;

import com.bs.graduation.utils.SymbolUtils;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.map.Feature;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.FillSymbol;

import java.util.Map;

/**
 * 在地图上显示不同symbol信息
 */
public class ShowSymbolInfo {

    private MapView mapView;
    private GraphicsLayer infoLayout;
    private boolean isShow;

    private int[] gInts;
    private long[] mapLongs;
    private Graphic[] peoGraphic;
    private Graphic[] areGraphic;

    private Context context;
    private FeatureLayer featureLayer;

    public ShowSymbolInfo(Context context, MapView mapView, FeatureLayer featureLayer){
        this.context = context;
        this.mapView = mapView;
        this.featureLayer = featureLayer;
    }

    public void setMapLongs(long[] mapLongs) {
        this.mapLongs = mapLongs;
    }

    private void showLayout(){
        if(infoLayout == null){
            infoLayout = new GraphicsLayer();
            mapView.addLayer(infoLayout);
        }
        isShow = true;
    }

    public void removeLayout(){
        if(infoLayout != null){
            infoLayout.removeAll();
        }
        gInts = null;
        isShow = false;
    }

    public void showNativeInfo(){
        removeLayout();
    }

    /**
     * 人口信息模块
     */
    public void showPeopleInfo(){
        if(!isShow){
            showLayout();
        }
        int size = mapLongs.length;
        if(peoGraphic == null){
            peoGraphic = new Graphic[size];
            for (int i = 0; i < size; i++){
                Feature feature = featureLayer.getFeature(mapLongs[i]);
                Object obj = feature.getAttributeValue("Population");
                double dou = 0d;
                if(obj instanceof Double){
                    dou = (double) obj;
                }
                FillSymbol symbol = SymbolUtils.getSymbolFromPopulation(context, dou);
                peoGraphic[i] = new Graphic(feature.getGeometry(), symbol);
            }
        }

        if(gInts == null){
            gInts = infoLayout.addGraphics(peoGraphic);
        }else{
            infoLayout.updateGraphics(gInts, peoGraphic);
        }
    }

    /**
     * 面积信息模块
     */
    public void showAreaInfo(){
        if(!isShow){
            showLayout();
        }
        int size = mapLongs.length;
        if(areGraphic == null){
            areGraphic = new Graphic[size];
            for (int i = 0; i < size; i++){
                Feature feature = featureLayer.getFeature(mapLongs[i]);
                FillSymbol symbol = SymbolUtils.getSymbolFromArea(context, feature.getGeometry().calculateArea2D());
                areGraphic[i] = new Graphic(feature.getGeometry(), symbol);
            }
        }

        if(gInts == null){
            gInts = infoLayout.addGraphics(areGraphic);
        }else{
            infoLayout.updateGraphics(gInts, areGraphic);
        }
    }

}
