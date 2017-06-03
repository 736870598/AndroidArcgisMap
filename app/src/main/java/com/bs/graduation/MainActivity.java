package com.bs.graduation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bs.graduation.view.MyMapView;

/**
 * 程序主界面
 */
public class MainActivity extends AppCompatActivity  {

    private MyMapView mapView;
    private View peopleTag;
    private View areaTag;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mapView = (MyMapView) findViewById(R.id.myMapView);
        peopleTag = findViewById(R.id.people_tag);
        areaTag = findViewById(R.id.area_tag);
    }

    /**
     * 设置右上角的菜单选项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * 点击菜单的事件响应
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_select:
                //选择图层，调用系统文件夹进行选择
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,10000);
                break;
            case R.id.menu_paint:
                //绘制图形
                mapView.visiblePaintTool();
                break;
            case R.id.menu_serch:
                //空间搜索
                mapView.visiblesearchTool();
                break;
            case R.id.menu_native:
                //原始地图
                mapView.showNativeInfo();
                peopleTag.setVisibility(View.GONE);
                areaTag.setVisibility(View.GONE);
                break;
            case R.id.menu_people:
                //人口信息
                mapView.showPeolpleInfo();
                peopleTag.setVisibility(View.VISIBLE);
                areaTag.setVisibility(View.GONE);
                break;
            case R.id.menu_area:
                //面积信息
                mapView.showAreaInfo();
                peopleTag.setVisibility(View.GONE);
                areaTag.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }


    /**
     *  选择文件后回调响应
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 10000) {
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。

            //妹子的手机
//            String path = uri.getPath();
            //魅蓝note2
            String path = decodePahtFromMz(uri.getPath());

            //如果选择的文件是.shp格式的文件就进行加载，否则提示选择类型错误
            if(path != null && (path.endsWith(".shp") || path.endsWith(".SHP") )){
                loadShpFile(path);
                return;
            }
            Toast.makeText(this, "文件格式不对，请重新选择！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 加载本地shp文件
     */
    private void loadShpFile(String path){
        mapView.loadLocalMap(path);
    }

    /**
     * 在魅族手机上加载选择路径
     */
    public String decodePahtFromMz(String path){
        String[] split = path.split(":");
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + split[1];
    }

}
