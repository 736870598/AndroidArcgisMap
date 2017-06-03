package com.bs.graduation.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bs.graduation.R;
import com.bs.graduation.adapter.PaintToolAdapter;
import com.bs.graduation.adapter.RecyclerViewHolder;
import com.bs.graduation.drawtool.DrawEvent;
import com.bs.graduation.drawtool.DrawEventListener;
import com.bs.graduation.drawtool.DrawTool;
import com.bs.graduation.utils.SaveUtils;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.Field;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.table.FeatureTable;
import com.esri.core.tasks.query.QueryParameters;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 显示地图的view
 */
public class MyMapView extends RelativeLayout implements View.OnClickListener,
        RecyclerViewHolder.RVItemClickListener, DrawEventListener, TextView.OnEditorActionListener {

    private View view;
    private MapView mapView;
    private RecyclerView paintList;
    private RelativeLayout searchView;
    private EditText searchEt;

    private FeatureLayer featureLayer;

    private GraphicsLayer graphicsLayer;
    private SimpleFillSymbol markerSymbol;

    private GraphicsLayer drawLayer;
    private DrawTool drawTool;

    private boolean isInitMap;

    private Handler handler;

    private ProgressDialog dialog;

    private ShowSymbolInfo showTypeInfo;

    public MyMapView(Context context) {
        this(context, null);
    }

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.map_layout, this);
        mapView = (MapView) view.findViewById(R.id.map_view);
        view.findViewById(R.id.zoom_in).setOnClickListener(this);
        view.findViewById(R.id.zoom_out).setOnClickListener(this);
    }

    /**
     * 加载本地图层
     * @param path  shp保存路径
     */
    public void loadLocalMap(String path){
        mapView.removeAll();
        try {
            isInitMap = true;
            ShapefileFeatureTable shapefileFeatureTable=new ShapefileFeatureTable(path);
            featureLayer=new FeatureLayer(shapefileFeatureTable);
            featureLayer.setEnableLabels(true);
            featureLayer.setRenderer(new SimpleRenderer(new SimpleFillSymbol(getResources().getColor(R.color.map_green))));
            mapView.addLayer(featureLayer);
            showTypeInfo = new ShowSymbolInfo(getContext(), mapView, featureLayer);
            search("", false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置显示图形绘制的布局
     */
    public void visiblePaintTool(){
        if(!isInitMap){
            Toast.makeText(getContext(), "请先选择图层！", Toast.LENGTH_SHORT).show();
            return;
        }
        featureLayer.setVisible(false);
        setPaintToolState(View.VISIBLE);
        setSearchViewState(GONE);
    }

    /**
     * 设置显示空间搜索的布局
     */
    public void visiblesearchTool(){
        if(!isInitMap){
            Toast.makeText(getContext(), "请先选择图层！", Toast.LENGTH_SHORT).show();
            return;
        }
        featureLayer.setVisible(true);
        setPaintToolState(GONE);
        setSearchViewState(VISIBLE);
    }

    /**
     * 显示原始地图
     */
    public void showNativeInfo(){
        if(!isInitMap){
            Toast.makeText(getContext(), "请先选择图层！", Toast.LENGTH_SHORT).show();
            return;
        }
        featureLayer.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
                showTypeInfo.showNativeInfo();
                handler.sendEmptyMessage(3);
            }
        }).start();
    }

    /**
     * 显示人口信息地图
     */
    public void showPeolpleInfo(){
        if(!isInitMap){
            Toast.makeText(getContext(), "请先选择图层！", Toast.LENGTH_SHORT).show();
            return;
        }
        featureLayer.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
                showTypeInfo.showPeopleInfo();
                handler.sendEmptyMessage(3);
            }
        }).start();
    }

    /**
     * 显示面积信息地图
     */
    public void showAreaInfo(){
        if(!isInitMap){
            Toast.makeText(getContext(), "请先选择图层！", Toast.LENGTH_SHORT).show();
            return;
        }
        featureLayer.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
                showTypeInfo.showAreaInfo();
                handler.sendEmptyMessage(3);
            }
        }).start();
    }

    /**
     * 设置搜索布局的状态
     */
    private void setSearchViewState(int state){
        if(searchView == null){
            searchView = (RelativeLayout) view.findViewById(R.id.search_layout);
            searchEt = (EditText) searchView.findViewById(R.id.search_et);
            searchEt.setOnEditorActionListener(this);
            findViewById(R.id.cancel_tv).setOnClickListener(this);
        }

        searchView.setVisibility(state);
        InputMethodManager imm = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        if(state == VISIBLE){
            //显示软键盘
            searchView.requestFocus();
            imm.showSoftInput(searchView, InputMethodManager.SHOW_FORCED);
        }else{
            //隐藏软键盘
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            if(graphicsLayer != null){
                graphicsLayer.removeAll();
                mapView.removeLayer(graphicsLayer);
            }
            graphicsLayer = null;
        }
    }

    /**
     * 设置画图布局的状态
     */
    private void setPaintToolState(int state){
        if(paintList == null){
            paintList = (RecyclerView) view.findViewById(R.id.show_paint_rv);
            paintList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            paintList.setAdapter(new PaintToolAdapter(this));
        }
        paintList.setVisibility(state);
        if(state == VISIBLE){
            initPaintTool();
        }else{
            if(drawLayer != null){
                this.drawLayer.removeAll();
                this.drawTool.deactivate();
                mapView.removeLayer(drawLayer);
            }
            drawLayer = null;
        }
    }

    /**
     * 初始化画图工具
     */
    private void initPaintTool(){
        if(drawTool == null){
            drawTool = new DrawTool(mapView);
            // 此类实现DawEventListener接口
            drawTool.addEventListener(this);
        }
        if(drawLayer == null){
            // 在drawLayer上绘制几何图形
            drawLayer = new GraphicsLayer();
            mapView.addLayer(drawLayer);
        }

    }

    @Override
    public void handleDrawEvent(DrawEvent event) {
        this.drawLayer.addGraphic(event.getDrawGraphic());
    }

    /**
     * 点击画图工具选项时间监听
     */
    @Override
    public void onItemClick(RecyclerViewHolder viewHolder, Object object) {
        switch (viewHolder.getAdapterPosition()){
            case 0:  //线条
                drawTool.activate(DrawTool.POLYLINE);
                break;
            case 1:  //矩形
                drawTool.activate(DrawTool.ENVELOPE);
                break;
            case 2:  //点
                drawTool.activate(DrawTool.POINT);
                break;
            case 3:  //圆
                drawTool.activate(DrawTool.CIRCLE);
                break;
            case 4:  //多边形
                drawTool.activate(DrawTool.FREEHAND_POLYGON);
                break;
            case 5:  //清除
                this.drawLayer.removeAll();
                this.drawTool.deactivate();
                break;
            case 6:  //保存
                boolean flag = SaveUtils.saveMapViewScreen(getContext(), mapView);
                if(flag){
                    this.drawLayer.removeAll();
                    this.drawTool.deactivate();
                    setPaintToolState(View.GONE);
                }
                featureLayer.setVisible(true);
                break;
            case 7:  //取消
                this.drawLayer.removeAll();
                this.drawTool.deactivate();
                setPaintToolState(View.GONE);
                featureLayer.setVisible(true);
                break;
        }
    }


    /**
     * 放大缩小地图按钮事件监听
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.zoom_in:
                mapView.zoomin();
                break;
            case R.id.zoom_out:
                mapView.zoomout();
                break;
            case R.id.cancel_tv:
                if(searchEt.getText().toString().isEmpty()){
                    setSearchViewState(GONE);
                }else{
                    searchEt.setText("");
                }
                break;
        }
    }

    /**
     * 初始化handler用于在主线程中提示使用
     */
    private void initHandler(){
        if(handler != null){
            return;
        }
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        Toast.makeText(getContext(), "查询不到相关信息！", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "查询时出错！", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        dialog = new ProgressDialog(getContext());
                        dialog.setMessage("正在加载中...");
                        dialog.show();
                        break;
                    case 3:
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        if(msg.obj != null){
                            Toast.makeText(getContext(), (String)msg.obj , Toast.LENGTH_SHORT).show();
                            break;
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 点击输入法的搜索按钮事件监听
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH){
            //隐藏软键盘
            if(!v.getText().toString().isEmpty()){
                InputMethodManager imm = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                search(v.getText().toString(), true);
                searchEt.setText("");
            }
            return true;
        }
        return false;
    }

    /**
     * 搜索
     */
    private void search(String key, final boolean flag){
        initHandler();
        FeatureTable featureTable = featureLayer.getFeatureTable();
        QueryParameters parameters = new QueryParameters();
        if(flag){
            List<Field> fields = featureTable.getFields();

            boolean haveId = false;
            for (Field f : fields){
                if(f.getFieldType() == Field.esriFieldTypeString && f.getName().equalsIgnoreCase("ID")){
                    haveId = true;
                    break;
                }
            }
            String whereStr;
            if(haveId){
                whereStr = "ID like '%" + key + "%'" + " OR " + "NAME like '%" + key + "%'";
            }else{
                whereStr = "NAME like '%" + key + "%'";
            }
            parameters.setWhere(whereStr);
        }
        featureTable.queryIds(parameters, new CallbackListener<long[]>() {
            @Override
            public void onCallback(long[] longs) {
                if(longs == null || longs.length == 0){
                    handler.sendEmptyMessage(0);
                    return ;
                }
                if(flag){
                    handler.sendEmptyMessage(2);
                    showSearchInfo(featureLayer.getFeature(longs[0]));
                }else{
                    showTypeInfo.setMapLongs(longs);
                }
            }
            @Override
            public void onError(Throwable throwable) {
                handler.sendEmptyMessage(1);
            }
        });
    }


    /**
     * 显示搜索后的信息
     */
    private void showSearchInfo(Feature feature){
        if(markerSymbol == null){
            markerSymbol = new SimpleFillSymbol(Color.BLACK);
            markerSymbol.setAlpha(70);
        }

        if(graphicsLayer == null){
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        }
        graphicsLayer.removeAll();

        Geometry geometry = feature.getGeometry();
        Graphic graphic = new Graphic(feature.getGeometry(), markerSymbol);
        graphicsLayer.addGraphic(graphic);
        mapView.setExtent(geometry);

        DecimalFormat df = new java.text.DecimalFormat("#.00");
        String area = df.format(feature.getGeometry().calculateArea2D());
        String toast = "该地区面积大约为" + area + "多km²";
        Object peo = feature.getAttributeValue("Population");
        if(peo instanceof Double){
            toast += "，人口大约为" + df.format(peo) + "多万";
        }

        handler.sendMessage(handler.obtainMessage(3, toast));
    }

}
